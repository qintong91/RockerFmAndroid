package com.example.mi.rockerfm.Bus;

import com.example.mi.rockerfm.JsonBeans.SongDetial;

/**
 * Created by qintong on 16-5-16.
 */
public class SongLoadDitialObtainEvent {
    public SongDetial.Song getSong() {
        return song;
    }

    public void setSong(SongDetial.Song song) {
        this.song = song;
    }

    private SongDetial.Song song;
}
