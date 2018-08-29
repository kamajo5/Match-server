package com.kamajo.match_operation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    TextView name, email;
    AppCompatButton btn;
    RSA r = new RSA();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        btn = (AppCompatButton) findViewById(R.id.btn_logout);
        btn.setVisibility(View.GONE);
        setInfo();
    }

    private void setInfo() {
        SharedPreferences info = getSharedPreferences((String) getText(R.string.File_name), Context.MODE_PRIVATE);
        String t;
        t = info.getString("ID", "");
        if (!t.equals("")) {
            btn.setVisibility(View.VISIBLE);
            t = info.getString("Login", "");
            name.append(t);
            t = info.getString("Email", "");
            email.append(t);
        }
    }

    public void logout(View view) {
        SharedPreferences info = getSharedPreferences((String) getText(R.string.File_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.clear();
        editor.commit();
        r.clear_data();
        Intent i = new Intent(InfoActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void homepage(View view) {
        Intent i = new Intent(InfoActivity.this, MainActivity.class);
        startActivity(i);
    }
}