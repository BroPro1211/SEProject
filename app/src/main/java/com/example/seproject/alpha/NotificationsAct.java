package com.example.seproject.alpha;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.Manifest;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.seproject.R;

import java.util.Calendar;

public class NotificationsAct extends PermissionsActivity {
    private EditText notificationTextET;
    private final String CHANNEL_ID = "123";
    private final String CHANNEL_NAME = "Notifications channel";
    private final int NOTIFICATION_ID = 1;
    private NotificationClass notificationClass;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private final int ALARM_REQUEST_CODE = 32;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationTextET = findViewById(R.id.notificationEditText);
        timePicker = findViewById(R.id.timePicker);

        notificationClass = new NotificationClass(this);
        notificationClass.createChannel(CHANNEL_ID, CHANNEL_NAME);
    }

    private void sendNotification() {
        // get text
        String text = notificationTextET.getText().toString();
        notificationTextET.setText("");

        final String TITLE = "Alpha Version Notification";
        notificationClass.notification(this, TITLE, text, CHANNEL_ID, NOTIFICATION_ID);
    }
    private void permissionFailed(){
        Toast toast = Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT);
        toast.show();
    }
    public void sendNotification(View view){
        getPermissionBeforeAction(getBaseContext(), Manifest.permission.POST_NOTIFICATIONS, this::sendNotification, this::permissionFailed);
    }

    public void timedNotification(View view){
        getPermissionBeforeAction(getBaseContext(), Manifest.permission.POST_NOTIFICATIONS, this::timedNotification, this::permissionFailed);
    }
    public void timedNotification(){
        // create intents
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("channelID", CHANNEL_ID);
        intent.putExtra("channelName", CHANNEL_NAME);
        intent.putExtra("notificationID", NOTIFICATION_ID);

        alarmIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // calculate time
        int hour = timePicker.getHour();
        int min = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);
        int currentSec = calendar.get(Calendar.SECOND);

        int timeDiff = 60*((hour-currentHour)*60 + min-currentMin) - currentSec;
        calendar.add(Calendar.SECOND, timeDiff);
        if (currentHour > hour || (currentHour == hour && currentMin > min))
            calendar.add(Calendar.HOUR, 24);
        Log.d("timediff", "s:" + ((calendar.getTimeInMillis()-Calendar.getInstance().getTimeInMillis())/1000));

        alarmManager.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), alarmIntent);

    }

}