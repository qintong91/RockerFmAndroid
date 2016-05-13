package com.example.mi.rockerfm.UI;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi.rockerfm.JsonBeans.ArticleContent;
import com.example.mi.rockerfm.JsonBeans.Articles;
import com.example.mi.rockerfm.JsonBeans.SongDetial;
import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.utls.ContentWebViewJsAdapter;
import com.example.mi.rockerfm.utls.Net;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by qin on 2016/3/5.
 */
public class ArticleActivity extends Activity {
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final String MIME_TYPE = "text/html";
    private WebSettings mWebSettings;
    private Articles.Article mArticle;
    private String mHtml;
    public static long olｄTime;
    private Handler mHandler;
    private ArticleContent mArticleContent;
    private ContentWebViewJsAdapter mWebViewJsAdapter;
    @Bind(R.id.article_webview)
    WebView mWebView;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_author)
    TextView mTvAuthor;
    @Bind(R.id.iv_avatar)
    SimpleDraweeView mIvAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_article);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

// 设置支持缩放
        mWebSettings.setSupportZoom(false);
        this.mWebView.setWebViewClient(new MyWebViewClient());
        mWebViewJsAdapter = new ContentWebViewJsAdapter(mWebView,null,this);
    }

    @Override
    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(Articles.Article event) {
        olｄTime = System.currentTimeMillis();
        mArticle = event;
        if (mWebView != null) {
            Call<ArticleContent> call = Net.getContentApi().articleContent(mArticle.getId(),mWebSettings.getUserAgentString());
            call.enqueue(new ContentCallback());
            mTvTitle.setText(mArticle.getTitleAttr());
            mTvAuthor.setText(mArticle.getAuthor().getNickname());
            mIvAuthor.setImageURI(Uri.parse(mArticle.getAuthor().getAvatarSrc()));
        }
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(final SongDetial.Song event) {
        Log.d("Time_Html_getsongs", System.currentTimeMillis() - olｄTime + "");
        mWebViewJsAdapter.updateSongDetial(event);
    }

    private final class ContentCallback implements Callback<ArticleContent> {

        @Override
        public void onResponse(Call<ArticleContent> call, Response<ArticleContent> response) {
            Log.d("Time_Html_prepared",System.currentTimeMillis() - olｄTime + "");
            mArticleContent = response.body();
            mWebViewJsAdapter.setmArticleContent(mArticleContent);
            mWebView.loadDataWithBaseURL(null, mArticleContent.getContentHtml(), MIME_TYPE, ENCODING_UTF_8, null);
            Log.d("Time_Html_2222", System.currentTimeMillis() - olｄTime + "");
            Toast.makeText(ArticleActivity.this, System.currentTimeMillis() - olｄTime + "", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Call<ArticleContent> call, Throwable t) {
            setNetRequestFailure();
            Log.i("aa", t.getMessage());
        }
    }

    private void setNetRequestFailure() {
        Toast.makeText(ArticleActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
    }

    private class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Toast.makeText(ArticleActivity.this,"Clicking"+url,Toast.LENGTH_SHORT).show();
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("Time_Html_finished",System.currentTimeMillis() - olｄTime + "");
            Toast.makeText(ArticleActivity.this,"finish",Toast.LENGTH_SHORT).show();
            mWebViewJsAdapter.updateEmptySongs();
        }
    }

}
