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

package com.huawei.hms.videoeditor.ui.mediaeditor.cover;

import static com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil.dp2px;

import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEThumbnailCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.BitmapUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ThumbNailMemoryCache;

import androidx.annotation.Nullable;

public class CoverTrackView extends View {
    private static final String TAG = "CoverTrackView";

    private Paint paint;

    protected int imageWidth = dp2px(64);

    private int maxWidth = 0;

    private HVEAsset asset;

    protected List<String> realPathList = new Vector<>();

    protected List<String> trimInPathList = new Vector<>();

    protected List<String> trimOutPathList = new Vector<>();

    private boolean isDrawHistory = true;

    protected long currentTime = 0;

    protected int startIndex = 0;

    protected int endIndex = 10;

    private long lastDrawTime = 0;

    private Bitmap lastBitmap = null;

    private Bitmap imageAssetBitmap;

    private final Rect rectF = new Rect(0, 0, imageWidth, imageWidth);

    private Handler mHandler = new Handler();

    Runnable mCheckReDraw = this::checkReDraw;

    Runnable mReDrawBitmap = this::reDrawBitmap;

    public CoverTrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.onInitializeImageTrackView(context, attrs);
    }

    public CoverTrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.onInitializeImageTrackView(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(maxWidth, imageWidth);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmaps(canvas);
    }

    // 绘制缩略图
    private void drawBitmaps(Canvas canvas) {
        int lastDrawX = startIndex * imageWidth;
        try {
            if (asset instanceof HVEVideoAsset) {
                for (int i = startIndex; i < endIndex; i++) {
                    int index = (int) Math.ceil(BigDecimalUtils.mul(i, 10));
                    if (index >= realPathList.size()) {
                        index = realPathList.size() - 1;
                    }

                    if (BigDecimalUtils.compareTo(lastDrawX + imageWidth, maxWidth)) {
                        // 最后一帧显示不全裁剪显示
                        int width = (int) Math.floor(maxWidth - lastDrawX);
                        if (width <= 0) {
                            break;
                        }
                        Rect rect = new Rect(lastDrawX, 0, lastDrawX + width, imageWidth);
                        Rect rectF1 = new Rect(0, 0, width, imageWidth);
                        canvas.drawBitmap(getCutEndBitmap(getDrawBitmap(realPathList.get(index)), width), rectF1, rect,
                            paint);
                        break;
                    }

                    Rect rect = new Rect(lastDrawX, 0, lastDrawX + imageWidth, imageWidth);
                    canvas.drawBitmap(getDrawBitmap(realPathList.get(index)), rectF, rect, paint);
                    lastDrawX += imageWidth;
                }
            } else if (imageAssetBitmap != null && !imageAssetBitmap.isRecycled()) {
                for (int i = startIndex; i < endIndex; i++) {
                    if (BigDecimalUtils.compareTo(lastDrawX + imageWidth, maxWidth)) {
                        // 最后一帧显示不全裁剪显示
                        int width = (int) Math.floor(maxWidth - lastDrawX);
                        if (width <= 0) {
                            break;
                        }
                        Rect rect = new Rect(lastDrawX, 0, lastDrawX + width, imageWidth);
                        Rect rectF1 = new Rect(0, 0, width, imageWidth);
                        canvas.drawBitmap(getCutEndBitmap(imageAssetBitmap, width), rectF1, rect, paint);
                        break;
                    }
                    Rect rect = new Rect(lastDrawX, 0, lastDrawX + imageWidth, imageWidth);
                    canvas.drawBitmap(imageAssetBitmap, rectF, rect, paint);
                    lastDrawX += imageWidth;
                }
            }
            lastDrawTime = currentTime;
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
            SmartLog.e(TAG, "onDraw:  " + e.getMessage());
        }
    }

    private Bitmap getDrawBitmap(String path) {
        Bitmap bitmap = ThumbNailMemoryCache.getInstance().getCache(path, imageWidth);
        if (bitmap.getWidth() != imageWidth) {
            bitmap = getScaleBitmap(bitmap);
        }
        if (bitmap != null) {
            lastBitmap = bitmap;
        }
        return lastBitmap;
    }

    /**
     * 按目标宽高获取缩放后的bitmap
     *
     * @param bitmap 原始缩略图
     * @return 缩放后的缩略图
     */
    private Bitmap getScaleBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = imageWidth;
        int newHeight = imageWidth;
        float sWidth = ((float) newWidth) / width;
        float sHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(sWidth, sHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    /**
     * 裁剪bitmap的结束
     */
    private Bitmap getCutEndBitmap(Bitmap bitmap, int cutWidth) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, cutWidth, bitmap.getHeight(), null, false);
        return newBitmap;
    }

    private void onInitializeImageTrackView(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        maxWidth = ScreenUtil.getScreenWidth(context) - dp2px(36);
    }

    protected double getImageCount() {
        return BigDecimalUtils.div(maxWidth, imageWidth);
    }

    public double getTrimInImageCount() {
        return BigDecimalUtils.div(BigDecimalUtils.div(asset.getTrimIn(), asset.getSpeed()), 100);
    }

    public void prepareFileList(String filePath) {
        // 封面缩略图不可裁剪，但list装填逻辑与主泳道的保持一致
        if (trimInPathList.size() < getTrimInImageCount()) {
            trimInPathList.add(filePath);
        } else if (realPathList.size() < getMaxImageCount()) {
            realPathList.add(filePath);
        } else {
            trimOutPathList.add(filePath);
        }
    }

    public void getThumbNail() {
        if (asset == null) {
            return;
        }

        if (asset instanceof HVEVideoAsset) {
            HVEThumbnailCallback imageCallback = new HVEThumbnailCallback() {
                @Override
                public void onImageAvailable(Bitmap bitmap, long timeStamp) {
                }

                @Override
                public void onImagePathAvailable(String filePath, long timeStamp) {
                    prepareFileList(filePath);
                    // api小于24,view post内存泄漏; api大于24，view post必须在add view之后才能执行run
                    // 用handler替换view post来线程通信
                    if (mHandler != null) {
                        mHandler.post(mCheckReDraw);
                    }
                }

                @Override
                public void onSuccess() {
                    if (asset == null || realPathList == null) {
                        return;
                    }
                    SmartLog.d(TAG, "cover asset.getDuration = " + asset.getDuration());
                    SmartLog.d(TAG, "cover real path list = " + realPathList.size());
                }

                @Override
                public void onFail(String errorCode, Exception e) {

                }
            };

            ((HVEVideoAsset) asset).getThumbNail(dp2px(48), dp2px(48), 0, (int) (100 * asset.getSpeed()), 0,
                asset.getOriginLength(), true, imageCallback);
        } else if (asset instanceof HVEImageAsset) {
            HVEImageAsset imageAsset = (HVEImageAsset) asset;
            HVEThumbnailCallback imageCallback = new HVEThumbnailCallback() {
                @Override
                public void onImageAvailable(Bitmap bitmap, long timeStamp) {
                    if (imageAssetBitmap == null || imageAssetBitmap.isRecycled()) {
                        imageAssetBitmap = BitmapUtils.centerSquareScaleBitmap(bitmap, imageWidth);
                        bitmap.recycle();
                        postInvalidate();
                    } else {
                        bitmap.recycle();
                    }
                }

                @Override
                public void onImagePathAvailable(String fielPath, long timeStamp) {
                }

                @Override
                public void onSuccess() {
                    reDrawBitmap();
                }

                @Override
                public void onFail(String errorCode, Exception e) {

                }
            };
            imageAsset.getThumbNail(imageWidth, imageWidth, 0, imageAsset.getDuration(), imageCallback);
        }
    }

    public double getMaxImageCount() {
        return BigDecimalUtils.div(asset.getDuration(), 100);
    }

    private void checkReDraw() {
        if (isDrawHistory && (realPathList.size() == 1 || endIndex * 10 <= realPathList.size()) && mHandler != null) {
            mHandler.post(mReDrawBitmap);
        }
    }

    private void reDrawBitmap() {
        SmartLog.i("reDrawBitmap", "xxxxxxxxx ");
        if (asset == null) {
            return;
        }

        long realCurrentTime = currentTime - asset.getStartTime();
        double startTime = realCurrentTime - 5000;
        double endTime = realCurrentTime + 5000;
        if (startTime < 0) {
            startTime = 0;
        }
        if (endTime > asset.getDuration()) {
            endTime = asset.getDuration();
        }
        if (startTime == 0) {
            startIndex = 0;
        } else {
            startIndex = (int) Math
                .floor(BigDecimalUtils.div(BigDecimalUtils.mul(getImageCount(), startTime), asset.getDuration()));
        }
        if (endTime == 0) {
            return;
        }
        endIndex =
            (int) Math.ceil(BigDecimalUtils.div(BigDecimalUtils.mul(getImageCount(), endTime), asset.getDuration()));
        isDrawHistory = endIndex * 10 >= realPathList.size();
        SmartLog.i(TAG, "getIndex start: " + startIndex + "  end: " + endIndex);
        postInvalidate();
    }

    public void handleCurrentTimeChange(long time) {
        currentTime = time;
        if (lastDrawTime == 0 || Math.abs(lastDrawTime - currentTime) > 2000) {
            post(this::reDrawBitmap);
        } else {
            SmartLog.i("handleCurrentTimeChange", ": " + lastDrawTime + " currentTime: " + currentTime);
        }
    }

    public void setAsset(HVEAsset asset) {
        this.asset = asset;
        maxWidth = (int) BigDecimalUtils
            .round(BigDecimalUtils.mul(BigDecimalUtils.div(asset.getDuration(), 1000), imageWidth), 0);
        getThumbNail();
    }
}
