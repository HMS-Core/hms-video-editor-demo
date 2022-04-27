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

import android.content.res.Configuration;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.utils.ArrayUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.MediaFolderViewModel;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.PickVideoViewModel;
import com.huawei.hms.videoeditor.ui.template.adapter.ModulePickAdapter;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;
import com.huawei.hms.videoeditor.ui.template.utils.ModuleSelectManager;
import com.huawei.hms.videoeditor.ui.template.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.template.viewmodel.ModuleEditViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModuleVideoFragment extends LazyFragment implements ModulePickAdapter.OnItemClickListener,
    ModuleSelectManager.OnSelectItemChangeListener, ModuleSelectManager.OnSelectItemDeleteListener {
    private static final String TAG = "ModuleVideoFragment";

    private RecyclerView mVideoRecyclerView;

    private ModulePickAdapter mMediaAdapter;

    private PickVideoViewModel mPickVideoViewModel;

    private MediaFolderViewModel mMediaFolderViewModel;

    private ModuleEditViewModel mEditViewViewModel;

    private int mReplacePosition = -1;

    private ToastWrapper mToastWrapper;

    private String mFolderPath = "";

    private int mItemWidth = 0;

    private int mSpanCount = 3;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_pick_video;
    }

    @Override
    protected void initView(View view) {
        mVideoRecyclerView = view.findViewById(R.id.choice_recyclerview);
    }

    @Override
    protected void initObject() {
        mPickVideoViewModel = new ViewModelProvider(this, mFactory).get(PickVideoViewModel.class);
        mMediaFolderViewModel = new ViewModelProvider(mActivity, mFactory).get(MediaFolderViewModel.class);
        mEditViewViewModel = new ViewModelProvider(mActivity, mFactory).get(ModuleEditViewModel.class);
        mVideoRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mVideoRecyclerView.setItemAnimator(itemAnimator);
        mMediaAdapter = new ModulePickAdapter(mActivity);
        SafeBundle safeBundle = new SafeBundle(getArguments());
        mReplacePosition = safeBundle.getInt(Constant.EXTRA_SELECT_RESULT, -1);
        if (mReplacePosition != -1 && mReplacePosition >= 0
            && mReplacePosition < mEditViewViewModel.getInitData().size()) {
            long validDuration = mEditViewViewModel.getInitData().get(mReplacePosition).getValidDuration();
            mMediaAdapter.setReplaceValidDuration(validDuration);
            mMediaAdapter.setMaterialDataList(mEditViewViewModel.getInitData());
        }
        mItemWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 48f))) / mSpanCount;

        mVideoRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, mSpanCount));
        if (mVideoRecyclerView.getItemDecorationCount() == 0) {
            mVideoRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(mActivity, 8f),
                SizeUtils.dp2Px(mActivity, 8f), ContextCompat.getColor(mActivity, R.color.transparent)));
        }
        mMediaAdapter.setImageViewWidth(mItemWidth);
        mVideoRecyclerView.setAdapter(mMediaAdapter);
        mToastWrapper = new ToastWrapper();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentViewWidth = SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 48f));
        mItemWidth = currentViewWidth / mSpanCount;
        mVideoRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, mSpanCount));
        mMediaAdapter.setImageViewWidth(mItemWidth);
        mMediaAdapter.notifyDataSetChanged();
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
        mMediaAdapter.setOnItemClickListener(this);
        ModuleSelectManager.getInstance().setVideoItemDeleteListener(this);
        ModuleSelectManager.getInstance().addOnSelectItemChangeListener(this);
    }

    @Override
    public void onItemClick(int position) {
        PagedList<MediaData> pagedList = mMediaAdapter.getCurrentList();
        if (ArrayUtils.isEmpty(pagedList)) {
            return;
        }
        int size = pagedList.size();
        if (position >= size) {
            return;
        }
        MediaData item = pagedList.get(position);
        if (item == null) {
            return;
        }
        if (mReplacePosition != -1 && item.getDuration() < mMediaAdapter.getReplaceValidDuration()) {
            mToastWrapper.makeTextWithShow(mActivity, getResources().getString(R.string.video_duration_too_short_text),
                700);
            return;
        }
        if (mReplacePosition != -1) {
            MaterialData data = mEditViewViewModel.getInitData().get(mReplacePosition);
            if (!item.getPath().equals(data.getPath())) {
                data = new MaterialData();
            }
            data.setMimeType(item.getMimeType());
            data.setDuration(item.getDuration());
            data.setValidDuration(mMediaAdapter.getReplaceValidDuration());
            data.setPath(item.getPath());
            data.setSize(item.getSize());
            data.setUri(item.getUri());
            data.setWidth(item.getWidth());
            data.setHeight(item.getHeight());
            data.setIndex(item.getIndex());

            mEditViewViewModel.setMaterialData(mReplacePosition, data);
            NavController mNavController =
                Navigation.findNavController(mActivity, R.id.nav_host_fragment_module_detail);
            NavDestination navDestination = mNavController.getCurrentDestination();
            if (navDestination != null && navDestination.getId() == R.id.videoModuleReplaceFragment) {
                mNavController.popBackStack(R.id.videoModuleEditFragment, false);
            }
            return;
        }
        if (ModuleSelectManager.getInstance().canNextStep()) {
            mToastWrapper.makeTextWithShow(mActivity, getResources().getString(R.string.selected_upper_limit), 700);
            return;
        }
        if (item.getDuration() < ModuleSelectManager.getInstance().getCurrentDuration()) {
            mToastWrapper.makeTextWithShow(mActivity, getResources().getString(R.string.video_duration_too_short_text),
                700);
            return;
        }
        int index = item.getIndex();
        item.setIndex(index + 1);
        item.setPosition(position);
        mMediaAdapter.notifyItemChanged(position);
        ModuleSelectManager.getInstance().addSelectItemAndSetIndex(item);
    }

    @Override
    public void onSelectItemDelete(MediaData item) {
        item.setIndex(0);
        mMediaAdapter.notifyItemChanged(item.getPosition());
    }

    @Override
    public void onSelectItemChange(MaterialData item) {
        PagedList<MediaData> pagedList = mMediaAdapter.getCurrentList();
        if (pagedList == null || item == null) {
            return;
        }
        SmartLog.i(TAG, "onSelectItemChange" + item.getIndex());
        int position = pagedList.indexOf(item);
        SmartLog.i(TAG, "onSelectItemPosition" + position);
        mMediaAdapter.notifyItemChanged(position);
    }

    @Override
    public void onStop() {
        if (mToastWrapper != null) {
            mToastWrapper.cancelToast();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mToastWrapper != null) {
            mToastWrapper.cancelToast();
        }
        super.onDestroy();
    }
}
