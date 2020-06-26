package sdk.chat.firbase.online;

import android.content.Context;

import sdk.chat.core.module.AbstractModule;
import sdk.chat.core.session.ChatSDK;

public class FirebaseLastOnlineModule extends AbstractModule {

    public static final FirebaseLastOnlineModule instance = new FirebaseLastOnlineModule();
    public static FirebaseLastOnlineModule shared() {
        return instance;
    }

    @Override
    public void activate(Context context) {
        ChatSDK.a().lastOnline = new FirebaseLastOnlineHandler();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void stop() {

    }
}
