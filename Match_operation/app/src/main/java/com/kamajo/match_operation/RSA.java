package com.kamajo.match_operation;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import static android.content.ContentValues.TAG;


public class RSA {

    public static final String KEY_ALGORITHM = "RSA";

    public void initKey() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/public.key");//Get OutputStream for NewFile Location
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(publicKey.getEncoded());
            fos.close();

            File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/private.key");//Get OutputStream for NewFile Location
            FileOutputStream fos2 = new FileOutputStream(file2);
            fos2.write(privateKey.getEncoded());
            fos2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear_data() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/public.key");
            file.delete();
            File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/private.key");
            file2.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send_key() {
        AttemptSavetoserweFile attemptSavetoserweFile = new AttemptSavetoserweFile();
        attemptSavetoserweFile.execute();
    }

    public String encrypt(String text) {
        String result = "";
        Log.e(TAG, "czytam ");
        byte[] encodedPublicKey = null;
        try {
            File fis1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/public.txt");
            FileInputStream fis = new FileInputStream(fis1);
            Log.e(TAG, "otworzylem ");
            encodedPublicKey = new byte[1024];
            fis.read(encodedPublicKey);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Log.e(TAG, "do kodowania " + text);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(spec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING"); //or try with "RSA"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            Log.e(TAG, "zaszyfrowany" + text + " = " + encrypted);
            result = Base64.encodeBase64String(encrypted);
            Log.e(TAG, "zaszyfrowany" + text + " = " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    //pobranie klucza publicznego z serwera
    public void get_public_key() {
        AttemptFile file = new AttemptFile();
        file.execute();
    }
}

class AttemptSavetoserweFile extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {

            // the file to be posted
            String textFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/public.key";
            Log.v(TAG, "textFile: " + textFile);

            // the URL where the file will be posted
            String postReceiverUrl = "http://192.168.0.154/Match_server/ping.php";
            Log.v(TAG, "postURL: " + postReceiverUrl);

            // new HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);

            File file = new File(textFile);
            FileBody fileBody = new FileBody(file);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addPart("file", fileBody);
            HttpEntity multiPartEntity = builder.build();


            httpPost.setEntity(multiPartEntity);
            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity).trim();
                Log.v(TAG, "Response: " + responseStr);

                // you can add an if statement here and do other actions based on the response
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

}

class AttemptFile extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        try {
            URL url = new URL("http://192.168.0.154/Match_server/file.php");//Create Download URl
            HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
            c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
            c.connect();//connect the URL Connection

            if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server returned HTTP " + c.getResponseCode() + " " + c.getResponseMessage());
            }

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/public.txt");//Get OutputStream for NewFile Location
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                Log.e(TAG, "data " + buffer);
                fos.write(buffer, 0, len1);//Write new file
            }
            fos.close();
            is.close();
            Log.e(TAG, "Download ");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Download Error Exception " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}