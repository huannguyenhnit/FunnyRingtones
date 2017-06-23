package com.coderalone.admin.funnyringtones.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.coderalone.admin.funnyringtones.model.RingtoneEntity;
import com.coderalone.admin.funnyringtones.reponsitory.BaseReponsitory;
import com.coderalone.admin.funnyringtones.reponsitory.RingtoneReponsitory;
import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class RingtonePresenter extends BasePresenter {

    private RingtoneReponsitory mRingtoneReponsitory;
    private PresenterListener.RingtonePresenterListener mRingtonePresenterListener;
    private Context mContext;
    private List<RingtoneEntity> mRingtoneList;

    /**
     * Load ringtone list data from API url.
     *
     * @param subCategoryId
     * @param page
     */
    public void loadRingtoneListData(int subCategoryId, int page) {
        mRingtonePresenterListener.showLoadingProgress();
        mRingtoneReponsitory.sendStringRequest(this, String.format(Constant.RINGTONE_LIST_API_URL, subCategoryId, page));
    }

    /**
     * Called when user click to item on the ringtone list.
     *
     * @param position position clicked item .
     */
    public void onClickItem(int position) {
        RingtoneEntity ringtone;
        if (position > mRingtoneList.size() || position < 0) {
            ringtone = null;
        } else {
            ringtone = mRingtoneList.get(position);
        }
        mRingtonePresenterListener.onClickItem(ringtone);
    }

    @Override
    public void init(BaseReponsitory reponsitory, PresenterListener presenterListener, Context context) {
        this.mRingtoneReponsitory = (RingtoneReponsitory) reponsitory;
        this.mRingtonePresenterListener = (PresenterListener.RingtonePresenterListener) presenterListener;
        this.mContext = context;
        mRingtoneList = new ArrayList<>();
    }

    @Override
    public void loadDataSuccess(String json) {
        //Check json data is null or empty.
        if (TextUtils.isEmpty(json)) {
            mRingtonePresenterListener.loadDataError();
            return;
        }
        //Check json regex.
        if (!Utils.checkJsonRegex(json, Constant.ID, Constant.NAME)) {
            mRingtonePresenterListener.loadDataError();
            return;
        }
        try {
            ArrayList ringtoneList = super.loadDataSuccess(json, RingtoneEntity.class);
            //Check data is null or empty.
            if (ringtoneList == null || ringtoneList.isEmpty()) {
                mRingtonePresenterListener.loadDataError();
            }
            mRingtoneList.addAll(ringtoneList);

//            if (mRingtoneList.size() <= 7) {
//                mRingtoneList.add(new RingtoneEntity(true));
//            }

            mRingtonePresenterListener.loadDataSuccess((ArrayList) mRingtoneList);

        } catch (IllegalAccessException e) {
            mRingtonePresenterListener.loadDataError();
        } catch (InstantiationException e) {
            mRingtonePresenterListener.loadDataError();
        }
    }

    @Override
    public void loadDataError() {
        mRingtonePresenterListener.loadDataError();
    }

}

