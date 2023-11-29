package com.example.seproject;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

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
            i = new Intent(this, LogIn.class);
        }
        else if (id == R.id.galleryMenu){
            i = new Intent(this, GalleryAct.class);
        }
        else if (id == R.id.cameraMenu) {
            i = new Intent(this, CameraAct.class);
        }
        else if (id == R.id.FBStorageMenu) {
            i = new Intent(this, FBDatabaseAct.class);
        }
        else if (id == R.id.notificationMenu) {
            i = new Intent(this, NotificationsAct.class);
        }
        else if (id == R.id.chatMenu) {
            i = new Intent(this, ChatAct.class);
        }
        else if (id == R.id.googleMapsMenu) {
            i = new Intent(this, GoogleMapsAct.class);
        }
        else if (id == R.id.sharedPreferencesMenu){
            i = new Intent(this, SharedPreferencesAct.class);
        }
        assert i != null;

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
