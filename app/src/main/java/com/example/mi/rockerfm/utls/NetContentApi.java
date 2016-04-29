package com.example.mi.rockerfm.utls;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by qintong on 16-1-27.
 */

public interface NetContentApi {

    @GET()
    Call<com.example.mi.rockerfm.JsonBeans.ArticleContent> articleContent( String path);

}
