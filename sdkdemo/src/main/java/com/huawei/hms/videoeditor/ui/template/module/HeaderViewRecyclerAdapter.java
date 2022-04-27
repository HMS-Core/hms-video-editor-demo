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

package com.huawei.hms.videoeditor.ui.template.module;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.hms.videoeditor.ui.common.utils.ArrayUtils;
import com.huawei.hms.videoeditor.ui.common.view.decoration.MStaggeredGridLayoutManager;
import com.huawei.hms.videoeditor.utils.SmartLog;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter {
    private static final String TAG = "HeaderViewRecyclerAdapter";

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static final int BASE_FOOTER_VIEW_TYPE = Integer.MIN_VALUE;

    private static final int BASE_HEADER_VIEW_TYPE = BASE_FOOTER_VIEW_TYPE + 0x00FFFFFF;

    private final List<Integer> mHeaderViewTypeList = new ArrayList<Integer>();

    private final List<Integer> mFooterViewTypeList = new ArrayList<Integer>();

    private final SparseArray<View> mHeaderViewInfoMap = new SparseArray<View>();

    private final SparseArray<View> mFooterViewInfoMap = new SparseArray<View>();

    private RecyclerView.Adapter mContentAdapter;

    private GridLayoutManager mContentLayoutManager = null;

    private boolean mIsStaggeredGrid;

    private RecyclerView.AdapterDataObserver mContentDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerViewsCountCount = getHeadersCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount,
                toPosition + headerViewsCountCount + itemCount);
        }
    };

    public HeaderViewRecyclerAdapter(RecyclerView.Adapter adapter) {
        this.mContentAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeaderViewType(viewType)) {
            return createHeaderFooterViewHolder(mHeaderViewInfoMap.get(viewType));
        }
        if (isFooterViewType(viewType)) {
            return createHeaderFooterViewHolder(mFooterViewInfoMap.get(viewType));
        }

        return mContentAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getHeadersCount() || position >= getHeadersCount() + mContentAdapter.getItemCount()) {
            return;
        }

        mContentAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return ArrayUtils.getListElement(mHeaderViewTypeList, position);
        } else if (isFooterPosition(position)) {
            return ArrayUtils.getListElement(mFooterViewTypeList,
                position - mContentAdapter.getItemCount() - getHeadersCount());
        } else {
            return mContentAdapter.getItemViewType(position - getHeadersCount());
        }
    }

    @Override
    public int getItemCount() {
        return getFootersCount() + getHeadersCount() + mContentAdapter.getItemCount();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);

        mContentAdapter.registerAdapterDataObserver(mContentDataObserver);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);

        mContentAdapter.unregisterAdapterDataObserver(mContentDataObserver);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        adjustSpanSize(recyclerView);

        mContentAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mContentAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    public int getHeadersCount() {
        return mHeaderViewTypeList.size();
    }

    public int getFootersCount() {
        return mFooterViewTypeList.size();
    }

    public boolean isEmpty() {
        return mContentAdapter == null || mContentAdapter.getItemCount() == 0;
    }

    public void addHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null");
        }

        if (mFooterViewInfoMap.indexOfValue(view) >= 0) {
            return;
        }

        int type = getTypeAndId(view);

        mHeaderViewInfoMap.append(type, view);
        mHeaderViewTypeList.add(type);

        notifyItemInserted(getHeadersCount() - 1);
    }

    private int getTypeAndId(View view) {
        int id;

        if ((id = view.getId()) < 0) {
            id = View.generateViewId();
            view.setId(id);
        }

        return id + BASE_HEADER_VIEW_TYPE;
    }

    public void addHeaderView(View view, int index) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null");
        }
        if (index < 0 || index > mHeaderViewTypeList.size()) {
            SmartLog.w(TAG, "addHeaderView and index error!");
            return;
        }

        if (mFooterViewInfoMap.indexOfValue(view) >= 0) {
            return;
        }

        int type = getTypeAndId(view);

        mHeaderViewInfoMap.append(type, view);
        mHeaderViewTypeList.add(index, type);

        notifyItemInserted(index);
    }

    public void addFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null");
        }

        if (mFooterViewInfoMap.indexOfValue(view) >= 0) {
            return;
        }

        int id;

        if ((id = view.getId()) < 0) {
            id = View.generateViewId();
            view.setId(id);
        }

        int type = id + BASE_FOOTER_VIEW_TYPE;

        mFooterViewInfoMap.append(type, view);
        mFooterViewTypeList.add(type);

        notifyItemInserted(getItemCount() - 1);
    }

    public void setFooterVisibility(boolean shouldShow) {
        View view;
        for (Integer type : mFooterViewTypeList) {
            view = mFooterViewInfoMap.get(type);
            view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return mContentAdapter;
    }

    private boolean isHeaderViewType(int viewType) {
        return mHeaderViewInfoMap.get(viewType) != null;
    }

    private boolean isFooterViewType(int viewType) {
        return mFooterViewInfoMap.get(viewType) != null;
    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaderViewTypeList.size();
    }

    private boolean isFooterPosition(int position) {
        return position >= mHeaderViewTypeList.size() + mContentAdapter.getItemCount();
    }

    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        if (mIsStaggeredGrid) {
            MStaggeredGridLayoutManager.LayoutParams layoutParams =
                new MStaggeredGridLayoutManager.LayoutParams(MStaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                    MStaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
        } else {
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }

        return new ViewHolder(view);
    }

    public void adjustSpanSize(RecyclerView recycler) {
        if (recycler.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();
            if (layoutManager == null) {
                return;
            }

            if (null != mContentLayoutManager) {
                layoutManager.setSpanCount(mContentLayoutManager.getSpanCount());
            }

            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter = isHeaderPosition(position) || isFooterPosition(position);

                    if (isHeaderOrFooter) {
                        return layoutManager.getSpanCount();
                    }

                    if (null == mContentLayoutManager) {
                        return 1;
                    }

                    return mContentLayoutManager.getSpanSizeLookup().getSpanSize(position - getHeadersCount());
                }
            });
        }

        if (recycler.getLayoutManager() instanceof MStaggeredGridLayoutManager) {
            this.mIsStaggeredGrid = true;
        }
    }
}
