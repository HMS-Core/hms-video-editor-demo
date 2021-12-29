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

package com.huawei.hms.videoeditor.ui.mediaeditor.speed;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVESpeedCurvePoint;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

public class CustomCurveSpeedFragment extends BaseFragment {
    protected ViewModelProvider.AndroidViewModelFactory factory;

    private ImageView iv_cancel;

    private ImageView iv_certain;

    private TextView iv_reset;

    private CustomCurveSpeedView curveSpeedView;

    private EditPreviewViewModel mEditPreviewViewModel;

    private HuaweiVideoEditor mEditor;

    private List<HVESpeedCurvePoint> currentList;

    private List<HVESpeedCurvePoint> resetList;

    private String curveName;

    private ConstraintLayout addPointLayout;

    private ConstraintLayout.LayoutParams resetParams;

    private ConstraintLayout.LayoutParams timeSpeedParams;

    private LinearLayout timeSpeedLayout;

    private HVEAsset asset;

    private AddType addType = AddType.NO;

    private float timeFactor = 0;

    private TextView oldTime;

    private TextView newTime;

    private ImageView addIcon;

    private TextView addText;

    private int pointIndex = 0;

    private int position;

    public static CustomCurveSpeedFragment newInstance(int position, HVEAsset selectedAsset,
        List<HVESpeedCurvePoint> temp, List<HVESpeedCurvePoint> resetList, String curveName) {
        CustomCurveSpeedFragment fragment = new CustomCurveSpeedFragment();
        fragment.setSelectedPosition(position, selectedAsset, temp, resetList, curveName);
        return fragment;
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.curve_speed_layout;
    }

