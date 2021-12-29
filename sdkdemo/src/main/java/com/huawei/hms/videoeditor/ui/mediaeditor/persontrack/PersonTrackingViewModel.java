
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

package com.huawei.hms.videoeditor.ui.mediaeditor.persontrack;

import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_PIP_OPERATION_HUMAN_TRACKING;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_OPERATION_HUMAN_TRACKING;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_HUMAN_TRACKING;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.graphics.Point;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.EditorManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class PersonTrackingViewModel extends AndroidViewModel {
    private static final String TAG = "PersonTrackingViewModel";

    private MutableLiveData<Integer> humanTrackingEnter = new MutableLiveData<>();

    private MutableLiveData<Point> trackingPoint = new MutableLiveData<>();

    private MutableLiveData<Boolean> trackingIsReady = new MutableLiveData<>();

    private MutableLiveData<Integer> trackingIsStart = new MutableLiveData<>();

    private HVEAsset selectedTrackingAsset;

    private int humanTrackingEntrance;

    private long mVideoEndTime;

    public PersonTrackingViewModel(@NonNull Application application) {
        super(application);
    }

    public HVETimeLine getTimeLine() {
        return EditorManager.getInstance().getTimeLine();
    }

    public List<HVEAsset> getItems() {
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (videoLane == null) {
            return new ArrayList<>();
        }
        return videoLane.getAssets();
    }

    public HVEAsset getMainLaneAsset() {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return null;
        }

        long time = timeLine.getCurrentTime();

        HVEVideoLane lane = EditorManager.getInstance().getMainLane();
        if (lane != null && time == lane.getEndTime()) {
            return lane.getAssetByIndex(lane.getAssets().size() - 1);
        }

        for (int i = 0; i < getItems().size(); i++) {
            HVEAsset asset = getItems().get(i);
            if (time >= asset.getStartTime() && time < asset.getEndTime()) {
                return asset;
            }
        }
        return null;
    }

    public long getVideoEndTime() {
        return mVideoEndTime;
    }

    public void setVideoEndTime(long mVideoEndTime) {
        this.mVideoEndTime = mVideoEndTime;
    }

    public MutableLiveData<Integer> getHumanTrackingEnter() {
        return humanTrackingEnter;
    }

    public MutableLiveData<Boolean> getTrackingIsReady() {
        return trackingIsReady;
    }

    public MutableLiveData<Integer> getTrackingIsStart() {
        return trackingIsStart;
    }

    public MutableLiveData<Point> getTrackingPoint() {
        return trackingPoint;
    }

    public HVEAsset getSelectedTracking() {
        return selectedTrackingAsset;
    }

    public int getHumanTrackingEntrance() {
        return humanTrackingEntrance;
    }

    public void setHumanTrackingEnter(Integer humanTrackingEnter) {
        this.humanTrackingEnter.postValue(humanTrackingEnter);
    }

    public void setTrackingIsReady(Boolean trackingIsReady) {
        this.trackingIsReady.postValue(trackingIsReady);
    }

    public void setTrackingIsStart(Integer trackingIsStart) {
        this.trackingIsStart.postValue(trackingIsStart);
    }

    public void setTrackingPoint(Point trackingPoint) {
        this.trackingPoint.postValue(trackingPoint);
    }

    public void setSelectedTracking(HVEAsset hveAsset) {
        this.selectedTrackingAsset = hveAsset;
    }

    public void setHumanTrackingEntrance(int humanTrackingEntrance) {
        this.humanTrackingEntrance = humanTrackingEntrance;
    }

    public void humanTracking(int type, HVEAIProcessCallback humanTrackingCallback) {
        HVEAsset trackingAsset = null;
        if (type == EDIT_VIDEO_STATE_HUMAN_TRACKING) {
            trackingAsset = getMainLaneAsset();
        } else if (type == EDIT_VIDEO_OPERATION_HUMAN_TRACKING || type == EDIT_PIP_OPERATION_HUMAN_TRACKING) {
            trackingAsset = getSelectedTracking();
        }
        startTracking(trackingAsset, humanTrackingCallback);
    }


    public void startTracking(HVEAsset trackingAsset, HVEAIProcessCallback humanTrackingCallback) {
        if (trackingAsset == null) {
            return;
        }
        if (trackingAsset instanceof HVEVideoAsset) {

            ((HVEVideoAsset) trackingAsset).addHumanTrackingEffect(new HVEAIProcessCallback() {
                @Override
                public void onProgress(int progress) {
                    if (humanTrackingCallback != null) {
                        humanTrackingCallback.onProgress(progress);
                    }
                }

                @Override
                public void onSuccess() {
                    if (humanTrackingCallback != null) {
                        humanTrackingCallback.onSuccess();
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    if (humanTrackingCallback != null) {
                        humanTrackingCallback.onError(errorCode, errorMessage);
                    }
                }
            });
        }
    }

    public void interruptHumanTracking() {
        SmartLog.i(TAG, "enter interruptHumanTracking");
        if (selectedTrackingAsset == null) {
            SmartLog.e(TAG, "selectedTracking is null!");
            return;
        }
        if (selectedTrackingAsset instanceof HVEVideoAsset) {
            ((HVEVideoAsset) selectedTrackingAsset).interruptHumanTracking();
        }
    }
}
