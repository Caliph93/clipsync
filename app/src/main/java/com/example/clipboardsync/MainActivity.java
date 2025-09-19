package com.example.clipboardsync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private EditText editServer;
    private Button btnStart, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editServer = findViewById(R.id.editServer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        SharedPreferences prefs = getSharedPreferences("clip_sync", Context.MODE_PRIVATE);
        editServer.setText(prefs.getString("server_url", ""));

        btnStart.setOnClickListener(v -> {
            String url = editServer.getText().toString().trim();
            if (!url.isEmpty()) {
                prefs.edit().putString("server_url", url).apply();
                Intent i = new Intent(MainActivity.this, ClipboardService.class);
                i.putExtra("server_url", url);
                ContextCompat.startForegroundService(MainActivity.this, i);
            }
        });

        btnStop.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ClipboardService.class);
            stopService(i);
        });
    }
}
