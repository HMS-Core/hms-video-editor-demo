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

package com.huawei.hms.videoeditor.ui.template.adapter;

import java.text.NumberFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;
import com.huawei.hms.videoeditor.ui.template.utils.ModuleSelectManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ModulePickSelectAdapter extends RecyclerView.Adapter<ModulePickSelectAdapter.ViewHolder> {
    private Context mContext;
    private List<MaterialData> mAllMediaList;
    private ModuleSelectManager mModuleManager;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ModulePickSelectAdapter(Context context, List<MaterialData> list) {
        mContext = context;
        mAllMediaList = list;
        mModuleManager = ModuleSelectManager.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_module_select_video_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialData item = mAllMediaList.get(position);
        if (item == null) {
            return;
        }
        holder.mDurationTv.setText(mContext.getResources().getQuantityString(R.plurals.seconds_time,
                (int) BigDecimalUtils.div(item.getValidDuration(), 1000f, 1),
                NumberFormat.getInstance().format(BigDecimalUtils.div(item.getValidDuration(), 1000f, 1))));
        if (!TextUtils.isEmpty(item.getPath())) {
            holder.mDurationTv.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.mMediaIv.setVisibility(View.VISIBLE);
            holder.mIvNoPathSelect.setVisibility(View.INVISIBLE);
            holder.mIvNoPathNormal.setVisibility(View.INVISIBLE);
            holder.mDeleteIv.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(item.getPath())
                    .apply(new RequestOptions()
                            .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(SizeUtils.dp2Px(mContext, 3)))))
                    .into(holder.mMediaIv);
            holder.mMaskView.setVisibility(View.VISIBLE);
        } else {
            if (position == mModuleManager.getCurrentIndex()) {
                holder.mMediaIv.setVisibility(View.INVISIBLE);
                holder.mIvNoPathSelect.setVisibility(View.VISIBLE);
                holder.mIvNoPathNormal.setVisibility(View.INVISIBLE);
            } else {
                holder.mMediaIv.setVisibility(View.INVISIBLE);
                holder.mIvNoPathSelect.setVisibility(View.INVISIBLE);
                holder.mIvNoPathNormal.setVisibility(View.VISIBLE);
            }
            holder.mDurationTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_70));
            holder.mDeleteIv.setVisibility(View.GONE);
            holder.mMaskView.setVisibility(View.INVISIBLE);
        }

        holder.mDeleteIv.setOnClickListener(new OnClickRepeatedListener((v) -> {
            mModuleManager.removeSelectItemAndSetIndex(position);
        }));
        if(mOnItemClickListener != null) {
            holder.mMaskView.setOnClickListener(new OnClickRepeatedListener((v) -> mOnItemClickListener.onItemClick(position)));
        }
    }

    @Override
    public int getItemCount() {
        return mAllMediaList == null ? 0 : mAllMediaList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvNoPathSelect;
        ImageView mIvNoPathNormal;
        ImageView mMediaIv;
        View mMaskView;
        TextView mDurationTv;
        ImageView mDeleteIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvNoPathSelect = itemView.findViewById(R.id.iv_no_path_select);
            mIvNoPathNormal = itemView.findViewById(R.id.iv_no_path_normal);
            mMediaIv = itemView.findViewById(R.id.iv_media);
            mMaskView = itemView.findViewById(R.id.mask_view);
            mDurationTv = itemView.findViewById(R.id.tv_duration);
            mDeleteIv = itemView.findViewById(R.id.iv_delete);
        }
    }
}
