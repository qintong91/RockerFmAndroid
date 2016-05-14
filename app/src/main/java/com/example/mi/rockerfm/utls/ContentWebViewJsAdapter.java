package com.example.mi.rockerfm.utls;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.mi.rockerfm.JsonBeans.ArticleContent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;
import com.example.mi.rockerfm.UI.ArticleActivity;

/**
 * Created by qintong on 16-5-9.
 */
public class ContentWebViewJsAdapter {
    WebView mWebView;
    ArticleContent mArticleContent;
    ArticleActivity mArticleActivity;

    public ContentWebViewJsAdapter(WebView webView, ArticleContent articleContent, ArticleActivity articleActivity) {
        mWebView = webView;
        mArticleContent = articleContent;
        mArticleActivity = articleActivity;
        mWebView.addJavascriptInterface(new JsObject(), "Android");
    }

    public void updateSongDetial(SongDetial.Song song) {
         mWebView.loadUrl("javascript:updateSong('" + song.getId() + "','" + song.getName() + "','" + getAtistsString(song) + "','" + song.getAlbum().getPicUrl() + "')");
    }

    public void updateEmptySongs() {
        // webView.addJavascriptInterface(new ContentHtmlUtl(), "Android");
        mWebView.loadUrl("javascript:updateEmptySongs()");
    }

    public class JsObject {
        @JavascriptInterface
        public String getSongTitle(String id) {
            Log.e("songssss",id+"nnnnnnn");
            if (mArticleContent.getSongsMap().get(id) == null){
                 return "";
            }
            Toast.makeText(mArticleActivity, "Loading From JsObject!!!!!!!!!!!!!"+mArticleContent.getSongsMap().get(id).getName(), Toast.LENGTH_SHORT).show();
            return mArticleContent.getSongsMap().get(id).getName();
        }

        @JavascriptInterface
        public String getArtistsName(String id) {
            if (mArticleContent.getSongsMap().get(id) == null){
                 return "";
            }
            return getAtistsString(mArticleContent.getSongsMap().get(id));
        }

        @JavascriptInterface
        public String getAlbumSrc(String id) {
            if (mArticleContent.getSongsMap().get(id) == null)
            {
                 return null;
            }
            Toast.makeText(mArticleActivity, "Loading From JsObject!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
            return mArticleContent.getSongsMap().get(id).getAlbum().getPicUrl();
        }

    }

    public void setmArticleContent(ArticleContent mArticleContent) {
        this.mArticleContent = mArticleContent;
    }

    private static String getAtistsString(SongDetial.Song song) {
        StringBuilder sb = new StringBuilder();
        for (SongDetial.Artist artist : song.getArtists()) {
            sb.append(artist.getName());
            sb.append(" ");
        }
        return sb.toString();
    }

}
