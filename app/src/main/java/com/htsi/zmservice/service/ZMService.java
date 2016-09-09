package com.htsi.zmservice.service;

import com.htsi.zmservice.models.SongInfo;
import com.htsi.zmservice.models.response.ListSongResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;


/**
 * Created by htsi.
 * Since: 1/5/16 on 11:38 AM
 * Project: ZMService
 */
public interface ZMService {

    @FormUrlEncoded
    @POST("/api/mobile/song/getsonginfo")
    Call<SongInfo> getSongInfoById(@Field("requestdata") String encodedID);

    @GET("/complete")
    Call<ListSongResponse> search(@QueryMap Map<String, String> options);
}
