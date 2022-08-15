
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

package com.huawei.hms.videoeditor.ui.mediaeditor.fragment;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEBlur;
import com.huawei.hms.videoeditor.sdk.bean.HVECanvas;
import com.huawei.hms.videoeditor.sdk.bean.HVEColor;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.sdk.v1.AssetBeanAnalyer;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.canvas.CanvasBlurAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.canvas.CanvasColor2Adapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.canvas.CanvasPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.canvas.CanvasStyleStyleAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.effect.adapter.BaseGridLayoutManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.MySeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditor.ui.mediapick.activity.PicturePickActivity;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CanvasBackgroundFragment extends BaseFragment {
    public static final String TAG = "CanvasBackgroundFragment";

    private ImageView mIvCertain;

    protected EditPreviewViewModel mEditPreviewViewModel;

    private CanvasPanelViewModel mCanvasPanelViewModel;

    private ImageView mCustomColor;

    private View mCustomColorBg;

    private ColorPickerFragment colorPickerFragment;

    private FragmentManager fm;

    private RecyclerView mBlueRecyclerView;

    private RecyclerView mColorRecyclerView;

    private RecyclerView mStyleRecyclerView;

    private CanvasBlurAdapter blurAdapter;

    private CanvasColor2Adapter canvasColorAdapter;

    private CanvasStyleStyleAdapter mCanvasStyleAdapter;

    private View mCancelHeaderView;

    private HVEColor rgbColor;

    private List<HVEColumnInfo> mColumnList;

    private List<CloudMaterialBean> mStyleList;

    private RelativeLayout mStyleAddLayout;

    private View mStyleAddNormalBg;

    private View mStyleAddSelectBg;

    private ImageView mStyleAddNormalImage;

    private ImageView mStyleAddSelectImage;

    private ImageView mStyleAddDeleteImage;

    private String stylePath = null;

    private String styleId = null;

    private HVEBlur blurs = null;

    private String mStyleSelectPath = "";

    private String mStyleSelectCloudId = "";

    private int mStyleSelectPosition = Integer.MIN_VALUE;

    private String mFromAlbumPath;

    private ConstraintLayout mLoadingLayout;

    private LoadingIndicatorView mIndicatorView;

    private RelativeLayout mErrorLayout;

    private TextView mErrorTv;

    private int mCurrentPage = 0;

    private boolean applyAll = false;

    private TextView mCanvasColorAll;

    private ImageView blur_rl_add;

    private LinearLayoutCompat blur_rl_custom;

    private MySeekBar mBlurSeek;

    private NestedScrollView mCanvascrollview;

    private int mProgress;

    private RelativeLayout.LayoutParams mLayoutParams = null;

    private RelativeLayout.LayoutParams mSeekbarParams = null;

    private float xEvent = 0f;

    private boolean isSelectFromAlbum;

    private boolean isFirst;

    HuaweiVideoEditor mEditor;

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
        return R.layout.fragment_canvas_background;
    }

    @Override
    protected void initView(View view) {
        TextView mTvTitle = view.findViewById(R.id.tv_title);
        mIvCertain = view.findViewById(R.id.iv_certain);
        mCustomColor = view.findViewById(R.id.custom_color);
        mCustomColorBg = view.findViewById(R.id.custom_color_bg);
        mBlueRecyclerView = view.findViewById(R.id.blue_recycler_view);
        blur_rl_add = view.findViewById(R.id.rl_add);
        blur_rl_custom = view.findViewById(R.id.rl_custom);
        mBlurSeek = view.findViewById(R.id.blur_custom_seek);
        if (ScreenUtil.isRTL()) {
            mBlurSeek.setScaleX(RTL_UI);
        } else {
            mBlurSeek.setScaleX(LTR_UI);
        }
        mCanvascrollview = view.findViewById(R.id.canvas_scrollview);

        mStyleRecyclerView = view.findViewById(R.id.style_recycler_view);
        mColorRecyclerView = view.findViewById(R.id.color_recycler_view);
        mCanvasColorAll = view.findViewById(R.id.canvas_color_all);
        mTvTitle.setText(getString(R.string.edit_item10_4));
        fm = getChildFragmentManager();
        colorPickerFragment = new ColorPickerFragment();

        mCancelHeaderView = LayoutInflater.from(mActivity).inflate(R.layout.adapter_add_canvas_header, null, false);
        int mImageViewWidth = (SizeUtils.screenWidth(mActivity) - (SizeUtils.dp2Px(mActivity, 72))) / 6;
        mCancelHeaderView.setLayoutParams(new LinearLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        mStyleAddLayout = mCancelHeaderView.findViewById(R.id.rl_add);
        mStyleAddNormalBg = mCancelHeaderView.findViewById(R.id.item_add_image_normal_bg);
        mStyleAddSelectBg = mCancelHeaderView.findViewById(R.id.item_add_image_select_view);
        mStyleAddNormalImage = mCancelHeaderView.findViewById(R.id.item_add_view);
        mStyleAddSelectImage = mCancelHeaderView.findViewById(R.id.item_image);
        mStyleAddDeleteImage = mCancelHeaderView.findViewById(R.id.item_image_delete);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mIndicatorView = view.findViewById(R.id.indicator);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mErrorTv = view.findViewById(R.id.error_text);
    }

    @Override
    protected void initObject() {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mCanvasPanelViewModel = new ViewModelProvider(this, mFactory).get(CanvasPanelViewModel.class);
        mCanvasPanelViewModel.setEditPreviewViewModel(mEditPreviewViewModel);
        mEditor = mEditPreviewViewModel.getEditor();
        setTimeoutEnable();
        mColumnList = new ArrayList<>();
        mStyleList = new ArrayList<>();
        mCanvasStyleAdapter = new CanvasStyleStyleAdapter(context, mStyleList, R.layout.adapter_canvas_style_item);
        mStyleRecyclerView.setLayoutManager(new BaseGridLayoutManager(mActivity, 6));
        if (mStyleRecyclerView.getItemDecorationCount() == 0) {
            mStyleRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(mActivity, 8),
                SizeUtils.dp2Px(mActivity, 8), ContextCompat.getColor(mActivity, R.color.transparent)));
        }
        mStyleRecyclerView.setItemAnimator(null);
        mStyleRecyclerView.setAdapter(mCanvasStyleAdapter);
        mCanvasStyleAdapter.addHeaderView(mCancelHeaderView);
        mStyleRecyclerView.setNestedScrollingEnabled(false);
        mStyleRecyclerView.setHasFixedSize(true);
        mStyleRecyclerView.setFocusable(false);
        mBlurSeek.setMinProgress(0);
        mBlurSeek.setMaxProgress(10);
        mBlurSeek.setAnchorProgress(0);
        mCanvasPanelViewModel.initColumns();

        HVEVideoLane mainLane = mEditPreviewViewModel.getVideoLane();
        if (mainLane == null) {
            return;
        }

        HVECanvas canvas = mainLane.getLaneCanvas(mEditPreviewViewModel.getSeekTime());
        if (canvas != null) {
            if (canvas.getType() == HVECanvas.Type.COLOR) {
                HVEColor oldColor = canvas.getColor();
                SmartLog.d(TAG, "initObject color bg HVEColor:" + oldColor);
            }
            if (canvas.getType() == HVECanvas.Type.IMAGE) {
                mStyleSelectPath = canvas.getImagePath();
                mStyleSelectCloudId = canvas.getCloudId();
                if (!TextUtils.isEmpty(mStyleSelectPath) && TextUtils.isEmpty(mStyleSelectCloudId)) {
                    Glide.with(context)
                        .load(mStyleSelectPath)
                        .apply(new RequestOptions().transform(new MultiTransformation<>(new CenterCrop(),
                            new RoundedCorners(SizeUtils.dp2Px(context, 2)))))
                        .into(mStyleAddSelectImage);
                    mStyleAddDeleteImage.setVisibility(View.VISIBLE);
                }
            }
            if (canvas.getType() == HVECanvas.Type.FUZZ) {
                HVEBlur oldBlurs = canvas.getBlur();
                if (oldBlurs != null) {
                    mProgress = (int) oldBlurs.BLURSIZE;
                }
                SmartLog.d(TAG, "initObject fuzz bg HVEBlur:" + oldBlurs);
            }
        }
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
        }
    }

    @Override
    protected void initData() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mIndicatorView.show();
        initColorData();
        initStyleData();
        mBlurSeek.setProgress(mProgress);
        HVEAsset mainLaneAsset = mEditPreviewViewModel.getMainLaneAsset();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        if (mainLaneAsset != null) {
            if (mainLaneAsset instanceof HVEImageAsset) {
                ((HVEImageAsset) mainLaneAsset).getFirstFrame(ScreenUtil.dp2px(48), ScreenUtil.dp2px(48),
                    new HuaweiVideoEditor.ImageCallback() {
                        @Override
                        public void onSuccess(Bitmap bitmap, long timeStamp) {
                            mActivity.runOnUiThread(() -> initBlurData(bitmap));
                        }

                        @Override
                        public void onFail(int errorCode) {

                        }
                    });
            } else {
                mEditor.getBitmapAtSelectedLan(0, timeLine.getCurrentTime(), new HuaweiVideoEditor.ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, long time) {
                        if (mActivity == null || bitmap == null) {
                            return;
                        }

                        mActivity.runOnUiThread(() -> initBlurData(bitmap));
                    }

                    @Override
                    public void onFail(int errorCode) {

                    }
                });
            }
        }
    }

    @Override
    protected void initEvent() {
        blur_rl_add.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (blurAdapter == null) {
                return;
            }

            isSelectFromAlbum = false;
            initPrimaryViewState();
            mProgress = 0;
            initPrimaryState();
            blur_rl_add.setSelected(!blur_rl_add.isSelected());
            mBlueRecyclerView.setVisibility(View.VISIBLE);
            mBlurSeek.setVisibility(View.INVISIBLE);
            mBlurSeek.setAnchorProgress(0);
            setIsSlid(false);
            int aSelectPosition = blurAdapter.getSelectPosition();
            blurAdapter.setSelectPosition(-1);
            if (aSelectPosition != -1) {
                blurAdapter.notifyItemChanged(aSelectPosition);
            }
            mEditPreviewViewModel.setCanvasImageData("");
            mCanvasPanelViewModel.setBackgroundColor(null, false);
        }));

        blur_rl_custom.setOnClickListener(new OnClickRepeatedListener(v -> {
            isSelectFromAlbum = false;
            initPrimaryViewState();
            initPrimaryState();
            mBlurSeek.setProgress(mProgress);
            blur_rl_custom.setSelected(!blur_rl_custom.isSelected());
            mBlueRecyclerView.setVisibility(View.INVISIBLE);
            mBlurSeek.setVisibility(View.VISIBLE);
            mStyleRecyclerView.setNestedScrollingEnabled(false);
            mBlueRecyclerView.setNestedScrollingEnabled(false);
            mColorRecyclerView.setNestedScrollingEnabled(false);
            setIsSlid(false);
        }));

        mBlurSeek.setOnProgressChangedListener(progress -> {
            mProgress = progress;
            mEditPreviewViewModel.setToastTime(String.valueOf(progress));
            float blur = (float) progress;
            blurs = new HVEBlur(blur, 100, 100);
            mCanvasPanelViewModel.setBackgroundBlur(blurs, applyAll);
        });
        mBlurSeek.setcTouchListener(isTouch -> mEditPreviewViewModel
            .setToastTime(isTouch ? String.valueOf((int) mBlurSeek.getProgress()) : ""));

        mCustomColor.setOnClickListener(new OnClickRepeatedListener(v -> {
            isSelectFromAlbum = false;
            initPrimaryViewState();
            mProgress = 0;
            initPrimaryState();
            mCustomColorBg.setVisibility(View.VISIBLE);
            showBottomDialog();
            mBlueRecyclerView.setVisibility(View.VISIBLE);
            mBlurSeek.setVisibility(View.GONE);
            setIsSlid(false);
        }));

        colorPickerFragment.setSelectedListener((color, layoutParams, seekbarParams, xParams) -> {
            mCustomColor.setBackgroundColor(color);
            rgbColor = new HVEColor(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color));
            mCanvasPanelViewModel.setBackgroundColor(rgbColor, applyAll);
            mLayoutParams = layoutParams;
            mSeekbarParams = seekbarParams;
            xEvent = xParams;
        });

        mCanvasColorAll.setOnClickListener(new OnClickRepeatedListener(v -> {
            applyAll = true;
            isAll();
            ToastWrapper.makeText(context, context.getText(R.string.applied_to_all), Toast.LENGTH_SHORT).show();
            applyAll = false;
        }));

        mIvCertain.setOnClickListener(new OnClickRepeatedListener(v -> {
            mActivity.onBackPressed();
        }));

        mCancelHeaderView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mStyleSelectPosition == -2) {
                mStyleSelectPosition = Integer.MIN_VALUE;
            } else {
                if (!StringUtil.isEmpty(mStyleSelectPath)) {
                    mStyleSelectPosition = -2;
                }
            }
            mStyleAddNormalBg.setVisibility(mStyleSelectPosition == -2 ? View.GONE : View.VISIBLE);
            mStyleAddSelectBg.setVisibility(mStyleSelectPosition == -2 ? View.VISIBLE : View.GONE);
            mStyleAddNormalImage.setVisibility(!StringUtil.isEmpty(mStyleSelectPath) ? View.GONE : View.VISIBLE);
            mStyleAddSelectImage.setVisibility(!StringUtil.isEmpty(mStyleSelectPath) ? View.VISIBLE : View.GONE);

            if (mCanvasStyleAdapter != null) {
                int canvasSelectPosition = mCanvasStyleAdapter.getSelectPosition();
                mCanvasStyleAdapter.setSelectPosition(-1);
                if (canvasSelectPosition != -1) {
                    mCanvasStyleAdapter.notifyItemChanged(canvasSelectPosition);
                }
            }
            mCanvasPanelViewModel.setBackgroundColor(null, false);
        }));

        mStyleAddLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            setIsSlid(false);
            isSelectFromAlbum = true;
            mActivity.startActivityForResult(new Intent(mActivity, PicturePickActivity.class),
                VideoClipsActivity.ACTION_ADD_CANVAS_REQUEST_CODE);
        }));

        mStyleAddDeleteImage.setOnClickListener(new OnClickRepeatedListener(v -> {
            mStyleSelectPath = "";
            mStyleAddNormalBg.setVisibility(View.VISIBLE);
            mStyleAddSelectBg.setVisibility(View.GONE);
            mStyleAddNormalImage.setVisibility(View.VISIBLE);
            mStyleAddSelectImage.setVisibility(View.GONE);
            mStyleSelectPosition = Integer.MIN_VALUE;
            mStyleAddDeleteImage.setVisibility(View.GONE);
            mEditPreviewViewModel.setCanvasImageData("");
            String bitmapString = mCanvasPanelViewModel.getBitmapString();
            if (TextUtils.equals(bitmapString, mFromAlbumPath)) {
                mCanvasPanelViewModel.setBackgroundBitmap(null, null, applyAll);
            }
        }));

        mCanvascrollview.setOnScrollChangeListener(
            (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (mColumnList.size() > 0) {
                        mCurrentPage++;
                        HVEColumnInfo materialsCutContent = mColumnList.get(0);
                        mCanvasPanelViewModel.loadMaterials(materialsCutContent, mCurrentPage);
                    }
                }
            });

        mCanvasStyleAdapter.setOnItemClickListener(new CanvasStyleStyleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int dataPosition) {
                isSelectFromAlbum = false;
                initPrimaryViewState();
                mProgress = 0;
                mBlueRecyclerView.setVisibility(View.VISIBLE);
                if (blurAdapter != null) {
                    int aSelectPosition = blurAdapter.getSelectPosition();
                    blurAdapter.setSelectPosition(-1);
                    if (aSelectPosition != -1) {
                        blurAdapter.notifyItemChanged(aSelectPosition);
                    }
                }
                mColorRecyclerView.setVisibility(View.VISIBLE);
                if (canvasColorAdapter != null) {
                    int colorSelectPosition = canvasColorAdapter.getSelectPosition();
                    canvasColorAdapter.setSelectPosition(-1);
                    if (colorSelectPosition != -1) {
                        canvasColorAdapter.notifyItemChanged(colorSelectPosition);
                    }
                }

                mBlurSeek.setVisibility(View.INVISIBLE);
                mBlueRecyclerView.setVisibility(View.VISIBLE);
                setIsSlid(false);
                CloudMaterialBean content = mStyleList.get(dataPosition);
                if (mStyleSelectPosition == -2) {
                    mStyleSelectPosition = Integer.MIN_VALUE;
                    mStyleAddSelectBg.setVisibility(View.GONE);
                }
                int cSelectPosition = mCanvasStyleAdapter.getSelectPosition();
                if (cSelectPosition != position) {
                    mCanvasStyleAdapter.setSelectPosition(position);
                    if (cSelectPosition != -1) {
                        mCanvasStyleAdapter.notifyItemChanged(cSelectPosition);
                    }
                    mCanvasStyleAdapter.notifyItemChanged(position);
                    AssetBeanAnalyer assetBeanAnalyer = AssetBeanAnalyer.create(content.getLocalPath());
                    if (assetBeanAnalyer == null) {
                        return;
                    }

                    stylePath = assetBeanAnalyer.getAssetPath();
                    styleId = content.getId();
                    blurs = null;
                    rgbColor = null;
                    mCanvasPanelViewModel.setBackgroundBitmap(stylePath, styleId, applyAll);
                }
            }

            @Override
            public void onDownloadClick(int position, int dataPosition) {
                setIsSlid(false);
                int aPreviousPosition = mCanvasStyleAdapter.getSelectPosition();
                mCanvasStyleAdapter.setSelectPosition(position);
                CloudMaterialBean cutContent = mStyleList.get(dataPosition);

                mCanvasStyleAdapter.addDownloadMaterial(cutContent);
                mCanvasPanelViewModel.downloadColumn(aPreviousPosition, position, dataPosition, cutContent);
            }
        });

        mCanvasPanelViewModel.getDownloadSuccess().observe(this, downloadInfo -> {
            initPrimaryViewState();
            mProgress = 0;
            mBlueRecyclerView.setVisibility(View.VISIBLE);
            if (blurAdapter != null) {
                int aSelectPosition = blurAdapter.getSelectPosition();
                blurAdapter.setSelectPosition(-1);
                if (aSelectPosition != -1) {
                    blurAdapter.notifyItemChanged(aSelectPosition);
                }
            }
            mColorRecyclerView.setVisibility(View.VISIBLE);
            if (canvasColorAdapter != null) {
                int colorSelectPosition = canvasColorAdapter.getSelectPosition();
                canvasColorAdapter.setSelectPosition(-1);
                if (colorSelectPosition != -1) {
                    canvasColorAdapter.notifyItemChanged(colorSelectPosition);
                }
            }
            SmartLog.d(TAG, "getDownloadSuccess");
            mCanvasStyleAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getPosition();
            if (downloadPosition >= 0 && downloadInfo.getDataPosition() < mStyleList.size()
                && downloadInfo.getContentId().equals(mStyleList.get(downloadInfo.getDataPosition()).getId())) {
                mCanvasStyleAdapter.setSelectPosition(downloadInfo.getPosition());
                if (downloadInfo.getPreviousPosition() != -1) {
                    mCanvasStyleAdapter.notifyItemChanged(downloadInfo.getPreviousPosition());
                }
                mStyleList.set(downloadInfo.getDataPosition(), downloadInfo.getMaterialBean());
                mCanvasStyleAdapter.notifyDataSetChanged();

                if (downloadPosition == mCanvasStyleAdapter.getSelectPosition()) {
                    AssetBeanAnalyer assetBeanAnalyer =
                        AssetBeanAnalyer.create(downloadInfo.getMaterialBean().getLocalPath());
                    if (assetBeanAnalyer == null) {
                        return;
                    }

                    stylePath = assetBeanAnalyer.getAssetPath();
                    styleId = downloadInfo.getMaterialBean().getId();
                    blurs = null;
                    rgbColor = null;
                    mCanvasPanelViewModel.setBackgroundBitmap(stylePath, styleId, applyAll);
                }
            }
        });

        mCanvasPanelViewModel.getDownloadFail().observe(this, downloadInfo -> {
            mCanvasStyleAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            ToastWrapper.makeText(context, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
        });

        mCanvasPanelViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        mEditPreviewViewModel.getTimeout().observe(this, isTimeout -> {
            if (isTimeout && !isBackground) {
                mActivity.onBackPressed();
            }
        });

        mStyleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutSTManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutSTManager != null) {
                    int visibleItemCount = layoutSTManager.getChildCount();
                    int firstPosition = layoutSTManager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 1 && !isFirst && mStyleList.size() > 0) {
                        isFirst = true;
                    }
                }
            }
        });

    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void isAll() {
        mCanvasPanelViewModel.setBackgroundColor(rgbColor, true);
        if (!TextUtils.isEmpty(stylePath)) {
            mCanvasPanelViewModel.setBackgroundBitmap(stylePath, styleId, true);
        }
        if (blurs != null) {
            mCanvasPanelViewModel.setBackgroundBlur(blurs, true);
        }
    }

    private void initBlurData(Bitmap bitmap) {
        mBlueRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        List<Float> list = new ArrayList<>();
        list.add(0.2f);
        list.add(0.5f);
        list.add(0.8f);
        list.add(1f);
        list.add(3f);
        blurAdapter = new CanvasBlurAdapter(mActivity, list, R.layout.adapter_canvas_style, bitmap);
        blurAdapter.setBlurSelectedListener(blur -> {
            isSelectFromAlbum = false;
            initPrimaryViewState();
            mProgress = 0;
            mColorRecyclerView.setVisibility(View.VISIBLE);
            if (canvasColorAdapter != null) {
                int colorSelectPosition = canvasColorAdapter.getSelectPosition();
                canvasColorAdapter.setSelectPosition(-1);
                if (colorSelectPosition != -1) {
                    canvasColorAdapter.notifyItemChanged(colorSelectPosition);
                }
            }

            mStyleSelectPosition = -2;
            mStyleAddSelectBg.setVisibility(View.GONE);
            if (mCanvasStyleAdapter != null) {
                int bSelectPosition = mCanvasStyleAdapter.getSelectPosition();
                mCanvasStyleAdapter.setSelectPosition(-1);
                if (bSelectPosition != -1) {
                    mCanvasStyleAdapter.notifyItemChanged(bSelectPosition);
                }
            }

            blurs = new HVEBlur(blur, 100, 100);
            stylePath = null;
            styleId = null;
            rgbColor = null;
            mCanvasPanelViewModel.setBackgroundBlur(blurs, applyAll);
        });

        mBlueRecyclerView.setAdapter(blurAdapter);
        mBlueRecyclerView.setNestedScrollingEnabled(false);
        if (!isValidActivity()) {
            return;
        }
        mBlueRecyclerView
            .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                SizeUtils.dp2Px(mActivity, 48), SizeUtils.dp2Px(mActivity, 8)));
        HVEBlur blur = mCanvasPanelViewModel.getBackgroundBlur();

        if (blur != null) {
            for (int i = 0; i < list.size(); i++) {
                if (Float.compare(list.get(i), blur.BLURSIZE) == 0) {
                    blurAdapter.setSelectPosition(i);
                    blurAdapter.notifyItemChanged(i);
                    break;
                }
            }
        } else {
            blurAdapter.setSelectPosition(-1);
        }
    }

    private void initColorData() {
        ArrayList<Integer> bColorList = new ArrayList<>();
        bColorList.add(0xFFFFFFFF);
        bColorList.add(0xFFB8B8B8);
        bColorList.add(0xFF808080);
        bColorList.add(0xFF333333);
        bColorList.add(0xFF000000);
        bColorList.add(0xFFFBACAC);
        bColorList.add(0xFFE26464);
        bColorList.add(0xFFD14040);
        bColorList.add(0xFFC41B1B);
        bColorList.add(0xFF950202);
        bColorList.add(0xFFFBF1CA);
        bColorList.add(0xFFE8D471);
        bColorList.add(0xFFD2B72D);
        bColorList.add(0xFFBC9A12);
        bColorList.add(0xFFDFFBBF);
        bColorList.add(0xFFA9E36E);
        bColorList.add(0xFF5FC235);
        bColorList.add(0xFF3EA214);
        bColorList.add(0xFF056A01);
        bColorList.add(0xFFCCF6D1);
        bColorList.add(0xFF8CEC97);
        bColorList.add(0xFF48DB59);
        bColorList.add(0xFF169B25);
        bColorList.add(0xFF03680E);
        bColorList.add(0xFFC3F5F6);
        bColorList.add(0xFF71E7E9);
        bColorList.add(0xFF32C7C9);
        bColorList.add(0xFF10A5A7);
        bColorList.add(0xFF005E60);
        bColorList.add(0xFFDEE8FE);
        bColorList.add(0xFF8FAEF2);
        bColorList.add(0xFF3163D1);
        bColorList.add(0xFF1243AE);
        bColorList.add(0xFF022268);
        bColorList.add(0xFFC1BFFB);
        bColorList.add(0xFF8683F2);
        bColorList.add(0xFF4642E4);
        bColorList.add(0xFF2521CE);
        bColorList.add(0xFF0A0798);
        bColorList.add(0xFFF3CCF6);
        bColorList.add(0xFFE48DEB);
        bColorList.add(0xFFD354DE);
        bColorList.add(0xFFA612B4);
        bColorList.add(0xFF770881);
        bColorList.add(0xFFF6C3D9);
        bColorList.add(0xFFEC89B4);
        bColorList.add(0xFFD9548E);
        bColorList.add(0xFFB71E61);
        bColorList.add(0xFF750A39);
        bColorList.add(0xFF85CEFF);
        bColorList.add(0xFF5F98BE);
        bColorList.add(0xFF46789A);
        bColorList.add(0xFF2A4557);
        bColorList.add(0xFF12202A);
        bColorList.add(0xFFD6CC96);
        bColorList.add(0xFFA49C70);
        bColorList.add(0xFF6B6647);
        bColorList.add(0xFF27251B);
        bColorList.add(0xFFF1C7BD);
        bColorList.add(0xFFC8836F);
        bColorList.add(0xFFAB604D);
        bColorList.add(0xFF823E2C);
        bColorList.add(0xFF3E180E);

        mColorRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
        canvasColorAdapter = new CanvasColor2Adapter(mActivity, bColorList, R.layout.item_color_view);
        canvasColorAdapter.setBlurSelectedListener((blur, position) -> {
            isSelectFromAlbum = false;
            initPrimaryViewState();
            mProgress = 0;
            mBlueRecyclerView.setVisibility(View.VISIBLE);
            if (blurAdapter != null) {
                int aSelectPosition = blurAdapter.getSelectPosition();
                blurAdapter.setSelectPosition(-1);
                if (aSelectPosition != -1) {
                    blurAdapter.notifyItemChanged(aSelectPosition);
                }
            }

            mColorRecyclerView.setVisibility(View.VISIBLE);
            mStyleSelectPosition = -2;
            mStyleAddSelectBg.setVisibility(View.GONE);
            if (mCanvasStyleAdapter != null) {
                int dSelectPosition = mCanvasStyleAdapter.getSelectPosition();
                mCanvasStyleAdapter.setSelectPosition(-1);
                if (dSelectPosition != -1) {
                    mCanvasStyleAdapter.notifyItemChanged(dSelectPosition);
                }
            }

            setIsSlid(false);
            int color = blur;
            rgbColor = new HVEColor(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color));
            stylePath = null;
            styleId = null;
            blurs = null;
            mCanvasPanelViewModel.setBackgroundColor(rgbColor, applyAll);
        });
        mColorRecyclerView.setAdapter(canvasColorAdapter);
        mColorRecyclerView.setNestedScrollingEnabled(false);
    }

    private void initStyleData() {
        mCanvasPanelViewModel.getColumns().observe(this, list -> {
            if (list.size() > 0) {
                mColumnList.addAll(list);
                mLoadingLayout.setVisibility(View.VISIBLE);
                HVEColumnInfo columnInfo = list.get(0);
                mCanvasPanelViewModel.loadMaterials(columnInfo, mCurrentPage);
            }
        });

        mCanvasPanelViewModel.getcErrorString().observe(this, errorString -> {
            mLoadingLayout.setVisibility(View.GONE);
            mIndicatorView.hide();
            if (!TextUtils.isEmpty(errorString) && mStyleList.size() == 0) {
                mErrorTv.setText(errorString);
                mErrorLayout.setVisibility(View.VISIBLE);
            }
        });

        mErrorLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            mErrorLayout.setVisibility(View.GONE);
            if (mCurrentPage == 0) {
                mLoadingLayout.setVisibility(View.VISIBLE);
                mIndicatorView.show();
                mCanvasPanelViewModel.initColumns();
            } else {
                HVEColumnInfo materialsCutContent = mColumnList.get(0);
                mCanvasPanelViewModel.loadMaterials(materialsCutContent, mCurrentPage);
            }
        }));

        mCanvasPanelViewModel.getPageData().observe(this, list -> {
            if (mCurrentPage == 0) {
                mStyleList.clear();
                mCanvasStyleAdapter.notifyDataSetChanged();
            }
            mLoadingLayout.setVisibility(View.GONE);
            mIndicatorView.hide();

            if (!mStyleList.containsAll(list)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                mStyleList.addAll(list);
                checkHistory();
                mCanvasStyleAdapter.notifyDataSetChanged();
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
        });

        mEditPreviewViewModel.getCanvasImageData().observe(this, path -> {
            if (!StringUtil.isEmpty(path)) {
                this.mFromAlbumPath = path;
                initPrimaryViewState();
                mProgress = 0;
                mBlueRecyclerView.setVisibility(View.VISIBLE);
                if (blurAdapter != null) {
                    int aSelectPosition = blurAdapter.getSelectPosition();
                    blurAdapter.setSelectPosition(-1);
                    if (aSelectPosition != -1) {
                        blurAdapter.notifyItemChanged(aSelectPosition);
                    }
                }

                mColorRecyclerView.setVisibility(View.VISIBLE);
                mStyleSelectPosition = -2;
                mStyleAddSelectBg.setVisibility(View.GONE);
                if (mCanvasStyleAdapter != null) {
                    int eSelectPosition = mCanvasStyleAdapter.getSelectPosition();
                    mCanvasStyleAdapter.setSelectPosition(-1);
                    if (eSelectPosition != -1) {
                        mCanvasStyleAdapter.notifyItemChanged(eSelectPosition);
                    }
                }
                SmartLog.i(TAG, "initStyleData:addCanvasResult:" + path);
                mStyleSelectPath = path;
                mStyleAddSelectBg.setVisibility(View.VISIBLE);
                mStyleAddNormalBg.setVisibility(View.GONE);
                mStyleAddNormalImage.setVisibility(View.GONE);
                mStyleAddSelectImage.setVisibility(View.VISIBLE);
                stylePath = path;
                styleId = null;
                if (isSelectFromAlbum) {
                    mCanvasPanelViewModel.setBackgroundBitmap(stylePath, null, applyAll);
                }
                Glide.with(context)
                    .load(path)
                    .apply(new RequestOptions().transform(
                        new MultiTransformation<>(new CenterCrop(), new RoundedCorners(SizeUtils.dp2Px(context, 2)))))
                    .into(mStyleAddSelectImage);
                mStyleAddDeleteImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkHistory() {
        if (TextUtils.isEmpty(mStyleSelectCloudId) || mStyleSelectPosition >= 0) {
            return;
        }
        for (CloudMaterialBean cutContent : mStyleList) {
            if (mStyleSelectCloudId.equals(cutContent.getId())) {
                mStyleSelectPosition = mStyleList.indexOf(cutContent) + 1;
                mCanvasStyleAdapter.setSelectPosition(mStyleSelectPosition);
            }
        }
    }

    private void updateProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadPosition < mStyleList.size()
            && downloadInfo.getContentId().equals(mStyleList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mStyleRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mHwProgressBar = viewHolder.itemView.findViewById(R.id.item_progress);
                mHwProgressBar.setProgress(downloadInfo.getProgress());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEditPreviewViewModel.setCanvasImageData("");
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        mEditPreviewViewModel.destroyTimeoutManager();
    }

    public void showBottomDialog() {
        colorPickerFragment.setLayoutParams(mLayoutParams);
        colorPickerFragment.setSeekbarParams(mSeekbarParams);
        colorPickerFragment.setXEvent(xEvent);
        colorPickerFragment.show(fm, "colorPickerFragment");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setIsSlid(boolean isSlid) {
        mCanvascrollview.setOnTouchListener((v, event) -> isSlid);
    }

    private void initPrimaryViewState() {
        blur_rl_add.setSelected(false);
        blur_rl_custom.setSelected(false);
        mCustomColorBg.setVisibility(View.INVISIBLE);
        mBlurSeek.setVisibility(View.INVISIBLE);
        mBlurSeek.setAnchorProgress(0);
        mBlurSeek.setProgress(0);
        rgbColor = null;
        mCanvasPanelViewModel.setBackgroundColor(null, applyAll);
        stylePath = null;
        styleId = null;
        mCanvasPanelViewModel.setBackgroundBitmap(null, null, applyAll);
        blurs = null;
        mCanvasPanelViewModel.setBackgroundBlur(null, applyAll);
    }

    private void initPrimaryState() {
        mBlueRecyclerView.setVisibility(View.VISIBLE);
        if (blurAdapter != null) {
            int aSelectPosition = blurAdapter.getSelectPosition();
            blurAdapter.setSelectPosition(-1);
            if (aSelectPosition != -1) {
                blurAdapter.notifyItemChanged(aSelectPosition);
            }
        }
        mColorRecyclerView.setVisibility(View.VISIBLE);
        if (canvasColorAdapter != null) {
            int colorSelectPosition = canvasColorAdapter.getSelectPosition();
            canvasColorAdapter.setSelectPosition(-1);
            if (colorSelectPosition != -1) {
                canvasColorAdapter.notifyItemChanged(colorSelectPosition);
            }
        }
        mStyleSelectPosition = -2;
        mStyleAddSelectBg.setVisibility(View.GONE);
        if (mCanvasStyleAdapter != null) {
            int fSelectPosition = mCanvasStyleAdapter.getSelectPosition();
            mCanvasStyleAdapter.setSelectPosition(-1);
            if (fSelectPosition != -1) {
                mCanvasStyleAdapter.notifyItemChanged(fSelectPosition);
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDynamicViewLayoutChange();
    }
}
