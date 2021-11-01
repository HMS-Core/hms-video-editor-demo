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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DoodleView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder = null;

    private BaseAction curAction = null;

    private int currentColor = Color.BLACK;

    private int currentSize = 5;

    private Paint mPaint;

    private List<BaseAction> mBaseActions;

    private Bitmap mBitmap;

    private ActionType mActionType = ActionType.Path;

    public DoodleView(Context context) {
        super(context);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        bringToFront();

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(currentSize);
        setSize(5);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
        mBaseActions = new ArrayList<>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        float touchX = event.getX();
        float touchY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setCurAction(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                Canvas canvas = mSurfaceHolder.lockCanvas();
                for (BaseAction baseAction : mBaseActions) {
                    baseAction.draw(canvas);
                }
                curAction.move(touchX, touchY);
                curAction.draw(canvas);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                break;
            case MotionEvent.ACTION_UP:
                mBaseActions.add(curAction);
                curAction = null;
                break;

            default:
                break;
        }
        return true;
    }

    private void setCurAction(float x, float y) {
        switch (mActionType) {
            case Point:
                curAction = new MyPoint(x, y, currentColor);
                break;
            case Path:
                curAction = new MyPath(x, y, currentSize, currentColor);
                break;
            case Line:
                curAction = new MyLine(x, y, currentSize, currentColor);
                break;
            case Rect:
                curAction = new MyRect(x, y, currentSize, currentColor);
                break;
            case Circle:
                curAction = new MyCircle(x, y, currentSize, currentColor);
                break;
            case FillEcRect:
                curAction = new MyFillRect(x, y, currentSize, currentColor);
                break;
            case FilledCircle:
                curAction = new MyFillCircle(x, y, currentSize, currentColor);
                break;
            default:
                break;
        }
    }

    public void setColor(String color) {
        this.currentColor = Color.parseColor(color);
    }

    public void setSize(int size) {
        this.currentSize = size;
    }

    public void setType(ActionType type) {
        this.mActionType = type;
    }

    public Bitmap getBitmap() {
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        doDraw(canvas);
        return mBitmap;
    }

    private void doDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        for (BaseAction action : mBaseActions) {
            action.draw(canvas);
        }
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    public void reset() {
        if (mBaseActions != null && mBaseActions.size() > 0) {
            mBaseActions.clear();
            Canvas canvas = mSurfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            for (BaseAction action : mBaseActions) {
                action.draw(canvas);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    enum ActionType {
        Point,
        Path,
        Line,
        Rect,
        Circle,
        FillEcRect,
        FilledCircle
    }
}