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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class PreviewPresenter<T extends Fragment> {
    private T mTarget;

    PreviewPresenter(T target) {
        mTarget = target;
    }

    public void onCreate() {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    @NonNull
    public abstract Context getContext();

    public abstract void onSurfaceCreated(SurfaceTexture surfaceTexture);

    public abstract void onSurfaceChanged(int width, int height);

    public abstract void onSurfaceDestroyed();

    public abstract void showCompare(boolean enable);

    public abstract void switchCamera();
}
