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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.sdk.ai.HVEVideoSelection;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEAudioAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEStickerAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEWordAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEAudioLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.lane.HVELane;
import com.huawei.hms.videoeditor.sdk.lane.HVEStickerLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.tools.EditorRuntimeException;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SPManager;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.dialog.LoadingDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.huawei.hms.videoeditor.sdk.asset.HVEAsset.HVEAssetType.STICKER;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel.MUSIC_URI_INFO;

public class MenuViewModel extends AndroidViewModel {
    private static final String TAG = "MenuViewModel";

    private static final long VIDEO_DURATION = 3000;

    private EditPreviewViewModel mEditPreviewViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private HVEVideoSelection hveVideoSelection;

    public MutableLiveData<Boolean> isShowMenuPanel = new MutableLiveData<>();

    private MutableLiveData<Integer> videoSelectionEnter = new MutableLiveData<>();

    private MutableLiveData<Integer> videoSelectionFinish = new MutableLiveData<>();

    private int mCopyTextAndStickerDistance;

    public MenuViewModel(@NonNull Application application) {
        super(application);
        this.mCopyTextAndStickerDistance = SizeUtils.dp2Px(application, 5);
    }

    public void setEditPreviewViewModel(EditPreviewViewModel mEditPreviewViewModel) {
        this.mEditPreviewViewModel = mEditPreviewViewModel;
    }

    public void setMaterialEditViewModel(MaterialEditViewModel mMaterialEditViewModel) {
        this.mMaterialEditViewModel = mMaterialEditViewModel;
    }

    private HuaweiVideoEditor getEditor() {
        return EditorManager.getInstance().getEditor();
    }

    public void pauseTimeLine() {
        if (getEditor() == null) {
            return;
        }
        getEditor().pauseTimeLine();
    }

    public void setSelectedUUID(String choiceViewUUID) {
        mEditPreviewViewModel.setSelectedUUID(choiceViewUUID);
    }

    public MutableLiveData<Integer> getVideoSelectionEnter() {
        return videoSelectionEnter;
    }

    public void setVideoSelectionEnter(int videoSelectionEnter) {
        this.videoSelectionEnter.postValue(videoSelectionEnter);
    }

    public MutableLiveData<Integer> getVideoSelectionFinish() {
        return videoSelectionFinish;
    }

    public void setVideoSelectionFnish(int videoSelectionFinish) {
        this.videoSelectionFinish.postValue(videoSelectionFinish);
    }

    public void initVideoSelection(HVEAIInitialCallback callback) {
        if (hveVideoSelection == null) {
            hveVideoSelection = new HVEVideoSelection();
        }
        hveVideoSelection.initVideoSelectionEngine(callback);
    }

    private HVEAsset addVideo(HVEVideoLane videoLane, MediaData data, long startTime, boolean isVideoSelection) {
        HVEAsset asset;
        try {
            if (data.getType() == MediaData.MEDIA_VIDEO) {
                asset = videoLane.appendVideoAsset(data.getPath(), (int) startTime, data.getDuration(), data.getWidth(),
                    data.getHeight());
                if (isVideoSelection) {
                    setVideoSelectionFnish(0);
                    hveVideoSelection.getHighLight(data.getPath(), VIDEO_DURATION, start -> {
                        setVideoSelectionFnish(1);
                        if (start < 0) {
                            SmartLog.e(TAG, "illegal trimIn");
                            return;
                        }
                        long trimOut = data.getDuration() - start - VIDEO_DURATION;
                        videoLane.cutAsset(asset.getIndex(), start, HVELane.HVETrimType.TRIM_IN);
                        videoLane.cutAsset(asset.getIndex(), trimOut, HVELane.HVETrimType.TRIM_OUT);
                        mEditPreviewViewModel.updateDuration();
                        mEditPreviewViewModel.refreshAssetList();
                        hveVideoSelection.releaseVideoSelectionEngine();
                    });
                }
            } else {
                asset = videoLane.appendImageAsset(data.getPath(), (int) startTime);
            }
        } catch (EditorRuntimeException e) {
            SmartLog.e(TAG, "isVideo: " + e.getMessage());
            return null;
        }
        return asset;
    }

