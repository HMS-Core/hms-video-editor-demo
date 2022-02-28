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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.fragment;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEWordAsset;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.utils.FoldScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.adapter.TextBubblesAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextFlowerViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditTextFlowerFragment extends BaseFragment {
    private static final String TAG = "EditTextFlowerFragment";

    protected EditPreviewViewModel previewViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    protected TextFlowerViewModel mTextFlowerViewModel;

    private RelativeLayout mFlowerErrorLayout;

    private TextView mFlowerErrorTv;

    private ConstraintLayout mFlowerLoadingLayout;

    private LoadingIndicatorView mFlowerIndicatorView;

    private RecyclerView mFlowerRecyclerView;

    private List<CloudMaterialBean> mFlowerList;

    private TextBubblesAdapter textFlowerAdapter;

    private int mCurrentPage = 0;

    private Boolean mHasNextPage = false;

    private View cancelFlowerHeader;

    private boolean isScrolled = false;

    private RelativeLayout relativeLayout;

    private TextPanelViewModel textPanelViewModel;

    private boolean isFirst;

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
        return R.layout.fragment_edit_text_flower;
    }

    @Override
    protected void initView(View view) {
        mFlowerErrorLayout = view.findViewById(R.id.error_layout);
        mFlowerErrorTv = view.findViewById(R.id.error_text);
        mFlowerLoadingLayout = view.findViewById(R.id.loading_layout);
        mFlowerIndicatorView = view.findViewById(R.id.indicator);
        mFlowerRecyclerView = view.findViewById(R.id.rl_pic);
    }

    @Override
    protected void initObject() {
        setTimeoutEnable();
        previewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mTextFlowerViewModel = new ViewModelProvider(this, mFactory).get(TextFlowerViewModel.class);
        textPanelViewModel = new ViewModelProvider(mActivity, mFactory).get(TextPanelViewModel.class);

        int widthHeight = 0;
        if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(context)) {
            widthHeight = (SizeUtils.screenWidth(context) - (SizeUtils.dp2Px(context, 56))) / 8;
        } else {
            boolean isPortrait = ScreenUtil.isPortrait(context);
            int spanCount = 4;
            if (!isPortrait) {
                spanCount = 8;
            }
            widthHeight = (SizeUtils.screenWidth(context) - (SizeUtils.dp2Px(context, 56))) / spanCount;
        }
        mFlowerList = new ArrayList<>();
        textFlowerAdapter = new TextBubblesAdapter(mActivity, mFlowerList, R.layout.adapter_edittext_bubbles_item);
        mFlowerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        int lineCount = 4;
        if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(mActivity)) {
            lineCount = 7;
        } else {
            boolean isPortrait = ScreenUtil.isPortrait(context);
            if (!isPortrait) {
                lineCount = 8;
            }
        }
        GridLayoutManager manager = new GridLayoutManager(mActivity, lineCount);
        if (mFlowerRecyclerView.getItemDecorationCount() == 0) {
            mFlowerRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(mActivity, 8f),
                SizeUtils.dp2Px(mActivity, 8f), ContextCompat.getColor(mActivity, R.color.transparent)));
        }
        mFlowerRecyclerView.setLayoutManager(manager);
        mFlowerRecyclerView.setItemAnimator(null);
        mFlowerRecyclerView.setAdapter(textFlowerAdapter);
        cancelFlowerHeader =
            LayoutInflater.from(mActivity).inflate(R.layout.adapter_bubbles_cancel_header, mFlowerRecyclerView, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widthHeight, widthHeight);
        cancelFlowerHeader.setLayoutParams(layoutParams);
        textFlowerAdapter.addHeaderView(cancelFlowerHeader);
        relativeLayout = cancelFlowerHeader.findViewById(R.id.rl_head);

        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
        }
    }

    @Override
    protected void initData() {
        mFlowerLoadingLayout.setVisibility(View.VISIBLE);
        mFlowerIndicatorView.show();
        mTextFlowerViewModel.loadMaterials(mCurrentPage);
        mTextFlowerViewModel.getPageData().observe(this, materialsCutContents -> {
            if (mCurrentPage == 0) {
                mFlowerLoadingLayout.setVisibility(View.GONE);
                mFlowerIndicatorView.hide();
                mFlowerList.clear();
                textFlowerAdapter.setSelectPosition(-1);
            }

            if (!mFlowerList.containsAll(materialsCutContents)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                mFlowerList.addAll(materialsCutContents);
                textFlowerAdapter.notifyDataSetChanged();
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
        });
        mTextFlowerViewModel.getErrorString().observe(this, errorString -> {
            if (!TextUtils.isEmpty(errorString) && mFlowerList.size() == 0) {
                mFlowerLoadingLayout.setVisibility(View.GONE);
                mFlowerIndicatorView.hide();
                mFlowerErrorTv.setText(errorString);
                mFlowerErrorLayout.setVisibility(View.VISIBLE);
            }
        });

        mTextFlowerViewModel.getEmptyString().observe(this, emptyString -> {
            if (mCurrentPage == 0) {
                mFlowerLoadingLayout.setVisibility(View.GONE);
                mFlowerIndicatorView.hide();
            }
            ToastWrapper.makeText(mActivity, emptyString, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void initEvent() {
        cancelFlowerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setBackground(mActivity.getResources().getDrawable(R.drawable.edit_text_style_shape));
                setFlowerPath("");
                setEditPanelFlower(null);
                int mSelectPosition = textFlowerAdapter.getSelectPosition();
                if (mSelectPosition != -1) {
                    textFlowerAdapter.setSelectPosition(-1);
                    textFlowerAdapter.notifyItemChanged(mSelectPosition);
                }
            }
        });

        textFlowerAdapter.setOnItemClickListener(new TextBubblesAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(int position, int dataPosition) {
                relativeLayout.setBackground(mActivity.getResources().getDrawable(R.drawable.text_bubble_normal_bg));
                int mSelectPosition = textFlowerAdapter.getSelectPosition();
                if (mSelectPosition == position) {
                    textFlowerAdapter.setSelectPosition(-1);
                    textFlowerAdapter.notifyItemChanged(position);
                }
                if (mSelectPosition != position) {
                    textFlowerAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        textFlowerAdapter.notifyItemChanged(mSelectPosition);
                    }
                    textFlowerAdapter.notifyItemChanged(position);
                    setFlowerPath(mFlowerList.get(dataPosition).getLocalPath());
                    setEditPanelFlower(mFlowerList.get(dataPosition));
                }
            }

            @Override
            public void onDownloadClick(int position, int dataPosition) {
                int previousPosition = textFlowerAdapter.getSelectPosition();
                textFlowerAdapter.setSelectPosition(position);
                CloudMaterialBean content = mFlowerList.get(dataPosition);

                textFlowerAdapter.addDownloadMaterial(content);
                mTextFlowerViewModel.downloadColumn(previousPosition, position, dataPosition, content);
            }
        });

        mFlowerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView view, int newState) {
                super.onScrollStateChanged(view, newState);
                if (newState == SCROLL_STATE_IDLE
                    && textFlowerAdapter.getItemCount() >= textFlowerAdapter.getOriginalItemCount()) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) view.getLayoutManager();
                    if (!isScrolled && mHasNextPage && gridLayoutManager != null) {
                        int visibleItemPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                        if (visibleItemPosition == gridLayoutManager.getItemCount() - 1) {
                            mCurrentPage++;
                            mTextFlowerViewModel.loadMaterials(mCurrentPage);
                            isScrolled = false;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && gridLayoutManager != null) {
                    int lastCompletelyVisibleItemPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastCompletelyVisibleItemPosition == gridLayoutManager.getItemCount() - 1 && dy > 0) {
                        mCurrentPage++;
                        mTextFlowerViewModel.loadMaterials(mCurrentPage);
                        isScrolled = true;
                    }
                }

                if (gridLayoutManager != null) {
                    int visibleItemCount = gridLayoutManager.getChildCount();
                    int firstPosition = gridLayoutManager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && mFlowerList.size() > 0) {
                        isFirst = true;
                        for (int i = 0; i < visibleItemCount - 1; i++) {
                            CloudMaterialBean cutContent = mFlowerList.get(i);
                            textFlowerAdapter.addFirstScreenMaterial(cutContent);
                        }
                    }
                }
            }
        });

        mFlowerErrorLayout.setOnClickListener(v -> {
            if (mCurrentPage == 0) {
                mFlowerErrorLayout.setVisibility(View.GONE);
                mFlowerLoadingLayout.setVisibility(View.VISIBLE);
                mFlowerIndicatorView.show();
            }
            mTextFlowerViewModel.loadMaterials(mCurrentPage);
        });

        mTextFlowerViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        mTextFlowerViewModel.getDownloadSuccess().observe(this, downloadFlowerInfo -> {
            relativeLayout.setBackground(mActivity.getResources().getDrawable(R.drawable.text_bubble_normal_bg));
            SmartLog.d(TAG, "success:" + downloadFlowerInfo.getMaterialBean().getLocalPath());
            textFlowerAdapter.removeDownloadMaterial(downloadFlowerInfo.getContentId());
            int downloadPosition = downloadFlowerInfo.getPosition();
            int dataPosition = downloadFlowerInfo.getDataPosition();
            if (downloadPosition >= 0 && dataPosition < mFlowerList.size()
                && downloadFlowerInfo.getContentId().equals(mFlowerList.get(dataPosition).getId())) {
                mFlowerList.set(dataPosition, downloadFlowerInfo.getMaterialBean());
                textFlowerAdapter.notifyDataSetChanged();
                if (downloadPosition == textFlowerAdapter.getSelectPosition()) {
                    setFlowerPath(downloadFlowerInfo.getMaterialBean().getLocalPath());
                    setEditPanelFlower(downloadFlowerInfo.getMaterialBean());
                }
            }
        });

        mTextFlowerViewModel.getFontColumn().observe(this, fontColumn -> {
            textPanelViewModel.setFontColumn(fontColumn);
        });

        mTextFlowerViewModel.getDownloadFail().observe(this, downloadFlowerInfo -> {
            textFlowerAdapter.removeDownloadMaterial(downloadFlowerInfo.getContentId());
            updateFail(downloadFlowerInfo);
            ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
        });

        mTextFlowerViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void setFlowerPath(String path) {
        HVEAsset asset = previewViewModel.getSelectedAsset();
        if (asset == null && previewViewModel.isAddCoverTextStatus()) {
            asset = mMaterialEditViewModel.getSelectAsset();
        }
        if (asset == null) {
            return;
        }
        if (asset instanceof HVEWordAsset) {
            HVEWordAsset wordAsset = (HVEWordAsset) asset;
            if (TextUtils.isEmpty(path)) {
                wordAsset.removeFlowerWord();
            } else {
                wordAsset.setFlowerWord(path);
            }
            previewViewModel.updateTimeLine();
            mMaterialEditViewModel.refresh();
        }
    }

    private void setEditPanelFlower(CloudMaterialBean cutContent) {
        textPanelViewModel.setFlowerContent(cutContent);
    }

    private void updateFail(MaterialsDownloadInfo downloadFlowerInfo) {
        int downloadPosition = downloadFlowerInfo.getPosition();
        int dataPosition = downloadFlowerInfo.getDataPosition();
        if (downloadPosition >= 0 && dataPosition < mFlowerList.size()
            && downloadFlowerInfo.getContentId().equals(mFlowerList.get(dataPosition).getId())) {
            mFlowerList.set(dataPosition, downloadFlowerInfo.getMaterialBean());
            textFlowerAdapter.notifyItemChanged(downloadPosition);
        }
        ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
    }

    private void updateProgress(MaterialsDownloadInfo downloadFlowerInfo) {
        int downloadPosition = downloadFlowerInfo.getPosition();
        if (downloadPosition < 0) {
            return;
        }
        if (downloadFlowerInfo.getDataPosition() < mFlowerList.size() && downloadFlowerInfo.getContentId()
            .equals(mFlowerList.get(downloadFlowerInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mFlowerRecyclerView.findViewHolderForAdapterPosition(downloadFlowerInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mHwProgressBar = viewHolder.itemView.findViewById(R.id.item_progress);
                mHwProgressBar.setProgress(downloadFlowerInfo.getProgress());
            }
        }
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initTimeoutState();
            return false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        previewViewModel.destroyTimeoutManager();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        if (previewViewModel != null) {
            previewViewModel.destroyTimeoutManager();
        }
    }
}
