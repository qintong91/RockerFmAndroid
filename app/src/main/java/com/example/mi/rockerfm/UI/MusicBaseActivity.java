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
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class MusicBaseActivity extends FragmentActivity implements View.OnClickListener,ServiceConnection {
    SlidingUpPanelLayout musicLayout;
    @Bind(R.id.play_list) RecyclerView mRecyclerView;
    @Bind(R.id.dragView) View mDragView;
    @Bind(R.id.container) ViewGroup mContainer;
    @Bind(R.id.button_play) Button mPlayButtom;
    @Bind(R.id.button_list) Button mListButtom;
    @Bind(R.id.button_skip) Button mSkipButtom;
    @Bind(R.id.tv_music) TextView mMusicTextView;
    @Bind(R.id.tv_artists) TextView mArtistsTextView;
    @Bind(R.id.img_album) SimpleDraweeView mAlbumView;


    private RecyclerView.LayoutManager mLayoutManager;
    private MusicRecyclerViewAdapter mRecyclerAdapter;
    private List<SongDetial.Song> mSongList;
    private MediaSessionCompat.Token mMediaToken;
    private MusicService.MusicServiceBinder mMusicServiceBinder;
    private MediaControllerCompat mMediaController;
    private List<MediaSessionCompat.QueueItem> mMusicQueue;
    private MediaMetadataCompat mMediaMetadata;

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
    private void initMusicView() {
        ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
        content.removeAllViews();
        LayoutInflater flater = LayoutInflater.from(this);
        musicLayout = (SlidingUpPanelLayout)flater.inflate(R.layout.activity_rockerfm_main, null);
        ButterKnife.bind(this,musicLayout);
        content.addView(musicLayout);
        mDragView.setClickable(false);
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
        mPlayButtom.setOnClickListener(this);
        mListButtom.setOnClickListener(this);
        mSkipButtom.setOnClickListener(this);
        initRecyclerView();
    }

    @Override
    public void setContentView(int layoutResID) {
         LayoutInflater.from(this).inflate(layoutResID, mContainer, true);
    }

    @Override
    public void setContentView(View customContentView) {

        mContainer.addView(customContentView);

    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerAdapter = new MusicRecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
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
            MediaDescriptionCompat description = mMusicQueue.get(position).getDescription();
            long id = mMusicQueue.get(position).getQueueId();
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
            return mSongList == null ? 0 : mSongList.size();
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
                mMediaController.getTransportControls().playFromSearch(null,bundle);
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
    private class MusicControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            switch(state.getState()){
                case PlaybackStateCompat.STATE_PAUSED: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        mPlayButtom.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_36dp, null));
                    else
                        mPlayButtom.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_36dp));
                    break;
                }
                case PlaybackStateCompat.STATE_PLAYING: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        mPlayButtom.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_36dp, null));
                    else
                        mPlayButtom.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_36dp));
                    break;
                }
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mMediaMetadata = metadata;
            mAlbumView.setImageURI(Uri.parse(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
            mMusicTextView.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            mArtistsTextView.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        }

        @Override
        public void onSessionDestroyed() {
        }
    }
}
