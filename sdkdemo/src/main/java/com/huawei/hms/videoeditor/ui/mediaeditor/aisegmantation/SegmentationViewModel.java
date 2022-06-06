/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.mediaeditor.aisegmantation;

import android.app.Application;
import android.graphics.Point;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.sdk.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;

import java.util.List;

public class SegmentationViewModel extends AndroidViewModel {

    private static final String TAG = "SegmentationViewModel";

    private MutableLiveData<Integer> segmentationEnter = new MutableLiveData<>();

    private final MutableLiveData<List<HVEPosition2D>> points = new MutableLiveData<>();

    private final MutableLiveData<List<Point>> drawPoints = new MutableLiveData<>();

    private HVEAsset selectedAsset;

    private MutableLiveData<Boolean> isInit = new MutableLiveData<>();

    private MutableLiveData<Boolean> isReady = new MutableLiveData<>();

    private MutableLiveData<Integer> isStart = new MutableLiveData<>();

    public SegmentationViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getSegmentationEnter() {
        return segmentationEnter;
    }

    public void setSegmentationEnter(Integer segmentationEnter) {
        this.segmentationEnter.postValue(segmentationEnter);
    }

    public void setSelectedAsset(HVEAsset selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public HVEAsset getSelectedAsset() {
        return selectedAsset;
    }

    public void setPoints(List<HVEPosition2D> points) {
        this.points.postValue(points);
    }

    public MutableLiveData<List<HVEPosition2D>> getPoints() {
        return points;
    }

    public void setDrawPoints(List<Point> drawPoints) {
        this.drawPoints.postValue(drawPoints);
    }

    public MutableLiveData<List<Point>> getDrawPoints() {
        return drawPoints;
    }

    public void setIsInit(Boolean isInit) {
        this.isInit.postValue(isInit);
    }

    public MutableLiveData<Boolean> getIsInit() {
        return isInit;
    }

    public void setIsReady(Boolean isReady) {
        this.isReady.postValue(isReady);
    }

    public MutableLiveData<Boolean> getIsReady() {
        return isReady;
    }

    public MutableLiveData<Integer> getIsStart() {
        return isStart;
    }

    public void setIsStart(Integer isStart) {
        this.isStart.postValue(isStart);
    }

    public void initializeSegmentation(HVEAIInitialCallback downloadCallback) {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "selectedAsset is null");
            return;
        }
        ((HVEVideoAsset) selectedAsset).initSegmentationEngine(downloadCallback);
    }

    public void segmentationDetect(HVEAIProcessCallback segmentationCallback) {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "selectedAsset is null");
            return;
        }
        ((HVEVideoAsset) selectedAsset).addSegmentationEffect(segmentationCallback);
    }

    public void removeCurrentEffect() {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "selectedAsset is null");
            return;
        }
        setIsReady(false);
        ((HVEVideoAsset) selectedAsset).removeSegmentationEffect();
    }

    public void interruptCurrentEffect() {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "selectedAsset is null");
            return;
        }
        setIsReady(false);
        ((HVEVideoAsset) selectedAsset).interruptSegmentation();
    }
}
