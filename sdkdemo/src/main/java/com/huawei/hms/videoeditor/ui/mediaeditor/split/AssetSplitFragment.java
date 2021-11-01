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

package com.huawei.hms.videoeditor.ui.mediaeditor.split;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AssetSplitFragment extends BaseFragment {
    public static final String TAG = "AssetSplitFragment";

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

    private int mRvScrollX = 0;

    private int mItemWidth;

    private long mVideoCoverTime;

    private long mDurationTime;

    public static AssetSplitFragment newInstance(int operateId) {
        Bundle args = new Bundle();
        args.putInt("operateId", operateId);
        AssetSplitFragment fragment = new AssetSplitFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_asset_split;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.rv_split);
        ivCertain = view.findViewById(R.id.iv_certain);
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cut_second_menu_severing);
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
            }
        });

        ivCertain.setOnClickListener(new OnClickRepeatedListener(v -> {
            mMenuViewModel.pauseTimeLine();
            mMenuViewModel.splitAsset();

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
