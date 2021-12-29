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

package com.huawei.hms.videoeditor.ui.mediaeditor.crop;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.LicenseException;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVERational;
import com.huawei.hms.videoeditor.sdk.bean.HVESize;
import com.huawei.hms.videoeditor.sdk.lane.HVELane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.BigDecimalUtil;
import com.huawei.hms.videoeditor.sdk.util.HVEUtil;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.utils.FoldScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.TimeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.crop.CropView;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.huawei.hms.videoeditor.sdk.util.ImageUtil.correctionWH;


public class CropNewActivity extends BaseActivity implements CropView.ICropListener, HuaweiVideoEditor.PlayCallback, HuaweiVideoEditor.SurfaceCallback {
    public static final String TAG = "CropNewActivity";

    private CropView mCropView;

    protected RectF mCropF = null;

    protected RectF mRectVideoClipBound;

    protected RectF mCurDefaultClipBound;


    private RecyclerView mRecyclerView;

    private CropNewAdapter mAdapter;

    public static final String MEDIA_DATA = "media";
    public static final String MEDIA_RTN = "hvecut";
    public static final String EDITOR_UUID = "editor_uuid";
    public static final String EDITOR_CURRENT_TIME = "EditorCurrentTime";
    public static final String NO_ACTION_AFTER_RESET = "IsNoActionAfterReset";

    private HuaweiVideoEditor mEditor;
    private ViewGroup mPreview;
    private MediaData mMediaData;
    private ImageView mConfirm;

    private TextView mDuration;

    private float mAssetWidth;
    private float mAssetHeight;

    private float horiWidth;
    private float horiHeight;

    private ImageView mPlay;

    private SeekBar mVideoSeekBar;

    private TextView mStartTimeTxt;

    private TextView mRotation;

    private RectF mLastPageRectF;

    private TextView mReset;

    private int currentRotation;

    private ImageView mClose;

    private long editorCurrentTime;

