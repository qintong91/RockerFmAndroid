package com.example.mi.rockerfm.utls;

import com.example.mi.rockerfm.Converter.HtmlConverterFactory;
import com.example.mi.rockerfm.beans.Articals;

import retrofit.Retrofit;

/**
 * Created by qintong on 16-1-27.
 */
public class Net {
    private  static NetApi mApi;
    static final String BASIC_URL = "http://www.rockerfm.com";

    public static NetApi getmApi(){
        if(mApi == null){
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASIC_URL).addConverterFactory(HtmlConverterFactory.create()).build();
            NetApi api = retrofit.create(NetApi.class);
        }
        return mApi;
    }

}
