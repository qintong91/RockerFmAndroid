package com.example.mi.rockerfm.UI;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.utls.MusicPlayer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qintong on 2016/5/15.
 */
public class RockerFmMainActivity extends FragmentActivity {
    @Bind(R.id.main_sliding_layout)
    SlidingUpPanelLayout mLayout;
    @Bind(R.id.play_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.play)
    Button mPlayButtom;
    @Bind(R.id.dragView)
    View mDragView;

    private MusicPlayer mMusicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rockerfm_main);
        ButterKnife.bind(this);
        mLayout.setAnchorPoint(0.7f);
        mDragView.setClickable(false);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        TextView t = (TextView) findViewById(R.id.name);
        t.setText("test");
        mPlayButtom.setText("test");
        mPlayButtom.setMovementMethod(LinkMovementMethod.getInstance());
        mPlayButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLayout != null) {
                    if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                    } else {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ArticleFragment()).commit();
        mMusicPlayer = MusicPlayer.getInstance(mRecyclerView, this);

    }

}




