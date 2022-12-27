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

package com.huawei.hms.videoeditor.ui.mediaeditor.aihair.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.ai.HVEAIColorBean;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.List;

public class HairDyeingAdapter extends RCommandAdapter<HVEAIColorBean> {
    private volatile int currentSelectedPosition = -1;

    private OnAiHairAdapterItemClickListener onAiHairAdapterItemClickListener;

    public HairDyeingAdapter(Context context, List<HVEAIColorBean> list, int layoutId) {
        super(context, list, layoutId);
    }

    public void setOnItemClickListener(OnAiHairAdapterItemClickListener listener) {
        onAiHairAdapterItemClickListener = listener;
    }

    @Override
    protected void convert(RViewHolder holder, HVEAIColorBean colorBean, int dataPosition, int position) {
        View holderView = holder.getView(R.id.item_select_view_ai_hair);
        ImageView mItemIv = holder.getView(R.id.item_image_view_ai_hair);
        TextView mTitleTv = holder.getView(R.id.item_name_ai_hair);

        Glide.with(mContext)
            .load(colorBean.getPreviewUrl())
            .apply(new RequestOptions().transform(
                new MultiTransformation<>(new CenterCrop(), new RoundedCorners(SizeUtils.dp2Px(mContext, 4)))))
            .into(mItemIv);
        mTitleTv.setText(colorBean.getColorName());
        holderView.setSelected(currentSelectedPosition == position);
        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            if (onAiHairAdapterItemClickListener != null) {
                onAiHairAdapterItemClickListener.onAdapterItemClick(colorBean, position);
            }
        }));
    }

    public int getSelectPosition() {
        return currentSelectedPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.currentSelectedPosition = selectPosition;
    }

    public interface OnAiHairAdapterItemClickListener {
        void onAdapterItemClick(HVEAIColorBean colorBean, int position);
    }
}
