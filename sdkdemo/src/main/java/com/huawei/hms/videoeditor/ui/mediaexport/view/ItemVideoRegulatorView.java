/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.hms.videoeditor.ui.mediaexport.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.mediaexport.utils.ExportUtils;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.text.NumberFormat;
import java.util.Locale;

public class ItemVideoRegulatorView extends LinearLayout {

    private static final int RESOLUTION_480P = 480;

    private static final int RESOLUTION_720P = 720;

    private static final int RESOLUTION_1080P = 1080;

    private static final int RESOLUTION_2K = 2;

    private static final int RESOLUTION_4K = 4;

    private static final int FRAME_RATE_24 = 24;

    private static final int FRAME_RATE_25 = 25;

    private static final int FRAME_RATE_30 = 30;

    private static final int FRAME_RATE_50 = 50;

    private static final int FRAME_RATE_60 = 60;

    private static final int TYPE_RESOLUTION = 1;

    private LinearLayout mMax5View;

    private LinearLayout mMax4View;

    private LinearLayout mMax3View;

    private LinearLayout mMax2View;

    private TextView mMax5Progress1;

    private TextView mMax5Progress2;

    private TextView mMax5Progress3;

    private TextView mMax5Progress4;

    private TextView mMax5Progress5;

    private TextView mMax4Progress1;

    private TextView mMax4Progress2;

    private TextView mMax4Progress3;

    private TextView mMax4Progress4;

    private TextView mMax3Progress1;

    private TextView mMax3Progress2;

    private TextView mMax3Progress3;

    private TextView mMax2Progress1;

    private TextView mMax2Progress2;

    private TextView mTitle;

    private TextView mDescription;

    private SeekBar mSeekBar;

    private OnResolutionChanged onResolutionChanged;

    private OnFrameChanged onFrameChanged;

    private Context mContext;

    private int mType = 0;

    private int mMaxProgress = 2;

    private int mProgress = 2;

    public ItemVideoRegulatorView(Context context) {
        this(context, null);
    }

