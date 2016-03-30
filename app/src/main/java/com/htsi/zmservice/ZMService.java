package com.htsi.zmservice;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by htsi on 1/5/16.
 */
public interface ZMService {

    @FormUrlEncoded
    @POST("/api/mobile/song/getsonginfo")
    Call<SongInfo> getSongInfoById(@Field("requestdata") String encodedID);

}
