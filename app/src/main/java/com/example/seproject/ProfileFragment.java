package com.example.seproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment implements View.OnClickListener{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        Button signOutButton = view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        User.signOut();

        Intent i = new Intent(getContext(), LogIn.class);
        startActivity(i);

    }


}