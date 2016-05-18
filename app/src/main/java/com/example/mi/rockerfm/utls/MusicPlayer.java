package com.example.mi.rockerfm.utls;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
 import android.media.AudioManager;
import android.media.MediaPlayer;
  import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mi.rockerfm.Bus.MusicPlayStatusChangeEvent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;
import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.UI.MusicBaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by qintong on 16-5-16.
 */
public class MusicPlayer{
    private List<SongDetial.Song> mSongList;
    private MediaPlayer mMediaPlayer;
     private static MusicPlayer instance;
    private AudioManager mAudioManager;
    public static MusicPlayer getInstance(Application application){
        if(instance == null){
            synchronized (MusicPlayer.class){
                if(instance == null)
                    instance = new MusicPlayer(application);
            }
        }
         return instance;
    }

    public List<SongDetial.Song> getmSongList() {
        return mSongList;
    }

    private MusicPlayer(Application application){
        mAudioManager = (AudioManager)application.getSystemService(Context.AUDIO_SERVICE);
         mMediaPlayer = new MediaPlayer();
         mSongList = new ArrayList<SongDetial.Song>();
    }

    public boolean addPlaySong(SongDetial.Song song){
        if(!requestFocus())
            return false;
        mSongList.add(song);
        if(mMediaPlayer == null)
            mMediaPlayer = new MediaPlayer();
        try {
            if(mMediaPlayer.isPlaying())
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

    private void addSong(SongDetial.Song song){
        mSongList.add(song);
     }

    private void pauseSong(){
        if(mMediaPlayer == null || !mMediaPlayer.isPlaying())
            return;
        mMediaPlayer.pause();
    }
    private void playSong(){
        if(mMediaPlayer == null || mMediaPlayer.isPlaying())
            return;
        mMediaPlayer.start();
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
                pauseSong();
            }
        }
    };

}
