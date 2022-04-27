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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ModuleEditSelectAdapter extends RecyclerView.Adapter<ModuleEditSelectAdapter.ViewHolder> {
    private Context mContext;

    private List<MediaData> mAllMediaList;

    private OnItemClickListener mOnItemClickListener = null;

    private int currentIndex = 0;

    private boolean showSelectImage = true;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ModuleEditSelectAdapter(Context context, List<MediaData> list) {
        mContext = context;
        mAllMediaList = list;
    }

    public void replaceData(MediaData data, int position) {
        mAllMediaList.remove(position);
        mAllMediaList.add(position, data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_module_edit_select_video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaData item = mAllMediaList.get(position);
        holder.mIndexTv.setText(String.valueOf(position + 1));
        holder.mIndexTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_d1));

        float textTimer = BigDecimal.valueOf(BigDecimalUtils.div(item.getValidDuration(), 1000f, 1)).floatValue();
        String timeDuration = mContext.getResources()
            .getQuantityString(R.plurals.seconds_time, Double.valueOf(textTimer).intValue(),
                NumberFormat.getInstance().format(textTimer));
        holder.mDurationTv.setText(timeDuration);

        Glide.with(mContext)
            .load(item.getPath())
            .apply(new RequestOptions()
                .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(SizeUtils.dp2Px(mContext, 3)))))
            .into(holder.mMediaIv);

        if (showSelectImage) {
            if (position == currentIndex) {
                holder.selectImage.setVisibility(View.VISIBLE);
                holder.mDurationTv.setVisibility(View.INVISIBLE);
            } else {
                holder.selectImage.setVisibility(View.INVISIBLE);
                holder.mDurationTv.setVisibility(View.VISIBLE);
            }
        } else {
            holder.selectImage.setVisibility(View.INVISIBLE);
        }

        if (mOnItemClickListener != null) {
            holder.itemView
                .setOnClickListener(new OnClickRepeatedListener((v) -> mOnItemClickListener.onItemClick(position)));
        }
    }

    @Override
    public int getItemCount() {
        return mAllMediaList == null ? 0 : mAllMediaList.size();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void setSelectImageVisibility(boolean show) {
        showSelectImage = show;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageFilterView mMediaIv;

        TextView mDurationTv;

        TextView mIndexTv;

        View selectImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaIv = itemView.findViewById(R.id.iv_media);
            mDurationTv = itemView.findViewById(R.id.tv_duration);
            mIndexTv = itemView.findViewById(R.id.tv_index);
            selectImage = itemView.findViewById(R.id.item_select_view);
        }
    }
}
