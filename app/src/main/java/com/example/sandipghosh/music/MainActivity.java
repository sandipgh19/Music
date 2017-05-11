package com.example.sandipghosh.music;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.Manifest;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.SpeechRecognizerDbmHandler;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;
import com.cleveroad.play_widget.PlayLayout;
import com.cleveroad.play_widget.VisualizerShadowChanger;

import java.io.IOException;


import static com.example.sandipghosh.music.Myservice.audioWidget;
import static com.example.sandipghosh.music.Myservice.mediaPlayer1;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private AudioVisualization audioVisualization;
    Intent serviceIntent;
    static Utilities utils;
   // static MusicPlayerView mpv;
    static PlayLayout mPlayLayout;
    private VisualizerShadowChanger mShadowChanger;
    private VisualizerDbmHandler visualizerDbmHandler;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1;
    //static SeekBar songProgressBar;
    //static TextView songCurrentDurationLabel;
    //static TextView songTotalDurationLabel;
    private static Handler mHandler = new Handler();
    private boolean isOnline;
    private SharedPreferences preferences;
    private static final String KEY_POSITION_X = "position_x";
    private static final String KEY_POSITION_Y = "position_y";
    private static final String current = "position_x";
    private static final String max = "position_y";

    //private SeekBar seekBar;
    private int seekMax;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;
    public static final String BROADCAST_SEEKBAR = "com.example.sandipghosh.music.sendseekbar";
    private static final int MY_PERMISSIONS_REQUEST_READ_AUDIO = 11;

    String data;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
       // setContentView(R.layout.activity_main);
       // checkVisualiserPermissions();

      //  preferences = getSharedPreferences("myloginapp",Context.MODE_PRIVATE);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        mPlayLayout = (PlayLayout) findViewById(R.id.playLayout);
        //mPlayLayout.fastOpen();
        setImageForItem();
        mPlayLayout.setOnButtonsClickListener(new PlayLayout.OnButtonsClickListenerAdapter() {
            @Override
            public void onPlayButtonClicked() {

                if (mPlayLayout.isOpen()) {
                    mediaPlayer1.pause();
                    mPlayLayout.startDismissAnimation();
                    audioWidget.controller().pause();
                } else {

                    mPlayLayout.startRevealAnimation();

                    if (!isServiceRunning(Myservice.class)) {
                        serviceIntent.putExtra("sendlink", data);
                        startService(serviceIntent);
                        //  registerReceiver(broadcastReceiver, new IntentFilter(
                        //      Myservice.BROADCAST_ACTION));
                    } else {
                        mediaPlayer1.start();
                        mPlayLayout.startRevealAnimation();
                        audioWidget.controller().start();
                    }
                }
            }

            @Override
            public void onSkipPreviousClicked() {
               /* onPreviousClicked();
                if (!mPlayLayout.isOpen()) {
                    mPlayLayout.startRevealAnimation();
                }*/
            }

            @Override
            public void onSkipNextClicked() {
               /* onNextClicked();
                if (!mPlayLayout.isOpen()) {
                    mPlayLayout.startRevealAnimation();
                }*/
            }

            @Override
            public void onShuffleClicked() {
                Toast.makeText(MainActivity.this, "Stub", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRepeatClicked() {
                Toast.makeText(MainActivity.this, "Stub", Toast.LENGTH_SHORT).show();
            }
        });
        mPlayLayout.setOnProgressChangedListener(new PlayLayout.OnProgressChangedListener() {
            @Override
            public void onPreSetProgress() {
               // stopTrackingPosition();
            }

            @Override
            public void onProgressChanged(float progress) {
               /* Log.i("onProgressChanged", "Progress = " + progress);
                mediaPlayer.seekTo((int) (mediaPlayer.getDuration() * progress));
                startTrackingPosition();*/

                int seekPos = (int) (mediaPlayer1.getDuration() * progress);
                intent.putExtra("seekpos", seekPos);
                sendBroadcast(intent);
            }

        });


        audioVisualization = (AudioVisualization) findViewById(R.id.visualizer_view);
        checkVisualiserPermissions();


        SpeechRecognizerDbmHandler speechRecHandler = DbmHandler.Factory.newSpeechRecognizerHandler(this);
        speechRecHandler.innerRecognitionListener();
        audioVisualization.linkTo(speechRecHandler);

        // set audio visualization handler. This will REPLACE previously set speech recognizer handler

        visualizerDbmHandler = DbmHandler.Factory.newVisualizerHandler(this,0);
        audioVisualization.linkTo(visualizerDbmHandler);
      //  songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
      //  songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
      //  songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
      //  songProgressBar.setOnSeekBarChangeListener(this);
        registerReceiver(broadcastReceiver, new IntentFilter(
                Myservice.BROADCAST_ACTION));

        try {

            serviceIntent = new Intent(MainActivity.this, Myservice.class);
            intent = new Intent(BROADCAST_SEEKBAR);

        } catch (Exception e) {
            e.printStackTrace();
        }

       // mpv = (MusicPlayerView) findViewById(R.id.mpv);
      //  mpv.setCoverDrawable(R.drawable.mycover);
      //  mpv.setProgressVisibility(false);
        utils = new Utilities();

        if((isServiceRunning(Myservice.class)) && mediaPlayer1.isPlaying() == true) {
         //   mpv.start();
        //    mpv.isRotating();
           // mPlayLayout.fastOpen();
            mPlayLayout.startRevealAnimation();
            audioWidget.hide();
        }

     /*   mpv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (mpv.isRotating()) {
                    mediaPlayer1.pause();
                    mpv.stop();
                    audioWidget.controller().pause();

                    //stopService(serviceIntent);
                } else {

                        mpv.start();
                        if (!isServiceRunning(Myservice.class)) {
                            serviceIntent.putExtra("sendlink", data);
                            startService(serviceIntent);
                          //  registerReceiver(broadcastReceiver, new IntentFilter(
                              //      Myservice.BROADCAST_ACTION));
                        } else {
                            mediaPlayer1.start();
                            audioWidget.controller().start();
                    }
                }
            }
        });
*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        onPermissionsNotGranted();
                    }
                }
            };
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.permissions_title))
                    .setMessage(getString(R.string.draw_over_permissions_message))
                    .setPositiveButton(getString(R.string.btn_continue), listener)
                    .setNegativeButton(getString(R.string.btn_cancel), listener)
                    .setCancelable(false)
                    .show();
            return;
        }
    }

    // -- Broadcast Receiver to update position of seekbar from service --
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        String strSongEnded = serviceIntent.getStringExtra("song_ended");
        int seekProgress = Integer.parseInt(counter);

       /* SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(current, Integer.parseInt(counter));
        editor.putInt(max, Integer.parseInt(mediamax));
        editor.commit();
        int seekProgress = preferences.getInt(current, Integer.parseInt(counter));
        seekMax = preferences.getInt(max, Integer.parseInt(mediamax));*/
        seekMax = Integer.parseInt(mediamax);
        songEnded = Integer.parseInt(strSongEnded);
      //  songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(seekMax));
        // Displaying time completed playing
      //  songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(seekProgress));
       // songProgressBar.setMax(seekMax);
        mPlayLayout.setPostProgress((float) seekProgress / seekMax);
      //  mpv.setMax(seekMax);
      //  mpv.setProgress(seekProgress);
       // songProgressBar.setProgress(seekProgress);
        if (songEnded == 1) {
           // buttonPlayStop.setBackgroundResource(R.drawable.playbuttonsm);
          //  mpv.stop();
            audioWidget.controller().start();
            mPlayLayout.startDismissAnimation();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        audioVisualization.onResume();
        //mpv.getProgress();
        //audioWidget.hide();
        if (mShadowChanger != null) {
            mShadowChanger.setEnabledVisualization(true);
        }
    }

    @Override
    public void onPause() {
        audioVisualization.onPause();
        if (mShadowChanger != null) {
            mShadowChanger.setEnabledVisualization(false);
        }
        super.onPause();

        //audioWidget.show(preferences.getInt(KEY_POSITION_X, 100), preferences.getInt(KEY_POSITION_Y, 100));
        audioWidget.show(100,100);

       // mpv.getProgress();
    }

    @Override
    public void onDestroy() {
        audioVisualization.release();
        if (mShadowChanger != null) {
            mShadowChanger.release();
        }
        super.onDestroy();
    }

    private boolean isServiceRunning(@NonNull Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                onPermissionsNotGranted();
            } else {
                //checkReadStoragePermission();
            }
        }
    }

    private void onPermissionsNotGranted() {
        Toast.makeText(this, R.string.toast_permissions_not_granted, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


        if (fromUser) {
            int seekPos = seekBar.getProgress();
            intent.putExtra("seekpos", seekPos);
            sendBroadcast(intent);
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

    private void startVisualiser() {
        if(isServiceRunning(Myservice.class)) {
            if (mShadowChanger == null) {
                mShadowChanger = VisualizerShadowChanger.newInstance(mediaPlayer1.getAudioSessionId());
                mShadowChanger.setEnabledVisualization(true);
                mPlayLayout.setShadowProvider(mShadowChanger);
                // Log.i("startVisualiser", "startVisualiser " + mediaPlayer.getAudioSessionId());
            }
        }
    }

    private void checkVisualiserPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            startVisualiser();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
                AlertDialog.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            requestPermissions();
                        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                            permissionsNotGranted();
                        }
                    }
                };
                new android.app.AlertDialog.Builder(this)
                        .setTitle(getString(R.string.title_permissions))
                        .setMessage(Html.fromHtml(getString(R.string.message_permissions)))
                        .setPositiveButton(getString(R.string.btn_next), onClickListener)
                        .setNegativeButton(getString(R.string.btn_cancel), onClickListener)
                        .show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions();
                }
            }
        }
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
                MY_PERMISSIONS_REQUEST_READ_AUDIO
        );
    }

    private void permissionsNotGranted() {

    }

    private void setImageForItem() {
        Glide.with(this)
                .load(1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.white_centered_bordered_song_note_image)
                .into(imageTarget);
    }
    private SimpleTarget<GlideDrawable> imageTarget = new SimpleTarget<GlideDrawable>() {

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            mPlayLayout.setImageDrawable(resource);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            mPlayLayout.setImageDrawable(errorDrawable);
        }
    };




}
