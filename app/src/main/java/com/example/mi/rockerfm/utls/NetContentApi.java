package com.example.mi.rockerfm.utls;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by qintong on 16-1-27.
 */

public interface NetContentApi {
    @GET("/{id}")
    Call<com.example.mi.rockerfm.JsonBeans.ArticleContent> articleContent(@Path("id") String id, @Header("User-Agent") String userAgent);

}
