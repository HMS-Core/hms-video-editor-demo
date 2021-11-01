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

package com.huawei.hms.videoeditor.ui.mediaeditor.materialedit;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class StickerView extends TransformView {

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isDrawDelete) {
            canvas.drawBitmap(mDeleteBitmap, null, mDeleteRect, mPaint);
        }
        if (isDrawScale) {
            canvas.drawBitmap(mScaleBitmap, null, mScaleRect, mPaint);
        }
        if (isDrawEdit) {
            canvas.drawBitmap(mEditBitmap, null, mEditRect, mPaint);
        }
        if (isDrawCopy) {
            canvas.drawBitmap(mCopyBitmap, null, mCopyRect, mPaint);
        }
    }

}
