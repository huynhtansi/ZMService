/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htsi.zmservice.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.htsi.zmservice.R;
import com.htsi.zmservice.models.SongInfo;
import com.htsi.zmservice.models.response.ListSongResponse;
import com.htsi.zmservice.service.ServiceGenerator;
import com.htsi.zmservice.service.ZMService;
import com.htsi.zmservice.ui.adapter.ListSongAdapter;
import com.htsi.zmservice.utils.AnimUtils;
import com.htsi.zmservice.utils.BaselineGridTextView;
import com.htsi.zmservice.utils.ImeUtils;
import com.htsi.zmservice.utils.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends Activity {

    public static final String EXTRA_MENU_LEFT = "EXTRA_MENU_LEFT";
    public static final String EXTRA_MENU_CENTER_X = "EXTRA_MENU_CENTER_X";

    @BindView(R.id.container)
    ViewGroup mContainer;

    @BindView(R.id.search_view)
    SearchView mSearchView;

    @BindView(R.id.searchback_container)
    ViewGroup mSearchBackContainer;

    @BindView(R.id.searchback)
    ImageButton mSearchBack;

    @BindView(R.id.search_background)
    View mSearchBackground;

    @BindView(R.id.search_toolbar)
    ViewGroup mSearchToolbar;

    @BindView(R.id.scrim)
    View mScrim;

    @BindView(R.id.search_results)
    ListView mListSong;

    @BindView(android.R.id.empty)
    ProgressBar mProgressBar;

    @BindView(R.id.results_container)
    ViewGroup mResultsContainer;

    BaselineGridTextView mNoResults;

    private Transition auto;

    private int mSearchBackDistanceX;
    private int mSearchIconCenterX;

    private ZMService mZMService;


    public static Intent createStartIntent(Context context, int menuIconLeft, int menuIconCenterX) {
        Intent starter = new Intent(context, SearchActivity.class);
        starter.putExtra(EXTRA_MENU_LEFT, menuIconLeft);
        starter.putExtra(EXTRA_MENU_CENTER_X, menuIconCenterX);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupSearchView();

        mZMService = ServiceGenerator.createService(ZMService.class, true);
        // extract the search icon's location passed from the launching activity, minus 4dp to
        // compensate for different paddings in the views
        mSearchBackDistanceX = getIntent().getIntExtra(EXTRA_MENU_LEFT, 0) - (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mSearchIconCenterX = getIntent().getIntExtra(EXTRA_MENU_CENTER_X, 0);
        // translate icon to match the launching screen then animate back into position
        mSearchBackContainer.setTranslationX(mSearchBackDistanceX);
        mSearchBackContainer.animate()
                .translationX(0f)
                .setDuration(650L)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        // transform from search icon to back icon
        AnimatedVectorDrawable searchToBack = (AnimatedVectorDrawable) ContextCompat
                .getDrawable(this, R.drawable.avd_search_to_back);
        mSearchBack.setImageDrawable(searchToBack);
        searchToBack.start();
        // for some reason the animation doesn't always finish (leaving a part arrow!?) so after
        // the animation set a static drawable. Also animation callbacks weren't added until API23
        // so using post delayed :(
        // TODO fix properly!!
        mSearchBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchBack.setImageDrawable(ContextCompat.getDrawable(SearchActivity.this,
                        R.drawable.ic_arrow_back_padded));
            }
        }, 600L);

        // fade in the other search chrome
        /*mSearchBackground.animate()
                .alpha(1f)
                .setDuration(300L)
                .setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(this));*/
        mSearchView.animate()
                .alpha(1f)
                .setStartDelay(400L)
                .setDuration(400L)
                .setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(this))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSearchView.requestFocus();
                        ImeUtils.showIme(mSearchView);
                    }
                });

        // animate in a scrim over the content behind
        mScrim.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mScrim.getViewTreeObserver().removeOnPreDrawListener(this);
                AnimatorSet showScrim = new AnimatorSet();
                showScrim.playTogether(
                        ViewAnimationUtils.createCircularReveal(
                                mScrim,
                                mSearchIconCenterX,
                                mSearchBackground.getBottom(),
                                0,
                                (float) Math.hypot(mSearchBackDistanceX, mScrim.getHeight()
                                        - mSearchBackground.getBottom())),
                        ObjectAnimator.ofArgb(
                                mScrim,
                                ViewUtils.BACKGROUND_COLOR,
                                Color.TRANSPARENT,
                                ContextCompat.getColor(SearchActivity.this, R.color.scrim)));
                showScrim.setDuration(400L);
                showScrim.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(SearchActivity
                        .this));
                showScrim.start();
                return false;
            }
        });

        //onNewIntent(getIntent());
        mListSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongInfo songInfo = (SongInfo) mListSong.getAdapter().getItem(position);
                Intent intent = new Intent();
                intent.putExtra("SongID", songInfo.getId());
                setResult(RESULT_OK, intent);
                dismiss();
            }
        });
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchView.setImeOptions(mSearchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ImeUtils.hideIme(mSearchView);
                mSearchView.clearFocus();
                mListSong.setVisibility(View.GONE);
                setNoResultsVisibility(View.GONE);

                showLoading();
                Map<String, String> options = new HashMap<>();
                options.put("num", "100");
                options.put("type", "song");
                options.put("query", query);
                Call<ListSongResponse> call = mZMService.search(options);
                call.enqueue(new Callback<ListSongResponse>() {
                    @Override
                    public void onResponse(Call<ListSongResponse> call, Response<ListSongResponse> response) {
                        hideLoading();
                        if (response.body() != null) {
                            if (response.body().getResult().equals("true")) {
                                List<SongInfo> songInfoList = response.body().getTypeSongs().get(0).getSongInfos();

                                if (songInfoList == null || songInfoList.size() == 0) {
                                    setNoResultsVisibility(View.VISIBLE);
                                    return;
                                }
                                mListSong.setVisibility(View.VISIBLE);
                                mListSong.setAdapter(new ListSongAdapter(SearchActivity.this, songInfoList));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ListSongResponse> call, Throwable t) {

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }
        });
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /*if (hasFocus && confirmSaveContainer.getVisibility() == View.VISIBLE) {
                    hideSaveConfimation();
                }*/
            }
        });
    }

    public void showLoading() {
        TransitionManager.beginDelayedTransition(mContainer, auto);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        TransitionManager.beginDelayedTransition(mContainer, auto);
        mProgressBar.setVisibility(View.GONE);
    }

    private void setNoResultsVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (mNoResults == null) {
                ViewStub viewStub = (ViewStub) findViewById(R.id.stub_no_search_results);
                if (viewStub != null)
                    mNoResults = (BaselineGridTextView) viewStub.inflate();
                if (mNoResults != null) {
                    mNoResults.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSearchView.setQuery("", false);
                            mSearchView.requestFocus();
                            ImeUtils.showIme(mSearchView);
                        }
                    });
                }
            }
            String message = String.format(getString(R
                    .string.no_search_results), mSearchView.getQuery().toString());
            SpannableStringBuilder ssb = new SpannableStringBuilder(message);
            ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                    message.indexOf('“') + 1,
                    message.length() - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mNoResults.setText(ssb);
        }
        if (mNoResults != null) {
            mNoResults.setVisibility(visibility);
        }
    }


    @Override
    public void onBackPressed() {
        dismiss();
    }

    @OnClick({ R.id.scrim, R.id.searchback })
    protected void dismiss() {
        //if (dismissing) return;
        //dismissing = true;

        // translate the icon to match position in the launching activity
        mSearchBackContainer.animate()
                .translationX(mSearchBackDistanceX)
                .setDuration(600L)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finishAfterTransition();
                    }
                })
                .start();
        // transform from back icon to search icon
        AnimatedVectorDrawable backToSearch = (AnimatedVectorDrawable) ContextCompat
                .getDrawable(this, R.drawable.avd_back_to_search);
        mSearchBack.setImageDrawable(backToSearch);
        // clear the background else the touch ripple moves with the translation which looks bad
        mSearchBack.setBackground(null);
        backToSearch.start();
        // fade out the other search chrome
        mSearchView.animate()
                .alpha(0f)
                .setStartDelay(0L)
                .setDuration(120L)
                .setInterpolator(AnimUtils.getFastOutLinearInInterpolator(this))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // prevent clicks while other anims are finishing
                        mSearchView.setVisibility(View.INVISIBLE);
                    }
                })
                .start();
        mSearchBackground.animate()
                .alpha(0f)
                .setStartDelay(300L)
                .setDuration(160L)
                .setInterpolator(AnimUtils.getFastOutLinearInInterpolator(this))
                .setListener(null)
                .start();
        if (mSearchToolbar.getZ() != 0f) {
            mSearchToolbar.animate()
                    .z(0f)
                    .setDuration(600L)
                    .setInterpolator(AnimUtils.getFastOutLinearInInterpolator(this))
                    .start();
        }
        // if we're showing search results, circular hide them
        if (mResultsContainer.getHeight() > 0) {
            Animator closeResults = ViewAnimationUtils.createCircularReveal(
                    mResultsContainer,
                    mSearchIconCenterX,
                    0,
                    (float) Math.hypot(mSearchIconCenterX, mResultsContainer.getHeight()),
                    0f);
            closeResults.setDuration(500L);
            closeResults.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(SearchActivity
                    .this));
            closeResults.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mResultsContainer.setVisibility(View.INVISIBLE);
                }
            });
            closeResults.start();
        }

        // fade out the scrim
        mScrim.animate()
                .alpha(0f)
                .setDuration(400L)
                .setInterpolator(AnimUtils.getFastOutLinearInInterpolator(this))
                .setListener(null)
                .start();
    }

    @Override
    protected void onPause() {
        // needed to suppress the default window animation when closing the activity
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
