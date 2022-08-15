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

package com.huawei.hms.videoeditor.fragment;

import static com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity.CLIPS_VIEW_TYPE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity.VIEW_HISTORY;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.HomeRecordAdapter;
import com.huawei.hms.videoeditor.sdk.HVEProject;
import com.huawei.hms.videoeditor.sdk.HVEProjectManager;
import com.huawei.hms.videoeditor.sdk.bean.HVEWordStyle;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SharedPreferencesUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.decoration.RecyclerViewDivider;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextEditViewModel;
import com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity;
import com.huawei.hms.videoeditor.view.ClipDeleteDialog;
import com.huawei.hms.videoeditor.view.ClipRenameDialog;
import com.huawei.hms.videoeditor.view.HomeClipPopWindow;
import com.huawei.hms.videoeditor.viewmodel.MainViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import java.util.ArrayList;
import java.util.List;

public class ClipFragment extends BaseFragment {
    private TextView homeSelectNum;

    private EditorTextView mDraftClip;

    private LinearLayout mAddCardView;

    private RecyclerView mRecyclerView;

    private ConstraintLayout homeSelectLayout;

    private TextView homeSelectDelete;

    private TextView homeSelectAll;

    private TextView homeDraftNoText;

    private MainViewModel mainViewModel;

    private List<HVEProject> mDraftList;

    private HomeRecordAdapter mHomeRecordAdapter;

    private HomeClipPopWindow mActionPopWindow;

    private TextEditViewModel mTextEditViewModel;

    private ImageView back;

