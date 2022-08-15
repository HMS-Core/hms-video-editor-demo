/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.fragment;

import static com.huawei.hms.videoeditor.sdk.exception.HVEError.ERROR_EXPORT_DISK_NOT_ENOUGH;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.ui.common.LazyFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonBottomDialog;
import com.huawei.hms.videoeditor.ui.mediaexport.VideoExportActivity;
import com.huawei.hms.videoeditor.ui.mediaexport.model.ExportResult;
import com.huawei.hms.videoeditor.ui.mediaexport.view.ProgressView;
import com.huawei.hms.videoeditor.ui.mediaexport.view.SettingView;
import com.huawei.hms.videoeditor.ui.mediaexport.viewmodel.ExportViewModel;
import com.huawei.hms.videoeditor.ui.mediaexport.viewmodel.SettingViewModel;
import com.huawei.hms.videoeditor.ui.mediaexport.viewmodel.VerificationViewModel;
import com.huawei.hms.videoeditor.utils.SmartLog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.text.NumberFormat;
import java.util.Locale;

public class ExportFragment extends LazyFragment {
    private static final String TAG = "ExportFragment";

    // <------setting view 、confirm view------>
    private ConstraintLayout mSettingAndConfirm;

    private SettingView mSettingView;

    private Button mConfirmExport;

    private TextView mSizeView;

    private ConstraintLayout mVideoFrameLayout;

    private ImageView mIvPic;

    private FrameLayout mFrameLayout;

    private TextView mTip;

    private ProgressView mProgressbar;

    private TextView tvProgressPrompt;

    private ConstraintLayout mClExportFail;

    private ImageView mExportFail;

    private Button mExportAgain;

    private Button mExportCancel;

    private TextView mErrorMessage;

    private SettingViewModel settingViewModel;

    private VerificationViewModel verificationViewModel;

    private ExportViewModel exportViewModel;

    private CommonBottomDialog mStopExportDialog;

    private int mFrameLayoutWidth;

    private int mFrameLayoutHeight;

    private int layoutWidth;

    private int layoutHeight;

    public ExportFragment() {
    }

    @Override
    protected void setStatusBarColor(Activity activity) {
        statusBarColor = R.color.export_bg;
        navigationBarColor = R.color.export_bg;
        super.setStatusBarColor(activity);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_export;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        initSettingView(view);
        initExportingView(view);
        initExportResultView(view);
    }

    @Override
    protected void initData() {
        super.initData();
        initViewModel();
        initViewModelObserve();
        initViewStatus();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        initControlsListener();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshCover();
    }

    private void initSettingView(View view) {
        mSettingView = view.findViewById(R.id.scroll_layout_export_settings);
        mSettingAndConfirm = view.findViewById(R.id.setting_confirm_layout);
        mIvPic = view.findViewById(R.id.pic);
        mVideoFrameLayout = view.findViewById(R.id.video_frame_layout);
        mFrameLayout = view.findViewById(R.id.video_texture_view);
        mSizeView = view.findViewById(R.id.tv_size);
        mConfirmExport = view.findViewById(R.id.export);
    }

    private void initExportingView(View view) {
        mTip = view.findViewById(R.id.tv_tip);
        mProgressbar = view.findViewById(R.id.progressbar);
        tvProgressPrompt = view.findViewById(R.id.tv_progressbar_prompt);
        tvProgressPrompt.setText(String.format(Locale.ROOT, getResources().getString(R.string.export_progressing),
                NumberFormat.getPercentInstance().format(0)));
    }

    private void initExportResultView(View view) {
        mClExportFail = view.findViewById(R.id.cl_export_fail);
        mExportAgain = view.findViewById(R.id.export_again);
        mExportCancel = view.findViewById(R.id.back);
        mErrorMessage = view.findViewById(R.id.error_message);
        mExportFail = view.findViewById(R.id.iv_flag);
        mStopExportDialog = new CommonBottomDialog(mActivity);
    }

    private void initViewModel() {
        settingViewModel = new ViewModelProvider(mActivity, mFactory).get(SettingViewModel.class);
        verificationViewModel = new ViewModelProvider(mActivity, mFactory).get(VerificationViewModel.class);
        verificationViewModel.setEditUuid(settingViewModel.getEditUuid());
        exportViewModel = new ViewModelProvider(mActivity, mFactory).get(ExportViewModel.class);
        exportViewModel.setEditUuid(settingViewModel.getEditUuid());
    }

    private void updateVideoEstimateTextView(long size) {
        if (mActivity == null) {
            SmartLog.i(TAG, "update video size failed, mActivity is null");
            return;
        }

        String finalAge = String.format(Locale.ROOT, mActivity.getResources().getString(R.string.export_size), size);
        mSizeView.setText(finalAge);
    }

