package com.example.seproject.alpha;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.seproject.data_classes.FBref;
import com.example.seproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatAct extends MenuActivity {
    private EditText messageET;
    private ListView messagesLV;
    private List<Message> messagesList;
    private ListAdapter adapter;
    private ValueEventListener messageListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageET = findViewById(R.id.messageEditText);
        messagesLV = findViewById(R.id.messagesListView);

        messagesList = new ArrayList<>();
        FBref.refMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren())
                    messagesList.add(data.getValue(Message.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(getBaseContext(), "Error fetching messages", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, messagesList);
        messagesLV.setAdapter(adapter);

        messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot data : snapshot.getChildren())
                    messagesList.add(data.getValue(Message.class));
                messagesLV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(getBaseContext(), "Error updating messages", Toast.LENGTH_SHORT);
                toast.show();
            }
        };
        FBref.refMessages.addValueEventListener(messageListener);

    }


    public void sendMessage(View view){
        String txt = messageET.getText().toString();

        if (txt.length() == 0){
            Toast toast = Toast.makeText(getBaseContext(), "Enter message", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        int num = messagesList.size() + 1;
        Message message = new Message(num, txt);

        FBref.refMessages.child("msg"+num).setValue(message);

        messageET.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (messageListener != null) {
            FBref.refMessages.removeEventListener(messageListener);
        }

    }
}