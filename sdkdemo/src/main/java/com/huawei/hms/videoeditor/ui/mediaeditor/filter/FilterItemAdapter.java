
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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;

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
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilterItemAdapter extends RCommandAdapter<CloudMaterialBean> {
    private volatile int mSelectPosition = -1;

    private final Map<String, CloudMaterialBean> mDownloadingMap = new LinkedHashMap<>();

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public FilterItemAdapter(Context context, List<CloudMaterialBean> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    protected void convert(RViewHolder rViewHolder, CloudMaterialBean materialBean, int dataPosition, int position) {
        View selectView = rViewHolder.getView(R.id.item_select_view);
        ImageFilterView itemIv = rViewHolder.getView(R.id.item_image_view);
        ImageView clearicom = rViewHolder.getView(R.id.clearicom);
        TextView mameTv = rViewHolder.getView(R.id.item_name);
        ImageView downloadIv = rViewHolder.getView(R.id.item_download_view);
        ProgressBar downloadPb = rViewHolder.getView(R.id.item_progress);
        RelativeLayout rlEditName = rViewHolder.getView(R.id.fl_item_edit_view);

        clearicom.setVisibility(View.GONE);
        rlEditName.setVisibility(View.GONE);

        Glide.with(mContext)
            .load(materialBean.getPreviewUrl())
            .placeholder(R.drawable.filter_normal_bg)
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
                    return false;
                }
            })
            .into(itemIv);

        mameTv.setText(materialBean.getName());
        selectView.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        if (!StringUtil.isEmpty(materialBean.getLocalPath()) || position == 0) {
            downloadIv.setVisibility(View.GONE);
            downloadPb.setVisibility(View.GONE);
        } else {
            downloadIv.setVisibility(mSelectPosition == position ? View.INVISIBLE : View.VISIBLE);
            downloadPb.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);
        }

        if (mDownloadingMap.containsKey(materialBean.getId())) {
            downloadIv.setVisibility(View.GONE);
            downloadPb.setVisibility(View.VISIBLE);
        }

        rViewHolder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            downloadIv.setVisibility(View.INVISIBLE);
            if (mOnItemClickListener != null) {
                if (!StringUtil.isEmpty(materialBean.getLocalPath())) {
                    mOnItemClickListener.onItemClick(position, dataPosition);
                } else {
                    if (!mDownloadingMap.containsKey(materialBean.getId())) {
                        mOnItemClickListener.onDownloadClick(position, dataPosition);
                        downloadPb.setVisibility(View.VISIBLE);
                    }
                }
            }
        }));

        downloadIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            downloadIv.setVisibility(View.INVISIBLE);
            downloadPb.setVisibility(View.VISIBLE);
            if (mOnItemClickListener != null && !mDownloadingMap.containsKey(materialBean.getId())) {
                mOnItemClickListener.onDownloadClick(position, dataPosition);
            }
        }));
    }

    public void removeDownloadMaterial(String contentId) {
        mDownloadingMap.remove(contentId);
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
    }

    public void addDownloadMaterial(CloudMaterialBean item) {
        mDownloadingMap.put(item.getId(), item);
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int dataPosition);

        void onDownloadClick(int position, int dataPosition);
    }

}
