
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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.FoldScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.LanguageUtils;
import com.huawei.hms.videoeditor.ui.common.utils.LocalResourceUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.List;

public class MenuAdapter extends RCommandAdapter<EditMenuBean> {

    private static final String TAG = "MenuAdapter";

    private final ConstraintLayout.LayoutParams contentParams;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public MenuAdapter(Context context, List<EditMenuBean> list, int layoutId) {
        super(context, list, layoutId);
        int itemWidth = (int) (SizeUtils.screenWidth(mContext) / 6.5f);
        int itemHeight = LanguageUtils.isZh() ? SizeUtils.dp2Px(mContext, 56) : SizeUtils.dp2Px(mContext, 64);
        if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(context)) {
            itemWidth = (int) (SizeUtils.screenWidth(mContext) / 12.5f);
        }
        contentParams = new ConstraintLayout.LayoutParams(itemWidth, itemHeight);
    }

    @Override
    protected void convert(RViewHolder holder, EditMenuBean editMenuBean, int dataPosition, int position) {
        if (contentParams == null) {
            SmartLog.e(TAG, "contentParams is null");
            return;
        }

        holder.itemView.setLayoutParams(contentParams);

        holder.itemView.setEnabled(editMenuBean.isEnable());
        holder.setViewAlpha(R.id.iv_icon, editMenuBean.isEnable() ? 1f : 0.45f);
        holder.setViewAlpha(R.id.tv_name, editMenuBean.isEnable() ? 1f : 0.45f);

        int imageResourceId = LocalResourceUtil.getDrawableId(mContext, editMenuBean.getDrawableName());
        if (imageResourceId != 0) {
            holder.setImageResource(R.id.iv_icon, imageResourceId);
        } else {
            holder.setImageResource(R.id.iv_icon, R.drawable.logo);
        }

        int stringResourceId = LocalResourceUtil.getStringId(mContext, editMenuBean.getName());
        if (stringResourceId != 0) {
            String name = mContext.getString(LocalResourceUtil.getStringId(mContext, editMenuBean.getName()));
            SmartLog.d(TAG, "name:" + name + " name-length:" + name.length());
            holder.setText(R.id.tv_name, LocalResourceUtil.getStringId(mContext, editMenuBean.getName()));
        } else {
            holder.setText(R.id.tv_name, R.string.app_name);
        }

        holder.itemView.setTag(R.id.editMenuTag, editMenuBean);
        holder.itemView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (editMenuBean.isEnable() && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position, dataPosition);
            }
        }));
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int dataPosition);
    }

}
