package com.example.mi.rockerfm.JsonBeans;

import java.io.Serializable;

/**
 * Created by qintong on 16-4-27.
 */
public class Songs implements Serializable {
    public String getmp3Url() {
        return mp3Url;
    }

    private String mp3Url;
    private String picUrl;
    private String name;
 }
