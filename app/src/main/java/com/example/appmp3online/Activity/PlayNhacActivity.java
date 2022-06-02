package com.example.appmp3online.Activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.appmp3online.Model.BaiHat;
import com.example.appmp3online.R;
import com.example.appmp3online.Service.CreateNotification;
import com.example.appmp3online.Service.OnClearFormRecentService;
import com.example.appmp3online.databinding.ActivityPlayNhacBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;

public class PlayNhacActivity extends AppCompatActivity{
    private ActivityPlayNhacBinding binding;
    private final ArrayList<BaiHat> baiHatArrayList = new ArrayList<>();
    private String title ="";
    private int position = 0;
    private boolean repeat = false;
    private boolean checkrandum = false;
    private boolean next = false;
    private NotificationManager notificationManager;
    private boolean isRunning = false;
    //Service
    private MyBackgroupService myService;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_play_nhac);
        isRunning = true;
        connectService();
        startAsyn();
        //Kiem tra tin hieu mang
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        getDataIntent();
        customToolbar();
        evenClick();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            startService(new Intent(getApplicationContext(), OnClearFormRecentService.class));
        }

    }

    private void connectService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MyBackgroupService.MyLocalBinder binder = (MyBackgroupService.MyLocalBinder) iBinder;
                myService = binder.getService();
                myService.setPlaying(true);
                updateInfo();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        Intent intent = new Intent();
        intent.setClassName(this, MyBackgroupService.class.getName());
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void updateInfo() {
        if (myService == null) {
            return;
        }
        BaiHat currentSong = myService.getCurrentItem();
        if (currentSong == null) {
            return;
        }
        binding.txtTenBaiHatPlay.setText(currentSong.getNameSong());
        binding.txtCaSiPlay.setText(currentSong.getNameSinger());
        if (myService.isPlaying()) {
            Glide.with(binding.imgPlay)
                    .load(R.drawable.ic_pause)
                    .into(binding.imgPlay);
        } else {
            Glide.with(binding.imgPlay)
                    .load(R.drawable.iconplay)
                    .into(binding.imgPlay);
        }
    }

    public void startAsyn(){
        AsyncTask<Void , Integer , Void> asyncTask = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (isRunning){
                    SystemClock.sleep(300);
                    if(myService==null || !myService.isPrepared()){
                        continue;
                    }
                    MediaPlayer mp = myService.getMediaPlayer();
                    publishProgress(mp.getDuration(),mp.getCurrentPosition());
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                binding.seekBarPlaynhac.setProgress(values[1]* 100 / values[0]);
                binding.txtTimeEnd.setText(new SimpleDateFormat("mm:ss").format(values[0]));
                binding.txtTimeStart.setText(new SimpleDateFormat("mm:ss").format(values[1]));
            }
        };

        asyncTask.executeOnExecutor(Executors.newFixedThreadPool(1));
    }
    private void createChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,"Zing MP3", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager !=null){
                 notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void evenClick() {
        binding.imgRepeat.setOnClickListener(v -> {
//            if (!repeat){
//                if (checkrandum){
//                    checkrandum = false;
//                    binding.imgRepeat.setImageResource(R.drawable.iconsyned);
//                    binding.imgSuffe.setImageResource(R.drawable.iconsuffle);
//                }
//                binding.imgRepeat.setImageResource(R.drawable.iconsyned);
//                repeat = true;
//            }else {
//                binding.imgRepeat.setImageResource(R.drawable.iconrepeat);
//                repeat = false;
//            }
            myService.repeat();
            updateInfo();
        });
        binding.imgSuffe.setOnClickListener(v -> {
//            if (!checkrandum){
//                if (repeat){
//                    repeat = false;
//                    binding.imgSuffe.setImageResource(R.drawable.iconshuffled);
//                    binding.imgRepeat.setImageResource(R.drawable.iconrepeat);
//                }
//                binding.imgSuffe.setImageResource(R.drawable.iconshuffled);
//                checkrandum  = true;
//            }else {
//                binding.imgSuffe.setImageResource(R.drawable.iconsuffle);
//                checkrandum = false;
//            }
            myService.randum();
            updateInfo();
        });
        binding.seekBarPlaynhac.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    myService.getMediaPlayer().seekTo(myService.getMediaPlayer().getDuration()/100*progress);
                    binding.seekBarPlaynhac.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        binding.imgPlay.setOnClickListener(view -> {
//            if (mediaPlayer !=null){
//                if (mediaPlayer.isPlaying()){
//                    mediaPlayer.pause();
//                    binding.imgPlay.setImageResource(R.drawable.iconplay);
//                }else {
//                    mediaPlayer.start();
//                    binding.imgPlay.setImageResource(R.drawable.ic_pause);
//                    //Create Notification
//                    CreateNotification.createNotification(PlayNhacActivity.this,baiHatArrayList.get(position),R.drawable.ic_pause,1,baiHatArrayList.size()-1);
//                }
//            }
            myService.pause();
            updateInfo();
        });
        binding.imgPreview.setOnClickListener(v -> {
//            if (baiHatArrayList.size()>0){
//                if (mediaPlayer.isPlaying() || mediaPlayer != null){
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                    mediaPlayer = null;
//                }
//                if (position < (baiHatArrayList.size())){
//                    binding.imgPlay.setImageResource(R.drawable.ic_pause);
//                    position--;
//                    if (position <0) {
//                        position = baiHatArrayList.size() - 1;
//                    }
//                    if (repeat){
//                        position += 1;
//                    }
//                    if (checkrandum){
//                        Random random = new Random();
//                        int index = random.nextInt(baiHatArrayList.size());
//                        if (index==position){
//                            position = index - 1;
//                        }
//                        position = index;
//                    }
//                    new PlayMP3().execute(baiHatArrayList.get(position).getLinkSong());
//                    Objects.requireNonNull(getSupportActionBar()).setTitle(baiHatArrayList.get(position).getNameSong());
//                    updateTime();
//                }
//            }
//            binding.imgPreview.setEnabled(false);
//            binding.imgNext.setEnabled(false);
//            Handler handler1 = new Handler();
//            handler1.postDelayed(() -> {
//                binding.imgPreview.setEnabled(true);
//                binding.imgNext.setEnabled(true);
//            },5000);
//            CreateNotification.createNotification(PlayNhacActivity.this,baiHatArrayList.get(position),R.drawable.ic_pause,position,baiHatArrayList.size()-1);
            Glide.with(binding.imgPlay).load(R.drawable.iconplay).into(binding.imgPlay);
            myService.previous();
            updateInfo();
        });
        binding.imgNext.setOnClickListener(v -> {
//            if (baiHatArrayList.size()>0){
//                if (mediaPlayer.isPlaying() || mediaPlayer != null){
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                    mediaPlayer = null;
//                }
//                if (position < (baiHatArrayList.size())){
//                    binding.imgPlay.setImageResource(R.drawable.ic_pause);
//                    position++;
//                    if (repeat){
//                        if (position == 0){
//                            position = baiHatArrayList.size();
//                        }
//                        position -= 1;
//                    }
//                    if (checkrandum){
//                        Random random = new Random();
//                        int index = random.nextInt(baiHatArrayList.size());
//                        if (index==position){
//                            position = index - 1;
//                        }
//                        position = index;
//                    }
//                    if(position>(baiHatArrayList.size()-1)){
//                        position=0;
//                    }
//                    new PlayMP3().execute(baiHatArrayList.get(position).getLinkSong());
//                    Objects.requireNonNull(getSupportActionBar()).setTitle(baiHatArrayList.get(position).getNameSong());
//                    updateTime();
//                }
//
//            }
//            binding.imgPreview.setEnabled(false);
//            binding.imgNext.setEnabled(false);
//            Handler handler1 = new Handler();
//            handler1.postDelayed(() -> {
//                binding.imgPreview.setEnabled(true);
//                binding.imgNext.setEnabled(true);
//            },5000);
//            CreateNotification.createNotification(PlayNhacActivity.this,baiHatArrayList.get(position),R.drawable.ic_pause,position,baiHatArrayList.size()-1);
            Glide.with(binding.imgPlay).load(R.drawable.iconplay).into(binding.imgPlay);
            myService.next();
            updateInfo();
        });
    }

//    private void getDataIntent() {
//        Intent intent = getIntent();
//        baiHatArrayList.clear();
//        if (intent !=null){
//            if (intent.hasExtra("playnhac")){
//                 baiHat = intent.getParcelableExtra("playnhac");
//                binding.txtTenBaiHatPlay.setText(baiHat.getNameSong());
//                binding.txtCaSiPlay.setText(baiHat.getNameSinger());
//                title = baiHat.getNameSong();
//                baiHatArrayList.add(baiHat);
//            }
//        }
//        if(baiHatArrayList.size()>0){
//            new PlayMP3().execute(baiHatArrayList.get(0).getLinkSong());
//            binding.imgPlay.setImageResource(R.drawable.ic_pause);
//        }
//    }

    private void customToolbar() {

        setSupportActionBar(binding.toolbarPlayNhac);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbarPlayNhac.setTitle(title);
        binding.toolbarPlayNhac.setTitleTextColor(getResources().getColor(R.color.white));
        binding.toolbarPlayNhac.setNavigationOnClickListener(v -> {
            myService.getMediaPlayer().stop();
            finish();

        });
    }

    @SuppressLint("StaticFieldLeak")
    private class PlayMP3 extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }

        @Override
        protected void onPostExecute(String baiHat) {
            super.onPostExecute(baiHat);
            myService.play(myService.getCurrentPosition());
            myService.getMediaPlayer().start();

        }
    }

