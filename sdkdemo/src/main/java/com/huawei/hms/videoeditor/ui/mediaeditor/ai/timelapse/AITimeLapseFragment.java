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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenBuilderUtil;
import com.huawei.hms.videoeditor.ui.common.view.EditorTextView;
import com.huawei.hms.videoeditor.ui.common.view.ScaleBar;
import com.huawei.hms.videoeditor.utils.SmartLog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import static com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse.AITimeLapseViewModel.STATE_BACK_PRESSED;
import static com.huawei.hms.videoeditor.ui.mediaeditor.ai.timelapse.AITimeLapseViewModel.STATE_EDIT;

public class AITimeLapseFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "TimeLapseFragment";

    public static final int RTL_UI = -1;

    public static final int LTR_UI = 1;

    private static final String OPERATE_ID = "operate_id";

    private TextView tvTitle;

    private EditorTextView tvSky;

    private View viewSky;

    private ConstraintLayout clSky;

    private ScaleBar scaleBarSky;

    private int scaleSky = 0;

    private RadioGroup rgSpeedSkyGroup;

    private RadioButton rbSpeedSlowSkyButton;

    private TextView tvSpeedSlowSkyTextView;

    private RadioButton rbSpeedStandardSkyButton;

    private TextView tvSpeedStandardSkyTextView;

    private RadioButton rbSpeedFastSkyButton;

    private TextView tvSpeedFastSkyTextView;

    private int speedSky = 2;

    private EditorTextView tvRiverEditorTextView;

    private View viewRiver;

    private ConstraintLayout clRiver;

    private ScaleBar scaleBarRiver;

    private TextView tvStartProcessTextView;

    private int scaleRiver = 90;

    private RadioGroup rgSpeedRiver;

    private RadioButton rbSpeedSlowRiver;

    private TextView tvSpeedSlowRiver;

    private RadioButton rbSpeedStandardRiver;

    private TextView tvSpeedStandardRiver;

    private RadioButton rbSpeedFastRiver;

    private TextView tvSpeedFastRiver;

    private int speedRiver = 2;

    private AITimeLapseViewModel aiTimeLapseViewModel;

    private int mSkyRiverType;

    private int mOperationId;

    private boolean isReleaseByBack = true;

    public static AITimeLapseFragment newInstance(int operationId) {
        Bundle args = new Bundle();
        args.putInt(OPERATE_ID, operationId);
        AITimeLapseFragment aiTimeLapseFragment = new AITimeLapseFragment();
        aiTimeLapseFragment.setArguments(args);
        return aiTimeLapseFragment;
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
        rgSpeedSkyGroup = view.findViewById(R.id.rg_speed_sky);
        rbSpeedSlowSkyButton = view.findViewById(R.id.rb_speed_slow_sky);
        rbSpeedStandardSkyButton = view.findViewById(R.id.rb_speed_standard_sky);
        rbSpeedFastSkyButton = view.findViewById(R.id.rb_speed_fast_sky);
        tvSpeedSlowSkyTextView = view.findViewById(R.id.tv_speed_slow_sky);
        tvSpeedStandardSkyTextView = view.findViewById(R.id.tv_speed_standard_sky);
        tvSpeedFastSkyTextView = view.findViewById(R.id.tv_speed_fast_sky);
        tvSpeedSlowSkyTextView.setOnClickListener(this);
        tvSpeedStandardSkyTextView.setOnClickListener(this);
        tvSpeedFastSkyTextView.setOnClickListener(this);

        tvRiverEditorTextView = view.findViewById(R.id.tv_river);
        tvRiverEditorTextView.setOnClickListener(this);
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

        tvStartProcessTextView = view.findViewById(R.id.tv_start_process);
        tvStartProcessTextView.setOnClickListener(this);
        if (ScreenBuilderUtil.isRTL()) {
            scaleBarSky.setScaleX(RTL_UI);
            scaleBarRiver.setScaleX(RTL_UI);
        } else {
            scaleBarSky.setScaleX(LTR_UI);
            scaleBarRiver.setScaleX(LTR_UI);
        }
    }

    @Override
    protected void initObject() {
        aiTimeLapseViewModel = new ViewModelProvider(mActivity, mFactory).get(AITimeLapseViewModel.class);
        SafeBundle bundle = new SafeBundle(getArguments());
        mOperationId = bundle.getInt(OPERATE_ID, 0);

        mSkyRiverType = aiTimeLapseViewModel.getTimeLapseResult();
        aiTimeLapseViewModel.setSkyRiverType(mSkyRiverType);
        SmartLog.d(TAG, "TimeLapseResult== " + mSkyRiverType);

        aiTimeLapseViewModel.setTimeLapseStatus(STATE_EDIT);
    }

    @Override
    protected void initData() {
        scaleBarSky.setScale(0);
        scaleBarRiver.setScale(90);
        if (aiTimeLapseViewModel != null) {
            scaleSky = aiTimeLapseViewModel.getScaleSky();
            scaleBarSky.setScale(scaleSky);

            speedSky = aiTimeLapseViewModel.getSpeedSky();
            if (speedSky == 1) {
                rbSpeedSlowSkyButton.setChecked(true);
            } else if (speedSky == 2) {
                rbSpeedStandardSkyButton.setChecked(true);
            } else if (speedSky == 3) {
                rbSpeedFastSkyButton.setChecked(true);
            }

            scaleRiver = aiTimeLapseViewModel.getScaleRiver();
            scaleBarRiver.setScale(scaleRiver);

            speedRiver = aiTimeLapseViewModel.getSpeedRiver();
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
            if (aiTimeLapseViewModel != null) {
                aiTimeLapseViewModel.setScaleSky(scaleSky);
            }
        });
        scaleBarSky.setaTouchListener(isTouch -> {
            String scaleText = scaleBarSky.getProgressText(scaleSky);
            if (!isTouch && aiTimeLapseViewModel != null) {
                aiTimeLapseViewModel.setScaleSky(scaleSky);
            }
            SmartLog.d(TAG, "sky scale:" + scaleSky);
        });
        rgSpeedSkyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.rb_speed_slow_sky) {
                    rbSpeedSlowSkyButton.setChecked(true);
                    speedSky = 1;
                } else if (checkedId == R.id.rb_speed_standard_sky) {
                    rbSpeedStandardSkyButton.setChecked(true);
                    speedSky = 2;
                } else if (checkedId == R.id.rb_speed_fast_sky) {
                    rbSpeedFastSkyButton.setChecked(true);
                    speedSky = 3;
                }
                if (aiTimeLapseViewModel != null) {
                    aiTimeLapseViewModel.setSpeedSky(speedSky);
                }
                SmartLog.d(TAG, "sky speed:" + speedSky);
            }
        });

        scaleBarRiver.setOnProgressChangedListener(progress -> {
            scaleRiver = scaleBarRiver.getScaleByProgress(progress);
            String scaleText = scaleBarRiver.getProgressText(progress);
            if (aiTimeLapseViewModel != null) {
                aiTimeLapseViewModel.setScaleRiver(scaleRiver);
            }
        });
        scaleBarRiver.setaTouchListener(isTouch -> {
            String scaleText = scaleBarRiver.getProgressText(scaleRiver);
            if (!isTouch && aiTimeLapseViewModel != null) {
                aiTimeLapseViewModel.setScaleRiver(scaleRiver);
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
                if (aiTimeLapseViewModel != null) {
                    aiTimeLapseViewModel.setSpeedRiver(speedRiver);
                }
                SmartLog.d(TAG, "river speed:" + speedRiver);
            }
        });

        if (isValidActivity()) {
            mActivity.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_sky) {
            showSky();
        } else if (id == R.id.tv_river) {
            showRiver();
        } else if (id == R.id.tv_speed_slow_sky) {
            rbSpeedSlowSkyButton.setChecked(true);
            speedSky = 1;
        } else if (id == R.id.tv_speed_standard_sky) {
            rbSpeedStandardSkyButton.setChecked(true);
            speedSky = 2;
        } else if (id == R.id.tv_speed_fast_sky) {
            rbSpeedFastSkyButton.setChecked(true);
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
        } else if (id == R.id.tv_start_process) {
            aiTimeLapseViewModel.setTimeLapseStart(mOperationId);
            isReleaseByBack = false;
            onBackPressed();
        }
    }

    private void showSky() {
        tvSky.setSelected(true);
        viewSky.setVisibility(View.VISIBLE);
        clSky.setVisibility(View.VISIBLE);
        tvRiverEditorTextView.setSelected(false);
        viewRiver.setVisibility(View.INVISIBLE);
        clRiver.setVisibility(View.GONE);
    }

    private void showRiver() {
        tvSky.setSelected(false);
        viewSky.setVisibility(View.INVISIBLE);
        clSky.setVisibility(View.GONE);
        tvRiverEditorTextView.setSelected(true);
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
        if (aiTimeLapseViewModel != null) {
            aiTimeLapseViewModel.setTimeLapseStatus(STATE_BACK_PRESSED);
            if (isReleaseByBack) {
                aiTimeLapseViewModel.releaseEngine();
            }
        }
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

}
