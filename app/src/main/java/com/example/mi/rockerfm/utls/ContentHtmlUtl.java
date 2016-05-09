package com.example.mi.rockerfm.utls;

import android.webkit.WebView;

import com.example.mi.rockerfm.JsonBeans.SongDetial;

/**
 * Created by qintong on 16-5-9.
 */
public class ContentHtmlUtl {
    public static void updateSongDetial(WebView webView , SongDetial.Song song){
        StringBuilder sb = new StringBuilder();
        for(SongDetial.Artist artist : song.getArtists()){
            sb.append(artist.getName());
            sb.append(" ");
        }
        webView.loadUrl("javascript:wave('" + song.getId() + "','"+ song.getName() +"','"+ sb.toString() +"','" + song.getAlbum().getPicUrl() +"')");

    }
}
