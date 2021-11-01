
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

package com.huawei.hms.videoeditor.ui.mediaeditor.audio.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.huawei.hms.videoeditor.ui.common.LazyFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class LocalLinkMusicFragment extends LazyFragment {

    private EditText mLinkEdit;

    private ImageView mDeleteIv;

    private CardView mDownloadCv;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_local_link_music;
    }

    @Override
    protected void initView(View view) {
        mLinkEdit = view.findViewById(R.id.ed_search);
        mDeleteIv = view.findViewById(R.id.delete_iv);
        mDownloadCv = view.findViewById(R.id.download_cv);
    }

    @Override
    protected void initObject() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        if (mDeleteIv != null) {
            mDeleteIv.setOnClickListener(new OnClickRepeatedListener(v -> {
                if (mLinkEdit == null) {
                    return;
                }
                mLinkEdit.setText("");
            }));
        }

        if (mLinkEdit == null) {
            return;
        }
        mLinkEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDeleteIv.setVisibility(StringUtil.isEmpty(s.toString()) ? View.GONE : View.VISIBLE);
                resetCardView(!StringUtil.isEmpty(s.toString()));
            }
        });
    }

    private void resetCardView(boolean isSelect) {
        if (mDownloadCv == null) {
            return;
        }
        mDownloadCv.setCardBackgroundColor(isSelect ? ContextCompat.getColor(mContext, R.color.white)
            : ContextCompat.getColor(mContext, R.color.color_fff_10));
    }
}
