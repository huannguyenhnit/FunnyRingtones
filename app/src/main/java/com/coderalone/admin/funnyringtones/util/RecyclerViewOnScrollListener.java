package com.coderalone.admin.funnyringtones.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerViewOnScrollListener extends
        RecyclerView.OnScrollListener {

    private boolean mLoading = false;
    private int mTotalPages;
    private int mCurrentPage = 1;
    private int mTotalItemCount;
    private int mLastItemVisible;
    private int mLastItemEnable;
    private boolean isLinearLayoutManager;

    public void setLinearLayoutManager(boolean linearLayoutManager) {
        isLinearLayoutManager = linearLayoutManager;
    }

    public void setTotalPages(int mTotalPages) {
        this.mTotalPages = mTotalPages;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (isLinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            mTotalItemCount = linearLayoutManager.getItemCount();
            mLastItemVisible = linearLayoutManager.findLastVisibleItemPosition();
        }else {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            mTotalItemCount = gridLayoutManager.getItemCount();
            mLastItemVisible = gridLayoutManager.findLastVisibleItemPosition();
        }

        if (!mLoading && (mLastItemVisible == mTotalItemCount - 1) && (mLastItemEnable != mLastItemVisible) && (mCurrentPage < mTotalPages)) {
            mLoading = true;
        }

        if (mLoading) {
            mLastItemEnable = mLastItemVisible;
            mLoading = false;
            mCurrentPage++;
            onLoadMore(mCurrentPage);
        }

    }

    /**
     * Load more list
     *
     * @param currentPage
     */
    public abstract void onLoadMore(int currentPage);

}