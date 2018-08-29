package com.kamajo.match_operation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends Activity {

    ListView list;
    JSONParser jsonParser = new JSONParser();
    RSA r = new RSA();
    String url = "http://192.168.0.154/Match_server/history.php";
    String t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        list = (ListView) findViewById(R.id.view);
        r.send_key();
        r.get_public_key();
        SharedPreferences info = getSharedPreferences((String) getText(R.string.File_name), Context.MODE_PRIVATE);
        t = info.getString("ID", "");
        AttemptHistory attemptHistory = new AttemptHistory();
        attemptHistory.execute(r.encrypt(t));
    }

    private class AttemptHistory extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String id = args[0];

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("ID", id));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);
            return json;
        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("message").equals("success")) {
                        String[] item = new String[(result.getInt("count"))];
                        int k = 0;
                        for (int i = 0; i < result.getInt("count"); i++) {
                            item[i] = (result.getString(String.valueOf((int) k)) + " = " + result.getString(String.valueOf((int) k + 1)));
                            k +=2;
                        }
                        show(item);
                    } else {
                        Toast.makeText(getApplicationContext(), "message", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void homepage(View view) {
        Intent i = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void show(String[] t) {
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, t);
        list.setAdapter(adapter);

    }

}

