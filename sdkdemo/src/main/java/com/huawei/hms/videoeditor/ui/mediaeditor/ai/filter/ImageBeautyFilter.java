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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.filter;

import android.util.Log;

import com.huawei.hms.videoeditor.ai.HVEAIBeauty;
import com.huawei.hms.videoeditor.ai.HVEAIBeautyOptions;
import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;

public class ImageBeautyFilter {
    private static final String TAG = "GLImageBeautyFilter";

    private HVEAIBeauty hveaiBeauty;

    public ImageBeautyFilter() {
        hveaiBeauty = new HVEAIBeauty();
    }

    public void initEngine(HVEAIInitialCallback callback) {
        hveaiBeauty.initEngine(callback);
    }

    public void prepare() {
        Log.i(TAG, "prepare ");
        hveaiBeauty.prepare();
        Log.i(TAG, "prepare end ");
    }

    public void resize(int imgWidth, int imgHeight) {
        Log.i(TAG, "resize imgWidth " + imgWidth + " imgHeight " + imgHeight);
        hveaiBeauty.resize(imgWidth, imgHeight);
    }

    public int updateOptions(HVEAIBeautyOptions beautyOptions) {
        Log.i(TAG, "enter updateOptions");
        hveaiBeauty.updateOptions(beautyOptions);
        return 0;
    }

    public int drawFrameBuffer(int textureId) {
        Log.i(TAG, "drawFrameBuffer textureId " + textureId);
        int currentTexture = hveaiBeauty.process(textureId);
        Log.i(TAG, "drawFrameBuffer currentTexture " + currentTexture);
        return currentTexture;
    }

    public void release() {
        hveaiBeauty.releaseEngine();
    }
}
