package com.htsi.zmservice;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by htsi.
 * Since: 4/7/16 on 3:26 PM
 * Project: OMusico
 */
public class ListSongAdapter extends BaseAdapter {

    private Context mContext;
    private List<SongInfo> mSongs;

    public ListSongAdapter(Context pContext, List<SongInfo> pSongs) {
        this.mContext = pContext;
        this.mSongs = pSongs;
    }

    @Override
    public int getCount() {
        return this.mSongs.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SongHolder songHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_song, parent, false);
            songHolder = new SongHolder(convertView);
            convertView.setTag(songHolder);
        } else {
            songHolder = (SongHolder) convertView.getTag();
        }
        SongInfo songInfo = mSongs.get(position);
        songHolder.mTextTitle.setText(songInfo.getName().trim());
        if (TextUtils.isEmpty(songInfo.getArtist()))
            songHolder.mTextArtist.setText(mContext.getString(R.string.no_name));
        else
            songHolder.mTextArtist.setText(songInfo.getArtist().trim());

        return convertView;
    }

    class SongHolder {
        @Bind(R.id.title)
        TextView mTextTitle;
        @Bind(R.id.artist)
        TextView mTextArtist;

        public SongHolder(View pView) {
            ButterKnife.bind(this, pView);
        }
    }
}