    private void initViewModelObserve() {
        settingViewModel.getHasCover().observe(mActivity, hasCover -> {
            mFrameLayoutWidth = settingViewModel.getCanvasWidth();
            mFrameLayoutHeight = settingViewModel.getCanvasHeight();
            if (hasCover) {
                setCover(mFrameLayoutWidth, mFrameLayoutHeight);
                return;
            }
            mVideoFrameLayout.post(() -> {
                updateFrameParams();
                mIvPic.setBackgroundColor(getResources().getColor(R.color.pure_black));
            });
        });

        settingViewModel.getFirstFrame().observe(mActivity, bitmap -> setCover(bitmap.getWidth(), bitmap.getHeight()));

        exportViewModel.getExportProgress().observe(mActivity, this::setExportProgress);

        exportViewModel.getExportComplete().observe(mActivity, this::executeExportResult);
    }

    private void initViewStatus() {
        settingViewModel.requestCoverImg();
        updateVideoEstimateTextView(exportViewModel.calculateSize(false));
        mSettingView.setMaxProgress(verificationViewModel.getResolutionMaxProgress(), verificationViewModel.getFrameRateMaxProgress());
    }

    private void initControlsListener() {
        mConfirmExport.setOnClickListener(onClickRepeatedListener);
        mSettingView.setSettingViewChangeListener(settingViewChangeListener);
        mExportAgain.setOnClickListener(new OnClickRepeatedListener(v -> repeatExport()));
        mExportCancel.setOnClickListener(new OnClickRepeatedListener(v -> mActivity.finish()));
    }