    protected void initView(View view) {
        factory = new ViewModelProvider.AndroidViewModelFactory(mActivity.getApplication());
        mEditPreviewViewModel = new ViewModelProvider(mActivity, factory).get(EditPreviewViewModel.class);
        mEditor = mEditPreviewViewModel.getEditor();
        oldTime = view.findViewById(R.id.time);
        newTime = view.findViewById(R.id.change_time);
        addIcon = view.findViewById(R.id.add_point);
        addText = view.findViewById(R.id.point_text);
        curveSpeedView = view.findViewById(R.id.curveSpeedView);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        iv_cancel = view.findViewById(R.id.iv_close);
        iv_cancel.setVisibility(View.VISIBLE);
        iv_certain = view.findViewById(R.id.iv_certain);
        iv_reset = view.findViewById(R.id.reset_curve_speed);
        timeSpeedLayout = view.findViewById(R.id.time_speed_layout);
        addPointLayout = view.findViewById(R.id.add_point_layout);
        tvTitle.setText(getString(R.string.custom));
        resetParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT);
        resetParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        timeSpeedParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT);
        timeSpeedParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        refreshTime();
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initObject() {
        setTimeoutEnable();
        curveSpeedView.init(asset, currentList);
        curveSpeedView.setCustomCurveCallBack(new CustomCurveSpeedView.CustomCurveCallBack() {
            @Override
            public void lineChanged(float time) {
                timeFactor = time;
            }

            @Override
            public void pointChanged(int index, float time, float speed) {
                SmartLog.i("xxxxxxxx", "time: " + time + "  speed: " + speed);
                if (currentList.size() > index) {
                    currentList.set(index, new HVESpeedCurvePoint(time, speed));
                }
                mEditPreviewViewModel.updateCurvePointMap(position, currentList);
            }

            @Override
            public void isOnPoint(int is) {
                if (pointIndex != is) {
                    pointIndex = is;
                    checkAddOrDelete();
                }
            }

            @Override
            public void touchUp() {
                adjustVideoCurveSpeed();
                curveSpeedView.refresh(currentList);
            }
        });

        curveSpeedView.setOnTouchListener((view, event) -> {
            if (view.getId() == R.id.curveSpeedView) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });
        mEditPreviewViewModel.setAddCurveSpeedStatus(true);
    }

    protected void initData() {
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchSTListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchSTListener);
        }
        mEditPreviewViewModel.destroyTimeoutManager();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchSTListener);
        }
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.destroyTimeoutManager();
        }
    }

    protected void initEvent() {
        mEditPreviewViewModel.getTimeout()
            .observe(getViewLifecycleOwner(), isTimeout -> {
                if (isTimeout && !isBackground) {
                    mActivity.onBackPressed();
                }
            });
        iv_certain.setOnClickListener(new OnClickRepeatedListener(v -> {
            HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
            mActivity.onBackPressed();
        }));

        iv_cancel.setOnClickListener(new OnClickRepeatedListener(v -> {
            adjustVideoCurveSpeed();
            mActivity.onBackPressed();
        }));

        iv_reset.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (currentList == null) {
                return;
            }
            currentList.clear();
            currentList.addAll(resetList);
            adjustVideoCurveSpeed();
            curveSpeedView.refresh(currentList);
        }));

        addPointLayout.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (addType) {
                    case NO:
                        break;
                    case ADD:
                        addPoint();
                        break;
                    case DELETE:
                        deletePoint();
                        break;
                }
            }
        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    private void addPoint() {
        int index = -1;
        for (int i = 0; i < currentList.size() - 1; i++) {
            if (timeFactor >= currentList.get(i).timeFactor && timeFactor <= currentList.get(i + 1).timeFactor) {
                index = i + 1;
                break;
            }
        }
        if (index == -1) {
            return;
        }
        currentList.add(index, new HVESpeedCurvePoint(timeFactor, 1));
        curveSpeedView.refresh(currentList);
        adjustVideoCurveSpeed();

    }
    private void deletePoint() {
        if (pointIndex == 0 || pointIndex == -1 || pointIndex == currentList.size() - 1) {
            return;
        }
        currentList.remove(pointIndex);
        curveSpeedView.refresh(currentList);
        adjustVideoCurveSpeed();
    }

    private void checkAddOrDelete() {
        if (pointIndex == 0 || pointIndex == currentList.size() - 1) {
            addType = AddType.NO;
            addPointLayout.setAlpha(1f);
        } else if (pointIndex == -1) {
            addType = AddType.ADD;
            addPointLayout.setAlpha(1f);
            addIcon.setImageResource(R.drawable.icon_add_mini);
            addText.setText(getString(R.string.add_point));
        } else {
            addType = AddType.DELETE;
            addPointLayout.setAlpha(1f);
            addIcon.setImageResource(R.drawable.icon_delete_point);
            addText.setText(getString(R.string.delete_point));
        }
    }

    public void setSelectedPosition(int position, HVEAsset asset, List<HVESpeedCurvePoint> list,
        List<HVESpeedCurvePoint> resetLists, String curveName) {
        if (resetList == null) {
            resetList = new ArrayList<>();
        } else {
            resetList.clear();
        }
        if (currentList == null) {
            currentList = new ArrayList<>();
        } else {
            currentList.clear();
        }
        resetList.addAll(resetLists);
        currentList = list;
        this.curveName = curveName;
        this.asset = asset;
        this.position = position;
    }

    private void refreshTime() {
        if (asset == null || oldTime == null || newTime == null) {
            return;
        }
        oldTime.setText(
            getString(R.string.duration) + NumberFormat.getInstance().format(asset.getOriginLength() / 1000) + "s");
        newTime.setText(NumberFormat.getInstance().format(asset.getDuration() / 1000) + "s");
        ViewTreeObserver addObserver = addPointLayout.getViewTreeObserver();
        addObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                addPointLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                measureRemainSpace();
            }
        });
    }

    private void measureRemainSpace() {
        int remainSpaceWidth =
            (ScreenUtil.getScreenWidth(mActivity) - addPointLayout.getMeasuredWidth() - ScreenUtil.dp2px(32)) / 2;
        iv_reset.post(new Runnable() {
            @Override
            public void run() {
                if (iv_reset.getMeasuredWidth() > remainSpaceWidth) {
                    resetParams.setMargins(ScreenUtil.dp2px(16), ScreenUtil.dp2px(8), 0, 0);
                    resetParams.topToBottom = R.id.add_point_layout;
                    timeSpeedParams.setMargins(0, ScreenUtil.dp2px(8), ScreenUtil.dp2px(16), 0);
                    timeSpeedParams.topToBottom = R.id.add_point_layout;
                } else {
                    resetParams.setMargins(ScreenUtil.dp2px(16), 0, 0, 0);
                    resetParams.topToTop = R.id.add_point_layout;
                    resetParams.bottomToBottom = R.id.add_point_layout;
                }
            }
        });
        iv_reset.setLayoutParams(resetParams);
        timeSpeedLayout.post(new Runnable() {
            @Override
            public void run() {
                if (timeSpeedLayout.getMeasuredWidth() > remainSpaceWidth) {
                    timeSpeedParams.setMargins(0, ScreenUtil.dp2px(8), ScreenUtil.dp2px(16), 0);
                    timeSpeedParams.topToBottom = R.id.add_point_layout;
                } else {
                    timeSpeedParams.setMargins(0, 0, ScreenUtil.dp2px(16), 0);
                    timeSpeedParams.topToTop = R.id.add_point_layout;
                    timeSpeedParams.bottomToBottom = R.id.add_point_layout;
                }
            }
        });
        timeSpeedLayout.setLayoutParams(timeSpeedParams);
    }

    private void adjustVideoCurveSpeed() {
        asset = mEditPreviewViewModel.getSelectedAsset();
        if (asset == null) {
            asset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (asset == null || asset.getType() != HVEAsset.HVEAssetType.VIDEO) {
            return;
        }
        if (mEditPreviewViewModel.getTimeLine() == null) {
            return;
        }

        List<HVEVideoLane> videoLanes = mEditPreviewViewModel.getTimeLine().getAllVideoLane();
        if (videoLanes.isEmpty()) {
            return;
        }
        HVEVideoLane videoLane = videoLanes.get(asset.getLaneIndex());
        if (videoLane == null) {
            return;
        }

        mEditor.pauseTimeLine();

        boolean isSuccess = videoLane.addCurveSpeed(asset.getIndex(), curveName, currentList);
        if (isSuccess) {
            mEditPreviewViewModel.reloadMainLane();
            mEditor.playTimeLine(asset.getStartTime(), asset.getEndTime());
        } else {
            ToastWrapper.makeText(mActivity, mActivity.getString(R.string.set_seeped_fail), Toast.LENGTH_SHORT).show();
        }
        refreshTime();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchSTListener = new VideoClipsActivity.TimeOutOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent ev) {
            initTimeoutState();
            return false;
        }
    };

    private enum AddType {
        ADD,
        DELETE,
        NO
    }
}
