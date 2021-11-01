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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.SpeedBar;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GeneralSpeedFragment extends BaseFragment {
    private TextView tv_title;

    private ImageView iv_certain;

    private SpeedBar speedBar;

    protected EditPreviewViewModel mEditPreviewViewModel;

    private HuaweiVideoEditor mEditor;

    private float speed = 1;

    private TextView generalSpeed;

    private View generalView;

    private RecyclerView mRecyclerView;

    // private HVEVideoLane videoLane;
    private HVEAsset currentSelectedAsset;

    private boolean isFirst;

    public static GeneralSpeedFragment newInstance(int id) {
        GeneralSpeedFragment fragment = new GeneralSpeedFragment();
        return fragment;
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
        return R.layout.fragment_generalspeed_style;
    }

    @Override
    protected void initView(View view) {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mEditor = mEditPreviewViewModel.getEditor();
        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.cut_second_menu_speed_change);
        iv_certain = view.findViewById(R.id.iv_certain);
        speedBar = view.findViewById(R.id.speedbar);
        if (ScreenUtil.isRTL()) {
            speedBar.setScaleX(RTL_UI);
        } else {
            speedBar.setScaleX(LTR_UI);
        }
        generalSpeed = view.findViewById(R.id.general_speed);
        generalView = view.findViewById(R.id.general_view);
        mRecyclerView = view.findViewById(R.id.rv_speed);
        View mCancelHeaderView =
            LayoutInflater.from(mActivity).inflate(R.layout.adapter_add_filter_header, null, false);
        mCancelHeaderView.setLayoutParams(
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2Px(context, 75)));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                    SizeUtils.dp2Px(mActivity, 75), SizeUtils.dp2Px(mActivity, 8)));
        }
        mRecyclerView.setItemAnimator(null);
        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
    }

    @Override
    protected void initObject() {
    }

    @Override
    protected void initData() {
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
        showGeneral();
    }

    private void showGeneral() {
        generalSpeed.setSelected(true);
        generalView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        speedBar.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initEvent() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null) {
                    int visibleItemCount = manager.getChildCount();
                    int firstPosition = manager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst) {
                        isFirst = true;
                    }
                }
            }
        });

        generalSpeed.setOnClickListener(new OnClickRepeatedListener(v -> {
            showGeneral();
        }));

        speedBar.setOnProgressChangedListener(progress -> {
            if (progress <= 0) {
                speed = 0.5F;
            } else {
                speed = progress / 10F;
            }
            mEditPreviewViewModel.setToastTime(speed + "");
        });

        speedBar.setaTouchListener(isTouch -> {
            if (!isTouch) {
                adjustVideoSpeed(speed);
            }
            mEditPreviewViewModel.updateDuration();
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

    public static String format(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.toString();
    }

    private void adjustVideoSpeed(float mProgress) {
        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
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
            mEditor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
        } else {
            ToastWrapper.makeText(mActivity, mActivity.getString(R.string.set_seeped_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private float getSDKSpeed() {
        if (currentSelectedAsset instanceof HVEVideoAsset) {
            return currentSelectedAsset.getSpeed();
        }
        return 1;
    }

    @Override
    public void onBackPressed() {
        if (!(mActivity instanceof VideoClipsActivity)) {
            return;
        }

        if (mEditPreviewViewModel == null) {
            return;
        }

        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
        if (editor == null) {
            return;
        }
        super.onBackPressed();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initTimeoutState();
            return false;
        }
    };
}
