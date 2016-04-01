package com.htsi.zmservice;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.cover)
    ImageView imgCover;

    @Bind(R.id.loading)
    ProgressBar pbLoading;

    @Bind(R.id.title)
    TextView tvTitle;

    @Bind(R.id.artist)
    TextView tvArtist;

    @Bind(R.id.contentInfo)
    View contentInfo;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private SongInfo mSongInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getIntentData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                // get the icon's location on screen to pass through to the search screen
                View searchMenuView = mToolbar.findViewById(R.id.menu_search);
                int[] loc = new int[2];
                searchMenuView.getLocationOnScreen(loc);
                startActivityForResult(SearchActivity.createStartIntent(this, loc[0], loc[0] +
                        (searchMenuView.getWidth() / 2)), 0, ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());
                searchMenuView.setAlpha(0f);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                // reset the search icon which we hid
                View searchMenuView = mToolbar.findViewById(R.id.menu_search);
                if (searchMenuView != null) {
                    searchMenuView.setAlpha(1f);
                }
                break;
        }
    }

    private void getIntentData() {
        /*Intent intent = getIntent();
        String encodedSongId;
        if (intent.getExtras() != null) {
            String mSongURL = intent.getExtras().getString(Intent.EXTRA_TEXT);
            if (mSongURL != null) {
                encodedSongId = mSongURL.replaceFirst(".*//*([^/?]+).*", "$1").replace(".html", "");
                Log.d("HTSI", encodedSongId);
                getSongData(encodedSongId);
            }
        } else {

        }*/
        getSongData("ZW7OZ668");
    }

    private void getSongData(String encodedSongId) {
        String formattedID = "{\"id\":\"" + encodedSongId + "\"}";

        ZMService zmService = ServiceGenerator.createService(ZMService.class);
        Call<SongInfo> call = zmService.getSongInfoById(formattedID);

        call.enqueue(new Callback<SongInfo>() {
            @Override
            public void onResponse(Response<SongInfo> response, Retrofit retrofit) {
                mSongInfo = response.body();
                pbLoading.setVisibility(View.GONE);
                Log.d("HTSI", ServiceGenerator.IMAGE_BASE_URL + mSongInfo.getThumbnail());
                new AsyncTask<String, Void, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(String... params) {
                        try {
                            return Glide.with(MainActivity.this)
                                    .load(params[0])
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(-1, -1).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (bitmap != null) {
                            Palette.from(bitmap).generate(paletteAsyncListener);
                            imgCover.setImageBitmap(bitmap);
                        }
                        tvTitle.setText(mSongInfo.getTitle());
                        tvArtist.setText(mSongInfo.getArtist());
                        contentInfo.setVisibility(View.VISIBLE);
                    }
                }.execute(ServiceGenerator.IMAGE_BASE_URL + mSongInfo.getThumbnail());
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(Palette palette) {
            contentInfo.setBackgroundColor(palette.getDarkVibrantColor(Color.BLACK));
        }
    };

    public void onDownloadButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btnLink128:
                new DownloadMusicTask(MainActivity.this).execute(mSongInfo.getLinkDown().getLink128(),
                        mSongInfo.getTitle(), mSongInfo.getArtist(), mSongInfo.getSongId());
                break;
            case R.id.btnLink320:
                new DownloadMusicTask(MainActivity.this).execute(mSongInfo.getLinkDown().getLink320(),
                        mSongInfo.getTitle(), mSongInfo.getArtist(), mSongInfo.getSongId());
                break;
            case R.id.btnLinkLossless:
                new DownloadMusicTask(MainActivity.this).execute(mSongInfo.getLinkDown().getLinklossless(),
                        mSongInfo.getTitle(), mSongInfo.getArtist(), mSongInfo.getSongId());
                break;
        }
    }

}
