package edu.upenn.cis350.botanist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Jackie on 3/24/17.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences userSettings = context.getSharedPreferences(
                UserSettingsActivity.USER_SETTINGS_PREFS_NAME, Context.MODE_PRIVATE
        );

        // Checks if a repeating notification has been created before & re-generates the
        // notification after booting the device
        if (userSettings.getBoolean("notificationsScheduled", false) &&
                intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            NotificationPublisher.setRepeatingAlarm(context);
        }
    }
}
