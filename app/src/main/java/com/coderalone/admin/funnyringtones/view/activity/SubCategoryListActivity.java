package com.coderalone.admin.funnyringtones.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.coderalone.admin.funnyringtones.R;
import com.coderalone.admin.funnyringtones.app.MyApplication;
import com.coderalone.admin.funnyringtones.model.CategoryEntity;
import com.coderalone.admin.funnyringtones.model.SubCategoryEntity;
import com.coderalone.admin.funnyringtones.presenter.PresenterListener;
import com.coderalone.admin.funnyringtones.presenter.SubCategoryPresenter;
import com.coderalone.admin.funnyringtones.reponsitory.SubCategoryReponsitory;
import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.RecyclerViewOnScrollListener;
import com.coderalone.admin.funnyringtones.util.Utils;
import com.coderalone.admin.funnyringtones.view.adapter.SubCategoryAdapter;
import com.coderalone.admin.funnyringtones.view.customview.ProgressDialogCustom;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SubCategoryListActivity extends AppCompatActivity implements PresenterListener.SubCategoryPresenterListener, View.OnClickListener {

    private CategoryEntity mCategoryEntity;
    private int mCategoryId;
    private ActionBar mActionBar;
    private ProgressDialogCustom mProgress;
    private RecyclerView mRecyclerView;
    private int mPage = 1;
    private RecyclerViewOnScrollListener mRecyclerViewOnScrollListener;
    private SubCategoryAdapter mSubCategoryAdapter;
    private SubCategoryPresenter mSubCategoryPresenter;
    private InterstitialAd mInterstitialAd;
    private AdRequest mAdRequest;
    private Intent mRingtoneListIntent;

    public static Intent getNewActivityStartIntent(Context context, CategoryEntity categoryEntity) {
        Intent intent = new Intent(context, SubCategoryListActivity.class);
        intent.putExtra(Constant.CATEGORY, categoryEntity);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_list);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_DISCONNECT_CODE) {
            if (resultCode == Constant.RESULT_CODE_OK) {
                //Re-load data from url
                mCategoryEntity = getIntent().getParcelableExtra(Constant.CATEGORY);
                mCategoryId = mCategoryEntity.getCategoryId();
                mPage = 1;
                loadSubCategoryListData();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mProgress.isShowing()) {
            mProgress.dismiss();
        }
        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////
    // public methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void showLoadingProgress() {
        mProgress.show();
    }

    @Override
    public void loadDataSuccess(ArrayList subCategoryList) {
        List<SubCategoryEntity> mSubCategoryList = subCategoryList;
        mSubCategoryAdapter.setSubCategoryList(mSubCategoryList);
        //Refresh recycler view.
        mSubCategoryAdapter.notifyDataSetChanged();
        //Calculator the page number.
        SubCategoryEntity subCategoryEntity = (SubCategoryEntity) mSubCategoryList.get(0);
        mRecyclerViewOnScrollListener.setTotalPages((int) Math.ceil((float) subCategoryEntity.getTotalItem() / 10f));
        if (mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    @Override
    public void loadDataError() {
        Intent intent = new Intent(SubCategoryListActivity.this, CategoryListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.SERVER_MAINTENANCE, getString(R.string.server_maintenance));
        startActivity(intent);
    }

    @Override
    public void onClickItem(Object item) {
        mProgress.show();
        Random random = new Random();
        int showAdsFlag = random.nextInt(3);
        if (showAdsFlag == 1) {
            //Load ads
            mInterstitialAd.loadAd(mAdRequest);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                }
            });
        }

        SubCategoryEntity subCategoryEntity = (SubCategoryEntity) item;
        mRingtoneListIntent = RingtoneListActivity.getNewActivityStartIntent(SubCategoryListActivity.this, subCategoryEntity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mProgress.isShowing()) {
                    mProgress.dismiss();
                }
                startActivity(mRingtoneListIntent);
            }
        }, 2000);

    }

    /**
     * Navigation button click event.
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        super.onBackPressed();
    }


    ////////////////////////////////////////////////////////////////////////////
    // private methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Init view.
     */
    private void initView() {

        mCategoryEntity = getIntent().getParcelableExtra(Constant.CATEGORY);
        if (mCategoryEntity == null) {
            return;
        }

        initAppBar();
        mProgress = new ProgressDialogCustom(this);

        mInterstitialAd = new InterstitialAd(SubCategoryListActivity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.ads_unit_interstitial));
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice("335E1A23BDB625D0E0349DCC0939B5E0");
        mAdRequest = adRequestBuilder.build();

        mCategoryId = mCategoryEntity.getCategoryId();
        mSubCategoryPresenter = new SubCategoryPresenter();
        mSubCategoryPresenter.init(new SubCategoryReponsitory(), this, this);

        mActionBar.setTitle(mCategoryEntity.getCategoryName());
        //Init RecyclerView.
        mRecyclerView = (RecyclerView) findViewById(R.id.sub_category_list_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //Init Adapter.
        mSubCategoryAdapter = new SubCategoryAdapter(mSubCategoryPresenter, this);
        mSubCategoryAdapter.setSubCategoryList(new ArrayList<SubCategoryEntity>());
        //Set Adapter for RecyclerView.
        mRecyclerView.setAdapter(mSubCategoryAdapter);
        // Init OnScrollLister to receive event load more.
        mRecyclerViewOnScrollListener = new RecyclerViewOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                mPage = currentPage;
                //Check internet before load data.
                if (Utils.isConnectInternet(MyApplication.getInstance().getApplicationContext())) {
                    loadSubCategoryListData();
                } else {
                    Intent intent = DisconnectActivity.getNewActivityStartIntent(SubCategoryListActivity.this);
                    intent.putExtra(Constant.CATEGORY, mCategoryEntity);
                    startActivityForResult(intent, Constant.REQUEST_DISCONNECT_CODE);
                }
            }

        };
        mRecyclerViewOnScrollListener.setLinearLayoutManager(false);
        // Add scroll listener for recycler view.
        mRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        //Check internet before load data.
        if (Utils.isConnectInternet(MyApplication.getInstance().getApplicationContext())) {
            loadSubCategoryListData();
        } else {
            Intent intent = DisconnectActivity.getNewActivityStartIntent(SubCategoryListActivity.this);
            intent.putExtra(Constant.CATEGORY, mCategoryEntity);
            startActivityForResult(intent, Constant.REQUEST_DISCONNECT_CODE);
        }

    }

    /**
     * Init app bar.
     */
    private void initAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);
    }

    /**
     * Load data from url
     */
    private void loadSubCategoryListData() {
        mSubCategoryPresenter.loadSubCategoryListData(mCategoryId, mPage);
    }

}