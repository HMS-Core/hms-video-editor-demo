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

package com.huawei.hms.videoeditor.ui.mediaeditor.canvas;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class CanvasColor2Adapter extends RCommandAdapter<Integer> {
    private int mSelectPosition = -1;

    OnBlurSelectedListener blurSelectedListener;

    public CanvasColor2Adapter(Context context, List<Integer> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    protected void convert(RViewHolder holder, Integer integer, int dataPosition, int position) {
        View viewBg = holder.getView(R.id.bg_view_item_color);
        View viewColor = holder.getView(R.id.color_view_item_color);
        viewBg.setVisibility(mSelectPosition == position ? View.VISIBLE : View.GONE);
        viewColor.setBackgroundColor(integer);
        viewColor.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mSelectPosition != position) {
                if (mSelectPosition != -1) {
                    int lastp = mSelectPosition;
                    mSelectPosition = position;
                    notifyItemChanged(lastp);
                } else {
                    mSelectPosition = position;
                }
                notifyItemChanged(mSelectPosition);
                if (blurSelectedListener != null) {
                    blurSelectedListener.onBlurSelected(integer, position);
                }
            }
        }));
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
    }

    public int getSelectPosition() {
        return this.mSelectPosition;
    }

    public void setBlurSelectedListener(OnBlurSelectedListener blurSelectedListener) {
        this.blurSelectedListener = blurSelectedListener;
    }

    public interface OnBlurSelectedListener {
        void onBlurSelected(Integer blur, int position);
    }

}
