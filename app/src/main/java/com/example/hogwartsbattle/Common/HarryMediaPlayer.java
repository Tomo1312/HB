package com.example.hogwartsbattle.Common;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.hogwartsbattle.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HarryMediaPlayer extends MediaPlayer {
    Context context;
    ArrayList<Integer> playList;
    MediaPlayer mediaPlayer;
    int playingSong;
    Timer mpTimer;

    public HarryMediaPlayer(Context context) {
        this.context = context;
        this.playList = new ArrayList<>();
        this.playList.add(R.raw.christmasathogwarts);
        this.playList.add(R.raw.entryintothegreathallandthebanquet);
        this.playList.add(R.raw.hagridtheprofessor);
        this.playList.add(R.raw.harryswondrousworld);
        this.playList.add(R.raw.prologue);
        this.playList.add(R.raw.visittothezooandletterfromhogwarts);
        this.playingSong = (int) (Math.random() * 5 + 0);

    }

    public void startPlaying() {
        this.mediaPlayer = MediaPlayer.create(context, playList.get(playingSong));
        mediaPlayer.start();
        mpTimer = new Timer();
        if (playList.size() > 1) playNext();
    }

    private void playNext() {
        mpTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mediaPlayer.reset();
                while (true) {
                    int random = (int) (Math.random() * 5 + 0);
                    if (playingSong != random) {
                        playingSong = random;
                        break;
                    }
                }
                mediaPlayer = MediaPlayer.create(context, playList.get(playingSong));
                mediaPlayer.start();
                if (playList.size() > 1) playNext();
            }
        }, mediaPlayer.getDuration() + 100);
    }
    public void stopMediaPlayer(){
        mpTimer.cancel();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

}