//    private static void play(String baiHat) {
//        try {
//            mediaPlayer = new MediaPlayer();
//            //play nhạc online
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setOnCompletionListener(mp -> {
//                mediaPlayer.stop();
//                mediaPlayer.reset();
//            });
//            //khởi tạo dữ liệu bai hat
//            mediaPlayer.setDataSource(baiHat);
//            mediaPlayer.prepare();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    private void updateTime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myService.getMediaPlayer() != null) {
                    binding.seekBarPlaynhac.setProgress(myService.getMediaPlayer().getCurrentPosition());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                    binding.txtTimeStart.setText(simpleDateFormat.format(myService.getMediaPlayer().getCurrentPosition()));
                    handler.postDelayed(this, 300);
                    myService.getMediaPlayer().setOnCompletionListener(mp -> {
                        next = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }, 300);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (next) {
                    if (position < (baiHatArrayList.size())) {
                        binding.imgPlay.setImageResource(R.drawable.ic_pause);
                        position--;
                        if (position < 0) {
                            position = baiHatArrayList.size() - 1;
                        }
                        if (repeat) {
                            position += 1;
                        }
                        if (checkrandum) {
                            Random random = new Random();
                            int index = random.nextInt(baiHatArrayList.size());
                            if (index == position) {
                                position = index - 1;
                            }
                            position = index;
                        }
                        new PlayMP3().execute(baiHatArrayList.get(position).getLinkSong());
                        updateTime();
                    }
                    binding.imgPreview.setEnabled(false);
                    binding.imgNext.setEnabled(false);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(() -> {
                        binding.imgPreview.setEnabled(true);
                        binding.imgNext.setEnabled(true);
                    }, 5000);
                    next = false;
                    handler1.removeCallbacks(this);
                } else {
                    handler1.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void TimeSong() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        //tổng thời gian bài hát
        binding.txtTimeEnd.setText(simpleDateFormat.format(myService.getMediaPlayer().getDuration()));
        binding.seekBarPlaynhac.setMax(myService.getMediaPlayer().getDuration());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }
    }
}