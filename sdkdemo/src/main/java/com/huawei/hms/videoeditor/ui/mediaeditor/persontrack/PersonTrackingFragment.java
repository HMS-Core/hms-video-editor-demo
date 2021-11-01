
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

package com.huawei.hms.videoeditor.ui.mediaeditor.persontrack;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverTrackView;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PersonTrackingFragment extends BaseFragment {
    public static final String TAG = "PersonTrackingFragment";

    private RecyclerView mRecyclerView;

    private CoverAdapter mCoverAdapter;

    private List<HVEAsset> mAssetList;

    private EditPreviewViewModel mEditPreviewViewModel;

    private PersonTrackingViewModel mPersonTrackingViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private VideoClipsPlayViewModel mSdkPlayViewModel;

    private HVEAsset selectedAsset;

    private TextView tvTitle;

    private ImageView ivCertain;

    private int mRvScrollX = 0;

    private int mItemWidth;

    private long mVideoCoverTime;

    private long mDurationTime;

    private int mOperateId;

    private boolean isTrackingReady = false;

    public static PersonTrackingFragment newInstance(int operateId) {
        Bundle args = new Bundle();
        args.putInt("operateId", operateId);
        PersonTrackingFragment fragment = new PersonTrackingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_person_tracking;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.rv_person);
        ivCertain = view.findViewById(R.id.iv_certain);
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cut_second_menu_human_tracking);
        tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.clip_color_E6FFFFFF));
    }

    @Override
    protected void initObject() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        mOperateId = safeBundle.getInt("operateId", 0);
        mItemWidth = SizeUtils.dp2Px(mActivity, 64);
        mPersonTrackingViewModel = new ViewModelProvider(mActivity, mFactory).get(PersonTrackingViewModel.class);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mSdkPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
        mEditPreviewViewModel.setPersonTrackingStatus(true);
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
        selectedAsset = mPersonTrackingViewModel.getSelectedTracking();
        if (selectedAsset == null) {
            SmartLog.e(TAG, "SelectedAsset is null!");
            return;
        }
        mDurationTime = selectedAsset.getDuration();
        mPersonTrackingViewModel.setVideoEndTime(selectedAsset.getStartTime() + mDurationTime);
        getBitmapList(selectedAsset);

        mPersonTrackingViewModel.getTrackingIsReady().observe(mActivity, aBoolean -> {
            isTrackingReady = aBoolean;
        });
        mSdkPlayViewModel.getPlayState().observe(this, aBoolean -> {
            if (aBoolean) {
                isTrackingReady = false;
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
                isTrackingReady = false;
                updateTimeLine();
            }
        });

        ivCertain.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (isTrackingReady) {
                mPersonTrackingViewModel.setTrackingIsStart(mOperateId);
            } else {
                ToastWrapper.makeText(mActivity, R.string.no_person_selected, Toast.LENGTH_SHORT).show();
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
        mEditPreviewViewModel.setPersonTrackingStatus(false);
    }
}
