package com.example.sandipghosh.music;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cleveroad.play_widget.PlayLayout;
import com.cleveroad.play_widget.VisualizerShadowChanger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.sandipghosh.music.Myservice.audioWidget;
import static com.example.sandipghosh.music.Myservice.mediaPlayer1;

/**
 * Created by sandipghosh on 11/03/17.
 */

public class PlayerActivity extends AppCompatActivity {

    private PlayLayout mPlayLayout;
    private VisualizerShadowChanger mShadowChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.hide();
        }
        setContentView(R.layout.player);
        mPlayLayout = (PlayLayout) findViewById(R.id.revealView);
        setImageForItem();
        mPlayLayout.setOnButtonsClickListener(new PlayLayout.OnButtonsClickListenerAdapter() {
            @Override
            public void onPlayButtonClicked() {
               // playButtonClicked();
               /* if (!isServiceRunning(Myservice.class)) {
                    serviceIntent.putExtra("sendlink", data);
                    startService(serviceIntent);
                    //  registerReceiver(broadcastReceiver, new IntentFilter(
                    //      Myservice.BROADCAST_ACTION));
                } else {
                    mediaPlayer1.start();
                    audioWidget.controller().start();
                }*/
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
               // Toast.makeText(MainActivity.this, "Stub", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRepeatClicked() {
               // Toast.makeText(MainActivity.this, "Stub", Toast.LENGTH_SHORT).show();
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
            }

        });
        checkVisualiserPermissions();

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

    private void startVisualiser() {
        if (mShadowChanger == null) {
            mShadowChanger = VisualizerShadowChanger.newInstance(1);
            mShadowChanger.setEnabledVisualization(true);
            mPlayLayout.setShadowProvider(mShadowChanger);
           // Log.i("startVisualiser", "startVisualiser " + mediaPlayer.getAudioSessionId());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11) {
            boolean bothGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.RECORD_AUDIO.equals(permissions[i]) || Manifest.permission.MODIFY_AUDIO_SETTINGS.equals(permissions[i])) {
                    bothGranted &= grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }
            if (bothGranted) {
                startVisualiser();
            } else {
                //permissionsNotGranted();
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
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.title_permissions))
                        .setMessage(Html.fromHtml(getString(R.string.message_permissions)))
                        .setPositiveButton(getString(R.string.btn_next), onClickListener)
                        .setNegativeButton(getString(R.string.btn_cancel), onClickListener)
                        .show();
            } else {
                requestPermissions();
            }
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
               11
        );
    }

    private void permissionsNotGranted() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mShadowChanger != null) {
            mShadowChanger.setEnabledVisualization(true);
        }
    }

    @Override
    protected void onPause() {
        if (mShadowChanger != null) {
            mShadowChanger.setEnabledVisualization(false);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mShadowChanger != null) {
            mShadowChanger.release();
        }
        super.onDestroy();
    }

    private void setImageForItem() {
        Glide.with(this)
                .load(1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.white_centered_bordered_song_note_image)
                .into(imageTarget);
    }
}
