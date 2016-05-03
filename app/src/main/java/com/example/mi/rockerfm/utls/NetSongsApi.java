package com.example.mi.rockerfm.utls;

import com.example.mi.rockerfm.JsonBeans.Articles;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by qin on 2016/5/4.
 */
public interface NetSongsApi {
    @GET("admin-ajax.php?action=get_posts_by_page_json&source=mobile")
    @Query("id",)
    Call<Articles> mainArticles(@Query("id") int id,@Query("ids") int ids);
}
