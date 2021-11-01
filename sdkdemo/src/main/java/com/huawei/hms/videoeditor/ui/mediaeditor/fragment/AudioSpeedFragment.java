/*
 *  Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hms.videoeditor.ui.mediaeditor.fragment;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEAudioAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEAudioLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.SpeedBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class AudioSpeedFragment extends BaseFragment {
    protected EditPreviewViewModel mEditPreviewViewModel;

    private TextView tv_title;

    private ImageView iv_certain;

    private SpeedBar speedBar;

    private float speed = 1;

    private HVEAsset currentSelectedAsset;

    private int defaultFadeTime = 10 * 1000;

    public static AudioSpeedFragment newInstance(int id) {
        AudioSpeedFragment fragment = new AudioSpeedFragment();
        return fragment;
    }

    public static String format(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.toString();
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_audio_speed;
    }

    @Override
    protected void initView(View view) {
        viewModel.setIsFootShow(true);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);

        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.cut_second_menu_speed_change);
        iv_certain = view.findViewById(R.id.iv_certain);
        speedBar = view.findViewById(R.id.speedbar);
        if (ScreenUtil.isRTL()) {
            speedBar.setScaleX(RTL_UI);
        } else {
            speedBar.setScaleX(LTR_UI);
        }
        View mCancelHeaderView =
            LayoutInflater.from(mActivity).inflate(R.layout.adapter_add_filter_header, null, false);
        mCancelHeaderView.setLayoutParams(
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2Px(context, 75)));
    }

    @Override
    protected void initObject() {
    }

    @Override
    protected void initData() {
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }

        if (currentSelectedAsset == null) {
            return;
        }
        speed = getSDKSpeed();
        float mProgress = 10;
        if (speed <= 0) {
            mProgress = 0F;
        } else {
            mProgress = speed * 10;
        }
        speedBar.setProgress(mProgress);
    }

    @Override
    protected void initEvent() {
        speedBar.setOnProgressChangedListener(progress -> {
            if (progress <= 0) {
                speed = 0.5F;
            } else {
                speed = progress / 10F;
            }
            mEditPreviewViewModel.setToastTime(speed + "");
            adjustAudioSpeed(speed);
        });

        speedBar.setaTouchListener(isTouch -> {
            if (!isTouch) {
                adjustVideoSpeed(speed);
                viewModel.getEditor()
                    .playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
            }
            mEditPreviewViewModel.setToastTime(isTouch ? speed + "" : "");
        });

        iv_certain.setOnClickListener(new OnClickRepeatedListener(v -> {
            mActivity.onBackPressed();
        }));

    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_210;
    }

    private void adjustVideoSpeed(float mProgress) {
        if (currentSelectedAsset == null || currentSelectedAsset.getType() != HVEAsset.HVEAssetType.VIDEO) {
            return;
        }

        if (mEditPreviewViewModel.getTimeLine() == null) {
            return;
        }

        List<HVEVideoLane> videoLanes = mEditPreviewViewModel.getTimeLine().getAllVideoLane();
        if (videoLanes.isEmpty()) {
            return;
        }
        HVEVideoLane videoLane = videoLanes.get(currentSelectedAsset.getLaneIndex());
        if (videoLane == null) {
            return;
        }
        boolean isSuccess = videoLane.changeAssetSpeed(currentSelectedAsset.getIndex(), mProgress);
        if (isSuccess) {
            HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
            if (editor != null) {
                editor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
            }
        } else {
            ToastWrapper.makeText(mActivity, mActivity.getString(R.string.set_seeped_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void adjustAudioSpeed(float mProgress) {
        HVEAsset audioAsset = mEditPreviewViewModel.getSelectedAsset();
        if (audioAsset == null || audioAsset.getType() != HVEAsset.HVEAssetType.AUDIO) {
            return;
        }
        HVETimeLine timeLine = mEditPreviewViewModel.getTimeLine();
        if (timeLine == null) {
            return;
        }

        List<HVEAudioLane> audioLanes = timeLine.getAllAudioLane();
        if (audioLanes.isEmpty()) {
            return;
        }
        HVEAudioLane lane = audioLanes.get(audioAsset.getLaneIndex());
        if (lane == null) {
            return;
        }
        float aSpeed = audioAsset.getSpeed();
        boolean isSuccess = lane.changeAssetSpeed(audioAsset.getIndex(), mProgress);
        if (isSuccess) {
            HVEAudioAsset asset = null;
            if (audioAsset instanceof HVEAudioAsset) {
                asset = ((HVEAudioAsset) audioAsset);
            }

            if (asset == null) {
                return;
            }

            int fadeInTimeMs = asset.getFadeInTime();
            int fadeOutTimeMs = asset.getFadeOutTime();

            double in = BigDecimalUtils.mul2(fadeInTimeMs, aSpeed, 5);
            double out = BigDecimalUtils.mul2(fadeOutTimeMs, aSpeed, 5);

            double fadeIn = BigDecimalUtils.div(in, speed, 5);
            double fadeOut = BigDecimalUtils.div(out, speed, 5);

            int roundIn = (int) Math.round(fadeIn);
            int roundOut = (int) Math.round(fadeOut);

            asset.setFadeInTime(Math.min(roundIn, defaultFadeTime));
            asset.setFadeOutTime(Math.min(roundOut, defaultFadeTime));

        }
    }

    private float getSDKSpeed() {
        if (currentSelectedAsset instanceof HVEAudioAsset) {
            return currentSelectedAsset.getSpeed();
        }
        return 1;
    }

    @Override
    public void onBackPressed() {
        if (viewModel != null) {
            if (currentSelectedAsset.getType() == HVEAsset.HVEAssetType.AUDIO) {
                HuaweiVideoEditor editor = viewModel.getEditor();
                if (editor != null) {
                    editor.pauseTimeLine();
                }
            }
            viewModel.setIsFootShow(false);
        }
        super.onBackPressed();
    }
}
