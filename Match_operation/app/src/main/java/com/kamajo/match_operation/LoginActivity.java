package com.kamajo.match_operation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    EditText pass, name, check_pass;
    String URL = "http://192.168.0.154/Match_server/user.php";
    JSONParser jsonParser = new JSONParser();
    RSA r = new RSA();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void Signin(View view) {
        r.initKey();
        r.send_key();
        pass = (EditText) findViewById(R.id.input_password);
        name = (EditText) findViewById(R.id.input_name);
        check_pass = (EditText) findViewById(R.id.input_password_repeat);
        if (CheckFieldValidation() == true && IsPasswordSame() == true) {
            AttemptLogin attemptLogin = new AttemptLogin();
            attemptLogin.execute(r.encrypt(name.getText().toString()), r.encrypt(pass.getText().toString()));
        }
    }

    private class AttemptLogin extends AsyncTask<String, String, JSONObject> {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override

        protected JSONObject doInBackground(String... args) {
            String password = args[1];
            String name = args[0];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Login", name));
            params.add(new BasicNameValuePair("Password", password));
            JSONObject json = jsonParser.makeHttpRequest(URL, "POST", params);
            return json;
        }

        protected void onPostExecute(JSONObject result) {
            try {
                if (result != null) {
                    Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                    if (result.getString("message").equals("Done") && result.getString("token").equals("1")) {
                        Save(result.getString("m_i"), result.getString("m_l"), result.getString("m_p"), result.getString("m_e"));
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    } else if (result.getString("message").equals("Done") && result.getString("token").equals("0")) {
                        Save(result.getString("m_i"), result.getString("m_l"), result.getString("m_p"), result.getString("m_e"));
                        Intent i = new Intent(LoginActivity.this, TokenActivity.class);
                        startActivity(i);
                    }
                    else if (result.getString("message").equals("Wrong Input")) {
                        Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
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

        boolean valid = true;
        if (name.getText().toString().equals("")) {
            name.setError("Can't be Empty");
            valid = false;
        } else if (pass.getText().toString().equals("")) {
            pass.setError("Can't be Empty");
            valid = false;
        } else if (check_pass.getText().toString().equals("")) {
            check_pass.setError("Can't be Empty");
            valid = false;
        }
        return valid;
    }

    private boolean IsPasswordSame() {
        if (!pass.getText().toString().equals(check_pass.getText().toString())) {
            check_pass.setError("Password is same");
            return false;
        }
        return true;
    }

    private void Save(String i, String n, String p, String e) {
        SharedPreferences info = getSharedPreferences((String) getText(R.string.File_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.clear();
        editor.commit();
        editor.putString("ID", i);
        editor.putString("Login", n);
        editor.putString("Password", p);
        editor.putString("Email", e);
        editor.commit();
    }
}
