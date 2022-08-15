/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.viewmodel;

import android.app.Application;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.LicenseException;
import com.huawei.hms.videoeditor.sdk.bean.HVEColor;
import com.huawei.hms.videoeditor.utils.SmartLog;

public class ExportPreviewViewModel extends AndroidViewModel
    implements HuaweiVideoEditor.PlayCallback, HuaweiVideoEditor.SurfaceCallback {
    private static final String TAG = "ExportPlayViewModel";

    private Application application;

    private MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();

    private MutableLiveData<Boolean> isShowCover = new MutableLiveData<>();

    private HuaweiVideoEditor playEditor;

    private boolean isEditorPlaying = false;

    public void setIsPlaying(boolean isPlaying) {
        isEditorPlaying = isPlaying;
        this.isPlaying.postValue(isPlaying);
    }

    public MutableLiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public void setIsShowCover(boolean isShowCover) {
        this.isShowCover.postValue(isShowCover);
    }

    public MutableLiveData<Boolean> getIsShowCover() {
        return isShowCover;
    }

    public ExportPreviewViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void initPlayEditor(ViewGroup layout) {
        playEditor = HuaweiVideoEditor.create(application.getApplicationContext(), "");
        try {
            playEditor.initEnvironment();
        } catch (LicenseException e) {
            SmartLog.e(TAG, "initEnvironment error: " + e.getErrorMsg());
        }
        playEditor.setDisplay(layout);
        playEditor.setPlayCallback(this);
        playEditor.setSurfaceCallback(this);
        HVEColor color = new HVEColor(20, 20, 20, 00);
        playEditor.setBackgroundColor(color);
    }

    public void initAsset(String path) {
        if (playEditor == null) {
            return;
        }
        playEditor.getTimeLine().appendVideoLane().appendVideoAsset(path);
    }

    public void playOrPauseEditor() {
        if (playEditor == null) {
            return;
        }
        if (isEditorPlaying) {
            pauseEditor();
        } else {
            playEditor();
        }
    }

    public void pauseEditor() {
        if (playEditor != null) {
            playEditor.pauseTimeLine();
            setIsPlaying(false);
        }
    }

    public void playEditor() {
        if (playEditor == null) {
            return;
        }
        HVETimeLine timeLine = playEditor.getTimeLine();
        if (timeLine == null) {
            return;
        }
        long startTime = timeLine.getCurrentTime();
        long endTime = timeLine.getDuration();
        if (endTime - startTime < 10) {
            startTime = 0;
        }
        playEditor.playTimeLine(startTime, endTime);
        setIsPlaying(true);
    }

    public void release() {
        if (playEditor == null) {
            return;
        }
        playEditor.stopEditor();
        playEditor = null;
    }

    @Override
    public void surfaceCreated() {

    }

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void surfaceChanged(int width, int height) {
        if (playEditor == null) {
            return;
        }
        HVETimeLine timeLine = playEditor.getTimeLine();
        if (timeLine == null) {
            return;
        }
        long time = timeLine.getCurrentTime();

        playEditor.seekTimeLine(time < 0 ? 0 : time, () -> setIsPlaying(false));
    }

    @Override
    public void onPlayProgress(long timeStamp) {
        if (playEditor == null) {
            return;
        }
        HVETimeLine timeLine = playEditor.getTimeLine();
        if (timeLine == null) {
            return;
        }
        long duration = timeLine.getDuration();
        setIsShowCover(duration == timeStamp);
    }

    @Override
    public void onPlayStopped() {
        setIsPlaying(false);
    }

    @Override
    public void onPlayFinished() {
        setIsPlaying(false);
        if (playEditor != null) {
            playEditor.seekTimeLine(0);
        }
    }

    @Override
    public void onPlayFailed() {
        SmartLog.e(TAG, "onPlayFailed!!!");
        setIsPlaying(false);
    }
}
