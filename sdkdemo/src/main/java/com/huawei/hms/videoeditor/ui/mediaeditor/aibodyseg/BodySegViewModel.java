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

package com.huawei.hms.videoeditor.ui.mediaeditor.aibodyseg;

import android.app.Application;

import com.huawei.hms.videoeditor.sdk.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.utils.SmartLog;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class BodySegViewModel extends AndroidViewModel {
    private static final String TAG = "SegmentationViewModel";

    private MutableLiveData<Integer> bodySeg = new MutableLiveData<>();

    private HVEAsset selectedAsset;

    public BodySegViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getBodySegEnter() {
        return bodySeg;
    }

    public void setBodySegEnter(Integer segmentationEnter) {
        this.bodySeg.postValue(segmentationEnter);
    }

    public void setSelectedAsset(HVEAsset selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public void initializeBodySeg(int segPart, HVEAIInitialCallback callback){
        if(selectedAsset == null){
            SmartLog.e(TAG, "selectedAsset is null");
            return;
        }
        ((HVEVisibleAsset) selectedAsset).initBodySegEngine(segPart, callback);
    }

    public void bodySegDetect(HVEAIProcessCallback callback){
        if(selectedAsset == null){
            SmartLog.e(TAG, "selectedAsset is null");
            return;
        }
        ((HVEVisibleAsset) selectedAsset).addBodySegEffect(callback);
    }

    public boolean removeCurrentEffect() {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "selectedAsset is null");
            return false;
        }
        return ((HVEVisibleAsset) selectedAsset).removeBodySegEffect();
    }

    public void interruptCurrentEffect() {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "selectedAsset is null");
            return;
        }
        ((HVEVisibleAsset) selectedAsset).interruptBodySegEffect();
    }

    public void releaseBodySegEngine() {
        if (selectedAsset == null) {
            SmartLog.e(TAG, "selectedAsset is null");
            return;
        }
        ((HVEVisibleAsset) selectedAsset).releaseBodySegEngine();
    }
}
