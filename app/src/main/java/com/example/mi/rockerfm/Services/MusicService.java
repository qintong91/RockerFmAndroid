package com.example.mi.rockerfm.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.mi.rockerfm.JsonBeans.SongDetial;
import com.example.mi.rockerfm.Model.MusicProvider;
import com.example.mi.rockerfm.R;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    public static final String OBJ_SONG = "ObjSong";
    public static final String SESSION_TAG = "mmFM";
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_FAST_FORWARD = "fastForward";
    public static final String ACTION_REWIND = "rewind";

    public static final String PARAM_TRACK_URI = "uri";

    private MediaSessionCompat mMediaSession;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private PlaybackStateCompat mPlaybackState;
    private MediaControllerCompat mMediaController;
    private MusicProvider mMusicProvider;
    private MediaSessionCompat.Callback mMediaSessionCallback;

    public class MusicServiceBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
        public MediaSessionCompat.Token getToken() {return mMediaSession.getSessionToken();}
    }

    private Binder mBinder = new MusicServiceBinder();



    public MusicService() {
    }

    public MediaSessionCompat.Token getMediaSessionToken() {
        return mMediaSession.getSessionToken();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                .build();
        mMediaSession.setPlaybackState(mPlaybackState);
        //updateNotification();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build();
        mMediaSession.setPlaybackState(mPlaybackState);
        mMediaPlayer.reset();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicProvider = new MusicProvider();
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build();

        // 1) set up media session and media session callback
        mMediaSession = new MediaSessionCompat(this, SESSION_TAG);
        mMediaSessionCallback = new MusicMediaSessionCallback();
        mMediaSession.setCallback(mMediaSessionCallback);
        mMediaSession.setQueue(mMusicProvider.getQueue());
        mMediaSession.setActive(true);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setPlaybackState(mPlaybackState);
        // 2) get instance to AudioManager
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 3) create our media player
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);

        // 4) create the media controller
        try {
            mMediaController = new MediaControllerCompat(this, mMediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mMediaSession.release();
    }


    private NotificationCompat.Action createAction(int iconResId, String title, String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder(iconResId, title, pendingIntent).build();
    }

    private void updateNotification() {

        NotificationCompat.Action playPauseAction = mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING ?
                createAction(R.drawable.ic_pause_circle_outline_black_36dp, "Pause", ACTION_PAUSE) :
                createAction(R.drawable.ic_play_circle_outline_black_36dp, "Play", ACTION_PLAY);
         Notification notification = new NotificationCompat.Builder(this)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setContentTitle("Cubs The Favorites?: 10/14/15")
                .setContentText("ESPN: PTI")
                .setOngoing(mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING)
                .setShowWhen(false)
                .setAutoCancel(false)
                .addAction(playPauseAction)
                .addAction(createAction(R.drawable.ic_skip_next_black_36dp, "Fast Forward", ACTION_FAST_FORWARD))
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(1, 2))
                .build();
         ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, notification);
    }

    public class MusicMediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            Log.d("MusicService","onPlayFromUri");
            try {
                SongDetial.Song song = (SongDetial.Song)extras.getSerializable(OBJ_SONG);
                mMediaPlayer.setDataSource(MusicService.this, Uri.parse(song.getmp3Url()));
                switch (mPlaybackState.getState()) {
                    case PlaybackStateCompat.STATE_PLAYING:
                    case PlaybackStateCompat.STATE_PAUSED:
                        mMediaPlayer.reset();
                        mMediaPlayer.prepare();
                        mPlaybackState = new PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_CONNECTING, 0, 1.0f)
                                .build();
                        break;
                    case PlaybackStateCompat.STATE_NONE:
                        mMediaPlayer.prepare();
                        mPlaybackState = new PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_CONNECTING, 0, 1.0f)
                                .build();
                        break;

                }
                mMediaPlayer.start();
                mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                        .build();

                MediaMetadataCompat data = mMusicProvider.getMusicData(song);
                mMediaSession.setMetadata(data);
                mMediaSession.setPlaybackState(mPlaybackState);

            } catch (IOException e) {

            }

        }

        @Override
        public void onPlay() {
            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_PAUSED:
                    mMediaPlayer.start();
                    mPlaybackState = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING,mMediaPlayer.getCurrentPosition(),1.0f)
                            .build();
                    mMediaSession.setPlaybackState(mPlaybackState);
                    //updateNotification();
                    break;

            }
        }

        @Override
        public void onPause() {
            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mMediaPlayer.pause();
                    mPlaybackState = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.getCurrentPosition(), 1.0f)
                            .build();
                    mMediaSession.setPlaybackState(mPlaybackState);
                    //updateNotification();
                    break;

            }
        }

        @Override
        public void onRewind() {
            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 10000);
                    break;

            }
        }

        @Override
        public void onFastForward() {
            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 10000);
                    break;

            }
        }
    };
    private void updatePlaybackState(String error) {
         long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            position = mMediaPlayer.getCurrentPosition();
        }
        int state = PlaybackStateCompat.STATE_NONE;
        if(mPlaybackState != null)
            state = mPlaybackState.getState();
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        }
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());
        mMediaSession.setPlaybackState(stateBuilder.build());
        /*if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
            mMediaNotification.startNotification();
        }*/
    }

}
