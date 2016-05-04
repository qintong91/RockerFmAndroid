package com.example.mi.rockerfm.Converter;

import android.util.Log;

import com.example.mi.rockerfm.JsonBeans.ArticleContent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;
import com.example.mi.rockerfm.utls.Net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by qintong on 16-1-25.
 */
public class JsoupResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Class<?> elementClass;
    private Type mType;
    private Object mObj;
    /* JsoupResponseBodyConverter() {
         elementClass = null;
     }*/
    private static final String SRC = "src";
    private static final String ID = "id";
    private static final String PIC_ORG = "data-original";
    private static final String HTML_HEAD = "<head><style>img{max-width:100% ; height:auto ; text-align:center  !important;}</style></head>\n";
    ArticleContent mArticalsContent;
    JsoupResponseBodyConverter(Type type) {
        elementClass = (Class) type;
        this.mType = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            Class classType = null;

            classType = Class.forName("com.example.mi.rockerfm.JsonBeans.ArticleContent");
            mObj = classType.newInstance();

            Log.i("~~~", elementClass.toString());
            if (elementClass == ArticleContent.class) {

                mArticalsContent = (ArticleContent) mObj;
                Document document = Jsoup.parse(value.string());
                Element element = document.select("div.entry-content").select(".noselect").select(".entry-topic").first();
                Elements elementsImg = element.select("img");
                for (int i = 0; i < elementsImg.size(); i++) {
                    Element e = elementsImg.get(i);
                    e.attr(SRC, e.attr(PIC_ORG));
                }
                Elements elementsSong = element.select("iframe");
                mArticalsContent.setSongsMap(new HashMap<String, SongDetial.Song>((int)Math.ceil(elementsSong.size()/0.75)));
                for (int i = 0; i < elementsSong.size(); i++) {
                    Element e = elementsSong.get(i);
                    String src = e.attr(SRC);
                    Matcher m = Pattern.compile("(?<=id=)(\\d+)").matcher(src);
                    String id = null;
                    while(m.find()){
                        id =m.group();
                        break;
                    }
                    Call<SongDetial> call = Net.getSongsApi().songDitials(id, "[" + id + "]");
                    call.enqueue(new LoadSongsDitialCallBack());

                }
                mArticalsContent.setContentHtml(getHtmlWithPicSrc(element.html()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) mObj;
    }

    private static String getHtmlWithPicSrc(String originalHtml) {
        String s = HTML_HEAD + originalHtml;
        return s;
    }
    private final class LoadSongsDitialCallBack implements Callback<SongDetial> {

        @Override
        public void onResponse(Call<SongDetial> call, Response<SongDetial> response) {
            if(mArticalsContent !=null &&mArticalsContent.getSongsMap()!=null){
                mArticalsContent.getSongsMap().put(response.body().getSong().getId(),response.body().getSong());
            }
        }

        @Override
        public void onFailure(Call<SongDetial> call, Throwable t) {
            Log.i("aa", t.getMessage());
        }
    }
}