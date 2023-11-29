package com.example.seproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public abstract class ImageAct extends BaseActivity {
    protected static final int REQUEST_CODE = 12;
    protected ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
    }

    public abstract void getImage(View view);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            setImage(data);

        }
    }
    protected abstract void setImage(Intent data);

    protected abstract String filePath();
    public void saveToFB(View view){
        Drawable drawable = imageView.getDrawable();
        if (drawable == null){
            Toast toast = Toast.makeText(getBaseContext(), "Upload image", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // create file path
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        String filename = "img" + year
                + "-" + month
                + "-" + day
                + "-" + hour
                + "-" + min
                + "-" + sec
                + ".jpeg";
        StorageReference imageRef = FBref.refStorage.child(filePath() + "/" + filename);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast toast = Toast.makeText(getBaseContext(), "Failed to save to FB", Toast.LENGTH_SHORT);
                toast.show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast toast = Toast.makeText(getBaseContext(), "Successfully saved to FB", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}