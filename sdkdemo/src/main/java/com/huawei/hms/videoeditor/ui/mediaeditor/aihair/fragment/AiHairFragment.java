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

package com.huawei.hms.videoeditor.ui.mediaeditor.aihair.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.effect.impl.HairDyeingEffect;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonProgressDialog;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.aihair.FilterLinearLayoutManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.aihair.adapter.AiHairAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.aihair.viewmodel.AiHairViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.MySeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AiHairFragment extends BaseFragment {
    private static final String TAG = "AiHairFragment";

    private AiHairViewModel mAiHairViewModel;

    private ImageView mIvCertain;

    private RecyclerView mRecyclerView;

    private TextView mTvIntensity;

    private MySeekBar mSeekBar;

    private LoadingIndicatorView mIndicatorView;

    private LinearLayout mErrorLayout;

    private TextView mTvError;

    private View mAiHairNone;

    private List<HVEColumnInfo> currentAiHairColumnList;

    private List<CloudMaterialBean> currentAiHairContentList;

    private AiHairAdapter mAiHairAdapter;

    private int mCurrentIndex = 0;

    private int mCurrentPage = 0;

    private int mCurrentSelectPosition = -1;

    private Boolean mHasNextPage = false;

    private boolean isFirst;

    HuaweiVideoEditor mEditor;

    private EditPreviewViewModel mEditPreviewViewModel;

    private VideoClipsActivity.TimeOutOnTouchListener mOnAiHairTouchListener;

    private HVEAsset currentSelectedAsset;

    private String currentSelectPath;

    private int currentSelectedColorProgress = 80;

    private String currentSelectedName;

    private CloudMaterialBean currentSelectedContent;

    private CommonProgressDialog aiHairProgressDialog;

    public static AiHairFragment newInstance() {
        AiHairFragment aiHairFragment = new AiHairFragment();
        return aiHairFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_ai_hair;
    }

    @Override
    protected void initView(View view) {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mAiHairViewModel = new ViewModelProvider(mActivity, mFactory).get(AiHairViewModel.class);
        mEditor = mEditPreviewViewModel.getEditor();

        mOnAiHairTouchListener = ev -> {
            initTimeoutState();
            return false;
        };

        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (currentSelectedAsset == null) {
            return;
        }

        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(mOnAiHairTouchListener);

        TextView mTvTitle = view.findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.cut_second_menu_ai_hair);
        mIvCertain = view.findViewById(R.id.iv_certain);
        mRecyclerView = view.findViewById(R.id.recycler_view_ai_hair);
        mTvIntensity = view.findViewById(R.id.intensity_ai_hair);
        mSeekBar = view.findViewById(R.id.seek_bar_ai_hair);
        mErrorLayout = view.findViewById(R.id.error_layout_ai_hair);
        mTvError = view.findViewById(R.id.error_text_ai_hair);
        mIndicatorView = view.findViewById(R.id.indicator_ai_hair);
        mIndicatorView.show();
    }

    @Override
    protected void initObject() {
        List<HVEEffect> hveEffectList = new ArrayList<>();

        if (currentSelectedAsset != null) {
            hveEffectList = currentSelectedAsset.getEffectsWithType(HVEEffect.HVEEffectType.HAIR_DYEING);
        }
        if (!hveEffectList.isEmpty()) {
            HVEEffect hveEffect = hveEffectList.get(0);
            if (hveEffect instanceof HairDyeingEffect) {
                currentSelectPath = ((HairDyeingEffect) hveEffect).getColorAssetPath();
                Log.i(TAG, "initObject: currentSelectPath==" + currentSelectPath);
                currentSelectedColorProgress = ((HairDyeingEffect) hveEffect).getStrength();
                Log.i(TAG, "initObject: currentSelectedColorProgress==" + currentSelectedColorProgress);

                if (currentSelectedAsset.getType() == HVEAsset.HVEAssetType.VIDEO) {
                    mSeekBar.setVisibility(View.INVISIBLE);
                }
                mSeekBar.setProgress(currentSelectedColorProgress);
            }
        }

        View mNoneHeaderView =
            LayoutInflater.from(requireContext()).inflate(R.layout.adapter_add_mask_header, null, false);
        mNoneHeaderView.setLayoutParams(
            new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(context, 58F), SizeUtils.dp2Px(context, 75F)));
        mAiHairNone = mNoneHeaderView.findViewById(R.id.rl_cancel_mask_header);
        mAiHairNone.setSelected(true);
        currentAiHairColumnList = new ArrayList<>();
        currentAiHairContentList = new ArrayList<>();
        mAiHairAdapter = new AiHairAdapter(mActivity, currentAiHairContentList, R.layout.adapter_add_ai_hair_item);
        mAiHairAdapter.addHeaderView(mNoneHeaderView);
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                    SizeUtils.dp2Px(mActivity, 75F), SizeUtils.dp2Px(mActivity, 8F)));
        }
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new FilterLinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAiHairAdapter);
    }

    @Override
    protected void initData() {
        mAiHairViewModel.setFatherColumn(HVEMaterialConstant.AI_HAIR_FATHER_COLUMN);
        mAiHairViewModel.initAiHairTitleColumns();
    }

    @Override
    protected void initEvent() {
        mAiHairViewModel.getAiHairTitleColumns().observe(getViewLifecycleOwner(), list -> {
            mIndicatorView.hide();
            if (list != null && list.size() > 0) {
                currentAiHairColumnList.clear();
                currentAiHairColumnList.addAll(list);
                mCurrentIndex = 0;
                HVEColumnInfo aiHairTitle = currentAiHairColumnList.get(mCurrentIndex);
                currentAiHairContentList.clear();

                mAiHairViewModel.loadAiHairMaterials(aiHairTitle.getColumnId(), mCurrentPage);
            }
        });

        mAiHairViewModel.getErrorText().observe(this, errorText -> {
            mIndicatorView.hide();
            if (!TextUtils.isEmpty(errorText) && currentAiHairContentList.size() == 0) {
                mTvError.setText(errorText);
                mErrorLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mTvIntensity.setVisibility(View.INVISIBLE);
                mSeekBar.setVisibility(View.INVISIBLE);
                mAiHairAdapter.setSelectPosition(-1);
                currentAiHairContentList.clear();
                mAiHairAdapter.notifyDataSetChanged();
            }
        });

        mAiHairViewModel.getEmptyText().observe(getViewLifecycleOwner(), emptyText -> {
            if (TextUtils.isEmpty(emptyText)) {
                mIndicatorView.hide();
                ToastWrapper.makeText(context, context.getString(R.string.result_empty), Toast.LENGTH_SHORT).show();
                if (mCurrentPage == 0) {
                    mAiHairAdapter.setSelectPosition(-1);
                    currentAiHairContentList.clear();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mErrorLayout.setVisibility(View.INVISIBLE);
                    mTvIntensity.setVisibility(View.INVISIBLE);
                    mSeekBar.setVisibility(View.INVISIBLE);
                    mAiHairNone.setSelected(true);
                }
                mAiHairAdapter.notifyDataSetChanged();
            }
        });

        mAiHairViewModel.getPageData().observe(getViewLifecycleOwner(), mAiHairMaterials -> {
            if (mCurrentPage == 0) {
                currentAiHairContentList.clear();
            }
            if (!currentAiHairContentList.containsAll(mAiHairMaterials)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                currentAiHairContentList.addAll(mAiHairMaterials);

                if ((!TextUtils.isEmpty(currentSelectPath)) && FileUtil.isPathExist(currentSelectPath)) {
                    for (int i = 0; i < currentAiHairContentList.size(); i++) {
                        if (currentSelectPath.equals(currentAiHairContentList.get(i).getLocalPath())) {
                            mCurrentSelectPosition = i + 1;
                            mAiHairAdapter.setSelectPosition(mCurrentSelectPosition);
                            mTvIntensity.setVisibility(View.VISIBLE);
                            if (currentSelectedAsset.getType() == HVEAsset.HVEAssetType.IMAGE) {
                                mSeekBar.setVisibility(View.VISIBLE);
                            }
                            mSeekBar.setProgress(currentSelectedColorProgress);
                            mAiHairNone.setSelected(false);
                        }
                    }
                }
                mAiHairAdapter.notifyDataSetChanged();
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
                mTvIntensity.setVisibility(View.INVISIBLE);
                mSeekBar.setVisibility(View.INVISIBLE);
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorLayout.setVisibility(View.INVISIBLE);
            mIndicatorView.hide();

        });

        mAiHairViewModel.getHasNextPageData().observe(this, aBoolean -> mHasNextPage = aBoolean);

        mAiHairViewModel.getDownloadSuccess().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "getDownloadSuccess");
            mAiHairAdapter.removeDownloadMap(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getPosition();
            if (downloadPosition >= 0
                && (downloadInfo.getDataPosition() < currentAiHairContentList.size() && downloadInfo.getContentId()
                    .equals(currentAiHairContentList.get(downloadInfo.getDataPosition()).getId()))) {
                mAiHairNone.setSelected(false);
                mAiHairAdapter.setSelectPosition(downloadPosition);

                currentSelectedContent = downloadInfo.getMaterialBean();
                if (currentSelectedContent == null) {
                    return;
                }
                currentAiHairContentList.set(downloadInfo.getDataPosition(), currentSelectedContent);
                mAiHairAdapter.notifyDataSetChanged();
                mAiHairNone.setSelected(false);
                mErrorLayout.setVisibility(View.INVISIBLE);
                mTvIntensity.setVisibility(View.VISIBLE);
                if (currentSelectedAsset.getType() == HVEAsset.HVEAssetType.IMAGE) {
                    mSeekBar.setVisibility(View.VISIBLE);
                }

                mSeekBar.setProgress(80);
                if (downloadPosition == mAiHairAdapter.getSelectPosition()) {
                    currentSelectedName = currentSelectedContent.getName();
                    currentSelectPath = currentSelectedContent.getLocalPath();
                    if (TextUtils.isEmpty(currentSelectedName) || TextUtils.isEmpty(currentSelectPath)) {
                        return;
                    }

                    currentSelectedColorProgress = (int) mSeekBar.getProgress();
                    adjustHairDyeingEffect(currentSelectPath, currentSelectedColorProgress, false);
                }
            }

        });

        mAiHairViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        mAiHairViewModel.getDownloadFail().observe(this, downloadInfo -> {
            mAiHairAdapter.removeDownloadMap(downloadInfo.getContentId());
            recoverProgress(downloadInfo);
            ToastWrapper.makeText(mActivity, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();

        });

        mErrorLayout.setOnClickListener(v -> {
            if (mCurrentPage == 0) {
                mErrorLayout.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mTvIntensity.setVisibility(View.INVISIBLE);
                mSeekBar.setVisibility(View.INVISIBLE);
                mIndicatorView.show();
            }
            if (currentAiHairColumnList.size() > 0) {
                HVEColumnInfo content = currentAiHairColumnList.get(mCurrentIndex);
                mCurrentPage = 0;
                mAiHairViewModel.loadAiHairMaterials(content.getColumnId(), mCurrentPage);
            } else {
                mAiHairViewModel.initAiHairTitleColumns();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isSlidingToLeft = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && manager != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount();

                    if (lastItemPosition == (itemCount - 1) && isSlidingToLeft) {
                        if (mCurrentIndex > 0) {
                            mCurrentPage++;
                            mAiHairViewModel.loadAiHairMaterials(currentAiHairContentList.get(mCurrentIndex).getId(),
                                mCurrentPage);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLeft = dx > 0;
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null) {
                    int visibleItemCount = manager.getChildCount();
                    int firstPosition = manager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && !isFirst
                        && currentAiHairContentList.size() > 0) {
                        isFirst = true;
                        for (int i = 0; i < visibleItemCount - 1; i++) {
                            CloudMaterialBean cutContent = currentAiHairContentList.get(i);
                            mAiHairAdapter.addFirstScreenData(cutContent);
                        }
                    }
                }
            }
        });

        mAiHairAdapter.setOnItemClickListener(new AiHairAdapter.OnAiHairAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(int position, int dataPosition) {
                mAiHairNone.setSelected(false);
                mErrorLayout.setVisibility(View.INVISIBLE);
                mTvIntensity.setVisibility(View.VISIBLE);
                if (currentSelectedAsset.getType() == HVEAsset.HVEAssetType.IMAGE) {
                    mSeekBar.setVisibility(View.VISIBLE);
                }

                mSeekBar.setProgress(80);
                int mSelectPosition = mAiHairAdapter.getSelectPosition();
                if (mSelectPosition != position) {
                    mAiHairAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        mAiHairAdapter.notifyItemChanged(mSelectPosition);
                    }
                    mAiHairAdapter.notifyItemChanged(position);
                    currentSelectedContent = currentAiHairContentList.get(dataPosition);
                    if (currentSelectedContent == null) {
                        return;
                    }
                    currentSelectedName = currentSelectedContent.getName();
                    currentSelectPath = currentSelectedContent.getLocalPath();
                    if (TextUtils.isEmpty(currentSelectedName) || TextUtils.isEmpty(currentSelectPath)) {
                        return;
                    }
                    currentSelectedColorProgress = (int) mSeekBar.getProgress();

                    adjustHairDyeingEffect(currentSelectPath, currentSelectedColorProgress, false);
                }
            }

            @Override
            public void onItemDownloadClick(int position, int dataPosition) {
                int previousPosition = mAiHairAdapter.getSelectPosition();
                mAiHairAdapter.setSelectPosition(position);
                CloudMaterialBean content = currentAiHairContentList.get(dataPosition);

                mAiHairAdapter.addDownloadMap(content);
                mAiHairViewModel.downloadAiHairMaterial(previousPosition, position, dataPosition, content);
            }
        });

        mSeekBar.setOnProgressChangedListener(progress -> {
            currentSelectedColorProgress = progress;
            mEditPreviewViewModel.setToastTime(progress + "");
            adjustHairDyeingEffect(currentSelectPath, currentSelectedColorProgress, true);

        });

        mSeekBar.setcTouchListener(isTouch -> {
            if (!isTouch && currentSelectedContent != null) {
                adjustHairDyeingEffect(currentSelectPath, currentSelectedColorProgress, true);
            }
            mEditPreviewViewModel.setToastTime(isTouch ? currentSelectedColorProgress + "" : "");
        });

        mIvCertain.setOnClickListener(new OnClickRepeatedListener(v -> mActivity.onBackPressed()));

        mAiHairNone.setOnClickListener(v -> {
            mAiHairNone.setContentDescription(getString(R.string.no_filter));
            mAiHairNone.setSelected(true);
            if (mAiHairNone.isSelected()) {
                mTvIntensity.setVisibility(View.INVISIBLE);
                mSeekBar.setVisibility(View.INVISIBLE);
                int mSelectPosition = mAiHairAdapter.getSelectPosition();
                mAiHairAdapter.setSelectPosition(-1);
                if (mSelectPosition != -1) {
                    mAiHairAdapter.notifyItemChanged(mSelectPosition);
                }

                if (!(currentSelectedAsset instanceof HVEVisibleAsset)) {
                    return;
                }

                boolean isRemoved = ((HVEVisibleAsset) currentSelectedAsset).removeHairDyeingEffect();
                if (isRemoved) {
                    ToastWrapper.makeText(context, getResources().getString(R.string.ai_hair_cancel)).show();
                    refreshMainLaneAndUIData();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(mActivity instanceof VideoClipsActivity)) {
            return;
        }
        if (mEditPreviewViewModel == null) {
            mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        }
        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_266;
    }

    @Override
    public void onBackPressed() {
        if (!(mActivity instanceof VideoClipsActivity)) {
            return;
        }
        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(mOnAiHairTouchListener);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (!(mActivity instanceof VideoClipsActivity)) {
            return;
        }
        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(mOnAiHairTouchListener);
        mEditPreviewViewModel.destroyTimeoutManager();
        super.onDestroy();
    }

    private void updateProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < currentAiHairContentList.size()
            && downloadInfo.getContentId()
                .equals(currentAiHairContentList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.item_progress_ai_hair);
                mDownloadPb.setProgress(downloadInfo.getProgress());
            }
        }
    }

    private void recoverProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < currentAiHairContentList.size()
            && downloadInfo.getContentId()
                .equals(currentAiHairContentList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.item_progress_mask_effect);
                if (mDownloadPb != null) {
                    mDownloadPb.setVisibility(View.INVISIBLE);
                }
                ImageView mDownloadIv = viewHolder.itemView.findViewById(R.id.item_download_view_mask_effect);
                if (mDownloadIv != null) {
                    mDownloadIv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void adjustHairDyeingEffect(String colorMapPath, int strength, boolean isStrength) {
        SmartLog.i(TAG, "adjustHairDyeingEffect:colorMapPath==" + colorMapPath);
        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (!(currentSelectedAsset instanceof HVEVisibleAsset)) {
            return;
        }

        if (isStrength) {
            boolean isSuccess = ((HVEVisibleAsset) currentSelectedAsset).setHairDyeingStrength(strength);
            if (isSuccess) {
                refreshMainLaneAndUIData();
            } else {
                showToast();
            }
        } else {
            initAIFunProgressDialog();

            ((HVEVisibleAsset) currentSelectedAsset).addHairDyeingEffect(new HVEAIProcessCallback() {
                @Override
                public void onProgress(int progress) {
                    SmartLog.i(TAG, "onAICloudProgress==" + progress);
                    if (mActivity == null || !isValidActivity()) {
                        return;
                    }
                    mActivity.runOnUiThread(() -> {
                        if (aiHairProgressDialog != null) {
                            aiHairProgressDialog.updateProgress(progress);
                        }
                    });
                }

                @Override
                public void onSuccess() {
                    if (mActivity == null) {
                        return;
                    }

                    mActivity.runOnUiThread(() -> {
                        if (aiHairProgressDialog != null) {
                            aiHairProgressDialog.updateProgress(0);
                            aiHairProgressDialog.dismiss();
                        }
                        refreshMainLaneAndUIData();
                    });
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    showToast();
                }
            }, currentSelectPath, currentSelectedColorProgress);
        }
    }

    private void initAIFunProgressDialog() {
        if (!isValidActivity()) {
            return;
        }

        aiHairProgressDialog = new CommonProgressDialog(mActivity, () -> {
            ((HVEVisibleAsset) currentSelectedAsset).interruptHairDyeing();
        });

        aiHairProgressDialog.setTitleValue(getString(R.string.ai_hair_generated));

        aiHairProgressDialog.setCanceledOnTouchOutside(false);
        aiHairProgressDialog.setCancelable(false);
        aiHairProgressDialog.show();
    }

    private void refreshMainLaneAndUIData() {
        if (!isValidActivity()) {
            return;
        }
        if (mEditPreviewViewModel == null || mEditor == null) {
            return;
        }
        mActivity.runOnUiThread(() -> {
            mEditPreviewViewModel.updateTimeLine();
            mEditor.seekTimeLine(mEditor.getTimeLine().getCurrentTime());
        });
    }

    private void showToast() {
        if (!isValidActivity()) {
            return;
        }
        mActivity.runOnUiThread(
            () -> ToastWrapper.makeText(mActivity, mActivity.getString(R.string.ai_hair_fail), Toast.LENGTH_SHORT)
                .show());
    }
}