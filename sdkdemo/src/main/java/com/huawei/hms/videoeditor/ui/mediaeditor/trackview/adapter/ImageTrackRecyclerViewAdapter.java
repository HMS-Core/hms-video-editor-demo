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

package com.huawei.hms.videoeditor.ui.mediaeditor.trackview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.ui.common.utils.BitmapUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;

public class ImageTrackRecyclerViewAdapter extends RecyclerView.Adapter {
    private List<HVEAsset> dataList = new ArrayList<>();

    protected int imageWidth = ScreenUtil.dp2px(64);

    private HuaweiVideoEditor.ImageCallback imageCallback;

    private EditPreviewViewModel viewModel;

    private boolean isClick;

    private int position;

    private Context context;

    public ImageTrackRecyclerViewAdapter(EditPreviewViewModel viewModel, Context context) {
        this.viewModel = viewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_image_lane, parent, false);
        MainViewHolder holder = new MainViewHolder(view);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClick) {
                    setCurrentSelect(true, holder.getAdapterPosition());
                } else {
                    setCurrentSelect(holder.getAdapterPosition() != getPosition(), holder.getAdapterPosition());
                }

                notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HVEAsset asset = dataList.get(position);

        if (!(holder instanceof MainViewHolder)) {
            return;
        }
        if (position == (dataList.size() - 1)) {
            ((MainViewHolder) holder).trans.setVisibility(View.GONE);
        } else {
            ((MainViewHolder) holder).trans.setVisibility(View.VISIBLE);
            ((MainViewHolder) holder).trans.getParent().requestDisallowInterceptTouchEvent(false);
            ((MainViewHolder) holder).trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!viewModel.setTransitionPanel(position)) {
                        ToastWrapper.makeText(context, context.getString(R.string.lowvideo), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        imageCallback = new HuaweiVideoEditor.ImageCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, long timeStamp) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap imageAssetBitmap = BitmapUtils.centerSquareScaleBitmap(bitmap, imageWidth);
                        ((MainViewHolder) holder).imageView.setImageBitmap(imageAssetBitmap);
                    }
                });
            }

            @Override
            public void onFail(int errorCode) {

            }
        };

        if (asset instanceof HVEVideoAsset) {
            HVEVideoAsset videoAsset = (HVEVideoAsset) asset;
            videoAsset.getFirstFrame(imageWidth, imageWidth, imageCallback);
        } else if (asset instanceof HVEImageAsset) {
            HVEImageAsset imageAsset = (HVEImageAsset) asset;
            imageAsset.getFirstFrame(imageWidth, imageWidth, imageCallback);
        }

        if (getPosition() == position && isClick()) {
            viewModel.pause();
            viewModel.setChoiceAsset(null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewModel.setCurrentTimeLine(asset.getStartTime());
                }
            }, 100);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateData(List<HVEAsset> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageView trans;

        View layout;

        MainViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_media);
            layout = itemView.findViewById(R.id.content_layout);
            trans = itemView.findViewById(R.id.iv_trans);
        }
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setCurrentSelect(boolean isClick, int position) {
        this.isClick = isClick;
        this.position = position;
    }
}
