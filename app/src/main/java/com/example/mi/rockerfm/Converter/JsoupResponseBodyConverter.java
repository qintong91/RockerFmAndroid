package com.example.mi.rockerfm.Converter;

import android.util.Log;

import com.example.mi.rockerfm.beans.Articals;
import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.Converter;

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

    JsoupResponseBodyConverter(Type type) {
        elementClass = (Class)type;
        this.mType = type;

    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try { Class classType = null;

            classType = Class.forName("com.example.mi.rockerfm.beans.Articals");
            Object obj = classType.newInstance();

            Log.i("~~~",elementClass.toString());
         if (elementClass == Articals.class) {

                Articals articals = (Articals) obj;
                Document document = Jsoup.parse(value.string());
                Elements contents = document.getElementsByTag("article");
                Field fieldArticalList = elementClass.getDeclaredField("articalList");
                List<Articals.Artical> list = new ArrayList<Articals.Artical>();
                fieldArticalList.set(articals,list);
                for (int i = 0; i < contents.size(); i++) {
                    Articals.Artical objArtical = new Articals.Artical();
                    objArtical.setId(contents.get(i).attr("id"));
                    objArtical.setTitle(contents.get(i).getElementsByTag("a").attr("title"));
                    objArtical.setHref(contents.get(i).getElementsByTag("a").attr("href"));
                    objArtical.setImgHref(contents.get(i).getElementsByTag("img").attr("src"));
                    objArtical.setIndexIntro(contents.get(i).getElementsByTag("img").attr("alt"));
                    objArtical.setType(contents.get(i).getElementsByTag("i").attr("class"));
                    objArtical.setAuthor(contents.get(i).getElementsByTag("i").attr("class"));
                    Elements labelElements = contents.get(i).getElementsByClass("label");
                    if(labelElements.size()>=1){
                        Elements labelStringElements = labelElements.get(0).getElementsByTag("a");
                        String[] tagStrings = new String[labelStringElements.size()];
                        for(int j=0;j<labelStringElements.size();j++){
                            tagStrings[j] = labelStringElements.get(j).text();
                        }
                        objArtical.setLabel(tagStrings);
                    }
                    list.add(objArtical);
                }
             mObj = articals;
    }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return (T) mObj;
    }
}