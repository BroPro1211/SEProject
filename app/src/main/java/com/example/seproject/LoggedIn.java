package com.example.seproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseUser;

public class LoggedIn extends BaseActivity {
    private TextView emailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        emailTV = findViewById(R.id.emailTextView);

        FirebaseUser user = FBref.FBAuth.getCurrentUser();

        emailTV.setText(user.getEmail());

    }
}