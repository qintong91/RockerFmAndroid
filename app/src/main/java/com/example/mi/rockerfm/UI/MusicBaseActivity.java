package com.example.mi.rockerfm.UI;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mi.rockerfm.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by qin on 2016/5/18.
 */
public class MusicBaseActivity extends Activity {
    private SlidingUpPanelLayout contentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();
     }
    private  void initContentView() {
        ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
        content.removeAllViews();
        contentLayout=new SlidingUpPanelLayout(this);
        content.addView(contentLayout);
        contentLayout.setDragView(R.layout.common_music_layout);
        //LayoutInflater.from(this).inflate(R.layout.common_music_layout, contentLayout, true);
    }
    @Override
          public void setContentView(int layoutResID) {

            LayoutInflater.from(this).inflate(layoutResID, contentLayout, true);

    }
    @Override
    public void setContentView(View customContentView) {
        contentLayout.addView(customContentView);

    }
}
