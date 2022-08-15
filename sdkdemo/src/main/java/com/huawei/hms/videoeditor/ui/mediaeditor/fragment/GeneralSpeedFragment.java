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

package com.huawei.hms.videoeditor.ui.mediaeditor.fragment;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEAudioAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVESpeedCurvePoint;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.adapter.comment.RViewHolder;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.SpeedBar;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.FilterLinearLayoutManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.DefaultPlayControlView;
import com.huawei.hms.videoeditor.ui.mediaeditor.speed.CurveSpeedItemAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.speed.CurveSpeedViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class GeneralSpeedFragment extends BaseFragment {
    private static final String TAG = "GeneralSpeedFragment";

    private TextView tv_title;

    private ImageView iv_certain;

    private SpeedBar speedBar;

    protected EditPreviewViewModel mEditPreviewViewModel;

    private CurveSpeedViewModel mFilterPanelViewModel;

    private HuaweiVideoEditor mEditor;

    private float speed = 1f;

    private EditorTextView generalSpeed;

    private EditorTextView curveSpeed;

    private View curveView;

    private View generalView;

    private RecyclerView mRecyclerView;

    private int currentSelectPosition = -1;

    private HVEAsset currentSelectedAsset;

    private List<HVEColumnInfo> mColumnList;

    private List<CloudMaterialBean> mCutContentList;

    private List<HVESpeedCurvePoint> currentSelectedCurveSpeedPoints;

    private CurveSpeedItemAdapter curveSpeedItemAdapter;

    private RelativeLayout mFilterCancelRl;

    private int mCurrentIndex = 0;

    private int mCurrentPage = 0;

    private Boolean mHasNextPage = false;

    private String currentSelectPath;

    private String currentSelectedName;

    private long startTimeDiff;

    private boolean isFirst;

    private CloudMaterialBean mMaterialsCutContent;

    public static GeneralSpeedFragment newInstance(int id) {
        GeneralSpeedFragment fragment = new GeneralSpeedFragment();
        return fragment;
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_generalspeed_style;
    }

    @Override
    protected void initView(View view) {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mFilterPanelViewModel = new ViewModelProvider(mActivity, mFactory).get(CurveSpeedViewModel.class);
        mEditor = mEditPreviewViewModel.getEditor();
        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.cut_second_menu_speed_change);
        iv_certain = view.findViewById(R.id.iv_certain);
        speedBar = view.findViewById(R.id.speedbar);
        if (ScreenUtil.isRTL()) {
            speedBar.setScaleX(RTL_UI);
        } else {
            speedBar.setScaleX(LTR_UI);
        }
        generalSpeed = view.findViewById(R.id.general_speed);
        curveSpeed = view.findViewById(R.id.curve_speed);
        curveView = view.findViewById(R.id.curve_view);
        generalView = view.findViewById(R.id.general_view);
        mRecyclerView = view.findViewById(R.id.rv_speed);
        View mCancelHeaderView =
            LayoutInflater.from(mActivity).inflate(R.layout.adapter_add_filter_header, null, false);
        mCancelHeaderView.setLayoutParams(
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SizeUtils.dp2Px(context, 75)));

        mFilterCancelRl = mCancelHeaderView.findViewById(R.id.rl_cancel);
        mFilterCancelRl.setSelected(true);
        mColumnList = new ArrayList<>();
        currentSelectedCurveSpeedPoints = new ArrayList<>();
        mCutContentList = new ArrayList<>();
        curveSpeedItemAdapter =
            new CurveSpeedItemAdapter(mActivity, mCutContentList, R.layout.adapter_add_curve_speed_item);
        mRecyclerView.setLayoutManager(new FilterLinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                    SizeUtils.dp2Px(mActivity, 75), SizeUtils.dp2Px(mActivity, 8)));
        }
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(curveSpeedItemAdapter);
        curveSpeedItemAdapter.addHeaderView(mCancelHeaderView);
        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
    }

    @Override
    protected void initObject() {
    }

    @Override
    protected void initData() {
        if (currentSelectedAsset == null) {
            return;
        }

        speed = getSDKSpeed();
        float mProgress = 10;
        if (speed <= 0) {
            mProgress = 0F;
        } else {
            mProgress = speed * 10;
        }
        speedBar.setProgress(mProgress);
        if (isCurveSpeed()) {
            showCurve();
        } else {
            showGeneral();
        }
        mFilterPanelViewModel.initColumns(HVEMaterialConstant.CUVER_SPEED_FATHER_COLUMN);
        mFilterPanelViewModel.getColumns().observe(getViewLifecycleOwner(), list -> {
            if (list != null && list.size() > 0) {
                mColumnList.clear();
                mColumnList.addAll(list);
                mCurrentIndex = 0;
                HVEColumnInfo content = mColumnList.get(mCurrentIndex);
                mCutContentList.clear();
                mFilterPanelViewModel.loadMaterials(content.getColumnId(), mCurrentPage);
            }
        });
        addCustomDataIntoList();
        if (mActivity instanceof VideoClipsActivity) {
            DefaultPlayControlView.setNeedSeek(false);
        }
    }

    private void showGeneral() {
        generalSpeed.setSelected(true);
        curveSpeed.setSelected(false);
        curveView.setVisibility(View.INVISIBLE);
        generalView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        speedBar.setVisibility(View.VISIBLE);
    }

    private void showCurve() {
        if (currentSelectedAsset == null) {
            return;
        }

        if (currentSelectedAsset.getType() != HVEAsset.HVEAssetType.AUDIO) {
            generalSpeed.setSelected(false);
            curveSpeed.setSelected(true);
            generalView.setVisibility(View.INVISIBLE);
            curveView.setVisibility(View.VISIBLE);
            speedBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initEvent() {
        mFilterPanelViewModel.getCurveErrorString().observe(this, errorString -> {
            if (!TextUtils.isEmpty(errorString) && mCutContentList.size() == 0) {
                if (mActivity != null) {
                    ToastWrapper.makeText(mActivity, mActivity.getString(R.string.result_illegal), Toast.LENGTH_SHORT)
                        .show();
                }
                curveSpeedItemAdapter.setSelectPosition(-1);
                mCutContentList.clear();
                curveSpeedItemAdapter.notifyDataSetChanged();
            }
        });

        mFilterPanelViewModel.getCurveEmptyString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (mActivity.getString(R.string.result_empty).equals(s)) {
                    SmartLog.i(TAG, "No data.");
                    curveSpeedItemAdapter.setSelectPosition(-1);
                    mCutContentList.clear();
                    addCustomDataIntoList();
                    curveSpeedItemAdapter.notifyDataSetChanged();
                }
            }
        });

        mFilterPanelViewModel.getPageData().observe(getViewLifecycleOwner(), list -> {
            if (mCurrentPage == 0) {
                mCutContentList.clear();
            }
            if (!mCutContentList.containsAll(list)) {
                SmartLog.i(TAG, "materialsCutContents is not exist.");
                CloudMaterialBean materialsCutContent = new CloudMaterialBean();
                materialsCutContent.setName(mActivity.getString(R.string.custom));
                materialsCutContent.setLocalDrawableId(R.drawable.icon_speed_custom);
                mCutContentList.add(materialsCutContent);
                mCutContentList.addAll(list);
                if (!TextUtils.isEmpty(currentSelectedName)) {
                    for (int i = 0; i < mCutContentList.size(); i++) {
                        if (currentSelectedName.equals(mCutContentList.get(i).getName())) {
                            currentSelectPosition = i + 1;
                            curveSpeedItemAdapter.setSelectPosition(currentSelectPosition);
                            mFilterCancelRl.setSelected(false);
                        }
                    }
                }
                curveSpeedItemAdapter.notifyDataSetChanged();
            } else {
                SmartLog.i(TAG, "materialsCutContents is exist.");
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isSlidingToLeft = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mHasNextPage && manager != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount();
                    if (lastItemPosition == (itemCount - 1) && isSlidingToLeft) {
                        if (mCurrentIndex > 0) {
                            mCurrentPage++;
                            mFilterPanelViewModel.loadMaterials(mCutContentList.get(mCurrentIndex).getId(),
                                mCurrentPage);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLeft = dx > 0;

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null) {
                    int visibleItemCount = manager.getChildCount();
                    int firstPosition = manager.findFirstVisibleItemPosition();
                    if (firstPosition != -1 && visibleItemCount > 0 && mCutContentList != null
                        && mCutContentList.size() > 0 && !isFirst) {
                        int num = visibleItemCount;
                        isFirst = true;
                        if (visibleItemCount > mCutContentList.size()) {
                            num = mCutContentList.size();
                        }
                        for (int i = 1; i < num - 1; i++) {
                            CloudMaterialBean cutContent = mCutContentList.get(i);
                            curveSpeedItemAdapter.addFirstScreenMaterial(cutContent);
                        }
                    }
                }
            }
        });

        curveSpeedItemAdapter.setOnItemClickListener(new CurveSpeedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int dataPosition) {
                mFilterCancelRl.setSelected(false);
                int mSelectPosition = curveSpeedItemAdapter.getSelectPosition();
                if (mSelectPosition != position) {
                    curveSpeedItemAdapter.setSelectPosition(position);
                    if (mSelectPosition != -1) {
                        curveSpeedItemAdapter.notifyItemChanged(mSelectPosition);
                    }
                    curveSpeedItemAdapter.notifyItemChanged(position);
                    if (position == 1) {
                        initCustomData();
                        adjustVideoCurveSpeed(currentSelectedName, currentSelectedCurveSpeedPoints);
                    } else {
                        mMaterialsCutContent = mCutContentList.get(dataPosition);
                        if (mMaterialsCutContent == null) {
                            return;
                        }
                        currentSelectPath = mMaterialsCutContent.getLocalPath();
                        if (TextUtils.isEmpty(currentSelectPath)) {
                            return;
                        }
                        currentSelectedName = mMaterialsCutContent.getName();
                        adjustVideoCurveSpeed(currentSelectedName, currentSelectPath);
                    }
                }
            }

            @Override
            public void onDownloadClick(int position, int dataPosition) {
                if (dataPosition >= mCutContentList.size()) {
                    return;
                }

                int previousPosition = curveSpeedItemAdapter.getSelectPosition();
                curveSpeedItemAdapter.setSelectPosition(position);
                CloudMaterialBean content = mCutContentList.get(dataPosition);

                curveSpeedItemAdapter.addDownloadMaterial(content);
                mFilterPanelViewModel.downloadColumn(previousPosition, position, dataPosition, content);
            }

            @Override
            public void onItemEditClick(int position, int dataPosition) {
                if (currentSelectedAsset == null) {
                    return;
                }
                if (currentSelectedAsset instanceof HVEImageAsset || currentSelectedAsset instanceof HVEAudioAsset) {
                    return;
                }
                String curveName = ((HVEVideoAsset) currentSelectedAsset).getCurveName();
                if (!TextUtils.isEmpty(curveName)) {
                    currentSelectedName = curveName;
                    currentSelectedCurveSpeedPoints = ((HVEVideoAsset) currentSelectedAsset).getCurvePoints();
                    if (position == 1) {
                        showBottomDialog(position, currentSelectedAsset, currentSelectedCurveSpeedPoints,
                            currentSelectedCurveSpeedPoints, currentSelectedName);
                    }
                }
            }
        });

        mFilterCancelRl.setOnClickListener(v -> {
            mFilterCancelRl.setContentDescription(mActivity.getString(R.string.no_filter));
            mFilterCancelRl.setSelected(true);
            if (mFilterCancelRl.isSelected()) {
                int mSelectPosition = curveSpeedItemAdapter.getSelectPosition();
                curveSpeedItemAdapter.setSelectPosition(-1);
                if (mSelectPosition != -1) {
                    curveSpeedItemAdapter.notifyItemChanged(mSelectPosition);
                }
                currentSelectedName = null;
                currentSelectedCurveSpeedPoints.clear();
                adjustVideoCurveSpeed(null, (String) null);
            }
        });

        mFilterPanelViewModel.getDownloadSuccess().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "getDownloadSuccess");
            curveSpeedItemAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            int downloadPosition = downloadInfo.getPosition();
            if (downloadPosition >= 0 && (downloadInfo.getDataPosition() < mCutContentList.size()
                && downloadInfo.getContentId().equals(mCutContentList.get(downloadInfo.getDataPosition()).getId()))) {
                mFilterCancelRl.setSelected(false);
                curveSpeedItemAdapter.setSelectPosition(downloadPosition);
                CloudMaterialBean materialsCutContent = downloadInfo.getMaterialBean();
                mCutContentList.set(downloadInfo.getDataPosition(), materialsCutContent);
                curveSpeedItemAdapter.notifyDataSetChanged();

                if (downloadPosition == curveSpeedItemAdapter.getSelectPosition()) {
                    currentSelectPath = materialsCutContent.getLocalPath();
                    currentSelectedName = materialsCutContent.getName();
                    if (TextUtils.isEmpty(currentSelectPath)) {
                        return;
                    }
                    adjustVideoCurveSpeed(currentSelectedName, currentSelectPath);
                }
            }

        });

        mFilterPanelViewModel.getDownloadProgress().observe(this, downloadInfo -> {
            SmartLog.d(TAG, "progress:" + downloadInfo.getProgress());
            updateProgress(downloadInfo);
        });

        mFilterPanelViewModel.getDownloadFail().observe(this, downloadInfo -> {
            curveSpeedItemAdapter.removeDownloadMaterial(downloadInfo.getContentId());
            cancelProgress(downloadInfo);
            ToastWrapper.makeText(mActivity, mActivity.getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
        });

        mFilterPanelViewModel.getCurveBoundaryPageData().observe(this, aBoolean -> {
            mHasNextPage = aBoolean;
        });

        generalSpeed.setOnClickListener(new OnClickRepeatedListener(v -> {
            showGeneral();
        }));

        curveSpeed.setOnClickListener(new OnClickRepeatedListener(v -> {
            showCurve();
        }));

        speedBar.setOnProgressChangedListener(progress -> {
            if (progress <= 0) {
                speed = 0.5F;
            } else {
                speed = progress / 10F;
            }
            mEditPreviewViewModel.setToastTime(speed + "");
        });

        speedBar.setaTouchListener(isTouch -> {
            if (!isTouch) {
                adjustVideoSpeed(speed);
            }
            mEditPreviewViewModel.setToastTime(isTouch ? speed + "" : "");
        });

        iv_certain.setOnClickListener(new OnClickRepeatedListener(v -> {
            mActivity.onBackPressed();
        }));

    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_210;
    }

    public void showBottomDialog(int position, HVEAsset selectedAsset, List<HVESpeedCurvePoint> temp,
        List<HVESpeedCurvePoint> resetList, String curveName) {
        ((VideoClipsActivity) mActivity).showCusterSpeedFragment(position, selectedAsset, temp, resetList, curveName);
    }

    public static String format(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.toString();
    }

    private void adjustVideoSpeed(float mProgress) {
        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (currentSelectedAsset == null || currentSelectedAsset.getType() != HVEAsset.HVEAssetType.VIDEO) {
            return;
        }

        HVETimeLine timeLine = mEditPreviewViewModel.getTimeLine();
        if (timeLine == null) {
            return;
        }

        List<HVEVideoLane> videoLanes = mEditPreviewViewModel.getTimeLine().getAllVideoLane();
        if (videoLanes.isEmpty()) {
            return;
        }
        HVEVideoLane videoLane = videoLanes.get(currentSelectedAsset.getLaneIndex());
        if (videoLane == null) {
            return;
        }
        boolean isSuccess = videoLane.changeAssetSpeed(currentSelectedAsset.getIndex(), mProgress);
        if (isSuccess) {
            mEditPreviewViewModel.reloadMainLane();
            long currentTime = timeLine.getCurrentTime();
            if (currentTime == currentSelectedAsset.getStartTime()) {
                mEditor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
            } else {
                mEditor.seekTimeLine(currentSelectedAsset.getStartTime(), () -> {
                    mEditor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
                });
            }
        } else {
            ToastWrapper.makeText(mActivity, mActivity.getString(R.string.set_seeped_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void adjustVideoCurveSpeed(String mSelectName, List<HVESpeedCurvePoint> speedPoints) {
        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (currentSelectedAsset == null || currentSelectedAsset.getType() != HVEAsset.HVEAssetType.VIDEO) {
            return;
        }
        HVETimeLine timeLine = mEditPreviewViewModel.getTimeLine();
        if (timeLine == null) {
            return;
        }

        List<HVEVideoLane> videoLanes = timeLine.getAllVideoLane();
        if (videoLanes.isEmpty()) {
            return;
        }
        HVEVideoLane videoLane = videoLanes.get(currentSelectedAsset.getLaneIndex());
        if (videoLane == null) {
            return;
        }
        boolean isSuccess = videoLane.addCurveSpeed(currentSelectedAsset.getIndex(), mSelectName, speedPoints);
        if (isSuccess) {
            mEditPreviewViewModel.reloadMainLane();
            long currentTime = timeLine.getCurrentTime();
            if (currentTime == currentSelectedAsset.getStartTime()) {
                mEditor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
            } else {
                mEditor.seekTimeLine(currentSelectedAsset.getStartTime(), () -> {
                    mEditor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
                });
            }
        } else {
            ToastWrapper.makeText(mActivity, mActivity.getString(R.string.set_seeped_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void adjustVideoCurveSpeed(String mSelectName, String path) {
        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (currentSelectedAsset == null || currentSelectedAsset.getType() != HVEAsset.HVEAssetType.VIDEO) {
            return;
        }
        HVETimeLine timeLine = mEditPreviewViewModel.getTimeLine();
        if (timeLine == null) {
            return;
        }

        List<HVEVideoLane> videoLanes = timeLine.getAllVideoLane();
        if (videoLanes.isEmpty()) {
            return;
        }
        HVEVideoLane videoLane = videoLanes.get(currentSelectedAsset.getLaneIndex());
        if (videoLane == null) {
            return;
        }
        boolean isSuccess;
        if (mSelectName == null && path == null) {
            isSuccess = videoLane.removeCurveSpeed(currentSelectedAsset.getIndex());
        } else {
            isSuccess = videoLane.addCurveSpeed(currentSelectedAsset.getIndex(), mSelectName, path);
        }

        if (isSuccess) {
            mEditPreviewViewModel.reloadMainLane();
            long currentTime = timeLine.getCurrentTime();
            if (currentTime == currentSelectedAsset.getStartTime()) {
                mEditor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
            } else {
                mEditor.seekTimeLine(currentSelectedAsset.getStartTime(), () -> {
                    mEditor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
                });
            }

            currentSelectedCurveSpeedPoints.clear();
            currentSelectedCurveSpeedPoints.addAll(((HVEVideoAsset) currentSelectedAsset).getCurvePoints());

        } else {
            ToastWrapper.makeText(mActivity, mActivity.getString(R.string.set_seeped_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private float getSDKSpeed() {
        if (currentSelectedAsset instanceof HVEVideoAsset) {
            return currentSelectedAsset.getSpeed();
        }
        return 1;
    }

    @Override
    public void onBackPressed() {
        if (!(mActivity instanceof VideoClipsActivity)) {
            return;
        }

        if (mEditPreviewViewModel == null) {
            return;
        }

        DefaultPlayControlView.setNeedSeek(true);
        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        DefaultPlayControlView.setNeedSeek(true);
        super.onDestroy();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initTimeoutState();
            return false;
        }
    };

    private void updateProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < mCutContentList.size()
            && downloadInfo.getContentId().equals(mCutContentList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.item_progress_curve_speed);
                mDownloadPb.setProgress(downloadInfo.getProgress());
            }
        }
    }

    private void cancelProgress(MaterialsDownloadInfo downloadInfo) {
        int downloadPosition = downloadInfo.getPosition();
        if (downloadPosition >= 0 && downloadInfo.getDataPosition() < mCutContentList.size()
            && downloadInfo.getContentId().equals(mCutContentList.get(downloadInfo.getDataPosition()).getId())) {
            RViewHolder viewHolder =
                (RViewHolder) mRecyclerView.findViewHolderForAdapterPosition(downloadInfo.getPosition());
            if (viewHolder != null) {
                ProgressBar mDownloadPb = viewHolder.itemView.findViewById(R.id.item_progress_curve_speed);
                mDownloadPb.setVisibility(View.GONE);
                ImageView mDownloadIv = viewHolder.itemView.findViewById(R.id.item_download_view_curve_speed);
                mDownloadIv.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isCurveSpeed() {
        if (!(currentSelectedAsset instanceof HVEVideoAsset)) {
            return false;
        }

        String curveName = ((HVEVideoAsset) currentSelectedAsset).getCurveName();
        if (!TextUtils.isEmpty(curveName)) {
            currentSelectedName = curveName;
            currentSelectedCurveSpeedPoints = ((HVEVideoAsset) currentSelectedAsset).getCurvePoints();
            return true;
        } else {
            currentSelectedName = null;
            currentSelectedCurveSpeedPoints.clear();
            mFilterCancelRl.setSelected(true);
            return false;
        }
    }

    private void initCustomData() {
        currentSelectedName = mActivity.getString(R.string.custom);
        currentSelectedCurveSpeedPoints.clear();
        currentSelectedCurveSpeedPoints.add(new HVESpeedCurvePoint(0f, 1f));
        currentSelectedCurveSpeedPoints.add(new HVESpeedCurvePoint(0.25f, 1f));
        currentSelectedCurveSpeedPoints.add(new HVESpeedCurvePoint(0.5f, 1f));
        currentSelectedCurveSpeedPoints.add(new HVESpeedCurvePoint(0.75f, 1f));
        currentSelectedCurveSpeedPoints.add(new HVESpeedCurvePoint(1f, 1f));
    }

    private void addCustomDataIntoList() {
        CloudMaterialBean materialsCutContent = new CloudMaterialBean();
        materialsCutContent.setName(mActivity.getString(R.string.custom));
        materialsCutContent.setLocalDrawableId(R.drawable.icon_speed_custom);
        mCutContentList.add(materialsCutContent);
        if (mActivity.getString(R.string.custom).equals(currentSelectedName)) {
            currentSelectPosition = 1;
            curveSpeedItemAdapter.setSelectPosition(currentSelectPosition);
            mFilterCancelRl.setSelected(false);
        } else {
            currentSelectPosition = -1;
            curveSpeedItemAdapter.setSelectPosition(currentSelectPosition);
            mFilterCancelRl.setSelected(true);
        }
    }
}