    public ItemVideoRegulatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemVideoRegulatorView);
        if (typedArray != null) {
            mType = typedArray.getInteger(R.styleable.ItemVideoRegulatorView_video_regulator_type, 0);
        }
        initData();
        initEvent();
    }

    private void initView() {
        if (mContext == null) {
            return;
        }
        LayoutInflater.from(mContext).inflate(R.layout.item_video_regulator, this, true);
        mTitle = findViewById(R.id.text_title);
        mDescription = findViewById(R.id.text_description);
        mSeekBar = findViewById(R.id.seek_bar);
        mSeekBar.post(() -> {
            mSeekBar.setMax(getMaxProgress());
            mSeekBar.setProgress(getMaxProgress() < 2 ? 1 : 2);
        });
        mMax5View = findViewById(R.id.max5_view);
        mMax5Progress1 = findViewById(R.id.max5_progress1);
        mMax5Progress2 = findViewById(R.id.max5_progress2);
        mMax5Progress3 = findViewById(R.id.max5_progress3);
        mMax5Progress4 = findViewById(R.id.max5_progress4);
        mMax5Progress5 = findViewById(R.id.max5_progress5);
        mMax4View = findViewById(R.id.max4_view);
        mMax4Progress1 = findViewById(R.id.max4_progress1);
        mMax4Progress2 = findViewById(R.id.max4_progress2);
        mMax4Progress3 = findViewById(R.id.max4_progress3);
        mMax4Progress4 = findViewById(R.id.max4_progress4);
        mMax3View = findViewById(R.id.max3_view);
        mMax3Progress1 = findViewById(R.id.max3_progress1);
        mMax3Progress2 = findViewById(R.id.max3_progress2);
        mMax3Progress3 = findViewById(R.id.max3_progress3);
        mMax2View = findViewById(R.id.max2_view);
        mMax2Progress1 = findViewById(R.id.max2_progress1);
        mMax2Progress2 = findViewById(R.id.max2_progress2);
        new Handler().postDelayed(() -> {
            switch (getMaxProgress()) {
                case 4:
                    mMax5View.setVisibility(View.VISIBLE);
                    mMax4View.setVisibility(View.GONE);
                    mMax3View.setVisibility(View.GONE);
                    mMax2View.setVisibility(View.GONE);
                    break;
                case 3:
                    mMax5View.setVisibility(View.GONE);
                    mMax4View.setVisibility(View.VISIBLE);
                    mMax3View.setVisibility(View.GONE);
                    mMax2View.setVisibility(View.GONE);
                    break;
                case 1:
                    mMax5View.setVisibility(View.GONE);
                    mMax4View.setVisibility(View.GONE);
                    mMax3View.setVisibility(View.GONE);
                    mMax2View.setVisibility(View.VISIBLE);
                    break;
                default:
                    mMax5View.setVisibility(View.GONE);
                    mMax4View.setVisibility(View.GONE);
                    mMax3View.setVisibility(View.VISIBLE);
                    mMax2View.setVisibility(View.GONE);
                    break;
            }
        }, 200);
    }

    private void initData() {
        if (mContext == null) {
            return;
        }
        String mediumLevel = String.format(Locale.getDefault(),
            mContext.getResources().getQuantityString(R.plurals.resolution_level, RESOLUTION_480P),
            RESOLUTION_480P);

        // 720P/HD(High Definition)
        String highLevel = String.format(Locale.getDefault(),
            mContext.getResources().getQuantityString(R.plurals.resolution_level, RESOLUTION_720P),
            RESOLUTION_720P);

        // 1080P/FHD(Full High Definition)
        String fullHighLevel = String.format(Locale.getDefault(),
            mContext.getResources().getQuantityString(R.plurals.resolution_level, RESOLUTION_1080P),
            RESOLUTION_1080P);

        // 2K/QHD(Quarter High Definition)
        String quarterHighLevel = mContext.getResources().getString(R.string.quarter_high_definition);

        // 4K/UDH(Ultra High Definition)
        String ultraHighLevel = mContext.getResources().getString(R.string.ultra_high_definition);

        if (mType == 2) {
            mTitle.setText(mContext.getResources().getString(R.string.export_frame_rate));
            mDescription.setText(mContext.getResources().getString(R.string.fps_prompt));
            mMax5Progress1.setText(NumberFormat.getInstance().format(FRAME_RATE_24));
            mMax5Progress2.setText(NumberFormat.getInstance().format(FRAME_RATE_25));
            mMax5Progress3.setText(NumberFormat.getInstance().format(FRAME_RATE_30));
            mMax5Progress4.setText(NumberFormat.getInstance().format(FRAME_RATE_50));
            mMax5Progress5.setText(NumberFormat.getInstance().format(FRAME_RATE_60));
            mMax4Progress1.setText(NumberFormat.getInstance().format(FRAME_RATE_24));
            mMax4Progress2.setText(NumberFormat.getInstance().format(FRAME_RATE_25));
            mMax4Progress3.setText(NumberFormat.getInstance().format(FRAME_RATE_30));
            mMax4Progress4.setText(NumberFormat.getInstance().format(FRAME_RATE_50));
            mMax3Progress1.setText(NumberFormat.getInstance().format(FRAME_RATE_24));
            mMax3Progress2.setText(NumberFormat.getInstance().format(FRAME_RATE_25));
            mMax3Progress3.setText(NumberFormat.getInstance().format(FRAME_RATE_30));
            mMax2Progress1.setText(NumberFormat.getInstance().format(FRAME_RATE_24));
            mMax2Progress2.setText(NumberFormat.getInstance().format(FRAME_RATE_25));
        } else {
            mTitle.setText(mContext.getResources().getString(R.string.export_resolution));
            mDescription.setText(mContext.getResources().getString(R.string.px_prompt));
            mMax5Progress1.setAllCaps(true);
            mMax5Progress2.setAllCaps(true);
            mMax5Progress3.setAllCaps(true);
            mMax5Progress4.setAllCaps(true);
            mMax5Progress5.setAllCaps(true);
            mMax5Progress1.setText(mediumLevel);
            mMax5Progress2.setText(highLevel);
            mMax5Progress3.setText(fullHighLevel);
            mMax5Progress4.setText(quarterHighLevel);
            mMax5Progress5.setText(ultraHighLevel);
            mMax4Progress1.setAllCaps(true);
            mMax4Progress2.setAllCaps(true);
            mMax4Progress3.setAllCaps(true);
            mMax4Progress4.setAllCaps(true);
            mMax4Progress1.setText(mediumLevel);
            mMax4Progress2.setText(highLevel);
            mMax4Progress3.setText(fullHighLevel);
            mMax4Progress4.setText(quarterHighLevel);
            mMax3Progress1.setAllCaps(true);
            mMax3Progress2.setAllCaps(true);
            mMax3Progress3.setAllCaps(true);
            mMax3Progress1.setText(mediumLevel);
            mMax3Progress2.setText(highLevel);
            mMax3Progress3.setText(fullHighLevel);
            mMax2Progress1.setAllCaps(true);
            mMax2Progress2.setAllCaps(true);
            mMax2Progress1.setText(mediumLevel);
            mMax2Progress2.setText(highLevel);
        }
    }

    private void initEvent() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarProgress(i);
                if (onResolutionChanged != null) {
                    Size size = ExportUtils.convertProgressToResolution(i);
                    onResolutionChanged.resolution(size.getWidth(), size.getHeight());
                }

                if (onFrameChanged != null) {
                    onFrameChanged.frame(ExportUtils.convertProgressToFrameRate(i));
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

    public void seekBarProgress(int progress) {
        switch (progress) {
            case 0:
                if (mContext != null) {
                    mMax5Progress1.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax5Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress4.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress5.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax4Progress1.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax4Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress4.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax3Progress1.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax3Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax3Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax2Progress1.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax2Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                }
                break;
            case 1:
                if (mContext != null) {
                    mMax5Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress2.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax5Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress4.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress5.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax4Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress2.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax4Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress4.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax3Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax3Progress2.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax3Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax2Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax2Progress2.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                }
                break;
            case 3:
                if (mContext != null) {
                    mMax5Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress4.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax5Progress5.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax4Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress4.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                }
                break;
            case 4:
                if (mContext != null) {
                    mMax5Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress3.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress4.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress5.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                }
                break;
            default:
                if (mContext != null) {
                    mMax5Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress3.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax5Progress4.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax5Progress5.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax4Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax4Progress3.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                    mMax4Progress4.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));

                    mMax3Progress1.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax3Progress2.setTextColor(mContext.getResources().getColor(R.color.color_fff_60));
                    mMax3Progress3.setTextColor(mContext.getResources().getColor(R.color.color_text_focus));
                }
                break;
        }
        setProgress(progress);
    }

    public interface OnResolutionChanged {
        void resolution(int width, int height);
    }

    public interface OnFrameChanged {
        void frame(int frame);
    }

    public void setOnResolutionChanged(OnResolutionChanged onResolutionChanged) {
        this.onResolutionChanged = onResolutionChanged;
    }

    public void setOnFrameChanged(OnFrameChanged onFrameChanged) {
        this.onFrameChanged = onFrameChanged;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int setting) {
        this.mMaxProgress = setting;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }
}
