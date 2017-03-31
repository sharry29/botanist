package edu.upenn.cis350.botanist;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by Jackie on 3/24/17.
 */

public class NotificationPublisher extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Creates a notification with the specified icon, title, and text
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.flower);
        builder.setContentTitle("Take a picture of your plants!");
        builder.setContentText("Your plants need love every day! Stop by Botanist to take a " +
                "picture of your plants!");
        builder.setAutoCancel(true);

        // Creates the intent that will be followed when the notification is clicked
        // Ensures that pressing the back button will return the user to the main menu instead of
        // out of the app
        Intent takePhotoIntent = new Intent(context, PhotoActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PhotoActivity.class);
        stackBuilder.addNextIntent(takePhotoIntent);
        PendingIntent takePhotoPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(takePhotoPendingIntent);

        // Sends the notification to the system
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void setRepeatingAlarm(Context context) {
        // Creates the intent that will trigger the onReceive method of Notification Publisher
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Sets the time for the notification to be sent
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);

        // Creates the alarm with the specified interval for when alarms should be sent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
