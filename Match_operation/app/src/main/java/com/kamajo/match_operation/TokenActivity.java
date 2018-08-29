package com.kamajo.match_operation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TokenActivity extends AppCompatActivity {

    EditText token;

    RSA r = new RSA();
    String url = "http://192.168.0.154/Match_server/token.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        token = (EditText) findViewById(R.id.input_token);
    }

    public void Active(View view) {
        r.get_public_key();
        r.send_key();
        if (CheckFieldValidation() == true) {
            AttemptToken attemptToken = new AttemptToken();
            attemptToken.execute(r.encrypt(Load_id().toString()), r.encrypt(token.getText().toString()));
        }
    }

    private class AttemptToken extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jsonParser = new JSONParser();
            String id = args[0];
            String token = args[1];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("ID", id));
            params.add(new BasicNameValuePair("Token", token));
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);
            return json;
        }

        protected void onPostExecute(JSONObject result) {
            try {
                if (result != null) {
                    Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                    if (result.getString("isActive") == "1") {
                        r.clear_data();
                        Intent i = new Intent(TokenActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean CheckFieldValidation() {
        if (token.getText().toString().equals("")) {
            token.setError("Can't be Empty");
            return false;
        }
        return true;
    }

    private String Load_id() {
        SharedPreferences info = getSharedPreferences((String) getText(R.string.File_name), Context.MODE_PRIVATE);
        String t;
        t = info.getString("ID", "");
        return t;
    }
}
