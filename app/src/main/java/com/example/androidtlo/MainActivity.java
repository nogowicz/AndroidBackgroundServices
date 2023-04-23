package com.example.androidtlo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private EditText addressTextField;
    private Button getInfoButton;
    private TextView fileSizeTextView;
    private TextView fileTypeTextView;
    private Button getFileButton;
    private TextView downloadedBytesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressTextField = (EditText) findViewById(R.id.addressTextField);
        getInfoButton = (Button) findViewById(R.id.getInfoButton);
        fileSizeTextView = (TextView) findViewById(R.id.fileSizeTextView);
        fileTypeTextView = (TextView) findViewById(R.id.fileTypeTextView);
        getFileButton = (Button) findViewById(R.id.getFileButton);
        downloadedBytesTextView = (TextView) findViewById(R.id.downloadedBytesTextView);


        addressTextField.setText("https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-5.4.36.tar.xz");
        String url = addressTextField.getText().toString();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }


        getInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetAsyncInfo getAsyncInfo = new GetAsyncInfo(fileSizeTextView, fileTypeTextView);
                getAsyncInfo.execute(url);
            }
        });


        getFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(url);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Uprawnienie zostało przyznane, można wykonać operacje na karcie pamięci
                } else {
                    // Uprawnienie nie zostało przyznane, nie można wykonać operacji na karcie pamięci
                }
                return;
            }
        }
    }

    public void downloadFile(String url) {
        Intent intent = new Intent(this, DownloadFile.class);
        intent.putExtra("url", url);
        startService(intent);
    }
}