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
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.sdk.materials.network.response.MaterialsCloudBean;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.FoldScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class EditTextFontAdapter extends RCommandAdapter<MaterialsCloudBean> {
    private static final String TAG = "EditTextFontAdapter";

    private final Map<String, MaterialsCloudBean> mDownloadingMap = new LinkedHashMap<>();

    private final Map<String, MaterialsCloudBean> mFirstScreenMap = new LinkedHashMap<>();

    private volatile int mSelectPosition = -1;

    private final int mImageViewWidth;

    private final int mImageViewHeight;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public EditTextFontAdapter(Context context, List<MaterialsCloudBean> list, int layoutId) {
        super(context, list, layoutId);
        int screenWidth = SizeUtils.screenWidth(mContext);
        int marginWidth = SizeUtils.dp2Px(mContext, Constant.IMAGE_WIDTH_MARGIN);
        if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(context)) {
            mImageViewWidth = (screenWidth - marginWidth) / Constant.IMAGE_COUNT_8;
        } else {
            mImageViewWidth = (screenWidth - marginWidth) / Constant.IMAGE_COUNT_4;
        }
        mImageViewHeight = mImageViewWidth / Constant.TEXT_HALF;
    }

    public void addFirstScreenMaterial(MaterialsCloudBean item) {
        if (!mFirstScreenMap.containsKey(item.getId())) {
            mFirstScreenMap.put(item.getId(), item);
        }
    }

    public void removeFirstScreenMaterial(MaterialsCloudBean materialsCutContent) {
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
    protected void convert(RViewHolder holder, MaterialsCloudBean item, int dataPosition, int position) {
        View mSelectView = holder.getView(R.id.item_select_view);
        ImageView mItemIv = holder.getView(R.id.item_image_view);
        ImageView mDownloadIv = holder.getView(R.id.item_download_view);
        ProgressBar mDownloadPb = holder.getView(R.id.item_progress);
        ImageView mDownloadPbCenterTv = holder.getView(R.id.progress_center_iv);
        mSelectView.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(mImageViewWidth, mImageViewHeight);
        params.bottomMargin = SizeUtils.dp2Px(mContext, Constant.TEXT_FONT_MARGIN_BOTTOM);
        holder.itemView.setLayoutParams(params);
        Glide.with(mContext)
            .load(item.getPreviewUrl())
            .placeholder(R.drawable.sticker_normal_bg)
            .apply(new RequestOptions().transform(new MultiTransformation<>(new CenterInside(),
                new RoundedCorners(SizeUtils.dp2Px(mContext, Constant.RADIUS_VALUE)))))
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
            mDownloadPb.setVisibility(View.GONE);
            mDownloadPbCenterTv.setVisibility(View.GONE);
        } else {
            mDownloadPbCenterTv.setVisibility(View.GONE);
            mDownloadPb.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
            mDownloadIv.setVisibility(mSelectPosition == position ? View.INVISIBLE : View.VISIBLE);
        }
        if (mDownloadingMap.containsKey(item.getId())) {
            mDownloadIv.setVisibility(View.GONE);
            mDownloadPb.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            mDownloadIv.setVisibility(View.INVISIBLE);
            if (mOnItemClickListener != null) {
                if (!StringUtil.isEmpty(item.getLocalPath())) {
                    holder.itemView.setAlpha(0.9f);
                    mOnItemClickListener.onItemClick(position, dataPosition);
                } else {
                    if (!mDownloadingMap.containsKey(item.getId())) {
                        mOnItemClickListener.onDownloadClick(position, dataPosition);
                        mDownloadPb.setVisibility(View.VISIBLE);
                        mDownloadPbCenterTv.setVisibility(View.VISIBLE);
                    }
                }
            }
        }));

        mDownloadIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            mDownloadIv.setVisibility(View.INVISIBLE);
            mDownloadPb.setVisibility(View.VISIBLE);
            if (mOnItemClickListener != null && !mDownloadingMap.containsKey(item.getId())) {
                mOnItemClickListener.onDownloadClick(position, dataPosition);
                mDownloadPbCenterTv.setVisibility(View.VISIBLE);
            }
        }));
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void addDownloadMaterial(MaterialsCloudBean item) {
        mDownloadingMap.put(item.getId(), item);
    }

    public void removeDownloadMaterial(String contentId) {
        mDownloadingMap.remove(contentId);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int dataPosition);

        void onDownloadClick(int position, int dataPosition);
    }
}
