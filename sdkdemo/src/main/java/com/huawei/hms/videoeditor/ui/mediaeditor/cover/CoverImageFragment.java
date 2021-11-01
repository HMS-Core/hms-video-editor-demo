
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

package com.huawei.hms.videoeditor.ui.mediaeditor.cover;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;
import static com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverImageViewModel.SOURCE_COVER_IMAGE_NAME_SUFFIX;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huawei.hms.videoeditor.common.agc.HVEApplication;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BigDecimalUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.secure.android.common.intent.SafeBundle;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 添加封面
 *
 * @author xwx882936
 * @since 2020/12/22
 */
public class CoverImageFragment extends BaseFragment {

    private static final String TAG = "CoverImageFragment";

    public static final String SET_COVER_PATH_TYPE_SPLIT_TAG = "##"; // 设置封面成功后分割路径和封面类型的tag

    public static final String IMAGE_COVER_PATH_TYPE_SPLIT_TAG = "&&"; // 相册导入分割路径的tag

    private LinearLayout mResetLayout;

    private ImageView mSaveLayout;

    private TextView mVideoTv;

    private View mVideoIndicator;

    private TextView mPictureTv;

    private View mPictureIndicator;

    private TextView mNoCoverTv;

    private View mNoCoverIndicator;

    private TextView mDescTv;

    private RelativeLayout mTrackLayout;

    private RecyclerView mRecyclerView;

    private RelativeLayout mCoverLayout;

    private ImageView mCoverImage;

    private ImageView mNoCoverImage;

    private LinearLayout mAddTextLayout;

    private CoverAdapter mCoverAdapter;

    private EditPreviewViewModel mEditPreviewViewModel;

    private CoverImageViewModel mCoverImageViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private boolean isInitVideoSelect;

    private long mInitVideoCoverTime;

    private boolean isInitNoCover;

    private String mInitSelectPicPath;

    private String mSelectPicPath;

    private String mSelectPicForImagePath;

    private String mInitSourcePicForImagePath; // 相册导入模式下 初始状态 源图片地址

    private String mSourcePicForImagePath; // 相册导入模式下 源图片地址

    private boolean isVideoSelect;

    private long mVideoCoverTime;

    private long mDurationTime;

    private int mRvScrollX = 0;

    private int mItemWidth;

    private boolean isNoCover = false;

    private boolean isSaveState = false;

    // 主轨道视频帧
    private List<HVEAsset> mAssetList;

    private List<HVEAsset> mCoverAssetList;

    private boolean isHasInit = false;

    private ImageView ivClose;

    private float mWidth = 0;

    private float mHeight = 0;

    public static CoverImageFragment newInstance(String projectId, boolean isNoCover, boolean videoSelect, String path,
        long initTime, long durationTime) {
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putBoolean("isNoCover", isNoCover);
        args.putBoolean("videoSelect", videoSelect);
        args.putString("initPath", path);
        args.putLong("initTime", initTime);
        args.putLong("durationTime", durationTime);
        CoverImageFragment fragment = new CoverImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_cover_image;
    }

