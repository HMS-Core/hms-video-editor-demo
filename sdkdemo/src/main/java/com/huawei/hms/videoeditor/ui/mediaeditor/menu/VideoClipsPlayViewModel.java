
/*
 *  Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class VideoClipsPlayViewModel extends AndroidViewModel {
    private final MutableLiveData<Long> videoDuration = new MutableLiveData<>();

    private final MutableLiveData<Long> currentTime = new MutableLiveData<>();

    private final MutableLiveData<Boolean> playState = new MutableLiveData<>();

    private final MutableLiveData<Boolean> fullScreenState = new MutableLiveData<>();

    private final MutableLiveData<Boolean> showFrameAddDelete = new MutableLiveData<>();

    public VideoClipsPlayViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Long> getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Long duration) {
        this.videoDuration.postValue(duration);
    }

    public MutableLiveData<Long> getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime.postValue(currentTime == null ? Long.valueOf(-1L) : currentTime);
    }

    public MutableLiveData<Boolean> getPlayState() {
        return playState;
    }

    public void setPlayState(Boolean isPlay) {
        if (playState.getValue() == isPlay) {
            return;
        }
        playState.setValue(isPlay);
    }

    public MutableLiveData<Boolean> getFullScreenState() {
        return fullScreenState;
    }

    public void setFullScreenState(boolean isFullScreen) {
        fullScreenState.postValue(isFullScreen);
    }

    public MutableLiveData<Boolean> getShowFrameAddDelete() {
        return showFrameAddDelete;
    }

    public void setFrameAddDeletell(boolean isShowFrameAdd) {
        showFrameAddDelete.postValue(isShowFrameAdd);
    }
}
