package com.htsi.zmservice;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by htsi.
 * Since: 4/7/16 on 12:12 PM
 * Project: OMusico
 */
public class ListSongResponse {

    @SerializedName("result")
    private String mResult;

    public String getResult() {
        return mResult;
    }

    @SerializedName("data")
    private List<TypeSong> mTypeSongs;

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
