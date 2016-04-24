package com.example.mi.rockerfm.UI;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.beans.Articals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * Created by qin on 2016/3/5.
 */
public class ArticleActivity extends Activity {
    private WebView mWebView;
    private String mUrl;
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final String MIME_TYPE = "text/html";
    public static final String PIC_SRC ="src";
    public static final String PIC_ORG ="data-original";
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_article);
        super.onCreate(savedInstanceState);
        mWebView = (WebView)findViewById(R.id.article_webview);
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
            Thread downloadThread = new Thread() {
                Document doc = null;
                Element  element = null;

                public void run() {
                    try {
                        doc = Jsoup.connect(mUrl).get();
                        element = doc.select("div.entry-content").select(".noselect").select(".entry-topic").first();

                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                    if (element == null) {
                        Log.e("error", "There is a problem with the selection");
                    } else {
                        // post a new Runnable from a Handler in order to run the WebView loading code from the UI thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //mWebView.loadData(element.html(), "text/html; charset=UTF-8", null);
                                //String content = String.format(readFile("template.txt"), element.html());
                                String s = getHtmlWithPicSrc(element.html());
                                mWebView.loadDataWithBaseURL(null, s, MIME_TYPE, ENCODING_UTF_8, null);
                            }
                        });
                    }
                }
            };
            downloadThread.start();
        }
    }
    private String readFile(String fileName) {
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
    }
    private String getHtmlWithPicSrc(String originalHtml){
        String s = "<head><style>img{max-width:100% !important;}</style></head>\n"+ originalHtml;

        return  (s.replaceAll(PIC_ORG,PIC_SRC));
    }
}
