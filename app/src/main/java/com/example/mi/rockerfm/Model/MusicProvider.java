package com.example.mi.rockerfm.Model;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.mi.rockerfm.JsonBeans.SongDetial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by qin on 2016/6/2.
 */
public class MusicProvider {
    public static final String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    List<MediaSessionCompat.QueueItem> mQueue;
     private final HashSet<String> mFavoriteSet;
    public MusicProvider(){
        mQueue = new ArrayList<MediaSessionCompat.QueueItem>();
        mFavoriteSet = new HashSet<String>();
    }
    public int addMusicItem(SongDetial.Song song){
        if(mFavoriteSet.contains(song.getId())){
            for(int i =0 ;i< mQueue.size();i++){
                MediaSessionCompat.QueueItem item = mQueue.get(i);
                if (item.getDescription().getMediaId().equals(song.getId()))
                    return i;
            }
            return -1;
        }else {
            mQueue.add(buildFromSong(song));
            return mQueue.size()-1;
        }

    }
    private MediaSessionCompat.QueueItem buildFromSong(SongDetial.Song song) {
        MediaMetadataCompat data = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getId())
                .putString(CUSTOM_METADATA_TRACK_SOURCE, song.getmp3Url())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum().getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getAtistsString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getName())
                .build();
        return new MediaSessionCompat.QueueItem(data.getDescription(), mQueue.size()+1);
    }
    public List<MediaSessionCompat.QueueItem> getQueue(){
        return mQueue;
    }

}
