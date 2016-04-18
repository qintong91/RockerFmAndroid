package com.example.mi.rockerfm.JsonBeans;

import java.io.Serializable;

/**
 * Created by qin on 2016/4/16.
 */
public class Author implements Serializable{
    private int id;

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarSrc() {
        return avatarSrc;
    }

    public String getAuthorPostsUrl() {
        return authorPostsUrl;
    }

    private String nickname;
    private String avatarSrc;
    private String authorPostsUrl;
}
