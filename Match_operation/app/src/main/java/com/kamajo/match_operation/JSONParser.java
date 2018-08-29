package com.kamajo.match_operation;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import static android.content.ContentValues.TAG;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    static String error = "";
    String pom = "";

    public JSONParser() {
    }

    public JSONObject makeHttpRequest(String url, String method, ArrayList params) {
        //Tworzenie zapytatnie HTTP
        try{
            if (method.equals("POST")) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                try {
                    Log.e("API123", " " + convertStreamToString(httpPost.getEntity().getContent()));
                    Log.e("API123", httpPost.getURI().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.e("API123", "" + httpResponse.getStatusLine().getStatusCode());
                error = String.valueOf(httpResponse.getStatusLine().getStatusCode());
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } else if (method.equals("GET")) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //czytanie z requesta
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("API123", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        byte[] t = Base64.decode(json, Base64.DEFAULT);
        pom=decrypt(t);
        // try parse the string to a JSON object
        json = pom;
        try {
            jObj = new JSONObject(json);
            jObj.put("error_code", error);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    private String decrypt(byte[] t) {
        List<String> list = new ArrayList<String>();
        byte[] encrypted = null;
        //odszyfrowuje
        byte[] encodedPrivateKey = null;
        try {
            try {
                File fis1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/private.key");
                FileInputStream fis = new FileInputStream(fis1);
                Log.e(TAG, "otworzylem ");
                encodedPrivateKey = new byte[1024];
                fis.read(encodedPrivateKey);
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Log.e(TAG, "response " + t);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int j = 0;
            int blockSize = cipher.getBlockSize();
            while (t.length - j * blockSize > 0) {
                encrypted = (cipher.doFinal(t, j * blockSize, blockSize));
                list.add(new String(encrypted));
                j++;
            }

            for (int i = 0; i < list.size(); i++) {
                pom += list.get(i);
            }
            Log.e(TAG, "otworzylem " + pom);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pom;
    }
}