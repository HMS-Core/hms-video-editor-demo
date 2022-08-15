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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.ai.HVEAITimeLapse;
import com.huawei.hms.videoeditor.ai.HVEAITimeLapseOptions;
import com.huawei.hms.videoeditor.ai.HVETimeLapseDetectCallback;
import com.huawei.hms.videoeditor.utils.SmartLog;

public class AITimeLapseViewModel extends AndroidViewModel {
    private static final String TAG = "TimeLapseViewModel";

    public static final int STATE_EDIT = 0x101;

    public static final int STATE_BACK_PRESSED = 0x202;

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

    private int timeLapseResult;

    private int timeLapseStatus;

    private MutableLiveData<Integer> timeLapseStart = new MutableLiveData<>();

    private HVEAITimeLapse mHVEAITimeLapse = new HVEAITimeLapse();

    public AITimeLapseViewModel(@NonNull Application application) {
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

    public int getTimeLapseResult() {
        return timeLapseResult;
    }

    public void setTimeLapseResult(int timeLapseResult) {
        this.timeLapseResult = timeLapseResult;
    }

    public int getTimeLapseStatus() {
        return timeLapseStatus;
    }

    public void setTimeLapseStatus(int timeLapseStatus) {
        this.timeLapseStatus = timeLapseStatus;
    }

    public MutableLiveData<Integer> getTimeLapseStart() {
        return timeLapseStart;
    }

    public void setTimeLapseStart(int timeLapseStart) {
        this.timeLapseStart.postValue(timeLapseStart);
    }

    public void initTimeLapse(HVEAIInitialCallback downloadCallback) {
        SmartLog.i(TAG, "enter initTimeLapse");
        if (mHVEAITimeLapse != null) {
            mHVEAITimeLapse.initEngine(downloadCallback);
        }
    }

    public void firstDetectTimeLapse(String imagePath, HVETimeLapseDetectCallback callback) {
        SmartLog.i(TAG, "enter detectFirstTimeLapse");
        if (mHVEAITimeLapse != null) {
            mHVEAITimeLapse.detectTimeLapse(imagePath, callback);
        }
    }

    public void processTimeLapse(String imagePath, int motionType, float skySpeed, int skyAngle, float waterSpeed,
        int waterAngle, HVEAIProcessCallback<String> processCallback) {
        SmartLog.i(TAG, "enter processTimeLapse");
        HVEAITimeLapseOptions options = new HVEAITimeLapseOptions.Builder().setMotionType(motionType)
            .setSkySpeed(skySpeed)
            .setSkyAngle(skyAngle)
            .setWaterAngle(waterAngle)
            .setWaterSpeed(waterSpeed)
            .build();
        if (mHVEAITimeLapse != null) {
            mHVEAITimeLapse.process(imagePath, options, processCallback);
        }
    }

    public void stopTimeLapse() {
        SmartLog.i(TAG, "enter stopWaterWalk");
        setSpeedSky(2);
        setScaleSky(0);
        setSpeedRiver(2);
        setScaleRiver(0);
        if (mHVEAITimeLapse != null) {
            mHVEAITimeLapse.interruptTimeLapse();
        }
    }

    public void releaseEngine() {
        SmartLog.i(TAG, "enter releaseEngine");
        if (mHVEAITimeLapse != null) {
            mHVEAITimeLapse.releaseEngine();
        }
    }
}