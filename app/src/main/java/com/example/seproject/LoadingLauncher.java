package com.example.seproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.seproject.data_classes.FBref;
import com.example.seproject.registration.LogIn;
import com.example.seproject.tools.ToolsFragment;
import com.google.firebase.auth.FirebaseUser;



/**
 * @author		Daniel Bronfenbrener
 * @version 1.0
 * @since 04/06/2024
 * The starting screen, which redirects the user to the main activity or log in screen
 */

public class LoadingLauncher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_launcher);

        SharedPreferences sharedPreferences = getSharedPreferences(ToolsFragment.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        ToolsFragment.updateLastActiveTime(sharedPreferences);


        // checks if user is already signed in
        FirebaseUser currentUser = FBref.FBAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d("SEProject", "Redirecting to log in");
            Intent i = new Intent(this, LogIn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else {
            Log.d("SEProject", "User " + currentUser.getUid() + " already signed in, redirecting to main");
            LogIn.getUserDataFromFB(getBaseContext(), this);
        }

    }
}