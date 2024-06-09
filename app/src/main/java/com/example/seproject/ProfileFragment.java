package com.example.seproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seproject.data_classes.FBref;
import com.example.seproject.registration.LogIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Fragment displays the logged in user's profile
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    private ImageView profileImage;
    private ActivityResultLauncher<String> getCameraPermission;
    private ActivityResultLauncher<Intent> getImageFromCamera;
    private ActivityResultLauncher<String> getStoragePermission;
    private ActivityResultLauncher<Intent> getImageFromGallery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCameraPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    getImageFromCamera.launch(cameraIntent);
                }
                else {
                    Toast.makeText(getContext(), "Please grant camera permission", Toast.LENGTH_LONG).show();
                }
            }
        });
        getImageFromCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Bitmap image = (Bitmap) result.getData().getExtras().get("data");

                        if (image != null){
                            saveNewImage(image);
                        }

                    }
                }
            }
        });

        getStoragePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    getImageFromGallery.launch(galleryIntent);
                }
                else {
                    Toast.makeText(getContext(), "Please grant storage permission", Toast.LENGTH_LONG).show();
                }
            }
        });
        getImageFromGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null){
                        Bitmap bitmap;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_LONG).show();
                            return;
                        }
                        saveNewImage(bitmap);

                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView emailTV = view.findViewById(R.id.emailTV);
        emailTV.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        TextView usernameTV = view.findViewById(R.id.usernameTV);
        usernameTV.setText(MainActivity.getCurrentUser().getUsername());

        Button signOutButton = view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(this);

        profileImage = view.findViewById(R.id.profileImageView);
        Bitmap image = MainActivity.getCurrentUser().getProfileImage();
        profileImage.setImageBitmap(image);

        ImageButton editProfileImage = view.findViewById(R.id.editProfileImage);
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), editProfileImage);

                popupMenu.getMenuInflater().inflate(R.menu.change_profile_image_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        if (id == R.id.changeImageCamera){
                            getCameraPermission.launch(Manifest.permission.CAMERA);
                        }

                        else{
                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                                getStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            else
                                getStoragePermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

        return view;
    }

    /**
     * Saves a new profile image
     * @param image The bitmap of the new image
     */
    private void saveNewImage(Bitmap image){
        MainActivity.getCurrentUser().setProfileImage(image);
        profileImage.setImageBitmap(image);
        uploadProfileImageToFB(image);
    }

    /**
     * Uploads a new profile image to firebase storage
     * @param image The bitmap of the new image
     */
    private void uploadProfileImageToFB(Bitmap image){
        String uid = MainActivity.getCurrentUser().getUid();

        String fileName = uid + FBref.IMAGE_FILE_EXTENSION;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        StorageReference bookStorageReference = FBref.FBUserImages.child(fileName);

        UploadTask uploadTask = bookStorageReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("SEProject", "Successfully saved user " + uid + " profile image to FB");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("SEProject", "Failed to save user " + uid + " profile image to FB", exception);
            }
        });
    }

    @Override
    public void onClick(View v) {
        MainActivity.signOut();

        Intent i = new Intent(getContext(), LogIn.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }


}