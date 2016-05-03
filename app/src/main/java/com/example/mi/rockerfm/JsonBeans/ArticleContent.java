package com.example.mi.rockerfm.JsonBeans;

import java.io.Serializable;

/**
 * Created by qintong on 16-4-27.
 */
public class ArticleContent implements Serializable {
    public String getContentHtml() {
        return ContentHtml;
    }

    public Songs[] getSongs() {
        return Songs;
    }

    public void setContentHtml(String contentHtml) {
        ContentHtml = contentHtml;
    }

    public void setMusic(Songs[] music) {
        this.Songs = music;
    }

    private String ContentHtml;
    private Songs[] Songs;
}
