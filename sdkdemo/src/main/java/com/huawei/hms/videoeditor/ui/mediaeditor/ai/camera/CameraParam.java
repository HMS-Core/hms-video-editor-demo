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

import android.hardware.Camera;

import com.huawei.hms.videoeditor.ui.mediaeditor.ai.filter.BeautyParam;

public final class CameraParam {

    public static final int DEFAULT_WIDTH = 1280;

    public static final int DEFAULT_HEIGHT = 720;

    public static final int PREVIEW_FPS = 30;

    public static final float RATIO = 0.5625f;

    public boolean showFps;

    public float currentRatio;

    public int expectFps;

    public int previewFps;

    public int expectWidth;

    public int expectHeight;

    public int previewWidth;

    public int previewHeight;

    public int orientation;

    public int cameraId;

    public boolean showCompare;

    public BeautyParam beauty;

    private static final CameraParam mInstance = new CameraParam();

    public static CameraParam getInstance() {
        return mInstance;
    }

    private CameraParam() {
        reset();
    }

    private void reset() {
        showFps = false;
        currentRatio = RATIO;
        expectFps = PREVIEW_FPS;
        previewFps = 0;
        expectWidth = DEFAULT_WIDTH;
        expectHeight = DEFAULT_HEIGHT;
        previewWidth = 0;
        previewHeight = 0;
        orientation = 0;
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        showCompare = false;
        beauty = new BeautyParam();
    }
}
