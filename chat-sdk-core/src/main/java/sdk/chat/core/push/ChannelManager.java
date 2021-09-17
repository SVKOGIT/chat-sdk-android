package sdk.chat.core.push;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sdk.chat.core.session.ChatSDK;

public class ChannelManager {

    public static String PushChannelsKey = "PushChannelsKey";

    @NonNull
    public List<String> getChannelsForUser(@NonNull String userEntityID) {
        SharedPreferences preferences = ChatSDK.shared().getPreferences();
        Set<String> channelSet = preferences.getStringSet(userKey(userEntityID), null);
        List<String> channels = new ArrayList<>();
        if (channelSet != null) {
            for (String channelId : channelSet) {
                if (channelId == null) {
                    continue;
                }
                channels.add(channelId);
            }
        }
        return channels;
    }

    @NonNull
    public String userKey(@NonNull String userEntityID) {
        return PushChannelsKey + userEntityID;
    }

    public void setChannelsForUser(String userEntityID, List<String> channels) {
        SharedPreferences.Editor editor = ChatSDK.shared().getPreferences().edit();
        editor.putStringSet(userKey(userEntityID), new HashSet<>(channels)).apply();
    }

    public List<String> getUserEntityIDs() {
        SharedPreferences preferences = ChatSDK.shared().getPreferences();
        Map<String, ?> all = preferences.getAll();
        List<String> userIDs = new ArrayList<>();
        for (String key : all.keySet()) {
            if (key.contains(PushChannelsKey)) {
                userIDs.add(key.replace(PushChannelsKey, ""));
            }
        }
        return userIDs;
    }

    public void addChannel(String channel) {
        addChannel(ChatSDK.currentUserID(), channel);
    }

    public void removeChannel(String channel) {
        removeChannel(ChatSDK.currentUserID(), channel);
    }

    public void addChannel(String userEntityID, String channel) {
        List<String> channels = getChannelsForUser(userEntityID);
        channels.add(channel);
        setChannelsForUser(userEntityID, channels);
    }

    public void removeChannel(String userEntityID, String channel) {
        List<String> channels = getChannelsForUser(userEntityID);
        channels.remove(channel);
        setChannelsForUser(userEntityID, channels);
    }

    public void channelsForUsersExcludingCurrent(Executor executor) {
        for (String entityID : getUserEntityIDs()) {
            if (!entityID.equals(ChatSDK.currentUserID())) {
                for (@NonNull String channel : getChannelsForUser(entityID)) {
                    executor.run(channel);
                }
            }
        }
    }

    public boolean isSubscribed(@NonNull String userEntityID, @NonNull String channel) {
        return getChannelsForUser(userEntityID).contains(channel);
    }

    public boolean isSubscribed(@NonNull String channel) {
        String userId = ChatSDK.currentUserID();
        return userId != null && isSubscribed(userId, channel);
    }

    public interface Executor {
        void run(@NonNull String channel);
    }

}
