package com.example.seproject.alpha;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private Runnable action;
    private Runnable failureAction;
    private String permission;


    public void getPermissionBeforeAction(Context context, String permission, Runnable action, Runnable failureAction){
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            this.action = action;
            this.failureAction = failureAction;
            this.permission = permission;

            Log.d("SEProject", "Asking for permission " + permission);

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    REQUEST_CODE);
        }
        else
            action.run();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("SEProject", "Permission " + permission + " granted");
            action.run();
        }
        else {
            Log.d("SEProject", "Permission " + permission + " not granted");
            if (failureAction != null)
                failureAction.run();
        }
    }
}
