package com.coderalone.admin.funnyringtones.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coderalone.admin.funnyringtones.R;

public class ProgressDialogCustom extends Dialog {

    private ImageView mProgressCustomImg;

    public ProgressDialogCustom(Context context) {
        super(context, R.style.ProgressDialog);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View progressView = mInflater.inflate(R.layout.progress_custom, null, false);
        mProgressCustomImg = (ImageView) progressView.findViewById(R.id.progress_custom);

        WindowManager.LayoutParams wLayoutParams = getWindow().getAttributes();
        wLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wLayoutParams);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(progressView);
        addContentView(linearLayout, params);
    }

    @Override
    public void show() {
        super.show();
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(2000);
        mProgressCustomImg.setAnimation(rotateAnimation);
        mProgressCustomImg.startAnimation(rotateAnimation);
    }

}
