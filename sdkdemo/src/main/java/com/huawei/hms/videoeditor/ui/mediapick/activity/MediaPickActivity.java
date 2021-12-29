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

package com.huawei.hms.videoeditor.ui.mediapick.activity;

import static com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager.DOWN_SAMPLING_DONE;
import static com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager.NEED_DOWN_SAMPLING;
import static com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager.NO_NEED_DOWN_SAMPLING;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.MAX_AUTO_TEMPLATE;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.MAX_PICK_NUM;
import static com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity.CLIPS_VIEW_TYPE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity.VIEW_NORMAL;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.common.utils.BitmapDecodeUtils;
import com.huawei.hms.videoeditor.common.utils.LanguageUtils;
import com.huawei.hms.videoeditor.sdk.HVEDownSamplingManager;
import com.huawei.hms.videoeditor.sdk.bean.HVEVisibleFormatBean;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.sdk.util.MemoryInfoUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RMCommandAdapter;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.tools.EditorRuntimeException;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.MediaDataDownSampleManager;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SystemUtils;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.Utils;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediapick.adapter.MediaFolderAdapter;
import com.huawei.hms.videoeditor.ui.mediapick.adapter.MediaPickSelectAdapter;
import com.huawei.hms.videoeditor.ui.mediapick.bean.MediaFolder;
import com.huawei.hms.videoeditor.ui.mediapick.fragment.GalleryFragment;
import com.huawei.hms.videoeditor.ui.mediapick.manager.MediaPickManager;
import com.huawei.hms.videoeditor.ui.mediapick.manager.MediaSelectDragCallback;
import com.huawei.hms.videoeditor.ui.mediapick.viewmodel.MediaFolderViewModel;
import com.huawei.secure.android.common.intent.SafeIntent;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MediaPickActivity extends BaseActivity {
    private static final String TAG = "MediaPickActivity";

    public static final String ACTION_TYPE = "action_type";

    public static final String DURATION = "duration";


    public static final int ACTION_ADD_MEDIA_TYPE = 1001;

    public static final int ACTION_APPEND_MEDIA_TYPE = 1002;

    public static final int ACTION_ADD_PIP_MEDIA_TYPE = 1003;

    public static final int ACTION_REPLACE_MEDIA_TYPE = 1004;

    public static final int ACTION_AUTO_SYNTHESIS_TYPE = 1005;

    public static final int REQ_PREVIEW_CODE = 1000;

    private FrameLayout mCloseLayout;

    private TextView mTitleGallery;

    private ImageView mDrawImage;

    private FrameLayout mContentLayout;

    private RelativeLayout mDirectoryLayout;

    private RecyclerView mDirectoryRecyclerview;

    private TextView mTitleMaterial;

    private ConstraintLayout mChoiceRootLayout;

    private LinearLayout mChoiceContentLayout;

    private RecyclerView mChoiceRecyclerview;

    private ImageView mQualityIcon;

    private EditorTextView mQualityText;

    private LinearLayout mQualityLayout;

    private TextView mTvTotalTime;

    private TextView mTvSelectVideoNum;

    private TextView mTvSelectPictureNum;

    private TextView mTvPressText;

    private TextView mTvPressCopy;

    private TextView mAddCardView;

    private Context mContext;

    public int mActionType = ACTION_ADD_MEDIA_TYPE;

    public long mCheckDuration;

    private boolean isQualitySelect = false;

    private boolean isFolderShow = false;

    private boolean isInitFolder = true;

    private boolean isVideoSelect = true;

    private String mCurrentVideoFolder;

    private String mCurrentImageFolder;

    private MediaFolderViewModel mMediaFolderViewModel;

    private List<MediaFolder> mVideoMediaFolders;

    private List<MediaFolder> mImageMediaFolders;

    private List<MediaFolder> mCurrentFolders;

    private MediaFolderAdapter mMediaFolderAdapter;

    private MediaPickManager mMediaPickManager;

    private ArrayList<MediaData> mSelectList;

    private MediaPickSelectAdapter mSelectAdapter;

    private ArrayList<MediaData> completesList;

    private List<Fragment> fragments;

    private static final int GALLERY_FRAGMENT = 0;

    private static final int MATERIAL_FRAGMENT = 0;

    private int mCurrentFragment = GALLERY_FRAGMENT;

    private OnClickRepeatedListener galleryListener;

    private TextView mSelectSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_pick);
        initView();
        initObject();
        initData();
        initEvent();
    }

    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
    private void initView() {
        mCloseLayout = findViewById(R.id.close_layout);
        mTitleGallery = findViewById(R.id.title_gallery);
        mDrawImage = findViewById(R.id.iv_draw);
        mTitleMaterial = findViewById(R.id.title_material);
        mDirectoryLayout = findViewById(R.id.rl_directory);
        mDirectoryRecyclerview = findViewById(R.id.directory_recyclerview);
        mContentLayout = findViewById(R.id.fragment_main);
        mChoiceRootLayout = findViewById(R.id.layout_choice_root);
        mChoiceContentLayout = findViewById(R.id.layout_choice_content);
        mChoiceRecyclerview = findViewById(R.id.choice_recyclerview);
        mTvTotalTime = findViewById(R.id.tv_total_time);
        mTvSelectVideoNum = findViewById(R.id.tv_select_video_num);
        mTvSelectPictureNum = findViewById(R.id.tv_select_picture_num);
        mTvPressText = findViewById(R.id.tv_press_drag);
        mTvPressCopy = findViewById(R.id.tv_press_drag_copy);
        mQualityIcon = findViewById(R.id.iv_choice_quality);
        mQualityText = findViewById(R.id.tv_choice_quality);
        mQualityLayout = findViewById(R.id.layout_choice_quality);
        mSelectSrc = findViewById(R.id.select_src);
        mAddCardView = findViewById(R.id.card_add);
        mAddCardView.setEnabled(false);

        if (LanguageUtils.isZh() || LanguageUtils.isEn()) {
            mTvPressText.setVisibility(View.VISIBLE);
            mTvPressCopy.setVisibility(View.GONE);
        } else {
            mTvPressText.setVisibility(View.GONE);
            mTvPressCopy.setVisibility(View.VISIBLE);
        }

        if (!MemoryInfoUtil.isLowMemoryDevice()) {
            mQualityLayout.setVisibility(View.VISIBLE);
        }

        if (!SystemUtils.isLowDevice()) {
            isQualitySelect = true;
            mQualityIcon.setSelected(true);
        }

        mSelectSrc.setText(
            Utils.setNumColor(String.format(Locale.ROOT, getResources().getString(R.string.select_media_has_select), 0),
                getResources().getColor(R.color.transparent)));

    }

    private void initObject() {
        mContext = this;
        mCurrentVideoFolder = getString(R.string.select_media_recent_projects);
        mCurrentImageFolder = getString(R.string.select_media_recent_projects);

        SafeIntent safeIntent = new SafeIntent(getIntent());
        mActionType = safeIntent.getIntExtra(ACTION_TYPE, ACTION_ADD_MEDIA_TYPE);
        mCheckDuration = safeIntent.getLongExtra(DURATION, 0);
        mMediaFolderViewModel = new ViewModelProvider(this, factory).get(MediaFolderViewModel.class);
        mMediaPickManager = MediaPickManager.getInstance();
        mMediaPickManager.clear();
        mSelectList = new ArrayList<>();
        mCurrentFolders = new ArrayList<>();
        mVideoMediaFolders = new ArrayList<>();
        mImageMediaFolders = new ArrayList<>();

        switch (mActionType) {
            case ACTION_ADD_MEDIA_TYPE:
            case ACTION_APPEND_MEDIA_TYPE:
                MediaPickManager.getInstance().setMaxSelectCount(MAX_PICK_NUM);
                break;
            case ACTION_ADD_PIP_MEDIA_TYPE:
            case ACTION_REPLACE_MEDIA_TYPE:
                mChoiceRootLayout.setVisibility(View.GONE);
                // mContentLayout
                mContentLayout.setPadding(0, 0, 0, SizeUtils.dp2Px(this, 16));
                break;
            case ACTION_AUTO_SYNTHESIS_TYPE:
                mAddCardView.setText(R.string.auto_template);
                MediaPickManager.getInstance().setMaxSelectCount(MAX_AUTO_TEMPLATE);
                break;
            default:
                break;
        }

        initAdapter();
        initFragment();
    }

    private void initAdapter() {
        mMediaFolderAdapter = new MediaFolderAdapter(this, mCurrentFolders, R.layout.adapter_media_pick_folder_item);
        mDirectoryRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mDirectoryRecyclerview.setAdapter(mMediaFolderAdapter);

        mSelectAdapter = new MediaPickSelectAdapter(this, mSelectList, mActionType);
        mChoiceRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        if (mChoiceRecyclerview.getItemDecorationCount() == 0) {
            mChoiceRecyclerview
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mContext, R.color.black),
                    SizeUtils.dp2Px(mContext, 68), SizeUtils.dp2Px(mContext, 4)));
        }
        mChoiceRecyclerview.setAdapter(mSelectAdapter);

        MediaSelectDragCallback mMediaSelectDragCallback = new MediaSelectDragCallback(mSelectAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(mMediaSelectDragCallback);
        touchHelper.attachToRecyclerView(mChoiceRecyclerview);
    }

    private void initFragment() {
        GalleryFragment galleryFragment = new GalleryFragment();
        fragments = new ArrayList<>(2);
        fragments.add(galleryFragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.isDestroyed()) {
            return;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_main, fragments.get(0));
        transaction.commit();
        mCurrentFragment = GALLERY_FRAGMENT;
        resetVideoOrPicture();
        galleryListener = new OnClickRepeatedListener(v -> {
            if (mCurrentFragment != GALLERY_FRAGMENT) {
                selectChanged(0);
                resetVideoOrPicture();
            } else {
                isFolderShow = !isFolderShow;
                setFolderVisibility();
            }
        });
    }

    private void initData() {
        mMediaFolderViewModel.initFolder();
        mMediaFolderViewModel.getGalleryVideoSelect().observe(this, videoSelect -> {
            isVideoSelect = videoSelect;
            mTitleGallery.setText(
                videoSelect ? (isInitFolder ? getString(R.string.select_media_recent_projects) : mCurrentVideoFolder)
                    : mCurrentImageFolder);
            if (isInitFolder) {
                isInitFolder = false;
            }
            mCurrentFolders.clear();
            mCurrentFolders.addAll(videoSelect ? mVideoMediaFolders : mImageMediaFolders);
            mMediaFolderAdapter.notifyDataSetChanged();
        });

        mMediaFolderViewModel.getVideoMediaData().observe(this, mediaFolders -> {
            mVideoMediaFolders.addAll(mediaFolders);
            mCurrentFolders.clear();
            mCurrentFolders.addAll(mVideoMediaFolders);
            mMediaFolderAdapter.notifyDataSetChanged();
        });

        mMediaFolderViewModel.getImageMediaData().observe(this, mediaFolders -> {
            mImageMediaFolders.addAll(mediaFolders);
        });
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

    private void initEvent() {
        mCloseLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (isFolderShow) {
                isFolderShow = false;
                setFolderVisibility();
                return;
            }
            mMediaPickManager.clear();
            finish();
        }));

        if (mTitleGallery != null) {
            mTitleGallery.setOnClickListener(galleryListener);
        }
        if (mDrawImage != null) {
            mDrawImage.setOnClickListener(galleryListener);
        }

        if (mTitleMaterial != null) {
            mTitleMaterial.setOnClickListener(new OnClickRepeatedListener(v -> {
                if (mCurrentFragment != MATERIAL_FRAGMENT) {
                    selectChanged(MATERIAL_FRAGMENT);
                    resetVideoOrPicture();
                }
            }));
        }

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

        mQualityLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            isQualitySelect = !isQualitySelect;
            mQualityIcon.setSelected(isQualitySelect);
        }));

        mAddCardView.setOnClickListener(new OnClickRepeatedListener(v -> {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT, mSelectList);

            switch (mActionType) {
                case ACTION_ADD_MEDIA_TYPE:
                    goToVideoClipsActivity(intent);
                    break;
                case ACTION_APPEND_MEDIA_TYPE:
                    setBackResult(intent);
                    break;
                default:
                    break;
            }

        }));

        mSelectAdapter.setOnItemClickListener(new MediaPickSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemMove(int fromPosition, int toPosition) {
                // 拖动已选择素材改变顺序
                Collections.swap(mSelectList, fromPosition, toPosition);
                Collections.swap(mMediaPickManager.getSelectItemList(), fromPosition, toPosition);
                mSelectAdapter.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onFinish() {
                mMediaPickManager.updateSortedIndex();
            }
        });

        mMediaPickManager.addOnSelectItemChangeListener(item -> {
            int selectItemCount = mMediaPickManager.getSelectItemList().size();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChoiceContentLayout.setVisibility(selectItemCount > 0 ? View.VISIBLE : View.GONE);
                }
            }, 100);

            if (item.getIndex() > 0) {
                addMediaToSelectList(item);
            } else {
                removeMediaFromSelectList(item);
            }
            refreshTotalStatus();
            switch (mActionType) {
                case ACTION_ADD_MEDIA_TYPE:
                case ACTION_APPEND_MEDIA_TYPE:
                    mAddCardView.setText(selectItemCount > 0
                        ? (String.format(Locale.ROOT, getResources().getString(R.string.media_import), selectItemCount))
                        : getString(R.string.media_import_new));
                    break;
                default:
                    break;
            }
            mAddCardView.setEnabled(selectItemCount > 0);
            mAddCardView.setBackground(
                selectItemCount > 0 ? ContextCompat.getDrawable(mContext, R.drawable.background_card_selector)
                    : ContextCompat.getDrawable(mContext, R.drawable.background_card_add_normal));

            mQualityLayout.setClickable(selectItemCount > 0);

            mQualityText.setTextColor(selectItemCount > 0 ? ContextCompat.getColor(mContext, R.color.color_fff_90)
                : ContextCompat.getColor(mContext, R.color.color_fff_40));

            mQualityIcon.setBackground(
                selectItemCount > 0 ? ContextCompat.getDrawable(mContext, R.drawable.media_bg_preview_btn_selector)
                    : ContextCompat.getDrawable(mContext, R.drawable.index_checkbox_unused));

        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mMediaPickManager.clear();
                finish();
            }
        });
    }

    private void setBackResult(Intent intent) {
        if (!isQualitySelect) {
            isCompresses();
        } else {
            setResult(Constant.RESULT_CODE, intent);
            finish();
        }
    }

    private void goToVideoClipsActivity(Intent intent) {
        if (!isQualitySelect) {
            isCompresses();
        } else {
            intent.setClass(MediaPickActivity.this, VideoClipsActivity.class);
            intent.putExtra(CLIPS_VIEW_TYPE, VIEW_NORMAL);
            intent.putExtra(VideoClipsActivity.EXTRA_FROM_SELF_MODE, true);
            startActivity(intent);
            finish();
        }
    }

    private void addMediaToSelectList(MediaData item) {
        if (!mSelectList.contains(item) && item.getIndex() > mSelectList.size() - 1) {
            setAssetProperty(item);
            if (FileUtil.isVideo(item.getPath())) {
                int isNeedCompress =
                    HVEDownSamplingManager.needDownSampling(item.getPath(), item.getWidth(), item.getHeight());
                item.setDownSamplingState(isNeedCompress);
            } else {
                item.setDownSamplingState(NO_NEED_DOWN_SAMPLING);
            }
            mSelectList.add(item);
            mSelectAdapter.notifyItemInserted(mSelectList.size() - 1);
        } else {
            mSelectAdapter.notifyDataSetChanged();
        }
    }

    private void removeMediaFromSelectList(MediaData item) {
        if (item.isFromCloud()) {
            for (int i = 0; i < mSelectList.size(); i++) {
                if (!StringUtil.isEmpty(mSelectList.get(i).getPath())
                    && mSelectList.get(i).getPath().equals(item.getPath())) {
                    removeNewData(i);
                    break;
                }
            }
        } else {
            if (mSelectList != null && !mSelectList.isEmpty()) {
                int position = mSelectList.indexOf(item);
                if (position >= 0) {
                    removeNewData(position);
                }
            }
        }
    }

    public void removeNewData(int position) {
        if (position >= 0) {
            mSelectList.remove(position);
            mSelectAdapter.notifyItemRemoved(position);
            if (position != (mSelectList.size())) {
                mSelectAdapter.notifyItemRangeChanged(position, mSelectList.size() - position);
            }
        }
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

    private void refreshTotalStatus() {
        int videoSize = 0;
        int photoSize = 0;
        long duration = 0;
        if (mSelectList != null) {
            int len = mSelectList.size();
            for (MediaData item : mSelectList) {
                if (item.isVideo()) {
                    videoSize++;
                    duration = duration + (item.getDuration() - item.getCutTrimIn() - item.getCutTrimOut());
                }
            }
            photoSize = len - videoSize;
        }

        mTvTotalTime.setText(
            Utils.setNumColor(String.format(Locale.ROOT, getResources().getString(R.string.select_media_total_time),
                TimeUtils.makeTimeString(mContext, duration)), getResources().getColor(R.color.white)));

        mTvSelectVideoNum
            .setText(Utils.setNumColor(getResources().getQuantityString(R.plurals.add_video_num, videoSize, videoSize),
                getResources().getColor(R.color.white)));
        mTvSelectPictureNum
            .setText(Utils.setNumColor(getResources().getQuantityString(R.plurals.add_image_num, photoSize, photoSize),
                getResources().getColor(R.color.white)));
    }

    private void setAssetProperty(MediaData mediaData) {
        if (TextUtils.isEmpty(mediaData.getPath()) || !(new File(mediaData.getPath()).exists())) {
            return;
        }

        try {
            boolean isImageType = false;

            if (HVEUtil.isLegalImage(mediaData.getPath())) {
                BitmapFactory.Options options = BitmapDecodeUtils.getBitmapOptions(mediaData.getPath());
                if (options.outWidth != -1 && options.outHeight != -1) {
                    isImageType = true;
                }
            }

            if (!isImageType) {
                getVideoProperty(mediaData);
            } else {
                getImageProperty(mediaData);
            }
        } catch (EditorRuntimeException e) {
            SmartLog.e(TAG, "setAssetProperty: " + e.getMessage());
        }
    }

    private void getImageProperty(MediaData mediaData) {
        mediaData.setDuration(3000);
        mediaData.setType(MediaData.MEDIA_IMAGE);
    }

    private void getVideoProperty(MediaData mediaData) {
        HVEVisibleFormatBean bean = HVEUtil.getVideoProperty(mediaData.getPath());
        if (bean != null) {
            mediaData.setDuration(bean.getDuration());
            mediaData.setWidth(bean.getWidth());
            mediaData.setHeight(bean.getHeight());
            mediaData.setType(MediaData.MEDIA_VIDEO);
        }
    }

    private void resetVideoOrPicture() {
        mTitleGallery.setTextColor(mCurrentFragment == 0 ? ContextCompat.getColor(mContext, R.color.tab_text_tint_color)
            : ContextCompat.getColor(mContext, R.color.tab_text_default_color));
        mDrawImage.setSelected(mCurrentFragment == 0);
        mTitleMaterial
            .setTextColor(mCurrentFragment == 0 ? ContextCompat.getColor(mContext, R.color.tab_text_default_color)
                : ContextCompat.getColor(mContext, R.color.tab_text_tint_color));
        if (mCurrentFragment == 0) {
            mTitleGallery.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            mTitleMaterial.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        } else {
            mTitleGallery.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            mTitleMaterial.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }
    }

    private void selectChanged(int index) {
        if (index >= fragments.size()) {
            return;
        }
        Fragment fragment = fragments.get(index);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.isDestroyed()) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        getCurrentFragment().onPause();
        if (fragment.isAdded()) {
            fragment.onResume();
        } else {
            ft.add(R.id.fragment_main, fragment);
        }
        showTab(index);
        ft.commit();
    }

    private void showTab(int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.isDestroyed()) {
            return;
        }
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (index == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        mCurrentFragment = index;
    }

    private Fragment getCurrentFragment() {
        return fragments.get(mCurrentFragment);
    }

    public void addPipOrReplaceItem(MediaData item) {
        HVEVisibleFormatBean bean = HVEUtil.getVideoProperty(item.getPath());
        if (bean == null) {
            SmartLog.d(TAG, "addPipOrReplaceItem item is image");
            backToVideoClipActivity(item);
            return;
        }

        setAssetProperty(item);
        int isNeedCompress = HVEDownSamplingManager.needDownSampling(item.getPath(), bean.getWidth(), bean.getHeight());
        SmartLog.d(TAG, "addPipOrReplaceItem isNeedCompress " + isNeedCompress);

        if (isNeedCompress == NO_NEED_DOWN_SAMPLING) {
            SmartLog.d(TAG, "addPipOrReplaceItem NO_NEED_DOWN_SAMPLING");
            backToVideoClipActivity(item);
            return;
        }

        if (isNeedCompress == DOWN_SAMPLING_DONE || isNeedCompress == NEED_DOWN_SAMPLING) {
            completesList = new ArrayList<>();
            completesList.add(item);
            new MediaDataDownSampleManager(this).downSampleList(completesList,
                new MediaDataDownSampleManager.DownSampleCallBack() {
                    @Override
                    public void onSuccess(ArrayList<MediaData> list) {
                        backToVideoClipActivity(list.get(0));
                    }

                    @Override
                    public void onCancel() {
                        SmartLog.e(TAG, "DownloadSampling canceled");
                    }

                    @Override
                    public void onFailed(String message) {
                        SmartLog.e(TAG, "DownloadSampling failed.");
                    }
                });
        }
    }

    private void backToVideoClipActivity(MediaData item) {
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_SELECT_RESULT, item);
        setResult(Constant.RESULT_CODE, intent);
        finish();
    }

    private void isCompresses() {
        new MediaDataDownSampleManager(this).downSampleList(mSelectList,
            new MediaDataDownSampleManager.DownSampleCallBack() {
                @Override
                public void onSuccess(ArrayList<MediaData> list) {
                    compressesResult(list);
                }

                @Override
                public void onCancel() {
                    SmartLog.e(TAG, "DownloadSampling canceled");
                }

                @Override
                public void onFailed(String message) {
                    SmartLog.e(TAG, "DownloadSampling failed.");
                }
            });
    }

    private void compressesResult(ArrayList<MediaData> mDownSamplingList) {
        if (mDownSamplingList == null) {
            return;
        }
        switch (mActionType) {
            case ACTION_ADD_MEDIA_TYPE:
                if (mDownSamplingList.size() > 0) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT, mDownSamplingList);
                    intent.setClass(MediaPickActivity.this, VideoClipsActivity.class);
                    intent.putExtra(VideoClipsActivity.EXTRA_FROM_SELF_MODE, true);
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                Intent intent2 = new Intent();
                intent2.putParcelableArrayListExtra(Constant.EXTRA_SELECT_RESULT, mDownSamplingList);
                setResult(Constant.RESULT_CODE, intent2);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPickManager.clear();
        mMediaPickManager = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (TimeOutOnTouchListener listener : onTouchListeners) {
            if (listener != null) {
                listener.onTouch(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface TimeOutOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }

    private final ArrayList<TimeOutOnTouchListener> onTouchListeners = new ArrayList<TimeOutOnTouchListener>(10);

    public void registerMyOnTouchListener(TimeOutOnTouchListener onTouchListener) {
        onTouchListeners.add(onTouchListener);
    }

    public void unregisterMyOnTouchListener(TimeOutOnTouchListener onTouchListener) {
        onTouchListeners.remove(onTouchListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PREVIEW_CODE && resultCode == RESULT_OK) {
            MediaData mediaDataCropAfter = MediaPickManager.getInstance().getMediaData();
            if (mediaDataCropAfter.getIndex() == 0) {
                mMediaPickManager.addSelectItemAndSetIndex(mediaDataCropAfter);
            } else {
                mMediaPickManager.notifySelectItemChange(mediaDataCropAfter);
            }
        }

    }
}
