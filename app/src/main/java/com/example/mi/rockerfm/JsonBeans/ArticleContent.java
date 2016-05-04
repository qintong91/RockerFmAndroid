package com.example.mi.rockerfm.JsonBeans;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by qintong on 16-4-27.
 */
public class ArticleContent implements Serializable {
    public String getContentHtml() {
        return ContentHtml;
    }

    public HashMap<String,SongDetial.Song> getSongsMap() {
        return songsMap;
    }

    public void setContentHtml(String contentHtml) {
        ContentHtml = contentHtml;
    }

    public void setSongsMap(HashMap<String,SongDetial.Song> songs) {
        songsMap = songs;
    }

    private String ContentHtml;

    private HashMap<String,SongDetial.Song> songsMap;
}
