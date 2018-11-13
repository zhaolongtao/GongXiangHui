package com.qunxianghui.gxh.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.qunxianghui.gxh.R;
import com.qunxianghui.gxh.config.Constant;
import com.qunxianghui.gxh.config.SpConstant;
import com.qunxianghui.gxh.ui.activity.LocationPublishActivity;
import com.qunxianghui.gxh.ui.fragments.homeFragment.activity.BaoLiaoActivity;
import com.qunxianghui.gxh.ui.fragments.homeFragment.activity.ProtocolActivity;
import com.qunxianghui.gxh.utils.FastBlurUtility;
import com.qunxianghui.gxh.utils.SPUtils;

public class OnekeyIssueDialog extends Dialog {
    private Activity mActicity;
    private Bitmap mFinalBlurBg;
    public OnekeyIssueDialog(@NonNull Context context) {
        super(context);
        mActicity = ((Activity) context);
        View view = LayoutInflater.from(context).inflate(R.layout.pop_onekey_issue, null);
        View.OnClickListener listener = new View.OnClickListener() {
            Intent intent = null;
            @Override
            public void onClick(View v) {
                dismiss();
                switch (v.getId()) {
                    case R.id.tv_video:
                        fitchVideo();
                        break;
                    case R.id.tv_location:
                        startActivity(LocationPublishActivity.class);
                        break;
                    case R.id.tv_baoliao:
                        startActivity(BaoLiaoActivity.class);
                        break;
                    case R.id.tv_local_service:
                    case R.id.tv_choice:
                        intent = new Intent(mActicity, ProtocolActivity.class);
                        intent.putExtra("url", v.getId() == R.id.tv_local_service ? Constant.LOCAL_SERVICES_ISSUE_URL : Constant.GOOD_SELECT__DETAIL_URL);
                        intent.putExtra("token", SPUtils.getString(SpConstant.ACCESS_TOKEN, ""));
                        intent.putExtra("tag", 1);
                        mActicity.startActivity(intent);
                        break;
                }
            }
        };
        setContentView(view);
        findViewById(R.id.tv_video).setOnClickListener(listener);
        findViewById(R.id.tv_location).setOnClickListener(listener);
        findViewById(R.id.tv_baoliao).setOnClickListener(listener);
        findViewById(R.id.tv_local_service).setOnClickListener(listener);
        findViewById(R.id.tv_choice).setOnClickListener(listener);
        findViewById(R.id.iv_onekey_issue_close).setOnClickListener(listener);
        findViewById(R.id.pop_ll).setOnClickListener(listener);
        Window dialogWindow = getWindow();
//        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.alpha = 0.9f;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;  //设置宽度
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;//设置dialog高度
        lp.y = 3;  //设置dialog距离底部的距离
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mFinalBlurBg != null && !mFinalBlurBg.isRecycled()) {
                    mFinalBlurBg.recycle();
                    mFinalBlurBg = null;
                }
            }
        });
    }

    /*获取系统的视频和录像*/
    private void fitchVideo() {
        PictureSelector.create(mActicity)
                .openGallery(PictureMimeType.ofVideo())
                .selectionMode(PictureConfig.SINGLE)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private void startActivity(Class clazz) {
        mActicity.startActivity(new Intent(mActicity, clazz));
    }

    public Dialog blurBg() {
        Window window = getWindow();
        Bitmap blurBg = null;
        if (window != null) {
            // 获取截图
            View activityView = mActicity.getWindow().getDecorView();
            activityView.setDrawingCacheEnabled(true);
            activityView.destroyDrawingCache();
            activityView.buildDrawingCache();
            Bitmap bmp = activityView.getDrawingCache();
            // 模糊处理并保存
            blurBg = FastBlurUtility.blur(mActicity, bmp);
            // 设置成dialog的背景
            window.setBackgroundDrawable(new BitmapDrawable(mActicity.getResources(), blurBg));
            bmp.recycle();
        }
        mFinalBlurBg = blurBg;
        findViewById(R.id.pop_ll2).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.one_key_issue_ll2));
        findViewById(R.id.pop_ll1).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.one_key_issue_ll1));
        return this;
    }

}
