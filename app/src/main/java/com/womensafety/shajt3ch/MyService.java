package com.womensafety.shajt3ch;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MyService extends Service {

    public static final String TAG = "MyService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        
        // Check for microphone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Voice recognition functionality disabled for now
            Toast.makeText(this, "Voice service started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
        }
        
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Emergency help method
    public void activateHelpMode() {
        Toast.makeText(this, "Help mode activated ", Toast.LENGTH_SHORT).show();

        SQLiteDatabase db2 = this.openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);
        Log.d("Number is:", Register.getNumber(db2));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Register.getNumber(db2), null, "Please HELP me !! I am in Danger, I need your help", null, null);

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Register.getNumber(db2)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        startActivity(intent);

        Toast.makeText(this, "Help mode activated ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "Service destroyed");
    }
}