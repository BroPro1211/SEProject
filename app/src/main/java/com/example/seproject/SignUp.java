package com.example.seproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.LinkedList;

public class SignUp extends BaseActivity {
    private EditText emailET;
    private EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);

    }

    public void signUp(View view){
        // get username and password
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if (email.length() == 0 || password.length() == 0) {
            Toast toast = Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        FBref.FBAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                        }
                        else {
                            Toast toast = Toast.makeText(getBaseContext(), "Authentication failed", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });


    }
}