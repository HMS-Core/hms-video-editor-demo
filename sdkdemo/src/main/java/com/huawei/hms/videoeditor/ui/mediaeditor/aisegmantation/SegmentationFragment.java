/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.mediaeditor.aisegmantation;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

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

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverTrackView;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import java.util.ArrayList;
import java.util.List;

public class SegmentationFragment extends BaseFragment {
    public static final String TAG = "SegmentationFragment";

    private RecyclerView mSegmentationRecyclerView;

    private CoverAdapter mSegmentationCoverAdapter;

    private List<HVEAsset> mSegmentationAssetList;

    private EditPreviewViewModel previewViewModel;

    private SegmentationViewModel segmentationViewModel;

    private MaterialEditViewModel materialEditViewModel;

    private VideoClipsPlayViewModel videoClipsPlayViewModel;

    private HVEAsset selectedAsset;

    private TextView tvSegmentationTitle;

    private ImageView ivSegmentationCertain;

    private int mRvScrollX = 0;

    private int mItemWidth;

    private long mVideoCoverTime;

    private long mDurationTime;

    private int mOperateId;

    private boolean isReady = false;

    public static SegmentationFragment newInstance(int operateId) {
        Bundle args = new Bundle();
        args.putInt("operateId", operateId);
        SegmentationFragment fragment = new SegmentationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_segmentation;
    }

    @Override
    protected void initView(View view) {
        mSegmentationRecyclerView = view.findViewById(R.id.rv_person);
        ivSegmentationCertain = view.findViewById(R.id.iv_certain);
        tvSegmentationTitle = view.findViewById(R.id.tv_title);
        tvSegmentationTitle.setText(R.string.cut_second_menu_segmentation);
        tvSegmentationTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.clip_color_E6FFFFFF));
    }

    @Override
    protected void initObject() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        mOperateId = safeBundle.getInt("operateId", 0);
        mItemWidth = SizeUtils.dp2Px(mActivity, 64);
        segmentationViewModel = new ViewModelProvider(mActivity, mFactory).get(SegmentationViewModel.class);
        previewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        materialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        videoClipsPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
        previewViewModel.setSegmentationStatus(true);
        segmentationViewModel.setIsInit(true);
        resetView();
    }

    @Override
    protected void initData() {
        mSegmentationAssetList = new ArrayList<>();
        mSegmentationCoverAdapter = new CoverAdapter(context, mSegmentationAssetList, R.layout.adapter_cover_item2);
        if (ScreenUtil.isRTL()) {
            mSegmentationRecyclerView.setScaleX(RTL_UI);
        } else {
            mSegmentationRecyclerView.setScaleX(LTR_UI);
        }
        mSegmentationRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        mSegmentationRecyclerView.setAdapter(mSegmentationCoverAdapter);
        mSegmentationRecyclerView.setItemAnimator(null);
        View headerView = new View(context);
        View footView = new View(context);
        headerView.setLayoutParams(
            new ViewGroup.LayoutParams(SizeUtils.screenWidth(context) / 2, SizeUtils.dp2Px(context, 64)));
        footView.setLayoutParams(
            new ViewGroup.LayoutParams(SizeUtils.screenWidth(context) / 2, SizeUtils.dp2Px(context, 64)));
        mSegmentationCoverAdapter.addHeaderView(headerView);
        mSegmentationCoverAdapter.addFooterView(footView);
        selectedAsset = segmentationViewModel.getSelectedAsset();
        if (selectedAsset == null) {
            SmartLog.e(TAG, "SelectedAsset is null!");
            return;
        }
        mDurationTime = selectedAsset.getDuration();

        getBitmapList(selectedAsset);

        segmentationViewModel.getIsReady().observe(mActivity, aBoolean -> {
            isReady = aBoolean;
        });

        videoClipsPlayViewModel.getPlayState().observe(this, aBoolean -> {
            if (aBoolean) {
                isReady = false;
                materialEditViewModel.clearMaterialEditData();
            }
        });
    }

    @Override
    protected void initEvent() {
        mSegmentationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mRvScrollX += dx;
                updateTimeLine();
            }
        });

        ivSegmentationCertain.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (isReady) {
                segmentationViewModel.setIsStart(mOperateId);
            }
            onBackPressed();
            MenuClickManager.getInstance().popView();
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void updateTimeLine() {
        if (mActivity != null && selectedAsset != null) {
            float totalWidth = mDurationTime / 1000f * mItemWidth;
            float percent = mRvScrollX / totalWidth;
            mVideoCoverTime = (long) (percent * mDurationTime);
            if (mVideoCoverTime > mDurationTime) {
                mVideoCoverTime = mDurationTime;
            }
            viewModel.setCurrentTimeLine(mVideoCoverTime + selectedAsset.getStartTime());

            notifyCurrentTimeChange(selectedAsset.getStartTime() + mVideoCoverTime);
        }
    }

    private void getBitmapList(HVEAsset selectedAsset) {
        if (mSegmentationAssetList != null) {
            mSegmentationAssetList.clear();
            mSegmentationAssetList.add(selectedAsset);
        }
        if (mSegmentationCoverAdapter != null) {
            mSegmentationCoverAdapter.notifyDataSetChanged();
        }
    }

    private void notifyCurrentTimeChange(long time) {
        if (mSegmentationRecyclerView != null) {
            for (int j = 0; j < mSegmentationRecyclerView.getChildCount(); j++) {
                if (mSegmentationRecyclerView.getChildAt(j) instanceof CoverTrackView) {
                    ((CoverTrackView) mSegmentationRecyclerView.getChildAt(j)).handleCurrentTimeChange(time);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (previewViewModel == null) {
            return;
        }
        materialEditViewModel.clearMaterialEditData();
        previewViewModel.setSegmentationStatus(false);
    }

    @Override
    protected void initViewModelObserve() {

    }

    private void resetView() {
        if (previewViewModel == null) {
            return;
        }
        previewViewModel.updateTimeLine();
    }
}
