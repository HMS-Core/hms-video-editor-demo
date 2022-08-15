/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEThumbnailCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ImageUtil;
import com.huawei.hms.videoeditor.ui.mediaexport.model.VideoParams;
import com.huawei.hms.videoeditor.utils.SmartLog;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SettingViewModel extends AndroidViewModel {
    private static final String TAG = "NewExportViewModel";

    private static final String EXPORT_COVER_NAME = "export_cover.jpg";

    private MutableLiveData<Bitmap> firstFrame = new MutableLiveData<>();

    private MutableLiveData<Boolean> hasCover = new MutableLiveData<>();

    private HuaweiVideoEditor editor;

    private Application application;

    private String coverUrl;

    private String source;

    private String editUuid;

    // <------ExportSuccessFragment data------>
    private Uri uri;

    private String videoPath;

    private int exportType;

    private String loadUrl;

    private boolean isTemplate;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public int getExportType() {
        return exportType;
    }

    public void setExportType(int exportType) {
        this.exportType = exportType;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public MutableLiveData<Bitmap> getFirstFrame() {
        return firstFrame;
    }

    public MutableLiveData<Boolean> getHasCover() {
        return hasCover;
    }

    public String getEditUuid() {
        return editUuid;
    }

    public void setEditUuid(String editUuid) {
        this.editUuid = editUuid;
        initEditor();
        initCoverUrl();
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    private void initEditor() {
        editor = HuaweiVideoEditor.getInstance(editUuid);
    }

    private void initCoverUrl() {
        if (editor == null) {
            return;
        }
        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine == null) {
            return;
        }
        HVEAsset hveAsset = timeLine.getCoverImage();
        if (hveAsset == null) {
            return;
        }
        setCoverUrl(hveAsset.getPath());
    }

    public void getVideoFirstFrame() {
        if (editor == null) {
            return;
        }

        HVETimeLine hveTimeLine = editor.getTimeLine();
        if (hveTimeLine == null) {
            return;
        }
        List<HVEVideoLane> videoLanes = hveTimeLine.getAllVideoLane();
        if (videoLanes.size() == 0) {
            return;
        }
        HVEAsset asset = videoLanes.get(0).getAssetByIndex(0);
        if (asset == null) {
            return;
        }

        int canvasWidth = editor.getCanvasWidth();
        int canvasHeight = editor.getCanvasHeight();
        SmartLog.i(TAG, "getCoverBitmap width: " + canvasWidth + " height: " + canvasHeight);
        if (asset instanceof HVEVideoAsset) {
            HVEUtil.getExactThumbnails(asset.getTrimIn(), asset.getPath(), videoThumbnailCallback);
            return;
        }

        if (asset instanceof HVEImageAsset) {
            ((HVEImageAsset) asset).getThumbNail(canvasWidth, canvasHeight, asset.getEndTime(), asset.getEndTime(),
                imageThumbnailCallback);
        }
    }

    private HVEUtil.HVEThumbnailCallback videoThumbnailCallback = new HVEUtil.HVEThumbnailCallback() {
        @Override
        public void onBitmap(Bitmap bitmap) {
            postCover(bitmap);
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFail(String errorCode, String errMsg) {

        }
    };

    private HVEThumbnailCallback imageThumbnailCallback = new HVEThumbnailCallback() {
        @Override
        public void onImageAvailable(Bitmap bitmap, long timeStamp) {
            postCover(bitmap);
        }

        @Override
        public void onImagePathAvailable(String filePath, long timeStamp) {

        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFail(String errorCode, Exception e) {

        }
    };

    private void postCover(Bitmap bitmap) {
        Bitmap adjustBitmap = ImageUtil.adjustBitmapSize(bitmap, getCanvasWidth(), getCanvasHeight());
        if (adjustBitmap != null) {
            try {
                this.coverUrl = FileUtil.saveBitmap(application.getApplicationContext(), bitmap,
                    System.currentTimeMillis() + EXPORT_COVER_NAME);
            } catch (IOException e) {
                SmartLog.e(TAG, "postCover error");
            }
            firstFrame.postValue(adjustBitmap);
        }
    }

    public void requestCoverImg() {
        if (!TextUtils.isEmpty(coverUrl)) {
            hasCover.setValue(true);
            return;
        }
        hasCover.postValue(false);
    }

    public long getVideoTime() {
        if (editor == null) {
            return 0L;
        }
        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine == null) {
            return 0L;
        }
        return timeLine.getDuration();
    }

    public long getEndTime() {
        if (editor == null) {
            return 0L;
        }
        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine == null) {
            return 0L;
        }
        return timeLine.getEndTime();
    }

    public int getCanvasWidth() {
        if (editor == null) {
            return 0;
        }
        int canvasWidth = editor.getCanvasWidth();
        return Math.max(canvasWidth, 0);
    }

    public int getCanvasHeight() {
        if (editor == null) {
            return 0;
        }
        int canvasHeight = editor.getCanvasHeight();
        return Math.max(canvasHeight, 0);
    }

    public VideoParams getResetParams(int videoWidth, int videoHeight) {
        int canvasWidth = getCanvasWidth();
        int canvasHeight = getCanvasHeight();

        int layoutWidth = canvasWidth;
        int layoutHeight = canvasHeight;

        if (canvasWidth > 0 && canvasHeight > 0) {
            float sx = videoWidth / (float) canvasWidth;
            float sy = videoHeight / (float) canvasHeight;
            if (sx >= sy) {
                layoutWidth = (int) (canvasWidth * videoHeight / (float) canvasHeight);
                layoutHeight = videoHeight;
            } else {
                layoutWidth = videoWidth;
                layoutHeight = (int) (canvasHeight * videoWidth / (float) canvasWidth);
            }
        }
        return new VideoParams(layoutWidth, layoutHeight);
    }

    public Uri initJumpUri(Activity activity) {
        Uri mediaJumpUri;
        if (uri != null) {
            return uri;
        }
        if (activity == null) {
            return null;
        }

        String filePath = getVideoPath();
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        try {
            mediaJumpUri = FileUtil.insert(activity.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                generateSaveValue(new File(filePath), getCanvasWidth(), getCanvasHeight(), getVideoTime()));
        } catch (IOException e) {
            SmartLog.e(TAG, "saveSuccess: IOException");
            mediaJumpUri = null;
        }
        return mediaJumpUri;
    }

    private ContentValues generateSaveValue(File file, int canvasWidth, int canvasHeight, long videoTime)
        throws IOException {
        ContentValues values = new ContentValues(10);
        String fileName = file.getName();
        String[] titleAndSubFix = fileName.split("\\.");
        if (titleAndSubFix.length > 0) {
            values.put(MediaStore.Video.Media.TITLE, titleAndSubFix[0]);
        }
        values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Video.Media.DATA, file.getCanonicalPath());

        long curTime = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, curTime);
        }
        values.put(MediaStore.Video.Media.DATE_MODIFIED, curTime / 1000);

        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            values.put(MediaStore.Video.Media.RESOLUTION, canvasWidth + "x" + canvasHeight);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.DURATION, videoTime);
        }

        values.put(MediaStore.Video.Media.SIZE, file.length());
        return values;
    }

    public void removeCoverBitmap() {
        String sdPath = application.getApplicationContext().getFilesDir().getAbsolutePath();
        File[] files = new File(sdPath).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().contains(EXPORT_COVER_NAME)) {
                boolean delete = file.delete();
                if (!delete) {
                    SmartLog.e(TAG, "removeCoverBitmap false");
                }
            }
        }
    }
}
