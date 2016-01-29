package com.example.mi.rockerfm.Converter;

import com.example.mi.rockerfm.beans.Articals;
import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import retrofit.Converter;

/**
 * Created by qintong on 16-1-25.
 */
public class JsoupResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Class<?> elementClass;

   /* JsoupResponseBodyConverter() {
        elementClass = null;
    }*/

    JsoupResponseBodyConverter(Class<?> elementClass) {
        this.elementClass = elementClass;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        Object o = Array.newInstance(elementClass);
        if (elementClass == Articals.class) {
            try {
                Articals articals = (Articals) o;
                Document document = Jsoup.parse(value.string());
                Elements contents = document.getElementsByTag("article");
                Field fieldArticalList = elementClass.getDeclaredField("articalList");
                Field fieldArtical = elementClass.getDeclaredField("articalList");
                fieldArticalList.set(articals, new ArrayList<Articals.Artical>());
                for (int i = 0; i < contents.size(); i++) {
                    fieldArtical.set("id", contents.get(i).attr("id"));
                    fieldArtical.set("title", contents.get(i).attr("title"));
                    fieldArtical.set("href", contents.get(i).attr("href"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) o;
    }
}