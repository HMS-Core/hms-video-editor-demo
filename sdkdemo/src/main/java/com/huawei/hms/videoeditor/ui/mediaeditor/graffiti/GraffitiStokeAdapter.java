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

package com.huawei.hms.videoeditor.ui.mediaeditor.graffiti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GraffitiStokeAdapter extends RecyclerView.Adapter {
    OnGraffitiChanged mListener;

    Context mContext;

    private int mSelectPosition = 2;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_graffiti_stoke_item, parent, false);
        mContext = view.getContext();

        return new GraffitiStokeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof GraffitiStokeViewHolder)) {
            return;
        }
        ViewGroup.LayoutParams layout = ((GraffitiStokeViewHolder) holder).circleOut.getLayoutParams();
        layout.width = ScreenUtil.dp2px(2 * position + 6);
        layout.height = ScreenUtil.dp2px(2 * position + 6);

        ViewGroup.LayoutParams layout2 = ((GraffitiStokeViewHolder) holder).circleIn.getLayoutParams();
        layout2.width = ScreenUtil.dp2px(2 * position + 2);
        layout2.height = ScreenUtil.dp2px(2 * position + 2);

        if (mSelectPosition == position) {
            ((GraffitiStokeViewHolder) holder).circleIn
                .setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_graffiti_stoke_in_selected));
            ((GraffitiStokeViewHolder) holder).circleOut
                .setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_graffiti_stoke_out_selected));
            GraffitiInfo info = new GraffitiInfo();
            info.type = GraffitiInfo.TYPE.WIDTH;
            info.stokeWidth = 2 * position + 6;
            mListener.onGraffitiChanged(info);
        } else {
            ((GraffitiStokeViewHolder) holder).circleIn
                .setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_graffiti_stoke_in));
            ((GraffitiStokeViewHolder) holder).circleOut
                .setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_graffiti_stoke_out));
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public void setGraffitiChangedListener(OnGraffitiChanged listener) {
        mListener = listener;
    }

    public class GraffitiStokeViewHolder extends RecyclerView.ViewHolder {
        public ImageView circleOut;

        public ImageView circleIn;

        public GraffitiStokeViewHolder(@NonNull View itemView) {
            super(itemView);
            circleOut = itemView.findViewById(R.id.item_normal_view);
            circleIn = itemView.findViewById(R.id.item_select_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (mSelectPosition != position) {
                        int lastp = mSelectPosition;
                        mSelectPosition = position;
                        notifyItemChanged(lastp);
                        notifyItemChanged(mSelectPosition);
                    }
                }
            });
        }
    }
}
