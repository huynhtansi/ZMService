package com.htsi.zmservice;

import com.google.gson.annotations.SerializedName;

/**
 * Created by htsi.
 * Since: 1/5/16 on 6:00 PM
 * Project: ZMService
 */
public class SongInfo {

    @SerializedName("id")
    private String id;

    @SerializedName("song_id")
    private String songId;

    @SerializedName("title")
    private String title;

    @SerializedName("name")
    private String name;

    @SerializedName("artist")
    private String artist;

    @SerializedName("composer")
    private String composer;

    @SerializedName("source")
    private LinkDown linkDown;

    @SerializedName("thumbnail")
    private String thumbnail;

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public LinkDown getLinkDown() {
        return linkDown;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSongId() {
        return songId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public class LinkDown {

        @SerializedName("128")
        private String link128;

        @SerializedName("320")
        private String link320;

        @SerializedName("lossless")
        private String linklossless;

        public String getLink128() {
            return link128;
        }

        public void setLink128(String link128) {
            this.link128 = link128;
        }

        public String getLink320() {
            return link320;
        }

        public void setLink320(String link320) {
            this.link320 = link320;
        }

        public String getLinklossless() {
            return linklossless;
        }

        public void setLinklossless(String linklossless) {
            this.linklossless = linklossless;
        }
    }
}
