package com.example.mi.rockerfm.utls;

import com.example.mi.rockerfm.Converter.HtmlConverterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by qintong on 16-1-27.
 */
/*http://www.jammyfm.com/wordpress/wp-admin/admin-ajax.php?action=get_posts_by_page_json&pageIndex=1&source=mobile&limit=10&topCategory=&isIndex=1&toutiaoPostId=0
* */
public class Net {
    private  static NetApi mApi;
    private static NetContentApi mContentApi;
    private static NetSongsApi mSongsApi;
    static final String ARTICLE_BASIC_URL = "http://www.jammyfm.com/";
    static final String BASIC_URL = "http://www.jammyfm.com/wordpress/wp-admin/";
    static final String SONGS_BASIC_URL = "http://music.163.com/api/";
    //static final String BASIC_URL = "http://gc.ditu.aliyun.com/";
    public static NetApi getmApi(){
        if(mApi == null){
            //Retrofit retrofit = new Retrofit.Builder().baseUrl(BASIC_URL).addConverterFactory(HtmlConverterFactory.create()).build();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASIC_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mApi = retrofit.create(NetApi.class);
        }
        return mApi;
    }
    public static NetContentApi getContentApi(){
        if(mContentApi == null){
            //Retrofit retrofit = new Retrofit.Builder().baseUrl(BASIC_URL).addConverterFactory(HtmlConverterFactory.create()).build();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(ARTICLE_BASIC_URL)
                    .addConverterFactory(HtmlConverterFactory.create())
                    .build();
            mContentApi = retrofit.create(NetContentApi.class);
        }
        return mContentApi;
    }
    public static NetSongsApi getSongsApi(){
        if(mSongsApi == null){
            Retrofit retrofit = new Retrofit.Builder().baseUrl(SONGS_BASIC_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mSongsApi = retrofit.create(NetSongsApi.class);
        }
        return mSongsApi;
    }

}
