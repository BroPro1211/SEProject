package com.example.seproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("broadcast","calledddd");
        String channelID = intent.getStringExtra("channelID");
        String channelName = intent.getStringExtra("channelName");
        int notificationID = intent.getIntExtra("notificationID", 1);

        NotificationClass notificationClass = new NotificationClass(context);
        notificationClass.createChannel(channelID, channelName);

        final String NOTIF_TITLE = "Alpha Timed Notification";
        final String NOTIF_TEXT = "So cool!";
        notificationClass.notification(context, NOTIF_TITLE, NOTIF_TEXT, channelID, notificationID);
    }
}