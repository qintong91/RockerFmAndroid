package com.example.mi.rockerfm.Bus;

import com.example.mi.rockerfm.JsonBeans.SongDetial;

/**
 * Created by qintong on 16-5-16.
 */
public class MusicPlayStatusChangeEvent {
    public enum RequestState {
        PLAY,
        PUASE,
    }

    public SongDetial.Song getSong() {
        return song;
    }

    public RequestState getState() {
        return state;
    }

    public void setSong(SongDetial.Song song) {
        this.song = song;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    private RequestState state;
    private SongDetial.Song song;
}
