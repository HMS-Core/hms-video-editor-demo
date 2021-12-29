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

package com.huawei.hms.videoeditor.ui.mediaeditor.blockface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.huawei.hms.videoeditor.common.utils.BitmapDecodeUtils;
import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEAIFaceTemplate;
import com.huawei.hms.videoeditor.sdk.materials.network.response.MaterialsCloudBean;
import com.huawei.hms.videoeditor.sdk.util.FileUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.sdk.v1.bean.AssetBean;
import com.huawei.hms.videoeditor.sdk.v1.json.Constants;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.utils.Utils;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.MaterialsRespository;
import com.huawei.hms.videoeditor.ui.mediaeditor.sticker.viewmodel.StickerItemViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.sticker.viewmodel.StickerPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditor.ui.mediapick.activity.PicturePickActivity;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.util.SafeBase64;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.huawei.hms.videoeditor.sdk.materials.network.response.MaterialsCutContentType.CUSTOM_STICKERS;

public class FaceBlockingFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "FaceBlockingFragment";

    public static final String CUSTOM_STICKER = "0";

    public static final String CLOUD_STICKER = "1";

    public static final String MOSAIC_STICKER = "2";

    public static final String NO_STICKER = "3";

    public static final String IS_FROM_FACE_BLOCKING = "is_from_face_blocking";

    private static final String OPERATE_ID = "operate_id";

    private TextView tvTitle;

    private TextView tvFaceNum;

    private TextView tvMaxFace;

    private RecyclerView recyclerViewFace;

    private FaceBlockingListAdapter mFaceBlockingListAdapter;

    private List<FaceBlockingInfo> mFaceBlockingInfoList = new ArrayList<>();

    private ImageView ivCancel;

    private ImageView ivChooseSticker;

    private ImageView ivMosaic;

    private RecyclerView recyclerViewSticker;

    private FaceStickerListAdapter mFaceStickerListAdapter;

    private View mStickerHeaderView;

    private List<FaceBlockingInfo> mStickerInfoList = new ArrayList<>();

    private TextView tvReset;

    private ConstraintLayout mLoadingLayout;

    private LoadingIndicatorView mIndicatorView;

    private RelativeLayout mErrorLayout;

    private TextView mErrorTv;

    private String mStickerPath = "";

    private boolean isDeleteSticker = false;

    private boolean isFirstLoadCustomSticker = true;

    private boolean isFirstLoadCloudSticker = true;

    private boolean isShowGray = true;

    private EditPreviewViewModel mEditPreviewViewModel;

    private FaceBlockingViewModel mFaceBlockingViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private StickerPanelViewModel mStickerPanelViewModel;

    private StickerItemViewModel mStickerItemViewModel;

    private VideoClipsPlayViewModel mSdkPlayViewModel;

    private HVEColumnInfo materialsCutContent;

    private List<HVEAIFaceTemplate> mFaceBoxList = new ArrayList<>();

    private List<MaterialsCloudBean> mStickerList = new ArrayList<>();

    private boolean mHasNextPage = false;

    private int mCurrentPage = 0;

    private boolean isScrolled = false;

    private String mStickerType = "";

    private HVEAsset mSelectedAsset;

    public static FaceBlockingFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(OPERATE_ID, id);
        FaceBlockingFragment fragment = new FaceBlockingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_face_blocking;
    }

    @Override
    protected void initView(View view) {
        view.findViewById(R.id.iv_certain).setOnClickListener(this);
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cut_second_menu_block_face);
        tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.clip_color_E6FFFFFF));
        tvFaceNum = view.findViewById(R.id.tv_face_num);
        tvMaxFace = view.findViewById(R.id.tv_max_face);
        recyclerViewFace = view.findViewById(R.id.recyclerView_face);
        recyclerViewSticker = view.findViewById(R.id.recyclerView_sticker);
        tvReset = view.findViewById(R.id.tv_reset);
        tvReset.setOnClickListener(this);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mErrorTv = view.findViewById(R.id.error_text);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mIndicatorView = view.findViewById(R.id.indicator);
    }

    @Override
    protected void initObject() {
        mStickerPanelViewModel = new ViewModelProvider(this, mFactory).get(StickerPanelViewModel.class);
        mStickerItemViewModel = new ViewModelProvider(mActivity, mFactory).get(StickerItemViewModel.class);
        mFaceBlockingViewModel = new ViewModelProvider(mActivity, mFactory).get(FaceBlockingViewModel.class);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mSdkPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);

        mFaceBlockingListAdapter =
                new FaceBlockingListAdapter(mActivity, mFaceBlockingInfoList, R.layout.adapter_block_face_item);
        recyclerViewFace.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        recyclerViewFace.addItemDecoration(
                new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.translucent_white_100),
                        SizeUtils.dp2Px(mActivity, 56), SizeUtils.dp2Px(mActivity, 8)));
        recyclerViewFace.setAdapter(mFaceBlockingListAdapter);
        recyclerViewFace.setNestedScrollingEnabled(false);

        mStickerHeaderView =
                LayoutInflater.from(mActivity).inflate(R.layout.adapter_face_sticker_head_view, null, false);
        mStickerHeaderView.setLayoutParams(
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2Px(context, 60)));
        ivChooseSticker = mStickerHeaderView.findViewById(R.id.iv_choose_sticker);
        ivChooseSticker.setOnClickListener(this);
        ivMosaic = mStickerHeaderView.findViewById(R.id.iv_mosaic);
        ivMosaic.setOnClickListener(this);
        ivMosaic.setEnabled(false);
        ivMosaic.setSelected(false);
        ivCancel = mStickerHeaderView.findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(this);
        ivCancel.setEnabled(false);
        ivCancel.setSelected(false);
        mFaceStickerListAdapter =
                new FaceStickerListAdapter(mActivity, mStickerInfoList, R.layout.adapter_block_face_sticker_item);
        mFaceStickerListAdapter.addHeaderView(mStickerHeaderView);
        recyclerViewSticker.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        recyclerViewSticker.addItemDecoration(
                new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.translucent_white_100),
                        SizeUtils.dp2Px(mActivity, 60), SizeUtils.dp2Px(mActivity, 6)));
        recyclerViewSticker.setAdapter(mFaceStickerListAdapter);
        recyclerViewSticker.setNestedScrollingEnabled(false);

        mEditPreviewViewModel.setFaceBlockingStatus(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mIndicatorView.show();
        recyclerViewSticker.setVisibility(View.GONE);
        if (mEditPreviewViewModel.getEditor() == null) {
            SmartLog.e(TAG, "HuaweiVideoEditor is null");
            return;
        }
        mSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (mSelectedAsset == null) {
            mSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (mSelectedAsset == null) {
            SmartLog.e(TAG, "SelectedAsset is null");
            return;
        }

        if (mStickerPanelViewModel != null) {
            mStickerPanelViewModel.initColumns(HVEMaterialConstant.STICKER_FACE);
            mStickerPanelViewModel.getColumns().observe(this, columns -> {
                if (columns.size() > 0) {
                    materialsCutContent = columns.get(0);
                    if (materialsCutContent != null) {
                        mStickerItemViewModel.loadMaterials(materialsCutContent.getColumnId(), mCurrentPage);
                    }
                }
            });
        }
        mStickerItemViewModel.getPageData().observe(this, materialsCutContents -> {
            if (mCurrentPage == 0) {
                recyclerViewSticker.setVisibility(View.VISIBLE);
                mLoadingLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);
                mIndicatorView.hide();
                mStickerList.clear();
                mStickerInfoList.clear();
                List<MaterialsCloudBean> localStickerList =
                        mFaceBlockingViewModel.getLocalMaterialsDataByType(CUSTOM_STICKERS);
                for (MaterialsCloudBean pageDatas : localStickerList) {
                    mEditPreviewViewModel.addBlockingSticker(pageDatas.getLocalPath());
                    mStickerInfoList.add(new FaceBlockingInfo("0", pageDatas.getLocalPath(), pageDatas, isShowGray));
                }
            }
            if (!mStickerList.containsAll(materialsCutContents)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                mStickerList.addAll(materialsCutContents);
                for (MaterialsCloudBean containsDatas : materialsCutContents) {
                    mStickerInfoList.add(new FaceBlockingInfo("1", parseStickerLocalPath(containsDatas.getLocalPath()),
                            containsDatas, isShowGray));
                }
                if (mFaceStickerListAdapter != null) {
                    mFaceStickerListAdapter.setSelectPosition(-1);
                    mFaceStickerListAdapter.notifyDataSetChanged();
                }
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
        });

        mStickerItemViewModel.getErrorType().observe(this, type -> {
            switch (type) {
                case MaterialsRespository.RESULT_ILLEGAL:
                    if (mCurrentPage == 0) {
                        mErrorTv.setText(getString(R.string.result_illegal));
                        mLoadingLayout.setVisibility(View.GONE);
                        mErrorLayout.setVisibility(View.VISIBLE);
                    }
                    mIndicatorView.hide();
                    break;
                case MaterialsRespository.RESULT_EMPTY:
                    if (mCurrentPage == 0) {
                        mIndicatorView.hide();
                        mStickerInfoList.clear();
                        List<MaterialsCloudBean> localStickerList =
                                mFaceBlockingViewModel.getLocalMaterialsDataByType(CUSTOM_STICKERS);
                        for (MaterialsCloudBean localDatas : localStickerList) {
                            mEditPreviewViewModel.addBlockingSticker(localDatas.getLocalPath());
                            mStickerInfoList
                                    .add(new FaceBlockingInfo("0", localDatas.getLocalPath(), localDatas, isShowGray));
                        }
                        if (mFaceStickerListAdapter != null) {
                            mFaceStickerListAdapter.setSelectPosition(-1);
                            mFaceStickerListAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                default:
                    break;
            }
        });

        mEditPreviewViewModel.getBlockingStickerList().observe(this, strings -> {
            List<String> pathList = getAllStickerPaths();
            for (String string : strings) {
                if (pathList.size() > 0) {
                    if (!pathList.contains(string)) {
                        FaceBlockingInfo blockingInfo = new FaceBlockingInfo();
                        blockingInfo.setType(CUSTOM_STICKER);
                        blockingInfo.setLocalSticker(string);
                        blockingInfo.setShowGray(getSelectedFacesNum() <= 0);
                        MaterialsCloudBean content =
                                mFaceBlockingViewModel.addStickerCustomToLocal(System.currentTimeMillis() + "", string);
                        if (content != null) {
                            blockingInfo.setMaterialsCutContent(content);
                        }
                        mStickerInfoList.add(0, blockingInfo);
                    }
                } else {
                    FaceBlockingInfo blockingInfo = new FaceBlockingInfo();
                    blockingInfo.setType(CUSTOM_STICKER);
                    blockingInfo.setLocalSticker(string);
                    blockingInfo.setShowGray(getSelectedFacesNum() <= 0);
                    MaterialsCloudBean content =
                            mFaceBlockingViewModel.addStickerCustomToLocal(System.currentTimeMillis() + "", string);
                    if (content != null) {
                        blockingInfo.setMaterialsCutContent(content);
                    }
                    mStickerInfoList.add(0, blockingInfo);
                }
            }
            if (mFaceStickerListAdapter != null) {
                if (isFirstLoadCustomSticker) {
                    isFirstLoadCustomSticker = false;
                    mFaceStickerListAdapter.setSelectPosition(-1);
                    mFaceStickerListAdapter.notifyItemInserted(0);
                } else {
                    mFaceStickerListAdapter.notifyItemInserted(0);
                    if (isDeleteSticker) {
                        isDeleteSticker = false;
                        mFaceStickerListAdapter.setSelectPosition(-1);
                    } else if (mFaceStickerListAdapter.getSelectPosition() != -1) {
                        mFaceStickerListAdapter.setSelectPosition(mFaceStickerListAdapter.getSelectPosition() + 1);
                    }
                }
                mFaceStickerListAdapter.notifyDataSetChanged();
            }
        });

        mFaceBlockingViewModel.getFaceBlockingList().observe(this, faceTemplatesList -> {

            mFaceBoxList = faceTemplatesList;
            if (mFaceBoxList == null || mFaceBoxList.isEmpty()) {
                SmartLog.e(TAG, "NO Face Data");
                return;
            }
            if (mSelectedAsset instanceof HVEVisibleAsset) {
                for (HVEAIFaceTemplate faceBox : mFaceBoxList) {
                    FaceBlockingInfo faceBlockingInfo = new FaceBlockingInfo();
                    faceBlockingInfo.setId(faceBox.getId());
                    faceBlockingInfo.setBitmap(base64ToBitmap(faceBox.getBase64Img()));
                    faceBlockingInfo.setFirstTimeStamp(faceBox.getFirstTimeStamp());
                    faceBlockingInfo.setFaceTemplates(faceBox);
                    mFaceBlockingInfoList.add(faceBlockingInfo);
                }
                for (FaceBlockingInfo info : mFaceBlockingInfoList) {
                    if (info.getType() == null) {
                        info.setMosaic(false);
                        info.setType(NO_STICKER);
                    }
                }
                setEffectToFace();
                if (mFaceBlockingListAdapter != null) {
                    mFaceBlockingListAdapter.notifyDataSetChanged();
                }
            }
            tvMaxFace.setText(NumberFormat.getInstance().format(mFaceBlockingInfoList.size()));
        });

        mFaceBlockingListAdapter.setFaceSelectedListener((faceBlockingInfo, position) -> {
            isFirstLoadCloudSticker = false;
            seekTimeLineOfFace(faceBlockingInfo.getFirstTimeStamp());
            if (!faceBlockingInfo.isSelected()) {
                faceBlockingInfo.setGetFocus(false);
            } else {
                confirmFaceSticker(mFaceBlockingInfoList, faceBlockingInfo);
            }
            if (!faceBlockingInfo.isGetFocus()) {
                isStickerUsed(mFaceBlockingInfoList, faceBlockingInfo);
            }
            int index = getSelectedFacesNum();
            if (index > 0) {
                isShowGray = false;
                ivCancel.setImageResource(R.drawable.icon_cancel_wu);
                ivCancel.setEnabled(true);
                ivCancel.setSelected(false);
                ivMosaic.setImageResource(R.drawable.bg_face_add_mosaic);
                ivMosaic.setEnabled(true);
                ivMosaic.setSelected(false);
                for (FaceBlockingInfo info : mStickerInfoList) {
                    info.setShowGray(false);
                }
                if (mFaceStickerListAdapter != null) {
                    mFaceStickerListAdapter.notifyDataSetChanged();
                }
            } else {
                cancelAllStickers(false);
            }
            if (index > 20) {
                String maxFace = mActivity.getResources().getQuantityString(R.plurals.face_blocking_max_face, 20, 20);
                ToastWrapper.makeText(mActivity, maxFace, Toast.LENGTH_SHORT).show();
                faceBlockingInfo.setSelected(false);
            } else {
                tvFaceNum.setText(NumberFormat.getInstance().format(getCheckedFacesNum()));
            }
            if (index >= mFaceBlockingInfoList.size()) {
                tvFaceNum.setTextColor(ContextCompat.getColor(mActivity, R.color.color_text_focus));
            } else {
                tvFaceNum.setTextColor(ContextCompat.getColor(mActivity, R.color.color_fff_60));
            }
            if (mFaceBlockingListAdapter != null) {
                mFaceBlockingListAdapter.notifyItemChanged(position);
            }
        });

        mFaceStickerListAdapter.setStickerSelectedListener(new FaceStickerListAdapter.OnStickerSelectedListener() {

            @Override
            public void onStickerSelected(FaceBlockingInfo faceBlockingInfo, int position, int dataPosition) {
                int size = 0;
                for (FaceBlockingInfo info : mFaceBlockingInfoList) {
                    if (info.isSelected() || info.isGetFocus()) {
                        size++;
                    }
                }
                if (size == 0) {
                    return;
                }
                mStickerType = faceBlockingInfo.getType();
                ivCancel.setSelected(false);
                ivMosaic.setSelected(false);
                int mSelectPosition = mFaceStickerListAdapter.getSelectPosition();
                if (mSelectPosition != position) {
                    mFaceStickerListAdapter.setSelectPosition(position);
                }
                mFaceStickerListAdapter.notifyItemChanged(position);
                mFaceStickerListAdapter.notifyItemChanged(mSelectPosition);
                mMaterialEditViewModel.clearMaterialEditData();
                mStickerPath = faceBlockingInfo.getLocalSticker();
                for (FaceBlockingInfo info : mFaceBlockingInfoList) {
                    if ((info.isSelected() && !info.isGetFocus()) || (info.isGetFocus() && !info.isSelected())) {
                        if (!TextUtils.isEmpty(faceBlockingInfo.getLocalSticker())) {
                            info.setGetFocus(true);
                            info.setSelected(false);
                            info.setMosaic(false);
                            info.setType(mStickerType);
                            info.setLocalSticker(mStickerPath);
                        }
                    }
                }
                if (mFaceBlockingListAdapter != null) {
                    mFaceBlockingListAdapter.notifyDataSetChanged();
                }

                setEffectToFace();
            }

            @Override
            public void onStickerDownload(int position, int dataPosition) {
                int size = 0;
                for (FaceBlockingInfo info : mFaceBlockingInfoList) {
                    if (info.isSelected() || info.isGetFocus()) {
                        size++;
                    }
                }
                if (size == 0) {
                    return;
                }

                if (mStickerInfoList == null || mStickerInfoList.isEmpty()) {
                    return;
                }
                MaterialsCloudBean contentST = mStickerInfoList.get(dataPosition).getMaterialsCutContent();
                if (contentST == null) {
                    return;
                }

                int mPreviousPosition = mFaceStickerListAdapter.getSelectPosition();
                mFaceStickerListAdapter.setSelectPosition(position);
                if (mPreviousPosition != -1) {
                    mFaceStickerListAdapter.notifyItemChanged(mPreviousPosition);
                }
                mFaceStickerListAdapter.notifyItemChanged(position);

                SmartLog.i(TAG, "startGetUrl time=" + System.currentTimeMillis());
                mFaceStickerListAdapter.addDownloadMaterial(contentST);
                mStickerItemViewModel.downloadMaterials(mPreviousPosition, position, contentST);
            }

            @Override
            public void onStickerDeleted(FaceBlockingInfo faceBlockingInfo, int position) {
                showDeleteDialog(faceBlockingInfo);
            }
        });

        mSdkPlayViewModel.getPlayState().observe(this, isPlaying -> mMaterialEditViewModel.clearMaterialEditData());

        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).showFaceCompareButton(true, (v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    removeEffect();
                    mEditPreviewViewModel.updateVideoLane();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mEditPreviewViewModel.startFaceBlockingTracking(mSelectedAsset, mFaceBlockingInfoList);
                    mEditPreviewViewModel.updateVideoLane();
                }
                return true;
            });
        }
    }

    private void showDeleteDialog(FaceBlockingInfo faceBlockingInfo) {
        FaceStickerDeleteDialog dialog = new FaceStickerDeleteDialog(mActivity);
        dialog.show();
        dialog.setOnPositiveClickListener(() -> {
            isDeleteSticker = true;
            Iterator<FaceBlockingInfo> iterator = mStickerInfoList.iterator();
            while (iterator.hasNext()) {
                FaceBlockingInfo info = iterator.next();
                if (!TextUtils.isEmpty(info.getLocalSticker())
                        && info.getLocalSticker().equals(faceBlockingInfo.getLocalSticker())) {
                    mEditPreviewViewModel.removeBlockingSticker(info.getLocalSticker());
                    mFaceBlockingViewModel.deleteLocalCustomSticker(info.getMaterialsCutContent());
                    iterator.remove();
                }
            }
            if (mFaceStickerListAdapter != null) {
                mFaceStickerListAdapter.notifyDataSetChanged();
            }
            for (FaceBlockingInfo info : mFaceBlockingInfoList) {
                if (!TextUtils.isEmpty(info.getLocalSticker())
                        && info.getLocalSticker().equals(faceBlockingInfo.getLocalSticker())) {
                    info.setMosaic(true);
                    info.setType(MOSAIC_STICKER);
                    info.setLocalSticker(null);
                    info.setGetFocus(false);
                }
            }
            if (mFaceBlockingListAdapter != null) {
                mFaceBlockingListAdapter.notifyDataSetChanged();
            }
            setEffectToFace();
        });
    }

    @SuppressLint("StringFormatMatches")
    @Override
    protected void initEvent() {
        mErrorLayout.setOnClickListener(v -> {
            mCurrentPage = 0;
            mErrorLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
            mIndicatorView.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mStickerPanelViewModel != null) {
                        mStickerPanelViewModel.initColumns(HVEMaterialConstant.STICKER_FACE);
                    }
                }
            }, 500);
        });
        recyclerViewSticker.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mFaceStickerListAdapter.getItemCount() >= mStickerList.size()) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (!isScrolled && mHasNextPage && layoutManager != null) {
                        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                        if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                            mCurrentPage++;
                            if (materialsCutContent != null) {
                                mStickerItemViewModel.loadMaterials(materialsCutContent.getColumnId(), mCurrentPage);
                            }
                            isScrolled = false;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && layoutManager != null) {
                    int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1 && dy > 0) {
                        mCurrentPage++;
                        if (materialsCutContent != null) {
                            mStickerItemViewModel.loadMaterials(materialsCutContent.getColumnId(), mCurrentPage);
                        }
                        isScrolled = true;
                    }
                }

            }
        });

        mStickerItemViewModel.getDownloadInfo().observe(this, new Observer<MaterialsDownloadInfo>() {
            @Override
            public void onChanged(MaterialsDownloadInfo downloadInfo) {
                int downloadState = downloadInfo.getState();
                switch (downloadState) {
                    case MaterialsRespository.DOWNLOAD_SUCCESS:
                        mStickerType = CLOUD_STICKER;
                        ivCancel.setSelected(false);
                        ivMosaic.setSelected(false);
                        mFaceStickerListAdapter.removeDownloadMaterial(downloadInfo.getContentId());
                        int downloadPosition = downloadInfo.getPosition();
                        int dataPosition = downloadPosition - 1;

                        if (dataPosition >= 0 && dataPosition < mStickerInfoList.size() && downloadInfo.getContentId()
                                .equals(mStickerInfoList.get(dataPosition).getMaterialsCutContent().getId())) {
                            mFaceStickerListAdapter.setSelectPosition(downloadPosition);
                            mStickerInfoList.get(dataPosition)
                                    .setLocalSticker(parseStickerLocalPath(downloadInfo.getMaterialBean().getLocalPath()));
                            mStickerInfoList.get(dataPosition).setMaterialsCutContent(downloadInfo.getMaterialBean());
                            mFaceStickerListAdapter.notifyDataSetChanged();

                            mStickerPath = parseStickerLocalPath(
                                    mStickerInfoList.get(dataPosition).getMaterialsCutContent().getLocalPath());
                            for (FaceBlockingInfo info : mFaceBlockingInfoList) {
                                if ((info.isSelected() && !info.isGetFocus())
                                        || (info.isGetFocus() && !info.isSelected())) {
                                    if (!TextUtils.isEmpty(mStickerPath)) {
                                        info.setMosaic(false);
                                        info.setGetFocus(true);
                                        info.setSelected(false);
                                        info.setType(mStickerType);
                                        info.setLocalSticker(mStickerPath);
                                    }
                                }
                            }
                            if (mFaceBlockingListAdapter != null) {
                                mFaceBlockingListAdapter.notifyDataSetChanged();
                            }
                            mMaterialEditViewModel.clearMaterialEditData();
                            if (isFirstLoadCloudSticker) {
                                isFirstLoadCloudSticker = false;
                            } else {
                                setEffectToFace();
                            }
                        }
                        break;
                    case MaterialsRespository.DOWNLOAD_FAIL:
                        mFaceStickerListAdapter.removeDownloadMaterial(downloadInfo.getContentId());
                        updateFail(downloadInfo);
                        ToastWrapper
                                .makeText(mActivity,
                                        downloadInfo.getMaterialBean().getName() + Utils.setNumColor(
                                                String.format(Locale.ROOT,
                                                        mActivity.getResources().getString(R.string.download_failed), 0),
                                                mActivity.getResources().getColor(R.color.transparent)),
                                        Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case MaterialsRespository.DOWNLOAD_LOADING:
                        SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
                        break;
                    default:
                        break;
                }
            }
        });

        mStickerItemViewModel.getBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void updateFail(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        int dataPosition = downloadInfo.getDataPosition();
        if (downloadPosition >= 0 && downloadPosition < mStickerInfoList.size() && downloadInfo.getContentId()
                .equals(mStickerInfoList.get(dataPosition).getMaterialsCutContent().getId())) {
            mStickerInfoList.get(downloadPosition).setMaterialsCutContent(downloadInfo.getMaterialBean());
            if (downloadInfo.getPreviousPosition() != -1) {
                mFaceStickerListAdapter.notifyItemChanged(downloadInfo.getPreviousPosition());
            }
            mFaceStickerListAdapter.notifyItemChanged(downloadPosition);
        }
    }

    @Override
    public void onClick(View v) {
        int resourceId = v.getId();
        if (resourceId == R.id.iv_certain) {
            ToastWrapper.makeText(mActivity, getString(R.string.face_blocking_success), Toast.LENGTH_SHORT).show();
            mActivity.onBackPressed();
        } else if (resourceId == R.id.iv_cancel) {
            ivMosaic.setSelected(false);
            ivCancel.setSelected(true);
            for (FaceBlockingInfo info : mFaceBlockingInfoList) {
                if (info.isSelected() || info.isGetFocus()) {
                    info.setType(NO_STICKER);
                    info.setMosaic(false);
                    info.setLocalSticker(null);
                }
            }
            if (mFaceBlockingListAdapter != null) {
                mFaceBlockingListAdapter.notifyDataSetChanged();
            }
            if (mFaceStickerListAdapter != null) {
                mFaceStickerListAdapter.setSelectPosition(-1);
                mFaceStickerListAdapter.notifyDataSetChanged();
            }
            setEffectToFace();
        } else if (resourceId == R.id.iv_choose_sticker) {
            if (getAllStickerPaths().size() == 20) {
                ToastWrapper.makeText(mActivity, R.string.delete_other_sticker_first, Toast.LENGTH_SHORT).show();
                return;
            }
            ivMosaic.setSelected(false);
            ivCancel.setSelected(false);

            Intent intent = new Intent(mActivity, PicturePickActivity.class);
            intent.putExtra(IS_FROM_FACE_BLOCKING, true);
            mActivity.startActivityForResult(intent, VideoClipsActivity.ACTION_ADD_BLOCKING_STICKER_REQUEST_CODE);
        } else if (resourceId == R.id.iv_mosaic) {

            if (getSelectedFacesNum() == 0) {
                return;
            }
            mStickerType = MOSAIC_STICKER;
            ivCancel.setSelected(false);
            ivMosaic.setSelected(true);
            mMaterialEditViewModel.clearMaterialEditData();

            int mSelectPosition = mFaceStickerListAdapter.getSelectPosition();
            mFaceStickerListAdapter.setSelectPosition(-1);
            if (mSelectPosition != -1) {
                mFaceStickerListAdapter.notifyItemChanged(mSelectPosition);
            }

            for (FaceBlockingInfo info : mFaceBlockingInfoList) {
                if (info.isSelected() || info.isGetFocus()) {
                    info.setType(MOSAIC_STICKER);
                    info.setMosaic(true);
                    info.setLocalSticker(null);
                    info.setGetFocus(true);
                    info.setSelected(false);
                }
            }
            if (mFaceBlockingListAdapter != null) {
                mFaceBlockingListAdapter.notifyDataSetChanged();
            }

            setEffectToFace();
        } else if (resourceId == R.id.tv_reset) {
            resetFaceBlocking();
        }
    }

    private synchronized void seekTimeLineOfFace(long fistTimeStamp) {
        mEditPreviewViewModel.setCurrentTimeLine(fistTimeStamp);
    }

    private void setEffectToFace() {
        if (mSelectedAsset instanceof HVEVisibleAsset) {
            mEditPreviewViewModel.startFaceBlockingTracking(mSelectedAsset, mFaceBlockingInfoList);
            mEditPreviewViewModel.updateVideoLane();
            mEditPreviewViewModel.refreshMenuState();
        }
    }

    private ArrayList<FaceBlockingInfo> getCurrentSelectedFaceBoxList(long fistTimeStamp) {
        ArrayList<FaceBlockingInfo> result = new ArrayList<>();
        for (FaceBlockingInfo facBoxInfo : mFaceBlockingInfoList) {
            if (facBoxInfo.getFirstTimeStamp() == fistTimeStamp && facBoxInfo.isSelected()) {
                result.add(facBoxInfo);
            }
        }
        return result;
    }

    private void isStickerUsed(List<FaceBlockingInfo> faceBlockingInfoList, FaceBlockingInfo info) {
        int index = 0;
        for (FaceBlockingInfo faceBlockingInfo : faceBlockingInfoList) {
            if (!TextUtils.isEmpty(info.getLocalSticker())
                    && info.getLocalSticker().equals(faceBlockingInfo.getLocalSticker())) {
                index++;
            }
        }
        if (index <= 1) {
            for (int i = 0; i < mStickerInfoList.size(); i++) {
                if (!TextUtils.isEmpty(info.getLocalSticker())
                        && !TextUtils.isEmpty(mStickerInfoList.get(i).getLocalSticker())) {
                    if (info.getLocalSticker().equals(mStickerInfoList.get(i).getLocalSticker())) {
                        mStickerInfoList.get(i).setSelected(false);
                        if (mFaceStickerListAdapter != null) {
                            mFaceStickerListAdapter.setSelectPosition(-1);
                            mFaceStickerListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            if (getMosaicNum() <= 1) {
                if (ivMosaic.isSelected()) {
                    ivMosaic.setSelected(false);
                }
            }
        }
    }

    private void confirmFaceSticker(List<FaceBlockingInfo> faceBlockingInfoList, FaceBlockingInfo faceBlockingInfo) {
        if (!faceBlockingInfo.isGetFocus()) {
            if (getFocusedFaceNum() > 0) {
                for (FaceBlockingInfo info : faceBlockingInfoList) {
                    info.setGetFocus(false);
                }
                if (mFaceBlockingListAdapter != null) {
                    mFaceBlockingListAdapter.notifyDataSetChanged();
                }

                for (FaceBlockingInfo info : mStickerInfoList) {
                    info.setSelected(false);
                    if (mFaceStickerListAdapter != null) {
                        mFaceStickerListAdapter.setSelectPosition(-1);
                        mFaceStickerListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private List<String> getAllStickerPaths() {
        List<String> result = new ArrayList<>();
        List<MaterialsCloudBean> localStickerList = mFaceBlockingViewModel.getLocalMaterialsDataByType(CUSTOM_STICKERS);
        for (MaterialsCloudBean info : localStickerList) {
            if (!TextUtils.isEmpty(info.getLocalPath())) {
                result.add(info.getLocalPath());
            }
        }
        return result;
    }

    private int getFocusedFaceNum() {
        int total = 0;
        for (FaceBlockingInfo info : mFaceBlockingInfoList) {
            if (info.isGetFocus()) {
                total++;
            }
        }
        return total;
    }

    private int getSelectedFacesNum() {
        int total = 0;
        for (FaceBlockingInfo info : mFaceBlockingInfoList) {
            if (info.isSelected() || info.isGetFocus()) {
                total++;
            }
        }
        return total;
    }

    private int getCheckedFacesNum() {
        int total = 0;
        for (FaceBlockingInfo info : mFaceBlockingInfoList) {
            if (info.isSelected()) {
                total++;
            }
        }
        return total;
    }

    private int getMosaicNum() {
        int index = 0;
        for (FaceBlockingInfo info : mFaceBlockingInfoList) {
            if (info.isMosaic()) {
                index++;
            }
        }
        return index;
    }

    private void cancelAllStickers(boolean isCancelSticker) {
        mStickerType = "";
        mStickerPath = "";

        ivCancel.setImageResource(R.drawable.icon_cancel_wu_gray);
        ivCancel.setSelected(false);
        ivCancel.setEnabled(false);
        ivMosaic.setImageResource(R.drawable.bg_face_add_mosaic_gray);
        ivMosaic.setSelected(false);
        ivMosaic.setEnabled(false);
        isShowGray = true;
        if (isCancelSticker) {
            removeEffect();
        }
        for (FaceBlockingInfo info : mStickerInfoList) {
            info.setShowGray(true);
        }
        for (FaceBlockingInfo info : mFaceBlockingInfoList) {
            info.setGetFocus(false);
            if (isCancelSticker) {
                info.setType(NO_STICKER);
                info.setMosaic(false);
                info.setSelected(false);
                info.setLocalSticker(null);
            }
        }
        if (mFaceBlockingListAdapter != null) {
            mFaceBlockingListAdapter.notifyDataSetChanged();
        }
        if (mFaceStickerListAdapter != null) {
            mFaceStickerListAdapter.setSelectPosition(-1);
            mFaceStickerListAdapter.notifyDataSetChanged();
        }
    }

    private void removeEffect() {
        if (mSelectedAsset instanceof HVEVisibleAsset) {
            ((HVEVisibleAsset) mSelectedAsset).removeFacePrivacyEffect();
        }
    }

    private void resetFaceBlocking() {
        tvFaceNum.setText(NumberFormat.getInstance().format(0));
        tvFaceNum.setTextColor(ContextCompat.getColor(mActivity, R.color.color_fff_60));
        cancelAllStickers(true);
        mMaterialEditViewModel.clearMaterialEditData();
        mEditPreviewViewModel.updateVideoLane();
        for (FaceBlockingInfo info : mFaceBlockingInfoList) {
            info.setSelected(false);
        }
        if (mFaceBlockingListAdapter != null) {
            mFaceBlockingListAdapter.notifyDataSetChanged();
        }
    }

    public String parseStickerLocalPath(String stickerFolder) {
        String localPath = "";
        try {
            String configJson = FileUtil.readJsonFile(stickerFolder + File.separator + Constants.CONFIG_JSON_NAME);
            AssetBean setting = new Gson().fromJson(configJson, AssetBean.class);
            localPath = stickerFolder + File.separator + setting.getResourceConfigs().get(0).getPath();
        } catch (Exception e) {
            SmartLog.e(TAG, "prase json failed");
        }
        return localPath;
    }


    public static Bitmap base64ToBitmap(String base64Data) {
        try {
            byte[] bytes = SafeBase64.decode(base64Data, Base64.DEFAULT);
            return BitmapDecodeUtils.decodeByteArray(bytes, 0, bytes.length);
        } catch (IllegalArgumentException exception) {
            SmartLog.e(TAG, "base64ToBitmap: " + exception.getMessage());
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mMaterialEditViewModel != null) {
            mMaterialEditViewModel.clearMaterialEditData();
        }

        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.updateVideoLane();
            mEditPreviewViewModel.setFaceBlockingStatus(false);
        }

        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).showFaceCompareButton(false, null);
        }
    }
}
