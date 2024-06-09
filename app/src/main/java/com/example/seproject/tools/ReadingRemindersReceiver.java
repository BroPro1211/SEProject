package com.example.seproject.tools;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Reading reminders broadcast receiver, that is called every day to check if a notification should be sent
 */
public class ReadingRemindersReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "Reading reminders";
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_TITLE = "We miss you!";
    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SEProject", "Activating reading reminders receiver");

        SharedPreferences sharedPreferences = context.getSharedPreferences(ToolsFragment.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(ToolsFragment.READING_REMINDERS_ENABLED, false)){
            Log.d("SEProject", "Checking time difference");

            long currentTime = System.currentTimeMillis();
            long lastSeenTime = sharedPreferences.getLong(ToolsFragment.LAST_ACTIVE_TIME, currentTime);
            long timeDiff = currentTime - lastSeenTime;

            if (needToSendReminder(timeDiff)){
                Log.d("SEProject", "Sending notification, time difference is " + timeDiff);
                initNotificationManager(context);
                sendNotification(context, timeDiff);
            }
            else{
                Log.d("SEProject", "Not sending notification, time difference is " + timeDiff);
            }
        }

    }

    /**
     * Returns whether a notification should be sent
     * @param timeDiff The time difference between the current time to the last seen time
     * @return Returns true if a notification should be sent, and false otherwise
     */
    private boolean needToSendReminder(long timeDiff){
        long daysPassed = timeDiff / (1000*60*60*24);
        return daysPassed == 3 || daysPassed % 7 == 0;


        /*
        // for testing purposes: sends notification each 2 minutes
        long minutesPassed = timeDiff / (1000 * 60);
        return minutesPassed % 2 == 0;
        */

    }

    /**
     * Initializes the notification manager
     * @param context The context
     */
    private void initNotificationManager(Context context){
        Log.d("SEProject", "Initializing notification manager");

        notificationManager = NotificationManagerCompat.from(context);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Sends a reminder notification
     * @param context The context
     * @param timeDiff The time difference between the current time to the last seen time
     */
    private void sendNotification(Context context, long timeDiff){

        NotificationCompat.Builder notificationBbuilder = new
                NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(getNotificationText(timeDiff))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            notificationManager.notify(NOTIFICATION_ID, notificationBbuilder.build());
            Log.d("SEProject", "Notification sent");
        }
        else
            Log.d("SEProject", "Couldn't send notification, permission not granted");
    }

    /**
     * Returns the notification text
     * @param timeDiff The time difference between the current time to the last seen time
     * @return Returns a string of the text
     */
    private String getNotificationText(long timeDiff){
        long daysPassed = timeDiff / (1000*60*60*24);

        if (daysPassed == 3)
            return "You haven't read for " + daysPassed + " days!";
        if (daysPassed == 7)
            return "You haven't read for 1 week!";
        return "You haven't read for " + (daysPassed/7) + " weeks!";


        /*
        // for testing purposes
        return (timeDiff / 1000) + " seconds";
        */


    }
}
