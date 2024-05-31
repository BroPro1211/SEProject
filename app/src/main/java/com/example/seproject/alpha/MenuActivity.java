package com.example.seproject.alpha;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent i = null;

        if (id == R.id.logInMenu){
            FirebaseAuth.getInstance().signOut();

            i = new Intent(this, LogIn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        else if (id == R.id.bookMenu){
            Toast toast = Toast.makeText(this, "Not yet", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (id == R.id.toolsMenu) {
            Toast toast = Toast.makeText(this, "Not yet", Toast.LENGTH_SHORT);
            toast.show();
        }


        /*i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();*/

        /*return super.onOptionsItemSelected(item);
    }*/
}
