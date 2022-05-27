package com.example.appmp3online.Activity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.SeekBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.example.appmp3online.Model.BaiHat;
import com.example.appmp3online.R;
import com.example.appmp3online.databinding.ActivityPlayNhacBinding;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class PlayNhacActivity extends AppCompatActivity{
    private ActivityPlayNhacBinding binding;
    private final ArrayList<BaiHat> baiHatArrayList = new ArrayList<>();
    private String title ="";
    private static MediaPlayer mediaPlayer;
    private int position =-1;
    private boolean repeat = false;
    private boolean checkrandum = false;
    private boolean next = false;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_play_nhac);
        //Kiem tra tin hieu mang
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getDataIntent();
        customToolbar();
        evenClick();

    }

    private void evenClick() {
        binding.imgPlay.setOnClickListener(view -> {
            if (mediaPlayer !=null){
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    binding.imgPlay.setImageResource(R.drawable.iconplay);
                }else {
                    mediaPlayer.start();
                    binding.imgPlay.setImageResource(R.drawable.ic_pause);
                }
            }
        });
        binding.imgRepeat.setOnClickListener(v -> {
            if (!repeat){
                if (checkrandum){
                    checkrandum = false;
                    binding.imgRepeat.setImageResource(R.drawable.iconsyned);
                    binding.imgSuffe.setImageResource(R.drawable.iconsuffle);
                }
                binding.imgRepeat.setImageResource(R.drawable.iconsyned);
                repeat = true;
            }else {
                binding.imgRepeat.setImageResource(R.drawable.iconrepeat);
                repeat = false;
            }
        });
        binding.imgSuffe.setOnClickListener(v -> {
            if (!checkrandum){
                if (repeat){
                    repeat = false;
                    binding.imgSuffe.setImageResource(R.drawable.iconshuffled);
                    binding.imgRepeat.setImageResource(R.drawable.iconrepeat);
                }
                binding.imgSuffe.setImageResource(R.drawable.iconshuffled);
                checkrandum  = true;
            }else {
                binding.imgSuffe.setImageResource(R.drawable.iconsuffle);
                checkrandum = false;
            }
        });
        binding.seekBarPlaynhac.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(binding.seekBarPlaynhac.getProgress());
            }
        });
        binding.imgNext.setOnClickListener(v -> {
            if (baiHatArrayList.size()>0){
                if (mediaPlayer.isPlaying() || mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if (position < (baiHatArrayList.size())){
                    binding.imgPlay.setImageResource(R.drawable.ic_pause);
                    position++;
                    if (repeat){
                        if (position == 0){
                            position = baiHatArrayList.size();
                        }
                        position -= 1;
                    }
                    if (checkrandum){
                        Random random = new Random();
                        int index = random.nextInt(baiHatArrayList.size());
                        if (index==position){
                            position = index - 1;
                        }
                        position = index;
                    }
                    if(position>(baiHatArrayList.size()-1)){
                        position=0;
                    }
                    new PlayMP3().execute(baiHatArrayList.get(position).getLinkSong());
                    Objects.requireNonNull(getSupportActionBar()).setTitle(baiHatArrayList.get(position).getNameSong());
                    updateTime();
                }

            }
            binding.imgPreview.setEnabled(false);
            binding.imgNext.setEnabled(false);
            Handler handler1 = new Handler();
            handler1.postDelayed(() -> {
                binding.imgPreview.setEnabled(true);
                binding.imgNext.setEnabled(true);
            },5000);
        });

        binding.imgPreview.setOnClickListener(v -> {
            if (baiHatArrayList.size()>0){
                if (mediaPlayer.isPlaying() || mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if (position < (baiHatArrayList.size())){
                    binding.imgPlay.setImageResource(R.drawable.ic_pause);
                    position--;
                    if (position <0) {
                        position = baiHatArrayList.size() - 1;
                    }
                    if (repeat){
                        position += 1;
                    }
                    if (checkrandum){
                        Random random = new Random();
                        int index = random.nextInt(baiHatArrayList.size());
                        if (index==position){
                            position = index - 1;
                        }
                        position = index;
                    }
                    new PlayMP3().execute(baiHatArrayList.get(position).getLinkSong());
                    Objects.requireNonNull(getSupportActionBar()).setTitle(baiHatArrayList.get(position).getNameSong());
                    updateTime();
                }
            }
            binding.imgPreview.setEnabled(false);
            binding.imgNext.setEnabled(false);
            Handler handler1 = new Handler();
            handler1.postDelayed(() -> {
                binding.imgPreview.setEnabled(true);
                binding.imgNext.setEnabled(true);
            },5000);
        });
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        baiHatArrayList.clear();
        if (intent !=null){
            if (intent.hasExtra("playnhac")){
                BaiHat baiHat = intent.getParcelableExtra("playnhac");
                binding.txtTenBaiHatPlay.setText(baiHat.getNameSong());
                binding.txtCaSiPlay.setText(baiHat.getNameSinger());
                title = baiHat.getNameSong();
                baiHatArrayList.add(baiHat);
            }
        }
        if(baiHatArrayList.size()>0){
            new PlayMP3().execute(baiHatArrayList.get(0).getLinkSong());
            binding.imgPlay.setImageResource(R.drawable.ic_pause);
        }
    }

    private void customToolbar() {
        setSupportActionBar(binding.toolbarPlayNhac);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbarPlayNhac.setTitle(title);
        binding.toolbarPlayNhac.setTitleTextColor(getResources().getColor(R.color.white));
        binding.toolbarPlayNhac.setNavigationOnClickListener(v -> {
            mediaPlayer.stop();
            baiHatArrayList.clear();
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
            play(baiHat);
            mediaPlayer.start();
            TimeSong();
            updateTime();
        }
    }

    private static void play(String baiHat) {
        try {
            mediaPlayer = new MediaPlayer();
            //play nhạc online
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.stop();
                mediaPlayer.reset();
            });
            //khởi tạo dữ liệu bai hat
            mediaPlayer.setDataSource(baiHat);
            mediaPlayer.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void updateTime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer !=null){
                    binding.seekBarPlaynhac.setProgress(mediaPlayer.getCurrentPosition());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =new SimpleDateFormat("mm:ss");
                    binding.txtTimeStart.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                    handler.postDelayed(this,300);
                    mediaPlayer.setOnCompletionListener(mp -> {
                        next = true;
                        try {
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    });
                }
            }
        },300);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(next){
                    if(position <(baiHatArrayList.size())){
                        binding.imgPlay.setImageResource(R.drawable.ic_pause);
                        position--;
                        if(position<0){
                            position = baiHatArrayList.size()-1;
                        }
                        if(repeat){
                            position += 1;
                        }
                        if(checkrandum){
                            Random random = new Random();
                            int index = random.nextInt(baiHatArrayList.size());
                            if(index==position){
                                position = index-1;
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
                    },5000);
                    next = false;
                    handler1.removeCallbacks(this);
                }else {
                    handler1.postDelayed(this,1000);
                }
            }
        },1000);
    }

    private void TimeSong() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        //tổng thời gian bài hát
        binding.txtTimeEnd.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
        binding.seekBarPlaynhac.setMax(mediaPlayer.getDuration());
    }
}