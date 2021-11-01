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

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
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
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ObjectAdapter extends RecyclerView.Adapter<ObjectAdapter.ObjectHolder> {

    private Activity context;

    private int mSelectPosition = -1;

    private List<HVEAsset> bitmapList;

    public ObjectAdapter(List<HVEAsset> bitmapList, Activity context) {
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
    public ObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.adapter_object_itme, parent, false);
        return new ObjectHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjectHolder holder, int position) {
        holder.mSelectView.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        HVEAsset asset = bitmapList.get(position);
        if (asset instanceof HVEVideoAsset) {
            ((HVEVideoAsset) asset).getFirstFrame(ScreenUtil.dp2px(56), ScreenUtil.dp2px(56), getCallback(holder));
        } else {
            ((HVEImageAsset) asset).getFirstFrame(ScreenUtil.dp2px(56), ScreenUtil.dp2px(56), getCallback(holder));
        }

        if (position == 0) {
            holder.mObjectName.setText(context.getString(R.string.main));
        } else {
            holder.mObjectName.setText(context.getString(R.string.first_menu_pip));
        }

        holder.image.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (selectedListener == null) {
                return;
            }
            if (mSelectPosition != position) {
                mSelectPosition = position;
                notifyItemChanged(mSelectPosition);
                selectedListener.onStyleSelected(mSelectPosition);
            }
        }));

        if (mSelectPosition == position) {
            holder.mSelectView.setVisibility(View.VISIBLE);
        } else {
            holder.mSelectView.setVisibility(View.INVISIBLE);
        }
    }

    private HuaweiVideoEditor.ImageCallback getCallback(@NonNull ObjectHolder holder) {
        return new HuaweiVideoEditor.ImageCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, long timeStamp) {
                context.runOnUiThread(() -> {
                    Glide.with(context)
                        .load(bitmap)
                        .apply(new RequestOptions().transform(new MultiTransformation<>(new CenterInside(),
                            new RoundedCorners(SizeUtils.dp2Px(context, 4)))))
                        .into(holder.image);
                });
            }

            @Override
            public void onFail(int errorCode) {

            }
        };
    }

    @Override
    public int getItemCount() {
        return bitmapList == null ? 0 : bitmapList.size();
    }

    static class ObjectHolder extends RecyclerView.ViewHolder {
        ImageView image;

        View mSelectView;

        TextView mObjectName;

        ObjectHolder(@NonNull View itemView) {
            super(itemView);
            mSelectView = itemView.findViewById(R.id.item_select_view);
            image = itemView.findViewById(R.id.item_image_view);
            mObjectName = itemView.findViewById(R.id.object_name);
        }
    }

    OnStyleSelectedListener selectedListener;

    public void setSelectedListener(OnStyleSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface OnStyleSelectedListener {
        void onStyleSelected(int position);
    }
}