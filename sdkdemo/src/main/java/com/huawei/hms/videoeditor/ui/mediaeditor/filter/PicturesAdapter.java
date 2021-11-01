
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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.mediapick.manager.MediaPickManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class PicturesAdapter extends PagedListAdapter<MediaData, PicturesAdapter.ViewHolder> {

    private final Activity mContext;

    private final MediaPickManager manager;

    private final int mImageViewWidth;

    private boolean showChoiceView = true;

    public PicturesAdapter(Activity context) {
        super(new DiffUtil.ItemCallback<MediaData>() {
            @Override
            public boolean areItemsTheSame(@NonNull MediaData oldMediaData, @NonNull MediaData newItem) {
                return oldMediaData.getPath().equals(newItem.getPath());
            }

            @Override
            public boolean areContentsTheSame(@NonNull MediaData oldItem, @NonNull MediaData newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        manager = MediaPickManager.getInstance();
        mImageViewWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 48))) / 3;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_module_pick_video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mContentLayout.setLayoutParams(new RelativeLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        MediaData item = getItem(position);
        if (item == null) {
            return;
        }
        Glide.with(mContext)
            .load(item.getPath())
            .override(mImageViewWidth, mImageViewWidth)
            .placeholder(R.drawable.color_20_100_8_bg)
            .error(R.drawable.color_20_100_8_bg)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.mMediaIv);

        if (!item.isVideo()) {
            holder.mDurationTv.setVisibility(View.GONE);
            holder.mMaskView.setBackgroundResource(R.color.transparent);
        }

        if (item.getIndex() != 0) {
            holder.mIndexTv.setText(String.valueOf(item.getIndex()));
            holder.mIndexTv.setBackgroundResource(R.drawable.ic_checkbox_selected_index);
        } else {
            holder.mIndexTv.setText("");
            holder.mIndexTv.setBackgroundResource(R.drawable.index_checkbox_normal);
        }
        holder.mTvHasImport.setVisibility(View.INVISIBLE);

        if (!showChoiceView) {
            holder.mIndexTv.setVisibility(View.GONE);
            holder.fullImageFilter.setVisibility(View.GONE);
            holder.mTvHasImport.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            if (item.isSetSelected()) {
                manager.removeSelectItemAndSetIndex(item);
            } else {
                manager.addSelectItemAndSetIndexNum(item);
            }
        }));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mContentLayout;

        ImageFilterView mMediaIv;

        View mMaskView;

        ImageFilterView fullImageFilter;

        TextView mTvHasImport;

        TextView mDurationTv;

        TextView mIndexTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContentLayout = itemView.findViewById(R.id.content_layout);
            mMediaIv = itemView.findViewById(R.id.iv_media);
            mMaskView = itemView.findViewById(R.id.mask_view);
            fullImageFilter = itemView.findViewById(R.id.if_full);
            mTvHasImport = itemView.findViewById(R.id.tv_has_import);
            mDurationTv = itemView.findViewById(R.id.tv_duration);
            mIndexTv = itemView.findViewById(R.id.tv_index);
        }
    }
}
