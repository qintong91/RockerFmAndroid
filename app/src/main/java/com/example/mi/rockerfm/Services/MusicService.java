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
    private static String TAG = "MusicService";

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
    private MediaMetadataCompat mPlayingMediaMetadata;

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
        if (mPlayingMediaMetadata != null) {
            mMediaSession.setMetadata(mPlayingMediaMetadata);
        }
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
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        mAudioManager.abandonAudioFocus(afChangeListener);
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
            SongDetial.Song song = (SongDetial.Song) extras.getSerializable(OBJ_SONG);
            MediaMetadataCompat data = mMusicProvider.getMusicData(song);
            playFromMediaMetaData(data);
        }

        @Override
        public void onSkipToQueueItem(long id) {
            playFromMediaMetaData(mMusicProvider.getMediaMetaDataByQueueItemId((int)id));
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
            mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
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
            mAudioManager.abandonAudioFocus(afChangeListener);

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
        @Override
        public void onSkipToNext() {
            playFromMediaMetaData(mMusicProvider.getNextMetadata());
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

    private void playFromMediaMetaData(MediaMetadataCompat data) {
        mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (data == null)
            return;
        try {
            mPlayingMediaMetadata = data;
            switch (mPlaybackState.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                case PlaybackStateCompat.STATE_PAUSED:
                    mMediaPlayer.reset();
                    mPlaybackState = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_CONNECTING, 0, 1.0f)
                            .build();
                    break;
                case PlaybackStateCompat.STATE_NONE:
                    mPlaybackState = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_CONNECTING, 0, 1.0f)
                            .build();
                    break;

            }
            mMediaPlayer.setDataSource(MusicService.this, Uri.parse(data.getString(MusicProvider.CUSTOM_METADATA_TRACK_SOURCE)));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .build();
            mMediaSession.setMetadata(data);
            mMediaSession.setQueue(mMusicProvider.getQueue());
            mMediaSession.setPlaybackState(mPlaybackState);

        } catch (IOException e) {

        }

    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG,"onAudioFocusChange"+focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                mMediaController.getTransportControls().pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                mMediaController.getTransportControls().play();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // mAm.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                mAudioManager.abandonAudioFocus(afChangeListener);
                // Stop playback
                mMediaController.getTransportControls().pause();
            }
        }
    };
}
