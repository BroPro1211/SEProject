package com.example.seproject.tools;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.seproject.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Fragment that displays the reading mode tool
 */
public class ReadingModeFragment extends Fragment implements Runnable, View.OnClickListener{
    private TextView elapsedTimeTV;
    private int elapsedSeconds;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentNotificationsFilter;
    private NotificationManager notificationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_reading_mode, container, false);


        TextView notificationsSilencedTV = view.findViewById(R.id.notificationsSilencedTV);

        notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager.isNotificationPolicyAccessGranted()) {
            notificationsSilencedTV.setVisibility(View.VISIBLE);
            currentNotificationsFilter = notificationManager.getCurrentInterruptionFilter();
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
        }
        else
            notificationsSilencedTV.setVisibility(View.GONE);


        elapsedTimeTV = view.findViewById(R.id.elapsedTimeTV);

        elapsedSeconds = 0;
        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(this);

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(this, 0, 1, TimeUnit.SECONDS);

        return view;

    }

    @Override
    public void run() {
        // Without runOnUiThread we get exception: Only the original thread that created a view hierarchy can touch its views.
        // Hence we must use runOnUiThread, which runs the code on the main thread
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String timeString = formatTime(elapsedSeconds);
                elapsedTimeTV.setText(timeString);

                elapsedSeconds++;

                if (timeString.equals("99:59:59")){
                    getParentFragmentManager().popBackStack();
                }
            }
        });
    }

    /**
     * Formats the elapsed seconds into a string
     * @param elapsedSeconds The number of elapsed seconds
     * @return Returns the formatted string
     */
    private String formatTime(int elapsedSeconds){
        int hours = elapsedSeconds / 3600;
        int minutes = (elapsedSeconds % 3600) / 60;
        int seconds = elapsedSeconds % 60;

        return String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
    }

    @Override
    public void onClick(View v) {
        getParentFragmentManager().popBackStack();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        scheduledExecutorService.shutdown();

        if (notificationManager.isNotificationPolicyAccessGranted())
            notificationManager.setInterruptionFilter(currentNotificationsFilter);
    }
}