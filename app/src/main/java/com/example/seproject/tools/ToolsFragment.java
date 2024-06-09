package com.example.seproject.tools;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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


/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Fragment that shows the tools features
 */
public class ToolsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private SwitchCompat readingRemindersSwitch;

    public static final String SHARED_PREFERENCES_NAME = "shared preferences";
    private SharedPreferences sharedPreferences;
    public static final String LAST_ACTIVE_TIME = "last active time";
    public static final String READING_REMINDERS_ENABLED = "reading reminders enabled";

    private AlarmManager alarmManager;
    public final static int ALARM_REQUEST_CODE = 1;
    private PendingIntent pendingIntent;

    private ActivityResultLauncher<String> getNotificationPermission;
    private ActivityResultLauncher<Intent> getDoNoDisturbPermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                    Toast.makeText(getContext(), "Please grant notifications permission", Toast.LENGTH_LONG).show();
                    readingRemindersSwitch.setChecked(false);
                }
            }
        });

        getDoNoDisturbPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        openReadingMode();
                    }
                });

                initViews(view);

        return view;
    }

    /**
     * Initializes the fragment's views
     * @param view The view of the fragment
     */
    private void initViews(View view){
        Button readingModeButton = view.findViewById(R.id.readingModeButton);
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
            getDoNoDisturbPermission.launch(new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }
        else
            openReadingMode();

    }

    /**
     * Opens the reading mode fragment
     */
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
                getNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            else
                enableReadingReminders();
        }
        else{
            disableReadingReminders();
        }

    }

    /**
     * Enables reading reminders
     */
    private void enableReadingReminders(){
        Log.d("SEProject", "Enabling reading reminders");

        sharedPreferences.edit()
                .putBoolean(READING_REMINDERS_ENABLED, true)
                .apply();

        updateLastActiveTime(sharedPreferences);

        alarmManager.setRepeating( AlarmManager.RTC_WAKEUP, getTriggerTime(), getIntervalTime() , pendingIntent);

        Log.d("SEProject", "Alarm manager set");
    }

    /**
     * Returns the time at which to trigger the reading reminders broadcast receiver
     * @return The next time in milliseconds at which to start the broadcast receiver
     */
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

    /**
     * Returns the interval between calls to the broadcast receiver
     * @return Returns the interval in milliseconds
     */
    private long getIntervalTime(){
        return AlarmManager.INTERVAL_DAY;


        /*
        // for testing purposes: activate the receiver each 60 seconds, as each minute passes
        // alarm times are not accurate so there will sometimes be a delay of some tens of seconds, but
        // at large intervals the error is negligible
        return 1000 * 60;
        */
    }

    /**
     * Disables reading reminders
     */
    private void disableReadingReminders(){
        Log.d("SEProject", "Disabling reading reminders");
        sharedPreferences.edit()
                .putBoolean(READING_REMINDERS_ENABLED, false)
                .apply();

        alarmManager.cancel(pendingIntent);
    }

    /**
     * Updates the last time seen on the app
     * @param sharedPreferences The shared preferences to store the last seen time
     */
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