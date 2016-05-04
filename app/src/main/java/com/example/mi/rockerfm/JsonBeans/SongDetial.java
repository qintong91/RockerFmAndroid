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


        private String mp3Url;
        private String name;
        private String id;
        private Album album;
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
}
