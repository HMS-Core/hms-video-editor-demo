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

package com.huawei.hms.videoeditor.ui.mediaeditor.timelapse;

import android.app.Application;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.sdk.ai.HVETimeLapseDetectCallback;
import com.huawei.hms.videoeditor.sdk.ai.HVETimeLapseEffectOptions;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.utils.SmartLog;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class TimeLapseViewModel extends AndroidViewModel {
    private static final String TAG = "TimeLapseViewModel";

    public static final int STATE_ERROR = -1;

    public static final int STATE_NO_SKY_WATER = 0;

    public static final int STATE_ONLY_SKY = 1;

    public static final int STATE_ONLY_WATER = 2;

    public static final int STATE_SKY_WATER = 3;

    private int skyRiverType;

    private int scaleSky = 0;

    private int speedSky = 2;

    private int scaleRiver = 90;

    private int speedRiver = 2;

    private HVEAsset selectedAsset;

    private int timeLapseResult;

    private MutableLiveData<Integer> timeLapseEnter = new MutableLiveData<>();

    private MutableLiveData<Integer> timeLapseStart = new MutableLiveData<>();

    public TimeLapseViewModel(@NonNull Application application) {
        super(application);
    }

    public int getSkyRiverType() {
        return skyRiverType;
    }

    public void setSkyRiverType(int skyRiverType) {
        this.skyRiverType = skyRiverType;
    }

    public int getScaleSky() {
        return scaleSky;
    }

    public void setScaleSky(int scaleSky) {
        this.scaleSky = scaleSky;
    }

    public int getSpeedSky() {
        return speedSky;
    }

    public void setSpeedSky(int speedSky) {
        this.speedSky = speedSky;
    }

    public int getScaleRiver() {
        return scaleRiver;
    }

    public void setScaleRiver(int scaleRiver) {
        this.scaleRiver = scaleRiver;
    }

    public int getSpeedRiver() {
        return speedRiver;
    }

    public void setSpeedRiver(int speedRiver) {
        this.speedRiver = speedRiver;
    }

    public HVETimeLine getTimeLine() {
        return EditorManager.getInstance().getTimeLine();
    }

    public HVEAsset getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(HVEAsset selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public int getTimeLapseResult() {
        return timeLapseResult;
    }

    public void setTimeLapseResult(int timeLapseResult) {
        this.timeLapseResult = timeLapseResult;
    }

    public MutableLiveData<Integer> getTimeLapseEnter() {
        return timeLapseEnter;
    }

    public void setTimeLapseEnter(int timeLapseEnter) {
        this.timeLapseEnter.postValue(timeLapseEnter);
    }

    public MutableLiveData<Integer> getTimeLapseStart() {
        return timeLapseStart;
    }

    public void setTimeLapseStart(int timeLapseStart) {
        this.timeLapseStart.postValue(timeLapseStart);
    }

    public void initTimeLapse(HVEAIInitialCallback downloadCallback) {
        SmartLog.i(TAG, "enter initTimeLapse");
        if (selectedAsset == null) {
            SmartLog.e(TAG, "Selected Asset is null!");
            return;
        }
        if (selectedAsset instanceof HVEImageAsset) {
            ((HVEImageAsset) selectedAsset).initTimeLapseEngine(downloadCallback);
        } else {
            SmartLog.e(TAG, "select asset not image");
        }
    }

    public void firstDetectTimeLapse(HVETimeLapseDetectCallback callback) {
        SmartLog.i(TAG, "enter detectTimeLapse");
        if (selectedAsset == null) {
            SmartLog.e(TAG, "Selected Asset is null!");
            return;
        }
        if (callback == null) {
            SmartLog.e(TAG, "callback is null!");
            return;
        }
        if (selectedAsset instanceof HVEImageAsset) {
            ((HVEImageAsset) selectedAsset).detectTimeLapse(callback);
        } else {
            SmartLog.e(TAG, "select asset not image");
        }
    }

    public void addTimeLapseEffect(int motionType, float skySpeed, int skyAngle, float waterSpeed, int waterAngle,
        HVEAIProcessCallback decodeCallback) {
        SmartLog.i(TAG, "enter detectTimeLapse");
        if (selectedAsset == null) {
            SmartLog.e(TAG, "Selected Asset is null!");
            return;
        }
        if (selectedAsset instanceof HVEImageAsset) {
            ((HVEImageAsset) selectedAsset)
                .addTimeLapseEffect(new HVETimeLapseEffectOptions.Builder().setMotionType(motionType)
                    .setSkySpeed(skySpeed)
                    .setSkyAngle(skyAngle)
                    .setWaterAngle(waterAngle)
                    .setWaterSpeed(waterSpeed)
                    .build(), decodeCallback);
        } else {
            SmartLog.e(TAG, "select asset not image");
        }
    }

    public void stopTimeLapse() {
        SmartLog.i(TAG, "enter stopWaterWalk");
        if (selectedAsset == null) {
            SmartLog.e(TAG, "Selected Asset is null!");
            return;
        }
        setSpeedSky(2);
        setScaleSky(0);
        setSpeedRiver(2);
        setScaleRiver(0);
        if (selectedAsset instanceof HVEImageAsset) {
            ((HVEImageAsset) selectedAsset).interruptTimeLapse();
        } else {
            SmartLog.e(TAG, "select asset not image");
        }
    }
}