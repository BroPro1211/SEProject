package com.example.seproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsAct extends BaseActivity {
    private static final int REQUEST_CODE = 1;
    private Runnable action;
    private Runnable failureAction;


    public static boolean checkPermission(Context context, String permission) {
        // checks if permission is not currently granted
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    protected void getPermissionBeforeAction(String permission, Runnable action, Runnable failureAction){
        if (! checkPermission(this, permission)) {
            this.action = action;
            this.failureAction = failureAction;
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

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            action.run();
        else
            failureAction.run();
    }
}
