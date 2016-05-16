package com.example.mi.rockerfm.utls;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ListView;

import com.example.mi.rockerfm.Bus.MusicPlayStatusChangeEvent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by qintong on 16-5-16.
 */
public class MusicPlayer {
    private List<SongDetial.Song> mSongList;
    private MediaPlayer mMediaPlayer;
    private ListView mListView;
    private static MusicPlayer instance;
    public static MusicPlayer getInstance(ListView listView){
        if(instance == null){
            synchronized (MusicPlayer.class){
                if(instance == null)
                    instance = new MusicPlayer();
            }
        }
        instance.mListView = listView;
        return instance;
    }
    private MusicPlayer(){
        mMediaPlayer = new MediaPlayer();
        mSongList = new ArrayList<SongDetial.Song>();
        EventBus.getDefault().register(this);
    }

    private boolean playSong(SongDetial.Song song){
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
        return mSongList.add(song);
    }

    private boolean addSong(SongDetial.Song song){
        return mSongList.add(song);
    }

    private void pauseSong(){
        if(mMediaPlayer == null || !mMediaPlayer.isPlaying())
            return;
        mMediaPlayer.pause();
    }
    @Subscribe(threadMode = ThreadMode.BackgroundThread)
    public void onEvent(final MusicPlayStatusChangeEvent event) {
        switch (event.getState()){
            case  ADD_AND_PLAY :
                playSong(event.getSong());

        }
    }
}