    OnClickRepeatedListener onClickRepeatedListener = new OnClickRepeatedListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startExport();
        }
    });

    SettingView.SettingViewChangeListener settingViewChangeListener = new SettingView.SettingViewChangeListener() {
        @Override
        public void onResolutionChanged(int width, int height) {
            updateVideoEstimateTextView(exportViewModel.calculateSize(width, height));
        }

        @Override
        public void onFrameChanged(int frame) {
            updateVideoEstimateTextView(exportViewModel.calculateSize(frame));
        }
    };

    private void setCover(int width, int height) {
        mFrameLayoutWidth = width;
        mFrameLayoutHeight = height;
        mVideoFrameLayout.post(() -> {
            updateCoverLayout();
            updateProgressbarParams(layoutWidth, layoutHeight);
        });
    }

    private void setCoverParams() {
        ConstraintLayout.LayoutParams coverParams = (ConstraintLayout.LayoutParams) mIvPic.getLayoutParams();
        int coverWidth = mVideoFrameLayout.getMeasuredWidth();
        int coverHeight = mVideoFrameLayout.getMeasuredHeight();
        layoutWidth = mFrameLayoutWidth;
        layoutHeight = mFrameLayoutHeight;

        if (mFrameLayoutWidth > 0 && mFrameLayoutHeight > 0) {
            float sx = coverWidth / (float) mFrameLayoutWidth;
            float sy = coverHeight / (float) mFrameLayoutHeight;
            if (sx >= sy) {
                layoutWidth = (int) (mFrameLayoutWidth * coverHeight / (float) mFrameLayoutHeight);
                layoutHeight = coverHeight;
            } else {
                layoutWidth = coverWidth;
                layoutHeight = (int) (mFrameLayoutHeight * coverWidth / (float) mFrameLayoutWidth);
            }
        }

        coverParams.width = layoutWidth;
        coverParams.height = layoutHeight;
        coverParams.setMargins(0, SizeUtils.dp2Px(mContext,16), 0, 0);
        coverParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        coverParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        coverParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        coverParams.bottomToBottom = R.id.guideline;
        if (mFrameLayout != null) {
            ConstraintLayout.LayoutParams videoParams =
                    (ConstraintLayout.LayoutParams) mFrameLayout.getLayoutParams();
            videoParams.width = layoutWidth;
            videoParams.height = layoutHeight;
            videoParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            videoParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            videoParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            videoParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            mFrameLayout.setLayoutParams(videoParams);
        }

        mIvPic.setLayoutParams(coverParams);
    }

    private void showExportUI() {
        tvProgressPrompt.setVisibility(View.VISIBLE);
        mProgressbar.setVisibility(View.VISIBLE);
        mTip.setVisibility(View.VISIBLE);
        if (verificationViewModel.isNormalAsset()) {
            mProgressbar.setVisibility(View.VISIBLE);
        }
    }

    private void updateProgressbarParams(int width, int height) {
        double proportionWidth =
                mFrameLayoutWidth == 0 ? width : BigDecimalUtil.div(width, mFrameLayoutWidth, 5);
        double proportionHeight = mFrameLayoutHeight == 0 ? height
                : BigDecimalUtil.div(height, mFrameLayoutHeight, 5);
        ConstraintLayout.LayoutParams layoutParams;
        if (mFrameLayoutWidth > mFrameLayoutHeight) {
            if (height == 0 || mFrameLayoutHeight == 0) {
                layoutParams = new ConstraintLayout.LayoutParams(width, height);
            } else if ((width / height) > (mFrameLayoutWidth / mFrameLayoutHeight)) {
                layoutParams = new ConstraintLayout.LayoutParams((int) (proportionHeight * mFrameLayoutWidth),
                        height + SizeUtils.dp2Px(mContext,16));
            } else {
                layoutParams = new ConstraintLayout.LayoutParams(width,
                        (int) (proportionWidth * mFrameLayoutHeight + SizeUtils.dp2Px(mContext,16)));
            }
        } else {
            layoutParams = new ConstraintLayout.LayoutParams((int) (proportionHeight * mFrameLayoutWidth),
                    height + SizeUtils.dp2Px(mContext,16));
        }
        layoutParams.leftToLeft = R.id.pic;
        layoutParams.topToTop = R.id.pic;
        layoutParams.rightToRight = R.id.pic;
        layoutParams.bottomToBottom = R.id.pic;
        if (mProgressbar != null) {
            mProgressbar.setLayoutParams(layoutParams);
            mProgressbar.setHeightByVideoHeight();
            mProgressbar.setProgress(100, mProgressbar.getProgress());
        }
    }

    private void updateCoverLayout() {
        setCoverParams();

        Glide.with(this)
                .load(settingViewModel.getCoverUrl())
                .apply(new RequestOptions().transform(new MultiTransformation<>(new CenterCrop())))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvPic);
    }

    @SuppressLint("SetTextI18n")
    public void setExportProgress(int progress) {
        tvProgressPrompt.setText(String.format(Locale.ROOT, getResources().getString(R.string.export_progressing),
                NumberFormat.getInstance().format(progress) + "%"));
        mProgressbar.setProgress(100, progress);
    }

    public void showExportStopDialog() {
        if (mActivity.isFinishing()) {
            return;
        }
        mStopExportDialog.show(getString(R.string.video_edit_export_msg), getString(R.string.video_edit_export_stop),
                getString(R.string.video_edit_export_cancel));
        mStopExportDialog.setOnDialogClickLister(new CommonBottomDialog.OnDialogClickLister() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onAllowClick() {
                if (exportViewModel != null) {
                    exportViewModel.interruptVideoExport();
                }
                if (mActivity instanceof VideoExportActivity) {
                    mActivity.finish();
                }
            }
        });
    }

    private void refreshCover() {
        ViewTreeObserver coverObserver = mVideoFrameLayout.getViewTreeObserver();
        coverObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateFrameParams();

                if (coverObserver.isAlive()) {
                    coverObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void startExport() {
        showExportUI();
        mSettingAndConfirm.setVisibility(View.GONE);
        exportViewModel.exportVideo(false, mActivity);
    }

    private void executeExportResult(ExportResult exportResult) {
        tvProgressPrompt.setVisibility(View.INVISIBLE);
        mProgressbar.setVisibility(View.INVISIBLE);
        mProgressbar.setProgress(100, 0);
        mTip.setVisibility(View.GONE);
        if (mStopExportDialog != null && mStopExportDialog.isShowing()) {
            mStopExportDialog.dismiss();
        }
        if (exportResult.isSuccess()) {
            settingViewModel.setUri(exportResult.getUri());
            settingViewModel.setVideoPath(exportResult.getVideoPath());
            NavController navController = NavHostFragment.findNavController(ExportFragment.this);
            NavDestination currentDestination = navController.getCurrentDestination();
            if (currentDestination != null && currentDestination.getId() == R.id.exportFragment) {
                navController.navigate(R.id.export_success);
            }
        } else {
            showExportFailedUI(exportResult.getResultCode());
        }
    }

    private void repeatExport() {
        showExportUI();
        mExportFail.setVisibility(View.INVISIBLE);
        mTip.setVisibility(View.VISIBLE);
        mClExportFail.setVisibility(View.GONE);
        exportViewModel.exportVideo(true, mActivity);
    }

    private void showExportFailedUI(int errCode) {
        mExportFail.setImageResource(R.drawable.bg_export_fail);
        mExportFail.setVisibility(View.VISIBLE);
        mClExportFail.setVisibility(View.VISIBLE);
        mTip.setVisibility(View.GONE);
        if (errCode == ERROR_EXPORT_DISK_NOT_ENOUGH) {
            mErrorMessage.setText(R.string.insufficient_disk_space);
        }
    }

    private void updateFrameParams() {
        setCoverParams();
        updateProgressbarParams(layoutWidth, layoutHeight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (settingViewModel == null) {
            return;
        }
        settingViewModel.removeCoverBitmap();
    }
}
