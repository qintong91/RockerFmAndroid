package com.example.mi.rockerfm.utls;

import com.example.mi.rockerfm.Converter.HtmlConverterFactory;
import com.example.mi.rockerfm.beans.Articals;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by qintong on 16-1-27.
 */
/*http://www.jammyfm.com/wordpress/wp-admin/admin-ajax.php?action=get_posts_by_page_json&pageIndex=1&source=mobile&limit=10&topCategory=&isIndex=1&toutiaoPostId=0
* */
public class Net {
    private  static NetApi mApi;
    //static final String BASIC_URL = "http://www.jammyfm.com/";
    static final String BASIC_URL = "http://www.jammyfm.com/wordpress/wp-admin/";
    //static final String BASIC_URL = "http://gc.ditu.aliyun.com/";
    public static NetApi getmApi(){
        if(mApi == null){
            //Retrofit retrofit = new Retrofit.Builder().baseUrl(BASIC_URL).addConverterFactory(HtmlConverterFactory.create()).build();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASIC_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            mApi = retrofit.create(NetApi.class);
        }
        return mApi;
    }

}
