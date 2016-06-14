package com.example.mi.rockerfm.utls;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by qintong on 16-1-27.
 */
/*http://www.jammyfm.com/wordpress/wp-admin/admin-ajax.php?action=get_posts_by_page_json&pageIndex=1&source=mobile&limit=10&topCategory=&isIndex=1&toutiaoPostId=0
* */
/*http://www.jammyfm.com/wordpress/wp-admin/admin-ajax.php?action=get_posts_by_page_json&pageIndex=2&source=mobile&limit=10&topCategory=video&subCategory=&isIndex=0&toutiaoPostId=0
 * */
public interface NetApi {
    public static final String TOP_CATOGORY_VIEDO = "video";
    public static final String TOP_CQTOGORY_SERIES = "series";
    public static final String TOP_CQTOGORY_ACTIVITY = "activity";
    public static final String TOP_CQTOGORY_ARTISTS = "artists";
    public static final int TYPE_INLAND = 1353;
    public static final int TYPE_WORLD = 1354;
    public static final int TYPE_COLUMN = 1246;
    public static final int TYPE_PERSONAL_STORY = 2817;
    public static final int TYPE_INTERVIEW = 970;
    public static final int TYPE_NEW_WORDKS = 1357;
    public static final int TYPE_INDUSTRY = 1371;
    public static final int TYPE_SHOW = 1356;


    @GET("admin-ajax.php?action=get_posts_by_page_json&source=mobile")
    Call<com.example.mi.rockerfm.JsonBeans.Articles> getArticles(@Query("pageIndex") int pageIndex, @Query("limit") int limit,@Query("topCategory") String topCatogory);

}
