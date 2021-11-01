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

package com.huawei.hms.videoeditor.ui.mediaeditor.effect.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class EffectItemAdapter extends RCommandAdapter<CloudMaterialBean> {
    private static final String TAG = "EffectItemAdapter";

    private final Map<String, CloudMaterialBean> mDownloadingMap = new LinkedHashMap<>();

    private final Map<String, CloudMaterialBean> mFirstScreenMap = new LinkedHashMap<>();

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
        if (!mDownloadingMap.containsKey(item.getId())) {
            mDownloadingMap.put(item.getId(), item);
        }
    }

    public void removeDownloadMaterial(String contentId) {
        mDownloadingMap.remove(contentId);
    }

    public void addFirstScreenMaterial(CloudMaterialBean item) {
        if (!mFirstScreenMap.containsKey(item.getId())) {
            mFirstScreenMap.put(item.getId(), item);
        }
    }

    public void removeFirstScreenMaterial(CloudMaterialBean materialsCutContent) {
        if (materialsCutContent == null || mFirstScreenMap.size() == 0) {
            SmartLog.e(TAG, "input materials is null");
            return;
        }
        mFirstScreenMap.remove(materialsCutContent.getId());
        if (mFirstScreenMap.size() == 0) {
            SmartLog.w(TAG, "HianalyticsEvent10007 postEvent");
        }
    }

    @Override
    protected void convert(RViewHolder holder, CloudMaterialBean item, int dataPosition, int position) {
        SmartLog.i(TAG, "onBindViewHolder:" + position + "&&" + item.getPreviewUrl());
        ConstraintLayout mContentView = holder.getView(R.id.item_content);
        mContentView.setTag(position);
        View mSelectView = holder.getView(R.id.item_select_view);
        ImageView mItemIv = holder.getView(R.id.item_image_view);
        ImageView mDownloadIv = holder.getView(R.id.item_download_view);
        View mDownloadPb = holder.getView(R.id.item_progress);
        TextView mNameTv = holder.getView(R.id.item_name);

        holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewHeight));
        mContentView.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewHeight));
        mSelectView.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        mItemIv.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        mSelectView.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);

        Glide.with(mContext)
            .load(item.getPreviewUrl())
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
                    removeFirstScreenMaterial(item);
                    return false;
                }
            })
            .into(mItemIv);

        mItemIv.setVisibility(View.VISIBLE);
        mNameTv.setText(item.getName());
        mNameTv.setVisibility(View.VISIBLE);

        if (!StringUtil.isEmpty(item.getLocalPath()) || item.getId().equals("-1")) {
            mDownloadIv.setVisibility(View.GONE);
            mDownloadPb.setVisibility(View.GONE);
        } else {
            mDownloadIv.setVisibility(mSelectPosition == position ? View.INVISIBLE : View.VISIBLE);
            mDownloadPb.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        }

        if (mDownloadingMap.containsKey(item.getId())) {
            mDownloadIv.setVisibility(View.GONE);
            mDownloadPb.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            if (mOnItemClickListener == null) {
                return;
            }
            if (!StringUtil.isEmpty(item.getLocalPath()) || item.getId().equals("-1")) {
                mOnItemClickListener.onItemClick(position);
            } else {
                if (!mDownloadingMap.containsKey(item.getId())) {
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
