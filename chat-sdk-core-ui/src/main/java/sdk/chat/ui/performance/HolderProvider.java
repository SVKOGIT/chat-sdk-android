package sdk.chat.ui.performance;

import java.util.HashMap;
import java.util.Map;

import sdk.chat.core.dao.Message;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.dao.User;
import sdk.chat.ui.ChatSDKUI;
import sdk.chat.ui.chat.model.MessageHolder;
import sdk.chat.ui.chat.model.ThreadHolder;
import sdk.chat.ui.chat.model.UserHolder;

public class HolderProvider {

    Map<User, UserHolder> userHolders = new HashMap<>();
    Map<Message, MessageHolder> messageHolders = new HashMap<>();
    Map<Thread, ThreadHolder> threadHolders = new HashMap<>();

    public UserHolder getUserHolder(User user) {
        if (user == null) {
            return null;
        }
        UserHolder holder = userHolders.get(user);
        if (holder == null) {
            holder = new UserHolder(user);
            userHolders.put(user, holder);
        }
        return holder;
    }

    public void removeUserHolder(User user) {
        userHolders.remove(user);
    }

    public MessageHolder getMessageHolder(Message message) {
        if (message == null) {
            return null;
        }
        MessageHolder holder = messageHolders.get(message);
        if (holder == null) {
            holder = ChatSDKUI.shared().getMessageRegistrationManager().onNewMessageHolder(message);
            messageHolders.put(message, holder);
        }
        return holder;
    }

    public void removeMessageHolder(Message message) {
        messageHolders.remove(message);
    }

    public ThreadHolder getThreadHolder(Thread thread) {
        if (thread == null) {
            return null;
        }
        ThreadHolder holder = threadHolders.get(thread);
        if (holder == null) {
            holder = new ThreadHolder(thread);
            threadHolders.put(thread, holder);
        }
        return holder;
    }

    public void removeThreadHolder(Thread thread) {
        threadHolders.remove(thread);
    }

    public void clear() {
        userHolders.clear();
        messageHolders.clear();
        threadHolders.clear();
    }

}
