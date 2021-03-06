package com.example.mi.rockerfm.UI;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi.rockerfm.Bus.ArticleClickEvent;
import com.example.mi.rockerfm.Bus.MusicPlayStatusChangeEvent;
import com.example.mi.rockerfm.Bus.SongLoadDitialObtainEvent;
import com.example.mi.rockerfm.JsonBeans.ArticleContent;
import com.example.mi.rockerfm.JsonBeans.Articles;
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

public class ArticleFragment extends Fragment {
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final String MIME_TYPE = "text/html";
    private WebSettings mWebSettings;
    private Articles.Article mArticle;
    public static long olｄTime;
    private ArticleContent mArticleContent;
    private ContentWebViewJsAdapter mWebViewJsAdapter;
    private MusicPlayStatusChangeEvent mMusicPlayStatusChangeEvent;
    @Bind(R.id.article_webview)
    WebView mWebView;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_author)
    TextView mTvAuthor;
    @Bind(R.id.iv_avatar)
    SimpleDraweeView mIvAuthor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this, view);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

// 设置支持缩放
        mWebSettings.setSupportZoom(false);
        this.mWebView.setWebViewClient(new MyWebViewClient());
        mWebViewJsAdapter = new ContentWebViewJsAdapter(mWebView, null, getActivity());
        return view;
    }

    @Override
    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        super.onStart();

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(ArticleClickEvent event) {
        olｄTime = System.currentTimeMillis();
        mArticle = event.getArticle();
        if (mWebView != null) {
            Call<ArticleContent> call = Net.getContentApi().articleContent(mArticle.getId(), mWebSettings.getUserAgentString());
            call.enqueue(new ContentCallback());
            mTvTitle.setText(mArticle.getTitleAttr());
            mTvAuthor.setText(mArticle.getAuthor().getNickname());
            mIvAuthor.setImageURI(Uri.parse(mArticle.getAuthor().getAvatarSrc()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(final SongLoadDitialObtainEvent event) {
        Log.d("Time_Html_getsongs", System.currentTimeMillis() - olｄTime + "");
        mWebViewJsAdapter.updateSongDetial(event.getSong());
    }

    private final class ContentCallback implements Callback<ArticleContent> {

        @Override
        public void onResponse(Call<ArticleContent> call, Response<ArticleContent> response) {
            Log.d("Time_Html_prepared", System.currentTimeMillis() - olｄTime + "");
            mArticleContent = response.body();
            mWebViewJsAdapter.setmArticleContent(mArticleContent);
            //Log.e("htmlDetial", mArticleContent.getContentHtml() + "");
            mWebView.loadDataWithBaseURL(null, mArticleContent.getContentHtml(), MIME_TYPE, ENCODING_UTF_8, null);
            Log.d("Time_Html_2222", System.currentTimeMillis() - olｄTime + "");
            //Toast.makeText(getActivity(), System.currentTimeMillis() - olｄTime + "", Toast.LENGTH_LONG).show(); 这是个问题
        }

        @Override
        public void onFailure(Call<ArticleContent> call, Throwable t) {
            setNetRequestFailure();
            Log.i("aa", t.getMessage());
        }
    }

    private void setNetRequestFailure() {
        Toast.makeText(getActivity(), "网络请求失败", Toast.LENGTH_SHORT).show();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(mMusicPlayStatusChangeEvent == null)
                mMusicPlayStatusChangeEvent = new MusicPlayStatusChangeEvent();
            mMusicPlayStatusChangeEvent.setSong(mArticleContent.getSongsMap().get((url.split("//"))[1]));
            mMusicPlayStatusChangeEvent.setState(MusicPlayStatusChangeEvent.RequestState.PLAY);
            EventBus.getDefault().post(mMusicPlayStatusChangeEvent);
            Toast.makeText(getActivity(), "Clicking" + url, Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(getActivity() == null)
                return;
            Log.d("Time_Html_finished", System.currentTimeMillis() - olｄTime + "");
            Toast.makeText(getActivity(), "finish", Toast.LENGTH_SHORT).show();
            mWebViewJsAdapter.updateEmptySongs();
        }
    }
}
