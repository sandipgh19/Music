package com.example.sandipghosh.music;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cleveroad.audiowidget.AudioWidget;

import java.io.IOException;


import static com.example.sandipghosh.music.MainActivity.mPlayLayout;
import static com.example.sandipghosh.music.MainActivity.utils;


/**
 * Created by sandipghosh on 04/03/17.
 */

public class Myservice extends Service {
    static MediaPlayer mediaPlayer1;
    static AudioWidget audioWidget;
    String sntSeekPos;
    int intSeekPos;
    int mediaPosition;
    int mediaMax;
    //Intent intent;
    private final Handler handler = new Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "com.example.sandipghosh.music.seekprogress";
    Intent seekIntent;

    String song = "http://m1509.wapka-file.com//g03/music/48385648/427184/6c6bm4pt2eQTWsrTmfaLZ4VsliNyU4QdHRjj9aqXGAQoicKUNw/d282b.mp3?md5=kuX8-R4iqJzMdjEUhXXvAg&expires=1489286784";

    //String song = "http://cyberindia.in/music/down/53512289/427184/NGZhNXJTbkUwV0s4c3hGTlFPRktucmsrQXI4U0UvOHlrdktmS0NRZFdWamNDUUhuaGc=/Asol+Nakol+-+Pranab+Ray.mp3";

    //String song = "http://programmerguru.com/android-tutorial/wp-content/uploads/2013/04/hosannatelugu.mp3";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        seekIntent = new Intent(BROADCAST_ACTION);

        mediaPlayer1 = new MediaPlayer();
        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer1.setDataSource(song);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer1.prepare();
            //mediaPlayer1.prepareAsync();
           // songProgressBar.setProgress(0);
           // songProgressBar.setMax(100);
            setupHandler();
            //songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(mediaPlayer1.getDuration()));
          //  updateProgressBar();
          /*  long totalDuration = mediaPlayer1.getDuration();
            long currentDuration = mediaPlayer1.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));*/


        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer1.start();
        mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audioWidget.controller().pause();
               // mpv.stop();
                mPlayLayout.startDismissAnimation();
            }
        });

      /*  songProgressBar.setProgress(0);
        songProgressBar.setMax(100);
        updateProgressBar();*/


        audioWidget = new AudioWidget.Builder(this).build();
        audioWidget.controller().start();

        audioWidget.controller().onControlsClickListener(new AudioWidget.OnControlsClickListener() {
            @Override
            public boolean onPlaylistClicked() {
                // playlist icon clicked
                // return false to collapse widget, true to stay in expanded state

                return true;
            }

            @Override
            public void onPreviousClicked() {
                // previous track button clicked
            }

            @Override
            public boolean onPlayPauseClicked() {
                // return true to change playback state of widget and play button click animation (in collapsed state)

                if (mediaPlayer1.isPlaying()) {
                    mediaPlayer1.pause();
                  //  mpv.stop();
                    mPlayLayout.startDismissAnimation();
                    audioWidget.controller().pause();
                } else {
                    audioWidget.controller().start();
                    mediaPlayer1.start();
                    mPlayLayout.startRevealAnimation();
                  //  mpv.start();
                }
                return true;
            }

            @Override
            public void onNextClicked() {
                // next track button clicked
            }

            @Override
            public void onAlbumClicked() {
                // album cover clicked
            }

            @Override
            public void onPlaylistLongClicked() {
                // playlist button long clicked
            }

            @Override
            public void onPreviousLongClicked() {
                // previous track button long clicked
            }

            @Override
            public void onPlayPauseLongClicked() {
                // play/pause button long clicked
            }

            @Override
            public void onNextLongClicked() {
                // next track button long clicked
            }

            @Override
            public void onAlbumLongClicked() {
                // album cover long clicked
            }
        });

// widget's state listener
        audioWidget.controller().onWidgetStateChangedListener(new AudioWidget.OnWidgetStateChangedListener() {
            @Override
            public void onWidgetStateChanged(@NonNull AudioWidget.State state) {
                // widget state changed (COLLAPSED, EXPANDED, REMOVED)
            }

            @Override
            public void onWidgetPositionChanged(int cx, int cy) {
                // widget position change. Save coordinates here to reuse them next time AudioWidget.show(int, int) called.
            }
        });
        audioWidget.hide();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // ---Set up receiver for seekbar change ---
        registerReceiver(broadcastReceiver, new IntentFilter(
                MainActivity.BROADCAST_SEEKBAR));

        setupHandler();
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        audioWidget.controller().onControlsClickListener(null);
        audioWidget.controller().onWidgetStateChangedListener(null);
        audioWidget.hide();
        audioWidget = null;
        if (mediaPlayer1.isPlaying()) {
            mediaPlayer1.stop();
        }
        stopSelf();
        mediaPlayer1.reset();
        mediaPlayer1.release();
        mediaPlayer1 = null;
        super.onDestroy();
    }


    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            // // Log.d(TAG, "entered sendUpdatesToUI");

            LogMediaPosition();

            handler.postDelayed(this, 1000); // 2 seconds

        }
    };

    private void LogMediaPosition() {
        // // Log.d(TAG, "entered LogMediaPosition");
        if (mediaPlayer1.isPlaying()) {
            mediaPosition = mediaPlayer1.getCurrentPosition();
            // if (mediaPosition < 1) {
            // Toast.makeText(this, "Buffering...", Toast.LENGTH_SHORT).show();
            // }
            mediaMax = mediaPlayer1.getDuration();
            //seekIntent.putExtra("time", new Date().toLocaleString());
            seekIntent.putExtra("counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
            seekIntent.putExtra("song_ended", String.valueOf(songEnded));
            sendBroadcast(seekIntent);
        }
    }

    // --Receive seekbar position if it has been changed by the user in the
    // activity
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }
    };

    // Update seek position from Activity
    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra("seekpos", 0);
        if (mediaPlayer1.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
            mediaPlayer1.seekTo(seekPos);
            setupHandler();
        }
    }

}
