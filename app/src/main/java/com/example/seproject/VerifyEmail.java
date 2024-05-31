package com.example.seproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {
    private TextView TV;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        TV = findViewById(R.id.textView);
        TV.setText("Email verification was sent to " + FirebaseAuth.getInstance().getCurrentUser().getEmail());

        username = getIntent().getStringExtra("username");
    }

    public void checkVerification(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Task<Void> reload = user.reload();
        reload.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user.isEmailVerified()){
                    String uid = user.getUid();
                    User DBUser = new User(uid, username);
                    FBref.FBUsers.child(uid).setValue(user);

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Verify email to proceed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}