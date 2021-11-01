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

package com.huawei.hms.videoeditor.ui.mediaeditor.audio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.bean.AudioData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.common.view.audio.AudioColumnView;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class LocalMusicAdapter extends PagedListAdapter<AudioData, LocalMusicAdapter.ViewHolder> {

    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public LocalMusicAdapter(Context context) {
        super(new DiffUtil.ItemCallback<AudioData>() {
            @Override
            public boolean areItemsTheSame(@NonNull AudioData oldItem, @NonNull AudioData newItem) {
                return oldItem.getPath().equals(newItem.getPath());
            }

            @Override
            public boolean areContentsTheSame(@NonNull AudioData oldItem, @NonNull AudioData newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_local_music_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioData item = getItem(position);
        if (item == null) {
            return;
        }
        holder.mLocalPlayCv.setVisibility(item.isPlaying() ? View.VISIBLE : View.GONE);
        if (item.isPlaying()) {
            holder.mLocalCv.setVisibility(View.INVISIBLE);
            holder.mLocalPlayCv.setVisibility(View.VISIBLE);
            holder.mAudioColumnView.setVisibility(View.VISIBLE);
            holder.mAudioColumnView.start();
        } else {
            holder.mLocalCv.setVisibility(View.VISIBLE);
            holder.mLocalPlayCv.setVisibility(View.GONE);
            holder.mAudioColumnView.setVisibility(View.GONE);
            holder.mAudioColumnView.stop();
        }
        holder.mNameTv.setText(item.getName());
        holder.mDurationTv.setText(TimeUtils.makeTimeString(mContext, item.getDuration()));
        holder.itemView.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position, item);
            }
        }));
        holder.mUseTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onUseClick(item);
            }
        }));
    }

    public interface OnItemClickListener {
        void onItemClick(int position, AudioData item);

        void onUseClick(AudioData item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView mLocalPlayCv;

        AudioColumnView mAudioColumnView;

        CardView mLocalCv;

        TextView mNameTv;

        TextView mDurationTv;

        TextView mUseTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLocalPlayCv = itemView.findViewById(R.id.local_play_cv);
            mAudioColumnView = itemView.findViewById(R.id.audio_column_view);
            mLocalCv = itemView.findViewById(R.id.local_cv);
            mNameTv = itemView.findViewById(R.id.local_name_tv);
            mDurationTv = itemView.findViewById(R.id.local_duration_tv);
            mUseTv = itemView.findViewById(R.id.local_use_tv);
        }
    }
}