    public List<HVEAsset> addVideos(List<MediaData> dataList, boolean isVideoSelection) {
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        List<HVEAsset> result = new ArrayList<>();
        if (timeLine == null || videoLane == null) {
            return Collections.EMPTY_LIST;
        }
        long currentTime = timeLine.getCurrentTime();
        long startTime;
        HVEAsset mainLaneAsset = mEditPreviewViewModel.getMainLaneAsset();
        if (mainLaneAsset == null) {
            int index = videoLane.getAssets().size() - 1;
            if (index < 0) {
                return Collections.EMPTY_LIST;
            }
            mainLaneAsset = videoLane.getAssets().get(index);
        }
        startTime = mainLaneAsset.getEndTime();

        for (int i = dataList.size() - 1; i >= 0; i--) {
            HVEAsset asset = addVideo(videoLane, dataList.get(i), startTime, isVideoSelection);
            if (asset instanceof HVEVisibleAsset) {
                cutAssetNoSeekTimeLine(dataList.get(i), (HVEVisibleAsset) asset);
            }
            result.add(asset);
            if (asset != null) {
                new Handler().postDelayed(() -> mEditPreviewViewModel.setSelectedUUID(asset.getUuid()), 5);
            }
        }
        SmartLog.d(TAG, "Add asset to scroll, currentTime " + currentTime + " startTime " + startTime);
        mEditPreviewViewModel.updateDuration();
        mEditPreviewViewModel.refreshAssetList();
        return result;
    }

    public void cutAssetNoSeekTimeLine(MediaData mediaData, HVEVisibleAsset mHveAsset) {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        if (mHveAsset == null || mediaData == null) {
            return;
        }
        mHveAsset.setRotation(mediaData.getRotation());
        if (mediaData.isMirrorStatus()) {
            mHveAsset.setHorizontalMirrorState(mediaData.isMirrorStatus());
        }
        if (mediaData.isVerticalMirrorStatus()) {
            mHveAsset.setVerticalMirrorState(mediaData.isVerticalMirrorStatus());
        }

        if (mediaData.getCutTrimIn() != 0 || mediaData.getCutTrimOut() != 0) {
            timeLine.getVideoLane(mHveAsset.getLaneIndex())
                .cutAsset(mHveAsset.getIndex(), mediaData.getCutTrimIn(), HVELane.HVETrimType.TRIM_IN);
            timeLine.getVideoLane(mHveAsset.getLaneIndex())
                .cutAsset(mHveAsset.getIndex(), mediaData.getCutTrimOut(), HVELane.HVETrimType.TRIM_OUT);
            mEditPreviewViewModel.updateDuration();
        }
    }

    public void deleteVideo(HVEAsset asset) {
        if (asset == null) {
            return;
        }
        if (EditorManager.getInstance().getEditor() == null || EditorManager.getInstance().getTimeLine() == null) {
            return;
        }
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (videoLane == null) {
            return;
        }
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        mEditor.pauseTimeLine();
        boolean isSuccess = videoLane.removeAsset(asset.getIndex());
        if (isSuccess) {
            mEditPreviewViewModel.reloadMainLane();
            mEditPreviewViewModel.setCurrentTimeLine(asset.getStartTime());
        }
    }

    public void splitAsset() {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        long splitPoint = 0;
        long currentTime = timeLine.getCurrentTime();

        SmartLog.i(TAG, "MenuViewModel: " + this + "mEditPreviewViewModel: " + mEditPreviewViewModel);
        HVEAsset asset = mEditPreviewViewModel.getMainLaneAsset();
        if (asset == null || mEditPreviewViewModel.getCurrentTime().getValue() == null
            || currentTime - asset.getStartTime() < EditPreviewViewModel.MIN_DURATION
            || asset.getEndTime() - currentTime < EditPreviewViewModel.MIN_DURATION) {
            ToastWrapper.makeText(getApplication(), getApplication().getString(R.string.nodivision), Toast.LENGTH_SHORT)
                .show();
            return;
        }
        splitPoint = currentTime - asset.getStartTime();
        HVEVideoLane hveVideoLane = EditorManager.getInstance().getMainLane();
        if (splitPoint == 0 || hveVideoLane == null) {
            return;
        }

        boolean isSuccess = hveVideoLane.splitAsset(asset.getIndex(), splitPoint);
        if (isSuccess) {
            mEditPreviewViewModel.setSelectedUUID("");
            mEditor.seekTimeLine(mEditPreviewViewModel.getSeekTime(), new HuaweiVideoEditor.SeekCallback() {
                public void onSeekFinished() {
                    mEditPreviewViewModel.refreshAssetList();
                }
            });
        }
        mEditPreviewViewModel.updateDuration();
    }

