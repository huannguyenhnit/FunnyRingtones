package com.coderalone.admin.funnyringtones.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.coderalone.admin.funnyringtones.R;
import com.coderalone.admin.funnyringtones.app.MyApplication;
import com.coderalone.admin.funnyringtones.model.RingtoneEntity;
import com.coderalone.admin.funnyringtones.model.SubCategoryEntity;
import com.coderalone.admin.funnyringtones.presenter.PresenterListener;
import com.coderalone.admin.funnyringtones.presenter.RingtonePresenter;
import com.coderalone.admin.funnyringtones.reponsitory.RingtoneReponsitory;
import com.coderalone.admin.funnyringtones.util.CallBackToRingtoneListActivityInterface;
import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.RecyclerViewOnScrollListener;
import com.coderalone.admin.funnyringtones.util.Utils;
import com.coderalone.admin.funnyringtones.view.adapter.RingtoneAdapter;
import com.coderalone.admin.funnyringtones.view.customview.ProgressDialogCustom;
import com.coderalone.admin.funnyringtones.view.fragment.AudioPlayFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class RingtoneListActivity extends AppCompatActivity implements PresenterListener.RingtonePresenterListener, View.OnClickListener, CallBackToRingtoneListActivityInterface {

    private SubCategoryEntity mSubCategoryEntity;
    private int mSubCategoryId;
    private ActionBar mActionBar;
    private ProgressDialogCustom mProgress;
    private RecyclerView mRecyclerView;
    private int mPage = 1;
    private RecyclerViewOnScrollListener mRecyclerViewOnScrollListener;
    private RingtoneAdapter mRingtoneAdapter;
    private RingtonePresenter mRingtonePresenter;
    private AudioPlayFragment mAudioPlayFragment;
    private AdView mAdViewBanner;
    private AdRequest mAdRequest;

    public static Intent getNewActivityStartIntent(Context context, SubCategoryEntity subCategoryEntity) {
        Intent intent = new Intent(context, RingtoneListActivity.class);
        intent.putExtra(Constant.SUB_CATEGORY, subCategoryEntity);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone_list);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_DISCONNECT_CODE) {
            if (resultCode == Constant.RESULT_CODE_OK) {
                //Load ads
                mAdViewBanner.loadAd(mAdRequest);
                //Re-load data from url
                mSubCategoryEntity = getIntent().getParcelableExtra(Constant.SUB_CATEGORY);
                mSubCategoryId = mSubCategoryEntity.getId();
                mPage = 1;
                loadRingtoneListData();
            }
        }
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
        if (mProgress.isShowing()) {
            mProgress.dismiss();
        }

        if (mAdViewBanner != null) {
            mAdViewBanner.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void showLoadingProgress() {
        mProgress.show();
    }

    @Override
    public void loadDataSuccess(ArrayList ringtoneList) {
        List<RingtoneEntity> mRingtoneList = ringtoneList;
        mRingtoneAdapter.setRingtoneList(mRingtoneList);
        //Refresh recycler view.
        mRingtoneAdapter.notifyDataSetChanged();
        //Calculator the page number.
        RingtoneEntity ringtoneEntity = (RingtoneEntity) ringtoneList.get(0);
        mRecyclerViewOnScrollListener.setTotalPages((int) Math.ceil((float) ringtoneEntity.getTotalItem() / 10f));
        if (mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    @Override
    public void loadDataError() {
        Intent intent = new Intent(RingtoneListActivity.this, CategoryListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.SERVER_MAINTENANCE, getString(R.string.server_maintenance));
        startActivity(intent);
    }

    @Override
    public void onClickItem(Object item) {
        mAdViewBanner.setVisibility(View.GONE);
        RingtoneEntity ringtoneEntity = (RingtoneEntity) item;

        if (mAudioPlayFragment == null) {
            mAudioPlayFragment = AudioPlayFragment.newInstance();
            mAudioPlayFragment.setSubCategoryEntity(mSubCategoryEntity);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.audio_play_frame_layout, mAudioPlayFragment);
            transaction.commit();
        }
        mAudioPlayFragment.setRingtoneEntity(ringtoneEntity);
        mAudioPlayFragment.setCallBackToRingtoneListActivityInterface(this);
        mAudioPlayFragment.startPlayMusic();
    }

    /**
     * Navigation up button click event.
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        super.onBackPressed();
    }

    @Override
    public void isDestroyAudioPlayFragment() {
        mAudioPlayFragment = null;
    }


    ////////////////////////////////////////////////////////////////////////////
    // private methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Init view
     */
    private void initView() {

        mSubCategoryEntity = getIntent().getParcelableExtra(Constant.SUB_CATEGORY);
        if (mSubCategoryEntity == null) {
            return;
        }

        initAppBar();
        mProgress = new ProgressDialogCustom(this);

        mAdViewBanner = (AdView) findViewById(R.id.ringtone_list_av_banner);
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice("335E1A23BDB625D0E0349DCC0939B5E0");
        mAdRequest = adRequestBuilder.build();

        mSubCategoryId = mSubCategoryEntity.getId();
        mActionBar.setTitle(mSubCategoryEntity.getName());
        mRingtonePresenter = new RingtonePresenter();
        mRingtonePresenter.init(new RingtoneReponsitory(), this, RingtoneListActivity.this);

        //Init RecyclerView.
        mRecyclerView = (RecyclerView) findViewById(R.id.ringtone_list_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getInstance().getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //Init Adapter.
        mRingtoneAdapter = new RingtoneAdapter(mRingtonePresenter, RingtoneListActivity.this);
        mRingtoneAdapter.setRingtoneList(new ArrayList<RingtoneEntity>());
        //Set Adapter for RecyclerView.
        mRecyclerView.setAdapter(mRingtoneAdapter);
        // Init OnScrollLister to receive event load more.
        mRecyclerViewOnScrollListener = new RecyclerViewOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                mPage = currentPage;
                //Check internet before load data.
                if (Utils.isConnectInternet(MyApplication.getInstance().getApplicationContext())) {
                    loadRingtoneListData();
                } else {
                    Intent intent = DisconnectActivity.getNewActivityStartIntent(RingtoneListActivity.this);
                    intent.putExtra(Constant.SUB_CATEGORY, mSubCategoryEntity);
                    startActivityForResult(intent, Constant.REQUEST_DISCONNECT_CODE);
                }
            }

        };
        mRecyclerViewOnScrollListener.setLinearLayoutManager(true);
        // Add scroll listener for recycler view.
        mRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        //Check internet before load data.
        if (Utils.isConnectInternet(MyApplication.getInstance().getApplicationContext())) {
            //Load ads
            mAdViewBanner.loadAd(mAdRequest);
            loadRingtoneListData();
        } else {
            Intent intent = DisconnectActivity.getNewActivityStartIntent(RingtoneListActivity.this);
            intent.putExtra(Constant.SUB_CATEGORY, mSubCategoryEntity);
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
    private void loadRingtoneListData() {
        mRingtonePresenter.loadRingtoneListData(mSubCategoryId, mPage);
    }

}