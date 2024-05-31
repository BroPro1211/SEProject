package com.example.seproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class BooksAPI extends AppCompatActivity {
    private EditText bookET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_api);

        bookET = (EditText) findViewById(R.id.bookEditText);

    }


    public void searchBook(View view) {
        String bookName = bookET.getText().toString();


    }
}