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

package com.huawei.hms.videoeditor.ui.mediaeditor.preview.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.ColorCutViewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class ColorCutoutView extends View implements Observer<Boolean> {
    private static final String TAG = "ColorCutoutView";

    private ColorCutViewModel colorCutViewModel;

    private List<HVEPosition2D> position2DS;

    private Path path;

    private Path pathWhite;

    private static final int R0 = 50; // dp

    private static final int CIRCLE_R_0 = ScreenUtil.dp2px(R0);

    private static final int R1 = 65; // dp

    private static final int CIRCLE_R_1 = ScreenUtil.dp2px(R1);

    private static final int RECT = 4;

    private static final int RECT_L = ScreenUtil.dp2px(RECT);

    private static final int STROKE_W = ScreenUtil.dp2px(2);

    private Paint paint;

    private CCOffset offset;

    private float CCX = 0;

    private float CCY = 0;

    private int color;

    private CCOffset videoOffset;

    private float assetViewWidth;

    private float assetViewHeight;

    private Matrix matrix;

    private Matrix convertMatrix;

    private Rect frameRect;

    private boolean isMoving;

    public ColorCutoutView(Context context) {
        super(context);
        init(context);
    }

    public ColorCutoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorCutoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ColorCutoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        matrix = new Matrix();
        convertMatrix = new Matrix();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewModelProvider.AndroidViewModelFactory mFactory =
            new ViewModelProvider.AndroidViewModelFactory(((Activity) context).getApplication());
        colorCutViewModel = new ViewModelProvider((ViewModelStoreOwner) context, mFactory).get(ColorCutViewModel.class);
        colorCutViewModel.getIsShow().observe((LifecycleOwner) context, this);
        videoOffset = new CCOffset();
        colorCutViewModel.getAsset().observe((LifecycleOwner) context, new Observer<HVEAsset>() {
            @Override
            public void onChanged(HVEAsset hveAsset) {
                if (hveAsset == null) {
                    return;
                }

                matrix.reset();
                path.reset();
                pathWhite.reset();
                offset.reset();
                if (!(hveAsset instanceof HVEVisibleAsset)) {
                    return;
                }

                HVEVisibleAsset hveEditAble = (HVEVisibleAsset) hveAsset;
                position2DS = hveEditAble.getRect();
                if (position2DS.size() < 4) {
                    return;
                }
                HVEPosition2D one2D = position2DS.get(1);
                HVEPosition2D three2D = position2DS.get(3);
                if (one2D == null || three2D == null) {
                    return;
                }
                CCX = (one2D.xPos + three2D.xPos) / 2;
                CCY = (one2D.yPos + three2D.yPos) / 2;
                path.setFillType(Path.FillType.EVEN_ODD);
                path.addCircle(CCX, CCY, CIRCLE_R_0, Path.Direction.CCW);
                path.addCircle(CCX, CCY, CIRCLE_R_1, Path.Direction.CCW);
                path.addRect(CCX - RECT_L, CCY - RECT_L, CCX + RECT_L, CCY + RECT_L, Path.Direction.CCW);

                pathWhite.setFillType(Path.FillType.EVEN_ODD);
                pathWhite.addCircle(CCX, CCY, CIRCLE_R_0 - 3, Path.Direction.CCW);
                pathWhite.addCircle(CCX, CCY, CIRCLE_R_1 - 3, Path.Direction.CCW);

                matrix.postTranslate(one2D.xPos, one2D.yPos);
                videoOffset.x = one2D.xPos;
                videoOffset.y = one2D.yPos;
                assetViewWidth = hveEditAble.getSize().width;
                assetViewHeight = hveEditAble.getSize().height;
                float rotate = ((HVEVisibleAsset) hveAsset).getRotation();
                matrix.postRotate(-rotate, videoOffset.x, videoOffset.y);
                matrix.invert(convertMatrix);
                frameRect.left = 0;
                frameRect.top = 0;
                frameRect.right = (int) assetViewWidth;
                frameRect.bottom = (int) assetViewHeight;

                getColorByPosition((float) assetViewWidth / 2, (float) assetViewHeight / 2);
                invalidate();
            }
        });
        path = new Path();
        pathWhite = new Path();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(STROKE_W);
        paint.setAntiAlias(true);
        offset = new CCOffset();
        frameRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        canvas.save();
        canvas.translate(offset.x, offset.y);
        canvas.clipPath(pathWhite);
        canvas.drawPath(pathWhite, paint);
        canvas.drawColor(color);
        canvas.restore();

        canvas.save();
        canvas.translate(offset.x, offset.y);
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    private float oldX;

    private float oldY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (isInCircle(x, y)) {
                    oldX = x;
                    oldY = y;
                    isMoving = true;
                } else {
                    isMoving = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMoving) {
                    offset.x += event.getX() - oldX;
                    offset.y += event.getY() - oldY;
                    oldX = event.getX();
                    oldY = event.getY();

                    float[] p = new float[2];
                    p[0] = CCX;
                    p[1] = CCY;

                    float[] pv = new float[2];
                    pv[0] = offset.x;
                    pv[1] = offset.y;

                    colorCutViewModel.setMove(true);
                    convertMatrix.mapPoints(p);
                    convertMatrix.mapVectors(pv);

                    CCOffset o = measureOffsetSafe(new Point((int) (p[0]), (int) (p[1])), (int) (pv[0]), (int) (pv[1]),
                        frameRect);
                    getColorByPosition(p[0] + o.x, p[1] + o.y);

                    pv[0] = o.x;
                    pv[1] = o.y;
                    matrix.mapVectors(pv);

                    offset.x = pv[0];
                    offset.y = pv[1];
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    public static class CCOffset {
        float x = 0;

        float y = 0;

        public CCOffset(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public CCOffset() {
        }

        public void reset() {
            x = 0;
            y = 0;
        }
    }

    @Override
    public void onChanged(Boolean aBoolean) {
        if (aBoolean) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    private boolean isInCircle(float x, float y) {
        return Float.compare(spacing(x - offset.x, y - offset.y, CCX, CCY), CIRCLE_R_1) <= 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    private float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    private void getColorByPosition(float x, float y) {
        HVEAsset hveAsset = colorCutViewModel.getAsset().getValue();
        if (hveAsset == null) {
            return;
        }

        if (hveAsset instanceof HVEImageAsset) {
            color = handlePoint(x, y, ((HVEImageAsset) hveAsset).getBitmap());
            colorCutViewModel.setEffectColor(color);
        } else {
            HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
            if (editor == null || editor.getTimeLine() == null) {
                return;
            }

            editor.getBitmapAtSelectedLan(hveAsset.getLaneIndex(), editor.getTimeLine().getCurrentTime(),
                new HuaweiVideoEditor.ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, long time) {
                        color = handlePoint(x, y, bitmap);
                        colorCutViewModel.setEffectColor(color);
                        invalidate();
                    }

                    @Override
                    public void onFail(int errorCode) {
                    }
                });
        }
    }

    private int handlePoint(float x, float y, Bitmap bitmap) {
        if (bitmap == null) {
            SmartLog.e(TAG, "handlePoint bitmap is null");
            return 0;
        }

        int lastX = (int) (x * bitmap.getWidth() / assetViewWidth);
        int lastY = (int) (y * bitmap.getHeight() / assetViewHeight);

        if (lastX > bitmap.getWidth() - 1) {
            lastX = bitmap.getWidth() - 1;
        }
        if (lastX < 0) {
            lastX = 0;
        }
        if (lastY > bitmap.getHeight() - 1) {
            lastY = bitmap.getHeight() - 1;
        }
        if (lastY < 0) {
            lastY = 0;
        }
        return bitmap.getPixel(lastX, lastY);
    }

    protected CCOffset measureOffsetSafe(Point point, int offX, int offY, Rect rect) {
        point.offset(offX, offY);
        if (isContainers(rect, point)) {
            return new CCOffset(offX, offY);
        }
        CCOffset aOffset = new CCOffset(0, 0);
        if (offX != 0) {
            boolean isInX = isInValue(point.x, rect.left, rect.right);
            if (!isInX) {
                if (Math.abs(point.x - rect.left) > Math.abs(point.x - rect.right)) {
                    aOffset.x = offX - point.x + rect.right;
                } else {
                    aOffset.x = offX - point.x + rect.left;
                }
            } else {
                aOffset.x = offX;
            }
        }
        if (offY != 0) {
            boolean isInY = isInValue(point.y, rect.top, rect.bottom);
            if (!isInY) {
                if (Math.abs(point.y - rect.top) > Math.abs(point.y - rect.bottom)) {
                    aOffset.y = offY - point.y + rect.bottom;
                } else {
                    aOffset.y = offY - point.y + rect.top;
                }
            } else {
                aOffset.y = offY;
            }
        }
        return aOffset;
    }

    protected boolean isContainers(Rect rect, Point point) {
        return isContainers(rect.left, rect.right, rect.top, rect.bottom, point.x, point.y);
    }

    protected boolean isContainers(int l, int r, int t, int b, int x, int y) {
        if ((l < x && r < x) || (t < y && b < y) || (l > x && r > x) || (t > y && b > y)) {
            return false;
        }
        return true;
    }

    protected boolean isInValue(int value, int l1, int l2) {
        if ((value > l1 && value > l2) || (value < l1 && value < l2)) {
            return false;
        }
        return true;
    }
}
