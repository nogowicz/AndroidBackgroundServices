package com.example.androidtlo;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GetAsyncInfo extends AsyncTask<String, Integer, List<Object>>  {


    private Integer mRozmiar;
    private String mTyp;
    private TextView fileSizeTextView;
    private TextView fileTypeTextView;


    public GetAsyncInfo(TextView fileSizeTextView, TextView fileTypeTextView) {
        this.fileSizeTextView = fileSizeTextView;
        this.fileTypeTextView = fileTypeTextView;
    }

    @Override
    protected List<Object> doInBackground(String... params) {
        String adres_url = params[0];
        HttpsURLConnection polaczenie = null;
        try {
            URL url = new URL(adres_url);
            polaczenie = (HttpsURLConnection) url.openConnection();
            polaczenie.setRequestMethod("GET");
            mRozmiar = polaczenie.getContentLength();
            mTyp = polaczenie.getContentType();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (polaczenie != null) polaczenie.disconnect();
        }
        List<Object> result = new ArrayList<>();
        result.add(mTyp);
        result.add(mRozmiar);
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //aktualizacja informacji o postępie
    }

    @Override
    protected void onPostExecute(List<Object> result) {
        String typ = (String) result.get(0);
        Integer rozmiar = (Integer) result.get(1);
        fileSizeTextView.setText(String.valueOf(rozmiar));
        fileTypeTextView.setText(typ);
    }
}
