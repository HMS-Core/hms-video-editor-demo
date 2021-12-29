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

package com.huawei.hms.videoeditor.ui.mediaeditor.graffiti;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GraffitiShapeAdapter extends RecyclerView.Adapter {

    List<GraffitiInfo> infoList = new ArrayList<>();

    OnGraffitiChanged mListener;

    Context mContext;

    private int mSelectPosition = 0;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_graffiti_color_item, parent, false);
        mContext = view.getContext();

        return new GraffitiColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof GraffitiColorViewHolder)) {
            return;
        }
        Glide.with(mContext).load(R.drawable.icon_zhuanchang_done).into(((GraffitiColorViewHolder) holder).imageView);
        ((GraffitiColorViewHolder) holder).selectView
            .setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        if (mSelectPosition == position) {
            mListener.onGraffitiChanged(infoList.get(position));
        }
        switch (infoList.get(position).shape) {
            case LINE:
                Glide.with(mContext).load(R.drawable.gra_line).into(((GraffitiColorViewHolder) holder).imageView);
                ((GraffitiColorViewHolder) holder).imageView
                    .setContentDescription(mContext.getResources().getString(R.string.zyqx));
                break;
            case CIRCLE:
                Glide.with(mContext).load(R.drawable.gra_circle).into(((GraffitiColorViewHolder) holder).imageView);
                ((GraffitiColorViewHolder) holder).imageView
                    .setContentDescription(mContext.getResources().getString(R.string.circle));
                break;
            case TRIANGLE:
                Glide.with(mContext).load(R.drawable.gra_tra_ang).into(((GraffitiColorViewHolder) holder).imageView);
                ((GraffitiColorViewHolder) holder).imageView
                    .setContentDescription(mContext.getResources().getString(R.string.angle));
                break;
            case RECTANGLE:
                Glide.with(mContext).load(R.drawable.gra_rect).into(((GraffitiColorViewHolder) holder).imageView);
                ((GraffitiColorViewHolder) holder).imageView
                    .setContentDescription(mContext.getResources().getString(R.string.rectangle));
                break;
            case STRACTLINE:
                Glide.with(mContext).load(R.drawable.gra_str_line).into(((GraffitiColorViewHolder) holder).imageView);
                ((GraffitiColorViewHolder) holder).imageView
                    .setContentDescription(mContext.getResources().getString(R.string.line));
                break;
            case FIVESTAR:
                Glide.with(mContext).load(R.drawable.gra_five_star).into(((GraffitiColorViewHolder) holder).imageView);
                ((GraffitiColorViewHolder) holder).imageView
                    .setContentDescription(mContext.getResources().getString(R.string.fivestar));
                break;
            case ARROW:
                Glide.with(mContext).load(R.drawable.gra_arrow).into(((GraffitiColorViewHolder) holder).imageView);
                ((GraffitiColorViewHolder) holder).imageView
                    .setContentDescription(mContext.getResources().getString(R.string.arrow));
                break;
            case DIAMOND:
                Glide.with(mContext)
                    .load(R.drawable.gra_mutlipe_star)
                    .into(((GraffitiColorViewHolder) holder).imageView);
                ((GraffitiColorViewHolder) holder).imageView
                    .setContentDescription(mContext.getResources().getString(R.string.fiveshape));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public void setGraffitiChangedListener(OnGraffitiChanged listener) {
        mListener = listener;

        GraffitiInfo info1 = new GraffitiInfo();
        info1.type = GraffitiInfo.TYPE.SHAPE;
        info1.shape = GraffitiInfo.Shape.LINE;

        GraffitiInfo info2 = new GraffitiInfo();
        info2.type = GraffitiInfo.TYPE.SHAPE;
        info2.shape = GraffitiInfo.Shape.RECTANGLE;

        GraffitiInfo info3 = new GraffitiInfo();
        info3.type = GraffitiInfo.TYPE.SHAPE;
        info3.shape = GraffitiInfo.Shape.CIRCLE;

        GraffitiInfo info4 = new GraffitiInfo();
        info4.type = GraffitiInfo.TYPE.SHAPE;
        info4.shape = GraffitiInfo.Shape.TRIANGLE;

        GraffitiInfo info5 = new GraffitiInfo();
        info5.type = GraffitiInfo.TYPE.SHAPE;
        info5.shape = GraffitiInfo.Shape.FIVESTAR;

        GraffitiInfo info6 = new GraffitiInfo();
        info6.type = GraffitiInfo.TYPE.SHAPE;
        info6.shape = GraffitiInfo.Shape.DIAMOND;

        GraffitiInfo info7 = new GraffitiInfo();
        info7.type = GraffitiInfo.TYPE.SHAPE;
        info7.shape = GraffitiInfo.Shape.ARROW;

        GraffitiInfo info8 = new GraffitiInfo();
        info8.type = GraffitiInfo.TYPE.SHAPE;
        info8.shape = GraffitiInfo.Shape.STRACTLINE;

        infoList.add(info1);
        infoList.add(info2);
        infoList.add(info3);
        infoList.add(info4);
        infoList.add(info5);
        infoList.add(info6);
        infoList.add(info7);
        infoList.add(info8);

    }

    public class GraffitiColorViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public View selectView;

        public GraffitiColorViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_graffiti_color);
            selectView = itemView.findViewById(R.id.item_select_view);
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
