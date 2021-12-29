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

import android.content.Context;

public abstract class RCommandAdapter<T> extends RMCommandAdapter<T> {

    public RCommandAdapter(Context context, List<T> list, final int layoutId) {
        super(context, list);

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(RViewHolder holder, T t, int dataPosition, int position) {
                RCommandAdapter.this.convert(holder, t, dataPosition, position);
            }

        });
    }

    protected abstract void convert(RViewHolder holder, T t, int dataPosition, int position);
}
