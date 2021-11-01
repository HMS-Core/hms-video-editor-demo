
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

package com.huawei.hms.videoeditor.ui.mediapick.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.mediapick.adapter.PicturePickAdapter;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.PickPictureViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PicturePickActivity extends BaseActivity {

    private ImageView mCloseIcon;

    private RecyclerView mRecyclerView;

    private PicturePickAdapter mMediaAdapter;

    private PickPictureViewModel mMediaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_pick);

        initView();
        initObject();
        initData();
        initEvent();
    }

    private void initView() {
        mCloseIcon = findViewById(R.id.iv_close);
        mRecyclerView = findViewById(R.id.choice_recyclerview);
    }

    private void initObject() {
        mMediaViewModel = new ViewModelProvider(this, factory).get(PickPictureViewModel.class);
        mRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mRecyclerView.setItemAnimator(itemAnimator);

        mMediaAdapter = new PicturePickAdapter(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(this, 14.5f),
                SizeUtils.dp2Px(this, 14.5f), ContextCompat.getColor(this, R.color.black)));
        }
        mRecyclerView.setAdapter(mMediaAdapter);
    }

    private void initData() {
        mMediaViewModel.getPageData().observe(this, pagedList -> {
            if (pagedList.size() > 0) {
                mMediaAdapter.submitList(pagedList);
            }
        });
    }

    private void initEvent() {
        mCloseIcon.setOnClickListener(new OnClickRepeatedListener(v -> finish()));

        mMediaAdapter.setOnItemClickListener(position -> {
            PagedList<MediaData> mediaDataList = mMediaAdapter.getCurrentList();
            if (mediaDataList != null && mediaDataList.size() > position) {
                MediaData mediaData = mediaDataList.get(position);
                Intent intent = new Intent();
                if (mediaData != null && mediaData.getPath() != null) {
                    intent.putExtra(Constant.EXTRA_SELECT_RESULT, mediaData.getPath());
                    setResult(Constant.RESULT_CODE, intent);
                    finish();
                }
            }
        });
    }
}
