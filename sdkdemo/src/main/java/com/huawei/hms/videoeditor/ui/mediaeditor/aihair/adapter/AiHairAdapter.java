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

package com.huawei.hms.videoeditor.ui.mediaeditor.aihair.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;

public class AiHairAdapter extends RCommandAdapter<CloudMaterialBean> {
    private volatile int currentSelectedPosition = -1;

    private final Map<String, CloudMaterialBean> mCurrentDownloadingMap = new LinkedHashMap<>();

    private final Map<String, CloudMaterialBean> mFirstDataMap = new LinkedHashMap<>();

    private OnAiHairAdapterItemClickListener onAiHairAdapterItemClickListener;

    public AiHairAdapter(Context context, List<CloudMaterialBean> list, int layoutId) {
        super(context, list, layoutId);
    }

    public void setOnItemClickListener(OnAiHairAdapterItemClickListener listener) {
        onAiHairAdapterItemClickListener = listener;
    }

    @Override
    protected void convert(RViewHolder holder, CloudMaterialBean materialItem, int dataPosition, int position) {
        View holderView = holder.getView(R.id.item_select_view_ai_hair);
        ImageView mItemIv = holder.getView(R.id.item_image_view_ai_hair);
        TextView mTitleTv = holder.getView(R.id.item_name_ai_hair);
        ImageView mDownloadIv = holder.getView(R.id.item_download_view_ai_hair);
        ProgressBar mDownloadPb = holder.getView(R.id.item_progress_ai_hair);

        Glide.with(mContext)
            .load(materialItem.getPreviewUrl())
            .apply(new RequestOptions().transform(
                new MultiTransformation<>(new CenterCrop(), new RoundedCorners(SizeUtils.dp2Px(mContext, 4)))))
            .addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                    boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                    DataSource dataSource, boolean isFirstResource) {
                    removeFirstScreenData(materialItem);
                    return false;
                }
            })
            .into(mItemIv);

        mTitleTv.setText(materialItem.getName());
        holderView.setSelected(currentSelectedPosition == position);
        if (!StringUtil.isEmpty(materialItem.getLocalPath()) || position == 0) {
            mDownloadIv.setVisibility(View.INVISIBLE);
            mDownloadPb.setVisibility(View.INVISIBLE);
        } else {
            mDownloadIv.setVisibility(currentSelectedPosition == position ? View.INVISIBLE : View.VISIBLE);
            mDownloadPb.setVisibility(currentSelectedPosition == position ? View.VISIBLE : View.INVISIBLE);
        }

        if (mCurrentDownloadingMap.containsKey(materialItem.getId())) {
            mDownloadIv.setVisibility(View.INVISIBLE);
            mDownloadPb.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            mDownloadIv.setVisibility(View.INVISIBLE);
            if (onAiHairAdapterItemClickListener != null) {
                if (!StringUtil.isEmpty(materialItem.getLocalPath())
                    && (FileUtil.isPathExist(materialItem.getLocalPath()))) {
                    onAiHairAdapterItemClickListener.onAdapterItemClick(position, dataPosition);
                } else {
                    if (!mCurrentDownloadingMap.containsKey(materialItem.getId())) {
                        onAiHairAdapterItemClickListener.onItemDownloadClick(position, dataPosition);
                        mDownloadPb.setVisibility(View.VISIBLE);
                    }
                }
            }
        }));

        mDownloadIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            mDownloadIv.setVisibility(View.INVISIBLE);
            mDownloadPb.setVisibility(View.VISIBLE);
            if (onAiHairAdapterItemClickListener != null && !mCurrentDownloadingMap.containsKey(materialItem.getId())) {
                onAiHairAdapterItemClickListener.onItemDownloadClick(position, dataPosition);
            }
        }));
    }

    public int getSelectPosition() {
        return currentSelectedPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.currentSelectedPosition = selectPosition;
    }

    public void addDownloadMap(CloudMaterialBean materialsData) {
        mCurrentDownloadingMap.put(materialsData.getId(), materialsData);
    }

    public void removeDownloadMap(String contentId) {
        mCurrentDownloadingMap.remove(contentId);
    }

    public void addFirstScreenData(CloudMaterialBean materialsData) {
        if (!mFirstDataMap.containsKey(materialsData.getId())) {
            mFirstDataMap.put(materialsData.getId(), materialsData);
        }
    }

    public void removeFirstScreenData(CloudMaterialBean materialsData) {
        if (materialsData == null || mFirstDataMap.size() == 0) {
            return;
        }
        mFirstDataMap.remove(materialsData.getId());
    }

    public interface OnAiHairAdapterItemClickListener {
        void onAdapterItemClick(int position, int dataPosition);

        void onItemDownloadClick(int position, int dataPosition);
    }
}
