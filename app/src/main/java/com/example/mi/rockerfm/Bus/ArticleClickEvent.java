package com.example.mi.rockerfm.Bus;

import com.example.mi.rockerfm.JsonBeans.Articles;

/**
 * Created by qintong on 16-5-16.
 */
public class ArticleClickEvent {

    public Articles.Article getArticle() {
        return article;
    }

    public void setArticle(Articles.Article a) {
        this.article = a;
    }

    private Articles.Article article;
}

