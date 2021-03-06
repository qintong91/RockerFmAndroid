package com.example.mi.rockerfm.JsonBeans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qin on 2016/4/16.
 */
public class Articles implements Serializable{
    public int getTotalCount() {
        return totalCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getLimit() {
        return limit;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List< Article> getData() {
        return data;
    }


    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private int totalCount;
    private int currentCount;
    private int limit;
    private int totalPage;
    private int currentPage;
    private List<Article> data;

    public static class Article implements Serializable{
        public String getPermalink() {
            return permalink;
        }

        public String getTitleAttr() {
            return titleAttr;
        }

        public String getTopCategory() {
            return topCategory;
        }

        public String getSubCategoryName() {
            return subCategoryName;
        }

        public Author getAuthor() {
            return author;
        }

        public String getCover() {
            return cover;
        }

        public String getCategaryMarkClassname() { return categaryMarkClassname; }

        public String getId() {
            return id;
        }

        private Author author;
        private String permalink;
        private String titleAttr;
        private String topCategory;
        private String subCategoryName;
        private String cover;
        private String categaryMarkClassname;
        private String id;
        //private String time;
    }

}
