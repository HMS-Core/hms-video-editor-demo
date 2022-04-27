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

package com.huawei.hms.videoeditor.ui.template.module;

import static android.app.Activity.RESULT_OK;
import static com.huawei.hms.videoeditor.ui.mediaexport.VideoExportActivity.SOURCE_HIMOVIE_LOCALVIDEO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.LicenseException;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVECanvas;
import com.huawei.hms.videoeditor.sdk.bean.HVEColor;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.template.HVETemplateElement;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.GsonUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonBottomDialog;
import com.huawei.hms.videoeditor.ui.mediaexport.VideoExportActivity;
import com.huawei.hms.videoeditor.ui.template.adapter.ModuleEditSelectAdapter;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.bean.TemplateProjectBean;
import com.huawei.hms.videoeditor.ui.template.module.activity.TemplateDetailActivity;
import com.huawei.hms.videoeditor.ui.template.utils.CropDataHelper;
import com.huawei.hms.videoeditor.ui.template.utils.ModuleSelectManager;
import com.huawei.hms.videoeditor.ui.template.view.decoration.CenterLayoutManager;
import com.huawei.hms.videoeditor.ui.template.view.dialog.ModuleComposeDialog;
import com.huawei.hms.videoeditor.ui.template.viewmodel.ModuleEditViewModel;
import com.huawei.hms.videoeditor.ui.template.viewmodel.TemplateEditViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class VideoModuleEditFragment extends BaseFragment implements HuaweiVideoEditor.PlayCallback {
    private static final String TAG = "VideoModuleEditFragment";

    private static final String IS_PLAYING = "isPlaying";

    private static final String CURRENT_TIME = "currentTime";

    private static final int REQUEST_EDIT_CODE = 1000;

    private static final int REQUEST_REPLACE_SHOT = 1001;

    private static final int REQUEST_MUDULE_SHOT = 1002;

    private static final int DRAFT_NOT_LOADED = -1;

    private static final int DRAFT_LOADING = 0;

    private static final int DRAFT_LOADED = 1;

    private static final int EXPORT_ACTIVITY = 500;

    private ImageView mCloseIcon;

    private ImageView mPlayStateIcon;

    private TextView mTvRunningTime;

    private TextView mTvTotalTime;

    private TextView mTvExport;

    private RecyclerView mRecyclerView;

    private LinearLayout mVideoLayout;

    private SeekBar seekBar;

    private final List<MediaData> mediaDatas = new ArrayList<>();

    private ModuleEditSelectAdapter mModuleEditSelectAdapter;

    private NavController mNavController;

    private TemplateProjectBean mTemplateResource;

    private HuaweiVideoEditor mEditor;

    private HVETimeLine mTimeLine = null;

    private long mCurrentTime = 0;

    private boolean isSeekBarTouch;

    private boolean isSeekBarPause;

    private boolean isTimelinePlaying;

    private static boolean isEditorReady = false;

    private ModuleComposeDialog mComposeDialog;

    private TemplateEditViewModel mEditViewModel;

    private ModuleEditViewModel mModuleEditViewModel;

    private long mStartTime;

    private int position;

    private CenterLayoutManager mCenterLayoutManager;

    private boolean isPauseTimeLine;

    private static final int REFRESH = 0x10001;

    private static final int TAB_MATERIALS = 0x10010;

    private static final int PLAY_STATE = 0x10020;

    private int mSelectPosition;

    private String mSource;

    private LinearLayout mReplace;

    @Override
    protected void initViewModelObserve() {

    }

    public static boolean isEditorReady() {
        return isEditorReady;
    }

    public static void setIsEditorReady(boolean isEditorReady) {
        VideoModuleEditFragment.isEditorReady = isEditorReady;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_video_module_edit;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initView(View view) {
        mEditViewModel = new ViewModelProvider(mActivity, mFactory).get(TemplateEditViewModel.class);
        mCloseIcon = view.findViewById(R.id.iv_close);
        mTvExport = view.findViewById(R.id.tv_save);
        mTvRunningTime = view.findViewById(R.id.tv_top_running_time);
        mTvTotalTime = view.findViewById(R.id.tv_top_total_time);
        mVideoLayout = view.findViewById(R.id.video_content_layout);
        mRecyclerView = view.findViewById(R.id.choice_recyclerview);
        mPlayStateIcon = view.findViewById(R.id.img_video_play_state);
        seekBar = view.findViewById(R.id.seek_bar_video);
        mReplace = view.findViewById(R.id.replace);
    }

    private String getStandardTime(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date = new Date(timeStamp + 8 * 60 * 60 * 1000);
        sdf.format(date);
        return sdf.format(date);
    }

    @Override
    protected void initObject() {
        mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment_module_detail);
        SafeBundle bundle = new SafeBundle(getArguments());
        String template = bundle.getString(Constant.TEMPLATE_KEY_DETAIL);
        mTemplateResource = GsonUtils.fromJson(template, TemplateProjectBean.class);
        mModuleEditViewModel = new ViewModelProvider(requireActivity(), mFactory).get(ModuleEditViewModel.class);

        mModuleEditViewModel.getCoverPath().observe(this, coverPath -> {
            if (mEditor == null || mEditor.getTimeLine() == null) {
                return;
            }

            mEditor.getTimeLine().addCoverImage(coverPath);
        });
        mModuleEditViewModel.getTemplateResourceLiveData().observe(this, templateResource -> {
            compose();
            if (mediaDatas.size() <= 0) {
                SmartLog.e(TAG, "resource path is null, ");
                return;
            }
            if (mediaDatas.size() != mTemplateResource.getEditableElements().size()) {
                SmartLog.e(TAG, "resource path is illegal, path size is: " + mediaDatas.size() + "; should be: "
                    + mTemplateResource.getEditableElements().size());
                return;
            }
            mTemplateResource = templateResource;

            setIsEditorReady(false);
            playTimeline(0, mTemplateResource.getEditableElements());
            initCanvas();
            showSelectFrameOnPlay(false);
        });
        mModuleEditViewModel.getMaterialDataListLiveData().observe(this, mediaDataList -> {
            mediaDatas.addAll(CropDataHelper.convertMaterialDataToMediaData(mediaDataList));
            setRYCenter(mediaDatas.size(), ScreenUtil.dp2px(56), mRecyclerView);
            mModuleEditSelectAdapter.notifyDataSetChanged();
        });

        mModuleEditViewModel.getReplacePositionLiveData().observe(this, position -> {
            if (mTemplateResource == null) {
                SmartLog.e(TAG, "template resource is null.");
                return;
            }
            if (mEditor == null) {
                SmartLog.e(TAG, "initObject observe mEditor null return");
                return;
            }
            setIsEditorReady(false);
            MediaData data =
                CropDataHelper.convertMaterialDataToMediaData(mModuleEditViewModel.getInitData().get(position));
            HVETemplateElement element = mTemplateResource.getEditableElements().get(position);

            mEditor.getTimeLine()
                .getAllVideoLane()
                .get(element.getLaneOrder())
                .replaceAssetPath(data.getPath(), element.getOrder(), true);

            mModuleEditSelectAdapter.replaceData(data, position);

            playTimeline(0, mTemplateResource.getEditableElements());
            isPauseTimeLine = false;
        });

        mReplace.setOnClickListener(new OnClickRepeatedListener(view -> {
            if (mEditor == null) {
                return;
            }

            SmartLog.i(TAG, "mReplace");
            mModuleEditViewModel.updateMaterialList(CropDataHelper.convertMediaDataToMaterialData(mediaDatas));

            mEditor.pauseTimeLine();
            isPauseTimeLine = true;
            SmartLog.e(TAG, "PLAY_STATE setVisibility is VISIBLE.  mReplace.setOnClickListener");
            mPlayStateIcon.setVisibility(View.VISIBLE);
            Bundle args = new Bundle();
            args.putInt(VideoModuleReplaceFragment.MEDIA_PICK_POSITION, position);
            args.putParcelableArrayList(Constant.EXTRA_MODULE_SELECT_RESULT,
                (ArrayList<? extends Parcelable>) CropDataHelper.convertMediaDataToMaterialData(mediaDatas));
            mNavController.navigate(R.id.videoModuleReplaceFragment, args);
        }));

        initAdapter();
    }

    private void initAdapter() {
        mModuleEditSelectAdapter = new ModuleEditSelectAdapter(context, mediaDatas);
        mCenterLayoutManager = new CenterLayoutManager(context, RecyclerView.HORIZONTAL, false);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(mCenterLayoutManager);
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(context, R.color.black),
                    SizeUtils.dp2Px(context, 106), SizeUtils.dp2Px(context, 14)));
        }
        mRecyclerView.setAdapter(mModuleEditSelectAdapter);
    }

    public synchronized void playCheckTimeLine(final long startTime, final long endTime) {
        HVETimeLine timeLine = mEditor.getTimeLine();
        if (timeLine == null) {
            SmartLog.e(TAG, "checkTimeLine failure, mTimeLine is null");
            return;
        }

        long currentTime = timeLine.getCurrentTime();
        if (startTime == currentTime) {
            mEditor.playTimeLine(startTime, endTime);
        } else {
            mEditor.seekTimeLine(startTime, () -> mEditor.playTimeLine(startTime, endTime));
        }
    }

    @Override
    protected void initData() {
        mEditViewModel.getUpdata().observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                if (mEditor != null) {
                    mEditor.setDisplay(mVideoLayout);
                    mEditor.setPlayCallback(VideoModuleEditFragment.this);
                    if (mEditor.getTimeLine() != null) {
                        playCheckTimeLine(mStartTime, mTimeLine.getEndTime());
                    }
                }
            }
        });
        mEditViewModel.getBackState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    if (mEditor != null && mStartTime >= 0) {
                        mEditor.setDisplay(mVideoLayout);
                        mEditor.setPlayCallback(VideoModuleEditFragment.this);
                        if (mEditor.getTimeLine() != null) {
                            mEditor.seekTimeLine(mStartTime);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void initEvent() {
        mVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSeekBarPause = false;
                isPauseTimeLine = isTimelinePlaying;
                setPlayState(isTimelinePlaying, mCurrentTime);
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavDestination navDestination = mNavController.getCurrentDestination();
                if (navDestination != null && navDestination.getId() == R.id.videoModuleEditFragment) {
                    showGiveUpDialog();
                }
            }
        });
        mCloseIcon.setOnClickListener(v -> {
            NavDestination navDestination = mNavController.getCurrentDestination();
            if (navDestination != null && navDestination.getId() == R.id.videoModuleEditFragment) {
                showGiveUpDialog();
            }
        });
        final boolean[] isEditorPlaying = new boolean[1];
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTvRunningTime.setText(getStandardTime(i));
                if (isSeekBarTouch && mEditor != null) {
                    mEditor.seekTimeLine(i);
                    mCurrentTime = i;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarTouch = true;
                isSeekBarPause = true;
                isEditorPlaying[0] = isTimelinePlaying;
                if (mEditor != null) {
                    mEditor.pauseTimeLine();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarTouch = false;
                if (mEditor != null) {
                    if (isEditorPlaying[0]) {
                        playCheckTimeLine(mCurrentTime, mEditor.getTimeLine().getEndTime());
                    } else {
                        mEditor.pauseTimeLine();
                        isSeekBarPause = false;
                    }
                    setPlayState(!isEditorPlaying[0], mCurrentTime);
                }
            }
        });

        OnClickRepeatedListener exportListener = new OnClickRepeatedListener(v -> {
            setPlayState(true, mCurrentTime);
            simpleExport();
        });
        mTvExport.setOnClickListener(exportListener);
        mModuleEditSelectAdapter.setOnItemClickListener(position -> {
            isPauseTimeLine = true;
            mSelectPosition = position;

            if (getLaneOrderCount() > 1) {
                mModuleEditSelectAdapter.setSelectImageVisibility(true);
            }
            if (mEditor == null) {
                SmartLog.e(TAG, "EditSelect OnItemClick mEditor null return");
                return;
            }

            updateSelectItemFrame(position);
            this.position = position;

            HVETemplateElement hveEditableElement = mTemplateResource.getEditableElements().get(position);

            mStartTime = hveEditableElement.getDisplayStartTime();
            mCurrentTime = mStartTime;
            mEditor.seekTimeLine(mStartTime, () -> {
                seekBar.setProgress((int) mStartTime);
                setPlayState(true, mCurrentTime);
            });
        });
    }

    private void updateSelectItemFrame(int position) {
        int oldIndex = mModuleEditSelectAdapter.getCurrentIndex();
        mModuleEditSelectAdapter.setCurrentIndex(position);

        mModuleEditSelectAdapter.notifyItemChanged(oldIndex);
        mModuleEditSelectAdapter.notifyItemChanged(position);
    }

    @Override
    protected int setViewLayoutEvent() {
        return NOMERA_HEIGHT;
    }

    private void simpleExport() {
        SmartLog.i(TAG, "export video");
        if (mVideoLayout == null || mActivity == null) {
            SmartLog.w(TAG, "mVirtualVideo is null or activity is null");
            return;
        }
        if (mEditor == null) {
            SmartLog.e(TAG, "simpleExport mEditor null return");
            return;
        }

        Intent intent = new Intent(context, VideoExportActivity.class);
        intent.putExtra(VideoExportActivity.EXPORT_TYPE_TAG, VideoExportActivity.TEMPLATE_EXPORT_TYPE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            intent.putExtra(VideoExportActivity.TEMPLATE_ID, bundle.getString(VideoExportActivity.TEMPLATE_ID));
            intent.putExtra(VideoExportActivity.TEMPLATE_NAME, bundle.getString("name"));
            intent.putExtra(TemplateDetailActivity.TEMPLATE_TYPE,
                bundle.getString(TemplateDetailActivity.TEMPLATE_TYPE));
            intent.putExtra(VideoExportActivity.EDITOR_UUID, mEditor.getUuid());
            mSource = bundle.getString("source");
            intent.putExtra("source", mSource);
        }

        if (mEditor == null || mEditor.getTimeLine() == null) {
            return;
        }
        HVETimeLine timeLine = mEditor.getTimeLine();

        HVEAsset asset = timeLine.getCoverImage();
        if (asset != null) {
            intent.putExtra(VideoExportActivity.COVER_URL, asset.getPath());
        }

        ModuleSelectManager.getInstance().destroy();
        startActivityForResult(intent, EXPORT_ACTIVITY);
    }

    private boolean currentTimeIsInAsset(List<HVETemplateElement> dataAssets, int position) {
        if (dataAssets == null || position >= dataAssets.size() || position < 0) {
            SmartLog.e(TAG, "input params is null");
            return false;
        }

        long currentStartTime = dataAssets.get(position).getStartTime();
        long currentEndTime = dataAssets.get(position).getEndTime();
        if (mEditor == null || mEditor.getTimeLine() == null) {
            return true;
        }
        return currentStartTime <= mEditor.getTimeLine().getCurrentTime()
            && mEditor.getTimeLine().getCurrentTime() <= currentEndTime;
    }

    private void setPlayState(boolean isPlaying, long currentTime) {
        Message msg = new Message();
        msg.what = PLAY_STATE;
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_TIME, (int) currentTime);
        bundle.putBoolean(IS_PLAYING, isPlaying);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case REFRESH:
                    if (isEditorReady) {
                        completeCompose();
                        removeMessages(REFRESH);
                    }
                    this.sendEmptyMessageDelayed(REFRESH, 100);
                    break;
                case TAB_MATERIALS:
                    if (mModuleEditSelectAdapter != null) {
                        int oldIndex = mModuleEditSelectAdapter.getCurrentIndex();
                        if (oldIndex != msg.arg1
                            && !currentTimeIsInAsset(mTemplateResource.getEditableElements(), oldIndex)) {
                            mModuleEditSelectAdapter.setCurrentIndex(msg.arg1);
                            mModuleEditSelectAdapter.notifyItemChanged(oldIndex);
                            mModuleEditSelectAdapter.notifyItemChanged(msg.arg1);

                            if (getLaneOrderCount() <= 1) {
                                mCenterLayoutManager.smoothScrollToPosition(mRecyclerView, new RecyclerView.State(),
                                    msg.arg1);
                            }
                        }
                    }
                    break;
                case PLAY_STATE:
                    boolean isPlaying = msg.getData().getBoolean(IS_PLAYING, false);
                    if (mEditor == null) {
                        SmartLog.e(TAG, "mEditor is null.");
                        return;
                    }

                    if (isPlaying) {
                        mEditor.pauseTimeLine();
                        mPlayStateIcon.setVisibility(View.VISIBLE);
                    } else {
                        showSelectFrameOnPlay(true);
                        if (mEditor.getTimeLine() == null) {
                            break;
                        }
                        long startTime = msg.getData().getInt(CURRENT_TIME, 0);
                        long endTime = mEditor.getTimeLine().getEndTime();
                        if (endTime - startTime < HuaweiVideoEditor.TIMER_PLAY_PERIOD) {
                            mEditor.seekTimeLine(0, new HuaweiVideoEditor.SeekCallback() {
                                @Override
                                public void onSeekFinished() {
                                    mEditor.playTimeLine(0, mEditor.getTimeLine().getEndTime());
                                }
                            });
                        } else {
                            playCheckTimeLine(msg.getData().getInt(CURRENT_TIME, 0),
                                mEditor.getTimeLine().getEndTime());
                        }

                        mPlayStateIcon.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void compose() {
        showComposeDialog();
        handler.sendEmptyMessage(REFRESH);
    }

    private void showComposeDialog() {
        mComposeDialog = new ModuleComposeDialog(context, context.getString(R.string.module_compose));
        mComposeDialog.setOnDismissListener(dialog1 -> {
            if (mActivity == null) {
                return;
            }
            mComposeDialog.hideLoading();
            WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
            params.alpha = 1f;
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mActivity.getWindow().setAttributes(params);
        });
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = 0.6f;
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mActivity.getWindow().setAttributes(params);
        mComposeDialog.show();
        mComposeDialog.showLoading();
        WindowManager.LayoutParams lp = mComposeDialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mComposeDialog.getWindow().setAttributes(lp);

        mComposeDialog.setOnCancelClickListener(() -> {

        });
    }

    private void completeCompose() {
        if (mComposeDialog != null && mComposeDialog.isShowing()) {
            Context context = ((ContextWrapper) mComposeDialog.getContext()).getBaseContext();
            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    SmartLog.i(TAG, "project is ready");
                    mComposeDialog.dismiss();
                }
            } else {
                SmartLog.i(TAG, "project is ready");
                mComposeDialog.dismiss();
            }
        }
    }

    private void showSelectFrameOnPlay(boolean isNotifyItem) {
        long laneOrderCount = getLaneOrderCount();
        if (laneOrderCount > 1) {
            mModuleEditSelectAdapter.setSelectImageVisibility(false);
            if (isNotifyItem) {
                mModuleEditSelectAdapter.notifyItemChanged(mSelectPosition);
            } else {
                mModuleEditSelectAdapter.notifyDataSetChanged();
            }
        }
    }

    private long getLaneOrderCount() {
        if (mTemplateResource == null || mTemplateResource.getEditableElements() == null) {
            return 0;
        }

        List<HVETemplateElement> hveEditableElements = mTemplateResource.getEditableElements();
        if (hveEditableElements == null || hveEditableElements.isEmpty()) {
            return 0;
        }

        List<Integer> laneOrders = new ArrayList<>();
        for (HVETemplateElement editableElement : hveEditableElements) {
            laneOrders.add(editableElement.getLaneOrder());
        }

        return laneOrders.stream().distinct().count();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_CODE && resultCode == Constant.RESULT_CODE) {
            ModuleSelectManager.getInstance().destroy();
        } else if (requestCode == EXPORT_ACTIVITY && resultCode == RESULT_OK) {
            SmartLog.i(TAG, "export activity is opened.");
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isPauseTimeLine = false;
        SmartLog.d(TAG, " tEST" + " onResume");
        if (mEditor != null) {
            // mCurrentTime = 0;
            if (mVideoLayout.getChildCount() == 0) {
                mEditor.setDisplay(mVideoLayout);
            }
            mEditor.setPlayCallback(this);
        }
        if (seekBar != null) {
            seekBar.setProgress((int) mCurrentTime);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mEditor != null) {
            SmartLog.e(TAG, "mEditor is not null.");
            mEditor.pauseTimeLine();
            isPauseTimeLine = true;
            isTimelinePlaying = false;
        }

        mPlayStateIcon.setVisibility(View.VISIBLE);
        if (seekBar != null) {
            seekBar.setProgress((int) mCurrentTime);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setIsEditorReady(false);
    }

    public void playTimeline(long startTime, List<HVETemplateElement> editableElements) {
        mEditor = mModuleEditViewModel.getEditor();
        Activity activity = getActivity();

        if (mEditor == null && activity != null) {
            mEditor = HuaweiVideoEditor.create(mTemplateResource.getTemplateId(), editableElements);
            try {
                mEditor.initEnvironment();
            } catch (LicenseException e) {
                SmartLog.e(TAG, "mEditor initEnvironment ERROR.");
            }
            setIsEditorReady(true);
            SmartLog.e(TAG, "mEditor is null, create it.");
            mModuleEditViewModel.setEditor(mEditor);
        }

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mEditor == null) {
                    SmartLog.e(TAG, "playTimeline run mEditor null return");
                    return;
                }
                mEditor.setDisplay(mVideoLayout);
                seekBar.setMax((int) getEndTime());
                mTvTotalTime.setText(getStandardTime(getEndTime()));
            }
        });
        mEditor.setPlayCallback(this);
        mTimeLine = mEditor.getTimeLine();
        if (mTimeLine == null) {
            return;
        }

        mModuleEditViewModel.initCoverPath(100, mEditor, mActivity, System.currentTimeMillis() + "cover.jpg",
            new ModuleEditViewModel.OnCoverCallback() {
                @Override
                public void onCoverSuccess() {
                    setIsEditorReady(true);
                    setPlayState(false, startTime);
                }

                @Override
                public void onCoverFail() {
                    SmartLog.e(TAG, "cover init failed.");
                    setIsEditorReady(true);
                }
            });
    }

    private long getEndTime() {
        if (mEditor == null) {
            return 0;
        }

        HVETimeLine timeLine = mEditor.getTimeLine();
        return timeLine == null ? 0 : timeLine.getEndTime();
    }

    private void initCanvas() {
        HVEColor color = new HVEColor(24, 24, 24, 0);
        if (mEditor == null) {
            SmartLog.e(TAG, "init canvas and editor is null.");
        } else {
            mEditor.setBackgroundColor(color);
            addCanvas(mEditor);
        }
    }

    private void addCanvas(HuaweiVideoEditor editor) {
        for (HVEVideoLane videoLane : editor.getTimeLine().getAllVideoLane()) {
            for (HVEAsset hveAsset : videoLane.getAssets()) {
                if (hveAsset instanceof HVEVisibleAsset) {
                    if (((HVEVisibleAsset) hveAsset).getCanvas() == null) {
                        ((HVEVisibleAsset) hveAsset).setCanvas(new HVECanvas(new HVEColor(0, 0, 0, 255)));
                    }
                }
            }
        }
    }

    private void finish() {
        if (SOURCE_HIMOVIE_LOCALVIDEO.equals(mSource)) {
            mActivity.setResult(RESULT_OK);
        } else {
            mActivity.setResult(Constant.RESULT_CODE);
        }
        mActivity.finish();
        if (mEditor != null) {
            mEditor.stopRenderer();
            mEditor.stopEditor();
            mEditor = null;
        }
    }

    @Override
    public void onPlayProgress(long timeStamp) {
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(() -> {
            if (!isPauseTimeLine) {
                isTimelinePlaying = true;
                mPlayStateIcon.setVisibility(View.INVISIBLE);
                SmartLog.i(TAG, "onPlayProgress isTimelinePlaying value is : " + isTimelinePlaying);
                seekBar.setProgress((int) timeStamp);
                mCurrentTime = timeStamp;
                position = getVideoPlayPosition(mTemplateResource, timeStamp);
                Message msg = new Message();
                msg.arg1 = position;
                msg.what = TAB_MATERIALS;
                handler.sendMessage(msg);
            } else {
                SmartLog.i(TAG, "onPlayStopped/onPause");
            }
        });
    }

    public int getVideoPlayPosition(TemplateProjectBean mTemplateResource, long timeStamp) {
        int pos = 0;
        if (mTemplateResource == null) {
            SmartLog.e(TAG, "input template resource is null.");
            return pos;
        }
        List<HVETemplateElement> editableElements = mTemplateResource.getEditableElements();

        if (editableElements == null || editableElements.size() == 0) {
            return pos;
        }
        if (timeStamp <= 0) {
            SmartLog.e(TAG, "input timeStamp small or equal 0.");
            return pos;
        } else if (mTemplateResource != null && timeStamp >= getEndTime()) {
            SmartLog.e(TAG, "input timeStamp large than timeline's endTime.");
            return editableElements.size() - 1;
        }
        long maxEndTime = 0;
        for (int i = 0; i < editableElements.size(); i++) {
            SmartLog.d(TAG, "--->" + i + "startTime：" + editableElements.get(i).getStartTime());
            SmartLog.d(TAG, "--->" + i + "endTime：" + editableElements.get(i).getEndTime());
            if (maxEndTime < editableElements.get(i).getEndTime()) {
                maxEndTime = editableElements.get(i).getEndTime();
            }
            if (timeStamp <= editableElements.get(i).getEndTime()
                && timeStamp >= editableElements.get(i).getStartTime()) {
                SmartLog.d(TAG, "--->" + timeStamp);
                if (i < editableElements.size() - 1
                    && editableElements.get(i).getEndTime() > editableElements.get(i + 1).getStartTime()
                    && timeStamp >= editableElements.get(i + 1).getStartTime()) {
                    return i + 1;
                }
                return i;
            }
        }
        if (timeStamp >= maxEndTime) {
            return editableElements.size() - 1;
        }
        return pos;
    }

    @Override
    public void onPlayStopped() {
        isTimelinePlaying = false;
        if (isSeekBarPause) {
            return;
        }
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPlayStateIcon.setVisibility(View.VISIBLE);
            }
        });
        SmartLog.i(TAG, "onPlayStopped isTimelinePlaying value is : " + isTimelinePlaying);
    }

    @Override
    public void onPlayFinished() {
        mActivity.runOnUiThread(() -> {
            isTimelinePlaying = false;
            mCurrentTime = 0;
            if (mEditor != null) {
                mEditor.seekTimeLine(mCurrentTime);
            }

            seekBar.setProgress((int) mCurrentTime);
            position = 0;
            Message msg = new Message();
            msg.arg1 = position;
            msg.what = TAB_MATERIALS;
            handler.sendMessage(msg);
            setPlayState(!isTimelinePlaying, mCurrentTime);
        });
    }

    @Override
    public void onPlayFailed() {
        isTimelinePlaying = false;
        setPlayState(true, mCurrentTime);
    }

    private void setRYCenter(int num, int itemWight, View view) {
        int interval = ScreenUtil.dp2px(14);
        int intervalAA = ScreenUtil.dp2px(16);
        int totalHasWight = SizeUtils.screenWidth(context) - num * (itemWight + interval);
        if (totalHasWight > 0 && (totalHasWight - interval) > 0 && (totalHasWight - interval) / 2 > intervalAA) {
            setViewMargin(view, (totalHasWight - interval) / 2 + interval, ScreenUtil.dp2px(24), 0, 0);
        } else {
            setViewMargin(view, ScreenUtil.dp2px(16), ScreenUtil.dp2px(24), 0, 0);
        }
    }

    public static <T> T getLayoutParams(View view, Class<T> clazz) {
        if (null == view || null == clazz) {
            return null;
        }

        Object layoutParams = view.getLayoutParams();
        if (clazz.isInstance(layoutParams)) {
            return (T) layoutParams;
        }

        return null;
    }

    public static void setViewMargin(View view, int start, int top, int end, int bottom) {
        ViewGroup.MarginLayoutParams lp = getLayoutParams(view, ViewGroup.MarginLayoutParams.class);
        if (lp != null) {
            int oldStart = lp.getMarginStart();
            int oldTop = lp.topMargin;
            int oldEnd = lp.getMarginEnd();
            int oldBottom = lp.bottomMargin;

            if (oldStart != start || oldTop != top || oldEnd != end || oldBottom != bottom) {
                lp.setMarginStart(start);
                lp.topMargin = top;
                lp.setMarginEnd(end);
                lp.bottomMargin = bottom;
                view.setLayoutParams(lp);
            }
        }
    }

    private void showGiveUpDialog() {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            return;
        }
        CommonBottomDialog dialog = new CommonBottomDialog(context);
        String giveUpEdit = "";
        String giveUpYes = "";
        String exportCancel = "";

        Application application = fragmentActivity.getApplication();
        if (application != null) {
            giveUpEdit = application.getString(R.string.is_give_up_edit_t1);
        }
        Context context = getContext();
        if (context != null) {
            giveUpYes = context.getString(R.string.is_give_up_yes_t);
            exportCancel = context.getString(R.string.video_edit_export_cancel);
        }

        dialog.show(giveUpEdit, giveUpYes, exportCancel);
        dialog.setOnDialogClickLister(new CommonBottomDialog.OnDialogClickLister() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onAllowClick() {
                finish();
            }
        });
    }

}
