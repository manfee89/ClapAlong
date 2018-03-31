package com.example.manfee.clapalong;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

public class PlaySongActivity extends AppCompatActivity {


    private static final String TAG = "CLAP";
    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    MediaPlayer mMediaPlayer;

    Runnable claphandler;
    TextView clap;
    PercussionOnsetDetector mPercussionDetector;
     Handler handler ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        requestAudioPermissions();

        clap = findViewById(R.id.clap);
    handler= new Handler();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.twinkle);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        double threshold = 1;
        double sensitivity = 25;
        claphandler = new Runnable() {
            @Override
            public void run() {
                Log.d("here","here");
                //Toast.makeText(PlaySongActivity.this, "Clap!", Toast.LENGTH_SHORT).show();
                clap.setText(clap.getText()+ "\nCLAP!" );
            }
        };

         mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {


                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d(TAG, "Clap detected!");

                        runOnUiThread(claphandler);
                    }
                }, sensitivity, threshold);

        mDispatcher.addAudioProcessor(mPercussionDetector);
        new Thread(mDispatcher, "Audio  Dispatcher").start();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            if (isFinishing()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }
    }

    //Requesting run-time permissions
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            onStart();
        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    onStart();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("hi","destroyed");
        mPercussionDetector.processingFinished();
        handler.removeCallbacks(claphandler);
    }
}
