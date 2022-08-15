
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

package com.huawei.hms.videoeditor.ui.mediaeditor;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.IntentFrom.INTENT_FROM_IMAGE_LIB;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.AI_BODY_SEG;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.AI_BODY_SEG_KEY;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.AI_HEAD_SEG;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.AI_HEAD_SEG_KEY;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.AI_SEGMENTATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.AI_SEGMENTATION_KEY;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.TIME_LAPSE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.TIME_LAPSE_KEY;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.VIDEO_SELECTION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager.VIDEO_SELECTION_KEY;
import static com.huawei.hms.videoeditor.ui.mediaeditor.timelapse.TimeLapseViewModel.STATE_ERROR;
import static com.huawei.hms.videoeditor.ui.mediaeditor.timelapse.TimeLapseViewModel.STATE_NO_SKY_WATER;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_AUDIO_CUSTOM_CURVESPEED;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_PIP_OPERATION_AI_SEGMENTATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_PIP_OPERATION_BODY_SEG;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_PIP_OPERATION_HEAD_SEG;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_PIP_OPERATION_HUMAN_TRACKING;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_PIP_OPERATION_TIME_LAPSE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_PIP_OPERATION_WINGS;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_OPERATION_AI_SEGMENTATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_OPERATION_HEAD_SEG;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_AI_SEGMENTATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_HEAD_SEG;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel.AUDIO_TYPE_MUSIC;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.LicenseException;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIError;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVECut;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.bean.HVESpeedCurvePoint;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.AudioData;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.CodecUtil;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SharedPreferenceUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SharedPreferencesUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SoftKeyBoardUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ThumbNailMemoryCache;
import com.huawei.hms.videoeditor.ui.common.utils.ToastUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.utils.VolumeChangeObserver;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.dialog.AdvanceExitDialog;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonProgressDialog;
import com.huawei.hms.videoeditor.ui.common.view.dialog.HumanTrackingProgressDialog;
import com.huawei.hms.videoeditor.ui.common.view.dialog.LoadingDialog;
import com.huawei.hms.videoeditor.ui.common.view.dialog.ProgressDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.aibodyseg.BodySegViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.aifun.AIBlockingHintDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.aisegmantation.SegmentationFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.aisegmantation.SegmentationViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.blockface.FaceBlockingFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.blockface.FaceBlockingViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.blockface.FaceProgressDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverImageViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.crop.AssetCropFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.crop.CropNewActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.keyframe.KeyFrameFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditData;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.DefaultPlayControlView;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.EditItemViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuControlViewRouter;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.persontrack.DrawBoxUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.persontrack.HumanTrackingConfirmDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.persontrack.PersonTrackingFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.persontrack.PersonTrackingViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.speed.CustomCurveSpeedFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.split.AssetSplitFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.fragment.EditPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.fragment.TrailerFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.timelapse.TimeLapseConfirmDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.timelapse.TimeLapseFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.timelapse.TimeLapseViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.fragment.EditPreviewFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditor.ui.mediaexport.VideoExportActivity;
import com.huawei.hms.videoeditor.ui.mediaexport.model.ExportConstants;
import com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoClipsActivity extends BaseActivity implements DefaultPlayControlView.HideLockButton {
    private static final String TAG = "VideoClipsActivity";

    private static final int TOAST_TIME = 700;

    public static final int VIEW_NORMAL = 1;

    public static final int VIEW_HISTORY = 3;

    public static final String CLIPS_VIEW_TYPE = "clipsViewType";

    public static final String PROJECT_ID = "projectId";

    public static final String CURRENT_TIME = "mCurrentTime";

    public static final String SOURCE = "source";

    public static final int ACTION_ADD_MEDIA_REQUEST_CODE = 1001;

    public static final int ACTION_ADD_AUDIO_REQUEST_CODE = 1002;

    public static final int ACTION_ADD_PICTURE_IN_REQUEST_CODE = 1003;

    public static final int ACTION_SPEECH_SYNTHESIS_REQUEST_CODE = 1004;

    public static final int ACTION_ADD_COVER_REQUEST_CODE = 1005;

    public static final int ACTION_ADD_CANVAS_REQUEST_CODE = 1006;

    public static final int ACTION_REPLACE_VIDEO_ASSET = 1007;

    public static final int ACTION_ADD_STICKER_REQUEST_CODE = 1009;

    public static final int ACTION_EXPORT_REQUEST_CODE = 1010;

    public static final int ACTION_PIP_VIDEO_ASSET = 1013;

    public static final int ACTION_CLIP_REQUEST_CODE = 1008;

    public static final int ACTION_ADD_BLOCKING_STICKER_REQUEST_CODE = 1015;

    public static final int ACTION_ADD_SELECTION_REQUEST_CODE = 1017;

    public static final String MAIN_ACTIVITY_NAME = "com.huawei.hms.videoeditor.MainActivity";

    public static final String EXTRA_FROM_SELF_MODE = "extra_from_self_mode";

    public static final String EDITOR_UUID = "editor_uuid";

    private static final long SEEK_INTERVAL = 10;

    private static final int MAX_TEXT = 50;

    private static final int VIEW_TYPE = 3;

    private RelativeLayout mVideoClipsNavBar;

    private ImageView mIvBack;

    private EditorTextView mTvExport;

    private ImageView mIvExport;

    private ImageView mIvFaceCompare;

    private Guideline guideline;

    private FrameLayout mSdkPlayLayout;

    private MenuFragment mMenuFragment;

    private VideoClipsPlayViewModel mSdkPlayViewModel;

    private EditItemViewModel mEditViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private EditPreviewViewModel mEditPreviewViewModel;

    private PersonTrackingViewModel mPersonTrackingViewModel;

    private FaceBlockingViewModel mFaceBlockingViewModel;

    private SegmentationViewModel mSegmentationViewModel;

    private BodySegViewModel mBodySegViewModel;

    private CoverImageViewModel mCoverImageViewModel;

    private MenuViewModel mMenuViewModel;

    private ArrayList<MediaData> mMediaDataList;

    private Context mContext;

    private Handler seekHandler;

    private volatile long mCurrentTime = 0;

    private long lastSeeKTime;

    private long lastTimeLineTime;

    private boolean isFullScreenState = false;

    private boolean mirrorStatus;

    public volatile boolean isVideoPlaying = false;

    private String mProjectId = "";

    private TranslateAnimation mHiddenAnim;

    private TranslateAnimation mShowAnim;

    SoftKeyBoardUtils mSoftKeyBoardUtils;

    private boolean isFromSelf = true;

    private boolean isSaveToApp = false;

    private VideoClipsPlayFragment mVideoClipsPlayFragment;

    private AdvanceExitDialog advanceExitDialog;

    private ToastWrapper mToastState;

    private ProgressDialog progressDialog;

    private CommonProgressDialog mSegmentationDialog;

    private CommonProgressDialog mBodySegDialog;

    private RelativeLayout mTextTemplateLayout;

    private EditText mTextTemplateEdit;

    private ImageView mTextTemplateConfirmTv;

    private boolean isSoftKeyboardShow = false;

    private EditPreviewFragment mEditPreviewFragment;

    private int mSoftKeyboardHeight = 0;

    private int mViewType;

    private final static int MIN_PROGRESS = 2;

    private FaceProgressDialog mFaceProgressDialog;

    private HumanTrackingProgressDialog mPersonTrackingDialog;

    private boolean isAbnormalExit;

    private PictureStickerChangeEvent mPictureStickerChangeEvent;

    private HuaweiVideoEditor mEditor;

    private TimeLapseViewModel mTimeLapseViewModel;

    private CommonProgressDialog mCommonProgressDialog;

    private long segTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColor = R.color.home_color_FF181818;
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_clips);
        createTailSource();
        isAbnormalExit = false;
        if (savedInstanceState != null) {
            isAbnormalExit = true;
            mCurrentTime = savedInstanceState.getLong(CURRENT_TIME);
            mViewType = savedInstanceState.getInt(CLIPS_VIEW_TYPE);
            isFromSelf = savedInstanceState.getBoolean(EXTRA_FROM_SELF_MODE);
            mProjectId = savedInstanceState.getString(PROJECT_ID);
        }
        initView();
        initNavBarAnim();
        initObject();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEditor != null) {
            EditorManager.getInstance().setEditor(mEditor);
            mMenuViewModel.setEditPreviewViewModel(mEditPreviewViewModel);
            mMenuViewModel.setMaterialEditViewModel(mMaterialEditViewModel);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.reSizeDialog();
        }
        if (mFaceProgressDialog != null && mFaceProgressDialog.isShowing()) {
            mFaceProgressDialog.reSizeDialog();
        }
        if (mCommonProgressDialog != null && mCommonProgressDialog.isShowing()) {
            mCommonProgressDialog.reSizeDialog();
        }
        if (isSoftKeyboardShow) {
            hideKeyboard();
        }
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.setSelectedUUID("");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mVideoClipsNavBar = findViewById(R.id.cl_video_clips_nav_bar);
        mIvBack = findViewById(R.id.iv_back);
        mTvExport = findViewById(R.id.tv_save);
        mIvExport = findViewById(R.id.iv_save);
        guideline = findViewById(R.id.guideline);
        mIvFaceCompare = findViewById(R.id.iv_face_compare);

        mSdkPlayLayout = findViewById(R.id.id_edit_play_layout);
        mVideoClipsPlayFragment =
                (VideoClipsPlayFragment) getSupportFragmentManager().findFragmentById(R.id.id_edit_play_fragment);

        mEditPreviewFragment =
                (EditPreviewFragment) getSupportFragmentManager().findFragmentById(R.id.id_edit_preview_fragment);
        mMenuFragment = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.id_menu_fragment);
        mTextTemplateLayout = findViewById(R.id.include_edit);
        mTextTemplateEdit = findViewById(R.id.edit_text_template);
        mTextTemplateConfirmTv = findViewById(R.id.img_certain);

        if (mVideoClipsPlayFragment != null) {
            mVideoClipsPlayFragment.setHideLockButton(this);
        }
    }

    private void initObject() {
        mContext = this;
        mSdkPlayViewModel = new ViewModelProvider(this, factory).get(VideoClipsPlayViewModel.class);
        mEditPreviewViewModel = new ViewModelProvider(this, factory).get(EditPreviewViewModel.class);
        mPersonTrackingViewModel = new ViewModelProvider(this, factory).get(PersonTrackingViewModel.class);
        mFaceBlockingViewModel = new ViewModelProvider(this, factory).get(FaceBlockingViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(this, factory).get(MaterialEditViewModel.class);
        mEditViewModel = new ViewModelProvider(this, factory).get(EditItemViewModel.class);
        mMenuViewModel = new ViewModelProvider(this, factory).get(MenuViewModel.class);
        mTimeLapseViewModel = new ViewModelProvider(this, factory).get(TimeLapseViewModel.class);
        mSegmentationViewModel = new ViewModelProvider(this, factory).get(SegmentationViewModel.class);
        mCoverImageViewModel = new ViewModelProvider(this, factory).get(CoverImageViewModel.class);
        mBodySegViewModel = new ViewModelProvider(this, factory).get(BodySegViewModel.class);

        seekHandler = new Handler();
        lastSeeKTime = System.currentTimeMillis();
        mMediaDataList = new ArrayList<>();
        mSoftKeyBoardUtils = new SoftKeyBoardUtils(this);

        SafeIntent safeIntent = new SafeIntent(getIntent());
        if (!isAbnormalExit) {
            mViewType = safeIntent.getIntExtra(CLIPS_VIEW_TYPE, 1);
            isFromSelf = safeIntent.getBooleanExtra(EXTRA_FROM_SELF_MODE, false);
            mProjectId = safeIntent.getStringExtra(PROJECT_ID);
        }
        ArrayList<MediaData> list = safeIntent.getParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT);

        Constant.IntentFrom.INTENT_WHERE_FROM =
                ("highlight".equals(safeIntent.getStringExtra(SOURCE)) ? INTENT_FROM_IMAGE_LIB : 0);
        String editorUuid = safeIntent.getStringExtra(EDITOR_UUID);

        if (!TextUtils.isEmpty(editorUuid)) {
            mEditor = HuaweiVideoEditor.getInstance(editorUuid);
        } else {
            mEditor = HuaweiVideoEditor.create(getApplicationContext(), mProjectId);
            try {
                mEditor.initEnvironment();
            } catch (LicenseException error) {
                SmartLog.e(TAG, "initEnvironment failed: " + error.getErrorMsg());
                ToastWrapper.makeText(mContext, mContext.getResources().getString(R.string.license_invalid)).show();
                finish();
                return;
            }
        }

        if (mEditor == null) {
            return;
        }
        EditorManager.getInstance().setEditor(mEditor);
        mEditPreviewViewModel.setFragment(mEditPreviewFragment);
        mMenuViewModel.setEditPreviewViewModel(mEditPreviewViewModel);
        switch (mViewType) {
            case VIEW_NORMAL:
                mMediaDataList = new ArrayList<>();
                if (list != null) {
                    mMediaDataList.addAll(list);
                }
                if (Constant.IntentFrom.INTENT_WHERE_FROM != Constant.IntentFrom.INTENT_FROM_IMAGE_LIB) {
                    if (EditorManager.getInstance().getMainLane() == null) {
                        if (EditorManager.getInstance().getTimeLine() == null) {
                            return;
                        }
                        EditorManager.getInstance().getTimeLine().appendVideoLane();
                    }

                    for (MediaData data : mMediaDataList) {
                        if (data != null) {
                            if (data.getType() == MediaData.MEDIA_VIDEO) {
                                HVEVideoAsset hveVideoAsset = EditorManager.getInstance()
                                        .getMainLane()
                                        .appendVideoAsset(data.getPath(), data.getDuration(), data.getWidth(),
                                                data.getHeight());
                                mMenuViewModel.cutAssetNoSeekTimeLine(data, hveVideoAsset);

                                if (mMediaDataList.size() == 1 && !isFromSelf) {
                                    defaultSelect(hveVideoAsset);
                                }
                            } else {
                                HVEImageAsset imageAsset = EditorManager.getInstance()
                                        .getMainLane()
                                        .appendImageAsset(data.getPath(), data.getDuration(), data.getWidth(),
                                                data.getHeight());

                                mMenuViewModel.cutAssetNoSeekTimeLine(data, imageAsset);

                                if (mMediaDataList.size() == 1 && !isFromSelf) {
                                    defaultSelect(imageAsset);
                                }
                            }
                        }
                    }
                }
                break;
            case VIEW_HISTORY:
                break;
            default:
                break;

        }
        mVideoClipsPlayFragment.initEditor();

        mEditPreviewViewModel.updateDuration();
        mEditPreviewViewModel.refreshAssetList();
        VolumeChangeObserver instance = VolumeChangeObserver.getInstace(getApplicationContext());
        instance.registerVolumeReceiver();
        SmartLog.d(TAG, "VideoClipsActivity projectid:" + EditorManager.getInstance().getEditor().getProjectId());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mIvBack.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextStyle();

                onBackPressed();
            }
        }, 100));

        mTvExport.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSdk = new Intent(mContext, VideoExportActivity.class);

                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    SmartLog.e(TAG, "Export Clicked but editor is null");
                    return;
                }
                HVETimeLine timeLine = editor.getTimeLine();
                if (timeLine == null) {
                    return;
                }
                if (mCoverImageViewModel != null) {
                    mCoverImageViewModel.updateDefaultCover(editor, timeLine.getCurrentTime());
                }
                intentSdk.putExtra(ExportConstants.EDITOR_UUID, editor.getUuid());

                SafeIntent safeIntent = new SafeIntent(getIntent());
                intentSdk.putExtra(SOURCE, safeIntent.getStringExtra(SOURCE));

                if (timeLine.getCoverImage() != null) {
                    intentSdk.putExtra(ExportConstants.COVER_URL, timeLine.getCoverImage().getPath());
                }

                startActivityForResult(intentSdk, ACTION_EXPORT_REQUEST_CODE);
            }
        }));

        mEditPreviewViewModel.getVideoDuration().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                mSdkPlayViewModel.setVideoDuration(aLong);
            }
        });

        mSdkPlayViewModel.setCurrentTime(0L);
        mSdkPlayViewModel.getCurrentTime().observe(this, time -> {
            if (time == -1) {
                mCurrentTime = 0;
                return;
            }
            mCurrentTime = time;
            if (mEditPreviewViewModel == null) {
                return;
            }
            mEditPreviewViewModel.setCurrentTime(mCurrentTime);
            mEditPreviewViewModel.isAlarmClock(System.currentTimeMillis());
        });

        mEditPreviewViewModel.getCurrentTime().observe(this, time -> {
            if (mCurrentTime == time || mSdkPlayViewModel == null) {
                return;
            }
            mSdkPlayViewModel.setCurrentTime(time);
            mVideoClipsPlayFragment.setSeekBarProgress(time);
        });

        mSdkPlayViewModel.getFullScreenState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFullScreen) {
                isFullScreenState = isFullScreen;
                if (isFullScreenState) {
                    guideline.setGuidelinePercent(1);
                    mMenuFragment.showMenu(false);
                    mIvBack.setVisibility(View.VISIBLE);
                    mTvExport.setVisibility(View.GONE);
                    ConstraintLayout.LayoutParams fullScreenParam = new ConstraintLayout.LayoutParams(0, 0);
                    fullScreenParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    fullScreenParam.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                    fullScreenParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                    fullScreenParam.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    mSdkPlayLayout.setLayoutParams(fullScreenParam);
                    mEditPreviewViewModel.setSelectedUUID("");
                } else {
                    mIvBack.setVisibility(View.VISIBLE);
                    guideline.setGuidelinePercent(0.575f);
                    mMenuFragment.showMenu(true);
                    mIvExport.setVisibility(View.GONE);
                    if (mVideoClipsNavBar.getVisibility() != View.VISIBLE) {
                        mVideoClipsNavBar.setVisibility(View.VISIBLE);
                    }
                    if (Constant.IntentFrom.INTENT_WHERE_FROM != 0) {
                        Constant.IntentFrom.INTENT_WHERE_FROM = 0;
                        SmartLog.d(TAG, "INTENT_WHERE_FROM B" + 0);
                    }
                    mTvExport.setVisibility(View.VISIBLE);
                    ConstraintLayout.LayoutParams defaultParam = new ConstraintLayout.LayoutParams(0, 0);
                    defaultParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    defaultParam.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                    defaultParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                    defaultParam.bottomToTop = R.id.guideline;
                    mSdkPlayLayout.setLayoutParams(defaultParam);
                }
            }
        });

        mSdkPlayViewModel.getPlayState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPlaying) {
                if (isPlaying && seekHandler != null) {
                    seekHandler.removeCallbacksAndMessages(null);
                }
                isVideoPlaying = isPlaying;
            }
        });

        mEditPreviewViewModel.getFaceDetectError().observe(this, errorCode -> {
            switch (errorCode) {
                case HVEAIError.AI_ERROR_CPU_NOT_SUPPORT:
                    ToastWrapper.makeText(mContext, R.string.device_not_support, Toast.LENGTH_SHORT).show();
                    break;
                case HVEAIError.AI_ERROR_NETWORK:
                case HVEAIError.AI_ERROR_TIMEOUT:
                    ToastWrapper.makeText(mContext, R.string.result_illegal, Toast.LENGTH_SHORT).show();
                    break;
                case HVEAIError.AI_ERROR_UNKNOWN:
                    ToastWrapper.makeText(mContext, R.string.identify_failed, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        });

        progressDialog = new ProgressDialog(this, getString(R.string.video_run_backward));
        progressDialog.setOnProgressClick(() -> {
            mEditPreviewViewModel.cancelVideoRevert();
        });

        mEditPreviewViewModel.getReverseCallback().observe(this, integer -> {
            if (integer == 1) {
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show(getWindowManager());
                    progressDialog.setStopVisble(true);
                    progressDialog.setCancelable(true);
                    progressDialog.setProgress(0);
                }
            } else if (integer == 2) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.setProgress(mEditPreviewViewModel.getReverseProgress());
                }
            } else if (integer == 3) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

        mToastState = new ToastWrapper();

        mEditPreviewViewModel.getToastString().observe(this, str -> {
            if (mToastState != null) {
                mToastState.makeTextWithShow(this, str, TOAST_TIME);
                if (mEditPreviewViewModel == null) {
                    return;
                }
                if (!StringUtil.isEmpty(str) && (str.equals(getString(R.string.reverse_success))
                        || str.equals(getString(R.string.reverse_cancel))
                        || str.equals(getString(R.string.reverse_fail)))) {
                    mEditPreviewViewModel.setCurrentTime(mCurrentTime);
                }
            }
        });

        mMenuViewModel.isShowMenuPanel.observe(this, showMenuPanel -> {
            if (showMenuPanel) {
                if (mVideoClipsNavBar.getVisibility() == View.VISIBLE) {
                    mVideoClipsNavBar.startAnimation(mHiddenAnim);
                }
                mVideoClipsNavBar.setVisibility(View.GONE);
            } else {
                if (mVideoClipsNavBar.getVisibility() != View.VISIBLE) {
                    mVideoClipsNavBar.startAnimation(mShowAnim);
                }
                mVideoClipsNavBar.setVisibility(View.VISIBLE);
            }
        });

        mSoftKeyBoardUtils.setOnSoftKeyBoardChangeListener(new SoftKeyBoardUtils.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                isSoftKeyboardShow = true;
                mSoftKeyboardHeight = height;
                mEditPreviewViewModel.setKeyBordShowHeight(height);
                mEditPreviewViewModel.setKeyBordShow(true);

                Stack<MenuControlViewRouter.Panel> stack = mMenuFragment.getViewStack();
                if (stack != null && !stack.isEmpty()) {
                    MenuControlViewRouter.Panel panel = stack.lastElement();
                    if (panel.object instanceof EditPanelFragment) {
                        mEditPreviewViewModel.setEditTextStatus(true);
                    } else if (panel.object instanceof TrailerFragment) {
                        mEditPreviewViewModel.setTrailerStatus(true);
                    }
                }
            }

            @Override
            public void keyBoardHide(int height) {
                isSoftKeyboardShow = false;
                mEditPreviewViewModel.setKeyBordShow(false);
                showInputLayout(false);
            }
        });

        mTextTemplateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("StringFormatMatches")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 50) {
                    ToastWrapper.makeText(mContext, String.format(Locale.ROOT,
                            getResources().getString(R.string.most_text), NumberFormat.getInstance().format(MAX_TEXT)))
                            .show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || s.length() == 0) {
                    mEditPreviewViewModel.setIsClearTemplate(true);
                }
                mEditPreviewViewModel.setmLastInputText(s.toString());
                mEditPreviewViewModel.setTemplateText(s.toString());
            }
        });

        mTextTemplateConfirmTv.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPreviewViewModel.setmLastInputText("");
                mEditPreviewViewModel.setEditTextTemplateStatus(false);
                hideKeyboard();
            }
        }));
        mPersonTrackingViewModel.getHumanTrackingEnter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == -1) {
                    return;
                }
                HVEAsset selectedAsset;
                if (integer == EDIT_PIP_OPERATION_HUMAN_TRACKING) {
                    selectedAsset = mEditPreviewViewModel.getSelectedAsset();
                } else {
                    selectedAsset = mEditPreviewViewModel.getMainLaneAsset();
                }
                if (selectedAsset != null) {
                    mPersonTrackingViewModel.setSelectedTracking(selectedAsset);
                }
                if (selectedAsset == null) {
                    SmartLog.e(TAG, "HumanTracking asset is null!");
                    return;
                }
                if (selectedAsset instanceof HVEVideoAsset) {
                    Boolean isCancel = isContainHumanTrackingEffect(selectedAsset);
                    if (!isCancel) {
                        initHumanTracking(integer, selectedAsset);
                    } else {
                        showDeleteDialog(integer, selectedAsset);
                    }
                }
                mPersonTrackingViewModel.setHumanTrackingEnter(-1);
            }
        });

        mPersonTrackingViewModel.getTrackingPoint().observe(this, pointList -> {
            HVEAsset trackingAsset = mPersonTrackingViewModel.getSelectedTracking();
            if (trackingAsset != null) {
                List<HVEVideoLane> allVideoLane = mEditor.getTimeLine().getAllVideoLane();
                HVEVideoLane videoLane = allVideoLane.get(trackingAsset.getLaneIndex());
                if (videoLane == null) {
                    SmartLog.e(TAG, "VideoLane is null!");
                    return;
                }
                mEditor.getBitmapAtSelectedLan(trackingAsset.getLaneIndex(), mEditPreviewViewModel.getSeekTime(),
                        new HuaweiVideoEditor.ImageCallback() {
                            @Override
                            public void onSuccess(Bitmap bitmap, long time) {
                                if (bitmap == null) {
                                    return;
                                }
                                int width = bitmap.getWidth();
                                int height = bitmap.getHeight();
                                Point point = CodecUtil.calculateEnCodeWH(width, height);
                                float scale = BigDecimalUtils.div(width, point.x);

                                HVEPosition2D position2D = new HVEPosition2D(BigDecimalUtils.div(pointList.x, scale),
                                        BigDecimalUtils.div(pointList.y, scale));

                                Bitmap dstBitmap = Bitmap.createScaledBitmap(bitmap, point.x, point.y, true);
                                List<Float> aiHumanTracking = null;
                                if (trackingAsset instanceof HVEVideoAsset) {
                                    aiHumanTracking =
                                            ((HVEVideoAsset) trackingAsset).selectHumanTrackingPerson(dstBitmap, position2D);
                                }

                                if (aiHumanTracking != null && !aiHumanTracking.isEmpty()) {
                                    mPersonTrackingViewModel.setTrackingIsReady(true);
                                    float minx = aiHumanTracking.get(0);
                                    float miny = aiHumanTracking.get(1);
                                    float maxx = aiHumanTracking.get(2);
                                    float maxy = aiHumanTracking.get(3);
                                    List<MaterialEditData> mMaterialEditDataList = new ArrayList<>();
                                    MaterialEditData materialEditData = DrawBoxUtil.drawBox(
                                            mPersonTrackingViewModel.getSelectedTracking(), mEditor.getTimeLine(),
                                            MaterialEditData.MaterialType.PERSON, minx, miny, maxx, maxy);
                                    mMaterialEditDataList.add(materialEditData);
                                    mMaterialEditViewModel.addMaterialEditDataList(mMaterialEditDataList);
                                } else {
                                    mPersonTrackingViewModel.setTrackingIsReady(false);
                                    runOnUiThread(() -> ToastWrapper
                                            .makeText(VideoClipsActivity.this, R.string.no_person_selected, Toast.LENGTH_SHORT)
                                            .show());
                                }

                                if (!bitmap.isRecycled()) {
                                    bitmap.recycle();
                                }
                                if (!dstBitmap.isRecycled()) {
                                    dstBitmap.recycle();
                                }
                            }

                            @Override
                            public void onFail(int errorCode) {
                                SmartLog.e(TAG, getString(R.string.result_illegal));
                                mPersonTrackingViewModel.setTrackingIsReady(false);
                                ToastWrapper.makeText(VideoClipsActivity.this, R.string.result_illegal, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        });

        mPersonTrackingViewModel.getTrackingIsStart().observe(this, integer -> {
            if (integer == -1) {
                return;
            }
            if (mPersonTrackingDialog == null) {
                mPersonTrackingDialog = new HumanTrackingProgressDialog(this, getString(R.string.people_tracking));
                mPersonTrackingDialog.setOnProgressClick(() -> {
                    mPersonTrackingViewModel.interruptHumanTracking();
                    ToastWrapper.makeText(this, getString(R.string.tracking_cancel), Toast.LENGTH_SHORT).show();
                    mPersonTrackingDialog = null;
                });

                mPersonTrackingDialog.setOnCancelListener(dialog -> {
                    mPersonTrackingViewModel.interruptHumanTracking();
                    ToastWrapper.makeText(this, getString(R.string.tracking_cancel), Toast.LENGTH_SHORT).show();
                    mPersonTrackingDialog = null;
                });
            }
            mPersonTrackingDialog.show(getWindowManager());
            mPersonTrackingDialog.setStopVisble(true);
            mPersonTrackingDialog.setCancelable(true);
            mPersonTrackingViewModel.humanTracking(integer, new HVEAIProcessCallback() {
                @Override
                public void onProgress(int progress) {
                    runOnUiThread(() -> {
                        if (mPersonTrackingDialog != null) {
                            mPersonTrackingDialog.setProgress(progress);
                        }
                    });
                }

                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        if (mPersonTrackingDialog != null) {
                            mPersonTrackingDialog.setProgress(0);
                            mPersonTrackingDialog.dismiss();
                        }
                        if (mEditPreviewViewModel != null) {
                            mEditPreviewViewModel.setSelectedUUID("");
                            mEditPreviewViewModel.setCurrentTime(mCurrentTime);
                        }

                        ToastWrapper
                                .makeText(VideoClipsActivity.this, getString(R.string.tracking_success), Toast.LENGTH_SHORT)
                                .show();
                    });
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    runOnUiThread(() -> {
                        if (mPersonTrackingDialog != null) {
                            mPersonTrackingDialog.setProgress(0);
                            mPersonTrackingDialog.dismiss();
                        }
                        ToastWrapper
                                .makeText(VideoClipsActivity.this, getString(R.string.no_person_tracked),
                                        Toast.LENGTH_SHORT)
                                .show();
                    });
                }
            });
            mPersonTrackingViewModel.setTrackingIsStart(-1);
        });

        mSegmentationViewModel.getSegmentationEnter().observe(this, integer -> {
            boolean isSegFirst = SharedPreferenceUtils.get(getApplicationContext(), AI_SEGMENTATION)
                    .getBoolean(AI_SEGMENTATION_KEY, false);
            if (integer == -1) {
                return;
            }
            HVEAsset selectedAsset;
            if (integer == EDIT_PIP_OPERATION_AI_SEGMENTATION) {
                selectedAsset = mEditPreviewViewModel.getSelectedAsset();
            } else {
                selectedAsset = mEditPreviewViewModel.getMainLaneAsset();
            }
            if (selectedAsset == null) {
                SmartLog.e(TAG, "Segmentation asset is null!");
                return;
            }
            mSegmentationViewModel.setSelectedAsset(selectedAsset);
            if (!isSegFirst) {
                AIBlockingHintDialog dg = new AIBlockingHintDialog(VideoClipsActivity.this,
                        (VideoClipsActivity.this).getString(R.string.cut_second_menu_segmentation),
                        (VideoClipsActivity.this).getString(R.string.auto_mark_description));
                dg.show();
                dg.setOnPositiveClickListener(() -> {
                    SharedPreferenceUtils.get(getApplicationContext(), AI_SEGMENTATION).put(AI_SEGMENTATION_KEY, true);
                    handleSegmentation(selectedAsset, integer);
                });
                dg.setOnCancelClickListener(() -> SharedPreferenceUtils.get(getApplicationContext(), AI_SEGMENTATION)
                        .put(AI_SEGMENTATION_KEY, false));
            } else {
                handleSegmentation(selectedAsset, integer);
            }
        });

        mSegmentationViewModel.getIsInit().observe(this, aBoolean -> {
            if (aBoolean) {
                List<MaterialEditData> mMaterialEditDataList = new ArrayList<>();
                MaterialEditData materialEditData =
                        new MaterialEditData((HVEVisibleAsset) mSegmentationViewModel.getSelectedAsset(),
                                MaterialEditData.MaterialType.SEGMENTATION, null);
                mMaterialEditDataList.add(materialEditData);
                mMaterialEditViewModel.addMaterialEditDataList(mMaterialEditDataList);
            }
        });

        final LoadingDialog dialog = new LoadingDialog(this, R.style.CustomDialog);
        Handler handler = new Handler(getMainLooper());

        mSegmentationViewModel.getPoints().observe(this, points -> {
            HVEAsset trackingAsset = mSegmentationViewModel.getSelectedAsset();
            if (trackingAsset != null) {
                List<HVEVideoLane> allVideoLane = mEditor.getTimeLine().getAllVideoLane();
                HVEVideoLane videoLane = allVideoLane.get(trackingAsset.getLaneIndex());
                if (videoLane == null) {
                    SmartLog.e(TAG, "VideoLane is null!");
                    return;
                }

                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }

                mEditor.getBitmapAtSelectedLan(trackingAsset.getLaneIndex(), mEditPreviewViewModel.getSeekTime(),
                        new HuaweiVideoEditor.ImageCallback() {
                            @Override
                            public void onSuccess(Bitmap bitmap, long time) {
                                if (handler != null) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog != null && dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                        }
                                    }, 1000);
                                }

                                if (bitmap == null) {
                                    return;
                                }
                                int width = bitmap.getWidth();
                                int height = bitmap.getHeight();
                                Point point = CodecUtil.calculateEnCodeWH(width, height);
                                Bitmap dstBitmap = Bitmap.createScaledBitmap(bitmap, point.x, point.y, true);
                                int result = -1;
                                if (trackingAsset instanceof HVEVideoAsset) {
                                    result =
                                            ((HVEVideoAsset) trackingAsset).selectSegmentationObject(dstBitmap, time, points);
                                }

                                if (result == 0) {
                                    mSegmentationViewModel.setIsReady(true);
                                }

                                if (!bitmap.isRecycled()) {
                                    bitmap.recycle();
                                }
                                if (!dstBitmap.isRecycled()) {
                                    dstBitmap.recycle();
                                }
                            }

                            @Override
                            public void onFail(int errorCode) {
                                if (handler != null) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog != null && dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                        }
                                    }, 1000);
                                }
                                SmartLog.e(TAG, getString(R.string.result_illegal));
                                mSegmentationViewModel.setIsReady(false);
                                ToastWrapper.makeText(VideoClipsActivity.this, R.string.result_illegal, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        });

        mSegmentationViewModel.getDrawPoints().observe(this, drawPoints -> {
            List<MaterialEditData> mMaterialEditDataList = new ArrayList<>();
            List<HVEPosition2D> segList = new ArrayList<>();
            int size = drawPoints.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    segList.add(new HVEPosition2D(drawPoints.get(i).x, drawPoints.get(i).y));
                }

                MaterialEditData materialEditData =
                        new MaterialEditData((HVEVisibleAsset) mSegmentationViewModel.getSelectedAsset(),
                                MaterialEditData.MaterialType.SEGMENTATION);
                materialEditData.setSegmentationList(segList);
                mMaterialEditDataList.add(materialEditData);
                mMaterialEditViewModel.addMaterialEditDataList(mMaterialEditDataList);
            }
        });

        mSegmentationViewModel.getIsStart().observe(this, integer -> {
            if (integer == -1) {
                return;
            }

            segmentationDetect(integer);
            mSegmentationViewModel.setIsStart(-1);
        });

        mEditPreviewViewModel.getFaceBlockingEnter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                detectFace(integer);
            }
        });

        mMenuViewModel.getVideoSelectionEnter().observe(this, integer -> {
            boolean isFirstShowDialog = SharedPreferenceUtils.get(getApplicationContext(), VIDEO_SELECTION)
                    .getBoolean(VIDEO_SELECTION_KEY, true);
            if (isFirstShowDialog) {
                AIBlockingHintDialog aiFunDialog = new AIBlockingHintDialog(VideoClipsActivity.this,
                        getString(R.string.cut_second_menu_video_selection), getString(R.string.auto_mark_description));
                aiFunDialog.setOnPositiveClickListener(() -> {
                    SharedPreferenceUtils.get(getApplicationContext(), VIDEO_SELECTION).put(VIDEO_SELECTION_KEY, false);
                    initVideoSelection(integer);
                });
                aiFunDialog
                        .setOnCancelClickListener(() -> SharedPreferenceUtils.get(getApplicationContext(), VIDEO_SELECTION)
                                .put(VIDEO_SELECTION_KEY, true));
                aiFunDialog.show();
            } else {
                initVideoSelection(integer);
            }
        });

        final LoadingDialog selectionDialog = new LoadingDialog(this, R.style.CustomDialog);
        Handler selectionHandler = new Handler(getMainLooper());

        mMenuViewModel.getVideoSelectionFinish().observe(this, integer -> {
            if (integer == 0 && (selectionDialog != null && !selectionDialog.isShowing())) {
                selectionDialog.show();
            } else {
                selectionHandler.postDelayed(() -> {
                    if (selectionDialog != null && selectionDialog.isShowing()) {
                        selectionDialog.dismiss();
                    }
                }, 500);
            }
        });

        mTimeLapseViewModel.getTimeLapseEnter().observe(this, integer -> {
            boolean isFirstShowDialog =
                    SharedPreferenceUtils.get(getApplicationContext(), TIME_LAPSE).getBoolean(TIME_LAPSE_KEY, true);
            if (isFirstShowDialog) {
                AIBlockingHintDialog aiFunDialog = new AIBlockingHintDialog(VideoClipsActivity.this,
                        getString(R.string.cut_second_menu_time_lapse), getString(R.string.auto_mark_description));
                aiFunDialog.setOnPositiveClickListener(() -> {
                    SharedPreferenceUtils.get(getApplicationContext(), TIME_LAPSE).put(TIME_LAPSE_KEY, false);
                    preDetectAsset(integer);
                });
                aiFunDialog.setOnCancelClickListener(
                        () -> SharedPreferenceUtils.get(getApplicationContext(), TIME_LAPSE).put(TIME_LAPSE_KEY, true));
                aiFunDialog.show();
            } else {
                preDetectAsset(integer);
            }
        });

        mTimeLapseViewModel.getTimeLapseStart().observe(this, integer -> {
            if (integer == -1) {
                return;
            }
            mCommonProgressDialog = new CommonProgressDialog(VideoClipsActivity.this, () -> {
                if (!isValidActivity()) {
                    SmartLog.e(TAG, "Activity is valid!");
                    return;
                }
                ToastWrapper.makeText(VideoClipsActivity.this, getString(R.string.time_lapse_cancel)).show();
                new Thread(() -> mTimeLapseViewModel.stopTimeLapse()).start();
                mCommonProgressDialog.dismiss();
                mCommonProgressDialog = null;
            });
            mCommonProgressDialog.setTitleValue(getString(R.string.time_lapse_process));
            mCommonProgressDialog.setCanceledOnTouchOutside(false);
            mCommonProgressDialog.setCancelable(false);
            mCommonProgressDialog.show();
            mTimeLapseViewModel.addTimeLapseEffect(mTimeLapseViewModel.getSkyRiverType(),
                    (float) mTimeLapseViewModel.getSpeedSky() / 3, mTimeLapseViewModel.getScaleSky(),
                    (float) mTimeLapseViewModel.getSpeedRiver() / 3, mTimeLapseViewModel.getScaleRiver(),
                    new HVEAIProcessCallback() {
                        @Override
                        public void onProgress(int progress) {
                            if (!isValidActivity()) {
                                return;
                            }
                            runOnUiThread(() -> {
                                if (mCommonProgressDialog != null) {
                                    if (!mCommonProgressDialog.isShowing()) {
                                        mCommonProgressDialog.show();
                                    }
                                    mCommonProgressDialog.updateProgress(progress);
                                }
                            });
                        }

                        @Override
                        public void onSuccess() {
                            if (!isValidActivity()) {
                                return;
                            }
                            runOnUiThread(() -> {
                                ToastWrapper.makeText(VideoClipsActivity.this, getString(R.string.time_lapse_complete))
                                        .show();
                                if (mCommonProgressDialog != null) {
                                    mCommonProgressDialog.updateProgress(0);
                                    mCommonProgressDialog.dismiss();
                                }
                                if (mEditPreviewViewModel != null) {
                                    mEditPreviewViewModel.updateVideoLane();
                                    mEditPreviewViewModel.refreshMenuState();
                                }
                                mTimeLapseViewModel.stopTimeLapse();
                            });
                        }

                        @Override
                        public void onError(int errorCode, String errorMsg) {
                            if (!isValidActivity()) {
                                return;
                            }
                            runOnUiThread(() -> {
                                ToastWrapper.makeText(VideoClipsActivity.this, getString(R.string.ai_exception)).show();
                            });
                            mTimeLapseViewModel.stopTimeLapse();
                        }
                    });
            mTimeLapseViewModel.setTimeLapseStart(-1);
        });

        mBodySegViewModel.getBodySegEnter().observe(this, integer -> {
            int segPart = 0;
            String sTitle = (VideoClipsActivity.this).getString(R.string.cut_second_menu_body_seg);
            String aiName = AI_BODY_SEG;
            String aiNameKey = AI_BODY_SEG_KEY;
            switch (integer) {
                case EDIT_VIDEO_STATE_HEAD_SEG:
                case EDIT_VIDEO_OPERATION_HEAD_SEG:
                case EDIT_PIP_OPERATION_HEAD_SEG:
                    segPart = 1;
                    sTitle = (VideoClipsActivity.this).getString(R.string.cut_second_menu_head_seg);
                    aiName = AI_HEAD_SEG;
                    aiNameKey = AI_HEAD_SEG_KEY;
                    break;
                default:
                    break;
            }
            boolean isShowDialog =
                    SharedPreferenceUtils.get(getApplicationContext(), aiName).getBoolean(aiNameKey, false);
            if (integer == -1) {
                return;
            }
            HVEAsset selectedAsset;
            if (integer == EDIT_PIP_OPERATION_BODY_SEG || integer == EDIT_PIP_OPERATION_HEAD_SEG) {
                selectedAsset = mEditPreviewViewModel.getSelectedAsset();
            } else {
                selectedAsset = mEditPreviewViewModel.getMainLaneAsset();
            }
            if (selectedAsset == null) {
                SmartLog.e(TAG, "Segmentation asset is null!");
                return;
            }
            mBodySegViewModel.setSelectedAsset(selectedAsset);
            int finalSegPart = segPart;
            if (!isShowDialog) {
                AIBlockingHintDialog dg = new AIBlockingHintDialog(VideoClipsActivity.this, sTitle,
                        (VideoClipsActivity.this).getString(R.string.auto_mark_description));
                dg.show();
                String finalAiName = aiName;
                String finalAiNameKey = aiNameKey;
                dg.setOnPositiveClickListener(() -> {
                    SharedPreferenceUtils.get(getApplicationContext(), finalAiName).put(finalAiNameKey, true);
                    handleBodySeg(selectedAsset, integer, finalSegPart);
                });
                dg.setOnCancelClickListener(
                        () -> SharedPreferenceUtils.get(getApplicationContext(), finalAiName).put(finalAiNameKey, false));
            } else {
                handleBodySeg(selectedAsset, integer, finalSegPart);
            }
        });

    }

    private void handleSegmentation(HVEAsset selectedAsset, int integer) {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "handleSegmentation asset is null!");
            return;
        }
        List<HVEEffect> effects = selectedAsset.getEffectsWithType(HVEEffect.HVEEffectType.SEGMENTATION);
        if (effects.isEmpty()) {
            initSegmentation(integer);
        } else {
            mEditPreviewViewModel.updateVideoLane();
            if (!isValidActivity()) {
                return;
            }
            runOnUiThread(() -> {
                ToastWrapper.makeText(VideoClipsActivity.this, R.string.segmentation_cancel, Toast.LENGTH_SHORT).show();
            });
            mSegmentationViewModel.removeCurrentEffect();
            if (mEditPreviewViewModel != null) {
                mEditPreviewViewModel.updateVideoLane();
                mEditPreviewViewModel.refreshMenuState();
            }
        }
        mSegmentationViewModel.setSegmentationEnter(-1);
    }

    private void handleBodySeg(HVEAsset selectedAsset, int integer, int segPart) {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "handleBodySeg asset is null!");
            return;
        }
        List<HVEEffect> effects = selectedAsset.getEffectsWithType(HVEEffect.HVEEffectType.BODY_SEG);
        if (effects.isEmpty()) {
            initBodySeg(segPart, integer);
        } else {
            mEditPreviewViewModel.updateVideoLane();
            if (!isValidActivity()) {
                return;
            }
            if (!mBodySegViewModel.removeCurrentEffect()) {
                return;
            }
            runOnUiThread(() -> {
                ToastWrapper.makeText(VideoClipsActivity.this, R.string.body_seg_cancel, Toast.LENGTH_SHORT).show();
            });
            if (mEditPreviewViewModel != null) {
                mEditPreviewViewModel.updateVideoLane();
                mEditPreviewViewModel.refreshMenuState();
            }
        }
        mBodySegViewModel.setBodySegEnter(-1);
    }

    private boolean isContainHumanTrackingEffect(HVEAsset asset) {
        for (HVEEffect effect : asset.getEffects()) {
            if (effect.getEffectType() == HVEEffect.HVEEffectType.HUMAN_TRACKING) {
                return true;
            }
        }
        return false;
    }

    public void detectFace(int operateId) {
        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        if (hveAsset == null) {
            hveAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (mFaceProgressDialog == null) {
            mFaceProgressDialog = new FaceProgressDialog(this, getString(R.string.face_identifying));

            HVEAsset finalHveAsset = hveAsset;
            mFaceProgressDialog.setOnProgressClick(() -> {
                mFaceBlockingViewModel.interruptFaceBlocking(finalHveAsset);
                ToastWrapper.makeText(this, getString(R.string.identify_interrupts), Toast.LENGTH_SHORT).show();
                mFaceProgressDialog = null;
                MenuClickManager.getInstance().popView();
            });

            mFaceProgressDialog.setOnCancelListener(dialog -> {
                mFaceBlockingViewModel.interruptFaceBlocking(finalHveAsset);
                ToastWrapper.makeText(this, getString(R.string.identify_interrupts), Toast.LENGTH_SHORT).show();
                mFaceProgressDialog = null;
            });
        }
        mFaceProgressDialog.show(getWindowManager());
        mFaceProgressDialog.setStopVisble(true);
        mFaceProgressDialog.setCancelable(true);
        mFaceProgressDialog.setProgress(MIN_PROGRESS);

        AtomicBoolean isFirstDownloadModel = new AtomicBoolean(false);
        long start = System.currentTimeMillis();
        if (mEditor == null || mEditPreviewViewModel == null || mFaceBlockingViewModel == null) {
            return;
        }
        if (mEditor.getTimeLine() == null) {
            return;
        }
        if (hveAsset instanceof HVEVisibleAsset) {
            HVEAsset visibleAsset = hveAsset;
            ((HVEVisibleAsset) hveAsset).initFacePrivacyEngine(new HVEAIInitialCallback() {
                @Override
                public void onProgress(int progress) {
                    if (progress < 100) {
                        isFirstDownloadModel.set(true);
                    }
                    if (isFirstDownloadModel.get()) {
                        int pr = (int) (progress * 0.1);
                        if (pr >= MIN_PROGRESS) {
                            runOnUiThread(() -> {
                                if (mFaceProgressDialog != null) {
                                    mFaceProgressDialog.setProgress(pr);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onSuccess() {
                    ((HVEVisibleAsset) visibleAsset).startFacePrivacyDetect(new HVEAIProcessCallback() {
                        @Override
                        public void onProgress(int progress) {
                            int detectProgress;
                            if (isFirstDownloadModel.get()) {
                                detectProgress = (int) (10 + (progress * 0.9));
                            } else {
                                detectProgress = progress;
                            }
                            if (detectProgress >= MIN_PROGRESS) {
                                runOnUiThread(() -> {
                                    if (mFaceProgressDialog != null) {
                                        mFaceProgressDialog.setProgress(detectProgress);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onSuccess() {
                            long end = System.currentTimeMillis();
                            SmartLog.i(TAG, "face block algorithm UI cost time: " + (end - start));
                            if (mFaceBlockingViewModel == null) {
                                return;
                            }
                            mFaceBlockingViewModel
                                    .setFaceBlockingList(((HVEVisibleAsset) visibleAsset).getAIFaceTemplates());
                            runOnUiThread(() -> {
                                if (mFaceProgressDialog != null) {
                                    mFaceProgressDialog.dismiss();
                                }
                            });
                            showFaceBlockingFragment(operateId);
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            if (mEditPreviewViewModel == null || mFaceBlockingViewModel == null) {
                                return;
                            }
                            mEditPreviewViewModel.setFaceDetectError(errorCode);
                            mFaceBlockingViewModel.setFaceBlockingList(null);
                            runOnUiThread(() -> {
                                if (mFaceProgressDialog != null) {
                                    mFaceProgressDialog.dismiss();
                                }
                            });
                        }
                    });
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    if (mEditPreviewViewModel != null) {
                        mEditPreviewViewModel.setFaceDetectError(errorCode);
                    }
                    if (mFaceBlockingViewModel != null) {
                        mFaceBlockingViewModel.setFaceBlockingList(null);
                    }
                    runOnUiThread(() -> {
                        if (mFaceProgressDialog != null) {
                            mFaceProgressDialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    private void initHumanTracking(int operateId, HVEAsset selectedAsset) {
        if (selectedAsset instanceof HVEVideoAsset) {
            runOnUiThread(() -> mVideoClipsPlayFragment.showLoadingView());
            ((HVEVideoAsset) selectedAsset).initHumanTrackingEngine(new HVEAIInitialCallback() {
                @Override
                public void onProgress(int progress) {
                }

                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        mVideoClipsPlayFragment.hideLoadingView();
                        showPersonTrackingFragment(operateId);
                        ToastWrapper
                                .makeText(VideoClipsActivity.this, R.string.click_the_person_to_follow, Toast.LENGTH_SHORT)
                                .show();
                    });
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    SmartLog.e(TAG, errorMessage);
                    mVideoClipsPlayFragment.hideLoadingView();
                    mPersonTrackingViewModel.setTrackingIsReady(false);
                    ToastWrapper.makeText(VideoClipsActivity.this, R.string.result_illegal, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void initSegmentation(Integer integer) {
        if (mVideoClipsPlayFragment != null) {
            mVideoClipsPlayFragment.showLoadingView();
        }
        mSegmentationViewModel.initializeSegmentation(new HVEAIInitialCallback() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoClipsPlayFragment != null) {
                        mVideoClipsPlayFragment.hideLoadingView();
                        showSegmentationFragment(integer);
                        ToastWrapper.makeText(VideoClipsActivity.this, R.string.segmentation_select, Toast.LENGTH_SHORT)
                                .show();
                    }
                    if (mEditor != null) {
                        mEditor.pauseTimeLine();
                    }
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoClipsPlayFragment != null) {
                        mVideoClipsPlayFragment.hideLoadingView();
                    }
                    ToastWrapper.makeText(VideoClipsActivity.this, R.string.result_illegal, Toast.LENGTH_SHORT).show();
                });
            }
        });
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.updateVideoLane();
            mEditPreviewViewModel.refreshMenuState();
        }
    }

    public void initBodySeg(Integer segPart, Integer integer) {
        if (mVideoClipsPlayFragment != null) {
            mVideoClipsPlayFragment.showLoadingView();
        }
        segTime = System.currentTimeMillis();
        mBodySegViewModel.initializeBodySeg(segPart, new HVEAIInitialCallback() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoClipsPlayFragment != null) {
                        mVideoClipsPlayFragment.hideLoadingView();
                    }
                    if (mEditor != null) {
                        mEditor.pauseTimeLine();
                    }
                });
                bodySegDetect(integer);
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoClipsPlayFragment != null) {
                        mVideoClipsPlayFragment.hideLoadingView();
                    }
                    ToastWrapper.makeText(VideoClipsActivity.this, R.string.result_illegal, Toast.LENGTH_SHORT).show();
                });
            }
        });
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.updateVideoLane();
            mEditPreviewViewModel.refreshMenuState();
        }
    }

    public void bodySegDetect(Integer integer) {
        initBodySegProgressDialog(integer);
        String sSuccess = getString(R.string.ai_body_seg_success);
        String sFail = getString(R.string.ai_body_seg_fail);
        switch (integer) {
            case EDIT_VIDEO_STATE_HEAD_SEG:
            case EDIT_VIDEO_OPERATION_HEAD_SEG:
            case EDIT_PIP_OPERATION_HEAD_SEG:
                sSuccess = getString(R.string.ai_head_seg_success);
                sFail = getString(R.string.ai_head_seg_fail);
                break;
            default:
                break;
        }
        String finalSSuccess = sSuccess;
        String finalSFail = sFail;
        mBodySegViewModel.bodySegDetect(new HVEAIProcessCallback() {
            @Override
            public void onProgress(int progress) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mBodySegDialog != null) {
                        if (!mBodySegDialog.isShowing()) {
                            mBodySegDialog.show();
                        }
                        mBodySegDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess() {
                long time = System.currentTimeMillis() - segTime;
                SmartLog.i(TAG, "seg cost time: " + time);

                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mBodySegDialog != null) {
                        mBodySegDialog.updateProgress(0);
                        mBodySegDialog.dismiss();
                    }
                    if (mBodySegViewModel != null) {
                        mBodySegViewModel.releaseBodySegEngine();
                    }
                    if (mEditPreviewViewModel != null) {
                        mEditPreviewViewModel.updateVideoLane();
                        mEditPreviewViewModel.refreshMenuState();
                    }
                    ToastUtils.getInstance().showToast(VideoClipsActivity.this, finalSSuccess, Toast.LENGTH_SHORT);
                });
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(
                        () -> ToastWrapper.makeText(VideoClipsActivity.this, finalSFail, Toast.LENGTH_SHORT).show());
                if (mBodySegDialog != null) {
                    mBodySegDialog.updateProgress(0);
                    mBodySegDialog.dismiss();
                }
            }
        });
    }

    private void initBodySegProgressDialog(int operateId) {
        if (!isValidActivity()) {
            return;
        }
        String sProcess = getString(R.string.ai_body_seg);
        switch (operateId) {
            case EDIT_VIDEO_STATE_HEAD_SEG:
            case EDIT_VIDEO_OPERATION_HEAD_SEG:
            case EDIT_PIP_OPERATION_HEAD_SEG:
                sProcess = getString(R.string.ai_head_seg);
                break;
            default:
                break;
        }
        mBodySegDialog = new CommonProgressDialog(VideoClipsActivity.this, () -> {
            mBodySegViewModel.interruptCurrentEffect();
            ToastWrapper.makeText(VideoClipsActivity.this, getString(R.string.segmentation_cancel), Toast.LENGTH_SHORT)
                    .show();
            if (mBodySegDialog != null && mBodySegDialog.isShowing()) {
                mBodySegDialog.dismiss();
            }
            mBodySegDialog = null;
        });
        mBodySegDialog.setTitleValue(sProcess);
        mBodySegDialog.setCanceledOnTouchOutside(false);
        mBodySegDialog.setCancelable(false);
        mBodySegDialog.show();
    }

    public void segmentationDetect(Integer integer) {
        initSegmentationProgressDialog(integer);

        mSegmentationViewModel.segmentationDetect(new HVEAIProcessCallback() {
            @Override
            public void onProgress(int progress) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mSegmentationDialog != null) {
                        if (!mSegmentationDialog.isShowing()) {
                            mSegmentationDialog.show();
                        }
                        mSegmentationDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mSegmentationDialog != null) {
                        mSegmentationDialog.updateProgress(0);
                        mSegmentationDialog.dismiss();
                    }
                    if (mEditPreviewViewModel != null) {
                        mEditPreviewViewModel.updateVideoLane();
                        mEditPreviewViewModel.refreshMenuState();
                    }
                    ToastWrapper.makeText(VideoClipsActivity.this, R.string.ai_segmentation_success, Toast.LENGTH_SHORT)
                            .show();
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    ToastWrapper.makeText(VideoClipsActivity.this, R.string.ai_segmentation_fail, Toast.LENGTH_SHORT)
                            .show();
                    if (mSegmentationDialog != null) {
                        mSegmentationDialog.updateProgress(0);
                        mSegmentationDialog.dismiss();
                    }
                });
            }
        });
    }

    private void initSegmentationProgressDialog(int operateId) {
        mSegmentationDialog = new CommonProgressDialog(this, () -> {
            mSegmentationViewModel.interruptCurrentEffect();
            switch (operateId) {
                case EDIT_VIDEO_STATE_AI_SEGMENTATION:
                    break;
                case EDIT_VIDEO_OPERATION_AI_SEGMENTATION:
                    break;
                case EDIT_PIP_OPERATION_AI_SEGMENTATION:
                    break;
                default:
                    break;
            }
            ToastWrapper.makeText(VideoClipsActivity.this, getString(R.string.segmentation_cancel), Toast.LENGTH_SHORT)
                    .show();
            mSegmentationDialog.dismiss();
            mSegmentationDialog = null;
        });
        mSegmentationDialog.setTitleValue(getString(R.string.ai_segmentation));
        mSegmentationDialog.setCanceledOnTouchOutside(false);
        mSegmentationDialog.setCancelable(false);
        mSegmentationDialog.show();
    }

    private void showDeleteDialog(int operateId, HVEAsset selectedAsset) {
        HumanTrackingConfirmDialog dialog = new HumanTrackingConfirmDialog(this);
        dialog.show();
        dialog.setOnPositiveClickListener(() -> {
            List<HVEVideoLane> allVideoLane = mEditor.getTimeLine().getAllVideoLane();
            HVEVideoLane videoLane = allVideoLane.get(selectedAsset.getLaneIndex());
            if (videoLane == null) {
                SmartLog.e(TAG, "HumanTracking VideoLane is null!");
                return;
            }
            if (mEditPreviewViewModel == null) {
                SmartLog.e(TAG, "EditPreviewViewModel is null!");
                return;
            }
            if (selectedAsset instanceof HVEVideoAsset) {
                ((HVEVideoAsset) selectedAsset).removeHumanTrackingEffect();
                initHumanTracking(operateId, selectedAsset);
            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public void showInputLayout(boolean isShow) {
        if (!isShow) {
            mTextTemplateEdit.clearFocus();
            mTextTemplateEdit.setFocusable(false);
            mTextTemplateEdit.setFocusableInTouchMode(false);
            mTextTemplateLayout.setVisibility(View.GONE);
            mEditPreviewViewModel.setEditTextTemplateStatus(false);
            if (MenuClickManager.getInstance().getViewStack().isEmpty()) {
                mMaterialEditViewModel.setIsTextTemplateEditState(false);
            }
            hideKeyboard();
            return;
        }
        mTextTemplateLayout.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) mTextTemplateLayout.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.setMargins(0, 0, 0, mSoftKeyboardHeight);
        }
        mTextTemplateEdit.setFocusable(true);
        mTextTemplateEdit.setFocusableInTouchMode(true);

        mTextTemplateEdit.requestFocus();

        try {
            mTextTemplateEdit.setSelection(mTextTemplateEdit.getText().length());
        } catch (RuntimeException e) {
            SmartLog.w(TAG, "showInputLayout setSelection " + e.getMessage());
        }
        mEditPreviewViewModel.setEditTextTemplateStatus(true);
        mEditPreviewViewModel.setNeedAddTextOrSticker(true);
        mMaterialEditViewModel.setIsTextTemplateEditState(true);
    }

    private void advanceExitDialog() {
        if (advanceExitDialog == null) {
            advanceExitDialog = new AdvanceExitDialog(this, new AdvanceExitDialog.OnClickListener() {
                @Override
                public void onSave() {
                    isSaveToApp = true;
                    onBackPressed();
                }

                @Override
                public void onBack() {
                    isSaveToApp = false;
                    finish();
                }
            });
        }
        if (isValidActivity()) {
            advanceExitDialog.show();
        }
    }

    private void initNavBarAnim() {
        mShowAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAnim.setDuration(500);
        mHiddenAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAnim.setDuration(500);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(CURRENT_TIME, mCurrentTime);
        outState.putInt(CLIPS_VIEW_TYPE, VIEW_TYPE);
        outState.putBoolean(EXTRA_FROM_SELF_MODE, true);
        if (mEditor != null) {
            outState.putString(PROJECT_ID, mEditor.getProjectId());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (isSoftKeyboardShow) {
            hideKeyboard();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backAction();
            }
        }, 50);

    }

    private void backAction() {
        mEditPreviewViewModel.setIsFootShow(false);
        mEditPreviewViewModel.setRecordAudio(false);

        if (mMenuFragment.getViewStack() != null && !mMenuFragment.getViewStack().isEmpty()) {
            MenuControlViewRouter.Panel panel = mMenuFragment.getViewStack().lastElement();
            if (panel.object instanceof BaseFragment) {
                if (mEditPreviewViewModel.isAddCoverTextStatus()) {
                    mMenuFragment.popView();
                    mMaterialEditViewModel.refresh();
                    return;
                }

                if (mEditPreviewViewModel.isAddCurveSpeedStatus()) {
                    mEditPreviewViewModel.setAddCurveSpeedStatus(false);
                    mMenuFragment.popView();
                    return;
                }

                if (mEditPreviewViewModel.isNeedAddTextOrSticker()) {
                    mMenuFragment.popView();
                    return;
                }
            }
        }

        if (isFullScreenState) {
            if (Constant.IntentFrom.INTENT_WHERE_FROM == INTENT_FROM_IMAGE_LIB) {
                Constant.IntentFrom.INTENT_WHERE_FROM = 0;
                VideoClipsActivity.super.onBackPressed();
                return;
            }
            mIvBack.setVisibility(View.VISIBLE);
            mSdkPlayViewModel.setFullScreenState(false);
        } else {
            if (!mMenuFragment.popView()) {

                if (!isFromSelf && !isSaveToApp) {
                    advanceExitDialog();
                    return;
                }

                if (isFromSelf || isSaveToApp) {
                    if (EditorManager.getInstance().getEditor() != null) {
                        EditorManager.getInstance().getEditor().saveProject();
                        stopEditor();
                        saveToast();
                    }
                }
                if (!isFromSelf) {
                    Intent intent = new Intent();
                    intent.setClassName(VideoClipsActivity.this, MAIN_ACTIVITY_NAME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                setResult(RESULT_OK);
                finish();
            } else {
                mEditPreviewViewModel.setSelectedUUID("");
            }
        }
    }

    private void stopEditor() {
        if (EditorManager.getInstance().getEditor() != null) {
            EditorManager.getInstance().recyclerEditor();
        }
    }

    private void saveToast() {
        int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
        Toast toast = Toast.makeText(this, getString(R.string.save_toast), Toast.LENGTH_SHORT);
        View view = toast.getView();
        if (view != null) {
            view.setBackgroundColor(Color.TRANSPARENT);
            TextView textView = view.findViewById(tvToastId);
            if (textView != null) {
                textView.setBackground(getDrawable(R.drawable.bg_toast_show));
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.clip_color_E6FFFFFF));
                textView.setPadding(SizeUtils.dp2Px(this, 16), SizeUtils.dp2Px(this, 8), SizeUtils.dp2Px(this, 16),
                        SizeUtils.dp2Px(this, 8));
                toast.setGravity(Gravity.CENTER, 0, -SizeUtils.dp2Px(this, 30));
            }
        }
        toast.show();
    }

    public void addMediaData() {
        Intent intent = new Intent(this, MediaPickActivity.class);
        intent.putParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT,
                (ArrayList<? extends MediaData>) getVideoImageAssets());
        intent.putExtra(MediaPickActivity.ACTION_TYPE, MediaPickActivity.ACTION_APPEND_MEDIA_TYPE);
        startActivityForResult(intent, ACTION_ADD_MEDIA_REQUEST_CODE);
    }

    private String getSourceName(String path) {
        String sourceName = "";
        for (MediaData item : mMediaDataList) {
            if (item.getPath().equals(path)) {
                sourceName = item.getName();
                break;
            }
        }
        return sourceName;
    }

    private List<MediaData> getVideoImageAssets() {
        List<MediaData> list = new ArrayList<>();
        if (mEditPreviewViewModel.getVideoLane() != null) {
            for (HVEAsset asset : mEditPreviewViewModel.getVideoLane().getAssets()) {
                if (!StringUtil.isEmpty(asset.getPath()) && (asset.getType() == HVEAsset.HVEAssetType.VIDEO
                        || asset.getType() == HVEAsset.HVEAssetType.IMAGE)) {
                    MediaData data = new MediaData();
                    data.setPath(asset.getPath());
                    data.setName(getSourceName(asset.getPath()));
                    list.add(data);
                }
            }
        }
        return list;
    }

    public void showFaceBlockingFragment(int operateId) {
        mMenuFragment.showFragment(operateId, FaceBlockingFragment.newInstance(operateId));
    }

    public void showPersonTrackingFragment(int operateId) {
        mMenuFragment.showFragment(operateId, PersonTrackingFragment.newInstance(operateId));
    }

    public void showSegmentationFragment(int operateId) {
        mMenuFragment.showFragment(operateId, SegmentationFragment.newInstance(operateId));
    }

    public void showAssetSplitFragment(int operateId) {
        mMenuFragment.showFragment(operateId, AssetSplitFragment.newInstance(operateId));
    }

    public void showKeyFrameFragment(int operateId) {
        mMenuFragment.showFragment(operateId, KeyFrameFragment.newInstance(operateId));
    }

    public void showAssetCropFragment(int operateId) {
        mMenuFragment.showFragment(operateId, AssetCropFragment.newInstance(operateId));
    }

    public void showCusterSpeedFragment(int position, HVEAsset selectedAsset, List<HVESpeedCurvePoint> temp,
                                        List<HVESpeedCurvePoint> resetList, String curveName) {
        mMenuFragment.showFragment(EDIT_AUDIO_CUSTOM_CURVESPEED,
                CustomCurveSpeedFragment.newInstance(position, selectedAsset, temp, resetList, curveName));
    }

    public void gotoCropVideoActivity() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine == null) {
            return;
        }

        EditorManager.getInstance().getEditor().pauseTimeLine();
        Intent intent = new Intent(this, CropNewActivity.class);
        intent.putExtra(CropNewActivity.EDITOR_UUID, editor.getUuid());

        HVEAsset asset = mEditPreviewViewModel.getSelectedAsset();
        if (asset == null
                || (asset.getType() != HVEAsset.HVEAssetType.VIDEO && asset.getType() != HVEAsset.HVEAssetType.IMAGE)) {
            asset = mEditPreviewViewModel.getMainLaneAsset();
        }

        if (asset == null) {
            return;
        }

        if (mMaterialEditViewModel != null) {
            mMaterialEditViewModel.clearMaterialEditData();
        }

        MediaData mediaData = new MediaData();
        mediaData.setPath(asset.getPath());
        mediaData.setCutTrimIn(asset.getTrimIn());
        mediaData.setCutTrimOut(asset.getTrimOut());
        mediaData.setRotation(((HVEVisibleAsset) asset).getRotation());
        HVECut hveCut = ((HVEVisibleAsset) asset).getHVECut();
        if (hveCut != null) {
            mediaData.setGlLeftBottomX(hveCut.getGlLeftBottomX());
            mediaData.setGlLeftBottomY(hveCut.getGlLeftBottomY());
            mediaData.setGlRightTopX(hveCut.getGlRightTopX());
            mediaData.setGlRightTopY(hveCut.getGlRightTopY());
        }

        String uuid = asset.getUuid();
        intent.putExtra("uuid", uuid);
        intent.putExtra(CropNewActivity.MEDIA_DATA, mediaData);
        intent.putExtra(CropNewActivity.EDITOR_CURRENT_TIME, getVideoClipCurrentTime(asset));
        startActivityForResult(intent, ACTION_CLIP_REQUEST_CODE);
    }

    private long getVideoClipCurrentTime(HVEAsset asset) {
        if (asset == null) {
            return 0;
        }
        long startTime = asset.getStartTime();
        long endTime = asset.getEndTime();
        if (mCurrentTime <= endTime && mCurrentTime >= startTime) {
            return mCurrentTime - startTime;
        } else {
            return 0;
        }
    }

    public void showTimeLapseFragment(int operateId) {
        mMenuFragment.showFragment(operateId, TimeLapseFragment.newInstance(operateId));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            pauseTimeLine();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            SafeIntent safeIntent = new SafeIntent(data);
            if (requestCode == ACTION_ADD_MEDIA_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                ArrayList<MediaData> selectList = safeIntent.getParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT);
                if (selectList != null) {
                    mMenuViewModel.addVideos(selectList, false);
                    mMediaDataList.addAll(selectList);
                }
            }

            if (requestCode == ACTION_ADD_AUDIO_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                AudioData audioData = safeIntent.getParcelableExtra(Constant.EXTRA_AUDIO_SELECT_RESULT);
                if (audioData != null && !StringUtil.isEmpty(audioData.getName())
                        && !StringUtil.isEmpty(audioData.getPath())) {
                    boolean isExtraAudio = safeIntent.getBooleanExtra(Constant.IS_EXTRA_AUDIO, false);
                    mEditPreviewViewModel.addAudio(audioData.getName(), audioData.getPath(), AUDIO_TYPE_MUSIC);
                }
            }

            if (requestCode == ACTION_SPEECH_SYNTHESIS_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                AudioData audioData = safeIntent.getParcelableExtra(Constant.EXTRA_AUDIO_SELECT_RESULT);
                if (audioData != null) {
                    mEditPreviewViewModel.addAudio(audioData.getName(), audioData.getPath(), AUDIO_TYPE_MUSIC);
                }
            }
            if (requestCode == ACTION_ADD_PICTURE_IN_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                MediaData selectList = safeIntent.getParcelableExtra(Constant.EXTRA_SELECT_RESULT);
                if (selectList != null) {

                    HVEAsset asset = mMenuViewModel.addPip(selectList);
                    if (asset != null) {
                        defaultSelect(asset);
                    }
                }
            }

            if (requestCode == ACTION_REPLACE_VIDEO_ASSET && resultCode == Constant.RESULT_CODE) {
                MediaData selectList = safeIntent.getParcelableExtra(Constant.EXTRA_SELECT_RESULT);
                if (selectList != null) {
                    if (mBodySegViewModel != null) {
                        mBodySegViewModel.removeCurrentEffect();
                    }
                    mMenuViewModel.replaceMainLaneAsset(selectList.getPath(), selectList.getCutTrimIn(),
                            selectList.getCutTrimOut());
                }
            }

            if (requestCode == ACTION_PIP_VIDEO_ASSET && resultCode == Constant.RESULT_CODE) {
                MediaData selectList = safeIntent.getParcelableExtra(Constant.EXTRA_SELECT_RESULT);
                if (selectList != null) {
                    mMenuViewModel.replacePipAsset(selectList.getPath(), selectList.getCutTrimIn(),
                            selectList.getCutTrimOut());
                }
            }

            if (requestCode == ACTION_ADD_CANVAS_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                String canvasPath = safeIntent.getStringExtra(Constant.EXTRA_SELECT_RESULT);
                mEditPreviewViewModel.setCanvasImageData(canvasPath);
            }

            if (requestCode == ACTION_ADD_BLOCKING_STICKER_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                String stickerPath = data.getStringExtra(Constant.EXTRA_SELECT_RESULT);
                mEditPreviewViewModel.addBlockingSticker(stickerPath);
            }

            if (requestCode == ACTION_ADD_SELECTION_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                ArrayList<MediaData> selectList = safeIntent.getParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT);
                if (selectList != null) {
                    mMenuViewModel.addVideos(selectList, true);
                    mMediaDataList.addAll(selectList);
                }
            }

            if (requestCode == ACTION_ADD_STICKER_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                String canvasPath = safeIntent.getStringExtra(Constant.EXTRA_SELECT_RESULT);
                mPictureStickerChangeEvent.onStickerPictureChange(canvasPath);
            }

            if (requestCode == ACTION_EXPORT_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                setResult(RESULT_OK, data);
                finish();
            }

            if (requestCode == ACTION_CLIP_REQUEST_CODE) {
                if (mEditPreviewViewModel == null || EditorManager.getInstance().getTimeLine() == null) {
                    return;
                }

                HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
                if (selectedAsset == null) {
                    selectedAsset = mEditPreviewViewModel.getMainLaneAsset();
                }
                if (selectedAsset == null) {
                    return;
                }

                HVEVisibleAsset visibleAsset = (HVEVisibleAsset) selectedAsset;

                MediaData mediaData = safeIntent.getParcelableExtra(CropNewActivity.MEDIA_RTN);
                if (mediaData == null) {
                    SmartLog.e(TAG, "media data is null");
                    return;
                }

                float rotation = mediaData.getRotation();
                HVECut hveCut = mediaData.getCut();
                if (hveCut == null) {
                    SmartLog.e(TAG, "get hve cut from media data failed");
                    return;
                }

                if (!visibleAsset.setHVECut(hveCut, rotation)) {
                    SmartLog.e(TAG, "set hve failed");
                    return;
                }
            }
        }
    }

    public void registerPictureStickerChangeEvent(PictureStickerChangeEvent pictureStickerChangeEvent) {
        mPictureStickerChangeEvent = pictureStickerChangeEvent;
    }

    public interface PictureStickerChangeEvent {
        void onStickerPictureChange(String canvasPath);
    }

    public boolean isMirrorStatus() {
        return mirrorStatus;
    }

    public void setMirrorStatus(boolean mirrorStatus) {
        this.mirrorStatus = mirrorStatus;
    }

    public void seekTimeLine(long duration) {
        SmartLog.d(TAG, "seekTimeLine:" + duration);
        if (duration < lastTimeLineTime) {
            seekHandler.removeCallbacksAndMessages(null);
            lastSeeKTime = System.currentTimeMillis();
            if (EditorManager.getInstance().getTimeLine() == null) {
                return;
            }

            EditorManager.getInstance().getEditor().seekTimeLine(duration, new HuaweiVideoEditor.SeekCallback() {
                @Override
                public void onSeekFinished() {
                    if (EditorManager.getInstance().getTimeLine() == null) {
                        return;
                    }
                    mCurrentTime = EditorManager.getInstance().getTimeLine().getCurrentTime();
                    if (mSdkPlayViewModel == null) {
                        return;
                    }
                    mSdkPlayViewModel.setCurrentTime(duration);
                }
            });
            lastTimeLineTime = duration;
        } else {
            long systemTime = System.currentTimeMillis();
            long delayMillis = 0;
            if (systemTime - lastSeeKTime >= SEEK_INTERVAL) {
                delayMillis = 0;
            } else {
                seekHandler.removeCallbacksAndMessages(null);
                delayMillis = systemTime - lastSeeKTime;
            }
            lastTimeLineTime = duration;
            seekHandler.postDelayed(() -> {
                lastSeeKTime = System.currentTimeMillis();

                if (EditorManager.getInstance().getTimeLine() == null) {
                    return;
                }

                EditorManager.getInstance().getEditor().seekTimeLine(duration, new HuaweiVideoEditor.SeekCallback() {
                    @Override
                    public void onSeekFinished() {
                        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
                        if (timeLine == null || mSdkPlayViewModel == null) {
                            return;
                        }
                        mCurrentTime = timeLine.getCurrentTime();
                        mSdkPlayViewModel.setCurrentTime(duration);
                    }
                });
            }, delayMillis);
        }
    }

    public void pauseTimeLine() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor != null) {
            editor.pauseTimeLine();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (TimeOutOnTouchListener listener : onTouchListeners) {
            if (listener != null) {
                listener.onTouch(ev);
            }
        }
        try {
            if (getWindow().superDispatchTouchEvent(ev)) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            return true;
        }

        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolumeChangeObserver instance = VolumeChangeObserver.getInstace(getApplicationContext());
        instance.unregisterVolumeReceiver();

        if (advanceExitDialog != null) {
            advanceExitDialog.dismiss();
            advanceExitDialog = null;
        }

        if (EditorManager.getInstance().getEditor() != null) {
            EditorManager.getInstance().recyclerEditor();
        }

        ThumbNailMemoryCache.getInstance().recycler();
    }

    private void createTailSource() {
        String dirPath = getFilesDir().toString() + "/tail";
        File dir = new File(dirPath);
        if (!dir.mkdirs()) {
            SmartLog.e(TAG, "fail to make dir ");
        }
        String backPath = dirPath + "/background.png";
        FileOutputStream fOut = null;
        try {
            File tailFile = new File(backPath);
            if (!tailFile.exists()) {
                if (!tailFile.createNewFile()) {
                    SmartLog.e(TAG, "fail to create tail file");
                }
                Bitmap bitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.BLACK);
                fOut = new FileOutputStream(tailFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
            }
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void isShowLockButton(boolean isShow) {
    }

    public void showFaceCompareButton(boolean isShow, View.OnTouchListener onTouchListener) {
        mIvFaceCompare.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (onTouchListener != null) {
            mIvFaceCompare.setOnTouchListener(onTouchListener);
        }
    }

    public boolean isFromSelfMode() {
        return isFromSelf;
    }

    public void initSetCoverData(String projectId, Bitmap bitmap, long time) {
        setBitmapCover(projectId, bitmap, time);
    }

    private void setBitmapCover(String projectId, Bitmap bitmap, long time) {
        if (TextUtils.isEmpty(projectId)) {
            SmartLog.e(TAG, "projectId is empty");
            return;
        }
        new Thread("CoverImageViewModel-Thread-1") {
            @Override
            public void run() {
                super.run();
                try {
                    String path = FileUtil.saveBitmap(getApplication(), projectId, bitmap,
                            System.currentTimeMillis() + "cover.png");
                    HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
                    if (timeLine != null) {
                        timeLine.addCoverImage(path);
                    }
                } catch (Exception e) {
                    SmartLog.e(TAG, e.getMessage());
                }
            }
        }.start();
    }

    private void defaultSelect(HVEAsset asset) {
        if (asset == null) {
            SmartLog.w(TAG, "defaultSelect asset is null");
            return;
        }

        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }

        editor.seekTimeLine(mCurrentTime, () -> mEditPreviewViewModel.setSelectedUUID(asset.getUuid()));
    }

    private void clearTextStyle() {
        SharedPreferencesUtils.getInstance().putIntValue(mContext, SharedPreferencesUtils.TEXT_COLOR_INDEX, -1);
        SharedPreferencesUtils.getInstance().putIntValue(mContext, SharedPreferencesUtils.TEXT_STROKE_INDEX, -1);
        SharedPreferencesUtils.getInstance().putIntValue(mContext, SharedPreferencesUtils.TEXT_SHAWDOW_INDEX, -1);
        SharedPreferencesUtils.getInstance().putIntValue(mContext, SharedPreferencesUtils.TEXT_BACK_INDEX, -1);
    }

    private final ArrayList<TimeOutOnTouchListener> onTouchListeners = new ArrayList<TimeOutOnTouchListener>(10);

    public void registerMyOnTouchListener(TimeOutOnTouchListener onTouchListener) {
        onTouchListeners.add(onTouchListener);
    }

    public void unregisterMyOnTouchListener(TimeOutOnTouchListener onTouchListener) {
        onTouchListeners.remove(onTouchListener);
    }

    public boolean isSoftKeyboardShow() {
        return isSoftKeyboardShow;
    }

    public void setSoftKeyboardShow(boolean softKeyboardShow) {
        isSoftKeyboardShow = softKeyboardShow;
    }

    public interface TimeOutOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }

    private void preDetectAsset(Integer integer) {
        if (integer == -1) {
            return;
        }
        HVEAsset imageAsset;
        if (integer == EDIT_PIP_OPERATION_TIME_LAPSE) {
            imageAsset = mEditPreviewViewModel.getSelectedAsset();
        } else {
            imageAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (imageAsset != null) {
            mTimeLapseViewModel.setSelectedAsset(imageAsset);
        }
        if (imageAsset == null) {
            SmartLog.e(TAG, "TimeLapse asset is null!");
            return;
        }
        if (imageAsset instanceof HVEImageAsset) {
            boolean isContainTimeLapseEffect = false;
            for (HVEEffect effect : imageAsset.getEffects()) {
                if (effect.getEffectType() == HVEEffect.HVEEffectType.TIMELAPSE) {
                    isContainTimeLapseEffect = true;
                }
            }
            SmartLog.i(TAG, "isContainEffect:" + isContainTimeLapseEffect);
            if (!isContainTimeLapseEffect) {
                initTimeLapse(integer);
            } else {
                showDeleteConfirmDialog(integer, (HVEImageAsset) imageAsset);
            }
        } else {
            ToastUtils.getInstance()
                    .showToast(this, getString(R.string.time_lapse_resource_not_support), Toast.LENGTH_SHORT);
        }
    }

    private void showDeleteConfirmDialog(int operateId, HVEImageAsset imageAsset) {
        TimeLapseConfirmDialog lapseConfirmDialog = new TimeLapseConfirmDialog(this);
        lapseConfirmDialog.show();
        lapseConfirmDialog.setOnPositiveClickListener(() -> {
            imageAsset.removeTimeLapseEffect();
            initTimeLapse(operateId);
        });
    }

    private void initTimeLapse(int operateId) {
        if (!isValidActivity()) {
            return;
        }
        runOnUiThread(() -> {
            if (mVideoClipsPlayFragment != null) {
                mVideoClipsPlayFragment.showLoadingView();
            }
        });
        mTimeLapseViewModel.initTimeLapse(new HVEAIInitialCallback() {
            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoClipsPlayFragment != null) {
                        mVideoClipsPlayFragment.hideLoadingView();
                    }
                    mTimeLapseViewModel.firstDetectTimeLapse((result) -> {
                        runOnUiThread(() -> {
                            if (STATE_NO_SKY_WATER == result || STATE_ERROR == result) {
                                ToastWrapper.makeText(VideoClipsActivity.this, getString(R.string.time_lapse_exception))
                                        .show();
                                mTimeLapseViewModel.stopTimeLapse();
                            } else {
                                mTimeLapseViewModel.setTimeLapseResult(result);
                                showTimeLapseFragment(operateId);
                            }
                        });
                    });
                });
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                SmartLog.e(TAG, errorMsg);
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoClipsPlayFragment != null) {
                        mVideoClipsPlayFragment.hideLoadingView();
                    }
                    ToastWrapper.makeText(VideoClipsActivity.this, R.string.result_illegal, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void initVideoSelection(int operateId) {
        if (!isValidActivity()) {
            return;
        }
        runOnUiThread(() -> {
            if (mVideoClipsPlayFragment != null) {
                mVideoClipsPlayFragment.showLoadingView();
            }
        });
        mMenuViewModel.initVideoSelection(new HVEAIInitialCallback() {
            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoClipsPlayFragment != null) {
                        mVideoClipsPlayFragment.hideLoadingView();
                    }
                    Intent intent = new Intent(VideoClipsActivity.this, MediaPickActivity.class);
                    intent.putExtra(MediaPickActivity.ACTION_TYPE, MediaPickActivity.ACTION_AUTO_VIDEO_SELECTION_TYPE);
                    startActivityForResult(intent, VideoClipsActivity.ACTION_ADD_SELECTION_REQUEST_CODE);
                });
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                SmartLog.e(TAG, errorMsg);
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoClipsPlayFragment != null) {
                        mVideoClipsPlayFragment.hideLoadingView();
                    }
                    ToastWrapper.makeText(VideoClipsActivity.this, R.string.result_illegal, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
