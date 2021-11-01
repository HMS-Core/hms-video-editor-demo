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

package com.huawei.hms.videoeditor.ui.common.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

public class TransitionItemAdapter extends RecyclerView.Adapter<TransitionItemAdapter.ViewHolder> {
    private Context mContext;

    private List<CloudMaterialBean> list;

    private final Map<String, CloudMaterialBean> downloadingMap = new LinkedHashMap<>();

    private int selectPosition = 0;

    private TransitionItemAdapter.OnItemClickListener onItemClickListener;

    public TransitionItemAdapter(Context context, List<CloudMaterialBean> list) {
        mContext = context;
        this.list = list;
    }

    public void setOnItemClickListener(TransitionItemAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setData(List<CloudMaterialBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransitionItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_add_animation_item, parent, false);
        return new TransitionItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransitionItemAdapter.ViewHolder holder, int position) {
        CloudMaterialBean materialsCutContent = list.get(position);

        Glide.with(mContext)
            .load(!StringUtil.isEmpty(materialsCutContent.getPreviewUrl()))
            .apply(new RequestOptions()
                .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(SizeUtils.dp2Px(mContext, 4)))))
            .addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                    boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                    DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            })
            .into(holder.itemIv);
        holder.selectView.setVisibility(selectPosition == position ? View.VISIBLE : View.INVISIBLE);
        holder.nameTv.setText(materialsCutContent.getName());
        holder.mDownloadPbCenterTv.setVisibility(View.GONE);

        if (!StringUtil.isEmpty(materialsCutContent.getLocalPath()) || position == 0) {
            holder.downloadIv.setVisibility(View.GONE);
            holder.downloadPb.setVisibility(View.GONE);
        } else {
            holder.downloadIv.setVisibility(selectPosition == position ? View.INVISIBLE : View.VISIBLE);
            holder.downloadPb.setVisibility(selectPosition == position ? View.VISIBLE : View.INVISIBLE);
        }

        if (downloadingMap.containsKey(materialsCutContent.getId())) {
            holder.downloadIv.setVisibility(View.GONE);
            holder.downloadPb.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            holder.downloadIv.setVisibility(View.INVISIBLE);
            if (onItemClickListener != null) {
                if (position == 0) {
                    onItemClickListener.onItemClick(position, getItemClick());
                    return;
                }
                if (!StringUtil.isEmpty(materialsCutContent.getLocalPath())) {
                    onItemClickListener.onItemClick(position, getItemClick());
                } else {
                    if (!downloadingMap.containsKey(materialsCutContent.getId())) {
                        onItemClickListener.onDownloadClick(position);
                        holder.downloadPb.setVisibility(View.VISIBLE);
                    }
                }
            }
        }));

        holder.downloadIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            holder.downloadIv.setVisibility(View.INVISIBLE);
            holder.downloadPb.setVisibility(View.VISIBLE);
            if (onItemClickListener != null && !downloadingMap.containsKey(materialsCutContent.getId())) {
                onItemClickListener.onDownloadClick(position);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public void addDownloadMaterial(CloudMaterialBean materialsCutContent) {
        downloadingMap.put(materialsCutContent.getId(), materialsCutContent);
    }

    public void removeDownloadMaterial(String contentId) {
        downloadingMap.remove(contentId);
    }

    private boolean itemClick = true;

    public void setItemClick(boolean itemClick) {
        this.itemClick = itemClick;
    }

    public boolean getItemClick() {
        return this.itemClick;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, boolean itemClick);

        void onDownloadClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View selectView;

        ImageFilterView itemIv;

        TextView nameTv;

        ImageView downloadIv;

        ProgressBar downloadPb;

        ImageView mDownloadPbCenterTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectView = itemView.findViewById(R.id.item_select_view);
            itemIv = itemView.findViewById(R.id.item_image_view);
            nameTv = itemView.findViewById(R.id.item_name);
            downloadIv = itemView.findViewById(R.id.item_download_view);
            downloadPb = itemView.findViewById(R.id.item_progress);
            mDownloadPbCenterTv = itemView.findViewById(R.id.progress_center_iv);
        }
    }
}
