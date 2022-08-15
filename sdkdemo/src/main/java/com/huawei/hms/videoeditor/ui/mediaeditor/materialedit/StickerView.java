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

package com.huawei.hms.videoeditor.ui.mediaeditor.materialedit;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class StickerView extends TransformView {
    public StickerView(Context context, @Nullable AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    @Override
    protected void onDraw(Canvas theCanvas) {
        super.onDraw(theCanvas);

        if (isDrawDelete) {
            theCanvas.drawBitmap(mDeleteBitmap, null, mDeleteRect, mPaint);
        }
        if (isDrawScale) {
            theCanvas.drawBitmap(mScaleBitmap, null, mScaleRect, mPaint);
        }
        if (isDrawEdit) {
            theCanvas.drawBitmap(mEditBitmap, null, mEditRect, mPaint);
        }
        if (isDrawCopy) {
            theCanvas.drawBitmap(mCopyBitmap, null, mCopyRect, mPaint);
        }
    }
}
