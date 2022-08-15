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

import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.MediaFolderViewModel;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.PickPictureViewModel;
import com.huawei.hms.videoeditor.ui.template.adapter.ModulePickAdapter;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;
import com.huawei.hms.videoeditor.ui.template.utils.ModuleSelectManager;
import com.huawei.hms.videoeditor.ui.template.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.template.viewmodel.ModuleEditViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

public class ModulePictureFragment extends LazyFragment implements ModulePickAdapter.OnItemClickListener,
        ModuleSelectManager.OnSelectItemChangeListener, ModuleSelectManager.OnSelectItemDeleteListener {

    private RecyclerView recyclerView;

    private ModulePickAdapter adapter;

    private ModuleSelectManager mModuleManger = ModuleSelectManager.getInstance();

    private PickPictureViewModel mPickPictureViewModel;

    private MediaFolderViewModel mMediaFolderViewModel;

    private ModuleEditViewModel mEditViewModel;

    private int mReplacePosition = -1;

    private ToastWrapper mToastWrapper;

    private String mFolderPath = "";

    private int mItemWidth = 0;

    private int mSpanCount = 3;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentViewWidth = SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 48f));
        mItemWidth = currentViewWidth / mSpanCount;
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, mSpanCount));
        if (adapter != null) {
            adapter.setImageViewWidth(mItemWidth);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_pick_video;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.choice_recyclerview);
    }

    @Override
    protected void initObject() {
        mPickPictureViewModel = new ViewModelProvider(this, mFactory).get(PickPictureViewModel.class);
        mMediaFolderViewModel = new ViewModelProvider(mActivity, mFactory).get(MediaFolderViewModel.class);
        mEditViewModel = new ViewModelProvider(mActivity, mFactory).get(ModuleEditViewModel.class);
        recyclerView.setHasFixedSize(true);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(animator);
        adapter = new ModulePickAdapter(requireActivity());
        SafeBundle safeBundle = new SafeBundle(getArguments());
        mReplacePosition = safeBundle.getInt(Constant.EXTRA_SELECT_RESULT, -1);
        if (mReplacePosition != -1) {
            adapter.setMaterialDataList(mEditViewModel.getInitData());
        }
        mItemWidth = (SizeUtils.screenWidth(mContext) - (SizeUtils.dp2Px(mContext, 48f))) / mSpanCount;

        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, mSpanCount));
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(mActivity, 8f),
                    SizeUtils.dp2Px(mActivity, 8f), ContextCompat.getColor(mActivity, R.color.black)));
        }
        adapter.setImageViewWidth(mItemWidth);
        recyclerView.setAdapter(adapter);
        mToastWrapper = new ToastWrapper();
    }

    @Override
    protected void initData() {
        mPickPictureViewModel.getPageData().observe(this, pagedList -> {
            adapter.submitList(pagedList);
        });

        mMediaFolderViewModel.getFolderSelect().observe(this, mediaFolder -> {
            if (mFolderPath.equals(mediaFolder.getDirPath())) {
                return;
            }
            mFolderPath = mediaFolder.getDirPath();
            MutableLiveData<Boolean> mutableLiveData = mMediaFolderViewModel.getGalleryVideoSelect();
            Boolean mutableLiveDataValue = mutableLiveData.getValue();
            if (mutableLiveDataValue != null && mPickPictureViewModel.getDataSource() != null
                    && !mutableLiveDataValue) {
                mPickPictureViewModel.setDirPathName(mFolderPath);
                mPickPictureViewModel.getDataSource().invalidate();
            }
        });

        mMediaFolderViewModel.getRotationState().observe(this, state -> {
            if (state == mPickPictureViewModel.getRotationState()) {
                return;
            }
            mPickPictureViewModel.setRotationState(state);
            DataSource dataSource = mPickPictureViewModel.getDataSource();
            if (dataSource != null) {
                dataSource.invalidate();
            }
        });

    }

    @Override
    protected void initEvent() {
        adapter.setOnItemClickListener(this);
        mModuleManger.setPictureItemDeleteListener(this);
    }

    @Override
    public void onItemClick(int position) {
        PagedList<MediaData> pagedList = adapter.getCurrentList();
        if (pagedList == null) {
            return;
        }
        if (mReplacePosition != -1) {
            MediaData item = pagedList.get(position);
            MaterialData data = mEditViewModel.getInitData().get(mReplacePosition);
            if (item == null || data == null) {
                return;
            }
            if (!item.getPath().equals(data.getPath())) {
                data = new MaterialData();
            }
            data.setMimeType(item.getMimeType());
            data.setDuration(item.getDuration());
            data.setValidDuration(item.getValidDuration());
            data.setPath(item.getPath());
            data.setSize(item.getSize());
            data.setUri(item.getUri());
            data.setWidth(item.getWidth());
            data.setHeight(item.getHeight());
            data.setIndex(item.getIndex());
            mEditViewModel.setMaterialData(mReplacePosition, data);
            NavController mNavController =
                    Navigation.findNavController(mActivity, R.id.nav_host_fragment_module_detail);
            NavDestination navDestination = mNavController.getCurrentDestination();
            if (navDestination != null && navDestination.getId() == R.id.videoModuleReplaceFragment) {
                mNavController.popBackStack(R.id.videoModuleEditFragment, false);
            }
            return;
        }
        if (mModuleManger.canNextStep()) {
            mToastWrapper.makeTextWithShow(mActivity, getResources().getString(R.string.selected_upper_limit), 700);
            return;
        }
        if (adapter == null) {
            return;
        }
        PagedList<MediaData> pagedList1 = adapter.getCurrentList();
        if (pagedList1 == null) {
            return;
        }

        MediaData item = pagedList1.get(position);
        if (item == null) {
            return;
        }
        int index = item.getIndex();
        item.setIndex(index + 1);
        item.setPosition(position);
        adapter.notifyItemChanged(position);
        mModuleManger.addSelectItemAndSetIndex(item);
    }

    @Override
    public void onSelectItemDelete(MediaData item) {
        item.setIndex(0);
        adapter.notifyItemChanged(item.getPosition());
    }

    @Override
    public void onSelectItemChange(MaterialData item) {

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
