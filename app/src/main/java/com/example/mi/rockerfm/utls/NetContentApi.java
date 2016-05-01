package com.example.mi.rockerfm.utls;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by qintong on 16-1-27.
 */

public interface NetContentApi {

    @GET("/{path}")
    Call<com.example.mi.rockerfm.JsonBeans.ArticleContent> articleContent(@Path("path") String path);

}
