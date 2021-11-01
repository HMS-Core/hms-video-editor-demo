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

package com.huawei.hms.videoeditor.ui.common.adapter.comment;

import androidx.collection.SparseArrayCompat;

public class ItemViewDelegateManager<T> {
    @SuppressWarnings("unchecked")
    private SparseArrayCompat<ItemViewDelegate<T>> delegateSparseArrayCompat = new SparseArrayCompat();

    public int getItemViewDelegateCount() {
        return delegateSparseArrayCompat.size();
    }

    public ItemViewDelegateManager<T> addDelegate(ItemViewDelegate<T> delegate) {
        int viewType = delegateSparseArrayCompat.size();
        if (delegate != null) {
            delegateSparseArrayCompat.put(viewType, delegate);
        }
        return this;
    }

    public ItemViewDelegateManager<T> addDelegate(int viewType, ItemViewDelegate<T> delegate) {
        if (delegateSparseArrayCompat.get(viewType) != null) {
            throw new IllegalArgumentException("An ItemViewDelegate is already registered for the viewType = "
                + viewType + ". Already registered ItemViewDelegate is " + delegateSparseArrayCompat.get(viewType));
        }
        delegateSparseArrayCompat.put(viewType, delegate);
        return this;
    }

    public int getItemViewType(T item, int position) {
        int delegatesCount = delegateSparseArrayCompat.size();
        for (int i = delegatesCount - 1; i >= 0; i--) {
            ItemViewDelegate<T> delegate = delegateSparseArrayCompat.valueAt(i);
            if (delegate.isForViewType(item, position)) {
                return delegateSparseArrayCompat.keyAt(i);
            }
        }
        throw new IllegalArgumentException(
            "No ItemViewDelegate added that matches position=" + position + " in data source");
    }

    public void convert(RViewHolder holder, T item, int dataPosition, int position) {
        int delegatesCount = delegateSparseArrayCompat.size();
        for (int i = 0; i < delegatesCount; i++) {
            ItemViewDelegate<T> delegate = delegateSparseArrayCompat.valueAt(i);

            if (delegate.isForViewType(item, position)) {
                delegate.convert(holder, item, dataPosition, position);
                return;
            }
        }
        throw new IllegalArgumentException(
            "No ItemViewDelegateManager added that matches position=" + position + " in data source");
    }

    public ItemViewDelegate getItemViewDelegate(int viewType) {
        return delegateSparseArrayCompat.get(viewType);
    }
}
