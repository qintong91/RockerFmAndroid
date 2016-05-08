package com.example.mi.rockerfm.UI;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
    long olｄTime;
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

/*            Thread downloadThread = new Thread() {
                Document doc = null;
                Element element = null;

                public void run() {
                    try {
                        doc = Jsoup.connect(mArticle.getPermalink()).get();
                        element = doc.select("div.entry-content").select(".noselect").select(".entry-topic").first();


                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                    if (element == null) {
                        Log.e("error", "There is a problem with the selection");
                    } else {
                        Elements elements = element.select("img");
                        for (int i = 0; i < elements.size(); i++) {
                            Element e = elements.get(i);
                            e.attr(PIC_SRC, e.attr(PIC_ORG));
                            e.removeAttr(PIC_ORG);
                        }
                        mHtml = getHtmlWithPicSrc(element.html());
                        // post a new Runnable from a Handler in order to run the WebView loading code from the UI thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //mWebView.loadData(element.html(), "text/html; charset=UTF-8", null);
                                //String content = String.format(readFile("template.txt"), element.html());

                                mWebView.loadDataWithBaseURL(null, mHtml, MIME_TYPE, ENCODING_UTF_8, null);
                                // mWebView.loadUrl("javascript:(function() {document.getElementById(\"Image1\").src=\"\";}()");
                                Toast.makeText(ArticleActivity.this, System.currentTimeMillis() - olｄTime + "", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            };
            downloadThread.start();*/
        }
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(SongDetial.Song event) {
       // mWebView.loadUrl("javascript:wave(" + event.getId() + "," + event.getName() +"," + event.getName() +")");
        mWebView.loadUrl("javascript:wave('" + event.getId() + "','" + event.getName() +"')");

    }

    private final class ContentCallback implements Callback<ArticleContent> {

        @Override
        public void onResponse(Call<ArticleContent> call, Response<ArticleContent> response) {

            mWebView.loadDataWithBaseURL(null, response.body().getContentHtml(), MIME_TYPE, ENCODING_UTF_8, null);
            // mWebView.loadUrl("javascript:(function() {document.getElementById(\"Image1\").src=\"\";}()");
            Toast.makeText(ArticleActivity.this, System.currentTimeMillis() - olｄTime + "", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Call<ArticleContent> call, Throwable t) {
            setNetRequestFailure();
            Log.i("aa", t.getMessage());
        }
    }

    /*private String readFile(String fileName) {
        AssetManager manager = getAssets();
        try {
            Scanner scanner = new Scanner(manager.open(fileName));
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNext()) {
                builder.append(scanner.nextLine());
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }*/

    private void setNetRequestFailure() {
        Toast.makeText(ArticleActivity.this, "网络请求失败", Toast.LENGTH_LONG).show();
    }

}
