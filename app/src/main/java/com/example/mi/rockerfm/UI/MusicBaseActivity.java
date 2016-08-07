package com.example.mi.rockerfm.UI;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mi.rockerfm.Bus.MusicPlayStatusChangeEvent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;
import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.Services.MusicService;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by qin on 2016/5/18.
 */
public abstract class MusicBaseActivity extends AppCompatActivity implements View.OnClickListener,ServiceConnection{
    private static int BUTTON_STATE_PAULSE = 0;
    private static int BUTTON_STATE_PLAY = 1;

    SlidingUpPanelLayout musicLayout;



    private RecyclerView.LayoutManager mLayoutManager;
    private MusicRecyclerViewAdapter mRecyclerAdapter;
    private List<SongDetial.Song> mSongList;
    private MediaSessionCompat.Token mMediaToken;
    private MusicService.MusicServiceBinder mMusicServiceBinder;
    private MediaControllerCompat mMediaController;
    private List<MediaSessionCompat.QueueItem> mMusicQueue;
    private MediaMetadataCompat mMediaMetadata;
    private PlaybackStateCompat mPlaybackState;
    private Handler mHandler=new Handler();
    private Runnable mSeekBarUpdateRunnable;
    private ViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,this, Context.BIND_AUTO_CREATE);
        initMusicView();
    }
    @Override
    protected void onStart(){
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop(){
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(this);

    }
    private void initMusicView() {
        mViewHolder = new ViewHolder();
        ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
        content.removeAllViews();
        LayoutInflater flater = LayoutInflater.from(this);
        musicLayout = (SlidingUpPanelLayout)flater.inflate(R.layout.activity_rockerfm_main, null);
        ButterKnife.bind(mViewHolder, musicLayout);
        content.addView(musicLayout);
        mViewHolder.mDragView.setClickable(false);
        musicLayout.setAnchorPoint(0.6f);
        musicLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
        musicLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mViewHolder.mPlayButtom.setOnClickListener(this);
        mViewHolder.mListButtom.setOnClickListener(this);
        mViewHolder.mSkipButtom.setOnClickListener(this);
        initSeekBar();
        initRecyclerView();
    }

    @Override
    public void setContentView(int layoutResID) {
         LayoutInflater.from(this).inflate(layoutResID, mViewHolder.mContainer, true);
    }

    @Override
    public void setContentView(View customContentView) {

        mViewHolder.mContainer.addView(customContentView);

    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mViewHolder.mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerAdapter = new MusicRecyclerViewAdapter();
        mViewHolder.mRecyclerView.setAdapter(mRecyclerAdapter);
        mViewHolder.mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                c.drawColor(getResources().getColor(R.color.colorDivider));
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 1);
            }
        });
    }

    private void initSeekBar(){
        mViewHolder.mSeekBar.setPadding(0, 0, 0, 0);
        mSeekBarUpdateRunnable =new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                updateSeekBar();
                //if(mMediaController != null && mMediaController.getQueue() == null)
                //要做的事情
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mSeekBarUpdateRunnable, 1000);
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
            MediaDescriptionCompat description = mMediaController.getQueue().get(position).getDescription();
            long id = mMediaController.getQueue().get(position).getQueueId();
            //SongDetial.Song song = mSongList.get(position);
            viewHolder.mSongTextView.setText(new StringBuilder(description.getTitle()).append(" - ").append(description.getDescription()).toString());
            if (description.getMediaId().equals(mMediaMetadata.getDescription().getMediaId()))
                viewHolder.mSongTextView.setTextColor(getColor(R.color.colorAccent));
            else
                viewHolder.mSongTextView.setTextColor(getColor(R.color.common_signin_btn_text_light));

        }

        //获取数据的数量
        @Override
        public int getItemCount() {
            //return mArticles == null ? 0:mArticles.ArticleList.size();
            return mMediaController == null||mMediaController.getQueue() == null ? 0 : mMediaController.getQueue().size();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.text_song)
            TextView mSongTextView;
            @Bind(R.id.button_delete)
            Button mDeleteButton;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mMediaController.getTransportControls().skipToQueueItem(getAdapterPosition());
                updateMusicView();
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MainThread) //子fragment调用 可以优化
      public void onEvent(final MusicPlayStatusChangeEvent event) {
        if(mMediaController == null)
            return;
        SongDetial.Song song = event.getSong();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MusicService.OBJ_SONG,song);
        switch (event.getState()){
            case  PLAY :
                mMediaController.getTransportControls().playFromSearch(null, bundle);
                break;
        }
        updateMusicView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_skip:
                mMediaController.getTransportControls().skipToNext();
                updateMusicView();
                break;
            case R.id.button_list:
                if (musicLayout != null) {
                    if (musicLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        musicLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                    } else {
                        musicLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }
                break;
            case R.id.button_play:
                if(mMediaController == null)
                    break;
                if(mMediaController.getPlaybackState().getState()==PlaybackStateCompat.STATE_PLAYING)
                    mMediaController.getTransportControls().pause();
                else
                    mMediaController.getTransportControls().play();
                updateMusicView();
                break;
        }
    }

    private void updateMusicView(){
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {       //connect Service
        mMusicServiceBinder = (MusicService.MusicServiceBinder)service;
        mMediaToken = mMusicServiceBinder.getToken();
        try {
            mMediaController = new MediaControllerCompat(MusicBaseActivity.this,mMediaToken);
            mMediaMetadata = mMediaController.getMetadata();
            if(mMediaMetadata != null)
                updateMusicBarView();
            mMediaController.registerCallback(new MusicControllerCallback());
            mMusicQueue = mMediaController.getQueue();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {                 //disconnect Service
        mMusicServiceBinder = null;
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mViewHolder.mSeekBar.setMax(duration);
    }

    private void updateSeekBar() {
        if (mPlaybackState == null) {
            return;
        }
        long currentPosition = mPlaybackState.getPosition();
        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_NONE) {
            currentPosition = 0;
        } else if (mPlaybackState.getState() != PlaybackStateCompat.STATE_PAUSED) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mPlaybackState.getPlaybackSpeed();
        }
        mViewHolder.mSeekBar.setProgress((int) currentPosition);
    }

    private class MusicControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            mPlaybackState = state;
            switch(state.getState()){
                case PlaybackStateCompat.STATE_PAUSED: {
                    changePlayButtton(BUTTON_STATE_PAULSE);
                    break;
                }
                case PlaybackStateCompat.STATE_PLAYING: {
                    changePlayButtton(BUTTON_STATE_PLAY);
                    break;
                }
                case PlaybackStateCompat.STATE_NONE: {
                    changePlayButtton(BUTTON_STATE_PLAY);
                    mViewHolder.mSeekBar.setProgress(0);
                }
            }
            mRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mMediaMetadata = metadata;
            updateMusicView();
            updateMusicBarView();
        }

        @Override
        public void onSessionDestroyed() {
        }


    }

    private void updateMusicBarView() {
        mViewHolder.mAlbumView.setImageURI(Uri.parse(mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        mViewHolder.mMusicTextView.setText(mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mViewHolder.mArtistsTextView.setText(mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        mViewHolder.mSeekBar.setMax((int) mMediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
    }

    private void changePlayButtton(int state) {
        if(state == BUTTON_STATE_PLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                mViewHolder.mPlayButtom.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_36dp, null));
            else
                mViewHolder.mPlayButtom.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_36dp));

        } else if(state == BUTTON_STATE_PAULSE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                mViewHolder.mPlayButtom.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_36dp, null));
            else
                mViewHolder.mPlayButtom.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_36dp));
        }

    }

    static class ViewHolder {
        @Bind(R.id.dragView) View mDragView;
        @Bind(R.id.play_list) RecyclerView mRecyclerView;
        @Bind(R.id.container) ViewGroup mContainer;
        @Bind(R.id.button_play) Button mPlayButtom;
        @Bind(R.id.button_list) Button mListButtom;
        @Bind(R.id.button_skip) Button mSkipButtom;
        @Bind(R.id.tv_music) TextView mMusicTextView;
        @Bind(R.id.tv_artists) TextView mArtistsTextView;
        @Bind(R.id.img_album) SimpleDraweeView mAlbumView;
        @Bind(R.id.seekbar_music) SeekBar mSeekBar;
    }
}
