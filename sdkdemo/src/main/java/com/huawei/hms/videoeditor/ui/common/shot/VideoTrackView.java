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

package com.huawei.hms.videoeditor.ui.common.shot;

import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtil;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;

import androidx.annotation.Nullable;

public class VideoTrackView extends View {
    private static final String TAG = "VideoTrackView";

    private Paint paint;

    protected int imageWidth = ScreenUtil.dp2px(48);

    private int maxWidth = 0;

    private HVEVideoAsset asset;

    protected List<Bitmap> bitmaps = new Vector<>();

    public VideoTrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.onInitializeImageTrackView(context, attrs);
    }

    public VideoTrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.onInitializeImageTrackView(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(maxWidth, ScreenUtil.dp2px(48));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmaps(canvas);
    }

    private void drawBitmaps(Canvas canvas) {
        int lastDrawX = 0;
        try {
            for (int i = 0; i <= bitmaps.size(); i++) {
                if (BigDecimalUtils.compareTo(lastDrawX + imageWidth, maxWidth)) {
                    int width = (int) Math.floor(maxWidth - lastDrawX);
                    if (width <= 0) {
                        break;
                    }
                    canvas.drawBitmap(getCutEndBitmap(bitmaps.get(i), width), lastDrawX, 0, paint);
                    break;
                } else {

                    canvas.drawBitmap(bitmaps.get(i), lastDrawX, 0, paint);
                    lastDrawX += imageWidth;

                }
            }
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
        }
    }

    private Bitmap getCutEndBitmap(Bitmap bitmap, int cutWidth) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, cutWidth, bitmap.getHeight(), null, false);
        return newBitmap;
    }

    private void onInitializeImageTrackView(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        maxWidth = ScreenUtil.getScreenWidth(context) - ScreenUtil.dp2px(36);
    }

    protected double getImageCount() {
        return BigDecimalUtils.div(maxWidth, imageWidth);
    }

    public void getThumbNail() {
        bitmaps.clear();
        HVEUtil.getThumbnails(asset.getPath(),
            (long) Math.floor(BigDecimalUtil.div(asset.getOriginLength(), getImageCount())), imageWidth, imageWidth,
            new HVEUtil.HVEThumbnailCallback() {

                @Override
                public void onBitmap(Bitmap bitmap) {
                    bitmaps.add(bitmap);
                    postInvalidate();
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(String errorCode, String errMsg) {

                }
            });
    }

    public void setVideoAsset(HVEVideoAsset asset) {
        this.asset = asset;
        getThumbNail();
    }

}
