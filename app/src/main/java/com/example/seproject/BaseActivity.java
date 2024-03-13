package com.example.seproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent i = null;

        if (id == R.id.logInMenu){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("loggedIn", false);
            editor.apply();

            FBref.FBAuth.signOut();

            i = new Intent(this, LaunchActivity.class);
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

        return super.onOptionsItemSelected(item);
    }
}
