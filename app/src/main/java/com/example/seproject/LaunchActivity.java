package com.example.seproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        boolean DEFAULT_VAL = false;
        boolean signedIn = sharedPref.getBoolean("signedIn", DEFAULT_VAL);

        if (signedIn) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        else {
            Intent i = new Intent(this, LogIn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }
}