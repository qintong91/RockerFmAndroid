package com.example.mi.rockerfm.JsonBeans;

import java.io.Serializable;

/**
 * Created by qintong on 16-4-27.
 */
public class ArticleContent implements Serializable {
    public String getContentHtml() {
        return ContentHtml;
    }

    public Music[] getMusic() {
        return music;
    }

    public void setContentHtml(String contentHtml) {
        ContentHtml = contentHtml;
    }

    public void setMusic(Music[] music) {
        this.music = music;
    }

    private String ContentHtml;
    private Music[] music;
}
