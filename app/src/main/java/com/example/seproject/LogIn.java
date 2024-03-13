package com.example.seproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {
    private EditText emailET;
    private EditText passwordET;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
    }

    public void signUp(View view){
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
        finish();
    }

    public void logIn(View view){
        // get username and password
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if (email.length() == 0 || password.length() == 0) {
            Toast toast = Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        FBref.FBAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // FirebaseUser user = FBref.FBAuth.getCurrentUser();

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("loggedIn", true);
                            editor.apply();

                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            /*Intent intent = new Intent(getBaseContext(), LoggedIn.class);
                            intent.putExtra("username", email);
                            intent.putExtra("password", password);*/
                            startActivity(intent);
                        }
                        else {
                            Toast toast = Toast.makeText(getBaseContext(), "Invalid credentials", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

    }

}