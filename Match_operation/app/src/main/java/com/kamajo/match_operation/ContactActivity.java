package com.kamajo.match_operation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }

    public void homepage(View view) {
        Intent i = new Intent(ContactActivity.this, MainActivity.class);
        startActivity(i);
    }
}
