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

package com.huawei.hms.videoeditor.ui.mediaeditor.canvas;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.GlideBlurTransformer;
import com.huawei.hms.videoeditor.ui.common.view.GlideRoundTransform;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.constraintlayout.utils.widget.ImageFilterView;

public class CanvasBlurAdapter extends RCommandAdapter<Float> {
    private Context context;

    private Bitmap bitmap;

    private int mSelectPosition = -1;

    OnBlurSelectedListener blurSelectedListener;

    public CanvasBlurAdapter(Context context, List<Float> list, int layoutId, Bitmap bitmap) {
        super(context, list, layoutId);
        this.bitmap = bitmap;
        this.context = context;
    }

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
    }

    public int getSelectPosition() {
        return this.mSelectPosition;
    }

    @Override
    protected void convert(RViewHolder holder, Float aFloat, int dataPosition, int position) {

        View mSelectView = holder.getView(R.id.item_add_image_select_view);
        ImageFilterView mImageView = holder.getView(R.id.item_image);
        mSelectView.setSelected(mSelectPosition == position);
        Glide.with(context)
            .load(bitmap)
            .apply(new RequestOptions().error(R.drawable.blur_menu)
                .transform(new MultiTransformation<>(new CenterCrop(),
                    // FIXME: 2021/2/5 aFloat*10 是因为glid的模糊效果只支持1-25，SDK支持的模糊效果有小数，临时方案，后续UI显示和SDK参数需要解耦；
                    new GlideBlurTransformer(context, (int) Math.min(aFloat * 10, 25)),
                    new GlideRoundTransform(SizeUtils.dp2Px(context, 4)))))
            .into(mImageView);

        mImageView.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectPosition != position) {
                    if (mSelectPosition != -1) {
                        int lastp = mSelectPosition;
                        mSelectPosition = position;
                        notifyItemChanged(lastp);
                        notifyItemChanged(mSelectPosition);
                    } else {
                        mSelectPosition = position;
                        notifyItemChanged(mSelectPosition);
                    }
                    if (blurSelectedListener != null) {
                        blurSelectedListener.onBlurSelected(aFloat);
                    }
                }
            }
        }));
    }

    public void setBlurSelectedListener(OnBlurSelectedListener blurSelectedListener) {
        this.blurSelectedListener = blurSelectedListener;
    }

    public interface OnBlurSelectedListener {
        void onBlurSelected(float blur);
    }
}
