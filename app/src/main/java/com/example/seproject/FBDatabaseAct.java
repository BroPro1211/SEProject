package com.example.seproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class FBDatabaseAct extends BaseActivity {
    private EditText titleET;
    private EditText textET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbdatabase);

        titleET = findViewById(R.id.titleEditText);
        textET = findViewById(R.id.textEditText);
    }

    public void saveToFB(View view){
        String title = titleET.getText().toString();
        String text = textET.getText().toString();

        if (title.length() == 0 || text.length() == 0){
            Toast toast = Toast.makeText(getBaseContext(), "Enter title and text", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        final String FILE_PATH = "textFiles";
        StorageReference fileRef = FBref.refStorage.child(FILE_PATH + "/" + title + ".txt");

        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        UploadTask uploadTask = fileRef.putStream(stream);
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
                titleET.setText("");
                textET.setText("");
            }
        });
    }
}