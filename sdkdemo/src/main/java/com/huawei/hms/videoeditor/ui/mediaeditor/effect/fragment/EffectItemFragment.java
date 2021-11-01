
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

package com.huawei.hms.videoeditor.ui.mediaeditor.effect.fragment;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.effect.adapter.BaseGridLayoutManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.effect.adapter.EffectItemAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.effect.viewmodel.EffectItemViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.effect.viewmodel.EffectPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsRespository;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EffectItemFragment extends BaseFragment {
    private static final String TAG = "EffectItemFragment";

    private static final int NOMERA_HEIGHT = 0;

    private RecyclerView mSTRecyclerView;

    private EffectItemAdapter mEffectItemAdapter;

    private RelativeLayout mSTErrorLayout;

    private TextView mSTErrorTv;

    private ConstraintLayout mSTLoadingLayout;

    private LoadingIndicatorView mSTIndicatorView;

    private EffectPanelViewModel mEffectPanelViewModel;

    private EffectItemViewModel mEffectItemViewModel;

    private String columnId;

    private List<CloudMaterialBean> mList = new ArrayList<>();

    private int mCurrentPage = 0;

    private boolean isFirst;

    private boolean isScrolled = false;

    private boolean mHasNextPage = false;

    private int cancelPosition = -1;

    public static EffectItemFragment newInstance(HVEColumnInfo materialsCutContent) {
        Bundle args = new Bundle();
        args.putString("columnId", materialsCutContent.getColumnId());
        EffectItemFragment fragment = new EffectItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_add_sticker_page;
    }

    @Override
    protected void initView(View view) {
        mSTRecyclerView = view.findViewById(R.id.pager_recycler_view);
        mSTErrorLayout = view.findViewById(R.id.error_layout);
        mSTErrorTv = view.findViewById(R.id.error_text);
        mSTLoadingLayout = view.findViewById(R.id.loading_layout);
        mSTIndicatorView = view.findViewById(R.id.indicator);

        mSTLoadingLayout.setVisibility(View.VISIBLE);
        mSTIndicatorView.show();

        mEffectItemAdapter = new EffectItemAdapter(context, mList, R.layout.adapter_add_sticker_item);
        BaseGridLayoutManager gridSTLayoutManager = new BaseGridLayoutManager(context, 4);
        if (mSTRecyclerView.getItemDecorationCount() == 0) {
            mSTRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(context, 8f),
                SizeUtils.dp2Px(context, 8f), ContextCompat.getColor(context, R.color.transparent)));
        }
        mSTRecyclerView.setItemAnimator(null);
        mSTRecyclerView.setLayoutManager(gridSTLayoutManager);
        mSTRecyclerView.setAdapter(mEffectItemAdapter);
    }

    @Override
    protected void initObject() {
        mEffectPanelViewModel =
            new ViewModelProvider(requireParentFragment(), mFactory).get(EffectPanelViewModel.class);
        mEffectItemViewModel = new ViewModelProvider(this, mFactory).get(EffectItemViewModel.class);
    }

    @Override
    protected void initData() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        columnId = safeBundle.getString("columnId");
        mEffectItemViewModel.loadMaterials(columnId, mCurrentPage);
    }

    @Override
    protected void initEvent() {
        mSTErrorLayout.setOnClickListener(v -> {
            if (mCurrentPage == 0) {
                mSTErrorLayout.setVisibility(View.GONE);
                mSTLoadingLayout.setVisibility(View.VISIBLE);
                mSTIndicatorView.show();
            }
            mEffectItemViewModel.loadMaterials(columnId, mCurrentPage);
        });

        mSTRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE && mEffectItemAdapter.getItemCount() >= mList.size()) {
                    GridLayoutManager layoutSTManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (!isScrolled && mHasNextPage && layoutSTManager != null) {
                        int itemSTPosition = layoutSTManager.findLastCompletelyVisibleItemPosition();
                        if (itemSTPosition == layoutSTManager.getItemCount() - 1) {
                            mCurrentPage++;
                            mEffectItemViewModel.loadMaterials(columnId, mCurrentPage);
                            isScrolled = false;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutSTManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && layoutSTManager != null) {
                    int itemSTPosition = layoutSTManager.findLastCompletelyVisibleItemPosition();
                    if (itemSTPosition == layoutSTManager.getItemCount() - 1 && dy > 0) {
                        mCurrentPage++;
                        mEffectItemViewModel.loadMaterials(columnId, mCurrentPage);
                        isScrolled = true;
                    }
                }
                if (layoutSTManager != null) {
                    int visibleItemCount = layoutSTManager.getChildCount();
                    int firstPosition = layoutSTManager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst) {
                        isFirst = true;
                        for (int i = 0; i < visibleItemCount; i++) {
                            CloudMaterialBean cutContent = mList.get(i);
                            mEffectItemAdapter.addFirstScreenMaterial(cutContent);
                        }
                    }
                }
            }
        });

        mEffectItemAdapter.setOnItemClickListener(new EffectItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int mSelectSTPosition = mEffectItemAdapter.getSelectPosition();
                cancelPosition = position;
                if (mSelectSTPosition == position) {
                    mEffectPanelViewModel.getRemoveData().postValue(true);
                    mEffectItemAdapter.setSelectPosition(-1);
                    mEffectItemAdapter.notifyItemChanged(position);
                    return;
                }

                mEffectItemAdapter.setSelectPosition(position);
                if (mSelectSTPosition != -1) {
                    mEffectItemAdapter.notifyItemChanged(mSelectSTPosition);
                }
                mEffectItemAdapter.notifyItemChanged(position);
                mEffectPanelViewModel.setSelectCutContent(mList.get(position));
                mEffectPanelViewModel.getRemoveData().postValue(false);
            }

            @Override
            public void onDownloadClick(int position) {
                if (mList == null || mList.isEmpty()) {
                    return;
                }
                cancelPosition = position;
                CloudMaterialBean contentST = mList.get(position);
                if (contentST == null) {
                    return;
                }
                int mPreviousPosition = mEffectItemAdapter.getSelectPosition();
                mEffectItemAdapter.setSelectPosition(position);
                if (mPreviousPosition != -1) {
                    mEffectItemAdapter.notifyItemChanged(mPreviousPosition);
                }
                mEffectItemAdapter.notifyItemChanged(position);
                mEffectItemAdapter.addDownloadMaterial(contentST);
                mEffectItemViewModel.downloadMaterials(mPreviousPosition, position, contentST);
            }
        });
    }

    @Override
    protected void initViewModelObserve() {
        mEffectItemViewModel.getPageData().observe(this, materialsCutContents -> {
            if (mCurrentPage == 0) {
                mSTLoadingLayout.setVisibility(View.GONE);
                mSTIndicatorView.hide();
                mList.clear();
                mEffectItemAdapter.notifyDataSetChanged();
            }
            if (!mList.containsAll(materialsCutContents)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                mList.addAll(materialsCutContents);
                mEffectItemAdapter.notifyDataSetChanged();
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
        });

        mEffectItemViewModel.getErrorType().observe(this, type -> {
            switch (type) {
                case MaterialsRespository.RESULT_ILLEGAL:
                    if (mList == null || mList.isEmpty()) {
                        mSTLoadingLayout.setVisibility(View.GONE);
                        mSTIndicatorView.hide();
                        mSTErrorTv.setText(getString(R.string.result_illegal));
                        mSTErrorLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case MaterialsRespository.RESULT_EMPTY:
                    if (mCurrentPage == 0) {
                        mSTLoadingLayout.setVisibility(View.GONE);
                        mSTIndicatorView.hide();
                    }
                    SmartLog.i(TAG, "No data.");
                    break;
                default:
                    break;
            }
        });

        mEffectItemViewModel.getDownloadInfo().observe(this, materialsDownloadInfo -> {
            int downloadState = materialsDownloadInfo.getState();
            switch (downloadState) {
                case MaterialsRespository.DOWNLOAD_SUCCESS:
                    SmartLog.d(TAG, "success:" + materialsDownloadInfo.getMaterialBean().getLocalPath());
                    mEffectItemAdapter.removeDownloadMaterial(materialsDownloadInfo.getContentId());
                    int downloadPosition = materialsDownloadInfo.getDataPosition();
                    if (downloadPosition < 0 || downloadPosition >= mList.size()) {
                        return;
                    }
                    if (!materialsDownloadInfo.getContentId().equals(mList.get(downloadPosition).getId())) {
                        return;
                    }
                    mList.set(downloadPosition, materialsDownloadInfo.getMaterialBean());
                    mEffectItemAdapter.notifyItemChanged(downloadPosition);
                    if (downloadPosition == mEffectItemAdapter.getSelectPosition()) {
                        mEffectPanelViewModel.setSelectCutContent(mList.get(downloadPosition));
                    }
                    break;
                case MaterialsRespository.DOWNLOAD_FAIL:
                    mEffectItemAdapter.removeDownloadMaterial(materialsDownloadInfo.getContentId());
                    updateFail(materialsDownloadInfo);
                    ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
                    break;
                case MaterialsRespository.DOWNLOAD_LOADING:
                    SmartLog.d(TAG, "progress:" + materialsDownloadInfo.getProgress());
                    break;
                default:
                    break;
            }
        });

        mEffectPanelViewModel.getCancelSelected().observe(this, cancelSelected -> {
            if (!cancelSelected) {
                return;
            }
            mEffectItemAdapter.setSelectPosition(-1);
            mEffectItemAdapter.notifyItemChanged(cancelPosition);
            mEffectPanelViewModel.getCancelSelected().postValue(false);
        });

        mEffectItemViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void updateFail(MaterialsDownloadInfo downloadSTInfo) {
        int downloadPosition = downloadSTInfo.getDataPosition();
        if (downloadPosition < 0 || downloadPosition >= mList.size()) {
            return;
        }
        if (!downloadSTInfo.getContentId().equals(mList.get(downloadPosition).getId())) {
            return;
        }
        mList.set(downloadPosition, downloadSTInfo.getMaterialBean());
        if (downloadSTInfo.getPreviousPosition() != -1) {
            mEffectItemAdapter.notifyItemChanged(downloadSTInfo.getPreviousPosition());
        }
        mEffectItemAdapter.notifyItemChanged(downloadPosition);

    }
}
