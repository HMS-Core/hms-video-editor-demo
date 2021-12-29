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

package com.huawei.hms.videoeditor.ui.mediapick.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.huawei.hms.videoeditor.ui.common.LazyFragment;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity;
import com.huawei.hms.videoeditor.ui.mediapick.adapter.MediaPickAdapter;
import com.huawei.hms.videoeditor.ui.mediapick.manager.MediaPickManager;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.MediaFolderViewModel;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.PickPictureViewModel;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PickPictureFragment extends LazyFragment implements MediaPickManager.OnSelectItemChangeListener {

    private RecyclerView mPictureRecyclerView;

    private MediaPickAdapter mMediaAdapter;

    private PickPictureViewModel mMediaPictureViewModel;

    private MediaFolderViewModel mMediaFolderViewModel;

    private String mFolderPath = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_pick_video;
    }

    @Override
    protected void initView(View view) {
        mPictureRecyclerView = view.findViewById(R.id.choice_recyclerview);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMediaAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initObject() {
        mMediaPictureViewModel = new ViewModelProvider(this, mFactory).get(PickPictureViewModel.class);
        mMediaFolderViewModel = new ViewModelProvider(mActivity, mFactory).get(MediaFolderViewModel.class);
        mPictureRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        mPictureRecyclerView.setItemAnimator(defaultItemAnimator);

        SafeBundle safeBundle = new SafeBundle(getArguments());
        List<MediaData> mInitMediaList = safeBundle.getParcelableArrayList(Constant.EXTRA_SELECT_RESULT);
        int actionType = safeBundle.getInt(MediaPickActivity.ACTION_TYPE);
        mMediaAdapter = new MediaPickAdapter(mActivity, actionType);
        if (mInitMediaList != null) {
            mMediaAdapter.setInitMediaList(mInitMediaList);
        }
        mPictureRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        if (mPictureRecyclerView.getItemDecorationCount() == 0) {
            mPictureRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(mActivity, 8),
                SizeUtils.dp2Px(mActivity, 8), ContextCompat.getColor(mActivity, R.color.black)));
        }
        mPictureRecyclerView.setAdapter(mMediaAdapter);
    }

    @Override
    protected void initData() {
        mMediaPictureViewModel.getPageData().observe(this, pagedList -> {
            mMediaAdapter.submitList(pagedList);
        });

        mMediaFolderViewModel.getFolderSelect().observe(this, mediaFolder -> {
            if (mFolderPath.equals(mediaFolder.getDirPath())) {
                return;
            }
            mFolderPath = mediaFolder.getDirPath();
            MutableLiveData<Boolean> mutableLiveData = mMediaFolderViewModel.getGalleryVideoSelect();
            Boolean mutableLiveDataValue = mutableLiveData.getValue();
            if (mutableLiveDataValue != null && mMediaPictureViewModel.getDataSource() != null
                && !mutableLiveDataValue) {
                mMediaPictureViewModel.setDirPathName(mFolderPath);
                mMediaPictureViewModel.getDataSource().invalidate();
            }
        });

        mMediaFolderViewModel.getRotationState().observe(this, state -> {
            if (state == mMediaPictureViewModel.getRotationState()) {
                return;
            }
            mMediaPictureViewModel.setRotationState(state);
            DataSource dataSource = mMediaPictureViewModel.getDataSource();
            if (dataSource != null) {
                dataSource.invalidate();
            }

        });
    }

    @Override
    protected void initEvent() {
        MediaPickManager.getInstance().addOnSelectItemChangeListener(this);
    }

    @Override
    public void onSelectItemChange(MediaData item) {
        PagedList<MediaData> mediaDataList = mMediaAdapter.getCurrentList();
        if (mediaDataList == null || item == null) {
            return;
        }
        int position = mediaDataList.indexOf(item);
        mMediaAdapter.notifyItemChanged(position);
    }
}
