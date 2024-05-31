package com.example.seproject.alpha;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationClass {
    private NotificationManagerCompat notificationManager;

    public NotificationClass(Context context) {
        notificationManager = NotificationManagerCompat.from(context);
    }

    public void createChannel(String channelID, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    public void notification(Context context, String title, String text, String channelID, int notificationID) {

        // build notification
        NotificationCompat.Builder notificationBbuilder = new
                NotificationCompat.Builder(context, channelID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            notificationManager.notify(notificationID, notificationBbuilder.build());


    }
}
