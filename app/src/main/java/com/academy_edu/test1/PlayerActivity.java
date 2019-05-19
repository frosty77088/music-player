package com.academy_edu.test1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btn_next,btn_previous,btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;
    TextView remainingTimeLabel;
    TextView elapsedTimeLabel;
    WebView myWB;

    static MediaPlayer myMediaPlayer;
    int position;

    String sname;

    ArrayList<File> mySongs;
    Thread updateseekBar;

    ImageView joom;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        joom = findViewById(R.id.joom);
        initJoom();

        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);

        btn_next= findViewById(R.id.next);
        btn_previous= findViewById(R.id.previous);
        btn_pause= findViewById(R.id.pause);

        songTextLabel = findViewById(R.id.songLabel);

        songSeekbar = findViewById(R.id.seekBar);

        myWB = findViewById(R.id.WarnerBros);



        getSupportActionBar().hide();

        if (myMediaPlayer != null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }


        updateseekBar = new Thread (new Runnable(){
            public void run () {
                int runtime = myMediaPlayer.getDuration();
                int currentPosition = 0;
                int adv = 0;
                while ((adv = ((adv = runtime - currentPosition) < 500) ? adv : 500) > 2) {
                    try {
                        Message msg = new Message();
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        msg.what = currentPosition;
                        handler.sendMessage(msg);
                        Thread.sleep(adv);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        break;
                    }
                }
            }
        });


        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        sname = bundle.getString("songname");

        songTextLabel.setText(sname);
        songTextLabel.setSelected(true);


        position = bundle.getInt("pos",0);

        final Uri u = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onBackPressed();
            }
        });
        myMediaPlayer.start();
        songSeekbar.setMax(myMediaPlayer.getDuration());

        updateseekBar.start();

        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    myMediaPlayer.seekTo(progress);
                    songSeekbar.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        joom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                myWB.setVisibility(View.VISIBLE);
                myWB.getSettings().setBuiltInZoomControls(true);
            }
        });


        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                songSeekbar.setMax(myMediaPlayer.getDuration());

                if(myMediaPlayer.isPlaying()){
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    myMediaPlayer.pause();
                }
                else
                {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    myMediaPlayer.start();
                }

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myMediaPlayer.stop();
                myMediaPlayer.release();

                position = ((position+1)%mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName();
                songTextLabel.setText(sname);

                btn_pause.setBackgroundResource(R.drawable.icon_pause);
                myMediaPlayer.start();
                try {
                    if (!updateseekBar.isAlive())
                        updateseekBar.start();
                } catch (IllegalThreadStateException e){
                    Toast.makeText(getApplicationContext(), "Если вы хотите долистать далеко, не проще ли вернутья к меню?", Toast.LENGTH_SHORT).show();
                    myMediaPlayer.stop();
                    finish();
                }
            }
        });


        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myMediaPlayer.stop();
                myMediaPlayer.release();

                position = ((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                sname = mySongs.get(position).getName();
                songTextLabel.setText(sname);
                songTextLabel.setText(sname);

                btn_pause.setBackgroundResource(R.drawable.icon_pause);
                myMediaPlayer.start();
                try {
                    if (!updateseekBar.isAlive())
                        updateseekBar.start();
                } catch (IllegalThreadStateException e){
                    Toast.makeText(getApplicationContext(), "Если вы хотите долистать далеко, не проще ли вернутья к меню?", Toast.LENGTH_SHORT).show();
                    myMediaPlayer.stop();
                    finish();
                }
            }
        });



    }

    private void initJoom(){
        Point size = getDisplaySize();
        int width = size.x;
        joom.setMaxHeight(width/3);
        joom.setMaxWidth(width/3);
    }

    private Point getDisplaySize(){
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            songSeekbar.setProgress(currentPosition);

            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(myMediaPlayer.getDuration()-currentPosition);
            remainingTimeLabel.setText("" + remainingTime);

        }
    };

    public String createTimeLabel(int time) {
        String timeLabel= "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }



        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();

        myMediaPlayer.stop();
    }



}

