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

package com.huawei.hms.videoeditor;

import static com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse.AITimeLapseViewModel.STATE_EDIT;
import static com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse.AITimeLapseViewModel.STATE_ERROR;
import static com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse.AITimeLapseViewModel.STATE_NO_SKY_WATER;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.ai.HVEAIApplication;
import com.huawei.hms.videoeditor.ai.HVEAIColor;
import com.huawei.hms.videoeditor.ai.HVEAIFaceReenact;
import com.huawei.hms.videoeditor.ai.HVEAIFaceSmile;
import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.ai.HVEAIObjectSeg;
import com.huawei.hms.videoeditor.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.ai.HVEAIVideoSelection;
import com.huawei.hms.videoeditor.ai.HVETimeLapseDetectCallback;
import com.huawei.hms.videoeditor.ai.util.HVEUtil;
import com.huawei.hms.videoeditor.sdk.MediaApplication;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.adapter.AIListAdapter;
import com.huawei.hms.videoeditor.ui.common.bean.AIInfoData;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.utils.ArrayUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonProgressDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.ObjectSegActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.ViewFileActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse.AITimeLapseFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse.AITimeLapseViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuConfig;
import com.huawei.hms.videoeditor.ui.mediaexport.utils.InfoStateUtil;
import com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity;
import com.huawei.hms.videoeditor.utils.SmartLog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class HomeActivity extends BaseActivity {
    public static final String TAG = "HomeActivity";

    private final String[] mPermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE};

    private static final int MEDIA_TYPE_VIDEO = 0;

    private static final int MEDIA_TYPE_PHOTO = 1;

    private static final int MEDIA_TYPE_VIDEO_OR_PHOTO = 2;

    private static final int REQUEST_CODE_OF_FACE_REENACT = 0x1001;

    private static final int REQUEST_CODE_OF_FACE_SMILE = 0x1002;

    private static final int REQUEST_CODE_OF_AI_COLOR = 0x1003;

    private static final int REQUEST_CODE_OF_TIME_LAPSE = 0x1004;

    private static final int REQUEST_CODE_OF_VIDEO_SELECTION = 0x1005;

    private static final int REQUEST_CODE_OF_OBJECT_SEGMENTATION = 0x1006;

    private CommonProgressDialog mVideoSelectionDialog;

    private CommonProgressDialog mTimeLapseDialog;

    private CommonProgressDialog mObjectSegDialog;

    private CommonProgressDialog mFaceReenactDialog;

    private CommonProgressDialog mFaceSmileDialog;

    private CommonProgressDialog mAIColorDialog;

    private AITimeLapseViewModel mTimeLapseViewModel;

    private String mFilePath = "";

    private HVEAIFaceReenact mFaceReenact;

    private HVEAIFaceSmile mFaceSmile;

    private HVEAIColor mAIColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        statusBarColor = R.color.home_color_FF181818;
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        InfoStateUtil.getInstance().checkInfoState(this);
        VideoEditorApplication.getInstance().setContext(this);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_home);
        MenuConfig.getInstance().initMenuConfig(this);
        initView();
        initAdapter();
        initData();
    }

    private void initView() {
        ImageView ivBanner = findViewById(R.id.iv_banner);
        ViewGroup.LayoutParams layoutParams = ivBanner.getLayoutParams();
        int width = ScreenUtil.getScreenWidth(this) - ScreenUtil.dp2px(32);
        int height = width / 2;
        layoutParams.width = width;
        layoutParams.height = height;

        findViewById(R.id.fl_video_editor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeLapseViewModel.getTimeLapseStatus() == STATE_EDIT) {
                    return;
                }
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("fromHome", true);
                startActivity(intent);
            }
        });
        mTimeLapseViewModel = new ViewModelProvider(this, factory).get(AITimeLapseViewModel.class);

        mTimeLapseViewModel.getTimeLapseStart().observe(this, integer -> {
            if (integer == -1) {
                return;
            }
            mTimeLapseDialog = new CommonProgressDialog(HomeActivity.this, () -> {
                if (!isValidActivity()) {
                    SmartLog.e(TAG, "Activity is valid!");
                    return;
                }
                ToastWrapper.makeText(HomeActivity.this, getString(R.string.time_lapse_cancel)).show();
                new Thread(() -> mTimeLapseViewModel.stopTimeLapse()).start();
                mTimeLapseDialog.dismiss();
                mTimeLapseDialog = null;
            });
            mTimeLapseDialog.setTitleValue(getString(R.string.time_lapse_process));
            mTimeLapseDialog.setCanceledOnTouchOutside(false);
            mTimeLapseDialog.setCancelable(false);
            mTimeLapseDialog.show();
            mTimeLapseViewModel.processTimeLapse(mFilePath, mTimeLapseViewModel.getSkyRiverType(),
                (float) mTimeLapseViewModel.getSpeedSky() / 3, mTimeLapseViewModel.getScaleSky(),
                (float) mTimeLapseViewModel.getSpeedRiver() / 3, mTimeLapseViewModel.getScaleRiver(),
                new HVEAIProcessCallback<String>() {
                    @Override
                    public void onProgress(int progress) {
                        if (!isValidActivity()) {
                            return;
                        }
                        runOnUiThread(() -> {
                            if (mTimeLapseDialog != null) {
                                if (!mTimeLapseDialog.isShowing()) {
                                    mTimeLapseDialog.show();
                                }
                                mTimeLapseDialog.updateProgress(progress);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (!isValidActivity()) {
                            return;
                        }
                        runOnUiThread(() -> {
                            if (mTimeLapseDialog != null) {
                                mTimeLapseDialog.updateProgress(0);
                                mTimeLapseDialog.dismiss();
                            }
                            ViewFileActivity.startActivity(HomeActivity.this, result, true);
                            new Handler(getMainLooper()).postDelayed(() -> {
                                mTimeLapseViewModel.stopTimeLapse();
                                mTimeLapseViewModel.releaseEngine();
                            }, 100);
                        });
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        if (!isValidActivity()) {
                            return;
                        }
                        runOnUiThread(() -> {
                            ToastWrapper.makeText(HomeActivity.this, getString(R.string.ai_exception)).show();
                        });
                        mTimeLapseViewModel.stopTimeLapse();
                        mTimeLapseViewModel.releaseEngine();
                    }
                });
            mTimeLapseViewModel.setTimeLapseStart(-1);
        });
    }

    private void initAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        List<AIInfoData> infoDataList = new ArrayList<>();
        infoDataList.add(new AIInfoData(getString(R.string.motion_photo), getString(R.string.motion_photo_subtitle),
            R.drawable.ic_motion_photo));
        infoDataList.add(new AIInfoData(getString(R.string.face_smile), getString(R.string.face_smile_subtitle),
            R.drawable.ic_face_smile));
        infoDataList.add(new AIInfoData(getString(R.string.ai_color), getString(R.string.ai_color_subtitle),
            R.drawable.ic_ai_color));
        infoDataList.add(new AIInfoData(getString(R.string.cut_second_menu_time_lapse),
            getString(R.string.time_lapse_subtitle), R.drawable.ic_time_lapse));
        infoDataList.add(new AIInfoData(getString(R.string.cut_second_menu_video_selection),
            getString(R.string.video_selection_subtitle), R.drawable.ic_video_selection));
        infoDataList.add(new AIInfoData(getString(R.string.cut_second_menu_segmentation),
            getString(R.string.object_segmentation_subtitle), R.drawable.edit_menu_segmentation));
        AIListAdapter listAdapter = new AIListAdapter(infoDataList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setItemAnimator(null);
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(this, 12f),
                SizeUtils.dp2Px(this, 12f), ContextCompat.getColor(this, R.color.black)));
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(new AIListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AIInfoData infoData, int position) {
                if (mTimeLapseViewModel.getTimeLapseStatus() == STATE_EDIT) {
                    return;
                }
                String title = infoData.getTitle();
                if (title.equals(getString(R.string.motion_photo))) {
                    MediaPickActivity.startActivityForResult(HomeActivity.this, MEDIA_TYPE_PHOTO,
                        REQUEST_CODE_OF_FACE_REENACT);
                } else if (title.equals(getString(R.string.face_smile))) {
                    MediaPickActivity.startActivityForResult(HomeActivity.this, MEDIA_TYPE_PHOTO,
                        REQUEST_CODE_OF_FACE_SMILE);
                } else if (title.equals(getString(R.string.ai_color))) {
                    MediaPickActivity.startActivityForResult(HomeActivity.this, MEDIA_TYPE_VIDEO_OR_PHOTO,
                        REQUEST_CODE_OF_AI_COLOR);
                } else if (title.equals(getString(R.string.cut_second_menu_time_lapse))) {
                    MediaPickActivity.startActivityForResult(HomeActivity.this, MEDIA_TYPE_PHOTO,
                        REQUEST_CODE_OF_TIME_LAPSE);
                } else if (title.equals(getString(R.string.cut_second_menu_video_selection))) {
                    MediaPickActivity.startActivityForResult(HomeActivity.this, MEDIA_TYPE_VIDEO,
                        REQUEST_CODE_OF_VIDEO_SELECTION);
                } else if (title.equals(getString(R.string.cut_second_menu_segmentation))) {
                    MediaPickActivity.startActivityForResult(HomeActivity.this, MEDIA_TYPE_PHOTO,
                        REQUEST_CODE_OF_OBJECT_SEGMENTATION);
                }
            }
        });
    }

    private void initData() {
        HVEAIApplication.getInstance().setApiKey("please set your apikey");

        UUID uuid = UUID.randomUUID();
        MediaApplication.getInstance().setLicenseId(uuid.toString());
    }

    private void verifyStoragePermissions(HomeActivity activity) {
        final int requestCode = 1;
        try {
            for (int i = 0; i < mPermissions.length; i++) {
                String permisson = mPermissions[i];
                int permissionRead = ActivityCompat.checkSelfPermission(activity, permisson);
                if (permissionRead != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, mPermissions, requestCode);
                }
            }
        } catch (Exception e) {
            SmartLog.e("MainActivity", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != RESULT_OK) {
            return;
        }
        SafeIntent safeIntent = new SafeIntent(data);
        ArrayList<MediaData> selectList = safeIntent.getParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT);
        if (!ArrayUtils.isEmpty(selectList)) {
            mFilePath = selectList.get(0).getPath();
            switch (requestCode) {
                case REQUEST_CODE_OF_VIDEO_SELECTION:
                    videoSelection(mFilePath);
                    break;
                case REQUEST_CODE_OF_TIME_LAPSE:
                    timeLapse(mFilePath);
                    break;
                case REQUEST_CODE_OF_FACE_REENACT:
                    faceReenact(mFilePath);
                    break;
                case REQUEST_CODE_OF_FACE_SMILE:
                    faceSmile(mFilePath);
                    break;
                case REQUEST_CODE_OF_AI_COLOR:
                    aiColor(mFilePath);
                    break;
                case REQUEST_CODE_OF_OBJECT_SEGMENTATION:
                    objectSeg(mFilePath);
                    break;
                default:
                    break;
            }
        }
    }

    private void videoSelection(String videoPath) {
        initVideoSelectionProgressDialog();
        HVEAIVideoSelection videoSelection = new HVEAIVideoSelection();
        videoSelection.initEngine(new HVEAIInitialCallback() {
            @Override
            public void onProgress(int progress) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mVideoSelectionDialog != null) {
                        if (!mVideoSelectionDialog.isShowing()) {
                            mVideoSelectionDialog.show();
                        }
                        mVideoSelectionDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                videoSelection.process(videoPath, 3000, new HVEAIProcessCallback<Long>() {
                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onSuccess(Long result) {
                        runOnUiThread(() -> {
                            if (mVideoSelectionDialog != null) {
                                mVideoSelectionDialog.updateProgress(0);
                                mVideoSelectionDialog.dismiss();
                            }
                            String startPoint = String.format(Locale.ROOT,
                                getResources().getString(R.string.highlights_start_point), String.valueOf(result));
                            ToastWrapper.makeText(HomeActivity.this, startPoint, Toast.LENGTH_SHORT).show();
                            ViewFileActivity.startActivity(HomeActivity.this, videoPath, result, 3000);
                        });
                        videoSelection.releaseEngine();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        SmartLog.e(TAG, errorMessage);
                        if (!isValidActivity()) {
                            return;
                        }
                        runOnUiThread(() -> {
                            if (mVideoSelectionDialog != null) {
                                mVideoSelectionDialog.updateProgress(0);
                                mVideoSelectionDialog.dismiss();
                            }
                            ToastWrapper.makeText(HomeActivity.this, getString(R.string.ai_exception)).show();
                        });
                    }
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.e(TAG, errorMessage);
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mTimeLapseDialog != null) {
                        mTimeLapseDialog.updateProgress(0);
                        mTimeLapseDialog.dismiss();
                    }
                    ToastWrapper.makeText(HomeActivity.this, getString(R.string.ai_exception)).show();
                });
            }
        });
    }

    private void initVideoSelectionProgressDialog() {
        mVideoSelectionDialog = new CommonProgressDialog(this, () -> {
            mVideoSelectionDialog.dismiss();
            mVideoSelectionDialog = null;
        });
        mVideoSelectionDialog.setTitleValue(getString(R.string.intelligent_processing));
        mVideoSelectionDialog.setCanceledOnTouchOutside(false);
        mVideoSelectionDialog.setCancelable(false);
        mVideoSelectionDialog.show();
    }

    private void timeLapse(String imagePath) {
        initTimeLapseProgressDialog();
        if (mTimeLapseViewModel != null) {
            mTimeLapseViewModel.initTimeLapse(new HVEAIInitialCallback() {
                @Override
                public void onProgress(int progress) {
                    if (!isValidActivity()) {
                        return;
                    }
                    runOnUiThread(() -> {
                        if (mTimeLapseDialog != null) {
                            if (!mTimeLapseDialog.isShowing()) {
                                mTimeLapseDialog.show();
                            }
                            mTimeLapseDialog.updateProgress(progress);
                        }
                    });
                }

                @Override
                public void onSuccess() {
                    if (!isValidActivity()) {
                        return;
                    }
                    runOnUiThread(() -> {
                        if (mTimeLapseDialog != null) {
                            mTimeLapseDialog.updateProgress(0);
                            mTimeLapseDialog.dismiss();
                        }
                    });
                    if (mTimeLapseViewModel != null) {
                        mTimeLapseViewModel.firstDetectTimeLapse(imagePath, new HVETimeLapseDetectCallback() {
                            @Override
                            public void onResult(int state) {
                                runOnUiThread(() -> {
                                    if (STATE_NO_SKY_WATER == state || STATE_ERROR == state) {
                                        ToastWrapper
                                            .makeText(HomeActivity.this, getString(R.string.time_lapse_exception))
                                            .show();
                                        mTimeLapseViewModel.stopTimeLapse();
                                        mTimeLapseViewModel.releaseEngine();
                                    } else {
                                        mTimeLapseViewModel.setTimeLapseResult(state);
                                        getSupportFragmentManager().beginTransaction()
                                            .add(R.id.fragment_container, AITimeLapseFragment.newInstance(state))
                                            .commitAllowingStateLoss();
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    SmartLog.e(TAG, errorMessage);
                    if (!isValidActivity()) {
                        return;
                    }
                    runOnUiThread(() -> {
                        if (mTimeLapseDialog != null) {
                            mTimeLapseDialog.updateProgress(0);
                            mTimeLapseDialog.dismiss();
                        }
                        ToastWrapper.makeText(HomeActivity.this, getString(R.string.ai_exception)).show();
                    });
                }
            });
        }
    }

    private void initTimeLapseProgressDialog() {
        mTimeLapseDialog = new CommonProgressDialog(this, () -> {
            mTimeLapseDialog.dismiss();
            mTimeLapseDialog = null;
        });
        mTimeLapseDialog.setTitleValue(getString(R.string.intelligent_processing));
        mTimeLapseDialog.setCanceledOnTouchOutside(false);
        mTimeLapseDialog.setCancelable(false);
        mTimeLapseDialog.show();
    }

    private void objectSeg(String photoPath) {
        initObjectSegmentationProgressDialog();
        HVEAIObjectSeg objectSeg = new HVEAIObjectSeg();
        objectSeg.initEngine(new HVEAIInitialCallback() {
            @Override
            public void onProgress(int progress) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mObjectSegDialog != null) {
                        if (!mObjectSegDialog.isShowing()) {
                            mObjectSegDialog.show();
                        }
                        mObjectSegDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mObjectSegDialog != null) {
                        mObjectSegDialog.updateProgress(0);
                        mObjectSegDialog.dismiss();
                    }
                    ObjectSegActivity.startActivity(HomeActivity.this, photoPath);
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.e(TAG, errorMessage);
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mObjectSegDialog != null) {
                        mObjectSegDialog.updateProgress(0);
                        mObjectSegDialog.dismiss();
                    }
                    ToastWrapper.makeText(HomeActivity.this, getString(R.string.ai_exception)).show();
                });
            }
        });
    }

    private void initObjectSegmentationProgressDialog() {
        mObjectSegDialog = new CommonProgressDialog(this, () -> {
            mObjectSegDialog.dismiss();
            mObjectSegDialog = null;
        });
        mObjectSegDialog.setTitleValue(getString(R.string.intelligent_processing));
        mObjectSegDialog.setCanceledOnTouchOutside(false);
        mObjectSegDialog.setCancelable(false);
        mObjectSegDialog.show();
    }

    private void faceReenact(String imagePath) {
        initFaceReenactProgressDialog();
        mFaceReenact = new HVEAIFaceReenact();
        mFaceReenact.process(imagePath, new HVEAIProcessCallback<String>() {
            @Override
            public void onProgress(int progress) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mFaceReenactDialog != null) {
                        if (!mFaceReenactDialog.isShowing()) {
                            mFaceReenactDialog.show();
                        }
                        mFaceReenactDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess(String result) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mFaceReenactDialog != null) {
                        mFaceReenactDialog.updateProgress(0);
                        mFaceReenactDialog.dismiss();
                    }
                    ViewFileActivity.startActivity(HomeActivity.this, result, true);
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.e(TAG, errorMessage);
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mFaceReenactDialog != null) {
                        mFaceReenactDialog.updateProgress(0);
                        mFaceReenactDialog.dismiss();
                    }
                    ToastWrapper.makeText(HomeActivity.this, getString(R.string.ai_exception)).show();
                });
            }
        });
    }

    private void initFaceReenactProgressDialog() {
        mFaceReenactDialog = new CommonProgressDialog(this, () -> {
            mFaceReenactDialog.dismiss();
            mFaceReenactDialog = null;
            if (mFaceReenact != null) {
                mFaceReenact.interruptProcess();
            }
        });
        mFaceReenactDialog.setTitleValue(getString(R.string.intelligent_processing));
        mFaceReenactDialog.setCanceledOnTouchOutside(false);
        mFaceReenactDialog.setCancelable(false);
        mFaceReenactDialog.show();
    }

    private void faceSmile(String imagePath) {
        initFaceSmileProgressDialog();
        mFaceSmile = new HVEAIFaceSmile();
        mFaceSmile.process(imagePath, new HVEAIProcessCallback<String>() {
            @Override
            public void onProgress(int progress) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mFaceSmileDialog != null) {
                        if (!mFaceSmileDialog.isShowing()) {
                            mFaceSmileDialog.show();
                        }
                        mFaceSmileDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess(String result) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mFaceSmileDialog != null) {
                        mFaceSmileDialog.updateProgress(0);
                        mFaceSmileDialog.dismiss();
                    }
                    ViewFileActivity.startActivity(HomeActivity.this, result, false);
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.e(TAG, errorMessage);
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mFaceSmileDialog != null) {
                        mFaceSmileDialog.updateProgress(0);
                        mFaceSmileDialog.dismiss();
                    }
                    ToastWrapper.makeText(HomeActivity.this, getString(R.string.ai_exception)).show();
                });
            }
        });
    }

    private void initFaceSmileProgressDialog() {
        mFaceSmileDialog = new CommonProgressDialog(this, () -> {
            mFaceSmileDialog.dismiss();
            mFaceSmileDialog = null;
            if (mFaceSmile != null) {
                mFaceSmile.interruptProcess();
            }
        });
        mFaceSmileDialog.setTitleValue(getString(R.string.intelligent_processing));
        mFaceSmileDialog.setCanceledOnTouchOutside(false);
        mFaceSmileDialog.setCancelable(false);
        mFaceSmileDialog.show();
    }

    private void aiColor(String filePath) {
        initAIColorProgressDialog();
        mAIColor = new HVEAIColor();
        mAIColor.process(filePath, new HVEAIProcessCallback<String>() {
            @Override
            public void onProgress(int progress) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mAIColorDialog != null) {
                        if (!mAIColorDialog.isShowing()) {
                            mAIColorDialog.show();
                        }
                        mAIColorDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess(String result) {
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mAIColorDialog != null) {
                        mAIColorDialog.updateProgress(0);
                        mAIColorDialog.dismiss();
                    }
                    boolean isVideo = HVEUtil.isVideo(filePath);
                    ViewFileActivity.startActivity(HomeActivity.this, result, isVideo);
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.e(TAG, errorMessage);
                if (!isValidActivity()) {
                    return;
                }
                runOnUiThread(() -> {
                    if (mAIColorDialog != null) {
                        mAIColorDialog.updateProgress(0);
                        mAIColorDialog.dismiss();
                    }
                    ToastWrapper.makeText(HomeActivity.this, getString(R.string.ai_exception)).show();
                });
            }
        });
    }

    private void initAIColorProgressDialog() {
        mAIColorDialog = new CommonProgressDialog(this, () -> {
            mAIColorDialog.dismiss();
            mAIColorDialog = null;
            if (mAIColor != null) {
                mAIColor.interruptProcess();
            }
        });
        mAIColorDialog.setTitleValue(getString(R.string.intelligent_processing));
        mAIColorDialog.setCanceledOnTouchOutside(false);
        mAIColorDialog.setCancelable(false);
        mAIColorDialog.show();
    }
}