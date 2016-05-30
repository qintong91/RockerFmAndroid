package com.example.mi.rockerfm.UI;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.example.mi.rockerfm.Services.MusicPlayer;
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
public class MusicBaseActivity extends FragmentActivity implements View.OnClickListener{
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


    private MusicPlayer mMusicPlayer;
    private RecyclerView.LayoutManager mLayoutManager;
    private MusicRecyclerViewAdapter mRecyclerAdapter;
    private List<SongDetial.Song> mSongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicPlayer = MusicPlayer.getInstance(getApplication());
        mSongList = mMusicPlayer.getmSongList();
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
        updateMusicBar();
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
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration(){
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
            SongDetial.Song song = mSongList.get(position);
            viewHolder.mSongTextView.setText(new StringBuilder(song.getName()).append(" - ").append(song.getAtistsString()).toString());
            if (mMusicPlayer.getCurrentIndex() == position)
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
                mMusicPlayer.playSong(getAdapterPosition());
                updateMusicView();
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
      public void onEvent(final MusicPlayStatusChangeEvent event) {
        SongDetial.Song song = event.getSong();
        switch (event.getState()){
            case  PLAY :
                mMusicPlayer.addPlaySong(song);
                break;
        }
        updateMusicView();
    }

    private void updateMusicBar() {
        SongDetial.Song song = mMusicPlayer.getmCurrentSong();
        MusicPlayStatusChangeEvent.RequestState state = mMusicPlayer.getPlayState();
        if(song == null)
            return;
        if (state == MusicPlayStatusChangeEvent.RequestState.PLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                mPlayButtom.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_36dp, null));
            else
                mPlayButtom.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_36dp));
        } else if (state == MusicPlayStatusChangeEvent.RequestState.PUASE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                mPlayButtom.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_36dp, null));
            else
                mPlayButtom.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_36dp));
        }
        mAlbumView.setImageURI(Uri.parse(song.getAlbum().getPicUrl()));
        mMusicTextView.setText(song.getName());
        mArtistsTextView.setText(song.getAtistsString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_skip:
                mMusicPlayer.skipSong();
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
                mMusicPlayer.changePlayStatus();
                updateMusicView();
                break;
        }
    }

    private void updateMusicView(){
        updateMusicBar();
        mRecyclerAdapter.notifyDataSetChanged();
    }


}
