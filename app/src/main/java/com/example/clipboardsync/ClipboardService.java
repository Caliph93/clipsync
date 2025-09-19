package com.example.clipboardsync;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class ClipboardService extends Service {
    private static final String TAG = "ClipboardService";
    private ClipboardManager clipboardManager;
    private String lastText = null;
    private String serverUrl = null;
    private OkHttpClient client = new OkHttpClient();

    private ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            try {
                if (clipboardManager.hasPrimaryClip()) {
                    CharSequence cs = clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(ClipboardService.this);
                    String text = cs != null ? cs.toString() : "";
                    if (!text.isEmpty() && !text.equals(lastText)) {
                        lastText = text;
                        postToServer(text);
                        startForeground(1, NotificationHelper.buildNotification(ClipboardService.this, "Sent: " + shortText(text)));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "clipboard read error", e);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverUrl = intent != null ? intent.getStringExtra("server_url") : null;
        if (serverUrl == null) {
            serverUrl = getSharedPreferences("clip_sync", Context.MODE_PRIVATE).getString("server_url", null);
        }
        String notifText = serverUrl != null ? serverUrl : "No server set";
        startForeground(1, NotificationHelper.buildNotification(this, notifText));
        clipboardManager.addPrimaryClipChangedListener(listener);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            clipboardManager.removePrimaryClipChangedListener(listener);
        } catch (Exception e) {
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void postToServer(String text) {
        if (serverUrl == null) {
            Log.w(TAG, "server url not set");
            return;
        }
        String url = serverUrl.trim();
        if (!url.endsWith("/")) url = url + "/";
        url = url + "set";
        RequestBody body = RequestBody.create(text, MediaType.parse("text/plain; charset=utf-8"));
        Request req = new Request.Builder().url(url).post(body).build();
        client.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.w(TAG, "post failed: " + e.getMessage());
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }

    private String shortText(String s) {
        return s.length() > 30 ? s.substring(0,30) + "..." : s;
    }
}
