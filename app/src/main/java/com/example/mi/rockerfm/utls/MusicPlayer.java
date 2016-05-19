package com.example.mi.rockerfm.utls;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.mi.rockerfm.Bus.MusicPlayStatusChangeEvent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by qintong on 16-5-16.
 */
public class MusicPlayer {
    private List<SongDetial.Song> mSongList;
    private MediaPlayer mMediaPlayer;
    private static MusicPlayer instance;
    private AudioManager mAudioManager;
    private SongDetial.Song mCurrentSong;
    private int mCurrentIndex;

    public static MusicPlayer getInstance(Application application) {
        if (instance == null) {
            synchronized (MusicPlayer.class) {
                if (instance == null)
                    instance = new MusicPlayer(application);
            }
        }
        return instance;
    }

    public List<SongDetial.Song> getmSongList() {
        return mSongList;
    }

    private MusicPlayer(Application application) {
        mAudioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mSongList = new ArrayList<SongDetial.Song>();
    }

    public boolean addPlaySong(SongDetial.Song song) {
        mSongList.add(0,song);
        return playSong(0);
    }

    private void addSong(SongDetial.Song song) {
        mSongList.add(song);
    }

    public void changePlayStatus() {
        if (mMediaPlayer == null)
            return;
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
        else {
            mMediaPlayer.start();
        }
    }

    public void skipSong() {
        playSong(mCurrentIndex+1);
    }

    private boolean requestFocus() {
        // Request audio focus for playback
        int result = mAudioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Raise it back to normal
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if(getPlayState() == MusicPlayStatusChangeEvent.RequestState.PLAY)
                    changePlayStatus();
            }
        }
    };

    public void setCurrentIndex(int index) {
        this.mCurrentIndex = index;
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public MusicPlayStatusChangeEvent.RequestState getPlayState() {
        if (mMediaPlayer.isPlaying())
            return MusicPlayStatusChangeEvent.RequestState.PLAY;
        else return MusicPlayStatusChangeEvent.RequestState.PUASE;
    }
    public SongDetial.Song getmCurrentSong(){
        if(mSongList==null || mCurrentIndex<0 || mCurrentIndex>=mSongList.size())
            return null;
        else
            return mSongList.get(mCurrentIndex);
    }
    public boolean playSong(int index) {
        if(mSongList==null || index<0 || index>=mSongList.size())
            return false;
        mCurrentIndex = index;
        SongDetial.Song song = mSongList.get(index);
        if (!requestFocus())
            return false;
        if (mMediaPlayer == null)
            mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.reset();
            mMediaPlayer
                    .setDataSource(song.getmp3Url());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            Log.v("AUDIOHTTPPLAYER", e.getMessage());
            return false;
        }
        return true;
    }
}
