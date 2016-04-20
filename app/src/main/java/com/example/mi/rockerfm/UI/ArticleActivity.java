package com.example.mi.rockerfm.UI;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.beans.Articals;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * Created by qin on 2016/3/5.
 */
public class ArticleActivity extends Activity {
    WebView mWebView;
    String mUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_article);
        super.onCreate(savedInstanceState);
        mWebView = (WebView)findViewById(R.id.article_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override   
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(String event) {
        mUrl = event;
        if(mWebView != null){
            mWebView.loadUrl(mUrl);
        }
    }
}
