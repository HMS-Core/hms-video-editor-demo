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
import java.util.Locale;

import android.annotation.SuppressLint;
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
import com.huawei.hms.videoeditor.ui.common.utils.Utils;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.adapter.TextBubblesAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextBubblesViewModel;
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

public class EditTextBubblesFragment extends BaseFragment {
    private static final String TAG = "EditTextBubblesFragment";

    protected EditPreviewViewModel mEditPreviewBubblesViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    protected TextBubblesViewModel mTextBubblesViewModel;

    private RelativeLayout mBubblesErrorLayout;

    private TextView mBubblesErrorTv;

    private ConstraintLayout mBubblesLoadingLayout;

    private LoadingIndicatorView mBubblesIndicatorView;

    private RecyclerView mBubblesRecyclerView;

    private List<CloudMaterialBean> mBubblesList;

    private TextBubblesAdapter textBubblesAdapter;

    private int mCurrentPage = 0;

    private Boolean mHasNextPage = false;

    private View cancelBubblesHeader;

    private boolean isScrolled = false;

    private RelativeLayout rlHead;

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
        return R.layout.fragment_edit_text_bubbles;
    }

    @Override
    protected void initView(View view) {
        mBubblesErrorLayout = view.findViewById(R.id.error_layout);
        mBubblesErrorTv = view.findViewById(R.id.error_text);
        mBubblesLoadingLayout = view.findViewById(R.id.loading_layout);
        mBubblesIndicatorView = view.findViewById(R.id.indicator);
        mBubblesRecyclerView = view.findViewById(R.id.rl_pic);
    }

    @Override
    protected void initObject() {
        setTimeoutEnable();
        mEditPreviewBubblesViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mTextBubblesViewModel = new ViewModelProvider(this, mFactory).get(TextBubblesViewModel.class);
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

        mBubblesList = new ArrayList<>();
        textBubblesAdapter = new TextBubblesAdapter(mActivity, mBubblesList, R.layout.adapter_edittext_bubbles_item);
        mBubblesRecyclerView
            .setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        int lineCount = 4;
        if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(mActivity)) {
            lineCount = 7;
        } else {
            boolean isPortrait = ScreenUtil.isPortrait(context);
            if (!isPortrait) {
                lineCount = 8;
            }
        }
        GridLayoutManager bubblesLayoutManager = new GridLayoutManager(mActivity, lineCount);
        if (mBubblesRecyclerView.getItemDecorationCount() == 0) {
            mBubblesRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(mActivity, 8f),
                SizeUtils.dp2Px(mActivity, 8f), ContextCompat.getColor(mActivity, R.color.transparent)));
        }
        mBubblesRecyclerView.setLayoutManager(bubblesLayoutManager);
        mBubblesRecyclerView.setItemAnimator(null);
        mBubblesRecyclerView.setAdapter(textBubblesAdapter);
        cancelBubblesHeader =
            LayoutInflater.from(mActivity).inflate(R.layout.adapter_bubbles_cancel_header, mBubblesRecyclerView, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widthHeight, widthHeight);
        cancelBubblesHeader.setLayoutParams(layoutParams);
        textBubblesAdapter.addHeaderView(cancelBubblesHeader);
        rlHead = cancelBubblesHeader.findViewById(R.id.rl_head);

        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchBubblesListener);
    }

    @Override
    protected void initData() {
        mBubblesLoadingLayout.setVisibility(View.VISIBLE);
        mBubblesIndicatorView.show();
        mTextBubblesViewModel.loadMaterials(mCurrentPage);
        mTextBubblesViewModel.getPageData().observe(this, materialsCutContents -> {
            if (mCurrentPage == 0) {
                mBubblesLoadingLayout.setVisibility(View.GONE);
                mBubblesIndicatorView.hide();
                mBubblesList.clear();
                textBubblesAdapter.setSelectPosition(-1);
            }
            if (!mBubblesList.containsAll(materialsCutContents)) {
                SmartLog.d(TAG, "materialsCutContents is not exist.");
                mBubblesList.addAll(materialsCutContents);
                textBubblesAdapter.notifyDataSetChanged();
            } else {
                SmartLog.d(TAG, "materialsCutContents is exist.");
            }
        });
        mTextBubblesViewModel.getErrorString().observe(this, errorString -> {
            if (!TextUtils.isEmpty(errorString) && mBubblesList.size() == 0) {
                mBubblesRecyclerView.setVisibility(View.GONE);
                mBubblesLoadingLayout.setVisibility(View.GONE);
                mBubblesIndicatorView.hide();
                mBubblesErrorTv.setText(errorString);
                mBubblesErrorLayout.setVisibility(View.VISIBLE);
            }
        });

        mTextBubblesViewModel.getEmptyString().observe(this, emptyString -> {
            if (mCurrentPage == 0) {
                mBubblesLoadingLayout.setVisibility(View.GONE);
                mBubblesIndicatorView.hide();
            }
            ToastWrapper.makeText(mActivity, emptyString, Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
    @Override
    protected void initEvent() {
        cancelBubblesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlHead.setBackground(mActivity.getResources().getDrawable(R.drawable.edit_text_style_shape));
                setBubblesPath("");
                setEditPanelBubbles(null);
                int mSelectPosition = textBubblesAdapter.getSelectPosition();
                textBubblesAdapter.setSelectPosition(-1);
                if (mSelectPosition != -1) {
                    textBubblesAdapter.notifyItemChanged(mSelectPosition);
                }
            }
        });

        textBubblesAdapter.setOnItemClickListener(new TextBubblesAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(int position, int dataPosition) {
                rlHead.setBackground(mActivity.getResources().getDrawable(R.drawable.text_bubble_normal_bg));
                int mSelectPosition = textBubblesAdapter.getSelectPosition();
                if (mSelectPosition == position) {
                    textBubblesAdapter.setSelectPosition(-1);
                    textBubblesAdapter.notifyItemChanged(position);
                }
                if (mSelectPosition != position) {
                    textBubblesAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        textBubblesAdapter.notifyItemChanged(mSelectPosition);
                    }
                    textBubblesAdapter.notifyItemChanged(position);
                    setBubblesPath(mBubblesList.get(dataPosition).getLocalPath());
                    setEditPanelBubbles(mBubblesList.get(dataPosition));
                }
            }

            @Override
            public void onDownloadClick(int position, int dataPosition) {
                int previousPosition = textBubblesAdapter.getSelectPosition();
                textBubblesAdapter.setSelectPosition(position);
                CloudMaterialBean bubblesContent = mBubblesList.get(dataPosition);

                textBubblesAdapter.addDownloadMaterial(bubblesContent);
                mTextBubblesViewModel.downloadColumn(previousPosition, position, dataPosition, bubblesContent);

            }
        });

        mBubblesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE
                    && textBubblesAdapter.getItemCount() >= textBubblesAdapter.getOriginalItemCount()) {
                    GridLayoutManager layoutBubblesManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (!isScrolled && mHasNextPage && layoutBubblesManager != null) {
                        int lastCompletelyVisibleItemPosition =
                            layoutBubblesManager.findLastCompletelyVisibleItemPosition();
                        if (lastCompletelyVisibleItemPosition == layoutBubblesManager.getItemCount() - 1) {
                            mCurrentPage++;
                            mTextBubblesViewModel.loadMaterials(mCurrentPage);
                            isScrolled = false;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager bubblesManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && bubblesManager != null) {
                    int visibleItemPosition = bubblesManager.findLastCompletelyVisibleItemPosition();
                    if (visibleItemPosition == bubblesManager.getItemCount() - 1 && dy > 0) {
                        mCurrentPage++;
                        mTextBubblesViewModel.loadMaterials(mCurrentPage);
                        isScrolled = true;
                    }
                }

                if (bubblesManager != null) {
                    int visibleItemCount = bubblesManager.getChildCount();
                    int firstPosition = bubblesManager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst && mBubblesList.size() > 0) {
                        isFirst = true;
                        for (int i = 0; i < visibleItemCount - 1; i++) {
                            CloudMaterialBean cutContent = mBubblesList.get(i);
                            textBubblesAdapter.addFirstScreenMaterial(cutContent);
                        }
                    }
                }
            }
        });

        mBubblesErrorLayout.setOnClickListener(v -> {
            mBubblesRecyclerView.setVisibility(View.VISIBLE);
            if (mCurrentPage == 0) {
                mBubblesErrorLayout.setVisibility(View.GONE);
                mBubblesLoadingLayout.setVisibility(View.VISIBLE);
                mBubblesIndicatorView.show();
            }
            mTextBubblesViewModel.loadMaterials(mCurrentPage);
        });

        mTextBubblesViewModel.getDownloadProgress().observe(this, downloadBubblesInfo -> {
            SmartLog.d(TAG, "progress:" + downloadBubblesInfo.getProgress());
            updateProgress(downloadBubblesInfo);
        });

        mTextBubblesViewModel.getFontColumn().observe(this, fontColumn -> {
            textPanelViewModel.setFontColumn(fontColumn);
        });

        mTextBubblesViewModel.getDownloadSuccess().observe(this, downloadBubblesInfo -> {
            rlHead.setBackground(mActivity.getResources().getDrawable(R.drawable.text_bubble_normal_bg));
            SmartLog.d(TAG, "success:" + downloadBubblesInfo.getMaterialBean().getLocalPath());
            textBubblesAdapter.removeDownloadMaterial(downloadBubblesInfo.getContentId());
            int downloadPosition = downloadBubblesInfo.getPosition();
            int dataPosition = downloadBubblesInfo.getDataPosition();
            if (downloadPosition >= 0 && dataPosition < mBubblesList.size()
                && downloadBubblesInfo.getContentId().equals(mBubblesList.get(dataPosition).getId())) {
                mBubblesList.set(dataPosition, downloadBubblesInfo.getMaterialBean());
                textBubblesAdapter.notifyDataSetChanged();
                if (downloadPosition == textBubblesAdapter.getSelectPosition()) {
                    setBubblesPath(downloadBubblesInfo.getMaterialBean().getLocalPath());
                    setEditPanelBubbles(downloadBubblesInfo.getMaterialBean());
                }
            }
        });

        mTextBubblesViewModel.getDownloadFail().observe(this, downloadBubblesInfo -> {
            textBubblesAdapter.removeDownloadMaterial(downloadBubblesInfo.getContentId());
            updateFail(downloadBubblesInfo);
            ToastWrapper.makeText(mActivity,
                downloadBubblesInfo.getMaterialBean().getName() + Utils.setNumColor(
                    String.format(Locale.ROOT, mActivity.getResources().getString(R.string.download_failed), 0),
                    mActivity.getResources().getColor(R.color.transparent)),
                Toast.LENGTH_SHORT).show();
        });

        mTextBubblesViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void setBubblesPath(String path) {
        HVEAsset asset = mEditPreviewBubblesViewModel.getSelectedAsset();
        if (asset == null && mEditPreviewBubblesViewModel.isAddCoverTextStatus()) {
            asset = mMaterialEditViewModel.getSelectAsset();
        }
        if (asset == null) {
            return;
        }
        if (asset instanceof HVEWordAsset) {
            HVEWordAsset wordAsset = (HVEWordAsset) asset;
            if (TextUtils.isEmpty(path)) {
                wordAsset.removeBubbleWord();
            } else {
                wordAsset.setBubbleWord(path);
            }

            mEditPreviewBubblesViewModel.updateTimeLine();
            mMaterialEditViewModel.refresh();
        }
    }

    private void setEditPanelBubbles(CloudMaterialBean cutContent) {
        textPanelViewModel.setBubblesContent(cutContent);
    }

    private void updateFail(MaterialsDownloadInfo downloadBubblesInfo) {
        int downloadPosition = downloadBubblesInfo.getPosition();
        if (downloadPosition >= 0 && downloadPosition < mBubblesList.size()
            && downloadBubblesInfo.getContentId().equals(mBubblesList.get(downloadPosition).getId())) {
            mBubblesList.set(downloadBubblesInfo.getDataPosition(), downloadBubblesInfo.getMaterialBean());
            textBubblesAdapter.notifyItemChanged(downloadPosition);
        }
        ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
    }

    private void updateProgress(MaterialsDownloadInfo downloadBubblesInfo) {
        int downloadPosition = downloadBubblesInfo.getPosition();
        if (downloadPosition < 0) {
            return;
        }
        if (downloadBubblesInfo.getDataPosition() < mBubblesList.size() && downloadBubblesInfo.getContentId()
            .equals(mBubblesList.get(downloadBubblesInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mBubblesRecyclerView.findViewHolderForAdapterPosition(downloadBubblesInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mHwProgressBar = viewHolder.itemView.findViewById(R.id.item_progress);
                mHwProgressBar.setProgress(downloadBubblesInfo.getProgress());
            }
        }
    }

    private void initBubblesTimeoutState() {
        mEditPreviewBubblesViewModel.initTimeoutManager();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchBubblesListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initBubblesTimeoutState();
            return false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchBubblesListener);
        }
        mEditPreviewBubblesViewModel.destroyTimeoutManager();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchBubblesListener);
        }
        if (mEditPreviewBubblesViewModel != null) {
            mEditPreviewBubblesViewModel.destroyTimeoutManager();
        }
    }
}
