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

import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;

import androidx.recyclerview.widget.RecyclerView;

public class RMCommandAdapter<T> extends RecyclerView.Adapter<RViewHolder> {
    private SparseArray<View> mHeaders = new SparseArray<>();
    private SparseArray<View> mFooters = new SparseArray<>();

    private int BASE_ITEM_TYPE_HEADER = 100000;

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
        if (mHeaders.indexOfValue(view) < 0) {
            mHeaders.put(BASE_ITEM_TYPE_HEADER++, view);
            notifyDataSetChanged();
        }
    }

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mHeaders.indexOfKey(viewType) >= 0) {
            View view = mHeaders.get(viewType);
            return RViewHolder.get(mContext, view);
        }

        if (mFooters.indexOfKey(viewType) >= 0) {
            View view = mFooters.get(viewType);
            return RViewHolder.get(mContext, view);
        }

        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        RViewHolder holder = RViewHolder.get(mContext, parent, layoutId);
        setListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(RViewHolder holder, int position) {
        if (isHeaderPosition(position) || isFooterPosition(position)) {
            return;
        }
        position = position - mHeaders.size();
        convert(holder, mList.get(position), position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaders.keyAt(position);
        }

        if (isFooterPosition(position)) {
            position = position - getOriginalItemCount() - mHeaders.size();
            return mFooters.keyAt(position);
        }
        position = position - mHeaders.size();
        if (!useItemViewDelegateManager()) {
            return super.getItemViewType(position);
        }
        return mItemViewDelegateManager.getItemViewType(mList.get(position), position);
    }

    private boolean isFooterPosition(int position) {
        return position >= getOriginalItemCount() + mHeaders.size();
    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaders.size();
    }

    private void setListener(final RViewHolder viewHolder) {
        viewHolder.getCovertView().setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition() - mHeaders.size();
                    mOnItemClickListener.onItemClick(v, viewHolder, position, viewHolder.getAdapterPosition());
                }
            }
        }));

        viewHolder.getCovertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition() - mHeaders.size();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position, viewHolder.getAdapterPosition());
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size() + mHeaders.size() + mFooters.size();
    }

    public int getOriginalItemCount() {
        return getItemCount() - mHeaders.size() - mFooters.size();
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

    @SuppressWarnings("unchecked")
    protected RMCommandAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    private boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
