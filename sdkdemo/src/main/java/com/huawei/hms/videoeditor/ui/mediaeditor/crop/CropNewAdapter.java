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

package com.huawei.hms.videoeditor.ui.mediaeditor.crop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.List;

public class CropNewAdapter extends RecyclerView.Adapter<CropNewAdapter.CanvasStyleHolder> {
    private List<Integer> list;
    private List<String> listName;
    private Context context;
    private int mSelectPosition = 0;

    CropNewAdapter(List<String> listName, List<Integer> list, Context context) {
        this.list = list;
        this.listName = listName;
        this.context = context;
    }

    @NonNull
    @Override
    public CanvasStyleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CanvasStyleHolder(LayoutInflater.from(context).inflate(R.layout.adapter_cropitem_style, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CanvasStyleHolder holder, int position) {
        int i = list.get(position);
        holder.image.setBackgroundResource(i);

        holder.styleTitle.setText(listName.get(position));
        if (position == mSelectPosition) {
            holder.styleTitle.setSelected(true);
        } else {
            holder.styleTitle.setSelected(false);
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
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 1;
        } else {
            return list.size();
        }
    }

    class CanvasStyleHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView styleTitle;
        ConstraintLayout rootView;

        CanvasStyleHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image_view);
            styleTitle = itemView.findViewById(R.id.style);
            rootView = itemView.findViewById(R.id.rootview);

            rootView.setOnClickListener(new OnClickRepeatedListener(v -> {
                int position = getAdapterPosition();
                if (mSelectPosition != position) {
                    if (mSelectPosition != 0) {
                        int lastly = mSelectPosition;
                        notifyItemChanged(lastly);
                    }
                    mSelectPosition = position;
                    notifyItemChanged(mSelectPosition);
                }
                if (selectedListener != null) {
                    selectedListener.onStyleSelected(position,false);
                }
            }));
        }
    }

    OnStyleSelectedListener selectedListener;

    public void setSelectedListener(OnStyleSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface OnStyleSelectedListener {
        void onStyleSelected(int position, boolean isReset);

        void onAlbumSelected();

    }


    public void setIndex(int position) {
        this.mSelectPosition = position;
    }

    public int getIndex() {
        return mSelectPosition;
    }

    public void click(int position, boolean isReset) {
        if (selectedListener != null) {
            selectedListener.onStyleSelected(position, isReset);
        }
    }
}