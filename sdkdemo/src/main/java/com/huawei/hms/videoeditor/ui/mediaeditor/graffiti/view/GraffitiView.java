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

package com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.common.agc.HVEApplication;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.DrawPath;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.DrawingListChangedListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.EarserChangedListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.GraffitiInfo;

public class GraffitiView extends View {
    private static final String TAG = "GraffitiView";

    private Paint paint;

    private Path path;

    private float downX;

    private float downY;

    private float tempX;

    private float tempY;

    private int paintWidth = 10;

    private final List<DrawPath> drawPathList;

    private final List<DrawPath> savePathList;

    private int mStokeAlpha = 255;

    private int mStokeColor = 0xFFFFFFFF;

    private DrawingListChangedListener mDrawingListChangedListener;

    private DrawPath.Position mPosition;

    private EarserChangedListener mEarserChangedListener;

    private HuaweiVideoEditor editor;

    public GraffitiView(Context context) {
        this(context, null);
    }

    public GraffitiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraffitiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        drawPathList = new ArrayList<>();
        savePathList = new ArrayList<>();
        initPaint();
    }

    public void setEditor(HuaweiVideoEditor editor) {
        this.editor = editor;
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(paintWidth);
        paint.setColor(mStokeColor);
        paint.setAlpha(mStokeAlpha);
        paint.setStyle(Paint.Style.STROKE);
        if (mEarserChangedListener != null) {
            mEarserChangedListener.onChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDetail(canvas);

        if (mDrawingListChangedListener != null && drawPathList != null && savePathList != null) {
            mDrawingListChangedListener.onChanged(drawPathList.size(), savePathList.size());
        }
    }

    private void drawDetail(Canvas canvas) {
        if (drawPathList != null && !drawPathList.isEmpty()) {
            for (DrawPath aDrawPath : drawPathList) {
                if (aDrawPath.shape == GraffitiInfo.Shape.LINE || aDrawPath.shape == GraffitiInfo.Shape.FIVESTAR
                    || aDrawPath.shape == GraffitiInfo.Shape.ARROW || aDrawPath.shape == GraffitiInfo.Shape.DIAMOND
                    || aDrawPath.shape == GraffitiInfo.Shape.TRIANGLE) {
                    if (aDrawPath.path != null) {
                        canvas.drawPath(aDrawPath.path, aDrawPath.paint);
                    }
                } else if (aDrawPath.shape == GraffitiInfo.Shape.STRACTLINE) {
                    if (aDrawPath.position.endY == 0 || aDrawPath.position.endX == 0) {
                        return;
                    }
                    canvas.drawLine(aDrawPath.position.startX, aDrawPath.position.startY, aDrawPath.position.endX,
                        aDrawPath.position.endY, aDrawPath.paint);
                } else if (aDrawPath.shape == GraffitiInfo.Shape.CIRCLE) {
                    if (aDrawPath.position.endY == 0 || aDrawPath.position.endX == 0) {
                        return;
                    }
                    float radius = (float) ((Math.sqrt((aDrawPath.position.endX - aDrawPath.position.startX)
                        * (aDrawPath.position.endX - aDrawPath.position.startX)
                        + (aDrawPath.position.endY - aDrawPath.position.startY)
                            * (aDrawPath.position.endY - aDrawPath.position.startY)))
                        / 2);
                    canvas.drawCircle((aDrawPath.position.startX + aDrawPath.position.endX) / 2,
                        (aDrawPath.position.startY + aDrawPath.position.endY) / 2, radius, aDrawPath.paint);
                } else if (aDrawPath.shape == GraffitiInfo.Shape.RECTANGLE) {
                    if (aDrawPath.position.endY == 0 || aDrawPath.position.endX == 0) {
                        return;
                    }
                    canvas.drawRect(aDrawPath.position.startX, aDrawPath.position.startY, aDrawPath.position.endX,
                        aDrawPath.position.endY, aDrawPath.paint);
                }
            }
        }
    }

    public void drawPentagon(float x0, float y0, float r, Path path) {
        double ch = 72 * Math.PI / 180;
        float x1 = x0;
        float x2 = (float) (x0 - Math.sin(ch) * r);
        float x3 = (float) (x0 + Math.sin(ch) * r);
        float x4 = (float) (x0 - Math.sin(ch / 2) * r);
        float x5 = (float) (x0 + Math.sin(ch / 2) * r);
        float y1 = y0 - r;
        float y2 = (float) (y0 - Math.cos(ch) * r);
        float y3 = y2;
        float y4 = (float) (y0 + Math.cos(ch / 2) * r);
        float y5 = y4;
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x4, y4);
        path.lineTo(x5, y5);
        path.lineTo(x3, y3);
        path.close();
    }

    DrawPath drawPath;

    float moveX;

    float moveY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                path = new Path();
                drawPath = new DrawPath();
                mPosition = new DrawPath.Position();
                mPosition.startX = downX;
                mPosition.startY = downY;
                drawPath.paint = paint;
                drawPath.path = path;
                drawPath.shape = mShape;
                drawPath.position = mPosition;
                drawPathList.add(drawPath);
                if (drawPath.shape == GraffitiInfo.Shape.LINE) {
                    path.moveTo(downX, downY);
                }
                invalidate();
                tempX = downX;
                tempY = downY;
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                if (drawPath.shape == GraffitiInfo.Shape.LINE) {
                    path.quadTo(tempX, tempY, moveX, moveY);
                }
                mPosition.endX = moveX;
                mPosition.endY = moveY;
                invalidate();
                tempX = moveX;
                tempY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                if (drawPath.shape == GraffitiInfo.Shape.TRIANGLE) {
                    path.moveTo(downX, downY);
                    path.quadTo(tempX, tempY, moveX, moveY);
                    path.quadTo(tempX, tempY, tempX + Math.abs(downX - tempX) * 2, tempY);
                    path.close();
                } else if (drawPath.shape == GraffitiInfo.Shape.FIVESTAR) {
                    drawFiveStar(
                        fivePoints(downX, downY, (float) hypotenuse(Math.abs(downX - tempX), Math.abs(downY - tempY))),
                        path);
                } else if (drawPath.shape == GraffitiInfo.Shape.DIAMOND) {
                    drawPentagon(downX, downY, Math.abs(downY - tempY), path);
                } else if (drawPath.shape == GraffitiInfo.Shape.ARROW) {
                    drawArrow(downX, downY, tempX, tempY, paintWidth, path);
                }
                initPaint();
                break;
            default:
                SmartLog.i(TAG, "onTouch run in default case");
        }

        return true;
    }

    public double hypotenuse(double a, double b) {
        return Math.sqrt(a * a + b * b);

    }

    public static float[] fivePoints(float xA, float yA, float rFive) {
        float xB = 0;
        float xC = 0;
        float xD = 0;
        float xE = 0;
        float yB = 0;
        float yC = 0;
        float yD = 0;
        float yE = 0;

        xD = (float) (xA - rFive * Math.sin(Math.toRadians(18)));
        xC = (float) (xA + rFive * Math.sin(Math.toRadians(18)));
        yD = yC = (float) (yA + Math.cos(Math.toRadians(18)) * rFive);
        yB = yE = (float) (yA + Math.sqrt(Math.pow((xC - xD), 2) - Math.pow((rFive / 2), 2)));
        xB = xA + (rFive / 2);
        xE = xA - (rFive / 2);
        float[] floats = new float[] {xA, yA, xD, yD, xB, yB, xE, yE, xC, yC, xA, yA};
        return floats;
    }

    private void drawFiveStar(float[] floatX, Path path) {
        path.moveTo(floatX[0], floatX[1]);
        path.lineTo(floatX[2], floatX[3]);
        path.lineTo(floatX[4], floatX[5]);
        path.lineTo(floatX[6], floatX[7]);
        path.lineTo(floatX[8], floatX[9]);
        path.close();
    }

    private void drawArrow(float sx, float sy, float ex, float ey, int width, Path path) {
        int size = 5;
        int count = 20;
        switch (width) {
            case 0:
                size = 5;
                count = 20;
                break;
            case 5:
                size = 8;
                count = 30;
                break;
            case 10:
                size = 11;
                count = 40;
                break;
            default:
                SmartLog.i(TAG, "drawArrow run in default case");
        }
        float aX = ex - sx;
        float aY = ey - sy;
        double d = aX * aX + aY * aY;
        double r = Math.sqrt(d);
        float zx = (float) (ex - (count * aX / r));
        float zy = (float) (ey - (count * aY / r));
        float xz = zx - sx;
        float yz = zy - sy;
        double zd = xz * xz + yz * yz;
        double zr = Math.sqrt(zd);
        path.moveTo(sx, sy);
        path.lineTo((float) (zx + size * yz / zr), (float) (zy - size * xz / zr));
        path.lineTo((float) (zx + size * 2 * yz / zr), (float) (zy - size * 2 * xz / zr));
        path.lineTo(ex, ey);
        path.lineTo((float) (zx - size * 2 * yz / zr), (float) (zy + size * 2 * xz / zr));
        path.lineTo((float) (zx - size * yz / zr), (float) (zy + size * xz / zr));
        path.close();

    }

    public int getDrawSize() {
        if (drawPathList != null) {
            return drawPathList.size();
        }
        return 0;
    }

    public void resetPaintColor(int color) {
        mStokeColor = color;
        paint.setColor(mStokeColor);
    }

    GraffitiInfo.Shape mShape = GraffitiInfo.Shape.LINE;

    public void resetPaintShape(GraffitiInfo.Shape se) {
        mShape = se;
    }

    public void resetPaintWidth(int width) {
        paintWidth = width;
        paint.setStrokeWidth(paintWidth);
    }

    public void eraser() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mShape = GraffitiInfo.Shape.LINE;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setColor(Color.TRANSPARENT);
        paint.setStrokeWidth(paintWidth + 6);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = editor.getCanvasWidth();
        int height = editor.getCanvasHeight();
        Log.i(TAG, "onMeasure: width:" + width + "height:" + height);
        setMeasuredDimension(width, height);
    }

    public void resetAlpha(int stokeAlpha) {
        mStokeAlpha = stokeAlpha;
        paint.setAlpha(mStokeAlpha);
    }

    public void clear() {
        drawPathList.clear();
        savePathList.clear();
        invalidate();
    }

    public void setDrawingListChangedListener(DrawingListChangedListener drawingListChangedListener) {
        mDrawingListChangedListener = drawingListChangedListener;
    }

    public void setEarserChangedListener(EarserChangedListener listener) {
        mEarserChangedListener = listener;
    }

    public String saveBitmap() {
        String fraffiFilePath = "";
        try {
            File dir = new File(HVEApplication.getInstance().getAppContext().getFilesDir().getCanonicalFile()
                + File.separator + "doodleview");
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    SmartLog.e(TAG, "fail to make dir file");
                }
            }
            fraffiFilePath = dir + File.separator + System.currentTimeMillis() + ".graffi";
            File fraffiFile = new File(fraffiFilePath);
            if (!fraffiFile.exists()) {
                if (!fraffiFile.getParentFile().mkdir()) {
                    SmartLog.e(TAG, "fail to make dir fraffi file");
                }
            }
            savePicByPNG(this.getBitmap(), fraffiFilePath);
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
            fraffiFilePath = "";
        }
        return fraffiFilePath;
    }

    public Bitmap getBitmap() {
        Bitmap mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        doDraw(canvas, mBitmap);
        return mBitmap;
    }

    private void doDraw(Canvas canvas, Bitmap bitmap) {
        canvas.drawColor(Color.TRANSPARENT);
        drawDetail(canvas);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    public static void savePicByPNG(Bitmap bitmap, String filePath) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    SmartLog.e(TAG, e.getMessage());
                }
            }
        }
    }
}
