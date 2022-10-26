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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.Arrays;
import java.util.List;

public class PreviewBeautyAdapter extends RecyclerView.Adapter<PreviewBeautyAdapter.ImageHolder> {

    private Context mContext;

    private int mSelected = 0;

    private final List<String> mItemNames;

    public PreviewBeautyAdapter(Context context) {
        mContext = context;
        String[] beautyLists = mContext.getResources().getStringArray(R.array.preview_beauty);
        mItemNames = Arrays.asList(beautyLists);
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_preview_beauty_view, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, final int position) {
        holder.beautyName.setText(mItemNames.get(position));
        if (position == mSelected) {
            holder.beautyPanel.setBackgroundResource(R.drawable.ic_camera_effect_selected);
        } else {
            holder.beautyPanel.setBackgroundResource(0);
        }
        holder.beautyRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelected == position) {
                    return;
                }
                int lastSelected = mSelected;
                mSelected = position;
                notifyItemChanged(lastSelected, 0);
                notifyItemChanged(position, 0);
                if (mBeautySelectedListener != null) {
                    mBeautySelectedListener.onBeautySelected(position, mItemNames.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mItemNames == null) ? 0 : mItemNames.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        public LinearLayout beautyRoot;

        public FrameLayout beautyPanel;

        public TextView beautyName;

        public ImageHolder(View itemView) {
            super(itemView);
            beautyRoot = (LinearLayout) itemView.findViewById(R.id.item_beauty_root);
            beautyPanel = (FrameLayout) itemView.findViewById(R.id.item_beauty_panel);
            beautyName = (TextView) itemView.findViewById(R.id.item_beauty_name);
        }
    }

    public interface OnBeautySelectedListener {
        void onBeautySelected(int position, String beautyName);
    }

    private OnBeautySelectedListener mBeautySelectedListener;

    public void addOnBeautySelectedListener(OnBeautySelectedListener listener) {
        mBeautySelectedListener = listener;
    }

    public int getSelectedPosition() {
        return mSelected;
    }
}
