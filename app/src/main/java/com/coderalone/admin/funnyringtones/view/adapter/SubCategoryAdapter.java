package com.coderalone.admin.funnyringtones.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coderalone.admin.funnyringtones.R;
import com.coderalone.admin.funnyringtones.model.SubCategoryEntity;
import com.coderalone.admin.funnyringtones.presenter.SubCategoryPresenter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private SubCategoryPresenter mSubCategoryPresenter;
    private Context mContext;
    private List<SubCategoryEntity> mSubCategoryList;

    public SubCategoryAdapter(SubCategoryPresenter mSubCategoryPresenter, Context mContext) {
        this.mSubCategoryPresenter = mSubCategoryPresenter;
        this.mContext = mContext;
    }

    public void setSubCategoryList(List<SubCategoryEntity> subCategoryList) {
        this.mSubCategoryList = subCategoryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sub_category_list_item, parent, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SubCategoryViewHolder holder = (SubCategoryViewHolder) viewHolder;
        SubCategoryEntity subCategoryEntity = mSubCategoryList.get(position);
        String imageUrl = subCategoryEntity.getImage();
        holder.mName.setText(subCategoryEntity.getName());
        Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(R.mipmap.no_image)
                .error(R.mipmap.no_image)
                .into(holder.mImage);

        holder.mRelativeLayout.setTag(position);
        holder.mRelativeLayout.setOnClickListener(this);

    }


    @Override
    public int getItemCount() {
        return mSubCategoryList == null ? 0 : mSubCategoryList.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sub_category_list_item:
                if (view.getTag() != null) {
                    int position = (int) view.getTag();
                    mSubCategoryPresenter.onClickItem(position);
                }
                break;
        }
    }

    class SubCategoryViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private TextView mName;
        private ImageView mImage;

        public SubCategoryViewHolder(View itemView) {
            super(itemView);

            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.sub_category_list_item);
            mName = (TextView) itemView.findViewById(R.id.sub_category_list_item_tv);
            mImage = (ImageView) itemView.findViewById(R.id.sub_category_list_item_img);

        }

    }

}