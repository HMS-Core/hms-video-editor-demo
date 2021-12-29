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

package com.huawei.hms.videoeditor.ui.mediaeditor.pip;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BlendAdapter extends RecyclerView.Adapter<BlendAdapter.BlendHolder> {

    private Context context;

    private int mSelectPosition = 0;

    private List<BlendItem> bitmapList = new ArrayList<>();

    public BlendAdapter(List<BlendItem> bitmapList, Context context) {
        this.context = context;
        this.bitmapList = bitmapList;
    }

    public void setIsSelect(int Select) {
        this.mSelectPosition = Select;
        notifyDataSetChanged();
    }

    public int getSelectPosition() {
        return this.mSelectPosition;
    }

    @NonNull
    @Override
    public BlendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.adapter_object_itme, parent, false);
        return new BlendHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull BlendHolder holder, int position) {
        holder.mSelectView.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        Glide.with(context)
            .load(bitmapList.get(position).drawableID)
            .apply(new RequestOptions().transform(
                new MultiTransformation<>(new CenterInside(), new RoundedCorners(SizeUtils.dp2Px(context, 4)))))
            .into(holder.image);
        holder.mBlendName.setText(bitmapList.get(position).blendName);

        holder.image.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (selectedListener == null) {
                return;
            }
            if (mSelectPosition != position) {
                notifyItemChanged(mSelectPosition);
                mSelectPosition = position;
                notifyItemChanged(position);
                selectedListener.onStyleSelected(mSelectPosition);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    class BlendHolder extends RecyclerView.ViewHolder {
        ImageView image;

        View mSelectView;

        TextView mBlendName;

        BlendHolder(@NonNull View itemView) {
            super(itemView);
            mSelectView = itemView.findViewById(R.id.item_select_view);
            image = itemView.findViewById(R.id.item_image_view);
            mBlendName = itemView.findViewById(R.id.object_name);
        }
    }

    OnStyleSelectedListener selectedListener;

    public void setSelectedListener(OnStyleSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface OnStyleSelectedListener {
        void onStyleSelected(int position);
    }

    public static class BlendItem {
        public int drawableID;

        public String blendName;

        public BlendItem(int drawableID, String blendName) {
            this.drawableID = drawableID;
            this.blendName = blendName;
        }
    }
}