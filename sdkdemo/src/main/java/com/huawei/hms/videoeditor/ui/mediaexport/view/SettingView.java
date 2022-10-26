/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class SettingView extends NestedScrollView {
    private ItemVideoRegulatorView mResolutionView;

    private ItemVideoRegulatorView mFrameRateView;

    private Context mContext;

    private SettingViewChangeListener settingViewChangeListener;

    public SettingView(@NonNull Context context) {
        super(context);
    }

    public SettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.fragment_export_setting, this, true);
        mResolutionView = findViewById(R.id.resolution_view);
        mFrameRateView = findViewById(R.id.frame_rate_view);
    }

    private void initData() {
        mResolutionView.seekBarProgress(mResolutionView.getMaxProgress() < 2 ? 1 : 2);
        mFrameRateView.seekBarProgress(mFrameRateView.getMaxProgress() < 2 ? 1 : 2);
    }

    private void initEvent() {
        mResolutionView.setOnResolutionChanged((width, height) -> {
            if (settingViewChangeListener != null) {
                settingViewChangeListener.onResolutionChanged(width, height);
            }
        });

        mFrameRateView.setOnFrameChanged(frame -> {
            if (settingViewChangeListener != null) {
                settingViewChangeListener.onFrameChanged(frame);
            }
        });
    }

    public interface SettingViewChangeListener {
        void onResolutionChanged(int width, int height);

        void onFrameChanged(int frame);
    }

    public void setSettingViewChangeListener(SettingViewChangeListener settingViewChangeListener) {
        this.settingViewChangeListener = settingViewChangeListener;
    }

    public void setMaxProgress(int resolutionMaxProgress, int frameMaxProgress) {
        mResolutionView.setMaxProgress(resolutionMaxProgress);
        mFrameRateView.setMaxProgress(frameMaxProgress);
    }
}
