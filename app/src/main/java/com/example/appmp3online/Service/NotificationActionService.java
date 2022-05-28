package com.example.appmp3online.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.appmp3online.Activity.PlayNhacActivity;
import com.example.appmp3online.Model.BaiHat;
import com.example.appmp3online.R;

public class NotificationActionService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
         context.sendBroadcast(new Intent("TRACKS_TRACKS").putExtra("actionname", intent.getAction()));
    }
}
