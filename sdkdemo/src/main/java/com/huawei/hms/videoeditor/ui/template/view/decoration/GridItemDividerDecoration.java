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

package com.huawei.hms.videoeditor.ui.template.view.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class GridItemDividerDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;

    private int dividerWidth;

    private int mDividerHeight;

    public GridItemDividerDecoration(int height) {
        dividerWidth = height;
        mDividerHeight = height;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public GridItemDividerDecoration(int height, @ColorInt int color) {
        dividerWidth = height;
        mDividerHeight = height;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public GridItemDividerDecoration(int width, int height, @ColorInt int color) {
        dividerWidth = width;
        mDividerHeight = height;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = getSpanCount(parent);
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }

        int childCount = adapter.getItemCount();
        boolean isLastRow = isLastRow(parent, itemPosition, spanCount, childCount);

        int top = 0;
        int left;
        int right;
        int bottom;
        int eachWidth = (spanCount - 1) * dividerWidth / spanCount;
        int dl = dividerWidth - eachWidth;

        left = itemPosition % spanCount * dl;
        right = eachWidth - left;
        bottom = mDividerHeight;
        if (isLastRow) {
            bottom = 0;
        }
        outRect.set(left, top, right, bottom);

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        draw(c, parent);
    }

    private void draw(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            boolean isLastRow = isLastRow(parent, i, getSpanCount(parent), childCount);
            int left = child.getLeft();
            int right = child.getRight();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDividerHeight;
            if (isLastRow) {
                bottom = top;
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
            top = child.getTop();
            bottom = child.getBottom() + mDividerHeight;
            left = child.getRight() + layoutParams.getMarginEnd();
            right = left + dividerWidth;
            if (isLastRow) {
                bottom = child.getBottom();
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager parentLayoutManager = parent.getLayoutManager();
        if (parentLayoutManager instanceof GridLayoutManager) {
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            return lines == pos / spanCount + 1;
        } else if (parentLayoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) parentLayoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount) {
                    return true;
                }
            } else {
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) manager).getSpanCount();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) manager).getSpanCount();
        }
        return spanCount;
    }
}
