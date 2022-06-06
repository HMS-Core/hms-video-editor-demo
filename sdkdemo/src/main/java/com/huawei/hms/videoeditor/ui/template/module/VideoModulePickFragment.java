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

import static android.app.Activity.RESULT_OK;
import static com.huawei.hms.videoeditor.ui.template.module.activity.TemplateEditActivity.REQUEST_EDIT_CODE;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.template.HVETemplateElement;
import com.huawei.hms.videoeditor.template.HVETemplateInfo;
import com.huawei.hms.videoeditor.template.HVETemplateManager;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RMCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtil;
import com.huawei.hms.videoeditor.ui.common.utils.GsonUtils;
import com.huawei.hms.videoeditor.ui.common.utils.LanguageUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SPManager;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.utils.Utils;
import com.huawei.hms.videoeditor.ui.common.view.RotationPopupWiew;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonBottomDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.FilterLinearLayoutManager;
import com.huawei.hms.videoeditor.ui.mediapick.adapter.MediaFolderAdapter;
import com.huawei.hms.videoeditor.ui.mediapick.bean.MediaFolder;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.MediaFolderViewModel;
import com.huawei.hms.videoeditor.ui.template.adapter.ModulePickSelectAdapter;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;
import com.huawei.hms.videoeditor.ui.template.bean.TemplateProjectBean;
import com.huawei.hms.videoeditor.ui.template.module.activity.TemplateDetailActivity;
import com.huawei.hms.videoeditor.ui.template.module.activity.VideoModuleDetailActivity;
import com.huawei.hms.videoeditor.ui.template.utils.CropDataHelper;
import com.huawei.hms.videoeditor.ui.template.utils.ModuleSelectManager;
import com.huawei.hms.videoeditor.ui.template.view.dialog.ModuleComposeDialog;
import com.huawei.hms.videoeditor.ui.template.viewmodel.ModuleEditViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.secure.android.common.intent.SafeIntent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoModulePickFragment extends BaseFragment {
    private static final String TAG = "VideoModulePickFragment";

    private static final int REQUEST_PHOTO_CODE = 1001;

    private static final int REQUEST_VIDEO_CODE = 1002;

    private static final int REQUEST_MODULE_SHOT = 1003;

    private ImageView mCloseIcon;

    private TextView mVideoTv;

    private TextView mPictureTv;

    private RecyclerView mChoiceRecyclerview;

    private TextView mTvTotalMaterial;

    private CardView mAddCardView;

    private int contentId;

    private int currIndex = 0;

    private List<Fragment> fragments;

    private NavController mNavController;

    private List<MaterialData> mMaterialDatas;

    private ModulePickSelectAdapter mModulePickSelectAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private float mTextureViewWidth;

    private float mTextureViewHeight;

    private String mTemplateId;

    private String mName;

    private String mDescription;

    private ModuleComposeDialog moduleComposeDialog;

    private Drawable mUseDrawable;

    private Drawable mUnUseDrawable;

    private View mVideoIndicator;

    private View mPictureIndicator;

    private ModuleSelectManager moduleSelectManager;

    private MutableLiveData<Integer> replacePositionLiveData;

    private static final String FILE_NAME = "MediaVideoMap";

    public static final SPManager TEMPLATE_VIDEO_SP = SPManager.get(FILE_NAME);

    private static final int REFRESH = 0x10001;

    private final List<MediaData> mediaDatas = new ArrayList<>();

    private String mSource;

    private LinearLayout mRotationSelectView;

    private TextView mRotationSelect;

    private RotationPopupWiew mPopupWiew;

    private LinearLayout mGalleryLayout;

    private TextView mTitleGallery;

    private ImageView mDrawImage;

    private boolean isFolderShow = false;

    private RelativeLayout mDirectoryLayout;

    private RecyclerView mDirectoryRecyclerview;

    private MediaFolderAdapter mMediaFolderAdapter;

    private List<MediaFolder> mCurrentFolders;

    private List<MediaFolder> mVideoMediaFolders;

    private List<MediaFolder> mImageMediaFolders;

    private MediaFolderViewModel mMediaFolderViewModel;

    private boolean isVideoSelect = true;

    private String mCurrentVideoFolder;

    private String mCurrentImageFolder;

    private boolean isInitFolder = true;

    private HVETemplateInfo templateInfo;

    private TemplateProjectBean templateProjectBean;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_video_module_pick;
    }

    @Override
    protected void initView(View view) {
        contentId = R.id.fragment_content;
        mCloseIcon = view.findViewById(R.id.iv_close);
        mVideoTv = view.findViewById(R.id.tv_video);
        mPictureTv = view.findViewById(R.id.tv_picture);
        mChoiceRecyclerview = view.findViewById(R.id.choice_recyclerview);
        mTvTotalMaterial = view.findViewById(R.id.tv_total_material);
        mAddCardView = view.findViewById(R.id.card_add);
        mVideoIndicator = view.findViewById(R.id.indicator_video);
        mPictureIndicator = view.findViewById(R.id.indicator_picture);
        mRotationSelectView = view.findViewById(R.id.rotation_select_view);
        mRotationSelect = view.findViewById(R.id.rotation_select);
        mGalleryLayout = view.findViewById(R.id.gallery_layout);
        mTitleGallery = view.findViewById(R.id.title_gallery);
        mDrawImage = view.findViewById(R.id.iv_draw);
        mDirectoryLayout = view.findViewById(R.id.rl_directory);
        mDirectoryRecyclerview = view.findViewById(R.id.directory_recyclerview);
        if (mAddCardView != null) {
            mAddCardView.setEnabled(false);
        }
    }

    @Override
    protected void initObject() {
        mCurrentVideoFolder = getString(R.string.select_media_recent_projects);
        mCurrentImageFolder = getString(R.string.select_media_recent_projects);
        initSpinner();
        mUseDrawable = ContextCompat.getDrawable(context, R.drawable.use_module_selector);
        mUnUseDrawable = ContextCompat.getDrawable(context, R.drawable.unuse_module_btn_bg);
        mMaterialDatas = new ArrayList<>();
        ModuleEditViewModel moduleEditViewModel =
            new ViewModelProvider(mActivity, mFactory).get(ModuleEditViewModel.class);
        mMediaFolderViewModel = new ViewModelProvider(mActivity, mFactory).get(MediaFolderViewModel.class);
        moduleSelectManager = ModuleSelectManager.getInstance();
        replacePositionLiveData = moduleEditViewModel.getReplacePositionLiveData();
        replacePositionLiveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.i(TAG, "onChanged" + integer);
                if (mMaterialDatas.size() < integer + 1) {
                    return;
                }
                MaterialData materialData = mMaterialDatas.get(integer);
                mMaterialDatas.set(integer, materialData);
                mModulePickSelectAdapter.notifyItemChanged(integer + 1);
                mModulePickSelectAdapter.notifyDataSetChanged();
                mAddCardView.setEnabled(true);
                mAddCardView.setBackground(mMaterialDatas.size() == integer + 1 ? mUseDrawable : mUnUseDrawable);
                moduleSelectManager.resetCurrentIndex();
            }
        });

        SafeBundle bundle = new SafeBundle(getArguments());
        mTextureViewWidth = bundle.getFloat(TemplateDetailActivity.TEXTURE_VIEW_WIDTH);
        mTextureViewHeight = bundle.getFloat(TemplateDetailActivity.TEXTURE_VIEW_HEIGHT);
        mTemplateId = bundle.getString(TemplateDetailActivity.TEMPLATE_ID);
        String templateinfo = bundle.getString(Constant.TEMPLATE_KEY_INFO);
        String templatedetail = bundle.getString(Constant.TEMPLATE_KEY_DETAIL);
        templateInfo = GsonUtils.fromJson(templateinfo, HVETemplateInfo.class);
        templateProjectBean = GsonUtils.fromJson(templatedetail, TemplateProjectBean.class);
        mName = templateInfo == null ? "" : templateInfo.getName();
        mDescription = templateInfo == null ? "" : templateInfo.getDescription();
        mSource = bundle.getString("source");

        mModulePickSelectAdapter = new ModulePickSelectAdapter(context, mMaterialDatas);
        mChoiceRecyclerview.setItemAnimator(null);
        mLinearLayoutManager = new FilterLinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        mChoiceRecyclerview.setLayoutManager(mLinearLayoutManager);
        if (mChoiceRecyclerview.getItemDecorationCount() == 0) {
            HorizontalDividerDecoration decoration =
                new HorizontalDividerDecoration(context.getResources().getColor(R.color.app_main_color),
                    SizeUtils.dp2Px(context, 68), SizeUtils.dp2Px(context, 4));
            mChoiceRecyclerview.addItemDecoration(decoration);
        }
        mChoiceRecyclerview.setAdapter(mModulePickSelectAdapter);

        ModuleVideoFragment mVideoFragment = new ModuleVideoFragment();
        ModulePictureFragment mPictureFragment = new ModulePictureFragment();
        fragments = new ArrayList<>();
        fragments.add(mVideoFragment);
        fragments.add(mPictureFragment);
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager.isDestroyed()) {
            return;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(contentId, fragments.get(0));
        transaction.commit();
        currIndex = 0;
        mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment_home);
        mCurrentFolders = new ArrayList<>();
        mVideoMediaFolders = new ArrayList<>();
        mImageMediaFolders = new ArrayList<>();
        initAdapter();
        resetVideoOrPicture();

    }

    @Override
    protected void initData() {
        mMediaFolderViewModel.initFolder();

        mMediaFolderViewModel.getGalleryVideoSelect().observe(this, videoSelect -> {
            isVideoSelect = videoSelect;
            mTitleGallery.setText(videoSelect
                ? (isInitFolder ? mActivity.getString(R.string.select_media_recent_projects) : mCurrentVideoFolder)
                : mCurrentImageFolder);
            if (isInitFolder) {
                isInitFolder = false;
            }
            if (mCurrentFolders != null) {
                mCurrentFolders.clear();
                mCurrentFolders.addAll(videoSelect ? mVideoMediaFolders : mImageMediaFolders);
            }
            mMediaFolderAdapter.notifyDataSetChanged();
        });

        mMediaFolderViewModel.getVideoMediaData().observe(this, mediaFolders -> {
            mVideoMediaFolders.addAll(mediaFolders);
            if (isVideoSelect && mCurrentFolders != null) {
                mCurrentFolders.clear();
                mCurrentFolders.addAll(mVideoMediaFolders);
                mMediaFolderAdapter.notifyDataSetChanged();
            }
        });

        mMediaFolderViewModel.getImageMediaData().observe(this, mediaFolders -> {
            mImageMediaFolders.addAll(mediaFolders);
            if (!isVideoSelect && mCurrentFolders != null) {
                mCurrentFolders.clear();
                mCurrentFolders.addAll(mImageMediaFolders);
                mMediaFolderAdapter.notifyDataSetChanged();
            }
        });
        initPhotoError();
        getTemplateResource();
    }

    @SuppressLint("SetTextI18n")
    private void getTemplateResource() {
        for (HVETemplateElement item : templateProjectBean.getEditableElements()) {
            MaterialData data = new MaterialData();
            data.setValidDuration(item.getDuration());
            mMaterialDatas.add(data);
            mediaDatas.add(CropDataHelper.convertMaterialDataToMediaData(data));
        }
        mModulePickSelectAdapter.notifyDataSetChanged();
        ModuleSelectManager.getInstance().initMaterials(mMaterialDatas);

        String materialNum = NumberFormat.getInstance().format(mMaterialDatas.size());
        String materialNumText = mActivity.getResources()
            .getQuantityString(R.plurals.piece_material_number, mMaterialDatas.size(), materialNum);
        SpannableString materialNumContent = new SpannableString(materialNumText);
        Utils.setTextAttrs(materialNumContent, materialNum, R.color.color_text_focus);
        mTvTotalMaterial.setText(materialNumContent);
    }

    private void initPhotoError() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    HVETemplateManager.HVETemplateProjectPrepareCallback downloadResourceCallBack =
        new HVETemplateManager.HVETemplateProjectPrepareCallback() {
            @Override
            public void onSuccess() {
                tabToEditActivity();
            }

            @Override
            public void onProgress(int progress) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (context == null) {
                            return;
                        }
                        updateProgress(context.getString(R.string.module_compose), progress);
                        SmartLog.i(TAG, "onDownloading progress value is : " + progress);
                    }
                });
            }

            @Override
            public void onFail(int errorCode) {
                completeCompose();
                FragmentActivity fragmentActivity = getActivity();
                if (fragmentActivity == null) {
                    return;
                }
                fragmentActivity.runOnUiThread(() -> {
                    ToastWrapper
                        .makeText(fragmentActivity.getApplicationContext(),
                            fragmentActivity.getApplicationContext().getString(R.string.result_illegal), 700)
                        .show();
                });
            }
        };

    @Override
    protected void initEvent() {
        if (mGalleryLayout != null) {
            mGalleryLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
                isFolderShow = !isFolderShow;
                setFolderVisibility();
            }));
        }
        mCloseIcon.setOnClickListener(new OnClickRepeatedListener(v -> {
            finishFragment();
        }));

        mVideoTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currIndex != 0) {
                selectChanged(0);
                currIndex = 0;
                resetVideoOrPicture();
            }
        }));

        mPictureTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currIndex != 1) {
                selectChanged(1);
                currIndex = 1;
                resetVideoOrPicture();
            }
        }));

        mAddCardView.setOnClickListener(new OnClickRepeatedListener(v -> {
            synthesis();
        }));

        ModuleSelectManager.getInstance().addOnSelectItemChangeListener(item -> {
            int index = ModuleSelectManager.getInstance().getInitItemList().indexOf(item);
            if (index >= mMaterialDatas.size()) {
                return;
            }
            mMaterialDatas.set(index, item);

            mediaDatas.set(index, CropDataHelper.convertMaterialDataToMediaData(item));
            mModulePickSelectAdapter.notifyItemChanged(index);
            checkCurrentPosition();

            templateProjectBean.getEditableElements().get(index).setUrl(item.getPath());

            mModulePickSelectAdapter.notifyDataSetChanged();
            boolean canNextStep = ModuleSelectManager.getInstance().canNextStep();
            mAddCardView.setEnabled(canNextStep);
            mAddCardView.setBackground(canNextStep ? mUseDrawable : mUnUseDrawable);
        });

        mMediaFolderAdapter.setOnItemClickListener(new RMCommandAdapter.OnItemClickListener() {
            @Override
            public void onItemTouch(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
            }

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                isFolderShow = false;
                setFolderVisibility();
                MediaFolder folder = mCurrentFolders.get(dataPosition);
                if (folder == null) {
                    return;
                }
                mTitleGallery.setText(folder.getDirName());
                if (isVideoSelect) {
                    mCurrentVideoFolder = folder.getDirName();
                } else {
                    mCurrentImageFolder = folder.getDirName();
                }
                mMediaFolderViewModel.setFolderPathSelect(dataPosition == 0 ? new MediaFolder() : folder);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                return false;
            }
        });

        if (isValidActivity()) {
            mActivity.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (isFolderShow) {
                        isFolderShow = false;
                        setFolderVisibility();
                    } else {
                        finishFragment();
                    }
                }
            });
        }
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void synthesis() {
        NavDestination navDestination = mNavController.getCurrentDestination();
        if (navDestination != null && navDestination.getId() == R.id.videoModulePickFragment) {
            if (templateProjectBean == null) {
                return;
            }

            if (mMaterialDatas == null || mMaterialDatas.size() <= 0) {
                SmartLog.e(TAG, "resource path is null, ");

                FragmentActivity fragmentActivity = getActivity();
                if (fragmentActivity == null) {
                    return;
                }

                fragmentActivity.runOnUiThread(() -> {
                    ToastWrapper.makeText(fragmentActivity.getApplication(), getString(R.string.media_not_select))
                        .show();
                });
                return;
            }
            if (mMaterialDatas.size() != templateProjectBean.getEditableElements().size()) {
                SmartLog.e(TAG, "resource path is illegal, path size is: " + mMaterialDatas.size() + "; should be: "
                    + templateProjectBean.getEditableElements().size());
                return;
            }

            for (int i = 0; i < templateProjectBean.getEditableElements().size(); i++) {
                templateProjectBean.getEditableElements().get(i).setUrl(mMaterialDatas.get(i).getPath());
            }
            compose();
            HVETemplateManager.getInstance()
                .prepareTemplateProject(templateProjectBean.getTemplateId(), downloadResourceCallBack);
        }
    }

    private void tabToEditActivity() {
        if (mActivity == null) {
            return;
        }
        Intent intent = new Intent(mActivity, VideoModuleDetailActivity.class);

        intent.putParcelableArrayListExtra(Constant.EXTRA_MODULE_SELECT_RESULT,
            (ArrayList<MaterialData>) mMaterialDatas);

        intent.putExtra(TemplateDetailActivity.TEXTURE_VIEW_WIDTH, mTextureViewWidth);
        intent.putExtra(TemplateDetailActivity.TEXTURE_VIEW_HEIGHT, mTextureViewHeight);
        intent.putExtra(TemplateDetailActivity.TEMPLATE_ID, mTemplateId);
        intent.putExtra(TemplateDetailActivity.NAME, mName);
        intent.putExtra(TemplateDetailActivity.DESCRIPTION, mDescription);
        intent.putExtra("source", mSource);
        intent.putExtra(Constant.TEMPLATE_KEY_DETAIL, new Gson().toJson(templateProjectBean));

        VideoModuleEditFragment.setIsEditorReady(true);
        mActivity.startActivityForResult(intent, REQUEST_EDIT_CODE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (TEMPLATE_VIDEO_SP != null) {
            TEMPLATE_VIDEO_SP.clear();
        }
    }

    private void checkCurrentPosition() {
        int currentIndex = ModuleSelectManager.getInstance().getCurrentIndex();
        if (currentIndex > 0 && currentIndex < mMaterialDatas.size()) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mChoiceRecyclerview.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(currentIndex - 1, 0);
            }
        }
    }

    private void compose() {
        showComposeDialog();
        handler.sendEmptyMessage(REFRESH);
    }

    private void showComposeDialog() {
        moduleComposeDialog = new ModuleComposeDialog(context, context.getString(R.string.module_compose));
        moduleComposeDialog.setOnDismissListener(dialog1 -> {
            if (mActivity == null) {
                return;
            }
            moduleComposeDialog.hideLoading();
            WindowManager.LayoutParams attributes = mActivity.getWindow().getAttributes();
            attributes.alpha = 1f;
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mActivity.getWindow().setAttributes(attributes);
        });
        WindowManager.LayoutParams layoutParams = mActivity.getWindow().getAttributes();
        layoutParams.alpha = 0.6f;
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mActivity.getWindow().setAttributes(layoutParams);
        moduleComposeDialog.show();
        moduleComposeDialog.showLoading();

        if (moduleComposeDialog.getWindow() != null) {
            WindowManager.LayoutParams lp = moduleComposeDialog.getWindow().getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            moduleComposeDialog.getWindow().setAttributes(lp);
        }
        moduleComposeDialog.setOnCancelClickListener(() -> {
        });
    }

    private void updateProgress(String str, int progress) {
        if (moduleComposeDialog != null && moduleComposeDialog.isShowing() && progress <= 100) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    moduleComposeDialog.updateMessage(str, progress);
                }
            });
        }
    }

    private void completeCompose() {
        if (moduleComposeDialog != null && moduleComposeDialog.isShowing()) {
            SmartLog.i(TAG, "project is ready");
            moduleComposeDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_CODE && resultCode == Constant.RESULT_CODE) {
            ModuleSelectManager.getInstance().destroy();
            closeModule(false);
        } else if (requestCode == REQUEST_EDIT_CODE && resultCode == RESULT_OK) {
            if (mActivity != null) {
                mActivity.setResult(RESULT_OK);
                mActivity.finish();
            }
        } else if (requestCode == REQUEST_PHOTO_CODE && resultCode == RESULT_OK) {
            replacePositionLiveData.setValue(ModuleSelectManager.getInstance().getCurrentIndex());
        } else if (requestCode == REQUEST_VIDEO_CODE && resultCode == RESULT_OK) {
            replacePositionLiveData.setValue(ModuleSelectManager.getInstance().getCurrentIndex());
        } else if (requestCode == REQUEST_MODULE_SHOT && resultCode == RESULT_OK) {
            SafeIntent safeIntent = new SafeIntent(data);
            MediaData mediaData = safeIntent.getParcelableExtra(Constant.CropConst.INTENT_EXTRAS_CROP_RESULT_DATA);

            if (mediaData == null) {
                return;
            }
            int position = safeIntent.getIntExtra(Constant.CropConst.INTENT_EXTRAS_POSITION, 0);
            mediaDatas.set(position, mediaData);
            SmartLog.i(TAG,
                "[onActivityResult] index: " + mediaData.getIndex() + "; TemplateCenterX :"
                    + mediaData.getTemplateCenterX() + "; TemplateCenterY :" + mediaData.getTemplateCenterY()
                    + "; TemplateScaleWidth ：" + mediaData.getTemplateScaleWidth() + "; TemplateScaleHeight ："
                    + mediaData.getTemplateScaleHeight());
            resetMaterialDataAfterCrop(mediaData, position);
        }
    }

    private void resetMaterialDataAfterCrop(MediaData mediaData, int position) {
        if (mediaData == null || position < 0) {
            return;
        }
        mMaterialDatas.get(position).setCutTrimIn(mediaData.getCutTrimIn());
        mMaterialDatas.get(position).setCutTrimOut(mediaData.getCutTrimOut());
        mMaterialDatas.get(position).setGlLeftBottomX(mediaData.getGlLeftBottomX());
        mMaterialDatas.get(position).setGlLeftBottomY(mediaData.getGlLeftBottomY());
        mMaterialDatas.get(position).setGlRightTopX(mediaData.getGlRightTopX());
        mMaterialDatas.get(position).setGlRightTopY(mediaData.getGlRightTopY());
        mMaterialDatas.get(position).setScaleX(mediaData.getScaleX());
        mMaterialDatas.get(position).setScaleY(mediaData.getScaleY());
        mMaterialDatas.get(position).setRotation(mediaData.getRotation());
        mMaterialDatas.get(position).setMirrorStatus(mediaData.isMirrorStatus());
        mMaterialDatas.get(position).setVerticalMirrorStatus(mediaData.isVerticalMirrorStatus());

        mMaterialDatas.get(position).setTemplateCenterX(mediaData.getTemplateCenterX());
        mMaterialDatas.get(position).setTemplateCenterY(mediaData.getTemplateCenterY());
        mMaterialDatas.get(position).setTemplateScaleWidth(mediaData.getTemplateScaleWidth());
        mMaterialDatas.get(position).setTemplateScaleHeight(mediaData.getTemplateScaleHeight());
    }

    private void showGiveUpDialog() {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            return;
        }
        CommonBottomDialog dialog = new CommonBottomDialog(fragmentActivity);
        String giveUpEdit = "";
        String giveUpYes = "";
        String exportCancel = "";

        Application application = fragmentActivity.getApplication();
        if (application != null) {
            giveUpEdit = application.getString(R.string.is_give_up_edit_t1);
        }
        Context context = getContext();
        if (context != null) {
            if (LanguageUtils.isEn()) {
                giveUpYes = context.getString(R.string.is_give_up_yes_t).toUpperCase(Locale.ENGLISH);
                exportCancel = context.getString(R.string.video_edit_export_cancel).toUpperCase(Locale.ENGLISH);
            } else {
                giveUpYes = context.getString(R.string.is_give_up_yes_t);
                exportCancel = context.getString(R.string.video_edit_export_cancel);
            }
        }

        dialog.show(giveUpEdit, giveUpYes, exportCancel);
        dialog.setOnDialogClickLister(new CommonBottomDialog.OnDialogClickLister() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onAllowClick() {
                closeModule(true);
            }
        });
    }

    private void closeModule(boolean isStopEditor) {
        ModuleSelectManager.getInstance().destroy();
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    private void resetVideoOrPicture() {
        mMediaFolderViewModel.setGalleryVideoSelect(currIndex == 0);
        mDrawImage.setSelected(true);
        mTitleGallery.setTextColor(ContextCompat.getColor(mActivity, R.color.tab_text_tint_color));
        mTitleGallery.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        mVideoTv.setTextColor(currIndex == 0 ? ContextCompat.getColor(context, R.color.tab_text_tint_color)
            : ContextCompat.getColor(context, R.color.color_text_second_level));
        mPictureTv.setTextColor(currIndex == 0 ? ContextCompat.getColor(context, R.color.color_text_second_level)
            : ContextCompat.getColor(context, R.color.tab_text_tint_color));
        mVideoIndicator.setVisibility(currIndex == 0 ? View.VISIBLE : View.INVISIBLE);
        mPictureIndicator.setVisibility(currIndex == 1 ? View.VISIBLE : View.INVISIBLE);
    }

    public void selectChanged(int index) {
        Fragment fragment = fragments.get(index);
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager.isDestroyed()) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        getCurrentFragment().onPause();
        if (fragment.isAdded()) {
            fragment.onResume();
        } else {
            ft.add(contentId, fragment);
        }
        showTab(index);
        ft.commit();
    }

    protected void showTab(int idx) {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager.isDestroyed()) {
            return;
        }
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currIndex);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        completeCompose();
        handler.removeCallbacksAndMessages(null);
        ModuleSelectManager.getInstance().destroy();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == REFRESH) {
                if (VideoModuleEditFragment.isEditorReady()) {
                    completeCompose();
                    removeMessages(REFRESH);
                }
                this.sendEmptyMessageDelayed(REFRESH, 100);
            }
        }
    };

    private void initAdapter() {
        mMediaFolderAdapter =
            new MediaFolderAdapter(mActivity, mCurrentFolders, R.layout.adapter_media_pick_folder_item);
        mDirectoryRecyclerview.setLayoutManager(new FilterLinearLayoutManager(mActivity));
        mDirectoryRecyclerview.setAdapter(mMediaFolderAdapter);
    }

    private void initSpinner() {
        final String[] rotationMenu = mActivity.getResources().getStringArray(R.array.rotation_menu);
        List<String> rotationList = new ArrayList<>(Arrays.asList(rotationMenu));
        mRotationSelect.setText(rotationMenu.length > 0 ? rotationMenu[0] : "");

        int width;

        if (mPopupWiew == null) {
            mPopupWiew = new RotationPopupWiew(mActivity, rotationList);
            width = mPopupWiew.getPopupWidth();
        } else {
            width = mPopupWiew.getContentView().getMeasuredWidth();
        }

        mPopupWiew.setOnActionClickListener(new RotationPopupWiew.OnActionClickListener() {
            @Override
            public void onFullClick() {
                mRotationSelect.setText(rotationMenu.length > 0 ? rotationMenu[0] : "");
                mMediaFolderViewModel.setRotationState(0);
            }

            @Override
            public void onPortraitClick() {
                mRotationSelect.setText(rotationMenu.length > 1 ? rotationMenu[1] : "");
                mMediaFolderViewModel.setRotationState(1);
            }

            @Override
            public void onLandClick() {
                mRotationSelect.setText(rotationMenu.length > 2 ? rotationMenu[2] : "");
                mMediaFolderViewModel.setRotationState(2);
            }
        });

        mRotationSelectView.setOnClickListener(new OnClickRepeatedListener(v -> {

            mPopupWiew.showAsDropDown(mRotationSelectView, -width + mRotationSelectView.getWidth() + 2,
                mRotationSelectView.getHeight() - 40);

            mPopupWiew.setOnDismissListener(() -> {
                WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
                params.alpha = 1.0f;
                mActivity.getWindow().setAttributes(params);
            });

        }));
    }

    private void finishFragment() {
        NavDestination navDestination = mNavController.getCurrentDestination();
        if (navDestination != null && navDestination.getId() == R.id.videoModulePickFragment) {
            if (mNavController.getCurrentDestination() != null
                && navDestination.getId() == R.id.videoModulePickFragment) {
                if (ModuleSelectManager.getInstance().getSelectItemList().size() > 0) {
                    showGiveUpDialog();
                } else {
                    closeModule(true);
                }
            }
        }
    }

    private void setFolderVisibility() {
        if (isFolderShow) {
            rotateArrow(false);
            mDirectoryLayout.setVisibility(View.VISIBLE);
            Animation aniSlideIn = AnimationUtils.loadAnimation(mActivity, R.anim.top_in);
            mDirectoryLayout.startAnimation(aniSlideIn);
        } else {
            Animation aniSlideOut = AnimationUtils.loadAnimation(mActivity, R.anim.top_out);
            aniSlideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mDirectoryLayout.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mDirectoryLayout.startAnimation(aniSlideOut);
            rotateArrow(true);
        }
    }

    private void rotateArrow(boolean flag) {
        float pivotX = BigDecimalUtil.div(mDrawImage.getWidth(), 2f);
        float pivotY = BigDecimalUtil.div(mDrawImage.getHeight(), 2f);
        float fromDegrees;
        float toDegrees;
        if (flag) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);

        animation.setFillAfter(true);
        mDrawImage.startAnimation(animation);
    }
}