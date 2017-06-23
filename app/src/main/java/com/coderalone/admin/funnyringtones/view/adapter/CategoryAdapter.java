package com.coderalone.admin.funnyringtones.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.coderalone.admin.funnyringtones.R;
import com.coderalone.admin.funnyringtones.model.CategoryEntity;
import com.coderalone.admin.funnyringtones.presenter.CategoryPresenter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements View.OnClickListener {

    private CategoryPresenter mCategoryPresenter;
    private List<CategoryEntity> mCategoryList;
    private Context mContext;

    public void setCategoryList(List<CategoryEntity> mCategoryList) {
        this.mCategoryList = mCategoryList;
    }

    public CategoryAdapter(CategoryPresenter mCategoryPresenter, Context mContext) {
        this.mCategoryPresenter = mCategoryPresenter;
        this.mContext = mContext;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_list_item,
                parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        CategoryEntity categoryEntity = mCategoryList.get(position);
        String imageUrl = categoryEntity.getCategoryImage();
        Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(R.mipmap.no_image)
                .error(R.mipmap.no_image)
                .into(holder.mImageView);

        holder.mRelativeLayout.setTag(position);
        holder.mRelativeLayout.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {

        return mCategoryList == null ? 0 : mCategoryList.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.category_list_item:
                if (view.getTag() != null) {
                    int position = (int) view.getTag();
                    mCategoryPresenter.onClickItem(position);
                }
                break;
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView mImageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.category_list_item);
            mImageView = (ImageView) itemView.findViewById(R.id.category_list_item_img);
        }
    }

}