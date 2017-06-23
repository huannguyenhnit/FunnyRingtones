package com.coderalone.admin.funnyringtones.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.coderalone.admin.funnyringtones.model.SubCategoryEntity;
import com.coderalone.admin.funnyringtones.reponsitory.BaseReponsitory;
import com.coderalone.admin.funnyringtones.reponsitory.SubCategoryReponsitory;
import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryPresenter extends BasePresenter {

    private SubCategoryReponsitory mSubCategoryReponsitory;
    private PresenterListener.SubCategoryPresenterListener mSubCategoryPresenterListener;
    private Context mContext;
    private List<SubCategoryEntity> mSubCategoryList;

    @Override
    public void init(BaseReponsitory reponsitory, PresenterListener presenterListener, Context context) {
        this.mSubCategoryReponsitory = (SubCategoryReponsitory) reponsitory;
        this.mSubCategoryPresenterListener = (PresenterListener.SubCategoryPresenterListener) presenterListener;
        this.mContext = context;
        mSubCategoryList = new ArrayList<>();
    }

    /**
     * Load sub-category list data from API url.
     */
    public void loadSubCategoryListData(int categoryId, int page) {
        mSubCategoryPresenterListener.showLoadingProgress();
        mSubCategoryReponsitory.sendStringRequest(this, String.format(Constant.SUB_CATEGORY_LIST_API_URL, categoryId, page));
    }

    @Override
    public void loadDataSuccess(String json) {
        //Check json data is null or empty.
        if (TextUtils.isEmpty(json)) {
            mSubCategoryPresenterListener.loadDataError();
            return;
        }
        //Check json regex.
        if (!Utils.checkJsonRegex(json, Constant.ID, Constant.IMAGE, Constant.NAME)) {
            mSubCategoryPresenterListener.loadDataError();
            return;
        }
        try {
            ArrayList categoryList = super.loadDataSuccess(json, SubCategoryEntity.class);
            //Check data is null or empty.
            if (categoryList == null || categoryList.isEmpty()) {
                mSubCategoryPresenterListener.loadDataError();
                return;
            }
            mSubCategoryList.addAll(categoryList);
            mSubCategoryPresenterListener.loadDataSuccess((ArrayList) mSubCategoryList);

        } catch (IllegalAccessException e) {
            mSubCategoryPresenterListener.loadDataError();
        } catch (InstantiationException e) {
            mSubCategoryPresenterListener.loadDataError();
        }
    }

    @Override
    public void loadDataError() {
        mSubCategoryPresenterListener.loadDataError();
    }

    /**
     * Called when user click to item on the sub-category list.
     *
     * @param position
     */
    public void onClickItem(int position) {
        SubCategoryEntity subCategoryEntity;
        if (position > mSubCategoryList.size() || position < 0) {
            subCategoryEntity = null;
        } else {
            subCategoryEntity = mSubCategoryList.get(position);
        }
        mSubCategoryPresenterListener.onClickItem(subCategoryEntity);
    }
}