    public void replaceMainLaneAsset(String path, long trimIn, long trimOut) {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        HVEAsset asset = mEditPreviewViewModel.getMainLaneAsset();
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (asset == null || videoLane == null) {
            ToastWrapper
                .makeText(getApplication(), getApplication().getString(R.string.replacefailed), Toast.LENGTH_SHORT)
                .show();
            return;
        }
        boolean isSuccess = videoLane.replaceAssetPath(path, asset.getIndex(), trimIn, trimOut);
        if (isSuccess) {
            mEditor.seekTimeLine(timeLine.getCurrentTime(), () -> {
                mEditPreviewViewModel.refreshAssetList();
                setSelectedUUID("");
            });
        } else {
            ToastWrapper
                .makeText(getApplication(), getApplication().getString(R.string.replacefailed), Toast.LENGTH_SHORT)
                .show();
        }
    }

    public void replacePipAsset(String path, long trimIn, long trimOut) {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        HVEAsset asset = mEditPreviewViewModel.getSelectedAsset();
        HVELane selectedLane = mEditPreviewViewModel.getSelectedLane();
        if (asset == null || selectedLane == null) {
            ToastWrapper
                .makeText(getApplication(), getApplication().getString(R.string.replacefailed), Toast.LENGTH_SHORT)
                .show();
            return;
        }
        if (selectedLane instanceof HVEVideoLane) {
            boolean isSuccess = ((HVEVideoLane) selectedLane).replaceAssetPath(path, asset.getIndex(), trimIn, trimOut);
            if (isSuccess) {
                mEditPreviewViewModel.refreshAssetList();
                mEditPreviewViewModel.updateDuration();
                setSelectedUUID("");
            } else {
                ToastWrapper
                    .makeText(getApplication(), getApplication().getString(R.string.replacefailed), Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }

    public void copyMainLaneAsset() {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return;
        }
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (videoLane == null) {
            SmartLog.d(TAG, "videoLane is null");
            return;
        }
        HVEAsset asset;
        HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
        HVEAsset timeLineAsset = mEditPreviewViewModel.getMainLaneAsset();
        if (selectedAsset == null && timeLineAsset == null) {
            return;
        }

        if (selectedAsset == null) {
            asset = timeLineAsset;
        } else {
            asset = selectedAsset;
        }
        HVEAsset newAsset = timeLine.copyAndInsertAsset(asset, videoLane.getIndex(), asset.getEndTime());
        if (newAsset != null) {
            mEditPreviewViewModel.refreshAssetList();
            setSelectedUUID(newAsset.getUuid());
            mEditPreviewViewModel.updateDuration();
        } else {
            ToastWrapper.makeText(getApplication(), getApplication().getString(R.string.copyfailed), Toast.LENGTH_SHORT)
                .show();
        }
    }

    public void copyOtherLaneAsset() {
        HVEAsset asset = mEditPreviewViewModel.getSelectedAsset();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null || asset == null) {
            return;
        }
        long startTime = timeLine.getCurrentTime();
        long endTime = startTime + asset.getDuration();
        HVEVideoLane lane = LaneSizeCheckUtils.getPipFreeLan(mEditor, startTime, endTime, getApplication());
        if (lane == null) {
            ToastWrapper
                .makeText(getApplication(), getApplication().getString(R.string.pip_lane_out_of_size),
                    Toast.LENGTH_SHORT)
                .show();
            return;
        }
        HVEAsset newAsset = timeLine.copyAndInsertAsset(asset, lane.getIndex(), startTime);
        if (newAsset != null) {
            setSelectedUUID(newAsset.getUuid());
            mEditPreviewViewModel.updateDuration();
            getEditor().seekTimeLine(timeLine.getCurrentTime(), () -> {
                mMaterialEditViewModel.refresh();
            });
        } else {
            ToastWrapper.makeText(getApplication(), getApplication().getString(R.string.copyfailed), Toast.LENGTH_SHORT)
                .show();
        }
    }

    public HVEEffect addTransitionEffect(CloudMaterialBean content, long duration, boolean preview) {
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (videoLane == null) {
            return null;
        }
        int mTransIndex = mEditPreviewViewModel.getTransLengthByIndex();
        if (mTransIndex == -1) {
            return null;
        }
        try {
            HVEEffect effect = videoLane.bindTransitionEffect(
                new HVEEffect.Options(content.getName(), content.getId(), content.getLocalPath()), mTransIndex,
                duration);
            if (effect != null && preview) {
                mEditPreviewViewModel.playTimeLine(Math.max(0, effect.getStartTime() - 300), effect.getEndTime() + 300);
            }
            return effect;
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
        }
        return null;
    }

    public boolean applyTransitionToAll(CloudMaterialBean content) {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return false;
        }
        long durationTime = content.getDuration();
        HVEVideoLane videoLane = timeLine.getVideoLane(0);
        if (videoLane == null) {
            return false;
        }
        List<HVEAsset> assetList = videoLane.getAssets();
        if (assetList == null || assetList.size() <= 2) {
            return false;
        }
        for (int i = 0; i < assetList.size() - 1; i++) {
            if (assetList.get(i).getDuration() < 300 && assetList.get(i + 1).getDuration() < 300) {
                continue;
            }
            List<Long> list = new ArrayList<>();
            list.add(assetList.get(i).getDuration());
            list.add(assetList.get(i + 1).getDuration());
            long duration = Collections.min(list) / 2;
            if (durationTime == 500) {
                if (duration > 1000) {
                    duration = 500;
                }
            } else {
                duration = durationTime;
            }
            videoLane.bindTransitionEffect(
                new HVEEffect.Options(content.getName(), content.getId(), content.getLocalPath()), i, duration);
        }
        return true;
    }