    private boolean isNoActionAfterReset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.edit_background;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_new);
        isNoActionAfterReset = true;
        findView();
        initEdit();
        initView();
    }

    private void findView() {
        mPreview = findViewById(R.id.viewpreview);
        mCropView = findViewById(R.id.image_crop_view);
        mConfirm = findViewById(R.id.contain_crop_video);
        mDuration = findViewById(R.id.seek_total);
        mVideoSeekBar = findViewById(R.id.videoseekbar);
        mStartTimeTxt = findViewById(R.id.starttime);
        mRotation = findViewById(R.id.rotate_crop_video);
        mReset = findViewById(R.id.reset_crop_video);
        mPlay = findViewById(R.id.playbtn);
        mClose = findViewById(R.id.back_crop_video);
    }

    private void initEdit() {
        SafeIntent intent = new SafeIntent(getIntent());
        mMediaData = intent.getParcelableExtra(MEDIA_DATA);
        editorCurrentTime = intent.getLongExtra(EDITOR_CURRENT_TIME, 0);

        if (mMediaData == null) {
            return;
        }

        String path = mMediaData.getPath();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        currentRotation = (int) mMediaData.getRotation();
        mEditor = HuaweiVideoEditor.createEditor(this);
        try {
            mEditor.initEnvironment();
        } catch (LicenseException e) {
            SmartLog.e(TAG, "initEnvironment failed: " + e.getErrorMsg());
            ToastWrapper.makeText(this, this.getResources().getString(R.string.license_invalid)).show();
            finish();
            return;
        }
        mEditor.setDisplay(mPreview);
        mEditor.setPlayCallback(this);

        HVEVideoLane hveVideoLane = mEditor.getTimeLine().appendVideoLane();
        if (HVEUtil.isLegalImage(path)) {
            hveVideoLane.appendImageAsset(path);
        } else {
            hveVideoLane.appendVideoAsset(path);
            hveVideoLane.cutAsset(0, mMediaData.getCutTrimIn(), HVELane.HVETrimType.TRIM_IN);
            hveVideoLane.cutAsset(0, mMediaData.getCutTrimOut(), HVELane.HVETrimType.TRIM_OUT);
        }

        HVETimeLine timeLine = mEditor.getTimeLine();
        if (timeLine == null) {
            return;
        }

        timeLine.setCurrentTime(editorCurrentTime);
        mEditor.seekTimeLine(editorCurrentTime, new HuaweiVideoEditor.SeekCallback() {
            @Override
            public void onSeekFinished() {
                mVideoSeekBar.setProgress((int) (editorCurrentTime));
                initCrop();
            }
        });
    }

    private void initView() {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaData mediaData = handleDataForPickCrop();
                SafeIntent safeIntent = new SafeIntent(new Intent());
                safeIntent.putExtra(MEDIA_RTN, mediaData);
                safeIntent.putExtra(NO_ACTION_AFTER_RESET, isNoActionAfterReset);
                setResult(RESULT_OK, safeIntent);
                release();
            }
        });

        mRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNoActionAfterReset = false;
                setRotation();
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNoActionAfterReset = true;
                setRotation(0);
                mAdapter.click(0, true);
            }
        });

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initProgress();
        initRecycler();
    }

    private void setRotation() {
        if (currentRotation >= 0 && currentRotation < 270) {
            currentRotation += 90;
        } else {
            currentRotation = 0;
        }
        setRotation(currentRotation);
    }

    private void setRotation(int rotation) {
        if (mEditor == null || mEditor.getTimeLine() == null) {
            return;
        }

        currentRotation = rotation;
        HVEVideoLane hveVideoLane = mEditor.getTimeLine().getVideoLane(0);
        if (hveVideoLane == null) {
            return;
        }
        List<HVEAsset> hveAssetList = hveVideoLane.getAssets();
        if (hveAssetList == null || hveAssetList.isEmpty()) {
            return;
        }
        HVEAsset asset = hveAssetList.get(0);
        if (horiWidth == 0 || horiHeight == 0) {
            float[] floats = correctionWH(mEditor.getSurfaceHeight(), mEditor.getSurfaceWidth(), mAssetWidth, mAssetHeight);
            horiWidth = floats[0];
            horiHeight = floats[1];
        }
        ((HVEVisibleAsset) asset).setRotation(rotation);
        if (rotation == 90 || rotation == 270) {
            ((HVEVisibleAsset) asset).setSize(horiWidth, horiHeight);
        } else {
            ((HVEVisibleAsset) asset).setSize(mAssetWidth, mAssetHeight);
        }
        mCropView.initialize(mCropView.getCrop(), mRectVideoClipBound, -rotation);

        mEditor.refresh(mEditor.getTimeLine().getCurrentTime());
    }

    private void initProgress() {
        if (mEditor == null || mEditor.getTimeLine() == null) {
            return;
        }

        long duration = mEditor.getTimeLine().getDuration();
        mDuration.setText(TimeUtils.makeTimeString(this, duration));
        mStartTimeTxt.setText(TimeUtils.makeTimeString(this, editorCurrentTime));
        mVideoSeekBar.setMax((int) duration);
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    mEditor.pauseTimeLine();
                } else {
                    mEditor.playTimeLine(editorCurrentTime, mEditor.getTimeLine().getEndTime());
                }
                mPlay.setSelected(!mPlay.isSelected());
            }
        });
        mVideoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mStartTimeTxt.setText(TimeUtils.makeTimeString(CropNewActivity.this, i));
                if (!isPlaying()) {
                    mEditor.seekTimeLine(i, () -> {
                    });
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mPlay.setSelected(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mEditor.pauseTimeLine();
            }
        });
    }

    private boolean isPlaying() {
        if (mPlay == null) {
            return false;
        }
        return mPlay.isSelected();
    }

    private void release() {
        if (mEditor != null) {
            mEditor.pauseTimeLine();
            mEditor.stopRenderer();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
    }

    private MediaData handleDataForPickCrop() {
        MediaData rtnDate = new MediaData();
        RectF crop = mCropView.getCrop();
        if (crop == null) {
            return null;
        }
        RectF screenBounds = new RectF(0, 0, mAssetWidth, mAssetHeight);
        float cropWidth = crop.right - crop.left;
        float cropHeight = crop.bottom - crop.top;

        float mParentHeight = screenBounds.bottom;
        float mParentWidth = screenBounds.right;

        float mBottomLeftX = (crop.left - (mParentWidth - mAssetWidth) / 2) / mAssetWidth;
        float mBottomLeftY = (mParentHeight - crop.bottom - (mParentHeight - mAssetHeight) / 2) / mAssetHeight;
        float mRightTopX = (crop.right - (mParentWidth - mAssetWidth) / 2) / mAssetWidth;
        float mRightTopY = (mParentHeight - crop.top - (mParentHeight - mAssetHeight) / 2) / mAssetHeight;

        rtnDate.setGlLeftBottomX(mBottomLeftX);
        rtnDate.setGlLeftBottomY(mBottomLeftY);
        rtnDate.setGlRightTopX(mRightTopX);
        rtnDate.setGlRightTopY(mRightTopY);
        rtnDate.setScaleX(cropWidth);
        rtnDate.setScaleY(cropHeight);

        rtnDate.setRotation(currentRotation);
        float[] floats = correctionWH(mAssetWidth, mAssetHeight, cropWidth, cropHeight);
        rtnDate.setHVEWidth(floats[0]);
        rtnDate.setHVEHeight(floats[1]);

        return rtnDate;
    }

    private void initRecycler() {
        mRecyclerView = findViewById(R.id.recyclerview_crop_video);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        List<Integer> list = new ArrayList<Integer>();
        list.add(R.drawable.crop_free);
        list.add(R.drawable.crop_full);
        list.add(R.drawable.crop_9_16);
        list.add(R.drawable.crop_16_9);
        list.add(R.drawable.crop_1_1);
        list.add(R.drawable.crop_4_3);
        list.add(R.drawable.crop_3_4);
        list.add(R.drawable.crop_2_35_1);
        list.add(R.drawable.crop_9_21);
        list.add(R.drawable.crop_21_9);

        String one = NumberFormat.getInstance().format(1);
        String three = NumberFormat.getInstance().format(3);
        String four = NumberFormat.getInstance().format(4);
        String nine = NumberFormat.getInstance().format(9);
        String sixteen = NumberFormat.getInstance().format(16);
        String twentyOne = NumberFormat.getInstance().format(21);

        List<String> listName = new ArrayList<String>();
        listName.add(getString(R.string.free));
        listName.add(getString(R.string.full));
        listName.add(nine + ":" + sixteen);
        listName.add(sixteen + ":" + nine);
        listName.add(one + ":" + one);
        listName.add(four + ":" + three);
        listName.add(three + ":" + four);
        listName.add(NumberFormat.getInstance().format(2.35) + ":" + one);
        listName.add(nine + ":" + twentyOne);
        listName.add(twentyOne + ":" + nine);

        mAdapter = new CropNewAdapter(listName, list, this);

        mAdapter.setIndex(0);
        mAdapter.setSelectedListener(new CropNewAdapter.OnStyleSelectedListener() {
            @Override
            public void onStyleSelected(int position, boolean isReset) {
                mAdapter.setIndex(position);
                mAdapter.notifyDataSetChanged();
                if (!isReset) {
                    isNoActionAfterReset = false;
                }
                float shouldWidth;
                float shouldHeight;
                float x = 1;
                float y = 1;
                switch (position) {
                    case 0:
                        x = mAssetWidth;
                        y = mAssetHeight;
                        mCropView.applyFreeAspect();
                        break;
                    case 1:
                        x = mAssetWidth;
                        y = mAssetHeight;
                        mCropView.applyAspect(x, y);
                        break;
                    case 2:
                        x = 9;
                        y = 16;
                        mCropView.applyAspect(x, y);
                        break;
                    case 3:
                        x = 16;
                        y = 9;
                        mCropView.applyAspect(x, y);
                        break;
                    case 4:
                        x = 1;
                        y = 1;
                        mCropView.applyAspect(x, y);
                        break;
                    case 5:
                        x = 4;
                        y = 3;
                        mCropView.applyAspect(x, y);
                        break;
                    case 6:
                        x = 3;
                        y = 4;
                        mCropView.applyAspect(x, y);
                        break;
                    case 7:
                        x = 235;
                        y = 100;
                        mCropView.applyAspect(x, y);
                        break;
                    case 8:
                        x = 9;
                        y = 21;
                        mCropView.applyAspect(x, y);
                        break;
                    case 9:
                        x = 21;
                        y = 9;
                        mCropView.applyAspect(x, y);
                        break;
                    default:
                        break;
                }

                if (isHorizontal()) {
                    float temp = x;
                    x = y;
                    y = temp;
                }

                if (position == 0 || position == 1) {
                    if (isHorizontal()) {
                        x = horiWidth;
                        y = horiHeight;
                    } else {
                        x = mAssetWidth;
                        y = mAssetHeight;
                    }
                }
                float[] floats = correctionWH(mAssetWidth, mAssetHeight, x, y);
                shouldWidth = floats[0];
                shouldHeight = floats[1];
                float xDis = BigDecimalUtil.div(mAssetWidth - shouldWidth, 2f);
                float yDis = BigDecimalUtil.div(mAssetHeight - shouldHeight, 2f);
                RectF rectF = new RectF(xDis, yDis, mAssetWidth - xDis, mAssetHeight - yDis);
                mCropView.initialize(rectF, mRectVideoClipBound, -currentRotation);
            }

            @Override
            public void onAlbumSelected() {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private boolean isHorizontal() {
        return currentRotation == 90 || currentRotation == 270;
    }

    private void initCrop() {
        if (mEditor == null) {
            return;
        }
        HVETimeLine timeLine = mEditor.getTimeLine();
        if (timeLine == null) {
            return;
        }
        HVEVideoLane videoLane = timeLine.getVideoLane(0);
        List<HVEAsset> hveAssetList = videoLane.getAssets();
        if (hveAssetList == null || hveAssetList.isEmpty()) {
            return;
        }
        HVEAsset asset = hveAssetList.get(0);
        if (asset == null) {
            SmartLog.e(TAG, "there is no asset in lane");
            return;
        }

        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        visibleAsset.setRotation(mMediaData.getRotation());
        runOnUiThread(() -> {
            mCropView.setVisibility(View.VISIBLE);

            mCropView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mCropView.setIcropListener(CropNewActivity.this);
            HVESize hveSize = visibleAsset.getSize();
            if (hveSize == null) {
                SmartLog.i(TAG, "[initCrop] editable hveSize is null!!");
                return;
            }
            mAssetWidth = hveSize.width;
            mAssetHeight = hveSize.height;

            mRectVideoClipBound = new RectF(0, 0, mAssetWidth, mAssetHeight);
            mCurDefaultClipBound = new RectF(mRectVideoClipBound);

            if (mMediaData.getGlLeftBottomX() != 0 || mMediaData.getGlLeftBottomY() != 0 || mMediaData.getGlRightTopX() != 0 || mMediaData.getGlRightTopY() != 0) {
                float left = mMediaData.getGlLeftBottomX() * mAssetWidth;
                float top = mAssetHeight - mMediaData.getGlRightTopY() * mAssetHeight;
                float right = mMediaData.getGlRightTopX() * mAssetWidth;
                float bottom = mAssetHeight - mMediaData.getGlLeftBottomY() * mAssetHeight;

                mLastPageRectF = new RectF(left, top, right, bottom);
                mCropView.initialize(mLastPageRectF, mRectVideoClipBound, 0);
            } else {
                mCropView.initialize(mRectVideoClipBound, mRectVideoClipBound, 0);
            }
            mCropView.applyFreeAspect();
        });
    }


    @Override
    public void onPlayState() {

    }

    @Override
    public void onMove() {

    }

    @Override
    public void onPlayProgress(long timeStamp) {
        runOnUiThread(() -> {
            mVideoSeekBar.setProgress((int) (timeStamp));
        });
    }

    @Override
    public void onPlayStopped() {

    }

    @Override
    public void onPlayFinished() {
        mPlay.setSelected(false);
    }

    @Override
    public void onPlayFailed() {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mEditor == null) {
            SmartLog.e(TAG, "[onConfigurationChanged] mEditor is null");
        } else {
            if (FoldScreenUtil.isFoldable()) {
                mEditor.pauseTimeLine();
                mPlay.setSelected(false);
                initEdit();
            }
        }
    }

    @Override
    public void surfaceCreated() {

    }

    @Override
    public void surfaceDestroyed() {

    }

    @Override
    public void surfaceChanged(int width, int height) {
        mEditor.setCanvasRational(new HVERational(width, height));
    }
}