package com.htsi.zmservice;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by htsi on 1/5/16.
 */
public interface ZMService {

    @FormUrlEncoded
    @POST("/api/mobile/song/getsonginfo")
    Call<SongInfo> getSongInfoById(@Field("requestdata") String encodedID);

    @GET("/complete")
    Call<ListSongResponse> search(@QueryMap Map<String, String> options);
}
