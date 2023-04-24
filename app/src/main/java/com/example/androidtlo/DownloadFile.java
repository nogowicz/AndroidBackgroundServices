package com.example.androidtlo;


import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadFile extends IntentService {

    public DownloadFile()
    {
        super("DownloadFile");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String url = intent.getStringExtra("url");
        try {
            URL downloadUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) downloadUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            int fileLength = conn.getContentLength();

            InputStream input = new BufferedInputStream(downloadUrl.openStream());
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/downloaded_file");

            byte[] data = new byte[1024];
            int total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
                Log.d("DownloadProgress", "Downloaded " + total + " / " + fileLength + " bytes");

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.example.androidtlo.DOWNLOAD_STATUS");
                broadcastIntent.putExtra("downloaded_bytes", total);
                sendBroadcast(broadcastIntent);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




