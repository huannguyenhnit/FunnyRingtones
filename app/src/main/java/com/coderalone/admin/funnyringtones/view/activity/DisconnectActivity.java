package com.coderalone.admin.funnyringtones.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.coderalone.admin.funnyringtones.R;
import com.coderalone.admin.funnyringtones.model.CategoryEntity;
import com.coderalone.admin.funnyringtones.model.SubCategoryEntity;
import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class DisconnectActivity extends AppCompatActivity implements View.OnClickListener {
    private InterstitialAd mInterstitialAd;
    private AdRequest mAdRequest;
    private Button mBtnRetry;

    public static Intent getNewActivityStartIntent(Context context) {
        Intent intent = new Intent(context, DisconnectActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disconnect_layout);

        mInterstitialAd = new InterstitialAd(DisconnectActivity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.ads_unit_interstitial));
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice("335E1A23BDB625D0E0349DCC0939B5E0");
        mAdRequest = adRequestBuilder.build();

        mBtnRetry = (Button) findViewById(R.id.disconnect_retry_btn);
        mBtnRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (Utils.isConnectInternet(this)) {
            
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Load ads into Interstitial Ads
                    mInterstitialAd.loadAd(mAdRequest);
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            mInterstitialAd.show();
                        }
                    });

                    Intent intent = getIntent();
                    if (intent != null) {
                        if (intent.getParcelableExtra(Constant.CATEGORY) != null) {
                            CategoryEntity categoryEntity = intent.getParcelableExtra(Constant.CATEGORY);
                            intent.putExtra(Constant.CATEGORY, categoryEntity);
                        } else if (intent.getParcelableExtra(Constant.SUB_CATEGORY) != null) {
                            SubCategoryEntity subCategoryEntity = intent.getParcelableExtra(Constant.SUB_CATEGORY);
                            intent.putExtra(Constant.SUB_CATEGORY, subCategoryEntity);
                        }
                        setResult(Constant.RESULT_CODE_OK, intent);
                    } else {
                        setResult(Constant.RESULT_CODE_CANCLE, intent);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                    
                }
            }, 1000);

        }
    }
}