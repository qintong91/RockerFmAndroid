package com.example.mi.rockerfm.Converter;

import android.text.TextUtils;
import android.util.Log;

import com.example.mi.rockerfm.JsonBeans.ArticleContent;
import com.example.mi.rockerfm.JsonBeans.SongDetial;
import com.example.mi.rockerfm.UI.ArticleActivity;
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
    private static final String HTML_HEAD = "<script>function updateSong(id,title,artists,album_src){  \n" +
            "var s = document.getElementById(id); \n" +
            "s.getElementsByTagName(\"a\")[0].href = 'music://'+id; \n" +
            "s.getElementsByClassName(\"title\")[0].innerHTML = 'title'; \n" +
            "s.getElementsByClassName(\"artists\")[0].innerHTML = artists; \n" +
            "s.getElementsByTagName(\"img\")[0].src = album_src;" +
            "} \n" +
            "</script>" +
            "<script>function updateEmptySongs(){  \n" +
            "            var s = document.getElementsByClassName(\"info\");  \n" +
            "            for (var i=0;i<s.length;i++)\n" +
            "            {\n" +
            "              if(s[i].getElementsByTagName(\"a\")[0].href == \"music://\"){       \n" +
            "              var id = s[i].attributes[\"id\"].value;  \n" +
            "              s[i].getElementsByTagName(\"a\")[0].href = 'music://'+id;  \n" +
            "              s[i].getElementsByClassName(\"title\")[0].innerHTML = Android.getSongTitle(id);  \n" +
            "              s[i].getElementsByClassName(\"artists\")[0].innerHTML = Android.getArtistsName(id);  \n" +
            "              s[i].getElementsByTagName(\"img\")[0].src = Android.getAlbumSrc(id);   \n" +
            " }\n" +
            "            }\n" +
            "          } </script>  " +
            "<head><style>img{max-width:100% ; height:auto ; text-align:center  !important;}</style></head>\n";

    private static final String MUSIC_HTML_STRING = " <div class=\"info\" style=\"margin-top: 20px;width: 100%;font-size: 12px;background: #F0F8FF; position: relative;display: inline-block;\" id=\"\"> \n" +
            "  <a href=\"music://\" style=\"height: 100px;float: left;width: 100%;text-decoration:none;\">\n" +
            " <img style=\"height: 100px;float: left;\"> \n" +
            "  <div class=\"cnt\" style=\"    margin-left: 120px;   \"> \n" +
            "   <div class=\"iner\"> \n" +
            "    <h2 class=\"title\"style=\"color: #000033;font-size: 18px;\"></h2> \n" +
            "    <p class=\"artists\" style=\"color: #666;font-size: 16px;\"></p> \n" +
            "   </div> \n" +
            "  </div> </a> \n" +
            "  </div>";
    private static final String MUSIC_URL = "http://music.163.com/";
    private ArticleContent mArticlesContent;
    JsoupResponseBodyConverter(Type type) {
        elementClass = (Class) type;
        this.mType = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            Log.d("Time_Html_getBody",System.currentTimeMillis() - ArticleActivity.olｄTime + "");
            Class classType = null;

            classType = Class.forName("com.example.mi.rockerfm.JsonBeans.ArticleContent");
            mObj = classType.newInstance();

            Log.i("~~~", elementClass.toString());
            if (elementClass == ArticleContent.class) {

                mArticlesContent = (ArticleContent) mObj;
                Document document = Jsoup.parse(value.string());
                Element element = document.select("div.entry-content").select(".noselect").select(".entry-topic").first();
                Elements elementsImg = element.select("img");
                if(elementsImg != null && elementsImg.size()>0) {
                    for (int i = 0; i < elementsImg.size(); i++) {
                        Element e = elementsImg.get(i);
                        e.attr(SRC, e.attr(PIC_ORG));
                    }
                }
                Elements elementsSong = element.select("iframe");
                Call<SongDetial> call = null;
                if (elementsSong != null && elementsSong.size() > 0) {
                    mArticlesContent.setSongsMap(new HashMap<String, SongDetial.Song>((int) Math.ceil(elementsSong.size() / 0.75)));
                    for (int i = elementsSong.size() - 1; i >= 0; i--) {
                        Element e = elementsSong.get(i);
                        String src = e.attr(SRC);
                        if (TextUtils.isEmpty(src) || !src.contains(MUSIC_URL))
                            continue;
                        Matcher m = Pattern.compile("(?<=id=)(\\d+)").matcher(src);
                        String id = null;
                        while (m.find()) {
                            id = m.group();
                            break;
                        }
                        call = Net.getSongsApi().songDitials(id, "[" + id + "]");
                        call.enqueue(new LoadSongsDitialCallBack());
                        e.before(MUSIC_HTML_STRING);
                        e.parent().getElementsByClass("info").first().attr("id", id);
                        e.remove();
                    }
                }
                mArticlesContent.setContentHtml(getHtmlWithHead(element.html()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) mObj;
    }

    private static String getHtmlWithHead(String originalHtml) {
        String s = HTML_HEAD + originalHtml;
        return s;
    }
    private final class LoadSongsDitialCallBack implements Callback<SongDetial> {

        @Override
        public void onResponse(Call<SongDetial> call, Response<SongDetial> response) {
            if(mArticlesContent !=null &&mArticlesContent.getSongsMap()!=null &&response.body().getSong()!=null){
                SongDetial.Song song =response.body().getSong();
                mArticlesContent.addSongs(song.getId(),song);
            }
        }

        @Override
        public void onFailure(Call<SongDetial> call, Throwable t) {
            Log.i("aa", t.getMessage());
        }
    }
}