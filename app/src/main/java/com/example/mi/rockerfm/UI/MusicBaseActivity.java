package com.example.mi.rockerfm.UI;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mi.rockerfm.Bus.MusicPlayStatusChangeEvent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;
import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.utls.MusicPlayer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by qin on 2016/5/18.
 */
public class MusicBaseActivity extends FragmentActivity {
    SlidingUpPanelLayout musicLayout;
    @Bind(R.id.play_list) RecyclerView mRecyclerView;
    @Bind(R.id.dragView) View mDragView;
    @Bind(R.id.container) ViewGroup mContainer;
    @Bind(R.id.play) Button mPlayButtom;
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

    private void initMusicView() {
        ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
        content.removeAllViews();
        LayoutInflater flater = LayoutInflater.from(this);
        musicLayout = (SlidingUpPanelLayout)flater.inflate(R.layout.activity_rockerfm_main, null);
        ButterKnife.bind(this,musicLayout);
        content.addView(musicLayout);
        musicLayout.setAnchorPoint(0.7f);
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
        mPlayButtom.setText("test");
        mPlayButtom.setMovementMethod(LinkMovementMethod.getInstance());
        mPlayButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicLayout != null) {
                    if (musicLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        musicLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                    } else {
                        musicLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }
            }
        });
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

        @Subscribe(threadMode = ThreadMode.MainThread)
        public void onEvent(final MusicPlayStatusChangeEvent event) {
            switch (event.getState()){
                case  ADD_AND_PLAY :
                    mMusicPlayer.addPlaySong(event.getSong());
            }
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

}
