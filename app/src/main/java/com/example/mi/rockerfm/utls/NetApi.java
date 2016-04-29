package com.example.mi.rockerfm.utls;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by qintong on 16-1-27.
 */
/*http://www.jammyfm.com/wordpress/wp-admin/admin-ajax.php?action=get_posts_by_page_json&pageIndex=1&source=mobile&limit=10&topCategory=&isIndex=1&toutiaoPostId=0
* */
public interface NetApi {

    @GET("admin-ajax.php?action=get_posts_by_page_json&source=mobile")
    Call<com.example.mi.rockerfm.JsonBeans.Articles> mainArticles(@Query("pageIndex") int pageIndex, @Query("limit") int limit);

}
