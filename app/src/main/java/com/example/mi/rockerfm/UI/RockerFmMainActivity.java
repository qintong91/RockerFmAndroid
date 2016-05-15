package com.example.mi.rockerfm.UI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi.rockerfm.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qin on 2016/5/15.
 */
public class RockerFmMainActivity extends FragmentActivity {
    private static final String TAG = "DemoActivity";

    @Bind(R.id.main_sliding_layout)
    SlidingUpPanelLayout mLayout;
    @Bind(R.id.play_list)
    ListView mPlayListView;
    @Bind(R.id.play)
    Button mPlayButtom;
    @Bind(R.id.dragView)
    View mDragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rockerfm_main);
        ButterKnife.bind(this);
        mLayout.setAnchorPoint(0.7f);
        mDragView.setClickable(false);
        List<String> your_array_list = Arrays.asList(
                "This",
                "Is",
                "An"
        );
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list);

        mPlayListView.setAdapter(arrayAdapter);
          mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
              @Override
              public void onPanelSlide(View panel, float slideOffset) {
                  Log.i(TAG, "onPanelSlide, offset " + slideOffset);
              }

              @Override
              public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                  Log.i(TAG, "onPanelStateChanged " + newState);
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

    }
}




