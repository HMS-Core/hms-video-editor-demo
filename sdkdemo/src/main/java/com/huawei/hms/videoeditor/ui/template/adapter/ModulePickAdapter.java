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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;
import com.huawei.hms.videoeditor.ui.template.utils.ModuleSelectManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ModulePickAdapter extends PagedListAdapter<MediaData, ModulePickAdapter.ViewHolder> {
    private Context mContext;

    private long mReplaceValidDuration = -1;

    private List<MaterialData> mMaterialDataList;

    private ModuleSelectManager mModuleManager;

    private OnItemClickListener mOnItemClickListener;

    private int mImageViewWidth;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ModulePickAdapter(Context context) {
        super(new DiffUtil.ItemCallback<MediaData>() {
            @Override
            public boolean areItemsTheSame(@NonNull MediaData oldItem, @NonNull MediaData newItem) {
                return oldItem.getPath().equals(newItem.getPath());
            }

            @Override
            public boolean areContentsTheSame(@NonNull MediaData oldItem, @NonNull MediaData newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mModuleManager = ModuleSelectManager.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pick_video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mContentLayout.setLayoutParams(new RelativeLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        MediaData item = getItem(position);
        String path = "";
        if (item == null) {
            return;
        }
        path = item.getPath();
        Glide.with(mContext)
            .load(path)
            .override(mImageViewWidth, mImageViewWidth)
            .placeholder(R.drawable.color_20_100_8_bg)
            .error(R.drawable.color_20_100_8_bg)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.mMediaIv);
        if (item.isVideo()) {
            holder.mDurationTv.setVisibility(View.VISIBLE);
            if (item.getCutTrimOut() <= 0 && item.getCutTrimIn() <= 0) {
                holder.mDurationTv.setText(TimeUtils.makeTimeString(mContext, item.getDuration()));
            } else {
                holder.mDurationTv.setText(TimeUtils.makeTimeString(mContext,
                    item.getDuration() - item.getCutTrimIn() - item.getCutTrimOut()));
            }
            if (mReplaceValidDuration != -1) {
                if (item.getDuration() < mReplaceValidDuration) {
                    holder.mMaskView.setBackgroundResource(R.drawable.color_ccc_70_0_bg);
                } else {
                    holder.mMaskView.setBackgroundResource(R.color.transparent);
                }
            } else {
                holder.mMaskView.setBackgroundResource(R.color.transparent);
            }
        } else {
            holder.mDurationTv.setVisibility(View.GONE);
            holder.mMaskView.setBackgroundResource(R.color.transparent);
        }

        holder.mIndexLayout.setVisibility(View.GONE);
        if (item.isVideo()) {
            if (mReplaceValidDuration != -1) {
                if (item.getDuration() < mReplaceValidDuration) {
                    holder.mMaskView.setBackgroundResource(R.drawable.color_ccc_70_0_bg);
                } else {
                    holder.mMaskView.setBackgroundResource(R.drawable.color_ccc_20_0_bg);
                }
            } else {
                if (item.getDuration() < mModuleManager.getCurrentDuration()) {
                    holder.mMaskView.setBackgroundResource(R.drawable.color_ccc_70_0_bg);
                } else {
                    holder.mMaskView.setBackgroundResource(R.drawable.color_ccc_20_0_bg);
                }
            }
        } else {
            holder.mMaskView.setBackgroundResource(R.drawable.color_ccc_20_0_bg);
        }
        if (item.getIndex() > 0 || isImport(item)) {
            holder.mTvHasImport.setVisibility(View.VISIBLE);
        } else {
            holder.mTvHasImport.setVisibility(View.INVISIBLE);
        }

        holder.mMaskView
            .setOnClickListener(new OnClickRepeatedListener((v) -> mOnItemClickListener.onItemClick(position)));
    }

    public void setReplaceValidDuration(long replaceValidDuration) {
        this.mReplaceValidDuration = replaceValidDuration;
    }

    public long getReplaceValidDuration() {
        return mReplaceValidDuration;
    }

    public void setMaterialDataList(List<MaterialData> mMaterialDataList) {
        this.mMaterialDataList = mMaterialDataList;
    }

    private boolean isImport(MediaData item) {
        boolean hasImport = false;
        if (mMaterialDataList == null || mMaterialDataList.size() <= 0) {
            return false;
        }
        for (MaterialData data : mMaterialDataList) {
            if (data.getPath().equals(item.getPath())) {
                hasImport = true;
                break;
            }
        }
        return hasImport;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mContentLayout;

        ImageView mMediaIv;

        View mMaskView;

        TextView mTvHasImport;

        TextView mDurationTv;

        LinearLayout mIndexLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContentLayout = itemView.findViewById(R.id.content_layout);
            mMediaIv = itemView.findViewById(R.id.iv_media);
            mMaskView = itemView.findViewById(R.id.mask_view);
            mTvHasImport = itemView.findViewById(R.id.tv_has_import);
            mDurationTv = itemView.findViewById(R.id.tv_duration);
            mIndexLayout = itemView.findViewById(R.id.tv_index_layout);
        }
    }

    public void setImageViewWidth(int imageViewWidth) {
        this.mImageViewWidth = imageViewWidth;
    }
}
