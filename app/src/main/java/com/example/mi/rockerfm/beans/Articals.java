package com.example.mi.rockerfm.beans;

import android.text.Html;

import java.util.List;

/**
 * Created by qintong on 16-1-26.
 */
public class Articals {
    List<Artical> articalList;
    public static class Artical{
        private int id;
        private String href;
        private String title;
        private String imgHref;
        private String imgAlt;
        private String type;
        private String indexIntro;
    }
}
