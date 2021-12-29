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

import static com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity.DURATION;

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
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.PickVideoViewModel;
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

public class PickVideoFragment extends LazyFragment implements MediaPickManager.OnSelectItemChangeListener {

    private RecyclerView mRecyclerView;

    private MediaPickAdapter mMediaAdapter;

    private PickVideoViewModel mPickVideoViewModel;

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
        mRecyclerView = view.findViewById(R.id.choice_recyclerview);
    }

    @Override
    protected void initObject() {
        mPickVideoViewModel = new ViewModelProvider(this, mFactory).get(PickVideoViewModel.class);
        mMediaFolderViewModel = new ViewModelProvider(mActivity, mFactory).get(MediaFolderViewModel.class);
        mRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mRecyclerView.setItemAnimator(itemAnimator);
        SafeBundle safeBundle = new SafeBundle(getArguments());
        long mCheckDuration = safeBundle.getLong(DURATION, 0);
        int actionType = safeBundle.getInt(MediaPickActivity.ACTION_TYPE);
        mMediaAdapter = new MediaPickAdapter(mActivity, actionType);
        mMediaAdapter.setReplaceValidDuration(mCheckDuration);
        List<MediaData> mInitMediaList = safeBundle.getParcelableArrayList(Constant.EXTRA_SELECT_RESULT);
        if (mInitMediaList != null) {
            mMediaAdapter.setInitMediaList(mInitMediaList);
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(mActivity, 8f),
                SizeUtils.dp2Px(mActivity, 8f), ContextCompat.getColor(mActivity, R.color.transparent)));
        }
        mRecyclerView.setAdapter(mMediaAdapter);
    }

    @Override
    protected void initData() {

        mPickVideoViewModel.getaPageData().observe(this, pagedList -> {
            mMediaAdapter.submitList(pagedList);
        });

        mMediaFolderViewModel.getFolderSelect().observe(this, mediaFolder -> {
            if (mFolderPath.equals(mediaFolder.getDirPath())) {
                return;
            }
            mFolderPath = mediaFolder.getDirPath();
            MutableLiveData<Boolean> mutableLiveData = mMediaFolderViewModel.getGalleryVideoSelect();
            Boolean mutableLiveDataValue = mutableLiveData.getValue();
            if (mutableLiveDataValue != null && mutableLiveDataValue) {
                mPickVideoViewModel.setaDirName(mediaFolder.getDirPath());
                DataSource dataSource = mPickVideoViewModel.getDataSource();
                if (dataSource != null) {
                    dataSource.invalidate();
                }
            }
        });

        mMediaFolderViewModel.getRotationState().observe(this, state -> {
            if (state == mPickVideoViewModel.getRotationState()) {
                return;
            }
            mPickVideoViewModel.setRotationState(state);
            DataSource dataSource = mPickVideoViewModel.getDataSource();
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
        for (int i = 0; i < mediaDataList.size(); i++) {
            MediaData media = mediaDataList.get(i);
            if (media == null) {
                continue;
            }
            if (media.getName().equals(item.getName())) {
                mMediaAdapter.notifyItemChanged(i);
                break;
            }
        }
    }
}
