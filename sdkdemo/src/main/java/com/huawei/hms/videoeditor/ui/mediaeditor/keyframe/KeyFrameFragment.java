/*
 *   Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.huawei.hms.videoeditor.ui.mediaeditor.keyframe;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.sdk.HVEKeyFrameAbility;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverTrackView;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class KeyFrameFragment extends BaseFragment {
    public static final String TAG = "KeyFrameFragment";

    private static int keyFrameWidth;

    private boolean isAdd = true;

    private RecyclerView mRecyclerView;

    private CoverAdapter mCoverAdapter;

    private List<HVEAsset> mAssetList;

    private EditPreviewViewModel mEditPreviewViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private VideoClipsPlayViewModel mSdkPlayViewModel;

    private MenuViewModel mMenuViewModel;

    private HVEAsset selectedAsset;

    private TextView tvTitle;

    private ImageView ivCertain;

    private TextView addButton;

    private int mRvScrollX = 0;

    private int mItemWidth;

    private ImageView imageView;

    private long mVideoCoverTime;

    private long mDurationTime;

    public static KeyFrameFragment newInstance(int operateId) {
        Bundle args = new Bundle();
        args.putInt("operateId", operateId);
        KeyFrameFragment fragment = new KeyFrameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_asset_keyframe;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.rv_split);
        imageView = view.findViewById(R.id.view);
        ivCertain = view.findViewById(R.id.iv_certain);
        addButton = view.findViewById(R.id.button);
        addButton.setText(R.string.add_wza);
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.keyframe);
        tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.clip_color_E6FFFFFF));
    }

    @Override
    protected void initObject() {
        mItemWidth = SizeUtils.dp2Px(mActivity, 64);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mSdkPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
        mMenuViewModel = new ViewModelProvider(mActivity, mFactory).get(MenuViewModel.class);
        resetView();
    }

    @Override
    protected void initData() {
        keyFrameWidth = SizeUtils.dp2Px(getActivity(), 9);
        mAssetList = new ArrayList<>();
        mCoverAdapter = new CoverAdapter(context, mAssetList, R.layout.adapter_cover_item2);
        if (ScreenUtil.isRTL()) {
            mRecyclerView.setScaleX(RTL_UI);
        } else {
            mRecyclerView.setScaleX(LTR_UI);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        mRecyclerView.setAdapter(mCoverAdapter);
        mRecyclerView.setItemAnimator(null);
        View headerView = new View(context);
        View footView = new View(context);
        headerView.setLayoutParams(
                new ViewGroup.LayoutParams(SizeUtils.screenWidth(context) / 2, SizeUtils.dp2Px(context, 64)));
        footView.setLayoutParams(
                new ViewGroup.LayoutParams(SizeUtils.screenWidth(context) / 2, SizeUtils.dp2Px(context, 64)));
        mCoverAdapter.addHeaderView(headerView);
        mCoverAdapter.addFooterView(footView);

        selectedAsset = mEditPreviewViewModel.getMainLaneAsset();

        if (selectedAsset == null) {
            SmartLog.e(TAG, "SelectedAsset is null!");
            return;
        }
        mDurationTime = selectedAsset.getDuration();
        getBitmapList(selectedAsset);

        mSdkPlayViewModel.getPlayState().observe(this, aBoolean -> {
            if (aBoolean) {
                mMaterialEditViewModel.clearMaterialEditData();
            }
        });

        mSdkPlayViewModel.getCurrentTime().observe(this, time -> {

        });

    }

    private void getBitmapList(HVEAsset selectedAsset) {
        if (mAssetList != null) {
            mAssetList.clear();
            mAssetList.add(selectedAsset);
        }
        if (mCoverAdapter != null) {
            mCoverAdapter.notifyDataSetChanged();
        }
    }

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
                mMaterialEditViewModel.clearMaterialEditData();
                mRvScrollX += dx;
                updateTimeLine();

                HVEKeyFrameAbility ability = null;
                if (selectedAsset != null) {
                    if (selectedAsset instanceof HVEKeyFrameAbility) {
                        ability = (HVEKeyFrameAbility) selectedAsset;
                    }
                }
                if (ability == null) {
                    setLineColor(imageView, R.color.white);
                    return;
                }

                int selectedKeyFrame = ability.getSelectedKeyFrame();
                List<Long> keyFrameList = ability.getAllKeyFrameTimestamp();
                int minIndex = -1;
                long minInterval = -1;
                int count = keyFrameList.size();

                if (count == 0) {
                    setLineColor(imageView, R.color.white);
                    return;
                }

                long time = mVideoCoverTime + selectedAsset.getStartTime();
                for (int i = 0; i < count; ++i) {
                    long duration = Math.abs(time - keyFrameList.get(i));

                    double width = BigDecimalUtil.mul(BigDecimalUtil.div(duration, 1000), 1000);
                    if (width <= keyFrameWidth) {
                        if (minIndex == -1) {
                            minIndex = i;
                            minInterval = duration;
                        } else if (compareTo(minInterval, duration)) {
                            minIndex = i;
                            minInterval = duration;
                        }
                    }
                }
                if (minIndex != selectedKeyFrame) {
                    ability.selectKeyFrame(minIndex);
                }
                if (minIndex == -1) {
                    setLineColor(imageView, R.color.white);
                    addButton.setText(R.string.add_wza);
                    isAdd = true;
                } else {
                    setLineColor(imageView, R.color.red);
                    mRecyclerView.stopScroll();
                    isAdd = false;
                    addButton.setText(R.string.cut_second_menu_delete);
                }
            }
        });

        ivCertain.setOnClickListener(new OnClickRepeatedListener(v -> {
            mMenuViewModel.pauseTimeLine();
            mActivity.onBackPressed();
        }));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAsset == null || !(selectedAsset instanceof HVEKeyFrameAbility)) {
                    return;
                }

                if (isAdd) {
                    ((HVEKeyFrameAbility) selectedAsset).addKeyFrame();
                    setLineColor(imageView, R.color.red);
                    addButton.setText(R.string.cut_second_menu_delete);
                    isAdd = false;
                } else {
                    ((HVEKeyFrameAbility) selectedAsset).removeKeyFrame();
                    setLineColor(imageView, R.color.white);
                    addButton.setText(R.string.add_wza);
                    isAdd = true;
                }
            }
        });
    }

    private void setLineColor(View view, int colorId) {
        if (view == null) {
            return;
        }

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        view.setBackgroundColor(activity.getResources().getColor(colorId));
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    public static boolean compareTo(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        int bj = b1.compareTo(b2);
        boolean res;
        if (bj > 0) {
            res = true;
        } else {
            res = false;
        }
        return res;
    }

    private void updateTimeLine() {
        if (mActivity != null && selectedAsset != null) {
            float totalWidth = mDurationTime / 1000f * mItemWidth;
            float percent = mRvScrollX / totalWidth;
            mVideoCoverTime = (long) (percent * mDurationTime);
            if (mVideoCoverTime > mDurationTime) {
                mVideoCoverTime = mDurationTime;
            }
            if (selectedAsset.getLaneIndex() == 0) {
                viewModel.setCurrentTimeLine(selectedAsset.getStartTime() + mVideoCoverTime);
            }
            notifyCurrentTimeChange(selectedAsset.getStartTime() + mVideoCoverTime);
        }
    }

    private void notifyCurrentTimeChange(long time) {
        if (mRecyclerView != null) {
            for (int j = 0; j < mRecyclerView.getChildCount(); j++) {
                if (mRecyclerView.getChildAt(j) instanceof CoverTrackView) {
                    ((CoverTrackView) mRecyclerView.getChildAt(j)).handleCurrentTimeChange(time);
                }
            }
        }
    }

    private void resetView() {
        if (mEditPreviewViewModel == null) {
            return;
        }
        mEditPreviewViewModel.updateTimeLine();
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mEditPreviewViewModel == null) {
            return;
        }
        mMaterialEditViewModel.clearMaterialEditData();
    }
}
