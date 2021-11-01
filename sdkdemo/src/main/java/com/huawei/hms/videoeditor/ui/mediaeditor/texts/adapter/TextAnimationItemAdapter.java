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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.adapter;

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
import com.bumptech.glide.load.resource.bitmap.CenterInside;
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

public class TextAnimationItemAdapter extends RecyclerView.Adapter<TextAnimationItemAdapter.ViewHolder> {
    private Context mContext;

    private List<CloudMaterialBean> mList;

    private final Map<String, CloudMaterialBean> bDownloadingMap = new LinkedHashMap<>();

    private int bSelectPosition = 0;

    private OnItemClickListener mOnItemClickListener;

    public TextAnimationItemAdapter(Context context, List<CloudMaterialBean> list) {
        mContext = context;
        mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setData(List<CloudMaterialBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_add_animation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CloudMaterialBean item = mList.get(position);

        Glide.with(mContext)
            .load(item.getPreviewUrl())
            .apply(new RequestOptions().transform(
                new MultiTransformation(new CenterInside(), new RoundedCorners(SizeUtils.dp2Px(mContext, 4)))))
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
            .into(holder.mItemIv);
        holder.mSelectView.setVisibility(bSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        holder.mNameTv.setText(item.getName());

        if (!StringUtil.isEmpty(item.getLocalPath()) || position == 0) {
            holder.mDownloadIv.setVisibility(View.GONE);
            holder.mDownloadPb.setVisibility(View.GONE);
        } else {
            holder.mDownloadIv.setVisibility(bSelectPosition == position ? View.INVISIBLE : View.VISIBLE);
            holder.mDownloadPb.setVisibility(bSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        }

        if (bDownloadingMap.containsKey(item.getId())) {
            holder.mDownloadIv.setVisibility(View.GONE);
            holder.mDownloadPb.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            if (mOnItemClickListener == null) {
                return;
            }
            if (position == 0) {
                mOnItemClickListener.onItemClick(position);
                return;
            }
            if (!StringUtil.isEmpty(item.getLocalPath())) {
                mOnItemClickListener.onItemClick(position);
            } else {
                if (!bDownloadingMap.containsKey(item.getId())) {
                    mOnItemClickListener.onDownloadClick(position);
                }
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public int getSelectPosition() {
        return bSelectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.bSelectPosition = selectPosition;
    }

    public void addDownloadMaterial(CloudMaterialBean item) {
        if (!bDownloadingMap.containsKey(item.getId())) {
            bDownloadingMap.put(item.getId(), item);
        }
    }

    public void removeDownloadMaterial(String contentId) {
        bDownloadingMap.remove(contentId);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDownloadClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mSelectView;

        ImageFilterView mItemIv;

        TextView mNameTv;

        ImageView mDownloadIv;

        ProgressBar mDownloadPb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mSelectView = itemView.findViewById(R.id.item_select_view);
            mItemIv = itemView.findViewById(R.id.item_image_view);
            mNameTv = itemView.findViewById(R.id.item_name);
            mDownloadIv = itemView.findViewById(R.id.item_download_view);
            mDownloadPb = itemView.findViewById(R.id.item_progress);
        }
    }
}
