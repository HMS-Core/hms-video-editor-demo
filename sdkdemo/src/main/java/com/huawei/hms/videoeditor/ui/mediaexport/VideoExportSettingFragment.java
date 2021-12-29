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

package com.huawei.hms.videoeditor.ui.mediaexport;

import java.io.File;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HVEExportManager;
import com.huawei.hms.videoeditor.sdk.HVEExportManager.HVEExportVideoCallback;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.bean.HVEVideoProperty;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.sdk.util.MemoryInfoUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.LazyFragment;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.FoldScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class VideoExportSettingFragment extends LazyFragment implements HVEExportVideoCallback {
    private static final String TAG = "VideoExportSettingFragment";

    public static final String ASPECT_RATIO = "AspectRatio";

    public static final String RESOLUTION = "Resolution";

    public static final int RESOLUTION_MAX_PROGRESS = 3;

    public static final int LOW_RESOLUTION_MAX_PROGRESS = 1;

    public static final int FRAME_RATE_MAX_PROGRESS = 4;

    public static final int LOW_FRAME_RATE_MAX_PROGRESS = 2;

    public static final String RESOLUTION_720P = "720P";

    public static final String RESOLUTION_1080P = "1080P";

    public static final String RESOLUTION_2K = "2K";

    public static final String RESOLUTION_4K = "4K";

    public static final int FRAME_RATE_24 = 24;

    public static final int FRAME_RATE_25 = 25;

    public static final int FRAME_RATE_30 = 30;

    public static final int FRAME_RATE_50 = 50;

    public static final int FRAME_RATE_60 = 60;

    private Button confirm;

    private LinearLayout mResolutionView;

    private LinearLayout mLowResolutionView;

    private LinearLayout mNovaResolutionView;

    private LinearLayout mFrameRateView;

    private LinearLayout mLowFrameRateView;

    private TextView mText720P;

    private TextView mText1080P;

    private TextView mText2K;

    private TextView mText4K;

    private TextView mLowText720P;

    private TextView mLowText1080P;

    private TextView mNovaText720P;

    private TextView mNovaText1080P;

    private TextView mNovaText2K;

    private TextView mText24;

    private TextView mText25;

    private TextView mText30;

    private TextView mText50;

    private TextView mText60;

    private TextView mLowText24;

    private TextView mLowText25;

    private TextView mLowText30;

    private TextView tip;

    private TextView sizeView;

    private SeekBar mFrameRateSb;

    private SeekBar mResolutionRateSb;

    private ConstraintLayout clExportSetting;

    private String mAspectRatio;

    private long time;

    private int mFrameRate = 30;

    private int pResolution = -1;

    private HVEVideoProperty.EncodeType mEncodeType = HVEVideoProperty.EncodeType.ENCODE_H_264;

    int width = 1920;

    int height = 1080;

    private final boolean isLowMemory = MemoryInfoUtil.isLowMemoryDevice();

    private HVEExportManager exportManager;

    private NestedScrollView.LayoutParams params;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.export_bg;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_video_export_setting;
    }

    @Override
    protected void initView(View view) {
        confirm = view.findViewById(R.id.export);
        clExportSetting = view.findViewById(R.id.cl_export);
        if (FoldScreenUtil.isFoldable() && FoldScreenUtil.isFoldableScreenExpand(mContext)) {
            params = (NestedScrollView.LayoutParams) clExportSetting.getLayoutParams();
            if (params != null) {
                params.width = (int) (SizeUtils.screenWidth(mContext) / 1.5f);
                params.gravity = Gravity.CENTER_HORIZONTAL;
            }
        }
        tip = view.findViewById(R.id.tv_tip);
        mFrameRateSb = view.findViewById(R.id.seek_bar_frame_rate);
        mResolutionRateSb = view.findViewById(R.id.seek_bar_resolution);
        sizeView = view.findViewById(R.id.tv_size);

        mResolutionView = view.findViewById(R.id.resolution_layout);
        mLowResolutionView = view.findViewById(R.id.resolution_layout_low);
        mNovaResolutionView = view.findViewById(R.id.resolution_layout_nava9);

        mFrameRateView = view.findViewById(R.id.frame_rate_layout);
        mLowFrameRateView = view.findViewById(R.id.frame_rate_layout_low);

        mText720P = view.findViewById(R.id.text_720P);
        mText1080P = view.findViewById(R.id.text_1080P);
        mText2K = view.findViewById(R.id.text_2K);
        mText4K = view.findViewById(R.id.text_4K);
        mLowText720P = view.findViewById(R.id.text_720P_low);
        mLowText1080P = view.findViewById(R.id.text_1080P_low);
        mNovaText720P = view.findViewById(R.id.text_720P_nava9);
        mNovaText1080P = view.findViewById(R.id.text_1080P_nava9);
        mNovaText2K = view.findViewById(R.id.text_2K_nava9);

        mText24 = view.findViewById(R.id.text_24);
        mText25 = view.findViewById(R.id.text_25);
        mText30 = view.findViewById(R.id.text_30);
        mText50 = view.findViewById(R.id.text_50);
        mText60 = view.findViewById(R.id.text_60);
        mLowText24 = view.findViewById(R.id.text_24_low);
        mLowText25 = view.findViewById(R.id.text_25_low);
        mLowText30 = view.findViewById(R.id.text_30_low);

        mText720P.setText(RESOLUTION_720P);
        mText1080P.setText(RESOLUTION_1080P);
        mText2K.setText(RESOLUTION_2K);
        mText4K.setText(RESOLUTION_4K);
        mLowText720P.setText(RESOLUTION_720P);
        mLowText1080P.setText(RESOLUTION_1080P);
        mNovaText720P.setText(RESOLUTION_720P);
        mNovaText1080P.setText(RESOLUTION_1080P);
        mNovaText2K.setText(RESOLUTION_2K);

        mText24.setText(String.valueOf(FRAME_RATE_24));
        mText25.setText(String.valueOf(FRAME_RATE_25));
        mText30.setText(String.valueOf(FRAME_RATE_30));
        mText50.setText(String.valueOf(FRAME_RATE_50));
        mText60.setText(String.valueOf(FRAME_RATE_60));
        mLowText24.setText(String.valueOf(FRAME_RATE_24));
        mLowText25.setText(String.valueOf(FRAME_RATE_25));
        mLowText30.setText(String.valueOf(FRAME_RATE_30));
        if (isLowMemory) {
            mResolutionRateSb.setMax(LOW_RESOLUTION_MAX_PROGRESS);
            mFrameRateSb.setMax(LOW_FRAME_RATE_MAX_PROGRESS);
            mResolutionView.setVisibility(View.GONE);
            mLowResolutionView.setVisibility(View.VISIBLE);
            mNovaResolutionView.setVisibility(View.GONE);
            mFrameRateView.setVisibility(View.GONE);
            mLowFrameRateView.setVisibility(View.VISIBLE);
        } else {
            mResolutionRateSb.setMax(RESOLUTION_MAX_PROGRESS);
            mFrameRateSb.setMax(FRAME_RATE_MAX_PROGRESS);
            mResolutionView.setVisibility(View.VISIBLE);
            mLowResolutionView.setVisibility(View.GONE);
            mNovaResolutionView.setVisibility(View.GONE);
            mFrameRateView.setVisibility(View.VISIBLE);
            mLowFrameRateView.setVisibility(View.GONE);
        }

        if (!(mActivity instanceof VideoExportActivity)) {
            return;
        }

        HuaweiVideoEditor editor = ((VideoExportActivity) mActivity).getEditor();
        if (editor == null || editor.getTimeLine() == null) {
            return;
        }

        time = editor.getTimeLine().getEndTime();
        long size = HVEUtil.getEstimatesExportVideoSize(1920, 1080, mFrameRate, time,
            mEncodeType == HVEVideoProperty.EncodeType.ENCODE_H_265);
        String finalAge = String.format(Locale.ROOT, mActivity.getResources().getString(R.string.export_size), size);
        sizeView.setText(finalAge);
    }

    @Override
    protected void initObject() {
    }

    @Override
    protected void initData() {
        mResolutionRateSb.setProgress(1);
        mFrameRateSb.setProgress(2);
    }

    @Override
    protected void initEvent() {
        if (confirm != null) {
            confirm.setOnClickListener(onClickRepeatedListener);
        }

        if (mResolutionRateSb != null) {
            mResolutionRateSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    String sFinalAge;
                    switch (progress) {
                        case 0:
                            pResolution = 0;
                            sFinalAge = String.format(Locale.ROOT,
                                mActivity.getResources().getString(R.string.export_size), ((time / 1000) * 3 / 8));
                            mText720P.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText2K.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText4K.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));

                            mLowText720P.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mLowText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));

                            mNovaText720P.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mNovaText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mNovaText2K.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            break;
                        case 1:
                            pResolution = 1;
                            sFinalAge = String.format(Locale.ROOT,
                                mActivity.getResources().getString(R.string.export_size), ((time / 1000) * 5 / 8));
                            mText720P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mText2K.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText4K.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));

                            mLowText720P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mLowText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));

                            mNovaText720P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mNovaText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mNovaText2K.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            sizeView.setText(sFinalAge);
                            break;
                        case 2:
                            pResolution = 2;
                            sFinalAge = String.format(Locale.ROOT,
                                mActivity.getResources().getString(R.string.export_size), ((time / 1000) * 8 / 8));
                            mText720P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText2K.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mText4K.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));

                            mNovaText720P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mNovaText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mNovaText2K.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            sizeView.setText(sFinalAge);
                            break;
                        case 3:
                            pResolution = 3;
                            sFinalAge = String.format(Locale.ROOT,
                                mActivity.getResources().getString(R.string.export_size), ((time / 1000) * 16 / 8));
                            mText720P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText1080P.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText2K.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText4K.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            break;
                        default:
                            pResolution = 1;
                            sFinalAge = String.format(Locale.ROOT,
                                mActivity.getResources().getString(R.string.export_size), ((time / 1000) * 3 / 8));
                            break;
                    }
                    SmartLog.d(TAG,
                        "[initEvent] progress= " + progress + " ; onProgressChanged: pResolution-->" + pResolution);
                    switch (pResolution) {
                        case -1:
                        case 1:
                            width = 1920;
                            height = 1080;
                            break;
                        case 0:
                            width = 1280;
                            height = 720;
                            break;
                        case 2:
                            width = 2560;
                            height = 1600;
                            break;
                        case 3:
                            width = 3840;
                            height = 2160;
                            break;
                        default:
                            break;
                    }
                    long size = HVEUtil.getEstimatesExportVideoSize(width, height, mFrameRate, time,
                        mEncodeType == HVEVideoProperty.EncodeType.ENCODE_H_265);
                    sFinalAge =
                        String.format(Locale.ROOT, mActivity.getResources().getString(R.string.export_size), size);
                    sizeView.setText(sFinalAge);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        if (mFrameRateSb != null) {
            mFrameRateSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    switch (progress) {
                        case 0:
                            mFrameRate = FRAME_RATE_24;
                            mText24.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mText25.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText30.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText50.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText60.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mLowText24.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mLowText25.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mLowText30.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            break;
                        case 2:
                            mFrameRate = FRAME_RATE_30;
                            mText24.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText25.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText30.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mText50.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText60.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mLowText24.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mLowText25.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mLowText30.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            break;
                        case 3:
                            mFrameRate = FRAME_RATE_50;
                            mText24.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText25.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText30.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText50.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mText60.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            break;
                        case 4:
                            mFrameRate = FRAME_RATE_60;
                            mText24.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText25.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText30.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText50.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText60.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            break;
                        case 1:
                        default:
                            mFrameRate = FRAME_RATE_25;
                            mText24.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText25.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mText30.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText50.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mText60.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mLowText24.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            mLowText25.setTextColor(mActivity.getResources().getColor(R.color.color_text_focus));
                            mLowText30.setTextColor(mActivity.getResources().getColor(R.color.color_fff_60));
                            break;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    @Override
    public void onCompileProgress(long time, long duration) {
        if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
            return;
        }
        VideoExportActivity activity = (VideoExportActivity) mActivity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (duration != 0) {
                    int progress = (int) (time * 100 / duration);
                    if (progress >= 100) {
                        progress = 100;
                        tip.setVisibility(View.GONE);
                    }
                    activity.setExportProgress(progress);
                }
            }
        });
    }

    @Override
    public void onCompileFinished(String path, Uri uri) {
        if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
            return;
        }
        VideoExportActivity activity = (VideoExportActivity) mActivity;
        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tip.setVisibility(View.GONE);
            }
        });

        activity.exportSuccess(uri);
    }

    @Override
    public void onCompileFailed(int errorCode, String errorMsg) {
        if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
            return;
        }
        VideoExportActivity activity = (VideoExportActivity) mActivity;
        activity.exportFail();
        NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putInt(RESOLUTION, pResolution);
        bundle.putString(ASPECT_RATIO, mAspectRatio);
        navController.navigate(R.id.export_fail, bundle);
    }

    OnClickRepeatedListener onClickRepeatedListener = new OnClickRepeatedListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tip.setVisibility(View.VISIBLE);

            VideoExportActivity activity = (VideoExportActivity) mActivity;
            if (activity == null) {
                return;
            }
            activity.setStartExport(true);
            activity.startConfirm();
            if (clExportSetting != null) {
                clExportSetting.setVisibility(View.INVISIBLE);
            }
            confirm.setVisibility(View.INVISIBLE);

            HVEVideoProperty videoProperty = new HVEVideoProperty(1920, 1080);
            switch (pResolution) {
                case -1:
                case 1:
                    videoProperty = new HVEVideoProperty(1920, 1080, mFrameRate, mEncodeType);
                    mAspectRatio = "1920*1080";
                    break;
                case 0:
                    videoProperty = new HVEVideoProperty(1280, 720, mFrameRate, mEncodeType);
                    mAspectRatio = "1280*720";
                    break;
                case 2:
                    videoProperty = new HVEVideoProperty(2560, 1600, mFrameRate, mEncodeType);
                    mAspectRatio = "2560*1600";
                    break;
                case 3:
                    videoProperty = new HVEVideoProperty(3840, 2160, mFrameRate, mEncodeType);
                    mAspectRatio = "3840*2160";
                    break;
                default:
                    break;
            }

            String outputPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator
                    + Constant.LOCAL_VIDEO_SAVE_PATH + File.separator + VideoExportActivity.getTime() + ".mp4";
            exportManager = new HVEExportManager();

            HuaweiVideoEditor editor = activity.getEditor();
            if (editor == null || TextUtils.isEmpty(outputPath)) {
                return;
            }

            exportManager.exportVideo(editor, VideoExportSettingFragment.this, videoProperty, outputPath);
        }
    });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void interruptVideoExport() {
        if (exportManager != null) {
            exportManager.interruptVideoExport();
        }
    }

    @Override
    public void onDestroy() {
        interruptVideoExport();
        super.onDestroy();
    }
}
