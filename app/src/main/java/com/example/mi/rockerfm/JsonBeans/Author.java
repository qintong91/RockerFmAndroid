package com.example.mi.rockerfm.JsonBeans;

/**
 * Created by qin on 2016/4/16.
 */
public class Author {
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
