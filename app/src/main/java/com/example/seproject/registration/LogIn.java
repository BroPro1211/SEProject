package com.example.seproject.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.data_classes.FBref;
import com.example.seproject.data_classes.User;
import com.example.seproject.registration.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

public class LogIn extends AppCompatActivity {
    private EditText emailET;
    private EditText passwordET;
    private Button logInButton;
    private Button signUpButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initViews();

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SEProject", "Navigating to sign up");
                Intent i = new Intent(getBaseContext(), SignUp.class);
                startActivity(i);
            }
        });
    }

    private void initViews(){
        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);

        logInButton = findViewById(R.id.logInButton);
        signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void logIn(){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        // check input
        boolean error = false;
        if (email.length() == 0) {
            emailET.setError("Enter email");
            error = true;
        }
        if (password.length() == 0){
            passwordET.setError("Enter password");
            error = true;
        }
        if (error) {
            Log.d("SEProject", "Illegal login input");
            return;
        }
        Log.d("SEProject", "Checked input");

        progressBar.setVisibility(View.VISIBLE);
        logInButton.setVisibility(View.INVISIBLE);

        Activity thisActivity = this;

        Log.d("SEProject", "Attempting to log in");
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(thisActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SEProject", "Successfully logged in");

                            getUserDataFromFB(getBaseContext(), thisActivity);

                        }
                        else {
                            Log.d("SEProject", "Failed to log in", task.getException());
                            Toast.makeText(getBaseContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            logInButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public static void getUserDataFromFB(Context context, Activity activity){
        Log.d("SEProject", "Attempting to retrieve user data from FB");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            throw new RuntimeException("No logged in user found");
        String uid = user.getUid();

        FBref.FBUsers.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("SEProject", "Successfully retrieved user data from FB");
                    User user = task.getResult().getValue(User.class);

                    //for (BookList)
                    //Log.d()
                    User.setCurrentUser(user);

                    enterApplication(context, activity);
                }
                else {
                    Toast.makeText(context, "Failed to retrieve user data", Toast.LENGTH_LONG).show();
                    Log.d("SEProject", "Failed to retrieve data from FB", task.getException());

                    FirebaseAuth.getInstance().signOut();
                }
            }
        });
    }

    public static void enterApplication(Context context, Activity activity){
        Log.d("SEProject", "User successfully logged in, navigating to main");

        User.checkCurrentUser();

        User.createOrderedBookLists();

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
    }

}