/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.viewmodel;

import static com.huawei.hms.videoeditor.sdk.bean.HVEVideoProperty.EncodeType.ENCODE_H_264;
import static com.huawei.hms.videoeditor.sdk.bean.HVEVideoProperty.EncodeType.ENCODE_H_265;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.sdk.HVEExportManager;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.bean.HVEVideoProperty;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.mediaexport.model.ExportResult;
import com.huawei.hms.videoeditor.utils.SmartLog;

import java.io.File;

public class ExportViewModel extends AndroidViewModel implements HVEExportManager.HVEExportVideoCallback {
    private static final String TAG = "ExportViewModel";

    private MutableLiveData<Integer> exportProgress = new MutableLiveData<>();

    private MutableLiveData<ExportResult> exportComplete = new MutableLiveData<>();

    private HuaweiVideoEditor editor;

    private HVEExportManager exportManager;

    private boolean isExporting;

    private int width = 1920;

    private int height = 1080;

    private int frameRate = 30;

    private boolean isH265;

    private int originWidth = 0;

    private int originHeight = 0;

    private int originFrame = 30;

    private HVEVideoProperty.EncodeType encodeType = ENCODE_H_264;

    private long fileSize;

    public ExportViewModel(@NonNull Application application) {
        super(application);
        initExportManager();
    }

    public void setEditUuid(String uuid) {
        editor = HuaweiVideoEditor.getInstance(uuid);
    }

    public void initExportManager() {
        exportManager = new HVEExportManager();
    }

    public MutableLiveData<Integer> getExportProgress() {
        return exportProgress;
    }

    public MutableLiveData<ExportResult> getExportComplete() {
        return exportComplete;
    }

    public boolean isExporting() {
        return isExporting;
    }

    public long calculateSize(int width, int height) {
        this.width = width;
        this.height = height;
        fileSize = HVEUtil.getEstimatesExportVideoSize(width, height, frameRate, getVideoTime(), isH265);
        return fileSize;
    }

    public long calculateSize(int frameRate) {
        this.frameRate = frameRate;
        fileSize = HVEUtil.getEstimatesExportVideoSize(width, height, frameRate, getVideoTime(), isH265);
        return fileSize;
    }

    public long calculateSize(boolean isH265) {
        this.isH265 = isH265;
        if (isH265) {
            this.encodeType = ENCODE_H_265;
        } else {
            this.encodeType = ENCODE_H_264;
        }
        return HVEUtil.getEstimatesExportVideoSize(width, height, frameRate, getVideoTime(), isH265);
    }

    @Override
    public void onCompileProgress(long time, long duration) {
        if (duration == 0) {
            SmartLog.i(TAG, "duration 0");
            return;
        }
        int progress = (int) (time * 100 / duration);
        if (progress >= 100) {
            progress = 100;
            this.isExporting = false;
        }
        exportProgress.postValue(progress);
    }

    @Override
    public void onCompileFinished(String path, Uri uri) {
        SmartLog.i(TAG, "onCompileFinished");
        this.isExporting = false;
        exportComplete.postValue(new ExportResult(uri, path, true));
    }

    @Override
    public void onCompileFailed(int errCode, String errorMsg) {
        SmartLog.i(TAG, "onCompileFailed " + errorMsg);
        this.isExporting = false;
        exportComplete.postValue(new ExportResult(false, errCode));
    }

    public void exportVideo(boolean isRepeat, Context context) {
        String outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            + File.separator + Constant.LOCAL_VIDEO_SAVE_PATH + File.separator + System.currentTimeMillis() + ".mp4";
        if (TextUtils.isEmpty(outputPath)) {
            return;
        }
        if (isRepeat) {
            initExportManager();
        }
        this.isExporting = true;
        if (exportManager == null || editor == null) {
            return;
        }
        HVEVideoProperty property = new HVEVideoProperty(width, height, frameRate, encodeType);
        property.setEncodeColorMode(false);
        SmartLog.i(TAG, "Export result is :" + "\tisH265 = " + isH265);
        exportManager.exportVideo(editor, this, property, outputPath);
    }

    public void interruptVideoExport() {
        isExporting = false;
        if (exportManager != null) {
            exportManager.interruptVideoExport();
        }
    }

    private long getVideoTime() {
        if (editor == null) {
            return 0L;
        }
        HVETimeLine timeLine = editor.getTimeLine();
        if (timeLine == null) {
            return 0L;
        }
        return timeLine.getDuration();
    }
}
