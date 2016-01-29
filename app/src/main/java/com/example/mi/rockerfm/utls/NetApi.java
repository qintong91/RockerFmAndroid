package com.example.mi.rockerfm.utls;

import com.example.mi.rockerfm.beans.Articals;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by qintong on 16-1-27.
 */
public interface NetApi {
    @GET()
     Void articals(Callback<Articals> cb);
}
