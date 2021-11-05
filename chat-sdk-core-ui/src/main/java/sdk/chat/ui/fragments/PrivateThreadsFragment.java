package sdk.chat.ui.fragments;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.events.NetworkEvent;
import sdk.chat.core.handlers.ThreadHandler;
import sdk.chat.core.interfaces.ThreadType;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.ui.R;
import sdk.chat.ui.utils.DialogUtils;
import sdk.chat.ui.utils.ToastHelper;
import sdk.guru.common.RX;

public class PrivateThreadsFragment extends ThreadsFragment {

    @Override
    protected Predicate<NetworkEvent> mainEventFilter() {
        return NetworkEvent.filterPrivateThreadsUpdated();
    }

    @Override
    public void initViews() {
        super.initViews();
    }

    @Override
    public void addListeners() {
        super.addListeners();
        dm.add(getOnLongClickObservable().subscribe(thread -> DialogUtils.showToastDialog(getContext(), 0, R.string.alert_delete_thread, R.string.delete,
                R.string.cancel, () -> {
                    ThreadHandler handler = ChatSDK.thread();
                    if (handler == null) {
                        Logger.error("ThreadHandler is null. Did you initialize ChatSDK?");
                        return;
                    }
                    dm.add(handler.deleteThread(thread)
                            .observeOn(RX.main())
                            .subscribe(() -> {
                            }, throwable -> ToastHelper.show(getContext(), throwable.getLocalizedMessage())));
                }, null)));
    }

    @Override
    protected Single<List<Thread>> getThreads() {
        return Single.defer(() -> {
            ThreadHandler handler = ChatSDK.thread();
            List<Thread> threads = handler != null ? handler.getThreads(ThreadType.Private) : Collections.emptyList();

            if (ChatSDK.config().privateChatRoomLifetimeMinutes == 0) {
                return Single.just(threads);
            } else {
                // Do we need to filter the list to remove old chat rooms?
                long now = new Date().getTime();
                List<Thread> filtered = new ArrayList<>();
                for (Thread t : threads) {
                    if (t.getCreationDate() == null || now - t.getCreationDate().getTime() < TimeUnit.MINUTES.toMillis(ChatSDK.config().privateChatRoomLifetimeMinutes)) {
                        filtered.add(t);
                    }
                }
                return Single.just(filtered);
            }
        }).subscribeOn(RX.single());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        /* Cant use switch in the library*/
        int id = item.getItemId();

        if (id == R.id.action_add) {
            ChatSDK.ui().startCreateThreadActivity(getContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
