package com.coderalone.admin.funnyringtones.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.coderalone.admin.funnyringtones.model.CategoryEntity;
import com.coderalone.admin.funnyringtones.reponsitory.BaseReponsitory;
import com.coderalone.admin.funnyringtones.reponsitory.CategoryReponsitory;
import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CategoryPresenter extends BasePresenter {

    private CategoryReponsitory mCategoryReponsitory;
    private PresenterListener.CategoryPresenterListener mCategoryPresenterListener;
    private Context mContext;
    private List<CategoryEntity> mCategoryList;

    /**
     * Load category list data from API url.
     */
    public void loadCategoryListData() {
        mCategoryPresenterListener.showLoadingProgress();
        mCategoryReponsitory.sendStringRequest(this, Constant.CATEGORY_LIST_API_URL);
    }

    /**
     * Called when user click to item on the ringtone list.
     *
     * @param position
     */
    public void onClickItem(int position) {
        CategoryEntity categoryEntity;
        if (position > mCategoryList.size() || position < 0) {
            categoryEntity = null;
        } else {
            categoryEntity = mCategoryList.get(position);
        }
        mCategoryPresenterListener.onClickItem(categoryEntity);
    }

    @Override
    public void init(BaseReponsitory reponsitory, PresenterListener presenterListener, Context context) {
        this.mCategoryReponsitory = (CategoryReponsitory) reponsitory;
        this.mCategoryPresenterListener = (PresenterListener.CategoryPresenterListener) presenterListener;
        this.mContext = context;
        mCategoryList = new ArrayList<>();
    }

    @Override
    public void loadDataSuccess(String json) {
        // Check json data is null or empty.
        if (TextUtils.isEmpty(json)) {
            mCategoryPresenterListener.loadDataError();
            return;
        }
        // Check json regex.
        if (!Utils.checkJsonRegex(json, Constant.ID, Constant.IMAGE, Constant.NAME)) {
            mCategoryPresenterListener.loadDataError();
            return;
        }
        try {
            ArrayList categoryList = super.loadDataSuccess(json, CategoryEntity.class);
            // Check data is null or empty.
            if (categoryList == null || categoryList.isEmpty()) {
                mCategoryPresenterListener.loadDataError();
                return;
            }
            mCategoryList.addAll(categoryList);
            mCategoryPresenterListener.loadDataSuccess((ArrayList) mCategoryList);
        } catch (IllegalAccessException e) {
            mCategoryPresenterListener.loadDataError();
        } catch (InstantiationException e) {
            mCategoryPresenterListener.loadDataError();
        }
    }

    @Override
    public void loadDataError() {
        mCategoryPresenterListener.loadDataError();
    }

}
