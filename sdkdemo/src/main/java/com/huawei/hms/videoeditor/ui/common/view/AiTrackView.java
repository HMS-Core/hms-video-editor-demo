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

package com.huawei.hms.videoeditor.ui.common.view;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEThumbnailCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.BitmapDecodeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;

import androidx.annotation.Nullable;

public class AiTrackView extends View {
    private static final String TAG = "AiTrackView";

    private Paint paint;

    protected int imageWidth = ScreenUtil.dp2px(67);

    private int maxWidth = 0;

    private List<HVEAsset> assets;

    private List<Bitmap> bitmaps = new Vector<>();

    private int thumbIndex = -1;

    private int mTimeSpace;

    private int callbackSize = 0;

    private Bitmap imageAssetBitmap;

    private Bitmap lastBitmap;

    public AiTrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.onInitializeImageTrackView(context, attrs);
    }

    public AiTrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.onInitializeImageTrackView(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(maxWidth, ScreenUtil.dp2px(67));
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
            for (Bitmap bitmap : bitmaps) {
                canvas.drawBitmap(bitmap, lastDrawX, 0, paint);
                lastDrawX += bitmap.getWidth();
            }
            if (lastBitmap == null || lastBitmap.isRecycled()) {
                return;
            }
            while (lastDrawX < maxWidth) {
                if (BigDecimalUtils.compareTo(lastDrawX + imageWidth, maxWidth)) {
                    int width = (int) Math.floor(maxWidth - lastDrawX);
                    if (width <= 0) {
                        break;
                    }
                    canvas.drawBitmap(getCutEndBitmap(lastBitmap, width), lastDrawX, 0, paint);
                    lastDrawX += lastBitmap.getWidth();
                    break;
                }
                canvas.drawBitmap(lastBitmap, lastDrawX, 0, paint);
                lastDrawX += lastBitmap.getWidth();
            }

            SmartLog.i(TAG, "drawBitmaps while is complete!");
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
            SmartLog.e(TAG, "onDraw:  " + e.getMessage());
        }
    }

    private void onInitializeImageTrackView(Context context, AttributeSet attrs) {
        if (ScreenUtil.isRTL()) {
            setScaleX(RTL_UI);
        } else {
            setScaleX(LTR_UI);
        }
        paint = new Paint();
        paint.setAntiAlias(true);
        maxWidth = ScreenUtil.getScreenWidth(context) - ScreenUtil.dp2px(36);
    }

    protected double getImageCount() {
        return BigDecimalUtils.div(maxWidth, imageWidth);
    }

    public void getThumbNail(HVEAsset asset) {
        double thumbCount = getThumbMAxSize(asset);
        SmartLog.i(TAG, "getThumbNail start index:" + asset.getIndex());
        callbackSize = 0;
        HVEThumbnailCallback callback = new HVEThumbnailCallback() {
            @Override
            public void onImageAvailable(Bitmap bitmap, long timeStamp) {
                imageAssetBitmap = bitmap;
                if (callbackSize == -1) {
                    return;
                }
                if (bitmap == null) {
                    SmartLog.e(TAG, "image  bitmap is null!");
                    return;
                }
                callbackSize += 1;
                if (BigDecimalUtils.compareTo(callbackSize, thumbCount)) {
                    addBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, false),
                        BigDecimalUtils.mul(imageWidth, BigDecimalUtils.sub(thumbCount, callbackSize - 1)));
                    callbackSize = -1;
                } else {
                    addBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, false), imageWidth);
                }
            }

            @Override
            public void onImagePathAvailable(String filePath, long timeStamp) {
                if (callbackSize == -1) {
                    return;
                }
                if (StringUtil.isEmpty(filePath)) {
                    SmartLog.e(TAG, "video  bitmap is null!");
                    return;
                }
                callbackSize += 1;
                if (BigDecimalUtils.compareTo(callbackSize, thumbCount)) {
                    addBitmap(BitmapDecodeUtils.decodeFile(filePath),
                        BigDecimalUtils.mul(imageWidth, BigDecimalUtils.sub(thumbCount, callbackSize - 1)));
                    callbackSize = -1;
                } else {
                    addBitmap(BitmapDecodeUtils.decodeFile(filePath), imageWidth);
                }
            }

            @Override
            public void onSuccess() {
                SmartLog.i(TAG, "getThumbNail success!  index:" + asset.getIndex());
                post(() -> handleThumb());
                if (imageAssetBitmap != null && !imageAssetBitmap.isRecycled()) {
                    imageAssetBitmap.recycle();
                }
            }

            @Override
            public void onFail(String errorCode, Exception e) {

            }
        };

        if (asset instanceof HVEVideoAsset) {
            ((HVEVideoAsset) asset).getThumbNail(imageWidth, imageWidth, 0, mTimeSpace, 0, asset.getDuration(), true,
                callback);
        } else if (asset instanceof HVEImageAsset) {
            ((HVEImageAsset) asset).getThumbNail(imageWidth, imageWidth, 0, mTimeSpace, 0, asset.getDuration(),
                callback);
        }
    }

    private double getThumbMAxSize(HVEAsset asset) {
        return BigDecimalUtils.div(asset.getDuration(), mTimeSpace);
    }

    private void addBitmap(Bitmap bitmap, double width) {
        if (bitmap == null) {
            SmartLog.e(TAG, "addBitmap  bitmap is null!");
            return;
        }
        if (bitmap.getWidth() != imageWidth) {
            Matrix matrix = new Matrix();
            matrix.postScale(((float) imageWidth) / bitmap.getWidth(), ((float) imageWidth) / bitmap.getHeight());
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            bitmap.recycle();
            bitmap = newBitmap;
        }
        lastBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        int cutWidth = (int) BigDecimalUtils.round(width, 0);
        if (cutWidth == 0) {
            return;
        }
        if (cutWidth < bitmap.getWidth()) {
            bitmap = getCutBitmap(bitmap, cutWidth);
        }
        bitmaps.add(bitmap);
        postInvalidate();
    }

    private Bitmap getCutBitmap(Bitmap bitmap, int cutWidth) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, cutWidth, bitmap.getHeight(), null, false);
        bitmap.recycle();
        return newBitmap;
    }

    private Bitmap getCutEndBitmap(Bitmap bitmap, int cutWidth) {
        Bitmap newEndBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        newEndBitmap = Bitmap.createBitmap(newEndBitmap, 0, 0, cutWidth, bitmap.getHeight(), null, false);
        bitmaps.add(newEndBitmap);
        return newEndBitmap;
    }

    private void handleThumb() {
        thumbIndex++;
        if (thumbIndex >= assets.size()) {
            SmartLog.i(TAG, "getThumbNail  complete!");
            return;
        }
        getThumbNail(assets.get(thumbIndex));
    }

    public void init(HVEVideoLane videoLane) {
        this.assets = videoLane.getAssets();
        SmartLog.i(TAG, "assets  size: " + assets.size());
        mTimeSpace = (int) BigDecimalUtils.round(BigDecimalUtils.div(videoLane.getDuration(), getImageCount()), 0);
        bitmaps.clear();
        thumbIndex = -1;
        handleThumb();
    }

    public void destroy() {
        for (Bitmap bitmap : bitmaps) {
            bitmap.recycle();
        }
        if (lastBitmap != null && !lastBitmap.isRecycled()) {
            lastBitmap.recycle();
        }
    }
}
