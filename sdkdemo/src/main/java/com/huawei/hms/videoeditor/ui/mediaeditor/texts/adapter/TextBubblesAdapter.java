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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.FoldScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class TextBubblesAdapter extends RCommandAdapter<CloudMaterialBean> {
    private static final String TAG = "TextBubblesAdapter";

    private final Map<String, CloudMaterialBean> mDownloadingMap = new LinkedHashMap<>();

    private final Map<String, CloudMaterialBean> mFirstScreenMap = new LinkedHashMap<>();

    private int mFirstScreenCount = 0;

    private final int mImageViewWidth;

    private volatile int mSelectPosition = -1;

    private ItemClickListener mOnItemClickListener;

    public void addFirstScreenMaterial(CloudMaterialBean item) {
        if (!mFirstScreenMap.containsKey(item.getId())) {
            mFirstScreenMap.put(item.getId(), item);
        }
        mFirstScreenCount = mFirstScreenMap.size();
    }

    public void removeFirstScreenMaterial(CloudMaterialBean materialsCutContent) {
        if (materialsCutContent == null || mFirstScreenMap.size() == 0) {
            SmartLog.e(TAG, "input materials is null");
            return;
        }
        mFirstScreenMap.remove(materialsCutContent.getId());
    }

    public TextBubblesAdapter(Context context, List<CloudMaterialBean> list, int layoutId) {
        super(context, list, layoutId);
        if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(context)) {
            mImageViewWidth = (SizeUtils.screenWidth(context) - (SizeUtils.dp2Px(context, 56))) / 8;
        } else {
            boolean isPortrait = ScreenUtil.isPortrait(context);
            int spanCount = 4;
            if (!isPortrait) {
                spanCount = 8;
            }
            mImageViewWidth = (SizeUtils.screenWidth(context) - (SizeUtils.dp2Px(context, 56))) / spanCount;
        }
    }

    @Override
    protected void convert(RViewHolder holder, CloudMaterialBean item, int dataPosition, int position) {
        View mSelectView = holder.getView(R.id.item_select_view);
        View mNormalView = holder.getView(R.id.item_normal_view);
        ImageView mItemIv = holder.getView(R.id.item_image_view);
        if (position == 0) {
            mItemIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            mItemIv.setScaleType(ImageView.ScaleType.FIT_XY);

        }
        ImageView mDownloadIv = holder.getView(R.id.item_download_view);
        ProgressBar mHwProgressBar = holder.getView(R.id.item_progress);

        holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        mSelectView.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        mNormalView.setVisibility(mSelectPosition != position ? View.VISIBLE : View.INVISIBLE);
        Glide.with(mContext)
            .load(!StringUtil.isEmpty(item.getPreviewUrl()) ? item.getPreviewUrl() : item.getLocalDrawableId())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
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
        if (!StringUtil.isEmpty(item.getLocalPath())) {
            mDownloadIv.setVisibility(View.GONE);
            mHwProgressBar.setVisibility(View.GONE);
        } else {
            mHwProgressBar.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
            mDownloadIv.setVisibility(mSelectPosition == position ? View.INVISIBLE : View.VISIBLE);
        }
        if (mDownloadingMap.containsKey(item.getId())) {
            mDownloadIv.setVisibility(View.GONE);
            mHwProgressBar.setVisibility(View.VISIBLE);
        }
        mNormalView.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new OnClickRepeatedListener(v -> {
            mDownloadIv.setVisibility(View.INVISIBLE);
            mHwProgressBar.setVisibility(View.VISIBLE);
            if (mOnItemClickListener != null) {
                if (!StringUtil.isEmpty(item.getLocalPath()) || item.getId().equals("-1")) {
                    mOnItemClickListener.onItemClickListener(position, dataPosition);
                } else {
                    if (!mDownloadingMap.containsKey(item.getId())) {
                        mOnItemClickListener.onDownloadClick(position, dataPosition);
                    }
                }
            }
        }));

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void setSelectPosition(int mSelectPosition) {
        this.mSelectPosition = mSelectPosition;
    }

    public void addDownloadMaterial(CloudMaterialBean item) {
        mDownloadingMap.put(item.getId(), item);
    }

    public void removeDownloadMaterial(String contentId) {
        mDownloadingMap.remove(contentId);
    }

    public interface ItemClickListener {
        void onItemClickListener(int position, int dataPosition);

        void onDownloadClick(int position, int dataPosition);
    }
}
