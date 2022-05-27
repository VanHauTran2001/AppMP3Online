package com.example.appmp3online.Activity;
import static com.example.appmp3online.Activity.MyApplication.CHANEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.appmp3online.Model.BaiHat;
import com.example.appmp3online.R;

import java.util.ArrayList;

public class MyBackgroundService extends Service {
    private BaiHat baiHat;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("dl","On create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        baiHat = intent.getParcelableExtra("playnhac");
        sendNotification(baiHat);
        return START_NOT_STICKY;
    }

    private void sendNotification(BaiHat song) {
        Intent intent = new Intent(this,PlayNhacActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.txtTenBaiHatNotification,song.getNameSong());
        remoteViews.setTextViewText(R.id.txtTemCaSiBaiHatNotification,song.getNameSinger());
        remoteViews.setImageViewResource(R.id.notificationPlay,R.drawable.ic_baseline_pause_circle_outline_24);
        remoteViews.setImageViewResource(R.id.notificationPlay,R.drawable.ic_baseline_close_24);
        Notification notification = new NotificationCompat.Builder(this,CHANEL_ID)
                                    .setContentIntent(pendingIntent)
                                    .setCustomContentView(remoteViews)
                                    .build();
        startForeground(1,notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("dl","On destroy");
    }
}
