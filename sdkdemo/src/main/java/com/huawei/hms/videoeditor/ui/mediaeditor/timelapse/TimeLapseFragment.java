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

package com.huawei.hms.videoeditor.ui.mediaeditor.timelapse;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenBuilderUtil;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.ScaleBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditor.utils.SmartLog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

public class TimeLapseFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "TimeLapseFragment";

    private static final String OPERATE_ID = "operate_id";

    private TextView tvTitle;

    private EditorTextView tvSky;

    private View viewSky;

    private ConstraintLayout clSky;

    private ScaleBar scaleBarSky;

    private int scaleSky = 0;

    private RadioGroup rgSpeedSky;

    private RadioButton rbSpeedSlowSky;

    private TextView tvSpeedSlowSky;

    private RadioButton rbSpeedStandardSky;

    private TextView tvSpeedStandardSky;

    private RadioButton rbSpeedFastSky;

    private TextView tvSpeedFastSky;

    private int speedSky = 2;

    private EditorTextView tvRiver;

    private View viewRiver;

    private ConstraintLayout clRiver;

    private ScaleBar scaleBarRiver;

    private TextView tvStartProcess;

    private int scaleRiver = 90;

    private RadioGroup rgSpeedRiver;

    private RadioButton rbSpeedSlowRiver;

    private TextView tvSpeedSlowRiver;

    private RadioButton rbSpeedStandardRiver;

    private TextView tvSpeedStandardRiver;

    private RadioButton rbSpeedFastRiver;

    private TextView tvSpeedFastRiver;

    private int speedRiver = 2;

    private EditPreviewViewModel mEditPreviewViewModel;

    private TimeLapseViewModel mTimeLapseViewModel;

    private int mSkyRiverType;

    private int mOperationId;

    private boolean isReleaseByBack = true;

    public static TimeLapseFragment newInstance(int operationId) {
        Bundle args = new Bundle();
        args.putInt(OPERATE_ID, operationId);
        TimeLapseFragment recognitionFragment = new TimeLapseFragment();
        recognitionFragment.setArguments(args);
        return recognitionFragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_time_lapse;
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cut_second_menu_time_lapse);
        view.findViewById(R.id.iv_certain).setOnClickListener(this);

        tvSky = view.findViewById(R.id.tv_sky);
        tvSky.setOnClickListener(this);
        clSky = view.findViewById(R.id.cl_sky);
        viewSky = view.findViewById(R.id.view_sky);
        scaleBarSky = view.findViewById(R.id.scale_bar_sky);
        rgSpeedSky = view.findViewById(R.id.rg_speed_sky);
        rbSpeedSlowSky = view.findViewById(R.id.rb_speed_slow_sky);
        rbSpeedStandardSky = view.findViewById(R.id.rb_speed_standard_sky);
        rbSpeedFastSky = view.findViewById(R.id.rb_speed_fast_sky);
        tvSpeedSlowSky = view.findViewById(R.id.tv_speed_slow_sky);
        tvSpeedStandardSky = view.findViewById(R.id.tv_speed_standard_sky);
        tvSpeedFastSky = view.findViewById(R.id.tv_speed_fast_sky);
        tvSpeedSlowSky.setOnClickListener(this);
        tvSpeedStandardSky.setOnClickListener(this);
        tvSpeedFastSky.setOnClickListener(this);

        tvRiver = view.findViewById(R.id.tv_river);
        tvRiver.setOnClickListener(this);
        viewRiver = view.findViewById(R.id.view_river);
        clRiver = view.findViewById(R.id.cl_river);
        scaleBarRiver = view.findViewById(R.id.scale_bar_river);
        rgSpeedRiver = view.findViewById(R.id.rg_speed_river);
        rbSpeedSlowRiver = view.findViewById(R.id.rb_speed_slow_river);
        rbSpeedStandardRiver = view.findViewById(R.id.rb_speed_standard_river);
        rbSpeedFastRiver = view.findViewById(R.id.rb_speed_fast_river);
        tvSpeedSlowRiver = view.findViewById(R.id.tv_speed_slow_river);
        tvSpeedStandardRiver = view.findViewById(R.id.tv_speed_standard_river);
        tvSpeedFastRiver = view.findViewById(R.id.tv_speed_fast_river);
        tvSpeedSlowRiver.setOnClickListener(this);
        tvSpeedStandardRiver.setOnClickListener(this);
        tvSpeedFastRiver.setOnClickListener(this);

        tvStartProcess = view.findViewById(R.id.tv_start_process);
        tvStartProcess.setOnClickListener(this);
        if (ScreenBuilderUtil.isRTL()) {
            scaleBarSky.setScaleX(Constant.RTL_UI);
            scaleBarRiver.setScaleX(Constant.RTL_UI);
        } else {
            scaleBarSky.setScaleX(Constant.LTR_UI);
            scaleBarRiver.setScaleX(Constant.LTR_UI);
        }
    }

    @Override
    protected void initObject() {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mTimeLapseViewModel = new ViewModelProvider(mActivity, mFactory).get(TimeLapseViewModel.class);
        SafeBundle safeBundle = new SafeBundle(getArguments());
        mOperationId = safeBundle.getInt(OPERATE_ID, 0);

        HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (selectedAsset == null) {
            selectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (selectedAsset != null) {
            mTimeLapseViewModel.setSelectedAsset(selectedAsset);
        }

        mSkyRiverType = mTimeLapseViewModel.getTimeLapseResult();
        mTimeLapseViewModel.setSkyRiverType(mSkyRiverType);
        SmartLog.d(TAG, "TimeLapseResult== " + mSkyRiverType);
    }

    @Override
    protected void initData() {
        scaleBarSky.setScale(0);
        scaleBarRiver.setScale(90);
        if (mTimeLapseViewModel != null) {
            scaleSky = mTimeLapseViewModel.getScaleSky();
            scaleBarSky.setScale(scaleSky);

            speedSky = mTimeLapseViewModel.getSpeedSky();
            if (speedSky == 1) {
                rbSpeedSlowSky.setChecked(true);
            } else if (speedSky == 2) {
                rbSpeedStandardSky.setChecked(true);
            } else if (speedSky == 3) {
                rbSpeedFastSky.setChecked(true);
            }

            scaleRiver = mTimeLapseViewModel.getScaleRiver();
            scaleBarRiver.setScale(scaleRiver);

            speedRiver = mTimeLapseViewModel.getSpeedRiver();
            if (speedRiver == 1) {
                rbSpeedSlowRiver.setChecked(true);
            } else if (speedRiver == 2) {
                rbSpeedStandardRiver.setChecked(true);
            } else if (speedRiver == 3) {
                rbSpeedFastRiver.setChecked(true);
            }
        }
    }

    @Override
    protected void initEvent() {
        scaleBarSky.setOnProgressChangedListener(progress -> {
            scaleSky = scaleBarSky.getScaleByProgress(progress);
            String scaleText = scaleBarSky.getProgressText(progress);
            if (mEditPreviewViewModel != null) {
                mEditPreviewViewModel.setToastTime(scaleText);
                mEditPreviewViewModel.setRotation(scaleSky);
            }
            if (mTimeLapseViewModel != null) {
                mTimeLapseViewModel.setScaleSky(scaleSky);
            }
        });
        scaleBarSky.setaTouchListener(isTouch -> {
            String scaleText = scaleBarSky.getProgressText(scaleSky);
            if (mEditPreviewViewModel != null) {
                mEditPreviewViewModel.setToastTime(isTouch ? scaleText : "");
                mEditPreviewViewModel.setRotation(isTouch ? scaleSky : 360);
            }
            if (!isTouch && mTimeLapseViewModel != null) {
                mTimeLapseViewModel.setScaleSky(scaleSky);
            }
            SmartLog.d(TAG, "sky scale:" + scaleSky);
        });
        rgSpeedSky.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_speed_slow_sky) {
                    rbSpeedSlowSky.setChecked(true);
                    speedSky = 1;
                } else if (checkedId == R.id.rb_speed_standard_sky) {
                    rbSpeedStandardSky.setChecked(true);
                    speedSky = 2;
                } else if (checkedId == R.id.rb_speed_fast_sky) {
                    rbSpeedFastSky.setChecked(true);
                    speedSky = 3;
                }
                if (mTimeLapseViewModel != null) {
                    mTimeLapseViewModel.setSpeedSky(speedSky);
                }
                SmartLog.d(TAG, "sky speed:" + speedSky);
            }
        });

        scaleBarRiver.setOnProgressChangedListener(progress -> {
            scaleRiver = scaleBarRiver.getScaleByProgress(progress);
            String scaleText = scaleBarRiver.getProgressText(progress);
            if (mEditPreviewViewModel != null) {
                mEditPreviewViewModel.setToastTime(scaleText);
                mEditPreviewViewModel.setRotation(scaleRiver);
            }
            if (mTimeLapseViewModel != null) {
                mTimeLapseViewModel.setScaleRiver(scaleRiver);
            }
        });
        scaleBarRiver.setaTouchListener(isTouch -> {
            String scaleText = scaleBarRiver.getProgressText(scaleRiver);
            if (mEditPreviewViewModel != null) {
                mEditPreviewViewModel.setToastTime(isTouch ? scaleText : "");
                mEditPreviewViewModel.setRotation(isTouch ? scaleRiver : 360);
            }
            if (!isTouch && mTimeLapseViewModel != null) {
                mTimeLapseViewModel.setScaleRiver(scaleRiver);
            }
            SmartLog.d(TAG, "river scale:" + scaleRiver);
        });
        rgSpeedRiver.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_speed_slow_river) {
                    rbSpeedSlowRiver.setChecked(true);
                    speedRiver = 1;
                } else if (checkedId == R.id.rb_speed_standard_river) {
                    rbSpeedStandardRiver.setChecked(true);
                    speedRiver = 2;
                } else if (checkedId == R.id.rb_speed_fast_river) {
                    rbSpeedFastRiver.setChecked(true);
                    speedRiver = 3;
                }
                if (mTimeLapseViewModel != null) {
                    mTimeLapseViewModel.setSpeedRiver(speedRiver);
                }
                SmartLog.d(TAG, "river speed:" + speedRiver);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_sky) {
            showSky();
        } else if (id == R.id.tv_river) {
            showRiver();
        } else if (id == R.id.tv_speed_slow_sky) {
            rbSpeedSlowSky.setChecked(true);
            speedSky = 1;
        } else if (id == R.id.tv_speed_standard_sky) {
            rbSpeedStandardSky.setChecked(true);
            speedSky = 2;
        } else if (id == R.id.tv_speed_fast_sky) {
            rbSpeedFastSky.setChecked(true);
            speedSky = 3;
        } else if (id == R.id.tv_speed_slow_river) {
            rbSpeedSlowRiver.setChecked(true);
            speedRiver = 1;
        } else if (id == R.id.tv_speed_standard_river) {
            rbSpeedStandardRiver.setChecked(true);
            speedRiver = 2;
        } else if (id == R.id.tv_speed_fast_river) {
            rbSpeedFastRiver.setChecked(true);
            speedRiver = 3;
        } else if (id == R.id.iv_certain) {
            isReleaseByBack = true;
            onBackPressed();
            MenuClickManager.getInstance().popView();
        } else if (id == R.id.tv_start_process) {
            mTimeLapseViewModel.setTimeLapseStart(mOperationId);
            isReleaseByBack = false;
            onBackPressed();
            MenuClickManager.getInstance().popView();
        }
    }

    private void showSky() {
        tvSky.setSelected(true);
        viewSky.setVisibility(View.VISIBLE);
        clSky.setVisibility(View.VISIBLE);
        tvRiver.setSelected(false);
        viewRiver.setVisibility(View.INVISIBLE);
        clRiver.setVisibility(View.GONE);
    }

    private void showRiver() {
        tvSky.setSelected(false);
        viewSky.setVisibility(View.INVISIBLE);
        clSky.setVisibility(View.GONE);
        tvRiver.setSelected(true);
        viewRiver.setVisibility(View.VISIBLE);
        clRiver.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initViewModelObserve() {
    }

    @Override
    protected int setViewLayoutEvent() {
        return DYNAMIC_HEIGHT;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.setRotation(360);
        }
        if (isReleaseByBack) {
            mTimeLapseViewModel.stopTimeLapse();
        }
    }
}
