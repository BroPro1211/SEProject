package com.example.seproject.tools;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.seproject.R;

import java.util.Calendar;


public class ToolsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private Button readingModeButton;
    private SwitchCompat readingRemindersSwitch;
    public static final String LAST_ACTIVE_TIME = "last active time";
    public static final String READING_REMINDERS_ENABLED = "reading reminders enabled";
    public static final String SHARED_PREFERENCES_NAME = "shared preferences";
    private SharedPreferences sharedPreferences;
    public static int ALARM_REQUEST_CODE = 1;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private ActivityResultLauncher<String> getNotificationPermission;
    private ActivityResultLauncher<Object> getDoNoDisturbPermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), ReadingRemindersReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        getNotificationPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o)
                    enableReadingReminders();
                else {
                    Toast.makeText(getContext(), "Grant notifications permission to use this feature", Toast.LENGTH_LONG).show();
                    readingRemindersSwitch.setChecked(false);
                }
            }
        });

        getDoNoDisturbPermission = registerForActivityResult(new ActivityResultContract<Object, Object>() {
            @Override
            public Object parseResult(int i, @Nullable Intent intent) {
                return null;
            }

            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Object o) {
                return new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            }
        }, new ActivityResultCallback<Object>() {
            @Override
            public void onActivityResult(Object o) {
                openReadingMode();
            }
        });

        initViews(view);

        return view;
    }

    private void initViews(View view){
        readingModeButton = view.findViewById(R.id.readingModeButton);
        readingModeButton.setOnClickListener(this);

        readingRemindersSwitch = view.findViewById(R.id.readingRemindersSwitch);
        readingRemindersSwitch.setChecked(sharedPreferences.getBoolean(ToolsFragment.READING_REMINDERS_ENABLED, false));
        readingRemindersSwitch.setOnCheckedChangeListener(this);
    }


    @Override
    public void onClick(View v) {

        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Toast.makeText(getContext(), "In order to use do not disturb, provide access to the app", Toast.LENGTH_LONG).show();
            getDoNoDisturbPermission.launch(null);
        }
        else
            openReadingMode();

    }

    private void openReadingMode(){
        Log.d("SEProject", "Opening reading mode");
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ReadingModeFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            // starting from android version tiramisu, sending notifications requires permission from the user
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                getNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            else
                enableReadingReminders();
        }
        else{
            disableReadingReminders();
        }

    }

    private void enableReadingReminders(){
        Log.d("SEProject", "Enabling reading reminders");

        sharedPreferences.edit()
                .putBoolean(READING_REMINDERS_ENABLED, true)
                .apply();

        updateLastActiveTime(sharedPreferences);

        alarmManager.setRepeating( AlarmManager.RTC_WAKEUP, getTriggerTime(), getIntervalTime() , pendingIntent);

        Log.d("SEProject", "Alarm manager set");
    }
    private long getTriggerTime(){
        // sets the trigger time to be at 9AM
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        return calendar.getTimeInMillis();


        /*
        // for testing purposes: sets the trigger time to be at a round minute
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
         */

    }
    private long getIntervalTime(){
        return AlarmManager.INTERVAL_DAY;


        /*
        // for testing purposes: activate the receiver each 60 seconds, as each minute passes
        // alarm times are not accurate so there will sometimes be a delay of some tens of seconds, but
        // at large intervals the error is negligible
        return 1000 * 60;
        */
    }

    private void disableReadingReminders(){
        Log.d("SEProject", "Disabling reading reminders");
        sharedPreferences.edit()
                .putBoolean(READING_REMINDERS_ENABLED, false)
                .apply();

        alarmManager.cancel(pendingIntent);
    }

    public static void updateLastActiveTime(SharedPreferences sharedPreferences){
        if (sharedPreferences.getBoolean(ToolsFragment.READING_REMINDERS_ENABLED, false)){
            long currentTime = System.currentTimeMillis();
            sharedPreferences.edit()
                    .putLong(LAST_ACTIVE_TIME, currentTime)
                    .apply();

            Log.d("SEProject", "Updated current time to " + currentTime);
        }
        else
            Log.d("SEProject", "Didn't update current time as reading reminders are disabled");
    }
}