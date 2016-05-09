package com.example.mi.rockerfm.JsonBeans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qintong on 16-4-27.
 */
public class SongDetial implements Serializable {
    private List<Song> songs;

    public Song getSong() {
        if (songs == null || songs.size() == 0)
            return null;
        return songs.get(0);
    }

    public static class Song implements Serializable {
        public String getmp3Url() {
            return mp3Url;
        }


        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public Album getAlbum() {
            return album;
        }

        public List<Artist> getArtists() {
            return artists;
        }

        private String mp3Url;
        private String name;
        private String id;
        private Album album;
        private List<Artist> artists;
    }

    public static class Album implements Serializable {
        public String getPicUrl() {
            return picUrl;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        private String name;
        private String picUrl;
        private String id;
    }
    public static class Artist implements Serializable {
        public String getId() {
            return id;
        }

        public String getImg1v1Url() {
            return img1v1Url;
        }

        public String getName() {
            return name;
        }

        public String getPicUrl() {
            return picUrl;
        }

        private String picUrl;
        private String img1v1Url;
        private String name;
        private String id;
    }
}
