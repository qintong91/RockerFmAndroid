package com.example.mi.rockerfm.Model;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.mi.rockerfm.JsonBeans.SongDetial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by qin on 2016/6/2.
 */
public class MusicProvider {
    public static final String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    List<MediaSessionCompat.QueueItem> mQueue;
    private final ConcurrentMap<String, MediaMetadataCompat> mMusicListById;
    public MusicProvider(){
        mQueue = new ArrayList<MediaSessionCompat.QueueItem>();
        mMusicListById = new ConcurrentHashMap<String,MediaMetadataCompat>();
    }
    public MediaMetadataCompat getMusicData(SongDetial.Song song){
        if(mMusicListById.containsKey(song.getId())){
            return mMusicListById.get(song.getId());
        }else {
            MediaMetadataCompat data = buildFromSong(song);
            mQueue.add(new MediaSessionCompat.QueueItem(data.getDescription(), mQueue.size()));
            mMusicListById.put(song.getId(),data);
            return data;
        }

    }

    public MediaMetadataCompat getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId) : null;
    }

    private MediaMetadataCompat buildFromSong(SongDetial.Song song) {
        MediaMetadataCompat data = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getId())
                .putString(CUSTOM_METADATA_TRACK_SOURCE, song.getmp3Url())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum().getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getAtistsString())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,song.getAtistsString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,song.getAlbum().getPicUrl())
                .build();
        return data;
    }
    public List<MediaSessionCompat.QueueItem> getQueue(){
        return mQueue;
    }

}
