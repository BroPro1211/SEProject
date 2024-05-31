package com.example.seproject.alpha;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class GalleryAct extends ImageAct {
    public void getImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE);
    }

    protected void setImage(Intent data){
        Uri image = data.getData();
        if (image != null)
            imageView.setImageURI(image);
    }

    protected String filePath(){
        return "images/gallery";
    }
}
