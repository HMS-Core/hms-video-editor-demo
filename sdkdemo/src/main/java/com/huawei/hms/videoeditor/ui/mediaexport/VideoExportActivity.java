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

package com.huawei.hms.videoeditor.ui.mediaexport;

import static com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity.SOURCE;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.common.store.sp.SPManager;
import com.huawei.hms.videoeditor.common.utils.BitmapDecodeUtils;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.ProgressView;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonBottomDialog;
import com.huawei.hms.videoeditor.ui.common.view.navigator.FixFragmentNavigator;
import com.huawei.secure.android.common.intent.SafeIntent;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.NavHostFragment;

public class VideoExportActivity extends BaseActivity
    implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener {

    private static final String TAG = "VideoExportActivity";

    private static final String SHORTCUT_IS_FIRST = "SHORTCUT_IS_FIRST";

    private static final String SHORTCUT_IS_FIRST_KEY = "SHORTCUT_IS_FIRST_KEY";

    public static final String SOURCE_HIMOVIE_LOCALVIDEO = "Himovie_LocalVideo";

    public static final int NORMAL_EXPORT_TYPE = 100;

    public static final String EXPORT_TYPE_TAG = "exportType";

    public static final String TEMPLATE_ID = "templateId";

    public static final String COVER_URL = "coverUrl";

    public static final String NAME = "name";

    public static final String DESCRIPTION = "description";

    public static final String EDITOR_UUID = "editor_uuid";

    private String destFileDir;

    private String destFileName;

    private TextureView mTextureView;

    private MediaPlayer mediaPlayer;

    private ImageView ivPic;

    private ImageView back;

    private ProgressView mProgressbar;

    private TextView tvFlag;

    private ImageView ivFlag;

    private TextView tvFlagTips;

    private TextView tvProgressPrompt;

    private TextView tvProgress;

    private LinearLayout llProgress;

    private Button btExportSuccess;

    private static int exportType;

    private static long time;

    private static String coverUrl;

    private static String name;

    private static String exportVideoPath;

    private static Uri mediaJumpUri;

    private int videoWidth;

    private int videoHeight;

    private float mTextureViewWidth;

    private float mTextureViewHeight;

    private static View view;

    private boolean isStartExport = false;

    private int width;

    private int height;

    private ConstraintLayout clExportShortcut;

    private CheckBox cbExportShortcut;

    private HuaweiVideoEditor editor;

    private Handler handler = new Handler();

    public void setStartExport(boolean startExport) {
        isStartExport = startExport;
    }

    public HuaweiVideoEditor getEditor() {
        return editor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.export_bg;
        super.onCreate(savedInstanceState);
        setTime(System.currentTimeMillis());
        SafeIntent intent = new SafeIntent(getIntent());
        setExportType(intent.getIntExtra(EXPORT_TYPE_TAG, NORMAL_EXPORT_TYPE));
        setCoverUrl(intent.getStringExtra(COVER_URL));
        setName(intent.getStringExtra(NAME));

        setContentView(R.layout.activity_video_export);
        initObject();

        ivPic = findViewById(R.id.pic);
        back = findViewById(R.id.iv_back);
        mProgressbar = findViewById(R.id.progressbar);
        tvProgressPrompt = findViewById(R.id.tv_progressbar_prompt);
        tvProgressPrompt.setText(
            String.format(Locale.ROOT, getResources().getString(R.string.export_progressing), String.valueOf(0)));
        tvProgress = findViewById(R.id.tv_progressbar);
        tvProgress.setText(String.valueOf(0));
        llProgress = findViewById(R.id.ll_progressbar);
        tvFlag = findViewById(R.id.tv_flag);
        ivFlag = findViewById(R.id.iv_flag);
        tvFlagTips = findViewById(R.id.tv_flag_tips);
        btExportSuccess = findViewById(R.id.bt_export_success);
        clExportShortcut = findViewById(R.id.cl_export_shortcut);
        cbExportShortcut = findViewById(R.id.cb_export_shortcut);

        initTextureView();
        initPlayer();

        CommonBottomDialog dialog = new CommonBottomDialog(this);
        clExportShortcut.setOnClickListener(new OnClickRepeatedListener(v -> {
            boolean isChecked = cbExportShortcut.isChecked();
            cbExportShortcut.setChecked(!isChecked);
        }));

        dialog.setOnDialogClickLister(new CommonBottomDialog.OnDialogClickLister() {
            @Override
            public void onCancelClick() {
                SPManager.get(SHORTCUT_IS_FIRST).put(SHORTCUT_IS_FIRST_KEY, false);
                backHomePage();
            }

            @Override
            public void onAllowClick() {
                SPManager.get(SHORTCUT_IS_FIRST).put(SHORTCUT_IS_FIRST_KEY, false);
                handler.postDelayed(() -> backHomePage(), 1000);
            }
        });

        btExportSuccess.setOnClickListener(v -> {
            this.setResult(RESULT_OK);
            switch (getExportType()) {
                case NORMAL_EXPORT_TYPE:
                    backHomePage();
                    break;
                default:
                    backHomePage();
                    break;
            }
        });

        String editorUuid = intent.getStringExtra(EDITOR_UUID);
        if (TextUtils.isEmpty(editorUuid)) {
            SmartLog.e(TAG, "editorUuid is null");
            return;
        }

        editor = HuaweiVideoEditor.getInstance(editorUuid);
        if (editor != null) {
            if (!TextUtils.isEmpty(coverUrl)) {
                Bitmap bitmap = BitmapDecodeUtils.decodeFile(coverUrl);
                if (bitmap != null) {
                    width = bitmap.getWidth();
                    height = bitmap.getHeight();
                }
                ivPic.setImageBitmap(bitmap);
            }
        }
        back.setOnClickListener(onClickRepeatedListener);
        forceStop = false;
    }

    private ContentValues generateSaveValue(File file) throws IOException {
        ContentValues values = new ContentValues(10);

        String fileName = file.getName();
        String[] titleAndSubFix = fileName.split("\\.");
        if (titleAndSubFix.length > 0) {
            values.put(MediaStore.Video.Media.TITLE, titleAndSubFix[0]);
        }
        values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Video.Media.DATA, file.getCanonicalPath());

        long curTime = System.currentTimeMillis();
        values.put(MediaStore.Video.Media.DATE_TAKEN, curTime);
        values.put(MediaStore.Video.Media.DATE_MODIFIED, curTime / 1000);

        values.put(MediaStore.Video.Media.RESOLUTION, videoWidth + "x" + videoHeight);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        if (editor != null && editor.getTimeLine() != null) {
            values.put(MediaStore.Video.Media.DURATION, editor.getTimeLine().getDuration());
        }
        values.put(MediaStore.Video.Media.SIZE, file.length());

        return values;
    }

    private void judgeGoToPhotoBrowser(File exportFile, Uri uri) {
        SafeIntent safeIntent = new SafeIntent(getIntent());
        String source = safeIntent.getStringExtra(SOURCE);
        if (SOURCE_HIMOVIE_LOCALVIDEO.equals(source)) {
            return;
        }
        if (uri != null) {
            FileUtil.goToPhotoBrowser(this, uri);
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(exportFile));
            this.sendBroadcast(intent);
        }
    }

    private void initJumpUri(Uri uri) {
        if (uri != null) {
            mediaJumpUri = uri;
            return;
        }
        try {
            mediaJumpUri = FileUtil.insert(this.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                generateSaveValue(new File(exportVideoPath)));
        } catch (IOException ex) {
            SmartLog.e(TAG, "saveSuccess: IOException");
            mediaJumpUri = null;
        }
    }

    private void backHomePage() {
        judgeGoToPhotoBrowser(new File(exportVideoPath), mediaJumpUri);
        Intent intent = new Intent();
        setResult(Constant.RESULT_CODE, intent);
        finish();
    }

    OnClickRepeatedListener onClickRepeatedListener = new OnClickRepeatedListener(view -> {
        if (isStartExport) {
            showExportStopDialog();
        } else {
            closeActivity();
        }
    });

    /**
     * 显示停止导出弹框
     */
    private void showExportStopDialog() {
        CommonBottomDialog dialog = new CommonBottomDialog(this);
        dialog.show(getString(R.string.video_edit_export_msg), getString(R.string.video_edit_export_stop),
            getString(R.string.video_edit_export_cancel));
        dialog.setOnDialogClickLister(new CommonBottomDialog.OnDialogClickLister() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onAllowClick() {
                isStartExport = false;
                closeActivity();
            }
        });
    }

    private void closeActivity() {
        Fragment naviationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        FragmentManager fragmentManager = null;
        if (naviationFragment != null) {
            fragmentManager = naviationFragment.getChildFragmentManager();
        }
        Fragment fragment = null;
        if (fragmentManager != null) {
            fragment = fragmentManager.getPrimaryNavigationFragment();
        }

        if (fragment instanceof VideoExportFailFragment) {
            ((VideoExportFailFragment) fragment).interruptVideoExport();
        }
        if (fragment instanceof VideoExportSettingFragment) {
            ((VideoExportSettingFragment) fragment).interruptVideoExport();
        }
        VideoExportActivity.this.finish();
    }

    private void initTextureView() {
        mTextureView = findViewById(R.id.video_texture_view);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setClickable(false);
        mTextureView.setOnClickListener(view -> {
            if (ivFlag.getVisibility() == View.VISIBLE && tvFlag.getVisibility() == View.VISIBLE) {
                ivFlag.setVisibility(View.INVISIBLE);
                tvFlag.setVisibility(View.INVISIBLE);
                tvFlagTips.setVisibility(View.GONE);
                ivPic.setVisibility(View.GONE);
                play(exportVideoPath);
            } else if (ivFlag.getVisibility() == View.INVISIBLE) {
                ivFlag.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
            } else if (ivFlag.getVisibility() == View.VISIBLE && tvFlag.getVisibility() == View.INVISIBLE) {
                ivFlag.setVisibility(View.INVISIBLE);
                mediaPlayer.start();
            }
        });
    }

    private void initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
        }
    }

    private void play(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        resetMediaPlayer(path);
    }

    private void resetMediaPlayer(String path) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, Uri.parse(path));
                mediaPlayer.prepareAsync();
            }
        } catch (RuntimeException e) {
            SmartLog.e(TAG, "prepare fail RuntimeException");
        } catch (Exception e) {
            SmartLog.e(TAG, "prepare fail Exception");
        }
    }

    protected void initObject() {
        destFileDir = getFilesDir().getAbsolutePath() + File.separator;
        destFileName = System.currentTimeMillis() + ".mp4";
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_export);
        if (fragment != null) {
            NavController navController = NavHostFragment.findNavController(fragment);
            NavigatorProvider provider = navController.getNavigatorProvider();
            FixFragmentNavigator fragmentNavigator =
                new FixFragmentNavigator(this, fragment.getChildFragmentManager(), fragment.getId());
            provider.addNavigator(fragmentNavigator);
            Bundle bundle = new Bundle();
            bundle.putString("destFileDir", destFileDir);
            bundle.putString("destFileName", destFileName);
            navController.setGraph(R.navigation.nav_graph_video_export, bundle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && mediaPlayer.isPlaying() && ivFlag != null) {
            ivFlag.setVisibility(View.INVISIBLE);
        }
    }

    public void startConfirm() {
        if (llProgress != null) {
            llProgress.setVisibility(View.VISIBLE);
        }
        if (mProgressbar != null) {
            if (editor == null) {
                return;
            }

            HVETimeLine timeLine = editor.getTimeLine();
            if (timeLine == null || timeLine.getAllVideoLane().isEmpty()) {
                return;
            }

            HVEVideoLane videoLane = timeLine.getVideoLane(0);
            if (videoLane.getAssets().isEmpty()) {
                return;
            }

            HVEVisibleAsset asset = (HVEVisibleAsset) videoLane.getAssetByIndex(0);
            if (asset != null) {
                int screenWidth = ScreenUtil.getScreenWidth(this);
                ivPic.post(() -> {
                    int ivHeight = ivPic.getHeight();
                    SmartLog.i(TAG, "ivHeight = " + ivHeight);

                    mProgressbar.setVisibility(View.VISIBLE);
                    double proportionWidth = width == 0 ? screenWidth : BigDecimalUtils.div(screenWidth, width, 5);
                    double proportionHeight = height == 0 ? ivHeight : BigDecimalUtils.div(ivHeight, height, 5);
                    ConstraintLayout.LayoutParams layoutParams;
                    if (width >= height) {
                        layoutParams = new ConstraintLayout.LayoutParams(screenWidth,
                            (int) (proportionWidth * height + ScreenUtil.dp2px(16)));
                    } else {
                        layoutParams = new ConstraintLayout.LayoutParams((int) (proportionHeight * width),
                            ivHeight + ScreenUtil.dp2px(16));
                    }

                    layoutParams.leftToLeft = R.id.pic;
                    layoutParams.topToTop = R.id.pic;
                    layoutParams.rightToRight = R.id.pic;
                    layoutParams.bottomToBottom = R.id.pic;
                    mProgressbar.setLayoutParams(layoutParams);
                    mProgressbar.setHeightByVideoHeight();
                });
            }

        }
    }

    public void exportAgain() {
        if (tvFlag != null) {
            tvFlag.setVisibility(View.INVISIBLE);
        }
        if (ivFlag != null) {
            ivFlag.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * exportAgainFail
     */
    public void exportAgainFail() {
        if (llProgress != null) {
            llProgress.setVisibility(View.INVISIBLE);
        }
        if (mProgressbar != null) {
            mProgressbar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * setExportProgress
     */
    @SuppressLint("SetTextI18n")
    public void setExportProgress(int progress) {
        runOnUiThread(() -> {
            tvProgressPrompt.setText(String.format(Locale.ROOT, getResources().getString(R.string.export_progressing),
                String.valueOf(progress)));
            tvProgress.setText(String.valueOf(progress));
            mProgressbar.setProgress(100, progress);
            if (progress >= 100) {
                isStartExport = false;
                llProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * exportFail
     */
    public void exportFail() {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        File deleteFile =
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator
                + Constant.LOCAL_VIDEO_SAVE_PATH + File.separator + VideoExportActivity.getTime() + ".mp4");
        if (deleteFile.isFile() && deleteFile.exists()) {
            boolean isDeleted = deleteFile.delete();
            if (!isDeleted) {
                SmartLog.i(TAG, "export failed and file delete failed!");
            }
        }

        if (forceStop) {
            return;
        }
        runOnUiThread(() -> {
            mTextureView.setClickable(false);
            tvFlag.setText(R.string.export_fail_simple);
            ivFlag.setImageResource(R.drawable.bg_export_fail);
            ivFlag.setVisibility(View.VISIBLE);
            mProgressbar.setVisibility(View.INVISIBLE);
            llProgress.setVisibility(View.INVISIBLE);
        });
    }

    /**
     * exportSuccess
     */
    public void exportSuccess(Uri uri) {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        runOnUiThread(() -> {
            ToastWrapper.makeText(getApplicationContext(), " " + getString(R.string.export_toast_tips) + " ").show();
            findViewById(R.id.tv_export_success).setVisibility(View.VISIBLE);
            btExportSuccess.setVisibility(View.VISIBLE);
            mTextureView.setClickable(true);
            tvFlagTips.setText(R.string.export_toast_tips);
            ivFlag.setImageResource(R.drawable.ic_export_vedio_play);
            ivFlag.setVisibility(View.VISIBLE);
            tvFlag.setVisibility(View.VISIBLE);
            mProgressbar.setVisibility(View.INVISIBLE);
            llProgress.setVisibility(View.INVISIBLE);
            exportVideoPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator
                    + Constant.LOCAL_VIDEO_SAVE_PATH + File.separator + VideoExportActivity.getTime() + ".mp4";
            MediaScannerConnection.scanFile(VideoExportActivity.this, new String[] {exportVideoPath}, null, null);

            initJumpUri(uri);
        });
    }

    /**
     * finishClick
     */
    public void finishClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("IsExportTemplate", true);
        setResult(Constant.RESULT_CODE, intent);
        finish();
    }

    boolean forceStop = false;

    @Override
    public void onBackPressed() {
        forceStop = true;
        if (isStartExport) {
            showExportStopDialog();
        } else {
            closeActivity();
            super.onBackPressed();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        if (ivFlag != null) {
            ivFlag.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setOnInfoListener(null);
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnVideoSizeChangedListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (editor != null) {
            editor = null;
        }

        isStartExport = false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.start();
        mp.setLooping(true);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mediaPlayer != null) {
            mTextureViewWidth = width;
            mTextureViewHeight = height;
            mediaPlayer.setSurface(new Surface(surface));
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mTextureViewWidth = width;
        mTextureViewHeight = height;
        updateTextureViewSizeCenter();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        videoWidth = mp.getVideoWidth();
        videoHeight = mp.getVideoHeight();
        updateTextureViewSizeCenter();
    }

    private void updateTextureViewSizeCenter() {
        float sx = mTextureViewWidth / (float) videoWidth;
        float sy = mTextureViewHeight / (float) videoHeight;
        Matrix matrix = new Matrix();

        matrix.preTranslate((mTextureViewWidth - videoWidth) / 2, (mTextureViewHeight - videoHeight) / 2);

        matrix.preScale(videoWidth / mTextureViewWidth, videoHeight / mTextureViewHeight);

        if (sx >= sy) {
            matrix.postScale(sy, sy, mTextureViewWidth / 2, mTextureViewHeight / 2);
        } else {
            matrix.postScale(sx, sx, mTextureViewWidth / 2, mTextureViewHeight / 2);
        }

        if (mTextureView != null) {
            mTextureView.setTransform(matrix);
            mTextureView.postInvalidate();
        }
    }

    public static void setCoverUrl(String coverUrl) {
        VideoExportActivity.coverUrl = coverUrl;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        VideoExportActivity.name = name;
    }

    public static long getTime() {
        return time;
    }

    public static void setTime(long time) {
        VideoExportActivity.time = time;
    }

    public static int getExportType() {
        return exportType;
    }

    public static void setExportType(int exportType) {
        VideoExportActivity.exportType = exportType;
    }

    public static View getView() {
        return view;
    }

    public static void setView(View view) {
        VideoExportActivity.view = view;
    }
}
