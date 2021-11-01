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

package com.huawei.hms.videoeditor.ui.mediapick.adapter;

import static com.huawei.hms.videoeditor.common.utils.ResUtils.getResources;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity;
import com.huawei.hms.videoeditor.ui.mediapick.manager.MediaPickManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MediaPickAdapter extends PagedListAdapter<MediaData, MediaPickAdapter.ViewHolder> {

    private static final String TAG = "MediaPickAdapter";

    private final Activity mContext;

    private final MediaPickManager manager;

    private final int mImageViewWidth;

    private List<MediaData> initMediaList;

    private long mReplaceValidDuration = -1;

    private boolean showChoiceView = true;

    public MediaPickAdapter(Activity context, int actionType) {
        super(new DiffUtil.ItemCallback<MediaData>() {
            @Override
            public boolean areItemsTheSame(@NonNull MediaData oldMediaData, @NonNull MediaData newItem) {
                return oldMediaData.getDirName().equals(newItem.getDirName())
                    && oldMediaData.getPath().equals(newItem.getPath());
            }

            @Override
            public boolean areContentsTheSame(@NonNull MediaData oldItem, @NonNull MediaData newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        manager = MediaPickManager.getInstance();
        mImageViewWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 48))) / 3;
        checkActionType();
    }

    private void checkActionType() {
        if (mContext instanceof MediaPickActivity) {
            MediaPickActivity pickActivity = (MediaPickActivity) mContext;
            if (pickActivity.mActionType == MediaPickActivity.ACTION_ADD_PIP_MEDIA_TYPE
                || pickActivity.mActionType == MediaPickActivity.ACTION_REPLACE_MEDIA_TYPE) {
                showChoiceView = false;
            }
        }
    }

    public void setReplaceValidDuration(long replaceValidDuration) {
        this.mReplaceValidDuration = replaceValidDuration;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_module_pick_video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mContentLayout.setLayoutParams(new RelativeLayout.LayoutParams(mImageViewWidth, mImageViewWidth));
        SmartLog.d(TAG, "position:" + position);
        SmartLog.d(TAG, "getAdapterPosition:" + holder.getAdapterPosition());
        MediaData item = getItem(holder.getAdapterPosition());
        if (item == null) {
            return;
        }

        Glide.with(mContext)
            .load(item.getPath())
            .override(mImageViewWidth, mImageViewWidth)
            .placeholder(R.drawable.color_20_100_8_bg)
            .error(R.drawable.color_20_100_8_bg)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.mMediaIv);

        if (item.isVideo()) {
            holder.mDurationTv.setVisibility(View.VISIBLE);

            if (item.getCutTrimOut() <= 0 && item.getCutTrimIn() <= 0) {
                holder.mDurationTv.setText(TimeUtils.makeTimeString(mContext, item.getDuration()));
            } else {
                holder.mDurationTv.setText(TimeUtils.makeTimeString(mContext,
                    item.getDuration() - item.getCutTrimIn() - item.getCutTrimOut()));
            }

            if (mReplaceValidDuration != -1) {
                if (item.getDuration() < mReplaceValidDuration) {
                    holder.mMaskView.setBackgroundResource(R.drawable.color_ccc_70_0_bg);
                } else {
                    holder.mMaskView.setBackgroundResource(R.color.transparent);
                }
            } else {
                holder.mMaskView.setBackgroundResource(R.color.transparent);
            }
        } else {
            holder.mDurationTv.setVisibility(View.GONE);
            holder.mMaskView.setBackgroundResource(R.color.transparent);
        }

        if (item.getIndex() != 0) {
            holder.mIndexTv.setText(String.valueOf(item.getIndex()));
            holder.mIndexTv.setBackgroundResource(R.drawable.ic_checkbox_selected_index);
        } else {
            holder.mIndexTv.setText("");
            holder.mIndexTv.setBackgroundResource(R.drawable.index_checkbox_normal);
        }
        if (isImport(item) || isCropped(item)) {
            holder.mTvHasImport.setVisibility(View.VISIBLE);
            if (isImport(item)) {
                holder.mTvHasImport.setText(R.string.media_imported);
            }
            if (isCropped(item)) {
                holder.mTvHasImport.setText(R.string.media_cropped);
            }
        } else {
            holder.mTvHasImport.setVisibility(View.INVISIBLE);
        }

        if (!showChoiceView) {
            holder.mIndexTv.setVisibility(View.GONE);
            holder.fullImageFilter.setVisibility(View.GONE);
            holder.mTvHasImport.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new OnClickRepeatedListener((v) -> {
            SmartLog.d(TAG, "onHandleMediaChoose position:" + position);
            int index = holder.getAdapterPosition();
            if (index >= 0) {
                onHandleMediaChoose(holder.getAdapterPosition(), getItem(index), holder);
            }
        }));

        holder.fullImageFilter.setOnClickListener(new OnClickRepeatedListener((v) -> {
            manager.setPreviewMediaData(item);
        }));
    }

    private void onHandleMediaChoose(int position, MediaData item, ViewHolder holder) {
        if (!showChoiceView) {
            if (mContext instanceof MediaPickActivity) {
                MediaPickActivity pickActivity = (MediaPickActivity) mContext;
                if (mReplaceValidDuration > 0 && FileUtil.isVideo(item.getPath())
                    && item.getDuration() < mReplaceValidDuration) {
                    return;
                }
                pickActivity.addPipOrReplaceItem(item);
            }
            return;
        }
        if (item.getIndex() == 0) {
            holder.mMediaIv.setContentDescription(mContext.getString(R.string.checked));
            if (!manager.addSelectItemAndSetIndex(item)) {
                Toast.makeText(mContext,
                    getResources().getQuantityString(R.plurals.media_max_send_images_or_videos_format,
                        manager.getMaxSelectCount(), manager.getMaxSelectCount()),
                    Toast.LENGTH_SHORT).show();
            }
        } else {
            holder.mMediaIv.setContentDescription(mContext.getString(R.string.check_out));
            if (position >= getItemCount() || position < 0) {
                return;
            }
            MediaData selectData = getItem(position);
            if (selectData != null) {
                int selectIndex = selectData.getIndex();
                MediaData entity;
                for (int i = selectIndex; i < manager.getSelectItemList().size(); i++) {
                    entity = manager.getSelectItemList().get(i);
                    manager.setNewIndexForSelectItem(entity, i);
                }
                manager.removeSelectItemAndSetIndex(item);
            }
        }
    }

    public void setInitMediaList(List<MediaData> mInitMediaList) {
        this.initMediaList = mInitMediaList;
    }

    private boolean isImport(MediaData item) {
        boolean hasImport = false;
        if (initMediaList == null || initMediaList.size() <= 0) {
            return false;
        }
        for (MediaData data : initMediaList) {
            if (data.getPath().equals(item.getPath()) || data.getName().equals(item.getName())) {
                hasImport = true;
                break;
            }
        }
        return hasImport;
    }

    private boolean isCropped(MediaData item) {
        return item.getCutTrimOut() > 0 || item.getCutTrimIn() > 0 || item.getGlLeftBottomX() != 0
            || item.getGlLeftBottomY() != 0 || item.getGlRightTopX() != 0 || item.getGlRightTopY() != 0;
    }

    public void setMaxSelectCount(int count) {
        manager.setMaxSelectCount(count);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mContentLayout;

        ImageFilterView mMediaIv;

        View mMaskView;

        ImageFilterView fullImageFilter;

        TextView mTvHasImport;

        TextView mDurationTv;

        TextView mIndexTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContentLayout = itemView.findViewById(R.id.content_layout);
            mMediaIv = itemView.findViewById(R.id.iv_media);
            mMaskView = itemView.findViewById(R.id.mask_view);
            fullImageFilter = itemView.findViewById(R.id.if_full);
            mTvHasImport = itemView.findViewById(R.id.tv_has_import);
            mDurationTv = itemView.findViewById(R.id.tv_duration);
            mIndexTv = itemView.findViewById(R.id.tv_index);
        }
    }
}
