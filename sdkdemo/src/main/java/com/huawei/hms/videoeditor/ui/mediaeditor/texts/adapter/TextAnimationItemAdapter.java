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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TextAnimationItemAdapter extends RecyclerView.Adapter<TextAnimationItemAdapter.ViewHolder> {
    private Context context;

    private List<CloudMaterialBean> list;

    private final Map<String, CloudMaterialBean> map = new LinkedHashMap<>();

    private int selectposition = 0;

    private OnItemClickListener clickListener;

    public TextAnimationItemAdapter(Context context, List<CloudMaterialBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_add_animation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vHolder, int position) {
        CloudMaterialBean item = list.get(position);

        Glide.with(context)
            .load(!StringUtil.isEmpty(item.getPreviewUrl()) ? item.getPreviewUrl() : item.getLocalDrawableId())
            .apply(new RequestOptions().transform(
                new MultiTransformation(new CenterInside(), new RoundedCorners(SizeUtils.dp2Px(context, 4)))))
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
            .into(vHolder.mItemIv);
        vHolder.view.setVisibility(selectposition == position ? View.VISIBLE : View.INVISIBLE);
        vHolder.mNameTv.setText(item.getName());

        if (!StringUtil.isEmpty(item.getLocalPath()) || position == 0) {
            vHolder.mDownloadIv.setVisibility(View.GONE);
            vHolder.mDownloadPb.setVisibility(View.GONE);
        } else {
            vHolder.mDownloadIv.setVisibility(selectposition == position ? View.INVISIBLE : View.VISIBLE);
            vHolder.mDownloadPb.setVisibility(selectposition == position ? View.VISIBLE : View.INVISIBLE);
        }

        if (map.containsKey(item.getId())) {
            vHolder.mDownloadIv.setVisibility(View.GONE);
            vHolder.mDownloadPb.setVisibility(View.VISIBLE);
        }

        vHolder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            if (clickListener == null) {
                return;
            }
            if (position == 0) {
                clickListener.onItemClick(position);
                return;
            }
            if (!StringUtil.isEmpty(item.getLocalPath())) {
                clickListener.onItemClick(position);
            } else {
                if (!map.containsKey(item.getId())) {
                    clickListener.onDownloadClick(position);
                }
            }
        }));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public void setData(List<CloudMaterialBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addDownloadMaterial(CloudMaterialBean item) {
        if (!map.containsKey(item.getId())) {
            map.put(item.getId(), item);
        }
    }

    public void removeDownloadMaterial(String contentId) {
        map.remove(contentId);
    }

    public int getSelectPosition() {
        return selectposition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectposition = selectPosition;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDownloadClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        ImageFilterView mItemIv;

        TextView mNameTv;

        ImageView mDownloadIv;

        ProgressBar mDownloadPb;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.view = view.findViewById(R.id.item_select_view);
            mItemIv = view.findViewById(R.id.item_image_view);
            mNameTv = view.findViewById(R.id.item_name);
            mDownloadIv = view.findViewById(R.id.item_download_view);
            mDownloadPb = view.findViewById(R.id.item_progress);
        }
    }
}
