package com.example.mi.rockerfm.utls;

import android.app.Activity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by qintong on 16-5-16.
 */
public class MusicPlayer {
    private List<SongDetial.Song> mSongList;
    private MediaPlayer mMediaPlayer;
    private RecyclerView mRecyclerView;
    private MusicRecyclerViewAdapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Activity mActivity;
    private static MusicPlayer instance;
    private AudioManager mAudioManager;
    public static MusicPlayer getInstance(RecyclerView recyclerView,Activity activity){
        if(instance == null){
            synchronized (MusicPlayer.class){
                if(instance == null)
                    instance = new MusicPlayer(recyclerView,activity);
            }
        }
         return instance;
    }
    private MusicPlayer(RecyclerView recyclerView,Activity activity){
        mAudioManager = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
        mRecyclerView = recyclerView;
        mActivity = activity;
        mMediaPlayer = new MediaPlayer();
        mSongList = new ArrayList<SongDetial.Song>();
        initRecyclerView();
        EventBus.getDefault().register(this);
    }

    private boolean addPlaySong(SongDetial.Song song){
        if(!requestFocus())
            return false;
        mSongList.add(song);
        mRecyclerAdapter.notifyDataSetChanged();
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
        mRecyclerAdapter.notifyDataSetChanged();
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
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(final MusicPlayStatusChangeEvent event) {
        switch (event.getState()){
            case  ADD_AND_PLAY :
                addPlaySong(event.getSong());

        }
    }
    private void initRecyclerView(){
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerAdapter = new MusicRecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }
    public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicRecyclerViewAdapter.ViewHolder> {
        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_item, viewGroup, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.mSongTextView.setText(mSongList.get(position).getName());
            viewHolder.mArtistsView.setText(mSongList.get(position).getAtistsString());

        }

        //获取数据的数量
        @Override
        public int getItemCount() {
            //return mArticles == null ? 0:mArticles.ArticleList.size();
            return mSongList == null ? 0 : mSongList.size();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.text_song)
            TextView mSongTextView;
            @Bind(R.id.text_artists)
            TextView mArtistsView;
            @Bind(R.id.button_delete)
            Button mDeleteButton;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }

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
