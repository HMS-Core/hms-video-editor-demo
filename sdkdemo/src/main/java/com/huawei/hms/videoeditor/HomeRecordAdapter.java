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

package com.huawei.hms.videoeditor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.sdk.HVEProject;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.utils.Utils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeRecordAdapter extends RecyclerView.Adapter<HomeRecordAdapter.ViewHolder> {
    private final Context mContext;

    private List<HVEProject> mRecords;

    private SimpleDateFormat mSimpleDateFormat;

    private OnItemClickListener mOnItemClickListener;

    private boolean mIsEditStatus = false;

    private List<HVEProject> mSelectList = new ArrayList<>();

    private final int mImageViewMaxWidth;

    public void setIsEditStatus(boolean mIsEditStatus) {
        this.mIsEditStatus = mIsEditStatus;
        Intent intent = new Intent();
        if (!mIsEditStatus) {
            intent.setAction("TABLAYOUT-VISIBLE");
            mSelectList.clear();
        } else {
            intent.setAction("TABLAYOUT-GONE");
        }
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        notifyDataSetChanged();
    }

    public boolean getIsEditStatus() {
        return mIsEditStatus;
    }

    public List<HVEProject> getSelectList() {
        return mSelectList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setSelectList(List<HVEProject> mSelectList) {
        this.mSelectList.clear();
        this.mSelectList.addAll(mSelectList);
    }

    public HomeRecordAdapter(Context context, List<HVEProject> records) {
        mContext = context;
        mRecords = records;
        mSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);
        mImageViewMaxWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 32.0f)));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        HVEProject item = mRecords.get(position);
        int mImageViewMaxHeight = (int) (mImageViewMaxWidth / 4.31f);
        ViewGroup.LayoutParams itemParams = holder.mContentLayout.getLayoutParams();
        ConstraintLayout.LayoutParams imageParams =
            new ConstraintLayout.LayoutParams(mImageViewMaxHeight, mImageViewMaxHeight);

        imageParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        imageParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        imageParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        if (mContext instanceof Activity && Utils.isMateX((Activity) mContext)) {
            imageParams.width = ScreenUtil.dp2px(156);
            imageParams.height = ScreenUtil.dp2px(88);
        }
        holder.mPictureIv.setLayoutParams(imageParams);
        holder.mContentLayout.setLayoutParams(itemParams);
        Glide.with(mContext)
            .load(item.getCoverPath())
            .apply(new RequestOptions().transform(new MultiTransformation<>(new CenterCrop())))
            .into(holder.mPictureIv);
        holder.mTitleTv.setText(item.getName());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(Locale.ROOT, mContext.getResources().getString(R.string.main_update_text),
            mSimpleDateFormat.format(item.getUpdateTime())));
        holder.mModifyTv.setText(stringBuilder.toString());
        holder.mSizeTv.setText(SizeUtils.bytes2kb(item.getSize()));
        holder.mTimeTv.setText(TimeUtils.makeTimeString(mContext, item.getDuration()));
        holder.mMoreIv.setVisibility(mIsEditStatus ? View.INVISIBLE : View.VISIBLE);
        holder.mDeleteIv.setVisibility(mIsEditStatus ? View.VISIBLE : View.INVISIBLE);
        if (mIsEditStatus && mSelectList.contains(item)) {
            holder.mDeleteIv.setSelected(true);
        } else {
            holder.mDeleteIv.setSelected(false);
        }
        holder.itemView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnItemClickListener != null) {
                if (mIsEditStatus) {
                    if (mSelectList.contains(item)) {
                        mSelectList.remove(item);
                    } else {
                        mSelectList.add(item);
                    }
                    mOnItemClickListener.onSelectClick(mSelectList, position);
                } else {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        }));

        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemLongClick();
            }
            return true;
        });

        holder.mMoreIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onActionClick(v, item, position);
            }
        }));

        holder.mDeleteIv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnItemClickListener == null) {
                return;
            }

            if (mIsEditStatus) {
                if (mSelectList.contains(item)) {
                    holder.mDeleteIv.setSelected(false);
                    mSelectList.remove(item);
                } else {
                    holder.mDeleteIv.setSelected(true);
                    mSelectList.add(item);
                }
                mOnItemClickListener.onSelectClick(mSelectList, position);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick();

        void onSelectClick(List<HVEProject> mSelectList, int position);

        void onActionClick(View view, HVEProject position, int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout mContentLayout;

        ImageView mPictureIv;

        TextView mTitleTv;

        EditorTextView mModifyTv;

        TextView mSizeTv;

        TextView mTimeTv;

        ImageView mMoreIv;

        ImageView mDeleteIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContentLayout = itemView.findViewById(R.id.content_layout_home_item);
            mPictureIv = itemView.findViewById(R.id.iv_content);
            mTitleTv = itemView.findViewById(R.id.clip_title);
            mModifyTv = itemView.findViewById(R.id.update_time);
            mSizeTv = itemView.findViewById(R.id.clip_size);
            mTimeTv = itemView.findViewById(R.id.clip_time);
            mMoreIv = itemView.findViewById(R.id.clip_more);
            mDeleteIv = itemView.findViewById(R.id.clip_delete);
        }
    }
}
