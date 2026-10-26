package com.womensafety.shajt3ch;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FakeCallActivity extends AppCompatActivity {

    private Ringtone ringtone;
    private TextView tvCaller;
    private TextView tvStatus;
    private boolean inCall = false;
    private Handler handler = new Handler();
    private int seconds = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        tvCaller = findViewById(R.id.tvCaller);
        tvStatus = findViewById(R.id.tvStatus);
        ImageButton btnAccept = findViewById(R.id.btnAccept);
        ImageButton btnDecline = findViewById(R.id.btnDecline);

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        playRingtone();

        String caller = getIntent().getStringExtra("callerName");
        if (caller == null || caller.isEmpty()) caller = "Mom";
        tvCaller.setText(caller);
        tvStatus.setText("Incoming call");

        btnAccept.setOnClickListener(v -> {
            inCall = true;
            stopRingtone();
            tvStatus.setText("00:00");
            seconds = 0;
            handler.postDelayed(timerTick, 1000);
        });

        btnDecline.setOnClickListener(v -> {
            stopRingtone();
            finish();
        });
    }

    private void stopRingtone() {
        try {
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }
        } catch (Exception ignored) {}
    }

    private void playRingtone() {
        try {
            if (ringtone != null && !ringtone.isPlaying()) {
                ringtone.play();
            }
        } catch (Exception ignored) {}
    }

    private final Runnable timerTick = new Runnable() {
        @Override public void run() {
            if (!inCall) return;
            seconds++;
            int m = seconds / 60;
            int s = seconds % 60;
            tvStatus.setText(String.format("%02d:%02d", m, s));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        stopRingtone();
        super.onDestroy();
    }
}


