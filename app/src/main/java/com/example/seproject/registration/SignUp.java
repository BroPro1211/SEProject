package com.example.seproject.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;


/**
 * @author		Daniel Bronfenbrener
 * @version 1.0
 * @since 04/06/2024
 * The sign up screen
 */
public class SignUp extends AppCompatActivity {
    private EditText emailET;
    private EditText usernameET;
    private EditText passwordET;
    private EditText verifyPasswordET;
    private Button backButton;
    private Button signUpButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();


    }
    /**
     * Initializes the activity views
     */
    private void initViews(){
        emailET = findViewById(R.id.emailEditText);
        usernameET = findViewById(R.id.usernameEditText);
        passwordET = findViewById(R.id.passwordEditText);
        verifyPasswordET = findViewById(R.id.verifyPasswordEditText);

        backButton = findViewById(R.id.backButton);
        signUpButton = findViewById(R.id.signUpButton);

        progressBar = findViewById(R.id.progressBar);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Gets user input and creates a new user for him. If successful, opens the main activity.
     */
    private void signUp(){
        String email = emailET.getText().toString();
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        String verifyPassword = verifyPasswordET.getText().toString();

        //check input
        boolean error = false;
        if (email.length() == 0) {
            emailET.setError("Enter email");
            error = true;
        }
        if (username.length() == 0){
            usernameET.setError("Enter username");
            error = true;
        }
        if (password.length() < 6){
            passwordET.setError("Password must contain at least 6 characters");
            error = true;
        }
        if (!password.equals(verifyPassword)){
            verifyPasswordET.setError("Incorrect password");
            error = true;
        }
        if (error) {
            Log.d("SEProject", "Illegal sign up input");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.INVISIBLE);

        Activity thisActivity = this;

        Log.d("SEProject", "Attempting to sign up");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SEProject", "Successfully created user");

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            User user = new User(uid, username);
                            FBref.FBUsers.child(uid).setValue(user);

                            MainActivity.setCurrentUser(user);

                            LogIn.enterApplication(getBaseContext(), thisActivity);
                        }
                        else {
                            Exception e = task.getException();
                            Log.d("SEProject", "Failed to sign up", e);

                            if (e instanceof FirebaseAuthUserCollisionException)
                                emailET.setError("User already exists");
                            else if (e instanceof FirebaseAuthWeakPasswordException)
                                passwordET.setError("Password length must be at least 6 characters");
                            else if (e instanceof FirebaseAuthInvalidCredentialsException)
                                emailET.setError("Invalid email");
                            else Toast.makeText(getBaseContext(), "Failed to sign up", Toast.LENGTH_LONG).show();

                            progressBar.setVisibility(View.INVISIBLE);
                            signUpButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

}