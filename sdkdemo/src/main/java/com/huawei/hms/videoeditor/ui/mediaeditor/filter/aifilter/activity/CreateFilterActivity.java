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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.activity;

import static com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel.ExclusiveFilterPanelViewModel.FILTER_TYPE_CLONE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel.ExclusiveFilterPanelViewModel.FILTER_TYPE_SINGLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.common.network.NetworkUtil;
import com.huawei.hms.videoeditor.common.utils.BitmapDecodeUtils;
import com.huawei.hms.videoeditor.materials.HVELocalMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.materials.HVEMaterialsManager;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.sdk.ai.HVEExclusiveFilter;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RMCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.decoration.GridItemDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.dialog.ProgressDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.PicturesAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel.ExclusiveFilterPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel.FolderListViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.viewmodel.PictureListViewModel;
import com.huawei.hms.videoeditor.ui.mediapick.adapter.MediaFolderAdapter;
import com.huawei.hms.videoeditor.ui.mediapick.bean.MediaFolder;
import com.huawei.hms.videoeditor.ui.mediapick.manager.MediaPickManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CreateFilterActivity extends BaseActivity implements MediaPickManager.OnSelectItemChangeListener {
    private static final String TAG = "CreateFilterActivity";

    private static final String FILTER_TYPE = "filterType";

    private static final String FILTER_CLONE = "clone";

    private static final int MAX_PROGRESS = 100;

    private static final int FILTER_MAX_QUANTITY = 99;

    private static final int MAX_TEXT = 5;

    private boolean isFolderShow = false;

    private OnClickRepeatedListener galleryListener;

    private HVEExclusiveFilter mFilterEngine;

    private Bitmap mBitmap;

    private Bitmap mFilterBitmap;

    private PicturesAdapter mMediaAdapter;

    private PictureListViewModel mPictureListViewModel;

    private FolderListViewModel mFolderListViewModel;

    private ExclusiveFilterPanelViewModel mExclusiveFilterPanelViewModel;

    private MediaPickManager mMediaPickManager;

    private MediaFolderAdapter mMediaFolderAdapter;

    private List<MediaFolder> mCurrentFolders;

    private List<MediaFolder> mImageMediaFolders;

    private RelativeLayout mDirectoryLayout;

    private RecyclerView mDirectoryRecyclerview;

    private FrameLayout mCloseLayout;

    private TextView mTitleGallery;

    private ImageView mDrawImage;

    private RecyclerView mPictureRecyclerView;

    private View mPlaceholderView;

    private ImageView mOriginalItem;

    private ImageView mOriginalDelete;

    private TextView mOriginalNum;

    private EditorTextView mOriginalText;

    private ImageView mRenderingItem;

    private ImageView mRenderingDelete;

    private TextView mRenderingNum;

    private EditorTextView mRenderingText;

    private EditText mEditRename;

    private TextView mEditRenameLoad;

    private RelativeLayout mLayoutCreate;

    private ImageView mImitationRenderingItem;

    private ImageView mImitationRenderingDelete;

    private TextView mImitationRenderingText;

    private boolean isOriginalView = false;

    private boolean isRenderingView = false;

    private MediaData mOriginalData;

    private MediaData mRenderingData;

    private String mFilterType;

    private boolean isCanCreate = false;

    private List<MediaData> mSelectList = new ArrayList<>();

    private int renameNum;

    private ProgressDialog mProgressDialog;

    private List<HVELocalMaterialInfo> cutContentList;

    private Timer mTimer = null;

    private TimerTask mTimerTask = null;

    private long mFilterStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.media_crop_background;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_filter);
        initView();
        initObject();
        initData();
        initEvent();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.reSizeDialog();
        }
    }

    @SuppressLint("StringFormatMatches")
    private void initView() {
        mCloseLayout = findViewById(R.id.close_layout);
        mPictureRecyclerView = findViewById(R.id.choice_recyclerview);
        mPlaceholderView = findViewById(R.id.view_placeholder);
        mTitleGallery = findViewById(R.id.title_gallery);
        mDrawImage = findViewById(R.id.iv_draw);
        mDirectoryLayout = findViewById(R.id.rl_directory);
        mDirectoryRecyclerview = findViewById(R.id.directory_recyclerview);

        mOriginalItem = findViewById(R.id.img_original_item);
        mOriginalDelete = findViewById(R.id.iv_original_delete);
        mOriginalNum = findViewById(R.id.iv_original_num);
        mOriginalText = findViewById(R.id.tv_original);

        mRenderingItem = findViewById(R.id.img_rendering_item);
        mRenderingDelete = findViewById(R.id.iv_rendering_delete);
        mRenderingNum = findViewById(R.id.iv_rendering_num);
        mRenderingText = findViewById(R.id.tv_rendering);

        mImitationRenderingItem = findViewById(R.id.imitation_rendering_item);
        mImitationRenderingDelete = findViewById(R.id.imitation_rendering_delete);
        mImitationRenderingText = findViewById(R.id.imitation_rendering);

        mEditRename = findViewById(R.id.text_rename);
        mEditRenameLoad = findViewById(R.id.text_rename_load);
        mLayoutCreate = findViewById(R.id.layout_choice_create);

        mEditRenameLoad.setText(
            String.format(Locale.ROOT, getResources().getString(R.string.exclusive_filter_tip_text11), MAX_TEXT));
    }

    private void initObject() {
        downloadTemplate();
        SafeIntent safeIntent = new SafeIntent(getIntent());
        mFilterType = safeIntent.getStringExtra(FILTER_TYPE);
        setLayoutCreatesTatus();

        mPictureListViewModel = new ViewModelProvider(this, factory).get(PictureListViewModel.class);
        mFolderListViewModel = new ViewModelProvider(this, factory).get(FolderListViewModel.class);
        mExclusiveFilterPanelViewModel = new ViewModelProvider(this, factory).get(ExclusiveFilterPanelViewModel.class);
        mMediaPickManager = MediaPickManager.getInstance();
        mMediaPickManager.clear();

        if (mFilterType.equals(FILTER_CLONE)) {
            mMediaPickManager.setMaxSelectCount(2);
            mOriginalItem.setSelected(true);
            mRenderingItem.setSelected(false);
            mOriginalItem.setImageResource(R.drawable.icon_tag_photo);
            mRenderingItem.setImageResource(R.drawable.icon_tag_photo);
            mPlaceholderView.setVisibility(View.VISIBLE);

            mImitationRenderingItem.setVisibility(View.GONE);
            mImitationRenderingDelete.setVisibility(View.GONE);
            mImitationRenderingText.setVisibility(View.GONE);
        } else {
            mMediaPickManager.setMaxSelectCount(1);
            mImitationRenderingItem.setVisibility(View.VISIBLE);
            mImitationRenderingText.setVisibility(View.VISIBLE);
            mImitationRenderingItem.setSelected(true);
            mImitationRenderingItem.setImageResource(R.drawable.icon_tag_photo);

            mOriginalItem.setVisibility(View.GONE);
            mOriginalText.setVisibility(View.GONE);
            mRenderingItem.setVisibility(View.GONE);
            mRenderingText.setVisibility(View.GONE);
        }

        mPictureRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        mPictureRecyclerView.setItemAnimator(defaultItemAnimator);
        mPictureRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        if (mPictureRecyclerView.getItemDecorationCount() == 0) {
            mPictureRecyclerView.addItemDecoration(new GridItemDividerDecoration(SizeUtils.dp2Px(this, 8),
                SizeUtils.dp2Px(this, 8), ContextCompat.getColor(this, R.color.black)));
        }
        mMediaAdapter = new PicturesAdapter(this);
        mPictureRecyclerView.setAdapter(mMediaAdapter);

        mCurrentFolders = new ArrayList<>();
        mImageMediaFolders = new ArrayList<>();

        mMediaFolderAdapter = new MediaFolderAdapter(this, mCurrentFolders, R.layout.adapter_media_pick_folder_item);
        mDirectoryRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mDirectoryRecyclerview.setAdapter(mMediaFolderAdapter);

        galleryListener = new OnClickRepeatedListener(v -> {
            if (!isFolderShow) {
                mCurrentFolders.clear();
                mCurrentFolders.addAll(mImageMediaFolders);
                mMediaFolderAdapter.notifyDataSetChanged();
            }
            isFolderShow = !isFolderShow;
            setFolderVisibility();
        });
    }

    private void initEvent() {
        MediaPickManager.getInstance().addOnSelectItemChangeListener(this);

        if (mTitleGallery != null) {
            mTitleGallery.setOnClickListener(galleryListener);
        }
        if (mDrawImage != null) {
            mDrawImage.setOnClickListener(galleryListener);
        }

        mCloseLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (isFolderShow) {
                isFolderShow = false;
                setFolderVisibility();
                return;
            }
            mMediaPickManager.clear();
            finish();
        }));

        mMediaPickManager.addOnSelectItemChangeListener(item -> {
            mSelectList = mMediaPickManager.getSelectItemList();
            SmartLog.d(TAG, "getIndex: " + item.getIndex());
            setLayoutCreatesTatus();

            if (!item.isSetSelected()) {
                if (mOriginalData != null && item.getPath().equals(mOriginalData.getPath())) {
                    deleteOriginal();
                } else if (mRenderingData != null && item.getPath().equals(mRenderingData.getPath())) {
                    eleteRendering();
                }
            }
            if (item.getIndex() <= 0) {
                return;
            }
            if (mFilterType.equals(FILTER_CLONE)) {
                if (mOriginalItem.isSelected() && !isOriginalView) {
                    addPicturesToSelect(item, mOriginalItem, item.getIndex());
                } else if (mRenderingItem.isSelected() && !isRenderingView) {
                    addPicturesToSelect(item, mRenderingItem, item.getIndex());
                }
            } else {
                addPicturesToSelect(item, mImitationRenderingItem, item.getIndex());
            }
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
                mTitleGallery.setText(folder.getDirName());
                mFolderListViewModel.setFolderPathSelect(dataPosition == 0 ? "" : folder.getDirName());
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int dataPosition, int position) {
                return false;
            }
        });

        mOriginalItem.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (!isOriginalView) {
                mOriginalItem.setSelected(true);
                mRenderingItem.setSelected(false);
            }
        }));

        mRenderingItem.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (!isRenderingView) {
                mOriginalItem.setSelected(false);
                mRenderingItem.setSelected(true);
            }
        }));

        mOriginalDelete.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mOriginalData != null) {
                mMediaPickManager.removeSelectItemAndSetIndex(mOriginalData);
            }
            deleteOriginal();
        }));

        mRenderingDelete.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mRenderingData != null) {
                mMediaPickManager.removeSelectItemAndSetIndex(mRenderingData);
            }
            eleteRendering();
        }));

        mImitationRenderingDelete.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (mRenderingData != null) {
                mMediaPickManager.removeSelectItemAndSetIndex(mRenderingData);
            }
            eleteRendering();
        }));

        mEditRename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                renameNum = charSequence.length();
                if (renameNum > 5) {
                    Drawable drawable = getResources().getDrawable(R.drawable.filter_rename_line_red);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mEditRename.setCompoundDrawables(null, null, null, drawable);
                    mEditRenameLoad.setVisibility(View.VISIBLE);
                } else if (renameNum == 0) {
                    Drawable drawable = getResources().getDrawable(R.drawable.filter_rename_line_default_white);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mEditRename.setCompoundDrawables(null, null, null, drawable);
                    mEditRenameLoad.setVisibility(View.GONE);
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.filter_rename_line);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mEditRename.setCompoundDrawables(null, null, null, drawable);
                    mEditRenameLoad.setVisibility(View.GONE);
                }
                setLayoutCreatesTatus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLayoutCreate.setOnClickListener(new OnClickRepeatedListener(v -> {
            cutContentList = mExclusiveFilterPanelViewModel.queryAllMaterialsByType(HVEMaterialConstant.AI_FILTER);
            if (cutContentList.size() >= FILTER_MAX_QUANTITY) {
                ToastWrapper
                    .makeText(getApplication(), getText(R.string.exclusive_filter_tip_text13), Toast.LENGTH_LONG)
                    .show();
                return;
            }
            if (isCanCreate) {
                filterSynthesis();
            } else {
                if (!NetworkUtil.isNetworkConnected()) {
                    ToastWrapper
                        .makeText(getApplication(), getText(R.string.exclusive_filter_tip_text12), Toast.LENGTH_LONG)
                        .show();
                    return;
                }
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(CreateFilterActivity.this,
                        getString(R.string.exclusive_filter_creating) + "...");
                    mProgressDialog.setOnProgressClick(this::stopTimer);
                }
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show(getWindowManager());
                    mProgressDialog.setStopVisble(true);
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.setProgress(0);
                }
                startTimer();
            }
        }));
    }

    private void deleteOriginal() {
        mOriginalData = null;
        mOriginalDelete.setVisibility(View.GONE);
        mOriginalNum.setVisibility(View.GONE);
        isOriginalView = false;
        mOriginalItem.setImageDrawable(null);
        mOriginalItem.setSelected(true);
        mOriginalItem.setImageResource(R.drawable.icon_tag_photo);
        if (!isRenderingView) {
            mRenderingItem.setImageResource(R.drawable.icon_tag_photo);
            mRenderingItem.setSelected(false);
        }
    }

    private void eleteRendering() {
        mRenderingData = null;
        isRenderingView = false;
        if (mFilterType.equals(FILTER_CLONE)) {
            mRenderingDelete.setVisibility(View.GONE);
            mRenderingItem.setImageDrawable(null);
            mRenderingItem.setImageResource(R.drawable.icon_tag_photo);
            mRenderingNum.setVisibility(View.GONE);
            if (!isOriginalView) {
                mOriginalItem.setImageResource(R.drawable.icon_tag_photo);
                mOriginalItem.setSelected(true);
                mRenderingItem.setSelected(false);
            } else {
                mRenderingItem.setSelected(true);
            }
        } else {
            mImitationRenderingDelete.setVisibility(View.GONE);
            mImitationRenderingItem.setImageDrawable(null);
            mImitationRenderingItem.setSelected(true);
            mImitationRenderingItem.setImageResource(R.drawable.icon_tag_photo);
        }
    }

    private void startTimer() {
        downloadTemplate();
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        if (isCanCreate) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.setProgress(MAX_PROGRESS);
                                mProgressDialog.dismiss();
                            }
                            filterSynthesis();
                        } else {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            ToastWrapper
                                .makeText(getApplication(), getText(R.string.exclusive_filter_tip_text12),
                                    Toast.LENGTH_LONG)
                                .show();
                        }
                    });
                }
            };
        }
        mTimer.schedule(mTimerTask, 3000);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void downloadTemplate() {
        if (mFilterEngine == null) {
            mFilterEngine = new HVEExclusiveFilter();
        }

        mFilterEngine.initExclusiveFilterEngine(new HVEAIInitialCallback() {
            @Override
            public void onProgress(int progress) {
                SmartLog.d(TAG, "progress: " + progress);
                if (progress == MAX_PROGRESS) {
                    isCanCreate = true;
                }
            }

            @Override
            public void onSuccess() {
                SmartLog.d(TAG, "onDownloadSuccess！");
                isCanCreate = true;
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.d(TAG, "errorMsg: " + errorMessage);
                isCanCreate = false;
            }
        });
    }

    private void filterSynthesis() {
        if (mSelectList.size() <= 0) {
            return;
        }
        if (mSelectList.size() == 1) {
            mBitmap = BitmapDecodeUtils.decodeFile(mSelectList.get(0).getPath());
            if (mBitmap == null) {
                return;
            }
            mFilterStartTime = System.currentTimeMillis();

            String saveName = TextUtils.isEmpty(mEditRename.getText().toString())
                ? getString(R.string.custom) + "\u00A0" + String.format("%02d", cutContentList.size() + 1)
                : mEditRename.getText().toString();

            String id = mFilterEngine.createExclusiveEffect(mBitmap, saveName);

            if (TextUtils.isEmpty(id)) {
                SmartLog.e(TAG, "filterSynthesis failed");
                return;
            }

            operateSDKResult(id);
        } else if (mSelectList.size() == 2) {
            mBitmap = BitmapDecodeUtils.decodeFile(mSelectList.get(0).getPath());
            mFilterBitmap = BitmapDecodeUtils.decodeFile(mSelectList.get(1).getPath());
            if (mBitmap == null || mFilterBitmap == null) {
                return;
            }
            mFilterStartTime = System.currentTimeMillis();

            String saveName = TextUtils.isEmpty(mEditRename.getText().toString())
                ? getString(R.string.custom) + "\u00A0" + String.format("%02d", cutContentList.size() + 1)
                : mEditRename.getText().toString();

            String id = mFilterEngine.imitateFilter(mBitmap, mFilterBitmap, saveName);
            if (TextUtils.isEmpty(id)) {
                return;
            }

            operateSDKResult(id);
        }
    }

    private void initData() {
        mFolderListViewModel.initFolder();
        mTitleGallery.setText(getString(R.string.exclusive_filter_photo));

        mPictureListViewModel.getPageData().observe(this, pagedList -> {
            if (pagedList.size() > 0) {
                mMediaAdapter.submitList(pagedList);
            }
        });

        mFolderListViewModel.getFolderSelect().observe(this, folderPath -> {
            if (mPictureListViewModel == null) {
                return;
            }
            mPictureListViewModel.setDirName(folderPath);
            DataSource dataSource = mPictureListViewModel.getDataSource();
            if (dataSource == null) {
                return;
            }
            dataSource.invalidate();
        });

        mPictureListViewModel.getLoadAfterData().observe(this, LoadAfter -> {
            mMediaAdapter.notifyDataSetChanged();
        });

        mFolderListViewModel.getImageMediaData().observe(this, mediaFolders -> {
            mImageMediaFolders.addAll(mediaFolders);
        });
    }

    private void setFolderVisibility() {
        if (isFolderShow) {
            rotateArrow(false);
            mDirectoryLayout.setVisibility(View.VISIBLE);
            Animation aniSlideIn = AnimationUtils.loadAnimation(this, R.anim.top_in);
            mDirectoryLayout.startAnimation(aniSlideIn);
        } else {
            Animation aniSlideOut = AnimationUtils.loadAnimation(this, R.anim.top_out);
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
        float pivotX = BigDecimalUtils.div(mDrawImage.getWidth(), 2f);
        float pivotY = BigDecimalUtils.div(mDrawImage.getHeight(), 2f);
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

    @Override
    public void onSelectItemChange(MediaData item) {
        if (mMediaAdapter == null) {
            return;
        }
        PagedList<MediaData> currentList = mMediaAdapter.getCurrentList();
        if (currentList == null) {
            return;
        }
        int position = currentList.indexOf(item);
        mMediaAdapter.notifyItemChanged(position);
    }

    private void addPicturesToSelect(MediaData item, ImageView imageView, int selectItemCount) {
        if (imageView == mOriginalItem) {
            isOriginalView = true;
            mOriginalData = item;
            mOriginalDelete.setVisibility(View.VISIBLE);
            mOriginalNum.setVisibility(View.VISIBLE);
            mOriginalNum.setText(String.valueOf(selectItemCount));
            mOriginalItem.setSelected(false);
            if (!isRenderingView) {
                mRenderingItem.setSelected(true);
            }
        } else if (imageView == mRenderingItem) {
            isRenderingView = true;
            mRenderingData = item;
            mRenderingDelete.setVisibility(View.VISIBLE);
            mRenderingNum.setVisibility(View.VISIBLE);
            mRenderingNum.setText(String.valueOf(selectItemCount));
            mRenderingItem.setSelected(false);
            if (!isOriginalView) {
                mOriginalItem.setSelected(true);
            }
        } else if (imageView == mImitationRenderingItem) {
            isRenderingView = true;
            mRenderingData = item;
            mImitationRenderingDelete.setVisibility(View.VISIBLE);
            mImitationRenderingItem.setSelected(false);
        }
        Glide.with(this)
            .load(!TextUtils.isEmpty(item.getCoverUrl()) ? item.getCoverUrl() : item.getPath())
            .apply(new RequestOptions().transform(new MultiTransformation(new CenterCrop())))
            .into(imageView);
    }

    private void setLayoutCreatesTatus() {
        boolean isClone;
        if (mFilterType != null && mFilterType.equals(FILTER_CLONE)) {
            isClone = mSelectList.size() > 1 && renameNum < 6;
        } else {
            isClone = mSelectList.size() > 0 && renameNum < 6;
        }
        mLayoutCreate.setEnabled(isClone);
        mLayoutCreate.setBackground(
            isClone ? ContextCompat.getDrawable(CreateFilterActivity.this, R.drawable.background_card_selector)
                : ContextCompat.getDrawable(CreateFilterActivity.this, R.drawable.background_card_add_normal));
    }

    private void operateSDKResult(String resp) {
        HVELocalMaterialInfo materialInfo = HVEMaterialsManager.queryLocalMaterialById(resp);

        Intent intent = new Intent();
        if (mFilterType.equals(FILTER_CLONE)) {
            intent.putExtra("FilterType", FILTER_TYPE_CLONE);
        } else {
            intent.putExtra("FilterType", FILTER_TYPE_SINGLE);
        }
        intent.putExtra("FilterPath", materialInfo.getMaterialPath());
        intent.putExtra("FilterId", resp);
        intent.putExtra("FilterName", materialInfo.getMaterialName());
        intent.putExtra("FilterStartTime", mFilterStartTime);
        setResult(Constant.RESP_CODE_CHOOSE_IMAGE, intent);
        finish();
        ToastWrapper.makeText(getApplication(), getText(R.string.exclusive_filter_tip_text5), Toast.LENGTH_SHORT)
            .show();
    }

    @Override
    public void onBackPressed() {
        if (isFolderShow) {
            isFolderShow = false;
            setFolderVisibility();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPickManager.clear();
        mMediaPickManager = null;
        stopTimer();
        if (mSelectList != null) {
            mSelectList = null;
        }
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (mFilterBitmap != null) {
            mFilterBitmap.recycle();
            mFilterBitmap = null;
        }
    }
}
