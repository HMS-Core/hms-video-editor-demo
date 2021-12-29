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

package com.huawei.hms.videoeditor.ui.common.adapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectAdapter<VL, T extends List<VL>, VH extends SelectAdapter.ThisViewHolder>
    extends RecyclerView.Adapter<VH> {

    private static final String TAG = "SelectAdapter";

    public T mData;

    protected Context mCtx;

    private int mlayout;

    protected int mPosition = 0;

    private VH selectedItem;

    private Class mCls;

    private OnItemClickListener onItemClickListener;

    private OnSelectChangeListener onSelectChangeListener;

    public SelectAdapter(Context context, T t, int resId, Class<VH> cls) {
        this.mCtx = context;
        this.mData = t;
        this.mlayout = resId;
        this.mCls = cls;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            Constructor constructor = this.mCls.getConstructor(this.getClass(), View.class);
            return (VH) constructor.newInstance(this, LayoutInflater.from(mCtx).inflate(mlayout, parent, false));
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
            | InvocationTargetException e) {
            SmartLog.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (mPosition == position) {
            holder.onSelect(holder.itemView);
            selectedItem = holder;
        } else {
            holder.onUnSelect(holder.itemView);
        }
        holder.setContent(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public abstract class ThisViewHolder extends RecyclerView.ViewHolder {

        public ThisViewHolder(@NonNull View itemView) {
            super(itemView);
            findView(itemView);
            itemView.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curPosition = getAdapterPosition();
                    if (curPosition != mPosition) {
                        if (onSelectChangeListener != null) {
                            onSelectChangeListener.onSelectChanged(mData.get(curPosition), curPosition, mPosition);
                        }
                        onSelect(itemView);
                        if (selectedItem != null) {
                            onUnSelect(selectedItem.itemView);
                        }
                        mPosition = curPosition;
                        selectedItem = (VH) ThisViewHolder.this;
                    }
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(mData.get(curPosition), curPosition);
                    }
                }
            }));
        }

        public <VL> void setContent(VL k) {
            bindView(k);
        }

        protected abstract void findView(View view);

        protected abstract <VL> void bindView(VL k);

        protected abstract void onSelect(View view);

        protected abstract void onUnSelect(View view);
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public int getPosition() {
        return this.mPosition;
    }

    public interface OnItemClickListener<VL> {
        <VL> void onItemClick(VL k, int position);
    }

    public interface OnSelectChangeListener<VL> {
        <VL> void onSelectChanged(VL k, int newPosition, int oldPosition);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnSelectChangedListener(OnSelectChangeListener onSelectChangedListener) {
        this.onSelectChangeListener = onSelectChangedListener;
    }
}