    @Override
    protected void initView(View view) {
        mResetLayout = view.findViewById(R.id.layout_reset);
        ivClose = view.findViewById(R.id.iv_close);
        mSaveLayout = view.findViewById(R.id.iv_certain);
        mVideoTv = view.findViewById(R.id.tv_video);
        mVideoIndicator = view.findViewById(R.id.video_indicator);
        mPictureTv = view.findViewById(R.id.tv_picture);
        mPictureIndicator = view.findViewById(R.id.picture_indicator);
        mNoCoverTv = view.findViewById(R.id.tv_no_cover);
        mNoCoverIndicator = view.findViewById(R.id.no_cover_indicator);
        mDescTv = view.findViewById(R.id.tv_desc);
        mTrackLayout = view.findViewById(R.id.video_truck);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mCoverLayout = view.findViewById(R.id.picture_edit_layout);
        mCoverImage = view.findViewById(R.id.iv_media);
        mNoCoverImage = view.findViewById(R.id.iv_no_cover);
        mAddTextLayout = view.findViewById(R.id.add_text_layout);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.set_cover_title);
        ivClose.setVisibility(View.GONE);
    }

    @Override
    protected void initObject() {
        mAssetList = new ArrayList<>();
        mCoverAssetList = new ArrayList<>();
        mItemWidth = SizeUtils.dp2Px(mActivity, 64);
        SafeBundle safeBundle = new SafeBundle(getArguments());
        isInitVideoSelect = safeBundle.getBoolean("videoSelect");
        mInitSelectPicPath = safeBundle.getString("initPath");
        mInitVideoCoverTime = safeBundle.getLong("initTime");
        mDurationTime = safeBundle.getLong("durationTime");
        isInitNoCover = safeBundle.getBoolean("isNoCover");
        if (mVideoCoverTime > mDurationTime) {
            mInitVideoCoverTime = mDurationTime;
        }
        isNoCover = isInitNoCover;
        isVideoSelect = isInitVideoSelect;
        if (!isInitNoCover && !isInitVideoSelect) {
            mSelectPicForImagePath = mInitSelectPicPath;
        }
        mVideoCoverTime = mInitVideoCoverTime;
        mEditPreviewViewModel = new ViewModelProvider(mActivity).get(EditPreviewViewModel.class);
        mCoverImageViewModel = new ViewModelProvider(mActivity).get(CoverImageViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity).get(MaterialEditViewModel.class);

        resetView();
        mCoverAdapter = new CoverAdapter(context, mAssetList, R.layout.adapter_cover_item2);
        if (ScreenUtil.isRTL()) {
            mRecyclerView.setScaleX(RTL_UI);
        } else {
            mRecyclerView.setScaleX(LTR_UI);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        mRecyclerView.setAdapter(mCoverAdapter);
        mRecyclerView.setItemAnimator(null);
        View headerView = new View(context);
        View footView = new View(context);
        headerView.setLayoutParams(
            new ViewGroup.LayoutParams(SizeUtils.screenWidth(context) / 2, SizeUtils.dp2Px(context, 64)));
        footView.setLayoutParams(
            new ViewGroup.LayoutParams(SizeUtils.screenWidth(context) / 2, SizeUtils.dp2Px(context, 64)));
        mCoverAdapter.addHeaderView(headerView);
        mCoverAdapter.addFooterView(footView);
    }

    /**
     * 相册导入模式获取封面源图
     */
    private void getSourcePicPath() {
        if (EditorManager.getInstance().getEditor() != null
            && !StringUtil.isEmpty(EditorManager.getInstance().getEditor().getProjectId())) {
            mInitSourcePicForImagePath = "";
            mSourcePicForImagePath = "";
            String sdPath =
                mActivity.getFilesDir().getAbsolutePath() + File.separator + HVEApplication.getInstance().getTag()
                    + "project/" + EditorManager.getInstance().getEditor().getProjectId();

            File[] files = new File(sdPath).listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.getName().contains(SOURCE_COVER_IMAGE_NAME_SUFFIX)) {
                    try {
                        mInitSourcePicForImagePath = file.getCanonicalPath();
                        mSourcePicForImagePath = file.getCanonicalPath();
                        SmartLog.d(TAG, "getSourcePicPath:" + mSourcePicForImagePath);
                    } catch (IOException e) {
                        SmartLog.e(TAG, "mSourcePicForImagePath is unValid");
                    }
                }
            }
        }
    }

    @Override
    protected void initData() {
        if (mEditPreviewViewModel == null) {
            return;
        }
        mEditPreviewViewModel.getImageItemList().observe(this, this::getBitmapList);

        mCoverImageViewModel.getCoverImageData().observe(this, imagePath -> {
            if (isHasInit && !StringUtil.isEmpty(imagePath)) {
                String[] split = imagePath.split(IMAGE_COVER_PATH_TYPE_SPLIT_TAG);
                if (split.length == 2) {
                    mSelectPicPath = split[0];
                    isVideoSelect = false;
                    mSelectPicForImagePath = mSelectPicPath;
                    mSourcePicForImagePath = mSelectPicPath;
                    Glide.with(mActivity)
                        .load(mSourcePicForImagePath)
                        .apply(new RequestOptions().transform(new MultiTransformation<>(new CenterCrop(),
                            new RoundedCorners(SizeUtils.dp2Px(mActivity, 8)))))
                        .into(mCoverImage);
                    isNoCover = false;
                    resetView();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        isHasInit = true;
    }

    @Override
    protected void initEvent() {
        mSaveLayout.setOnClickListener(v -> {
            isSaveState = true;
            HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
            HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
            if (editor == null || timeLine == null) {
                return;
            }

            EditorManager.getInstance()
                .getEditor()
                .getBitmapAtSelectedTime(isVideoSelect ? mVideoCoverTime : 0, new HuaweiVideoEditor.ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, long time) {
                        mCoverImageViewModel.removeSourcePicture(mInitSelectPicPath);
                        if (!StringUtil.isEmpty(mSourcePicForImagePath)
                            && !mSourcePicForImagePath.equals(mInitSourcePicForImagePath)) {
                            mCoverImageViewModel.removeSourcePicture(mInitSourcePicForImagePath);
                        }
                        HuaweiVideoEditor editor1 = EditorManager.getInstance().getEditor();
                        if (editor1 == null) {
                            return;
                        }

                        mCoverImageViewModel.setBitmapCover(editor1.getProjectId(), bitmap, isVideoSelect ? time : -2);
                    }

                    @Override
                    public void onFail(int errorCode) {
                    }
                });
            mEditPreviewViewModel.setSelectedUUID("");
        });

        mNoCoverTv.setOnClickListener(v -> {
            isNoCover = true;
            resetView();
            mMaterialEditViewModel.clearMaterialEditData();
        });

        mPictureTv.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (isNoCover || isVideoSelect) {
                if (!StringUtil.isEmpty(mSelectPicForImagePath)) {
                    isVideoSelect = false;
                    isNoCover = false;
                    resetView();
                } else {
                    chosePicture();
                }
            }
        }));

        // 相册导入编辑图片
        mCoverLayout.setOnClickListener(v -> chosePicture());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mRvScrollX += dx;
                updateTimeLine();
            }
        });

        ivClose.setOnClickListener(new OnClickRepeatedListener(v -> mActivity.onBackPressed()));
    }

    private void chosePicture() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor != null) {
            calculateSize();
            Intent intent = new Intent(mActivity, CoverImageActivity.class);
            intent.putExtra(CoverImageActivity.PROJECT_ID, editor.getProjectId());
            intent.putExtra(CoverImageActivity.WIDTH, mWidth);
            intent.putExtra(CoverImageActivity.HEIGHT, mHeight);
            mActivity.startActivityForResult(intent, VideoClipsActivity.ACTION_ADD_COVER_REQUEST_CODE);
        }
    }

    @Override
    protected int setViewLayoutEvent() {
        return USUALLY_HEIGHT;
    }

    private void getBitmapList(List<HVEAsset> list) {
        if (mAssetList != null) {
            mAssetList.clear();
            mAssetList.addAll(list);
        }
        if (EditorManager.getInstance().getEditor() == null
            || EditorManager.getInstance().getEditor().getTimeLine() == null) {
            return;
        }
        mCoverAdapter.notifyDataSetChanged();
        if (mRecyclerView == null) {
            return;
        }

        new Handler()
            .postDelayed(
                () -> mRecyclerView
                    .scrollBy(
                        (int) BigDecimalUtils
                            .round(BigDecimalUtils.mul(BigDecimalUtils.div(mInitVideoCoverTime, 1000), mItemWidth), 0),
                        0),
                30);
    }

    private void calculateSize() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor != null) {
            mWidth = editor.getCanvasWidth();
            mHeight = editor.getCanvasHeight();
        }
    }

    private void notifyCurrentTimeChange(long time) {
        if (mRecyclerView != null) {
            for (int j = 0; j < mRecyclerView.getChildCount(); j++) {
                if (mRecyclerView.getChildAt(j) instanceof CoverTrackView) {
                    ((CoverTrackView) mRecyclerView.getChildAt(j)).handleCurrentTimeChange(time);
                }
            }
        }
    }

    private void updateTimeLine() {
        if (mActivity != null) {
            float totalWidth = mDurationTime / 1000f * mItemWidth;
            float percent = mRvScrollX / totalWidth;
            mVideoCoverTime = (long) (percent * mDurationTime);
            if (mVideoCoverTime > mDurationTime) {
                mVideoCoverTime = mDurationTime;
            }
            notifyCurrentTimeChange(mVideoCoverTime);
            if (mEditPreviewViewModel == null) {
                return;
            }
            if (!((VideoClipsActivity) mActivity).isVideoPlaying) {
                ((VideoClipsActivity) mActivity).seekTimeLine(mVideoCoverTime);
            }
        }
    }

    private void resetView() {
        if (mEditPreviewViewModel == null) {
            return;
        }
        if (mActivity == null || !isAdded()) {
            return;
        }
        mEditPreviewViewModel.updateTimeLine();
        if (isNoCover) {
            mVideoTv.setTextColor(getResources().getColor(R.color.white));
            mPictureTv.setTextColor(getResources().getColor(R.color.white));
            mNoCoverTv.setTextColor(getResources().getColor(R.color.tab_text_tint_color));
            mNoCoverIndicator.setVisibility(View.VISIBLE);
            mVideoIndicator.setVisibility(View.INVISIBLE);
            mPictureIndicator.setVisibility(View.INVISIBLE);

            mResetLayout.setVisibility(View.INVISIBLE);
            mAddTextLayout.setVisibility(View.INVISIBLE);

            mDescTv.setVisibility(View.VISIBLE);
            mDescTv.setText(R.string.no_cover_text);
            mNoCoverImage.setVisibility(View.VISIBLE);
            mCoverLayout.setVisibility(View.GONE);
            mTrackLayout.setVisibility(View.GONE);
        } else {
            mNoCoverIndicator.setVisibility(View.INVISIBLE);
            mNoCoverImage.setVisibility(View.GONE);

            mVideoIndicator.setVisibility(isVideoSelect ? View.VISIBLE : View.INVISIBLE);
            mPictureIndicator.setVisibility(isVideoSelect ? View.INVISIBLE : View.VISIBLE);

            mPictureTv
                .setTextColor(getResources().getColor(isVideoSelect ? R.color.white : R.color.tab_text_tint_color));
            mVideoTv.setTextColor(getResources().getColor(isVideoSelect ? R.color.tab_text_tint_color : R.color.white));
            mNoCoverTv.setTextColor(getResources().getColor(R.color.white));
            mDescTv.setText(getString(R.string.left_right_move_select));

            mDescTv.setVisibility(isVideoSelect ? View.VISIBLE : View.INVISIBLE);
            mCoverLayout.setVisibility(isVideoSelect ? View.GONE : View.VISIBLE);
            mTrackLayout.setVisibility(isVideoSelect ? View.VISIBLE : View.GONE);

            mResetLayout.setVisibility(View.VISIBLE);
            mAddTextLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDynamicViewLayoutChange();
    }
}
