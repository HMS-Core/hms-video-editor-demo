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

package com.huawei.hms.videoeditor.ui.mediaeditor.effect.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EffectItemAdapter extends RCommandAdapter<CloudMaterialBean> {
    private static final String TAG = "EffectItemAdapter";

    private final Map<String, CloudMaterialBean> downloadingMap = new LinkedHashMap<>();

    private final Map<String, CloudMaterialBean> firstScreenMap = new LinkedHashMap<>();

    private final int mImageViewWidth;

    private final int mImageViewHeight;

    private volatile int mSelectPosition = -1;

    private OnItemClickListener mOnItemClickListener;

    public EffectItemAdapter(Context context, List<CloudMaterialBean> list, int layoutId) {
        super(context, list, layoutId);
        mImageViewWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 62))) / 4;
        mImageViewHeight = mImageViewWidth + (SizeUtils.dp2Px(mContext, 26));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
    }

    public void addDownloadMaterial(CloudMaterialBean item) {
        if (!downloadingMap.containsKey(item.getId())) {
            downloadingMap.put(item.getId(), item);
        }
    }

    public void removeDownloadMaterial(String contentId) {
        downloadingMap.remove(contentId);
    }

    public void addFirstScreenMaterial(CloudMaterialBean item) {
        if (!firstScreenMap.containsKey(item.getId())) {
            firstScreenMap.put(item.getId(), item);
        }
    }

    public void removeFirstScreenMaterial(CloudMaterialBean cloudMaterialBean) {
        if (cloudMaterialBean == null || firstScreenMap.size() == 0) {
            SmartLog.e(TAG, "input materials is null");
            return;
        }
        firstScreenMap.remove(cloudMaterialBean.getId());
        if (firstScreenMap.size() == 0) {
            SmartLog.w(TAG, "HianalyticsEvent10007 postEvent");
        }
    }

    @Override
    protected void convert(RViewHolder rViewHolder, CloudMaterialBean cloudMaterialBean, int dataPosition,
        int position) {
        SmartLog.i(TAG, "onBindViewHolder:" + position + "&&" + cloudMaterialBean.getPreviewUrl());
        ConstraintLayout mContentView = rViewHolder.getView(R.id.item_content);
        mContentView.setTag(position);
        ImageView mDownloadIv = rViewHolder.getView(R.id.item_download_view);
        View mDownloadPb = rViewHolder.getView(R.id.item_progress);
        TextView mNameTv = rViewHolder.getView(R.id.item_name);
        View mSelectView = rViewHolder.getView(R.id.item_select_view);
        ImageView mItemIv = rViewHolder.getView(R.id.item_image_view);

        rViewHolder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewHeight));
        mSelectView.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        mItemIv.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        mContentView.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewHeight));
        mSelectView.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);

        Glide.with(mContext)
            .load(cloudMaterialBean.getPreviewUrl())
            .apply(new RequestOptions()
                .transform(new MultiTransformation<>(new RoundedCorners(SizeUtils.dp2Px(mContext, 4)))))
            .addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                    boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                    DataSource dataSource, boolean isFirstResource) {
                    removeFirstScreenMaterial(cloudMaterialBean);
                    return false;
                }
            })
            .into(mItemIv);

        mItemIv.setVisibility(View.VISIBLE);
        mNameTv.setText(cloudMaterialBean.getName());
        mNameTv.setVisibility(View.VISIBLE);

        if (!StringUtil.isEmpty(cloudMaterialBean.getLocalPath()) || cloudMaterialBean.getId().equals("-1")) {
            mDownloadPb.setVisibility(View.GONE);
            mDownloadIv.setVisibility(View.GONE);
        } else {
            mDownloadPb.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
            mDownloadIv.setVisibility(mSelectPosition == position ? View.INVISIBLE : View.VISIBLE);
        }

        if (downloadingMap.containsKey(cloudMaterialBean.getId())) {
            mDownloadPb.setVisibility(View.VISIBLE);
            mDownloadIv.setVisibility(View.GONE);
        }

        rViewHolder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            if (mOnItemClickListener == null) {
                return;
            }
            if (!StringUtil.isEmpty(cloudMaterialBean.getLocalPath()) || cloudMaterialBean.getId().equals("-1")) {
                mOnItemClickListener.onItemClick(position);
            } else {
                if (!downloadingMap.containsKey(cloudMaterialBean.getId())) {
                    mOnItemClickListener.onDownloadClick(position);
                }
            }
        }));
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDownloadClick(int position);
    }
}
