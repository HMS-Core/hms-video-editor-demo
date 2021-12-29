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

package com.huawei.hms.videoeditor.ui.common.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.bean.HVERational;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ProportionAdapter extends RecyclerView.Adapter<ProportionAdapter.ViewHolder> {
    private Context context;

    private List<HVERational> rationalList;

    private List<Integer> imageList;

    private int mSelectPosition = 0;

    private OnItemClickListener mOnItemClickListener;

    public ProportionAdapter(List<HVERational> list, List<Integer> imageList, Context context) {
        this.rationalList = list;
        this.imageList = imageList;
        this.context = context;
    }

    public void setSelectPosition(int mSelectPosition) {
        this.mSelectPosition = mSelectPosition;
    }

    public int getSelectPosition() {
        return this.mSelectPosition;
    }

    @NonNull
    @Override
    public ProportionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.proportion_item, parent, false);
        return new ProportionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (rationalList == null || rationalList.size() <= 0) {
            return;
        }
        Integer imageInt = imageList.get(position);
        int i = imageInt.intValue();
        holder.image.setBackgroundResource(i);

        holder.layoutParent.setSelected(mSelectPosition == position);
        HVERational hveRational = rationalList.get(position);
        if (position == 0) {
            holder.proportion.setText(context.getResources().getString(R.string.free));
        } else if (position == 7) {
            holder.proportion
                .setText(hveRational.num / 100 + "." + hveRational.num % 100 + ":" + hveRational.dem / 100);
        } else if (position == 1) {
            holder.proportion.setText(context.getString(R.string.full));
        } else {
            holder.proportion.setText(hveRational.num + ":" + hveRational.dem);
        }

        if (position == mSelectPosition && mSelectPosition == 0) {
            holder.image.setBackgroundResource(R.drawable.crop_free_selete);
        } else if (position == mSelectPosition && mSelectPosition == 1) {
            holder.image.setBackgroundResource(R.drawable.crop_full_selete);
        } else if (position == mSelectPosition && mSelectPosition == 2) {
            holder.image.setBackgroundResource(R.drawable.crop_9_16selete);
        } else if (position == mSelectPosition && mSelectPosition == 3) {
            holder.image.setBackgroundResource(R.drawable.crop_16_9selete);
        } else if (position == mSelectPosition && mSelectPosition == 4) {
            holder.image.setBackgroundResource(R.drawable.crop_1_1selete);
        } else if (position == mSelectPosition && mSelectPosition == 5) {
            holder.image.setBackgroundResource(R.drawable.crop_4_3selete);
        } else if (position == mSelectPosition && mSelectPosition == 6) {
            holder.image.setBackgroundResource(R.drawable.crop_3_4selete);
        } else if (position == mSelectPosition && mSelectPosition == 7) {
            holder.image.setBackgroundResource(R.drawable.crop_2_35_1selete);
        } else if (position == mSelectPosition && mSelectPosition == 8) {
            holder.image.setBackgroundResource(R.drawable.crop_9_21selete);
        } else if (position == mSelectPosition && mSelectPosition == 9) {
            holder.image.setBackgroundResource(R.drawable.crop_21_9selete);
        }

        holder.layoutParent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        private TextView proportion;

        private ConstraintLayout layoutParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.background);
            proportion = itemView.findViewById(R.id.proportion);
            layoutParent = itemView.findViewById(R.id.layout_parent);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
