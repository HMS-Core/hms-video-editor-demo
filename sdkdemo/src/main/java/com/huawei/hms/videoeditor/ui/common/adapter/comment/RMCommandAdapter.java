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

package com.huawei.hms.videoeditor.ui.common.adapter.comment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;

import androidx.recyclerview.widget.RecyclerView;

public class RMCommandAdapter<T> extends RecyclerView.Adapter<RViewHolder> {
    private SparseArray<View> headers = new SparseArray<>();

    private SparseArray<View> footers = new SparseArray<>();

    private int BASE_ITEM_TYPE_HEADER = 100000;

    private int BASE_ITEM_TYPE_FOOTER = 200000;

    protected List<T> mList;

    protected Context mContext;

    public ItemViewDelegateManager mItemViewDelegateManager;

    private OnItemClickListener mOnItemClickListener;

    public RMCommandAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    public void addHeaderView(View view) {
        // Check whether the View object is not in the mHeaders array.
        if (headers.indexOfValue(view) < 0) {
            headers.put(BASE_ITEM_TYPE_HEADER++, view);
            notifyDataSetChanged();
        }
    }

    public void addFooterView(View view) {
        // Check whether the View object is not in the mFooters array.
        if (footers.indexOfValue(view) < 0) {
            footers.put(BASE_ITEM_TYPE_FOOTER++, view);
            notifyDataSetChanged();
        }
    }

    public int getHeaderCount() {
        return headers.size();
    }

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (headers.indexOfKey(viewType) >= 0) {
            View view = headers.get(viewType);
            return RViewHolder.get(mContext, view);
        }

        if (footers.indexOfKey(viewType) >= 0) {
            View view = footers.get(viewType);
            return RViewHolder.get(mContext, view);
        }

        ItemViewDelegate delegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = delegate.getItemViewLayoutId();
        RViewHolder holder = RViewHolder.get(mContext, parent, layoutId);
        setListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(RViewHolder holder, int position) {
        if (isHeaderPosition(position) || isFooterPosition(position)) {
            return;
        }
        position = position - headers.size();
        convert(holder, mList.get(position), position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return headers.keyAt(position);
        }

        if (isFooterPosition(position)) {
            position = position - getOriginalItemCount() - headers.size();
            return footers.keyAt(position);
        }
        position = position - headers.size();
        if (!useItemViewDelegateManager()) {
            return super.getItemViewType(position);
        }
        return mItemViewDelegateManager.getItemViewType(mList.get(position), position);
    }

    private boolean isFooterPosition(int position) {
        return position >= getOriginalItemCount() + headers.size();
    }

    private boolean isHeaderPosition(int position) {
        return position < headers.size();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener(final RViewHolder viewHolder) {
        viewHolder.getCovertView().setOnTouchListener((v, event) -> {
            if (mOnItemClickListener != null) {
                int position = viewHolder.getAdapterPosition() - headers.size();
                mOnItemClickListener.onItemTouch(v, viewHolder, position, viewHolder.getAdapterPosition());
            }
            return false;
        });

        viewHolder.getCovertView().setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOnItemClickListener != null) {
                int position = viewHolder.getAdapterPosition() - headers.size();
                mOnItemClickListener.onItemClick(v, viewHolder, position, viewHolder.getAdapterPosition());
            }
        }));

        viewHolder.getCovertView().setOnLongClickListener(v -> {
            if (mOnItemClickListener != null) {
                int position = viewHolder.getAdapterPosition() - headers.size();
                return mOnItemClickListener.onItemLongClick(v, viewHolder, position, viewHolder.getAdapterPosition());
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mList.size() + headers.size() + footers.size();
    }

    public int getOriginalItemCount() {
        return getItemCount() - headers.size() - footers.size();
    }

    @SuppressWarnings("unchecked")
    private void convert(RViewHolder holder, T t, int dataPosition) {
        mItemViewDelegateManager.convert(holder, t, dataPosition, holder.getAdapterPosition());
    }

    @SuppressWarnings("unchecked")
    protected RMCommandAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    private boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {
        void onItemTouch(View view, RecyclerView.ViewHolder holder, int dataPosition, int position);

        void onItemClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
