package com.htsi.zmservice;

import com.squareup.okhttp.OkHttpClient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by htsi.
 * Since: 1/5/16 on 5:19 PM
 * Project: ZMService
 */
public class ServiceGenerator {

    private static final String API_BASE_URL = "http://api.mp3.zing.vn";

    public static final String IMAGE_BASE_URL = "http://image.mp3.zdn.vn/";

    private static final String API_BASE_URL_SEARCH  = "http://ac.mp3.zing.vn";

    private static OkHttpClient okHttpClient = new OkHttpClient();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
            ;

    private static Retrofit.Builder builderSearch =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL_SEARCH)
                    .addConverterFactory(GsonConverterFactory.create())
            ;

    public static <S> S createService(Class<S> serviceClass, boolean search) {
        Retrofit retrofit;
        if (search) {
            retrofit = builderSearch.client(okHttpClient).build();
        } else {
            retrofit = builder.client(okHttpClient).build();
        }
        return retrofit.create(serviceClass);
    }
}
