package com.example.srk;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTask extends AsyncTask<String, Integer, String> {

    private ProgressDialog progressDialog;
    private int counter;
    private static final int SCHEMES_COUNT = 1;

    public DownloadTask(ProgressDialog progressDialog, int counter){
        this.progressDialog = progressDialog;
        this.counter = counter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            URL url = new URL(strings[0]);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            int length = urlConnection.getContentLength();

            InputStream inputStream = new BufferedInputStream(url.openStream());
            OutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Schemes/" + strings[1]);

            byte[] data = new byte[1024];
            long total = 0;
            int count;
            while((count = inputStream.read(data)) != -1){
                total += count;
                publishProgress((int) (total * 100 / length));
                outputStream.write(data, 0 , count);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            counter++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(counter == SCHEMES_COUNT){
            progressDialog.dismiss();
        }
    }
}
