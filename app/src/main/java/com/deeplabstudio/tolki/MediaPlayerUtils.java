package com.deeplabstudio.tolki;

import android.annotation.SuppressLint;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaPlayerUtils {
    private static MediaPlayer mediaPlayer;
    private static MediaPlayerUtils.Listener listener;
    private static Handler mHandler;
    private static SeekBar mSeekBar;
    private static ImageView mPlayPause;

    public static void getInstance() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if(mHandler == null) {
            mHandler = new Handler();
        }
    }

    public static void releaseMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            MediaPlayerUtils.mPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            MediaPlayerUtils.mSeekBar.setProgress(0);
        }
    }

    public static void pauseMediaPlayer() {
        mediaPlayer.pause();
    }

    public static void playMediaPlayer() {
        mediaPlayer.start();
        mHandler.postDelayed(mRunnable, 100);
    }

    public static void applySeekBarValue(int selectedValue) {
        mediaPlayer.seekTo(selectedValue);
        mHandler.postDelayed(mRunnable, 100);
    }

    public static void startAndPlayMediaPlayer(String audioUrl, SeekBar seekBar, ImageView button) throws IOException {
        MediaPlayerUtils.mSeekBar = seekBar;
        MediaPlayerUtils.mPlayPause = button;
        getInstance();
        if(isPlaying()) {
            pauseMediaPlayer();
        }
        releaseMediaPlayer();
        getInstance();
        mediaPlayer.setDataSource(audioUrl);
        mediaPlayer.prepare();
        mediaPlayer.setOnCompletionListener(onCompletionListener);

        MediaPlayerUtils.mSeekBar.setMax(mediaPlayer.getDuration());

        mHandler.postDelayed(mRunnable, 100);
        playMediaPlayer();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void startAndPlayMediaPlayer(String audioUrl, float pitch, SeekBar seekBar, ImageView button) throws IOException {
        MediaPlayerUtils.mSeekBar = seekBar;
        MediaPlayerUtils.mPlayPause = button;
        getInstance();
        if(isPlaying()) {
            pauseMediaPlayer();
        }
        releaseMediaPlayer();
        getInstance();
        mediaPlayer.setDataSource(audioUrl);
        mediaPlayer.prepare();
        mediaPlayer.setOnCompletionListener(onCompletionListener);

        PlaybackParams params = new PlaybackParams();
        params.setPitch(pitch);
        mediaPlayer.setPlaybackParams(params);

        MediaPlayerUtils.mSeekBar.setMax(mediaPlayer.getDuration());

        mHandler.postDelayed(mRunnable, 100);
        playMediaPlayer();
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static int getTotalDuration() {
        return mediaPlayer.getDuration();
    }

    private static MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            MediaPlayerUtils.releaseMediaPlayer();
            MediaPlayerUtils.mPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    };

    private static Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (isPlaying()) {
                    mHandler.postDelayed(mRunnable, 100);
                    mSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    interface Listener {
        void onAudioComplete();
        void onAudioUpdate(int currentPosition);
    }

}
