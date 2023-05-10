package com.example.androidtlo;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadFile extends IntentService {

    NotificationManager mManagerPowiadomien;
    NotificationManager mNotificationManager;;
    public static final String ID_KANALU = "KANAL_POWIADOMIEN";

    int total = 0;
    int fileLength;
    boolean czyPobieranieTrwa = true;
    private Handler mHandler;

    public DownloadFile()
    {
        super("DownloadFile");
    }
    public static final int NOTIFICATION_ID = 1;


    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                PostepInfo postepInfo = (PostepInfo) message.obj;
                updateNotification(postepInfo);
            }
        };
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String url = intent.getStringExtra("url");
        try {
            URL downloadUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) downloadUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            fileLength = conn.getContentLength();

            InputStream input = new BufferedInputStream(downloadUrl.openStream());
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/downloaded_file");

            byte[] data = new byte[1024];
            mNotificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            przygotujKanalPowiadomien();
            startForeground(NOTIFICATION_ID, utworzPowiadomienie());
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                mNotificationManager.notify(NOTIFICATION_ID, utworzPowiadomienie());

                output.write(data, 0, count);
                Log.d("DownloadProgress", "Downloaded " + total + " / " + fileLength + " bytes");

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.example.androidtlo.DOWNLOAD_STATUS");
                PostepInfo postepInfo = new PostepInfo(total, fileLength, "Pobieranie w trakcie");
                broadcastIntent.putExtra("postepInfo", postepInfo);
                sendBroadcast(broadcastIntent);

            }

            output.flush();
            output.close();
            input.close();
            czyPobieranieTrwa = false;

            PostepInfo postepInfo = new PostepInfo(total, fileLength, "Pobieranie zakończone");
            Message message = mHandler.obtainMessage();
            message.obj = postepInfo;
            mHandler.sendMessage(message);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.example.androidtlo.DOWNLOAD_STATUS");
            broadcastIntent.putExtra("downloaded_bytes", total);
            sendBroadcast(broadcastIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void przygotujKanalPowiadomien() {
        mManagerPowiadomien = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel kanal = new NotificationChannel(ID_KANALU, name, NotificationManager.IMPORTANCE_LOW);
            mManagerPowiadomien.createNotificationChannel(kanal);
        }
    }

    private Notification utworzPowiadomienie() {
        Intent intencjaPowiadomienia = new Intent(this, MainActivity.class);
        TaskStackBuilder budowniczyStosu = TaskStackBuilder.create(this);

        budowniczyStosu.addParentStack(MainActivity.class);
        budowniczyStosu.addNextIntent(intencjaPowiadomienia);
        PendingIntent intencjaOczekujaca = budowniczyStosu.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder budowniczyPowiadomien = new NotificationCompat.Builder(this, ID_KANALU);

        budowniczyPowiadomien.setContentTitle(getString(R.string.powiadomienie_tytul))
                .setProgress(fileLength, total, false)
                .setContentIntent(intencjaOczekujaca)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true);

        if (czyPobieranieTrwa) {
            budowniczyPowiadomien.setOngoing(true);

        } else {
            budowniczyPowiadomien.setOngoing(false);
        }

        return budowniczyPowiadomien.build();
    }

    private void updateNotification(PostepInfo postepInfo) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ID_KANALU)
                .setContentTitle(getString(R.string.powiadomienie_tytul))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true);

        if(postepInfo.mStatus.equals("Pobieranie w trakcie")) {
            builder.setProgress((int) postepInfo.mRozmiar, (int) postepInfo.mPobranychBajtow, false)
                    .setContentText("Pobrano: " + postepInfo.mPobranychBajtow + " / " + postepInfo.mRozmiar + " bajtów");
        } else {
            builder.setProgress(0, 0, false)
                    .setContentText("Pobieranie Zakończone");
        }
        Notification notification = builder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

}




