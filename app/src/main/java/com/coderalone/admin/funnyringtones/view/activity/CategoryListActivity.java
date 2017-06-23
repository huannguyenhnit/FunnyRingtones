package com.coderalone.admin.funnyringtones.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.coderalone.admin.funnyringtones.R;
import com.coderalone.admin.funnyringtones.app.MyApplication;
import com.coderalone.admin.funnyringtones.model.CategoryEntity;
import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CategoryListActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mCartoonImg;
    private ImageView mGameImg;
    private ImageView mMovieImg;
    private ImageView mMusicImg;
    private AdView mAdViewBanner;
    private AdRequest mAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        String serverMaintenace = getIntent().getStringExtra(Constant.SERVER_MAINTENANCE);
        if (serverMaintenace != null) {
            Toast.makeText(this, serverMaintenace, Toast.LENGTH_SHORT).show();
        }

        initView();
    }


    ////////////////////////////////////////////////////////////////////////////
    // public methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPause() {
        if (mAdViewBanner != null) {
            mAdViewBanner.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdViewBanner != null) {
            mAdViewBanner.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdViewBanner != null) {
            mAdViewBanner.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        CategoryEntity categoryEntity = new CategoryEntity();
        switch (v.getId()) {
            case R.id.category_cartoon_img:
                categoryEntity.setCategoryId(Constant.CARTOON_ID);
                categoryEntity.setCategoryName(Constant.CARTOON);
                gotoSubCategory(categoryEntity);
                break;
            case R.id.category_game_img:
                categoryEntity.setCategoryId(Constant.GAME_ID);
                categoryEntity.setCategoryName(Constant.GAME);
                gotoSubCategory(categoryEntity);
                break;
            case R.id.category_movie_img:
                categoryEntity.setCategoryId(Constant.MOVIE_ID);
                categoryEntity.setCategoryName(Constant.MOVIE);
                gotoSubCategory(categoryEntity);
                break;
            case R.id.category_music_img:
                categoryEntity.setCategoryId(Constant.MUSIC_ID);
                categoryEntity.setCategoryName(Constant.MUSIC);
                gotoSubCategory(categoryEntity);
                break;
            default:
                break;
        }

    }


    ////////////////////////////////////////////////////////////////////////////
    // private methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Init view.
     */
    private void initView() {
        //Init toolbar
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(Constant.CATEGORY);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        mCartoonImg = (ImageView) findViewById(R.id.category_cartoon_img);
        mCartoonImg.setOnClickListener(this);
        mGameImg = (ImageView) findViewById(R.id.category_game_img);
        mGameImg.setOnClickListener(this);
        mMovieImg = (ImageView) findViewById(R.id.category_movie_img);
        mMovieImg.setOnClickListener(this);
        mMusicImg = (ImageView) findViewById(R.id.category_music_img);
        mMusicImg.setOnClickListener(this);

        mAdViewBanner = (AdView) findViewById(R.id.category_av_banner);
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice("335E1A23BDB625D0E0349DCC0939B5E0");
        mAdRequest = adRequestBuilder.build();
        //Check internet before load ads.
        if (Utils.isConnectInternet(MyApplication.getInstance().getApplicationContext())) {
            mAdViewBanner.loadAd(mAdRequest);
        }
    }

    private void gotoSubCategory(CategoryEntity categoryEntity) {
        Intent intent = SubCategoryListActivity.getNewActivityStartIntent(CategoryListActivity.this, categoryEntity);
        startActivity(intent);
    }

}
