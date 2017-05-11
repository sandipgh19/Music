package com.example.sandipghosh.music;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by sandipghosh on 04/03/17.
 */

public class MusicMedia {

    MediaPlayer mediaPlayer;

    public void start() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();

        try {
            mediaPlayer.setDataSource("");
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {

        } catch (IllegalStateException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        }
        mediaPlayer.start();
    }


}
