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

package com.huawei.hms.videoeditor.ui.mediaeditor.graffiti;

import java.util.List;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.utils.SharedPreferencesUtils;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class GraffitiColorAdapter extends RCommandAdapter<Integer> {
    private int selectPosition = -1;

    private EditPreviewViewModel viewModel;

    private Context context;

    public GraffitiColorAdapter(Context context, List<Integer> list, int layoutId,
        EditPreviewViewModel textEditViewModel) {
        super(context, list, layoutId);
        this.context = context;
        this.viewModel = textEditViewModel;
    }

    @Override
    protected void convert(RViewHolder holder, Integer integer, int dataPosition, int position) {
        holder.itemView.setBackgroundColor(integer);
        View viewColor = holder.getView(R.id.color_view_item_color);
        View bgView = holder.getView(R.id.bg_view_item_color);
        if (position == 1) {
            bgView.setVisibility(View.VISIBLE);
        }
        viewColor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (selectPosition != position) {
                    if (selectPosition != -1) {
                        int lastp = selectPosition;
                        selectPosition = position;
                        notifyItemChanged(lastp);
                    } else {
                        selectPosition = position;
                    }
                    notifyItemChanged(selectPosition);
                    SharedPreferencesUtils.getInstance()
                        .putIntValue(context, SharedPreferencesUtils.COLOR_SELECT_INDEX, selectPosition);
                }
                return false;
            }
        });

        viewModel.getHeadClick().observe((LifecycleOwner) context, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                selectPosition = SharedPreferencesUtils.getInstance()
                    .getIntValue(context, SharedPreferencesUtils.COLOR_SELECT_INDEX);
                if (aBoolean) {
                    bgView.setVisibility(View.GONE);
                } else {
                    bgView.setVisibility(selectPosition == position ? View.VISIBLE : View.GONE);
                }
            }
        });
    }
}
