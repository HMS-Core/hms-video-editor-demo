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

package com.huawei.hms.videoeditor.ui.mediaeditor.split;

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
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;

public class AssetSplitFragment extends BaseFragment {
    public static final String TAG = "AssetSplitFragment";

    private RecyclerView recyclerView;

    private CoverAdapter coverAdapter;

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

    private long videoCoverTime;

    private long durationTime;

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
        recyclerView = view.findViewById(R.id.rv_split);
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
        coverAdapter = new CoverAdapter(context, mAssetList, R.layout.adapter_cover_item2);
        if (ScreenUtil.isRTL()) {
            recyclerView.setScaleX(RTL_UI);
        } else {
            recyclerView.setScaleX(LTR_UI);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(coverAdapter);
        recyclerView.setItemAnimator(null);
        View header = new View(context);
        View foot = new View(context);
        header.setLayoutParams(
            new ViewGroup.LayoutParams(SizeUtils.screenWidth(context) / 2, SizeUtils.dp2Px(context, 64)));
        foot.setLayoutParams(
            new ViewGroup.LayoutParams(SizeUtils.screenWidth(context) / 2, SizeUtils.dp2Px(context, 64)));
        coverAdapter.addHeaderView(header);
        coverAdapter.addFooterView(foot);

        selectedAsset = mEditPreviewViewModel.getMainLaneAsset();

        if (selectedAsset == null) {
            SmartLog.e(TAG, "SelectedAsset is null!");
            return;
        }
        durationTime = selectedAsset.getDuration();
        getBitmapList(selectedAsset);

        mSdkPlayViewModel.getPlayState().observe(this, aBoolean -> {
            if (aBoolean) {
                mMaterialEditViewModel.clearMaterialEditData();
            }
        });
    }

    private void getBitmapList(HVEAsset asset) {
        if (mAssetList != null) {
            mAssetList.clear();
            mAssetList.add(asset);
        }
        if (coverAdapter != null) {
            coverAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initEvent() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView view, int newState) {
                super.onScrollStateChanged(view, newState);
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

    private void updateTimeLine() {
        if (mActivity != null && selectedAsset != null) {
            float totalWidth = durationTime / 1000f * mItemWidth;
            float percent = mRvScrollX / totalWidth;
            videoCoverTime = (long) (percent * durationTime);
            if (videoCoverTime > durationTime) {
                videoCoverTime = durationTime;
            }
            if (selectedAsset.getLaneIndex() == 0) {
                viewModel.setCurrentTimeLine(selectedAsset.getStartTime() + videoCoverTime);
            }
            notifyCurrentTimeChange(selectedAsset.getStartTime() + videoCoverTime);
        }
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void notifyCurrentTimeChange(long time) {
        if (recyclerView != null) {
            for (int j = 0; j < recyclerView.getChildCount(); j++) {
                if (recyclerView.getChildAt(j) instanceof CoverTrackView) {
                    ((CoverTrackView) recyclerView.getChildAt(j)).handleCurrentTimeChange(time);
                }
            }
        }
    }

    @Override
    protected void initViewModelObserve() {

    }

    private void resetView() {
        if (mEditPreviewViewModel == null) {
            return;
        }
        mEditPreviewViewModel.updateTimeLine();
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
