package com.example.seproject;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationClass {
    private NotificationManagerCompat notificationManager;

    public NotificationClass(Context context) {
        notificationManager = NotificationManagerCompat.from(context);
    }

    public void createChannel(String channelID, String channelName) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void notification(Context context, String title, String text, String channelID, int notificationID) {

        // build notification
        NotificationCompat.Builder notiBbuilder = new
                NotificationCompat.Builder(context, channelID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (PermissionsAct.checkPermission(context, android.Manifest.permission.POST_NOTIFICATIONS))
            notificationManager.notify(notificationID, notiBbuilder.build());


    }
}
