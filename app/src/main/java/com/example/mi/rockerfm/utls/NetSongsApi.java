package com.example.mi.rockerfm.utls;

import com.example.mi.rockerfm.JsonBeans.SongDetial;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by qin on 2016/5/4.
 */
public interface NetSongsApi {
    @GET("song/detail/")
    Call<SongDetial> songDitials(@Query("id") String id,@Query("ids") String ids);
}