    public boolean removeAllTransition() {
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (videoLane == null) {
            return false;
        }
        while (videoLane.getTransitionEffects() != null && videoLane.getTransitionEffects().size() > 0) {
            videoLane.removeTransitionEffect(0);
        }
        return true;
    }

    public void addGraffiti(String path, String id) {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        HVEStickerAsset asset = addGraffiti(path, timeLine.getCurrentTime(), timeLine.getCurrentTime() + 3000);
        if (asset != null) {
            setSelectedUUID(asset.getUuid());
        }
    }

    public HVEStickerAsset addGraffiti(String path, long startTime, long endTime) {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return null;
        }

        HVEStickerLane stickerLane = LaneSizeCheckUtils.getStickerFreeLan(mEditor, startTime, endTime);
        HVEStickerAsset asset = stickerLane.appendStickerAsset(path, startTime, endTime - startTime);
        mEditor.seekTimeLine(timeLine.getCurrentTime(), new HuaweiVideoEditor.SeekCallback() {

            public void onSeekFinished() {
                int width = mEditor.getCanvasWidth();
                int height = mEditor.getCanvasHeight();
                asset.setSize(width, height);
                mEditPreviewViewModel.updateDuration();
            }
        });
        return asset;
    }

    public void copySticker() {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }

        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        if (hveAsset == null) {
            return;
        }
        if (hveAsset.getType() != HVEAsset.HVEAssetType.STICKER) {
            return;
        }

        long startTime = timeLine.getCurrentTime();
        long endTime = startTime + hveAsset.getDuration();
        HVEStickerLane stickerLane = LaneSizeCheckUtils.getStickerFreeLan(mEditor, startTime, endTime);

        HVEAsset newAsset = timeLine.copyAndInsertAsset(hveAsset, stickerLane.getIndex(), timeLine.getCurrentTime());
        if (newAsset == null) {
            return;
        }
        if (newAsset instanceof HVEStickerAsset) {
            HVEStickerAsset hveStickerAsset = (HVEStickerAsset) newAsset;
            HVEPosition2D hvePosition2D = hveStickerAsset.getPosition();
            if (hvePosition2D != null) {
                hveStickerAsset.setPosition(hvePosition2D.xPos + mCopyTextAndStickerDistance,
                    hvePosition2D.yPos + mCopyTextAndStickerDistance);
            }
        }
        mEditor.seekTimeLine(startTime, () -> {
            setSelectedUUID(newAsset.getUuid());
            mEditPreviewViewModel.updateDuration();
        });
    }

    public void deleteAsset() {
        HVEAsset asset = mEditPreviewViewModel.getSelectedAsset();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        if (asset == null) {
            return;
        }
        deleteAsset(asset);
        setSelectedUUID("");
        mEditPreviewViewModel.updateDuration();
        mEditor.seekTimeLine(timeLine.getCurrentTime());
    }

    public void deleteAsset(HVEAsset asset) {
        SmartLog.d(TAG, "deleteAsset:" + asset);
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null || asset == null) {
            return;
        }
        HVEStickerLane lane = timeLine.getStickerLane(asset.getLaneIndex());
        if (lane == null) {
            return;
        }

        lane.removeAsset(asset.getIndex());
        mEditPreviewViewModel.updateDuration();
    }

    public void deleteEffect(HVEEffect effect) {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null || effect == null) {
            return;
        }
        HVEEffectLane lane = timeLine.getEffectLane(effect.getLaneIndex());
        if (lane == null) {
            return;
        }
        lane.removeEffect(effect.getIndex());
        mEditor.seekTimeLine(timeLine.getCurrentTime());
    }

    public void copyAudio() {
        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null || hveAsset == null) {
            return;
        }
        if (hveAsset.getType() != HVEAsset.HVEAssetType.AUDIO) {
            return;
        }
        HVEAudioAsset asset = (HVEAudioAsset) hveAsset;
        long startTime = timeLine.getCurrentTime();
        long endTime = startTime + asset.getDuration();
        HVEAudioLane audioLane = LaneSizeCheckUtils.getAudioFreeLan(mEditor, startTime, endTime, getApplication());
        if (audioLane == null) {
            ToastWrapper
                .makeText(getApplication(), getApplication().getString(R.string.audio_lane_out_of_size),
                    Toast.LENGTH_SHORT)
                .show();
            return;
        }

        HVEAsset newAsset = timeLine.copyAndInsertAsset(hveAsset, audioLane.getIndex(), timeLine.getCurrentTime());
        if (newAsset == null) {
            return;
        }

        mEditor.seekTimeLine(startTime, new HuaweiVideoEditor.SeekCallback() {

            public void onSeekFinished() {
                setSelectedUUID(newAsset.getUuid());
            }
        });
    }

    public void deleteAudio() {
        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null || hveAsset == null) {
            return;
        }
        if (hveAsset.getType() != HVEAsset.HVEAssetType.AUDIO) {
            return;
        }
        HVEAudioLane lane = timeLine.getAudioLane(hveAsset.getLaneIndex());
        if (lane == null) {
            return;
        }

        lane.removeAsset(hveAsset.getIndex());
        setSelectedUUID("");

    }

    public void splitAudio() {
        long splitPoint = 0;
        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null || hveAsset == null) {
            return;
        }

        String uriString = SPManager.get(MUSIC_URI_INFO, getApplication()).getString(hveAsset.getPath(), "");

        if (hveAsset.getType() != HVEAsset.HVEAssetType.AUDIO) {
            return;
        }
        if (hveAsset.getStartTime() == timeLine.getCurrentTime()
            || hveAsset.getEndTime() == timeLine.getCurrentTime()) {
            return;
        }
        if ((timeLine.getCurrentTime() - hveAsset.getStartTime() < EditPreviewViewModel.MIN_DURATION
            || hveAsset.getEndTime() - timeLine.getCurrentTime() < EditPreviewViewModel.MIN_DURATION)) {
            ToastWrapper.makeText(getApplication(), getApplication().getString(R.string.nodivision), Toast.LENGTH_SHORT)
                .show();
            return;
        }
        if (timeLine.getCurrentTime() > hveAsset.getStartTime() && timeLine.getCurrentTime() < hveAsset.getEndTime()) {
            splitPoint = timeLine.getCurrentTime() - hveAsset.getStartTime();
        }
        if (splitPoint == 0) {
            return;
        }
        HVEAudioLane audioLane = timeLine.getAudioLane(hveAsset.getLaneIndex());
        if (audioLane == null) {
            return;
        }
        boolean isSuccess = audioLane.splitAsset(hveAsset.getIndex(), splitPoint);
        if (isSuccess) {

            if (audioLane.getAssets().size() > hveAsset.getIndex() + 1) {
                List<HVEAsset> assets = audioLane.getAssets();
                if (assets == null) {
                    return;
                }
                HVEAsset asset = assets.get(hveAsset.getIndex() + 1);
                if (asset == null) {
                    return;
                }
                SPManager.get(MUSIC_URI_INFO, getApplication()).put(asset.getPath(), uriString);
                mEditPreviewViewModel.setSelectedUUID(asset.getUuid());
            }
        }
    }

    public HVEAsset addPip(MediaData data) {
        if (data == null) {
            return null;
        }
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return null;
        }
        HVEAsset asset;
        HVEVideoLane videoLane;
        try {
            if (data.getType() == MediaData.MEDIA_VIDEO) {
                videoLane = LaneSizeCheckUtils.getPipFreeLan(mEditor, timeLine.getCurrentTime(),
                    timeLine.getCurrentTime() + data.getDuration(), getApplication());
                if (videoLane == null) {
                    ToastWrapper
                        .makeText(getApplication(), getApplication().getString(R.string.pip_lane_out_of_size),
                            Toast.LENGTH_SHORT)
                        .show();
                    return null;
                }
                asset = videoLane.appendVideoAsset(data.getPath(), (int) timeLine.getCurrentTime(), data.getDuration(),
                    data.getWidth(), data.getHeight());
            } else {
                videoLane = LaneSizeCheckUtils.getPipFreeLan(mEditor, timeLine.getCurrentTime(),
                    timeLine.getCurrentTime() + 3000, getApplication());
                if (videoLane == null) {
                    ToastWrapper
                        .makeText(getApplication(), getApplication().getString(R.string.pip_lane_out_of_size),
                            Toast.LENGTH_SHORT)
                        .show();
                    return null;
                }
                HVEImageAsset imageAsset = videoLane.appendImageAsset(data.getPath(), timeLine.getCurrentTime());
                if (imageAsset == null) {
                    return null;
                }
                asset = imageAsset;
            }
        } catch (EditorRuntimeException e) {
            SmartLog.e(TAG, "isVideo: " + e.getMessage());
            return null;
        }
        if (asset == null) {
            return null;
        }
        setSelectedUUID(asset.getUuid());
        mEditPreviewViewModel.updateDuration();
        return asset;
    }

    public void deletePip() {
        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null || hveAsset == null) {
            return;
        }
        if (hveAsset.getType() != HVEAsset.HVEAssetType.VIDEO && hveAsset.getType() != HVEAsset.HVEAssetType.IMAGE) {
            return;
        }
        HVEVideoLane lane = timeLine.getVideoLane(hveAsset.getLaneIndex());
        if (lane == null) {
            return;
        }
        lane.removeAsset(hveAsset.getIndex());
        setSelectedUUID("");
        mEditPreviewViewModel.updateDuration();
        mEditor.seekTimeLine(timeLine.getCurrentTime());
    }

    public void deleteSpecial() {
        HVEEffect effect = mEditPreviewViewModel.getSelectedEffect();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null || effect == null) {
            return;
        }
        deleteEffect(effect);
        setSelectedUUID("");
        mEditor.seekTimeLine(timeLine.getCurrentTime());
    }

    public void deleteFilter(int id) {
        HVEEffect effect = mEditPreviewViewModel.getSelectedEffect();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null || effect == null) {
            return;
        }
        deleteEffect(effect);
        setSelectedUUID("");
        mEditor.seekTimeLine(timeLine.getCurrentTime());
    }

    public void deleteAI() {
        HVEEffect effect = mEditPreviewViewModel.getSelectedEffect();
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null || effect == null) {
            return;
        }
        deleteEffect(effect);
        setSelectedUUID("");
        mEditor.seekTimeLine(timeLine.getCurrentTime());
    }

    public HVEWordAsset addText(String text, long startTime) {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return null;
        }
        long endTime = startTime + 3000;

        HVEStickerLane stickerLane = LaneSizeCheckUtils.getStickerFreeLan(mEditor, startTime, endTime);
        if (stickerLane == null) {
            return null;
        }
        HVEWordAsset wordAsset = addText(stickerLane, text, startTime, endTime - startTime, "");
        mEditPreviewViewModel.updateDuration();
        return wordAsset;
    }

    public HVEWordAsset addText(HVEStickerLane stickerLane, String text, long startTime, long duration, String id) {
        if (stickerLane == null) {
            return null;
        }
        HVEWordAsset wordAsset = stickerLane.appendWord(text, startTime, duration);
        if (wordAsset == null) {
            return null;
        }
        setSelectedUUID(wordAsset.getUuid());
        mEditPreviewViewModel.updateDuration();
        return wordAsset;
    }

    public void splitText() {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        HVEAsset aHveAsset = mEditPreviewViewModel.getSelectedAsset();
        if (aHveAsset == null) {
            return;
        }
        if (aHveAsset.getType() != HVEAsset.HVEAssetType.WORD) {
            return;
        }

        if (aHveAsset.getStartTime() == timeLine.getCurrentTime()
            || aHveAsset.getEndTime() == timeLine.getCurrentTime()) {
            ToastWrapper.makeText(getApplication(), getApplication().getString(R.string.nodivision), Toast.LENGTH_SHORT)
                .show();
            return;
        }

        if ((timeLine.getCurrentTime() - aHveAsset.getStartTime() < EditPreviewViewModel.MIN_DURATION
            || aHveAsset.getEndTime() - timeLine.getCurrentTime() < EditPreviewViewModel.MIN_DURATION)) {
            ToastWrapper.makeText(getApplication(), getApplication().getString(R.string.nodivision), Toast.LENGTH_SHORT)
                .show();
            return;
        }

        long splitPoint = 0;
        if (timeLine.getCurrentTime() > aHveAsset.getStartTime()
            && timeLine.getCurrentTime() < aHveAsset.getEndTime()) {
            splitPoint = timeLine.getCurrentTime() - aHveAsset.getStartTime();
        }
        if (splitPoint == 0) {
            return;
        }
        HVEWordAsset asset = (HVEWordAsset) aHveAsset;
        HVELane lane = mEditPreviewViewModel.getSelectedLane();
        if (lane == null) {
            return;
        }
        boolean success = lane.splitAsset(asset.getIndex(), splitPoint);
        if (success) {

            if (lane.getAssets().size() <= asset.getIndex() + 1) {
                return;
            }
            HVEAsset hveAsset = lane.getAssets().get(asset.getIndex() + 1);
            if (hveAsset == null) {
                return;
            }
            mEditor.seekTimeLine(hveAsset.getStartTime(),
                () -> mEditPreviewViewModel.setSelectedUUID(hveAsset.getUuid()));
        }
    }

    public void splitSticker() {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        HVEAsset bHveAsset = mEditPreviewViewModel.getSelectedAsset();
        if (bHveAsset == null) {
            return;
        }
        if (bHveAsset.getType() != STICKER) {
            return;
        }

        if (bHveAsset.getStartTime() == timeLine.getCurrentTime()
            || bHveAsset.getEndTime() == timeLine.getCurrentTime()) {
            ToastWrapper.makeText(getApplication(), getApplication().getString(R.string.nodivision), Toast.LENGTH_SHORT)
                .show();
            return;
        }

        if ((timeLine.getCurrentTime() - bHveAsset.getStartTime() < EditPreviewViewModel.MIN_DURATION
            || bHveAsset.getEndTime() - timeLine.getCurrentTime() < EditPreviewViewModel.MIN_DURATION)) {
            ToastWrapper.makeText(getApplication(), getApplication().getString(R.string.nodivision), Toast.LENGTH_SHORT)
                .show();
            return;
        }

        long splitPoint = 0;
        if (timeLine.getCurrentTime() > bHveAsset.getStartTime()
            && timeLine.getCurrentTime() < bHveAsset.getEndTime()) {
            splitPoint = timeLine.getCurrentTime() - bHveAsset.getStartTime();
        }
        if (splitPoint == 0) {
            return;
        }
        HVEStickerAsset asset = (HVEStickerAsset) bHveAsset;
        HVELane lane = mEditPreviewViewModel.getSelectedLane();
        if (lane == null) {
            return;
        }
        boolean success = lane.splitAsset(asset.getIndex(), splitPoint);
        if (success) {

            if (lane.getAssets().size() <= asset.getIndex() + 1) {
                return;
            }
            HVEAsset hveAsset = lane.getAssets().get(asset.getIndex() + 1);
            if (hveAsset == null) {
                return;
            }
            mEditor.seekTimeLine(hveAsset.getStartTime(),
                () -> mEditPreviewViewModel.setSelectedUUID(hveAsset.getUuid()));
        }
    }

    public void copyText() {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        if (hveAsset == null) {
            return;
        }
        if (hveAsset.getType() != HVEAsset.HVEAssetType.WORD) {
            return;
        }
        HVEWordAsset asset = (HVEWordAsset) hveAsset;
        long startTime = timeLine.getCurrentTime();
        long endTime = startTime + asset.getDuration();
        HVEStickerLane wordLane = LaneSizeCheckUtils.getStickerFreeLan(mEditor, startTime, endTime);
        if (wordLane == null) {
            return;
        }

        HVEAsset newAsset = timeLine.copyAndInsertAsset(hveAsset, wordLane.getIndex(), timeLine.getCurrentTime());
        if (newAsset == null) {
            return;
        }
        if (newAsset instanceof HVEWordAsset) {
            HVEWordAsset hveStickerAsset = (HVEWordAsset) newAsset;
            HVEPosition2D hvePosition2D = hveStickerAsset.getPosition();
            if (hvePosition2D != null) {
                hveStickerAsset.setPosition(hvePosition2D.xPos + mCopyTextAndStickerDistance,
                    hvePosition2D.yPos + mCopyTextAndStickerDistance);
            }
        }
        mEditor.seekTimeLine(startTime, new HuaweiVideoEditor.SeekCallback() {
            public void onSeekFinished() {
                setSelectedUUID(newAsset.getUuid());
                mEditPreviewViewModel.updateDuration();
            }
        });
    }

    public void deleteText() {
        HuaweiVideoEditor mEditor = EditorManager.getInstance().getEditor();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (mEditor == null || timeLine == null) {
            return;
        }
        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        if (hveAsset == null) {
            return;
        }
        if (hveAsset.getType() != HVEAsset.HVEAssetType.WORD) {
            return;
        }
        HVEStickerLane lane = timeLine.getStickerLane(hveAsset.getLaneIndex());
        if (lane == null) {
            return;
        }
        lane.removeAsset(hveAsset.getIndex());
        setSelectedUUID("");
        mEditPreviewViewModel.updateDuration();
    }

    public boolean isCanAddEffect(boolean isAsset) {
        if (isAsset) {
            return true;
        } else {
            long endTime = 0;
            long currentTime = 0;
            HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
            if (videoLane == null) {
                return false;
            }
            if (mEditPreviewViewModel == null) {
                return false;
            }
            currentTime = mEditPreviewViewModel.getSeekTime();
            endTime = videoLane.getEndTime();

            return (endTime - currentTime) > 100;
        }

    }

    public boolean isCanAddAudio() {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return false;
        }
        long endTime = timeLine.getEndTime();
        long currentTime = timeLine.getCurrentTime();
        return endTime - currentTime >= 200;
    }
}
