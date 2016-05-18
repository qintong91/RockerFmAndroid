package com.example.mi.rockerfm.UI;

import android.os.Bundle;

import com.example.mi.rockerfm.R;

/**
 * Created by qintong on 2016/5/15.
 */
public class RockerFmMainActivity extends MusicBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArticleFragment()).commit();

    }


}




