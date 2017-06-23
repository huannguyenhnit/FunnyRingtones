package com.coderalone.admin.funnyringtones.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coderalone.admin.funnyringtones.R;
import com.coderalone.admin.funnyringtones.model.RingtoneEntity;
import com.coderalone.admin.funnyringtones.presenter.RingtonePresenter;

import java.util.List;

public class RingtoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    //Save old position of item selected.
    private int mSelectedPosition = -1;

    private RingtonePresenter mRingtonePresenter;

    private Context mContext;

    private List<RingtoneEntity> mRingtoneList;

    public RingtoneAdapter(RingtonePresenter mRingtonePresenter, Context mContext) {
        this.mRingtonePresenter = mRingtonePresenter;
        this.mContext = mContext;
    }

    public void setRingtoneList(List<RingtoneEntity> mRingtoneList) {
        this.mRingtoneList = mRingtoneList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ringtone_list_item, parent, false);
        return new RingtoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RingtoneViewHolder) {
            RingtoneViewHolder holder = (RingtoneViewHolder) viewHolder;
            RingtoneEntity ringtoneEntity = mRingtoneList.get(position);
            holder.mName.setText(ringtoneEntity.getName());
            holder.mDescription.setText(ringtoneEntity.getSubCategory());

            holder.mRelativeLayout.setTag(position);
            holder.mRelativeLayout.setOnClickListener(this);

            if (ringtoneEntity.isSelected()) {
                holder.mRelativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.ringtone_item_hight_light));
            } else {
                holder.mRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mRingtoneList == null ? 0 : mRingtoneList.size();
    }

    @Override
    public void onClick(final View view) {
        int positon;
        switch (view.getId()) {
            case R.id.ringtone_list_item:
                if (view.getTag() != null) {
                    final int position = (int) view.getTag();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mSelectedPosition != position) {
                                if (mSelectedPosition != -1) {
                                    // Change selected status of old item is false.
                                    mRingtoneList.get(mSelectedPosition).setSelected(false);
                                }
                                // Change selected status of new item is true.
                                mRingtoneList.get(position).setSelected(true);
                            }
                            mSelectedPosition = position;
                            notifyDataSetChanged();
                            mRingtonePresenter.onClickItem(position);
                        }
                    }, 1000);

                }
                break;
        }

    }

    class RingtoneViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private TextView mName;
        private TextView mDescription;

        public RingtoneViewHolder(View itemView) {
            super(itemView);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.ringtone_list_item);
            mName = (TextView) itemView.findViewById(R.id.ringtone_list_tv_name);
            mDescription = (TextView) itemView.findViewById(R.id.ringtone_list_tv_description);
        }

    }

}