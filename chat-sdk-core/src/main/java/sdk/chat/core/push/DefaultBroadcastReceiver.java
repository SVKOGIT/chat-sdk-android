package sdk.chat.core.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.AlarmManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

import sdk.chat.core.session.ChatSDK;

/**
 * Created by ben on 5/10/18.
 */

// We want to use this receiver if the app has been killed or if it's in the background
public class DefaultBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isValid = ChatSDK.shared().isValid();
        if (!isValid) {
            // if sdk is not currently initialized, it might be initialized later on,
            // so we delay the delivery by x seconds
            rescheduleDelivery(context, intent);
            return;
        }
        deliverIntent(context, intent);
    }

    private void rescheduleDelivery(Context context, Intent intent) {
        AlarmManager manager = ContextCompat.getSystemService(context, AlarmManager.class);
        if (manager == null) {
            deliverIntent(context, intent);
            return;
        }

        int flags = PendingIntent.FLAG_ONE_SHOT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        long delay = TimeUnit.SECONDS.toMillis(5);
        long elapsed = SystemClock.elapsedRealtime() + delay;
        int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, flags);
        AlarmManagerCompat.setExactAndAllowWhileIdle(manager, type, elapsed, pendingIntent);
    }

    private void deliverIntent(Context context, Intent intent) {
        if (ChatSDK.push() != null && ChatSDK.push().getBroadcastHandler() != null) {
            ChatSDK.push().getBroadcastHandler().onReceive(context, intent);
        }
    }

}
