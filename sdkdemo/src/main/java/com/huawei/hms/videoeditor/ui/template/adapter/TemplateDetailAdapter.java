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

package com.huawei.hms.videoeditor.ui.template.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.template.HVETemplateInfo;
import com.huawei.hms.videoeditor.ui.template.view.exoplayer.ListPlayerView;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class TemplateDetailAdapter extends RCommandAdapter<HVETemplateInfo> {
    private static final String TAG = "TemplateDetailAdapter";

    private int maxWidth;

    private int maxHeight;

    private final String mCategoryId;

    private final String columnName;

    public TemplateDetailAdapter(Context context, int maxWidth, int maxHeight, String categoryId,
        List<HVETemplateInfo> list, int layoutId, String columnName) {
        super(context, list, layoutId);
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.mCategoryId = categoryId;
        this.columnName = columnName;
    }

    @Override
    protected void convert(RViewHolder holder, HVETemplateInfo templateInfo, int dataPosition, int position) {
        ListPlayerView listPlayerView = holder.getView(R.id.list_player_view);
        int realWidth = 0;
        int realHeight = 0;
        try {
            if (!TextUtils.isEmpty(templateInfo.getAspectRatio())) {
                String[] split = templateInfo.getAspectRatio().split("\\*");
                if (split.length != 2) {
                    SmartLog.e(TAG, "aspectRatio value Illegal");
                } else {
                    String width = split[0];
                    String height = split[1];
                    realWidth = Integer.parseInt(width.trim());
                    realHeight = Integer.parseInt(height.trim());
                }
            }
        } catch (RuntimeException e) {
            realWidth = maxWidth;
            realHeight = maxHeight;
        }

        listPlayerView.bindData(maxWidth, maxHeight, realWidth, realHeight, mCategoryId, templateInfo.getPreviewUrl(),
            templateInfo.getThumbUrl(), columnName);
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }
}
