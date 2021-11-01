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

package com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel;

import static android.content.Context.ALARM_SERVICE;
import static com.huawei.hms.videoeditor.sdk.asset.HVEAsset.HVEAssetType.AUDIO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlarmManager;
import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEAudioAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoReverseCallback;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableFilterEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEAudioLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.lane.HVELane;
import com.huawei.hms.videoeditor.sdk.lane.HVEStickerLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.utils.UpdateTimesManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.GraffitiInfo;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainRecyclerData;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.fragment.EditPreviewFragment;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class EditPreviewViewModel extends AndroidViewModel {
    private static final String TAG = "EditPreviewViewModel";

    public static final long MIN_DURATION = 100;

    public static final String MUSIC_URI_INFO = "music_uri_info";

    public static final int PROCESS_RESOURCE_PROGRESS = 2;

    public static final int AUDIO_TYPE_MUSIC = 101;

    private MutableLiveData<Boolean> isTimeout = new MutableLiveData<>();

    private MutableLiveData<Integer> intervalLevel = new MutableLiveData<>();

    private MutableLiveData<Double> intervalWidth = new MutableLiveData<>();

    private MutableLiveData<Long> videoDuration = new MutableLiveData<>();

    private MutableLiveData<List<HVEAsset>> imageItemList = new MutableLiveData<>();

    private MutableLiveData<String> selectedUUID = new MutableLiveData<>();

    private MutableLiveData<String> dragUUID = new MutableLiveData<>();

    private MutableLiveData<MainRecyclerData> data = new MutableLiveData<>();

    private MutableLiveData<Long> currentTime = new MutableLiveData<>();

    private MutableLiveData<Integer> transition = new MutableLiveData<>();

    private MutableLiveData<Integer> clickViewType = new MutableLiveData<>(-1);

    private MutableLiveData<String> mCanvasImageData = new MutableLiveData<>();

    private MutableLiveData<List<String>> mStickerListData = new MutableLiveData<>();

    public MutableLiveData<Boolean> isMoveAsset = new MutableLiveData<>();

    private MutableLiveData<String> refreshUUID = new MutableLiveData<>();

    private MutableLiveData<Integer> refreshCurrentMenuControl = new MutableLiveData<>();

    private MutableLiveData<Integer> reverseCallback = new MutableLiveData<>();

    private MutableLiveData<String> toastString = new MutableLiveData<>();

    private MutableLiveData<GraffitiInfo> graffitiInfoMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> addMusicVisible = new MutableLiveData<>();

    private MutableLiveData<Boolean> clearEditBorder = new MutableLiveData<>();

    private MutableLiveData<Boolean> isKeyBordShow = new MutableLiveData<>(false);

    private MutableLiveData<HVEAsset> mainLaneAsset = new MutableLiveData<>();

    private HVEAsset currentMainLaneAsset;

    private MutableLiveData<String> mTextTempValue = new MutableLiveData<>();

    private MutableLiveData<String> mLastInputText = new MutableLiveData<>();

    private MutableLiveData<String> mTextTrailerValue = new MutableLiveData<>();

    private MutableLiveData<Boolean> isDrawWave = new MutableLiveData<>();

    public MutableLiveData<String> getTextTempValue() {
        return mTextTempValue;
    }

    public void setTemplateText(String text) {
        mTextTempValue.postValue(text);
    }

    public MutableLiveData<String> getTextTrailerValue() {
        return mTextTrailerValue;
    }

    public void setTextTrailer(String text) {
        mTextTrailerValue.postValue(text);
    }

    private AlarmManager alarmManager = null;

    private MutableLiveData<Boolean> isEndOfVideoTrackView = new MutableLiveData<>();

    private MutableLiveData<String> faceDetectError = new MutableLiveData<>();

    public MutableLiveData<Boolean> clearGraffitView = new MutableLiveData<>();

    private MutableLiveData<String> refreshRecorderCaption = new MutableLiveData<>();

    private MutableLiveData<String> toastTime = new MutableLiveData<>();

    private MutableLiveData<CloudMaterialBean> defaultFontContent = new MutableLiveData<>();

    private MutableLiveData<Boolean> isFootShow = new MutableLiveData<Boolean>();

    private MutableLiveData<Boolean> footPrintsShow = new MutableLiveData<>();

    private int lastPosition = -1;

    private final MutableLiveData<Boolean> headClick = new MutableLiveData<>();

    public MutableLiveData<Boolean> getHeadClick() {
        return headClick;
    }

    public void setHeadClick(boolean b) {
        headClick.postValue(b);
    }

    private long seekTime = 0;

    private int reverseProgress;

    private static int keyFrameWidth = ScreenUtil.dp2px(9);

    private MutableLiveData<Integer> indexTitle = new MutableLiveData<>();

    public void setIndexTitle(int position) {
        indexTitle.postValue(position);
    }

    public MutableLiveData<Integer> getIndexTitle() {
        return indexTitle;
    }

    private MutableLiveData<Boolean> isBack = new MutableLiveData<>();

    private boolean resume;

    public void setRefresh(boolean resume) {
        this.resume = resume;
    }

    public boolean getRefresh() {
        return resume;
    }

    public void reloadMainLane() {
        if (EditorManager.getInstance().getMainLane() == null) {
            return;
        }
        imageItemList.postValue(getItems());
        updateDuration();
    }

    public void destroyTimeoutManager() {
        isTimeout.postValue(Boolean.FALSE);
        UpdateTimesManager.getInstance().destroy();
    }

    public void initTimeoutManager() {
        UpdateTimesManager.getInstance().initTimesManager(new UpdateTimesManager.UpdateListener() {
            @Override
            public void onTimeout(long currentTimeSpan) {
                SmartLog.d(TAG, "page timeout. " + currentTimeSpan);
                isTimeout.postValue(Boolean.TRUE);
            }

            @Override
            public void onStart() {
                SmartLog.d(TAG, "page timeout manager start. ");
                isTimeout.postValue(Boolean.FALSE);
            }

            @Override
            public void onUpdate(long currentTimeSpan) {
                isTimeout.postValue(Boolean.FALSE);
            }
        });
    }

    private boolean isRecordAudio = false;

    public MutableLiveData<Boolean> getIsBack() {
        return isBack;
    }

    public MutableLiveData<Boolean> getTimeout() {
        return isTimeout;
    }

    public MutableLiveData<Boolean> getClearGraffitView() {
        return clearGraffitView;
    }

    public void setClearGraffitView(Boolean b) {
        clearGraffitView.setValue(b);
    }

    public MutableLiveData<String> getRefreshRecorderCaption() {
        return refreshRecorderCaption;
    }

    public MutableLiveData<Integer> getReverseCallback() {
        return reverseCallback;
    }

    public int getReverseProgress() {
        return reverseProgress;
    }

    public MutableLiveData<String> getToastString() {
        return toastString;
    }

    public MutableLiveData<String> getFaceDetectError() {
        return faceDetectError;
    }

    public void setFaceDetectError(String errorCode) {
        this.faceDetectError.postValue(errorCode);
    }

    private MutableLiveData<HVEAsset> hveAsset = new MutableLiveData<>();

    public void setHVEAsset(HVEAsset hveAsset) {
        this.hveAsset.postValue(hveAsset);
    }

    public MutableLiveData<HVEAsset> getAsset() {
        return hveAsset;
    }

    private int mTransIndex = -1;

    private boolean isAddCoverTextStatus = false;

    public boolean isAddCoverTextStatus() {
        return isAddCoverTextStatus;
    }

    public void setAddCoverTextStatus(boolean addCoverTextStatus) {
        isAddCoverTextStatus = addCoverTextStatus;
    }

    private boolean isNeedAddTextOrSticker = true;

    public boolean isNeedAddTextOrSticker() {
        return isNeedAddTextOrSticker;
    }

    public void setNeedAddTextOrSticker(boolean needAddTextOrSticker) {
        isNeedAddTextOrSticker = needAddTextOrSticker;
    }

    private boolean isEditTextStatus = false;

    private boolean isEditTextTemplateStatus = false;

    private int mTextTemplateEditSelectIndex = -1;

    private boolean isEditStickerStatus = false;

    private boolean isPersonTrackingStatus = false;

    public boolean isEditTextStatus() {
        return isEditTextStatus;
    }

    public void setEditTextStatus(boolean isEditTextStatus) {
        this.isEditTextStatus = isEditTextStatus;
    }

    public boolean isEditTextTemplateStatus() {
        return isEditTextTemplateStatus;
    }

    public void setEditTextTemplateStatus(boolean isEditTextTemplateStatus) {
        this.isEditTextTemplateStatus = isEditTextTemplateStatus;
    }

    public int getTextTemplateEditSelectIndex() {
        return mTextTemplateEditSelectIndex;
    }

    public void setTextTemplateEditSelectIndex(int mTextTemplateEditSelectIndex) {
        this.mTextTemplateEditSelectIndex = mTextTemplateEditSelectIndex;
    }

    public boolean isEditStickerStatus() {
        return isEditStickerStatus;
    }

    public void setEditStickerStatus(boolean isEditStickerStatus) {
        this.isEditStickerStatus = isEditStickerStatus;
    }

    public boolean isPersonTrackingStatus() {
        return isPersonTrackingStatus;
    }

    public void setPersonTrackingStatus(boolean isPersonTrackingStatus) {
        this.isPersonTrackingStatus = isPersonTrackingStatus;
    }

    private final MutableLiveData<String> mEditPanelInputValue = new MutableLiveData<>();

    public void setEditPanelInputValue(String value) {
        this.mEditPanelInputValue.postValue(value);
    }

    public MutableLiveData<String> getEditPanelInputValue() {
        return mEditPanelInputValue;
    }

    public EditPreviewViewModel(@NonNull Application application) {
        super(application);
    }

    public void setVideoDuration(Long videoDuration) {
        this.videoDuration.postValue(videoDuration);
    }

    public void setSelectedUUID(String choiceViewUUID) {
        this.selectedUUID.postValue(choiceViewUUID);
    }

    public void setMoveAsset(Boolean isMoveAsset) {
        this.isMoveAsset.postValue(isMoveAsset);
    }

    public void setDragUUID(String dragUUID) {
        if (StringUtil.isEmpty(dragUUID)) {
            refreshTrackView(this.dragUUID.getValue());
        } else {
            refreshTrackView(dragUUID);
        }
        this.dragUUID.postValue(dragUUID);
    }

    public MutableLiveData<String> getDragUUID() {
        return dragUUID;
    }

    public void setIntervalWidth(double intervalWidth) {
        this.intervalWidth.setValue(intervalWidth);
    }

    public void setIntervalLevel(Integer intervalLevel) {
        this.intervalLevel.setValue(intervalLevel);
    }

    public MutableLiveData<Long> getVideoDuration() {
        return videoDuration;
    }

    public void setMainData(MainRecyclerData mainData) {
        this.data.postValue(mainData);
    }

    public MutableLiveData<Double> getIntervalWidth() {
        return intervalWidth;
    }

    public MutableLiveData<Integer> getIntervalLevel() {
        return intervalLevel;
    }

    public MutableLiveData<String> getSelectedUUID() {
        return selectedUUID;
    }

    public MutableLiveData<List<HVEAsset>> getImageItemList() {
        return imageItemList;
    }

    public MutableLiveData<Long> getCurrentTime() {
        return currentTime;
    }

    public MutableLiveData<MainRecyclerData> getMainData() {
        return data;
    }

    public MutableLiveData<Integer> getTransition() {
        return transition;
    }

    public MutableLiveData<Boolean> isMoveAsset() {
        return isMoveAsset;
    }

    public long getAudioTime() {
        long duration = 0;
        HVEAsset audioAsset = getSelectedAsset();
        if (audioAsset == null || audioAsset.getType() != AUDIO) {
            return duration;
        }
        duration = audioAsset.getDuration();
        return duration;
    }

    public void setSoundTrack(boolean isSwitch) {
        HVEVideoLane hveVideoLane = EditorManager.getInstance().getMainLane();
        if (hveVideoLane != null) {
            hveVideoLane.setMute(isSwitch);
        }
    }

    public void setCurrentTime(long currentTime) {
        seekTime = currentTime;
        mainLaneAssetChange(currentTime);
        this.currentTime.postValue(currentTime);
    }

    public void mainLaneAssetChange(long currentTime) {
        HVEAsset asset = getMainLaneAsset(currentTime);
        if (asset == null) {
            return;
        }

        SmartLog.d(TAG, "mainLaneAssetChange:" + (currentMainLaneAsset == asset));
        if (currentMainLaneAsset == asset) {
            return;
        }
        SmartLog.d(TAG, "mainLaneAssetChange:postValue");
        mainLaneAsset.postValue(asset);
        currentMainLaneAsset = asset;
    }

    public MutableLiveData<HVEAsset> getMainLaneAssetChanged() {
        return mainLaneAsset;
    }

    public long getSeekTime() {
        return seekTime;
    }

    public void updateTimeLine() {
        SmartLog.d(TAG, "showSetCoverImageFragment:" + seekTime);
        HuaweiVideoEditor huaweiVideoEditor = EditorManager.getInstance().getEditor();
        if (huaweiVideoEditor == null) {
            return;
        }
        huaweiVideoEditor.seekTimeLine(seekTime);
    }

    public HVEVideoLane getVideoLane() {
        return EditorManager.getInstance().getMainLane();
    }

    public HVETimeLine getTimeLine() {
        return EditorManager.getInstance().getTimeLine();
    }

    public void refreshAssetList() {
        if (EditorManager.getInstance().getMainLane() != null) {
            imageItemList.postValue(getItems());
        }
    }

    public List<HVEAsset> getItems() {
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (videoLane == null) {
            return new ArrayList<>();
        }
        return videoLane.getAssets();
    }

    public HVEAsset getMainLaneAsset(long currentTime) {
        long time = currentTime;
        HVEVideoLane lane = EditorManager.getInstance().getMainLane();
        if (lane != null && time == lane.getEndTime()) {
            return lane.getAssetByIndex(lane.getAssets().size() - 1);
        }

        for (int i = 0; i < getItems().size(); i++) {
            HVEAsset asset = getItems().get(i);
            if (time >= asset.getStartTime() && time < asset.getEndTime()) {
                return asset;
            }
        }
        return null;
    }

    public HVEAsset getMainLaneAsset() {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return null;
        }

        long time = timeLine.getCurrentTime();

        return getMainLaneAsset(time);
    }

    public void transitionReloadUI() {
        refreshAssetList();
        updateDuration();
    }

    public HVEEffect getEffectedTransition() {
        HVEVideoLane hveVideoLane = getVideoLane();
        if (hveVideoLane == null) {
            return null;
        }
        for (int i = 0; i < hveVideoLane.getTransitionEffects().size(); i++) {
            if (hveVideoLane.getTransitionEffects().get(i).getIntVal("from") == mTransIndex
                || hveVideoLane.getTransitionEffects().get(i).getIntVal("to") == mTransIndex + 1) {
                return hveVideoLane.getTransitionEffects().get(i);
            }
        }
        return null;
    }

    public long getTransMinDuration() {
        if (EditorManager.getInstance().getMainLane() == null) {
            return 0;
        }
        HVEVideoLane hveVideoLane = EditorManager.getInstance().getMainLane();
        if (hveVideoLane == null) {
            return 0;
        }
        HVEAsset firstAsset = hveVideoLane.getAssetByIndex(mTransIndex);
        if (firstAsset == null) {
            return 0;
        }
        HVEAsset secondAsset = hveVideoLane.getAssetByIndex(mTransIndex + 1);
        if (secondAsset == null) {
            return 0;
        }
        return Math.min(4000, Math.min(firstAsset.getDuration(), secondAsset.getDuration()) / 2);
    }

    public boolean addAudio(CloudMaterialBean content, int type) {
        HVETimeLine hveTimeLine = getTimeLine();
        if (hveTimeLine == null) {
            SmartLog.e(TAG, "timeline is null when add audio");
            return false;
        }
        HVEAudioAsset audioAsset =
            addAudio(content.getName(), content.getLocalPath(), hveTimeLine.getCurrentTime(), type);
        if (audioAsset == null) {
            SmartLog.e(TAG, "audioAsset is null when add audio");
            return false;
        }
        return true;
    }

    public void addAudio(String name, String path, int type) {
        HVETimeLine hveTimeLine = getTimeLine();
        if (hveTimeLine == null) {
            SmartLog.e(TAG, "timeline is null when add audio");
            return;
        }

        addAudio(name, path, hveTimeLine.getCurrentTime(), type);
    }

    public HVEAudioAsset addAudio(String name, String path, long startTime, int type) {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return null;
        }
        HVETimeLine hveTimeLine = getTimeLine();
        if (hveTimeLine == null) {
            return null;
        }

        if (startTime < 0) {
            startTime = hveTimeLine.getCurrentTime();
        }
        HVEAudioLane audioLane = LaneSizeCheckUtils.getAudioFreeLan(editor, startTime, hveTimeLine.getEndTime());
        if (audioLane == null) {
            ToastWrapper
                .makeText(getApplication(), getApplication().getString(R.string.audio_lane_out_of_size),
                    Toast.LENGTH_SHORT)
                .show();
            return null;
        }
        HVEAudioAsset asset = audioLane.appendAudioAsset(path, startTime);
        if (asset != null) {
            setSelectedUUID(asset.getUuid());
        }
        return asset;
    }

    public void playTimeLine(long startTime, long endTime) {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        editor.pauseTimeLine();
        editor.playTimeLine(startTime, endTime);
    }

    public void refreshFilterFloatVal(HVEEffect effect, float strenth) {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (effect != null) {
            if (effect instanceof ScriptableFilterEffect) {
                ((ScriptableFilterEffect) effect).setFloatValAction(HVEEffect.FILTER_STRENTH_KEY, strenth);
            } else {
                effect.setFloatVal(HVEEffect.FILTER_STRENTH_KEY, strenth);
            }
            if (editor == null) {
                return;
            }
            editor.refresh(seekTime);
        }
    }

    public void updateDuration() {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return;
        }
        setVideoDuration(timeLine.getDuration());
    }

    public void setChoiceAsset(HVEAsset asset) {
        if (asset == null) {
            setSelectedUUID("");
        } else {
            setSelectedUUID(asset.getUuid());
        }
    }

    private EditPreviewFragment mEditPreviewFragment;

    public void setFragment(EditPreviewFragment mEditPreviewFragment) {
        this.mEditPreviewFragment = mEditPreviewFragment;
    }

    public EditPreviewFragment getFragment() {
        return mEditPreviewFragment;
    }

    public void setCurrentTimeLine(long time) {
        HuaweiVideoEditor huaweiVideoEditor = EditorManager.getInstance().getEditor();
        if (huaweiVideoEditor == null) {
            return;
        }
        SmartLog.d(TAG, "seek to " + time);
        setCurrentTime(time);
    }

    public int getTransLengthByIndex() {
        return mTransIndex;
    }

    public HuaweiVideoEditor getEditor() {
        return EditorManager.getInstance().getEditor();
    }

    public void pause() {
        HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
        if (editor == null) {
            return;
        }
        editor.pauseTimeLine();
    }

    public HVEAsset getSelectedAsset() {
        if (selectedUUID == null || TextUtils.isEmpty(selectedUUID.getValue())) {
            return null;
        }
        return getSelectedAsset(selectedUUID.getValue());
    }

    public HVEAsset getSelectedAsset(String uuid) {
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (timeLine == null) {
            return null;
        }

        for (HVEVideoLane lane : timeLine.getAllVideoLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(uuid)) {
                    return asset;
                }
            }
        }
        for (HVEStickerLane lane : timeLine.getAllStickerLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(uuid)) {
                    return asset;
                }
            }
        }
        for (HVEAudioLane lane : timeLine.getAllAudioLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(uuid)) {
                    return asset;
                }
            }
        }
        return null;
    }

    public HVEEffect getSelectedEffect() {
        HVETimeLine hveTimeLine = getTimeLine();
        HuaweiVideoEditor editor = getEditor();
        if (selectedUUID == null || TextUtils.isEmpty(selectedUUID.getValue())) {
            return null;
        }

        if (editor == null || hveTimeLine == null) {
            return null;
        }

        for (HVEEffectLane lane : hveTimeLine.getAllEffectLane()) {
            for (HVEEffect effect : lane.getEffects()) {
                if (effect.getUuid().equals(selectedUUID.getValue())) {
                    return effect;
                }
            }
        }
        return null;
    }

    public HVEEffect getSelectedEffect(String uuid) {
        HVETimeLine hveTimeLine = getTimeLine();
        HuaweiVideoEditor editor = getEditor();
        if (StringUtil.isEmpty(uuid)) {
            return null;
        }

        if (editor == null || hveTimeLine == null) {
            return null;
        }

        for (HVEEffectLane lane : hveTimeLine.getAllEffectLane()) {
            for (HVEEffect effect : lane.getEffects()) {
                if (effect.getUuid().equals(uuid)) {
                    return effect;
                }
            }
        }
        return null;
    }

    public HVELane getSelectedLane() {
        HVETimeLine hveTimeLine = getTimeLine();
        HuaweiVideoEditor editor = getEditor();
        if (selectedUUID == null || TextUtils.isEmpty(selectedUUID.getValue())) {
            return null;
        }

        if (editor == null || hveTimeLine == null) {
            return null;
        }

        for (HVEVideoLane lane : hveTimeLine.getAllVideoLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(selectedUUID.getValue())) {
                    return lane;
                }
            }
        }
        for (HVEStickerLane lane : hveTimeLine.getAllStickerLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(selectedUUID.getValue())) {
                    return lane;
                }
            }
        }
        for (HVEAudioLane lane : hveTimeLine.getAllAudioLane()) {
            for (HVEAsset asset : lane.getAssets()) {
                if (asset.getUuid().equals(selectedUUID.getValue())) {
                    return lane;
                }
            }
        }
        return null;
    }

    public MutableLiveData<Integer> getClickViewType() {
        return clickViewType;
    }

    public MutableLiveData<String> getCanvasImageData() {
        return mCanvasImageData;
    }

    public void setCanvasImageData(String mCanvasImageData) {
        this.mCanvasImageData.postValue(mCanvasImageData);
    }

    public MutableLiveData<List<String>> getBlockingStickerList() {
        return mStickerListData;
    }

    public void addBlockingSticker(String sticker) {
        List<String> list;
        List<String> stickerListDataValue = mStickerListData.getValue();
        if (stickerListDataValue != null && stickerListDataValue.size() > 0) {
            list = new ArrayList<>(stickerListDataValue);
            list.add(0, sticker);
        } else {
            list = new ArrayList<>();
            list.add(sticker);
        }
        mStickerListData.postValue(list);
    }

    public void removeBlockingSticker(String sticker) {
        List<String> stickerListDataValue = mStickerListData.getValue();
        if (stickerListDataValue != null && stickerListDataValue.size() > 0) {
            Iterator<String> iterator = stickerListDataValue.iterator();
            while (iterator.hasNext()) {
                String path = iterator.next();
                if (path.equals(sticker)) {
                    iterator.remove();
                }
            }
            mStickerListData.postValue(stickerListDataValue);
        }
    }

    public int getSelectedLaneIndex() {
        long slelctedCurrentTime = seekTime;
        int index = 0;
        long splitPoint = 0;
        for (int i = 0; i < getItems().size(); i++) {
            HVEAsset asset = getItems().get(i);
            if (asset.getStartTime() == slelctedCurrentTime || asset.getEndTime() == slelctedCurrentTime) {
                return 0;
            }
            if (slelctedCurrentTime > asset.getStartTime() && slelctedCurrentTime < asset.getEndTime()) {
                index = i;
                splitPoint = slelctedCurrentTime - asset.getStartTime();
            }
        }
        if (splitPoint == 0) {
            return 0;
        }
        return index;
    }

    public void refreshTrackView(String refreshUUID) {
        this.refreshUUID.postValue(refreshUUID);
    }

    public MutableLiveData<String> getRefreshUUID() {
        return refreshUUID;
    }

    public void refreshCurrentMenuControl(int position) {
        refreshCurrentMenuControl.postValue(position);
    }

    public MutableLiveData<Integer> getRefreshCurrentMenuControl() {
        return refreshCurrentMenuControl;
    }

    public boolean isAlarmClock(long time) {
        boolean isClock = false;
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getApplication().getSystemService(ALARM_SERVICE);
        }
        HuaweiVideoEditor editor = getEditor();
        AlarmManager.AlarmClockInfo nextAlarmClock = alarmManager.getNextAlarmClock();
        long triggerTime = 0;
        if (nextAlarmClock != null) {
            triggerTime = nextAlarmClock.getTriggerTime();
            if (triggerTime - time >= -500 && triggerTime - time <= 500) {
                isClock = true;
                if (editor != null) {
                    editor.pauseTimeLine();
                }
            }
        }
        return isClock;
    }

    public void videoRevert() {
        HVETimeLine hveTimeLine = getTimeLine();
        HVEAsset hveVideoAsset = getSelectedAsset();
        if (hveVideoAsset == null) {
            hveVideoAsset = getMainLaneAsset();
        }
        if (hveVideoAsset == null) {
            toastString.postValue(getApplication().getString(R.string.reverse_assset_tips));
            return;
        }
        if (!(hveVideoAsset instanceof HVEVideoAsset)) {
            toastString.postValue(getApplication().getString(R.string.reverse_assset_error_tips));
            return;
        }
        int videoLaneIndex = hveVideoAsset.getLaneIndex();
        int assetIndex = hveVideoAsset.getIndex();
        HVEVideoLane hveVideoLane = hveTimeLine.getVideoLane(videoLaneIndex);
        reverseCallback.postValue(1);
        hveVideoLane.reverseVideo(assetIndex, new HVEVideoReverseCallback() {
            @Override
            public void onProgress(long currentTime, long duration) {
                reverseProgress = (int) (currentTime * 1D / duration * 100);
                reverseCallback.postValue(PROCESS_RESOURCE_PROGRESS);
            }

            @Override
            public void onSuccess() {
                reverseCallback.postValue(3);
                setSelectedUUID("");
                toastString.postValue(getApplication().getString(R.string.reverse_success));
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                reverseCallback.postValue(3);
                setSelectedUUID("");
                toastString.postValue(getApplication().getString(R.string.reverse_fail));

            }

            @Override
            public void onCancel() {
                reverseCallback.postValue(3);
                setSelectedUUID("");
                toastString.postValue(getApplication().getString(R.string.reverse_cancel));
            }
        });
    }

    public void cancelVideoRevert() {
        HVETimeLine hveTimeLine = getTimeLine();
        HVEAsset hveVideoAsset = getSelectedAsset();
        if (hveVideoAsset == null) {
            hveVideoAsset = getMainLaneAsset();
        }
        if (!(hveVideoAsset instanceof HVEVideoAsset)) {
            return;
        }

        if (getTimeLine() != null) {
            getTimeLine().getVideoLane(hveVideoAsset.getLaneIndex()).interruptReverseVideo(null);
        }
    }

    public void setGraffitiInfo(GraffitiInfo info) {
        SmartLog.d(TAG, "setGraffitiInfo: " + info.visible);
        graffitiInfoMutableLiveData.setValue(info);
    }

    public MutableLiveData<GraffitiInfo> getGraffitiInfo() {
        return graffitiInfoMutableLiveData;
    }

    public MutableLiveData<Boolean> getAddMusicVisible() {
        return addMusicVisible;
    }

    public void setAddMusicVisible(Boolean b) {
        addMusicVisible.setValue(b);
    }

    public MutableLiveData<Boolean> getClearEditBorder() {
        return clearEditBorder;
    }

    public void setClearEditBorder(Boolean b) {
        clearEditBorder.setValue(b);
    }

    private int keyBordShowHeight = 0;

    public int getKeyBordShowHeight() {
        return keyBordShowHeight;
    }

    public void setKeyBordShowHeight(int keyBordShowHeight) {
        this.keyBordShowHeight = keyBordShowHeight;
    }

    public MutableLiveData<Boolean> getKeyBordShow() {
        return isKeyBordShow;
    }

    public void setKeyBordShow(Boolean b) {
        isKeyBordShow.setValue(b);
    }

    public MutableLiveData<CloudMaterialBean> getDefaultFontContent() {
        return defaultFontContent;
    }

    public MutableLiveData<String> getToastTime() {
        return toastTime;
    }

    public void setToastTime(String toastTime) {
        this.toastTime.postValue(toastTime);
    }

    public void setIsFootShow(boolean isShow) {
        isFootShow.postValue(isShow);
    }

    public MutableLiveData<Boolean> getIsFootShow() {
        return isFootShow;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    private String contentPath;

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    private String contentId;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public MutableLiveData<Boolean> getIsEndOfVideoTrackView() {
        return isEndOfVideoTrackView;
    }

    public void setIsEndOfVideoTrackView(boolean b) {
        isEndOfVideoTrackView.postValue(b);
    }

    private boolean isSame;

    public boolean isSame() {
        return isSame;
    }

    public void setSame(boolean same) {
        isSame = same;
    }

    public void setIsBack(Boolean isBack) {
        this.isBack.postValue(isBack);
    }

    public boolean isRecordAudio() {
        return isRecordAudio;
    }

    public void setRecordAudio(boolean recordAudio) {
        isRecordAudio = recordAudio;
    }

    private boolean isAddCurveSpeedStatue = false;

    public boolean isAddCurveSpeedStatus() {
        return isAddCurveSpeedStatue;
    }

    public void setAddCurveSpeedStatus(boolean addCurveSpeedStatus) {
        this.isAddCurveSpeedStatue = addCurveSpeedStatus;
    }

    public MutableLiveData<Boolean> getIsDrawWave() {
        return isDrawWave;
    }

    public void setIsDrawWave() {
        isDrawWave.postValue(true);
    }

    private boolean isTrailerStatus = false;

    public void setTrailerStatus(boolean isTrailer) {
        isTrailerStatus = isTrailer;
    }

    public boolean isTrailerStatus() {
        return isTrailerStatus;
    }

    private MutableLiveData<Integer> tableIndex = new MutableLiveData<>();

    public void setTableIndex(int index) {
        tableIndex.postValue(index);
    }

    public MutableLiveData<Integer> getTableIndex() {
        return tableIndex;
    }

    private MutableLiveData<Boolean> isClearTemplate = new MutableLiveData<>();

    public void setIsClearTemplate(boolean b) {
        isClearTemplate.postValue(b);
    }

    public MutableLiveData<Boolean> getIsClearTemplate() {
        return isClearTemplate;
    }

    public String getmLastInputText() {
        return mLastInputText.getValue();
    }

    public void setmLastInputText(String mLastInputText) {
        this.mLastInputText.postValue(mLastInputText);
    }

    public MutableLiveData<Boolean> onTouchEvent = new MutableLiveData<>();

    public void setOnTouchEvent(boolean refresh) {
        this.onTouchEvent.setValue(refresh);
    }
}