    private boolean isFromHome = false;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main_clip;
    }

    @Override
    protected void initView(View view) {
        homeSelectNum = view.findViewById(R.id.home_select_num);
        mDraftClip = view.findViewById(R.id.home_draft_clip);
        mRecyclerView = view.findViewById(R.id.content_list);
        mAddCardView = view.findViewById(R.id.card_upload);
        homeSelectLayout = view.findViewById(R.id.home_select_layout);
        homeSelectDelete = view.findViewById(R.id.home_select_delete);
        homeSelectAll = view.findViewById(R.id.home_select_all);
        homeDraftNoText = view.findViewById(R.id.home_draft_no_text);
        homeSelectNum.setText(getResources().getQuantityString(R.plurals.home_select_num3, 0, 0));
        back = view.findViewById(R.id.iv_back);
        Activity activity = getActivity();
        if (activity != null) {
            isFromHome = activity.getIntent().getBooleanExtra("fromHome", false);
            back.setVisibility(isFromHome ? View.VISIBLE : View.GONE);
        }
        mTextEditViewModel = new ViewModelProvider(mActivity, mFactory).get(TextEditViewModel.class);
    }

    @Override
    protected void initObject() {
        mainViewModel = new ViewModelProvider(this, mFactory).get(MainViewModel.class);
        mDraftList = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mRecyclerView.setItemAnimator(itemAnimator);

        mHomeRecordAdapter = new HomeRecordAdapter(context, mDraftList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(context, LinearLayoutManager.VERTICAL,
            SizeUtils.dp2Px(context, 8), ContextCompat.getColor(context, R.color.edit_background)));
        mRecyclerView.setAdapter(mHomeRecordAdapter);

        boolean refresh = viewModel.getRefresh();
        if (refresh) {
            refresh();
        }
    }

    @Override
    protected void initData() {
        mainViewModel.getDraftProjects().observe(getViewLifecycleOwner(), draftProjects -> {
            if (draftProjects.size() > 0) {
                mDraftList.clear();
                for (HVEProject project : draftProjects) {
                    mDraftList.add(project);
                }
                mHomeRecordAdapter.notifyDataSetChanged();
                homeDraftNoText.setVisibility(View.GONE);
                mDraftClip.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                homeDraftNoText.setVisibility(View.VISIBLE);
                mDraftClip.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
            hideEditStatus();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initEvent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.onBackPressed();
                }
            }
        });
        mDraftClip.setOnClickListener(new OnClickRepeatedListener(v -> {
            mDraftClip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mDraftClip.setTextColor(context.getColor(R.color.white));
            initData();
        }));

        homeSelectNum.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mHomeRecordAdapter.getIsEditStatus()) {
                hideEditStatus();
                homeSelectAll.setSelected(false);
                homeSelectDelete.setSelected(false);
            } else {
                showEditStatus();
            }

        }));

        homeSelectDelete.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (homeSelectDelete.isSelected()) {
                if (mHomeRecordAdapter.getSelectList().isEmpty()) {
                    Toast.makeText(context, getText(R.string.home_select_num4), Toast.LENGTH_LONG).show();
                } else {
                    showDeleteDialog(mHomeRecordAdapter.getSelectList());
                }
            }
        }));

        homeSelectAll.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (homeSelectAll.isSelected()) {
                homeSelectAll.setText(R.string.home_select_all);
                homeSelectAll.setSelected(false);
                homeSelectDelete.setSelected(false);
                mHomeRecordAdapter.setSelectList(new ArrayList<>());
                homeSelectNum.setText(getResources().getQuantityString(R.plurals.home_select_num3,
                    new ArrayList<>().size(), new ArrayList<>().size()));
            } else {
                homeSelectAll.setText(R.string.home_select_all_deselect);
                homeSelectAll.setSelected(true);
                homeSelectDelete.setSelected(true);
                mHomeRecordAdapter.setSelectList(mDraftList);
                homeSelectNum.setText(
                    getResources().getQuantityString(R.plurals.home_select_num3, mDraftList.size(), mDraftList.size()));

            }
            mHomeRecordAdapter.notifyDataSetChanged();
        }));

        mAddCardView.setOnClickListener(new OnClickRepeatedListener(v -> {
            mTextEditViewModel.setLastWordStyle(new HVEWordStyle());
            mTextEditViewModel.refreshAsset();
            hideEditStatus();
            startActivity(new Intent(this.mActivity, MediaPickActivity.class));
            this.mActivity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_silent);
        }));

        mHomeRecordAdapter.setOnItemClickListener(new HomeRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HVEProject hveProject = mDraftList.get(position);
                if (context == null || hveProject == null) {
                    return;
                }

                SafeIntent intent = new SafeIntent(new Intent());
                intent.setClass(mActivity, VideoClipsActivity.class);
                intent.putExtra(CLIPS_VIEW_TYPE, VIEW_HISTORY);
                intent.putExtra(VideoClipsActivity.EXTRA_FROM_SELF_MODE, true);
                intent.putExtra("projectId", hveProject.getProjectId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick() {
                showEditStatus();
            }

            @Override
            public void onSelectClick(List<HVEProject> selectList, int position) {
                homeSelectAll.setSelected(selectList.size() == mDraftList.size());
                if (homeSelectAll.isSelected()) {
                    homeSelectAll.setText(R.string.home_select_all_deselect);
                } else {
                    homeSelectAll.setText(R.string.home_select_all);
                }
                homeSelectDelete.setSelected(selectList.size() != 0);
                mHomeRecordAdapter.notifyItemChanged(position);
                homeSelectNum.setText(
                    getResources().getQuantityString(R.plurals.home_select_num3, selectList.size(), selectList.size()));
            }

            @Override
            public void onActionClick(View view, HVEProject item, int pos) {
                showActionPopWindow(view, item, pos);
            }

        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void showActionPopWindow(View view, HVEProject item, int pos) {
        int width;
        int height;
        if (mActionPopWindow == null) {
            mActionPopWindow = new HomeClipPopWindow(mActivity);
            width = mActionPopWindow.getPopWidth() + 40;
            height = mActionPopWindow.getPopHeight() + 10;
        } else {
            width = mActionPopWindow.getContentView().getWidth();
            height = mActionPopWindow.getContentView().getHeight();
        }
        mActionPopWindow.setPosition(item);
        mActionPopWindow.setOnActionClickListener(new HomeClipPopWindow.ActionOnClickListener() {
            @Override
            public void onRenameClick(HVEProject item) {
                showRenameDialog(item);
            }

            @Override
            public void onCopyClick() {
                if (context == null) {
                    return;
                }
                if (mDraftList == null || mDraftList.isEmpty() || pos >= mDraftList.size() || pos < 0) {
                    return;
                }
                int copyTimes =
                    SharedPreferencesUtils.getInstance().getCopyDraftTimes(context, mDraftList.get(pos).getProjectId());
                StringBuilder copyName = new StringBuilder(mDraftList.get(pos).getName());
                copyTimes++;
                copyName.append(context.getString(R.string.home_select_delete_copy_name)).append(copyTimes);
                boolean isCopySuccess =
                    HVEProjectManager.copyDraft(mDraftList.get(pos).getProjectId(), copyName.toString());
                if (isCopySuccess) {
                    SharedPreferencesUtils.getInstance()
                        .putCopyDraftTimes(context, mDraftList.get(pos).getProjectId(), copyTimes);
                    refresh();
                    Toast
                        .makeText(context, context.getString(R.string.home_select_delete_copy_success),
                            Toast.LENGTH_SHORT)
                        .show();
                } else {
                    Toast
                        .makeText(context, context.getString(R.string.home_select_delete_copy_fail), Toast.LENGTH_SHORT)
                        .show();
                }
            }

            @Override
            public void onDeleteClick() {
                List<HVEProject> temps = new ArrayList<>();
                if (mDraftList == null || mDraftList.isEmpty() || pos >= mDraftList.size() || pos < 0) {
                    return;
                }
                temps.add(mDraftList.get(pos));
                showDeleteDialog(temps);
            }
        });

        int off = 10;
        if (pos == 0 || pos == 1) {
            mActionPopWindow.showAsDropDown(view, -width + view.getWidth(), -height / 3);
        } else {
            mActionPopWindow.showAsDropDown(view, -width + view.getWidth(), -height - view.getHeight() - off);
        }

        mActionPopWindow.setOnDismissListener(() -> {
            backgroundAlpha(1.0f);
        });
    }

    private void showDeleteDialog(List<HVEProject> mSelectProjectId) {
        if (context == null || mSelectProjectId == null || mDraftList == null || mDraftList.isEmpty()) {
            return;
        }
        ClipDeleteDialog dialog = new ClipDeleteDialog(context);
        dialog.show();
        dialog.setOnPositiveClickListener(() -> {
            for (HVEProject projectId : mSelectProjectId) {
                HVEProjectManager.deleteProject(projectId.getProjectId());
                int indexOfDraftList = getIndexOfDraftList(projectId.getProjectId());
                if (indexOfDraftList != -1) {
                    mDraftList.remove(projectId);
                }
            }
            hideEditStatus();
            refresh();
        });
    }

    private void showRenameDialog(HVEProject item) {
        if (mActivity == null) {
            return;
        }

        ClipRenameDialog dialog = new ClipRenameDialog(mActivity, item);
        dialog.show();
        dialog.setOnPositiveClickListener((updateName) -> {
            HVEProjectManager.updateProjectName(item.getProjectId(), updateName);
            hideEditStatus();
            refresh();
            initData();
        });
    }

    private int getIndexOfDraftList(String projectId) {
        int index = -1;
        for (int i = 0; i < mDraftList.size(); i++) {
            if (mDraftList.get(i).getProjectId().equals(projectId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void refresh() {
        mainViewModel.initDraftProjects();
        mHomeRecordAdapter.notifyDataSetChanged();
    }

    private void hideEditStatus() {
        homeSelectAll.setText(R.string.home_select_all);
        homeSelectAll.setSelected(false);
        homeSelectDelete.setSelected(false);
        mHomeRecordAdapter.setSelectList(new ArrayList<>());
        homeSelectNum.setText(getResources().getQuantityString(R.plurals.home_select_num3, new ArrayList<>().size(),
            new ArrayList<>().size()));
        homeSelectNum.setVisibility(View.INVISIBLE);
        homeSelectLayout.setVisibility(View.INVISIBLE);
        back.setVisibility(isFromHome ? View.VISIBLE : View.GONE);
        mHomeRecordAdapter.setIsEditStatus(false);
    }

    private void showEditStatus() {
        homeSelectAll.setText(R.string.home_select_all);
        homeSelectAll.setSelected(false);
        homeSelectDelete.setSelected(false);
        homeSelectNum.setVisibility(View.VISIBLE);
        homeSelectLayout.setVisibility(View.VISIBLE);
        back.setVisibility(View.GONE);
        mHomeRecordAdapter.setIsEditStatus(true);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mActivity.getWindow().setAttributes(lp);
    }
}