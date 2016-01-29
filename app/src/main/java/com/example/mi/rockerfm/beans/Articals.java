package com.example.mi.rockerfm.beans;

import android.text.Html;

import java.util.List;

/**
 * Created by qintong on 16-1-26.
 */
public class Articals {
    public List<Artical> getArticalList() {
        return articalList;
    }

    List<Artical> articalList;
    public static class Artical{
        public String getHref() {
            return href;
        }

        public int getId() {
            return id;
        }

        public String getImgAlt() {
            return imgAlt;
        }

        public String getImgHref() {
            return imgHref;
        }

        public String getIndexIntro() {
            return indexIntro;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }
        private int id;
        private String href;
        private String title;
        private String imgHref;
        private String imgAlt;
        private String type;
        private String indexIntro;
    }
}
