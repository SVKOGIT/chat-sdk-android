package sdk.chat.ui.views;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import org.pmw.tinylog.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import sdk.chat.core.dao.Keys;
import sdk.chat.core.dao.Message;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.events.EventType;
import sdk.chat.core.events.NetworkEvent;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.MessageSendProgress;
import sdk.chat.core.types.MessageSendStatus;
import sdk.chat.core.utils.AppBackgroundMonitor;
import sdk.chat.core.utils.Debug;
import sdk.chat.core.utils.Dimen;
import sdk.chat.ui.ChatSDKUI;
import sdk.chat.ui.R;
import sdk.chat.ui.R2;
import sdk.chat.ui.chat.model.MessageHolder;
import sdk.chat.ui.utils.ImageLoaderPayload;
import sdk.guru.common.DisposableMap;
import sdk.guru.common.RX;

public class ChatView extends LinearLayout implements MessagesListAdapter.OnLoadMoreListener {

    protected final DateFormat formatTime = DateFormat.getTimeInstance(DateFormat.SHORT);
    protected final DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT);
    protected boolean listenersAdded = false;
    @BindView(R2.id.messagesList)
    protected MessagesList messagesList;

    protected MessagesListAdapter<MessageHolder> messagesListAdapter;

    protected Map<Message, MessageHolder> messageHolderHashMap = new HashMap<>();
    protected List<MessageHolder> messageHolders = new ArrayList<>();

    protected DisposableMap dm = new DisposableMap();
    @BindView(R2.id.root)
    protected LinearLayout root;

    public void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat, this);
        ButterKnife.bind(this);

        final MessageHolders holders = new MessageHolders();
        ChatSDKUI.shared().getMessageCustomizer().onBindMessageHolders(getContext(), holders);

        messagesListAdapter = new MessagesListAdapter<>(ChatSDK.currentUserID(), holders, (imageView, url, payload) -> {
            ImageLoaderPayload ilp;
            if (payload instanceof ImageLoaderPayload) {
                ilp = (ImageLoaderPayload) payload;
            } else {
                ilp = new ImageLoaderPayload();
            }

            if (ilp.placeholder == 0) {
                ilp.placeholder = R.drawable.icn_200_image_message_placeholder;
            }
            if (ilp.error == 0) {
                ilp.error = R.drawable.icn_200_image_message_placeholder;
            }

            if (url == null) {
                Logger.debug("Stop here");
                return;
            }

            RequestManager request = Glide.with(this);
            RequestBuilder<?> builder;

            if (ilp.isAnimated) {
                builder = request.asGif();
            } else {
                builder = request.asDrawable().dontAnimate();
            }

            if (payload == null) {
                // User avatar
                builder.load(url)
                        .placeholder(R.drawable.icn_100_profile)
                        .override(Dimen.from(getContext(), R.dimen.small_avatar_width), Dimen.from(getContext(), R.dimen.small_avatar_height))
                        .into(imageView);
            } else {
                final int maxSize = maxImageWidth();
                final int currentWidth = ilp.width == 0 ? maxSize : ilp.width;
                final int currentHeight = ilp.height == 0 ? maxSize : ilp.height;
                final int width;
                final int height;

                if (currentWidth > currentHeight) {
                    width = maxSize;
                    height = maxSize / currentWidth * currentHeight;
                } else {
                    width = maxSize / currentHeight * currentWidth;
                    height = maxSize;
                }

                builder.load(url)
                        .error(ilp.error)
                        .override(width, height)
                        .placeholder(imageView.getDrawable())
                        .into(imageView);
            }
        });

        messagesListAdapter.setLoadMoreListener(this);

        messagesListAdapter.setOnMessageClickListener(holder -> {
            Message message = holder.getMessage();
            if (message.isReply()) {
                String originalMessageEntityID = message.stringForKey(Keys.Id);
                if (originalMessageEntityID != null) {
                    Message originalMessage = ChatSDK.db().fetchEntityWithEntityID(originalMessageEntityID, Message.class);
                    MessageHolder originalHolder = messageHolderHashMap.get(originalMessage);
                    if (originalHolder != null) {
                        int index = messageHolders.indexOf(originalHolder);
                        if (index >= 0) {
                            messagesList.smoothScrollToPosition(index);
                            return;
                        }
                    }
                }
            }
            delegate.onClick(holder.getMessage());
        });

        messagesListAdapter.setOnMessageLongClickListener(holder -> {
            delegate.onLongClick(holder.getMessage());
        });
        messagesList.setAdapter(messagesListAdapter);

        onLoadMore(0, 0);

    }

    protected Delegate delegate;

    public ChatView(Context context) {
        super(context);
    }

    public ChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDateHeadersFormatter(@Nullable DateFormatter.Formatter formatter) {
        messagesListAdapter.setDateHeadersFormatter(formatter);
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void addMessagesToEnd(@NonNull final List<Message> messages) {
        if (messages.isEmpty()) {
            return;
        }

        // Check to see if the holders already exist
        final List<MessageHolder> holders = new ArrayList<>();

        RX.runSingle(() -> {
            for (Message message : messages) {
                MessageHolder holder = messageHolderHashMap.get(message);
                if (holder == null) {
                    holder = ChatSDKUI.shared().getMessageCustomizer().onNewMessageHolder(message);
                    if (holder != null) {
                        messageHolderHashMap.put(message, holder);
                        holders.add(holder);
                    } else {
                        Logger.debug("Not allowed");
                    }
                }
            }
            Debug.messageList(messages);
        }, () -> {
            messageHolders.addAll(holders);
            messagesListAdapter.addToEnd(holders, false);
        }).subscribe(ChatSDK.events());
    }

    public void addListeners() {
        if (listenersAdded) {
            return;
        }
        listenersAdded = true;

        dm.add(ChatSDK.events().sourceOnMain()
                .filter(NetworkEvent.filterType(EventType.MessageAdded, EventType.MessageUpdated, EventType.MessageRemoved, EventType.MessageReadReceiptUpdated, EventType.MessageSendStatusUpdated))
                .filter(NetworkEvent.filterThreadEntityID(delegate.getThread().getEntityID()))
                .subscribe(networkEvent -> {
                    networkEvent.debug();
                    Message message = networkEvent.getMessage();
                    // We listed to the MessageAdded event when we receive but we listen to the message created status when we send
                    // Because we need to wait until the message payload is set which happens after it is added to the thread
                    if (networkEvent.typeIs(EventType.MessageAdded) || (networkEvent.typeIs(EventType.MessageSendStatusUpdated) && networkEvent.getMessageSendProgress().status == MessageSendStatus.Created)) {
                        addMessageToStartOrUpdate(message);
                        if (!AppBackgroundMonitor.shared().inBackground()) {
                            message.markReadIfNecessary();
                        }
                    }
                    if (networkEvent.typeIs(EventType.MessageUpdated)) {
                        if (message.getSender().isMe()) {
                            softUpdate(message);
                        } else {
                            // If this is not from us, then we need to calculate when to
                            // how the time and name that requires a full update
                            addMessageToStartOrUpdate(message);
                        }
                    }
                    if (networkEvent.typeIs(EventType.MessageRemoved)) {
                        removeMessage(networkEvent.getMessage());
                    }
                    if (networkEvent.typeIs(EventType.MessageReadReceiptUpdated) && ChatSDK.readReceipts() != null && message.getSender().isMe()) {
                        softUpdate(message);
                    }
                    if (networkEvent.typeIs(EventType.MessageSendStatusUpdated)) {
                        MessageSendProgress progress = networkEvent.getMessageSendProgress();
                        softUpdate(message, progress);
                    }
                }));

        dm.add(ChatSDK.events().sourceOnMain()
                .filter(NetworkEvent.filterType(EventType.UserPresenceUpdated, EventType.UserMetaUpdated))
                .subscribe(networkEvent -> {
                    if (delegate.getThread().containsUser(networkEvent.getUser())) {
                        notifyDataSetChanged();
                    }
                }));
    }

    protected int maxImageWidth() {
        // Prevent overly big messages in landscape mode
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return Math.round(getResources().getDisplayMetrics().widthPixels);
        } else {
            return Math.round(getResources().getDisplayMetrics().heightPixels);
        }
    }

    public void clearSelection() {
        messagesListAdapter.unselectAllItems();
    }

    public List<MessageHolder> getSelectedMessages() {
//        return MessageHolder.toMessages(messagesListAdapter.getSelectedMessages());
        return messagesListAdapter.getSelectedMessages();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        // Check if the thread was deleted. If so load messages since the last message or
        // the deletion date, whichever is more recent
        Date loadMessagesFrom = delegate.getThread().getLoadMessagesFrom();
        if (loadMessagesFrom != null) {
            dm.add(ChatSDK.thread()
                    .loadMoreMessagesAfter(delegate.getThread(), loadMessagesFrom, totalItemsCount != 0)
                    .observeOn(RX.main())
                    .subscribe(this::addMessagesToEnd));
        } else {
            Date loadFromDate = null;
            if (totalItemsCount != 0) {
                // This list has the newest first
                loadFromDate = messageHolders.get(messageHolders.size() - 1).getCreatedAt();
            }

            dm.add(ChatSDK.thread()
                    .loadMoreMessagesBefore(delegate.getThread(), loadFromDate, totalItemsCount != 0)
                    .observeOn(RX.main())
                    .subscribe(this::addMessagesToEnd));
        }
    }

    public void removeMessage(Message message) {
        MessageHolder holder = messageHolderHashMap.get(message);

        if (holder != null) {
            messagesListAdapter.delete(holder);
            messageHolders.remove(holder);
            messageHolderHashMap.remove(message);
        }

        updatePrevious(message);
        updateNext(message);
    }

    public void updatePrevious(Message message) {
        final MessageHolder holder = previous(message);
        if (holder != null) {
            RX.run(holder::update, () -> {
                messagesListAdapter.update(holder);
            }).subscribe(ChatSDK.events());
        }
    }

    public MessageHolder previous(@NonNull Message message) {
        Message previous = message.getPreviousMessage();
        if (previous != null) {
            return messageHolderHashMap.get(previous);
        }
        return null;
    }

    public void updateNext(Message message) {
        MessageHolder holder = next(message);
        if (holder != null) {
            messagesListAdapter.update(holder);
        }
    }

    public MessageHolder next(@NonNull Message message) {
        Message next = message.getNextMessage();
        if (next != null) {
            return messageHolderHashMap.get(next);
        }
        return null;
    }

    public void addMessageToStartOrUpdate(Message message) {
        addMessageToStartOrUpdate(message, null);
    }

    public void addMessageToStartOrUpdate(Message message, MessageSendProgress progress) {
        final MessageHolder holder = messageHolderHashMap.get(message);

        if (holder == null) {

            final MessageHolder finalHolder = ChatSDKUI.shared().getMessageCustomizer().onNewMessageHolder(message);

            messageHolders.add(0, finalHolder);
            messageHolderHashMap.put(finalHolder.getMessage(), finalHolder);

            // This means that we only scroll down if we were already at the bottom
            // it can be annoying if you have scrolled up and then a new message
            // comes in and scrolls the screen down
            boolean scroll = message.getSender().isMe();

            int offset = messagesList.computeVerticalScrollOffset();
            int extent = messagesList.computeVerticalScrollExtent();
            int range = messagesList.computeVerticalScrollRange();
            int distanceFromBottom = range - extent - offset;

            if (distanceFromBottom < 400) {
                scroll = true;
            }

            messagesListAdapter.addToStart(finalHolder, scroll);

            // Update the previous holder so that we can hide the
            // name if necessary
//            updatePrevious(message);

        } else {
            RX.run(() -> {
                holder.update();
                holder.setProgress(progress);
            }, () -> {
                messagesListAdapter.update(holder);
            }).subscribe(ChatSDK.events());
        }
    }

    public void softUpdate(Message message) {
        softUpdate(message, null);
    }

    // Just rebinds the message
    public void softUpdate(Message message, MessageSendProgress progress) {
        final MessageHolder holder = messageHolderHashMap.get(message);
        if (holder != null) {
            if (progress != null) {
                holder.setProgress(progress);
            }
            messagesListAdapter.update(holder);
        }
    }

    public interface Delegate {
        Thread getThread();

        void onClick(Message message);

        void onLongClick(Message message);
    }

    public void notifyDataSetChanged() {
        messagesListAdapter.notifyDataSetChanged();
    }

    public void clear() {
        if (messagesListAdapter != null) {
            messageHolderHashMap.clear();
            messageHolders.clear();
            messagesListAdapter.clear();
        }
    }

    public void copySelectedMessagesText(Context context, MessagesListAdapter.Formatter<MessageHolder> formatter, boolean reverse) {
        messagesListAdapter.copySelectedMessagesText(context, formatter, reverse);
    }

    public void enableSelectionMode(MessagesListAdapter.SelectionListener selectionListener) {
        messagesListAdapter.enableSelectionMode(selectionListener);
    }

    public void filter(final String filter) {
        if (filter == null || filter.isEmpty()) {
            clearFilter();
        } else {
            final ArrayList<MessageHolder> filtered = new ArrayList<>();
            RX.run(() -> {
                for (MessageHolder holder : messageHolders) {
                    if (holder.getText().toLowerCase().contains(filter.trim().toLowerCase())) {
                        filtered.add(holder);
                    }
                }
            }, () -> {
                messagesListAdapter.clear();
                messagesListAdapter.addToEnd(filtered, true);
            }).subscribe(ChatSDK.events());
        }
    }

    public void clearFilter() {
        messagesListAdapter.clear();
        messagesListAdapter.addToEnd(messageHolders, true);
    }

//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        dm.dispose();
//    }

    public void removeListeners() {
        dm.dispose();
        listenersAdded = false;
    }

    public static class CustomDateFormatter implements DateFormatter.Formatter {

        private final DateFormat hourlyFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        private final DateFormat currentYearFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        private final DateFormat yearlyFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        private final String textYesterday;

        public CustomDateFormatter(@NonNull String textYesterday) {
            this.textYesterday = textYesterday;
        }

        @NonNull
        @Override
        public String format(@NonNull Date date) {
            if (date.after(getDayStart())) { // is today
                return hourlyFormatter.format(date);
            } else if (date.after(getYesterdayStart())) { // is yesterday
                return String.format("%s %s", textYesterday, hourlyFormatter.format(date));
            } else if (date.after(getYearStart())) { // is this year
                return String.format("%s, %s", currentYearFormatter.format(date), hourlyFormatter.format(date));
            } else { // is last year or longer
                return yearlyFormatter.format(date);
            }
        }

        @NonNull
        private Date getYearStart() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getDayStart());
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            return calendar.getTime();
        }

        @NonNull
        private Date getYesterdayStart() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getDayStart());
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            return calendar.getTime();
        }

        @NonNull
        private Date getDayStart() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }

    }
}
