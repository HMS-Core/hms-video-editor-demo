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

package com.huawei.hms.videoeditor.ui.common.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.ColorCutoutFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.ColorCutViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class ColorCutAdapter extends SelectAdapter {
    ColorCutViewModel mColorCutViewModel;

    Context mContext;

    public ColorCutAdapter(Context context, List list, ColorCutViewModel colorCutViewModel) {
        super(context, list, R.layout.adapter_mask_effect, MaskViewHolder.class);
        this.mContext = context;
        this.mColorCutViewModel = colorCutViewModel;
    }

    @Override
    public int getPosition() {
        return super.getPosition();
    }

    private class MaskViewHolder extends ThisViewHolder {

        private TextView tv;

        private ImageView iv;

        public MaskViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void findView(View view) {
            iv = itemView.findViewById(R.id.iv);
            tv = itemView.findViewById(R.id.tv);
            mColorCutViewModel.getMove().observe((LifecycleOwner) mContext, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        if (getPosition() == 1) {
                            iv.setImageResource(R.drawable.ico_qiangdu_select);
                        }
                    } else {
                        if (getPosition() == 1) {
                            iv.setImageResource(R.drawable.ico_qiangdu);
                        }
                    }
                }
            });
        }

        @Override
        protected void onSelect(View view) {
            view.setSelected(true);
        }

        @Override
        protected void onUnSelect(View view) {
            view.setSelected(false);

        }

        @Override
        protected void bindView(Object k) {
            iv.setImageResource(((ColorCutoutFragment.Item) k).icoId);
            tv.setText(((ColorCutoutFragment.Item) k).name);
        }
    }
}
