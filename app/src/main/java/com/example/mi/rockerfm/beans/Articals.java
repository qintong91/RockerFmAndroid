package com.example.mi.rockerfm.beans;

import android.text.Html;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by qintong on 16-1-26.
 */
public class Articals implements Type{
    public List<Artical> getArticalList() {
        return articalList;
    }

    public List<Artical> articalList;
    public static class Artical{
        public String getHref() {
            return href;
        }

        public String getId() {
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
        private String id;

        public void setIndexIntro(String indexIntro) {
            this.indexIntro = indexIntro;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImgHref(String imgHref) {
            this.imgHref = imgHref;
        }

        public void setImgAlt(String imgAlt) {
            this.imgAlt = imgAlt;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getAuthor() {
            return author;
        }

        public String[] getLabel() {
            return label;
        }
        public void setAuthor(String author) {
            this.author = author;
        }

        public void setLabel(String[] label) {
            this.label = label;
        }
        public void setAuthorAvatar(String authorAvatar) {
            this.authorAvatar = authorAvatar;
        }
        public String getAuthorAvatar() {
            return authorAvatar;
        }

        private String href;
        private String title;
        private String imgHref;
        private String imgAlt;
        private String type;
        private String indexIntro;
        private String author;
        private String authorAvatar;
        private String[] label;
        //private String time;
    }
}
