package com.example.mi.rockerfm.utls;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.mi.rockerfm.JsonBeans.ArticleContent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;

/**
 * Created by qintong on 16-5-9.
 */
public class ContentHtmlUtl {
    public static void updateSongDetial(WebView webView, SongDetial.Song song) {
        webView.loadUrl("javascript:updateSong('" + song.getId() + "','" + song.getName() + "','" + getAtistsString(song) + "','" + song.getAlbum().getPicUrl() + "')");
    }

    public static void updateEmptySongs(WebView webView, final ArticleContent articleContent) {
       // webView.addJavascriptInterface(new ContentHtmlUtl(), "Android");
        webView.loadUrl("javascript:updateEmptySongs()");
    }
    private static String getAtistsString(SongDetial.Song song){
        StringBuilder sb = new StringBuilder();
        for (SongDetial.Artist artist : song.getArtists()) {
            sb.append(artist.getName());
            sb.append(" ");
        }
        return sb.toString();
    }
    public static class JsObject {
        @JavascriptInterface
        public String getSongTitle(String id) {
            return "a";
            //return articleContent.getSongsMap().get(id).getName();
        }

        @JavascriptInterface
        public String getArtistsName(String id) {
            return "a";
            //return getAtistsString(articleContent.getSongsMap().get(id));
        }

        @JavascriptInterface
        public String getAlbumSrc(String id) {
            return "a";
            //return articleContent.getSongsMap().get(id).getAlbum().getPicUrl();
        }

    }

}
