package com.example.seproject.alpha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
public class CameraAct extends ImageAct {
    public void getImage(View view){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    protected void setImage(Intent data){
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Bitmap image = (Bitmap) bundle.get("data");
            if (image != null)
                imageView.setImageBitmap(image);
        }
    }

    protected String filePath() {
        return "images/camera";
    }
}