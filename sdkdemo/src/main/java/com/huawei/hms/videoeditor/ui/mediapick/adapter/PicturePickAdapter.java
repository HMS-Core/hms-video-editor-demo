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

package com.huawei.hms.videoeditor.ui.mediapick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class PicturePickAdapter extends PagedListAdapter<MediaData, PicturePickAdapter.ViewHolder> {

    private final Context mContext;

    private final int mImageViewWidth;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public PicturePickAdapter(Context context) {
        super(new DiffUtil.ItemCallback<MediaData>() {
            @Override
            public boolean areItemsTheSame(@NonNull MediaData oldItem, @NonNull MediaData newData) {
                return oldItem.getPath().equals(newData.getPath());
            }

            @Override
            public boolean areContentsTheSame(@NonNull MediaData oldItem, @NonNull MediaData newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mImageViewWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 58))) / 3;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pick_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mContentLayout.setLayoutParams(new RelativeLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        MediaData item = getItem(position);
        if (item != null && item.getPath() != null) {
            Glide.with(mContext)
                .load(item.getPath())
                .apply(new RequestOptions().transform(
                    new MultiTransformation<>(new CenterCrop(), new RoundedCorners(SizeUtils.dp2Px(mContext, 8)))))
                .into(holder.mMediaIv);
        }
        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            mOnItemClickListener.onItemClick(position);
        }));
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mContentLayout;

        ImageView mMediaIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContentLayout = itemView.findViewById(R.id.content_layout);
            mMediaIv = itemView.findViewById(R.id.iv_media);
        }
    }
}
