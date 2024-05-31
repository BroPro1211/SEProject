package com.example.seproject.alpha;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.seproject.R;
import com.example.seproject.alpha.MenuActivity;

public class SharedPreferencesAct extends MenuActivity {
    private EditText textET;
    private TextView textTV;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_preferences);

        textET = findViewById(R.id.textEditText);
        textTV = findViewById(R.id.textTextView);
        sharedPref = getPreferences(Context.MODE_PRIVATE);

        displayText();

    }
    public void saveText(View view){
        String text = textET.getText().toString();
        textET.setText("");

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("text", text);
        editor.apply();

        displayText();

    }

    private void displayText(){
        final String DEFAULT_VAL = "Text will display here!";
        String text = sharedPref.getString("text", DEFAULT_VAL);

        textTV.setText(text);
    }
}