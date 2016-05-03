package com.example.mi.rockerfm.Converter;

import android.util.Log;

import com.example.mi.rockerfm.JsonBeans.ArticleContent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

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

                ArticleContent articalsContent = (ArticleContent) mObj;
                Document document = Jsoup.parse(value.string());
                Element element = document.select("div.entry-content").select(".noselect").select(".entry-topic").first();
                Elements elementsImg = element.select("img");
                for (int i = 0; i < elementsImg.size(); i++) {
                    Element e = elementsImg.get(i);
                    e.attr(SRC, e.attr(PIC_ORG));
                }
               /* Elements elementsSong = element.select("iframe");
                for (int i = 0; i < elementsSong.size(); i++) {
                    Element e = elementsSong.get(i);
                    String id = e.attr(ID);
                }*/
                articalsContent.setContentHtml(getHtmlWithPicSrc(element.html()));
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
}