package com.example.mi.rockerfm.Converter;

import okhttp3.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by qintong on 16-1-25.
 */
public class HtmlConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static HtmlConverterFactory create() {
        return new HtmlConverterFactory();
    }

    @Override
    public Converter<okhttp3.ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                    Retrofit retrofit) {
        return new JsoupResponseBodyConverter<Type>( type);
    }
}
