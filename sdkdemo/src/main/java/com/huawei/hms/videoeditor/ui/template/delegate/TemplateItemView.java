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

package com.huawei.hms.videoeditor.ui.template.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.CompactDecimalFormat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.huawei.hms.videoeditor.template.HVETemplateInfo;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.template.adapter.ItemViewDelegate;
import com.huawei.hms.videoeditor.ui.template.adapter.RViewHolder;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

public class TemplateItemView implements ItemViewDelegate<HVETemplateInfo> {

    private static final String TAG = "TemplateItemView";

    private final Context mContext;

    private int mImageViewMaxWidth;

    private OnItemClickListener mOnItemClickListener;

    public TemplateItemView(Context context, OnItemClickListener onItemClickListener) {
        mContext = context;
        mOnItemClickListener = onItemClickListener;
        mImageViewMaxWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 40))) / 2;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.adapter_moduld_pager_item;
    }

    @Override
    public void refreshWidthAndHeight() {
        mImageViewMaxWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 40))) / 2;
    }

    @Override
    public boolean isForViewType(HVETemplateInfo item, int position) {
        return true;
    }

    @Override
    public void convert(RViewHolder holder, HVETemplateInfo template, int dataPosition, int position) {
        ConstraintLayout mContentLayout = holder.getView(R.id.content_layout);
        ImageView mImageView = holder.getView(R.id.imageView);
        TextView mNameView = holder.getView(R.id.tv_name);
        TextView mTime = holder.getView(R.id.tv_use_info);
        TextView mUseView = holder.getView(R.id.tv_use);
        TextView mDescView = holder.getView(R.id.tv_desc);
        TextView mPart = holder.getView(R.id.tv_part);

        int mImageViewMaxHeight = 0;
        try {
            if (!TextUtils.isEmpty(template.getAspectRatio())) {
                String[] split = template.getAspectRatio().split("\\*");
                if (split.length != 2) {
                    SmartLog.e(TAG, "aspectRatio value Illegal");
                } else {
                    String width = split[0];
                    String height = split[1];
                    int realWidth = Integer.parseInt(width.trim());
                    int realHeight = Integer.parseInt(height.trim());
                    mImageViewMaxHeight = (realHeight * mImageViewMaxWidth) / realWidth;
                    SmartLog.i(TAG, "width:" + realWidth + "<=>" + mImageViewMaxWidth);
                    SmartLog.i(TAG, "height:" + realHeight + "<=>" + mImageViewMaxHeight);
                }
            }
        } catch (RuntimeException e) {
            mImageViewMaxHeight = mImageViewMaxWidth;
            SmartLog.e(TAG, "size is Invalid params");
        }
        ViewGroup.LayoutParams itemParams = mContentLayout.getLayoutParams();
        mContentLayout.setLayoutParams(itemParams);
        ConstraintLayout.LayoutParams imageParams =
            new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, mImageViewMaxHeight);
        imageParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        imageParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        imageParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        mImageView.setLayoutParams(imageParams);
        Glide.with(mContext)
            .load(template.getPreviewUrl())
            .override(mImageViewMaxWidth, mImageViewMaxHeight)
            .apply(new RequestOptions().transform(
                new MultiTransformation<>(new CenterCrop(), new RoundedCorners(SizeUtils.dp2Px(mContext, 8)))))
            .placeholder(R.drawable.color_20_100_8_bg)
            .error(R.drawable.color_20_100_8_bg)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                    boolean isFirstResource) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onLoadFailed(template);
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                    DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            })
            .into(mImageView);

        mNameView.setText(template.getName());

        Drawable durationAmount = mContext.getDrawable(R.drawable.icon_moban_time);
        durationAmount.setBounds(0, 0, SizeUtils.dp2Px(mContext, 12), SizeUtils.dp2Px(mContext, 12));
        mTime.setCompoundDrawables(durationAmount, null, null, null);
        String duration = TimeUtils.makeTimeString(mContext, template.getDuration() * 1000);
        mTime.setText(" " +duration);
        if (template.getSegmentsCount() > 0) {
            mPart.setVisibility(View.VISIBLE);
        } else {
            mPart.setVisibility(View.GONE);
        }

        Drawable partAmount = mContext.getDrawable(R.drawable.icon_moban_part);
        partAmount.setBounds(0, 0, SizeUtils.dp2Px(mContext, 12), SizeUtils.dp2Px(mContext, 12));
        mPart.setCompoundDrawables(partAmount, null, null, null);
        mPart.setText(" " +template.getSegmentsCount());

        mDescView.setText(template.getDescription());

        String tempNum = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tempNum = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
                    .format(template.getDownloadCount());
        }
        mUseView.setText(" " + tempNum);

        holder.itemView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        }));
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onLoadFailed(HVETemplateInfo template);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
