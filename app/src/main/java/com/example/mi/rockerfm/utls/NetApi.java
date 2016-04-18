package com.example.mi.rockerfm.utls;

import com.example.mi.rockerfm.UI.MainActivity;
import com.example.mi.rockerfm.beans.Articals;

import retrofit2.http.Query;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by qintong on 16-1-27.
 */
/*http://www.jammyfm.com/wordpress/wp-admin/admin-ajax.php?action=get_posts_by_page_json&pageIndex=1&source=mobile&limit=10&topCategory=&isIndex=1&toutiaoPostId=0
* */
public interface NetApi {

    @GET("admin-ajax.php?action=get_posts_by_page_json&source=mobile")
    Call<com.example.mi.rockerfm.JsonBeans.Articals> mainArticals(@Query("pageIndex") int pageIndex, @Query("limit") int limit);
  /*  @GET("geocoding?a=苏州市")
    Call<com.example.mi.rockerfm.JsonBeans.Articals> articals1();*/

}
