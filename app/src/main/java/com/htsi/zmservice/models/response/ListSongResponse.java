package com.htsi.zmservice.models.response;

import com.google.gson.annotations.SerializedName;
import com.htsi.zmservice.models.SongInfo;

import java.util.List;

/**
 * Created by htsi.
 * Since: 4/7/16 on 12:12 PM
 * Project: OMusico
 */
public class ListSongResponse {

    @SerializedName("result")
    private String mResult;
    @SerializedName("data")
    private List<TypeSong> mTypeSongs;

    public String getResult() {
        return mResult;
    }

    public List<TypeSong> getTypeSongs() {
        return mTypeSongs;
    }

    public class TypeSong {

        @SerializedName("song")
        private List<SongInfo> mSongInfos;

        public List<SongInfo> getSongInfos() {
            return mSongInfos;
        }
    }
}
