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

package com.huawei.hms.videoeditor.ui.common.bean;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class ThumbInfo {

    private Bitmap bitmap;

    private boolean isloading = false;

    private long startDownTime;

    private int mInt = 0;

    public ThumbInfo(Bitmap bmp) {
        this.bitmap = bmp;
    }

    @Override
    @NonNull
    public String toString() {
        return "ThumbInfo [bmp=" + ((null != bitmap) ? bitmap.getByteCount() : "null") + ", isloading=" + isloading
            + "]";
    }

    public void recycle() {
        if (null != bitmap) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bmp) {
        this.bitmap = bmp;
    }
}
