
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

import static com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity.ACTION_ADD_PICTURE_IN_REQUEST_CODE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity.ACTION_PIP_VIDEO_ASSET;
import static com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity.ACTION_REPLACE_VIDEO_ASSET;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import android.content.Intent;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEAudioAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.effect.impl.ColorFilterEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVELane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.utils.CpuUtils;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;
import com.huawei.hms.videoeditor.ui.common.utils.SPManager;
import com.huawei.hms.videoeditor.ui.common.utils.ToastUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingDialogUtils;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.aifun.AIBlockingHintDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.aifun.fragment.AiFunFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.aihair.fragment.AiHairFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.animation.videoanimation.fragment.AnimationPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.activity.AudioPickActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.fragment.SoundEffectFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.effect.fragment.EffectPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.FilterAdjustPanelView;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.FilterPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.aifilter.fragment.ExclusiveFilterPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.AudioSpeedFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.CanvasBackgroundFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.GeneralSpeedFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.GraffitiFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.MaskEffectFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.ObjectFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.TransitionPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.TransparencyPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.VideoProportionFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.fragment.VolumePanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.persontrack.PersonTrackingViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.pip.PicInPicMixFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.sticker.fragment.StickerPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.sticker.stickeranimation.fragment.StickerAnimationPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.fragment.EditPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditor.ui.mediapick.activity.MediaPickActivity;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.fragment.app.Fragment;

public class MenuClickManager {
    public final static String AI_FUN = "AI_FUN";

    public final static String AI_FUN_KEY = "AI_FUN_KEY";

    private final static String FACE_BLOCKING = "FACE_BLOCKING";

    private final static String FACE_BLOCKING_KEY = "FACE_BLOCKING_KEY";

    private VideoClipsActivity mActivity;

    private EditMenuContentLayout menuContentLayout;

    private MenuControlViewRouter mMenuControlViewRouter;

    private MenuViewModel mMenuViewModel;

    private EditPreviewViewModel editPreviewViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private PersonTrackingViewModel mPersonTrackingViewModel;

    private OnAssetDeleteListener mOnAssetDeleteListener;

    private List<Integer> cloudMaterialsIdList;

    private static class MenuClickManagerHolder {
        private static final MenuClickManager INSTANCE = new MenuClickManager();
    }

    public static MenuClickManager getInstance() {
        return MenuClickManagerHolder.INSTANCE;
    }

    public void init(VideoClipsActivity activity, EditMenuContentLayout menuContentLayout, MenuViewModel menuViewModel,
        EditPreviewViewModel editPreviewViewModel, MaterialEditViewModel materialEditViewModel,
        PersonTrackingViewModel personTrackingViewModel) {
        this.mActivity = activity;
        this.mMenuViewModel = menuViewModel;
        this.editPreviewViewModel = editPreviewViewModel;
        this.mMaterialEditViewModel = materialEditViewModel;
        this.mPersonTrackingViewModel = personTrackingViewModel;
        this.menuContentLayout = menuContentLayout;
        mMenuControlViewRouter =
            new MenuControlViewRouter(mActivity, R.id.fragment_container, menuContentLayout, mMenuViewModel);
        this.cloudMaterialsIdList = getCloudMaterialsIdList();
    }

    public void update(VideoClipsActivity activity, EditMenuContentLayout menuContentLayout,
        MenuViewModel menuViewModel, EditPreviewViewModel editPreviewViewModel,
        MaterialEditViewModel materialEditViewModel, PersonTrackingViewModel personTrackingViewModel) {
        this.mActivity = activity;
        this.mMenuViewModel = menuViewModel;
        this.editPreviewViewModel = editPreviewViewModel;
        this.mMaterialEditViewModel = materialEditViewModel;
        this.mPersonTrackingViewModel = personTrackingViewModel;
        this.menuContentLayout = menuContentLayout;
        if (mMenuControlViewRouter != null) {
            mMenuControlViewRouter.updateMenuViewModel(mMenuViewModel);
        }
        this.cloudMaterialsIdList = getCloudMaterialsIdList();
    }

    public void setOnAssetDeleteListener(OnAssetDeleteListener listener) {
        mOnAssetDeleteListener = listener;
    }

    public Stack<MenuControlViewRouter.Panel> getViewStack() {
        if (mMenuControlViewRouter == null) {
            return null;
        }
        return mMenuControlViewRouter.getViewStack();
    }

    public void showFragment(int menuId, Fragment fragment) {
        if (mMenuControlViewRouter != null) {
            mMenuControlViewRouter.showFragment(menuId, fragment);
        }
    }

    public boolean popView() {
        if (mMenuControlViewRouter == null) {
            return false;
        }
        boolean isPopView = mMenuControlViewRouter.popView();
        return isPopView;
    }

    public void handlerClickEvent(int id) {
        switch (id) {
            case EDIT_PIP_STATE_ADD:
                if (!LaneSizeCheckUtils.isCanAddPip(EditorManager.getInstance().getEditor(), mActivity)) {
                    ToastWrapper
                        .makeText(mActivity, mActivity.getString(R.string.pip_lane_out_of_size), Toast.LENGTH_SHORT)
                        .show();
                    return;
                }

                Intent startIntent = new Intent(mActivity, MediaPickActivity.class);
                startIntent.putExtra(MediaPickActivity.ACTION_TYPE, MediaPickActivity.ACTION_ADD_PIP_MEDIA_TYPE);
                mActivity.startActivityForResult(startIntent, ACTION_ADD_PICTURE_IN_REQUEST_CODE);
                break;

            case EDIT_VIDEO_STATE_SPLIT:
                mActivity.showAssetSplitFragment(id);
                break;

            case EDIT_VIDEO_STATE_KEYFRAME:
            case EDIT_VIDEO_OPERATION_KEYFRAME:
                mActivity.showKeyFrameFragment(id);
                break;

            case EDIT_VIDEO_STATE_TRIM:
            case EDIT_VIDEO_OPERATION_TRIM:
                HVEAsset assetTrim = editPreviewViewModel.getMainLaneAsset();
                if (assetTrim == null || !(assetTrim instanceof HVEVideoAsset)) {
                    ToastWrapper.makeText(mActivity, mActivity.getString(R.string.crop_limit), Toast.LENGTH_SHORT)
                        .show();
                    return;
                }
                mActivity.showAssetCropFragment(id);
                break;
            case EDIT_BACKGROUND_STATE:
                showPanelViewPrepare(id, new CanvasBackgroundFragment());
                break;

            case EDIT_VIDEO_STATE_REPLACE:
                Intent intent = new Intent(mActivity, MediaPickActivity.class);
                HVEAsset asset = editPreviewViewModel.getMainLaneAsset();
                if (asset == null) {
                    return;
                }
                intent.putExtra("isReplaceAsset", true);
                intent.putExtra(MediaPickActivity.ACTION_TYPE, MediaPickActivity.ACTION_REPLACE_MEDIA_TYPE);
                intent.putExtra(MediaPickActivity.DURATION, asset.getDuration());
                mActivity.startActivityForResult(intent, ACTION_REPLACE_VIDEO_ASSET);
                break;

            case EDIT_VIDEO_STATE_COPY:
                mMenuViewModel.copyMainLaneAsset();
                break;

            case EDIT_VIDEO_STATE_DELETE:
            case EDIT_VIDEO_OPERATION_DELETE:
                HVEAsset assetDelete = editPreviewViewModel.getMainLaneAsset();
                if (assetDelete == null) {
                    return;
                }

                if (EditorManager.getInstance().getMainLane() == null
                    || EditorManager.getInstance().getMainLane().getAssets().size() == 1) {
                    ToastWrapper.makeText(mActivity, mActivity.getString(R.string.lastvideo), Toast.LENGTH_SHORT)
                        .show();
                    return;
                }

                HVEVideoLane hveVideoLane = EditorManager.getInstance().getMainLane();
                if (hveVideoLane == null) {
                    return;
                }
                List<HVEAsset> hveAssetList = hveVideoLane.getAssets();
                if (hveAssetList == null) {
                    return;
                }
                mMenuViewModel.pauseTimeLine();
                mMenuViewModel.deleteVideo(assetDelete);
                mMaterialEditViewModel.clearMaterialEditData();
                if (mOnAssetDeleteListener != null) {
                    mOnAssetDeleteListener.onDelete(assetDelete);
                }
                break;

            case EDIT_VIDEO_STATE_ROTATION:
                HVEAsset hveAsset = editPreviewViewModel.getMainLaneAsset();
                if (hveAsset == null) {
                    return;
                }
                if (hveAsset instanceof HVEVideoAsset || hveAsset instanceof HVEImageAsset) {
                    HVEVisibleAsset visibleAsset = (HVEVisibleAsset) hveAsset;
                    float aRotation = visibleAsset.getRotation();
                    if (aRotation >= 0 && aRotation < 270) {
                        aRotation += 90;
                    } else {
                        aRotation = 0;
                    }
                    visibleAsset.setRotation(aRotation);
                    editPreviewViewModel.getEditor().refresh(editPreviewViewModel.getTimeLine().getCurrentTime());
                    ToastWrapper.makeText(mActivity, String.format(Locale.ROOT,
                        mActivity.getString(R.string.rotationdegree), String.valueOf(aRotation)), Toast.LENGTH_SHORT)
                        .show(500);
                }
                break;

            case EDIT_VIDEO_STATE_MIRROR:
            case EDIT_VIDEO_OPERATION_MIRROR:
            case EDIT_PIP_OPERATION_MIRROR:
                HVEAsset mirrorAsset = editPreviewViewModel.getSelectedAsset();
                if (mirrorAsset == null) {
                    mirrorAsset = editPreviewViewModel.getMainLaneAsset();
                }
                if (mirrorAsset == null) {
                    return;
                }

                boolean mirrorStatus = mActivity.isMirrorStatus();
                if (mirrorAsset instanceof HVEVideoAsset) {
                    mirrorStatus = ((HVEVideoAsset) mirrorAsset).getHorizontalMirrorState();
                    ((HVEVideoAsset) mirrorAsset).setHorizontalMirrorState(!mirrorStatus);
                }
                if (mirrorAsset instanceof HVEImageAsset) {
                    mirrorStatus = ((HVEImageAsset) mirrorAsset).getHorizontalMirrorState();
                    ((HVEImageAsset) mirrorAsset).setHorizontalMirrorState(!mirrorStatus);
                }
                mirrorStatus = !mirrorStatus;
                mActivity.setMirrorStatus(mirrorStatus);
                if (mirrorStatus) {
                    ToastWrapper.makeText(mActivity, mActivity.getString(R.string.openmirror), Toast.LENGTH_SHORT)
                        .show();
                } else {
                    ToastWrapper.makeText(mActivity, mActivity.getString(R.string.closemirror), Toast.LENGTH_SHORT)
                        .show();
                }

                if (editPreviewViewModel.getTimeLine() != null) {
                    editPreviewViewModel.getEditor().seekTimeLine(editPreviewViewModel.getTimeLine().getCurrentTime());
                }
                break;
            case EDIT_VIDEO_STATE_INVERTED:
                editPreviewViewModel.videoRevert();
                break;
            case EDIT_PIP_OPERATION_ADD:
                Intent startIntent2 = new Intent(mActivity, MediaPickActivity.class);
                startIntent2.putExtra(MediaPickActivity.ACTION_TYPE, MediaPickActivity.ACTION_ADD_PIP_MEDIA_TYPE);
                mActivity.startActivityForResult(startIntent2, ACTION_ADD_PICTURE_IN_REQUEST_CODE);
                break;
            case EDIT_VIDEO_OPERATION_SPLIT:
                mActivity.showAssetSplitFragment(id);
                break;
            case EDIT_VIDEO_OPERATION_COPY:
                mMenuViewModel.copyMainLaneAsset();
                break;
            case EDIT_VIDEO_STATE_VOLUME:
            case EDIT_VIDEO_OPERATION_VOLUME:
            case EDIT_PIP_OPERATION_VOLUME:
            case EDIT_AUDIO_STATE_VOLUME:
                showPanelViewPrepare(id, VolumePanelFragment.newInstance(id));
                break;

            case EDIT_VIDEO_STATE_SPEED:
            case EDIT_VIDEO_OPERATION_SPEED:
            case EDIT_PIP_OPERATION_SPEED:
            case EDIT_AUDIO_STATE_SPEED:
                HVEAsset speedAsset = editPreviewViewModel.getSelectedAsset();
                if (speedAsset == null) {
                    speedAsset = editPreviewViewModel.getMainLaneAsset();
                }
                if (speedAsset instanceof HVEVideoAsset) {
                    showPanelViewPrepare(id, GeneralSpeedFragment.newInstance(id));
                }

                if (speedAsset instanceof HVEAudioAsset) {
                    showPanelViewPrepare(id, AudioSpeedFragment.newInstance(id));
                }
                break;

            case EDIT_VIDEO_OPERATION_ROTATION:
            case EDIT_PIP_OPERATION_ROTATION:
                HVEAsset assets = editPreviewViewModel.getSelectedAsset();
                if (assets == null) {
                    return;
                }
                if (assets instanceof HVEVideoAsset || assets instanceof HVEImageAsset) {
                    HVEVisibleAsset visibleAsset = (HVEVisibleAsset) assets;
                    float rotation = visibleAsset.getRotation();
                    if (rotation >= 0 && rotation < 270) {
                        rotation += 90;
                    } else {
                        rotation = 0;
                    }
                    visibleAsset.setRotation(rotation);
                    if (editPreviewViewModel.getTimeLine() != null) {
                        editPreviewViewModel.getEditor().refresh(editPreviewViewModel.getTimeLine().getCurrentTime());
                    }
                    ToastWrapper.makeText(mActivity, String.format(Locale.ROOT,
                        mActivity.getString(R.string.rotationdegree), String.valueOf(rotation)), Toast.LENGTH_SHORT)
                        .show(500);
                }

                break;
            case EDIT_VIDEO_OPERATION_TAILORING:
            case EDIT_PIP_OPERATION_CROP:
            case EDIT_VIDEO_STATE_TAILORING:
                mActivity.gotoCropVideoActivity();
                break;

            case EDIT_PIP_OPERATION_ANIMATION:
            case EDIT_VIDEO_STATE_ANIMATION:
            case EDIT_VIDEO_OPERATION_ANIMATION:
                showPanelViewPrepare(id, AnimationPanelFragment.newInstance(id));
                break;

            case EDIT_VIDEO_OPERATION_INVERTED:
                editPreviewViewModel.videoRevert();
                break;

            case EDIT_VIDEO_STATE_MASK:
            case EDIT_VIDEO_OPERATION_MASK:
                showPanelViewPrepare(id, MaskEffectFragment.newInstance(true));
                break;

            case EDIT_VIDEO_OPERATION_REPLACE:
                Intent pipIntent = new Intent(mActivity, MediaPickActivity.class);
                HVEAsset mainAsset = editPreviewViewModel.getMainLaneAsset();
                if (mainAsset == null) {
                    return;
                }
                pipIntent.putExtra("isReplaceAsset", true);
                pipIntent.putExtra(MediaPickActivity.ACTION_TYPE, MediaPickActivity.ACTION_REPLACE_MEDIA_TYPE);
                pipIntent.putExtra(MediaPickActivity.DURATION, mainAsset.getDuration());
                mActivity.startActivityForResult(pipIntent, ACTION_REPLACE_VIDEO_ASSET);
                break;

            case EDIT_VIDEO_OPERATION_FILTER_ADD:
            case EDIT_FILTER_STATE_ADD:
            case EDIT_VIDEO_STATE_FILTER:
            case EDIT_PIP_OPERATION_FILTER:
                boolean isFromAsset = true;
                if (id == EDIT_FILTER_STATE_ADD) {
                    isFromAsset = false;
                }
                if (mActivity != null && !mMenuViewModel.isCanAddEffect(isFromAsset)) {
                    ToastWrapper
                        .makeText(mActivity, mActivity.getResources().getString(R.string.nofilter), Toast.LENGTH_SHORT)
                        .show();
                    return;
                }
                showPanelViewPrepare(id, FilterPanelFragment.newInstance(isFromAsset));
                break;
            case EDIT_FILTER_STATE_EXCLUSIVE:
                if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                    return;
                }

                if (isCpu32()) {
                    ToastWrapper.makeText(mActivity, mActivity.getResources().getString(R.string.cpu_limit)).show();
                    return;
                }

                if (!mMenuViewModel.isCanAddEffect(false)) {
                    ToastWrapper
                        .makeText(mActivity, mActivity.getResources().getString(R.string.exclusive_filter_tip_text14),
                            Toast.LENGTH_SHORT)
                        .show();
                    return;
                }

                showPanelViewPrepare(id, ExclusiveFilterPanelFragment.newInstance());
                break;
            case EDIT_VIDEO_OPERATION_ADJUST:
            case EDIT_FILTER_STATE_ADJUST:
            case EDIT_VIDEO_STATE_ADJUST:
            case EDIT_PIP_OPERATION_ADJUST:
            case EDIT_ADJUST_OPERATION_ADJUST:
                boolean isAsset = true;
                if (id == EDIT_FILTER_STATE_ADJUST || id == EDIT_ADJUST_OPERATION_ADJUST) {
                    isAsset = false;
                }
                if (mActivity != null && !mMenuViewModel.isCanAddEffect(isAsset)) {
                    ToastWrapper
                        .makeText(mActivity, mActivity.getApplication().getResources().getString(R.string.noadjust),
                            Toast.LENGTH_SHORT)
                        .show();
                    return;
                }
                showPanelViewPrepare(id, FilterAdjustPanelView.newInstance(isAsset));
                break;
            case EDIT_STICKER_STATE_ADD_TUYA:
            case EDIT_TEXT_STATE_ADD_ADDTUYA:
                showPanelViewPrepare(id, GraffitiFragment.newInstance(false));
                break;

            case EDIT_STICKER_STATE_ADD_STICKER:
            case EDIT_TEXT_STATE_STICKER:
            case EDIT_STICKER_OPERATION_ADD:
                showPanelViewPrepare(id, StickerPanelFragment.newInstance(false));
                break;

            case EDIT_STICKER_OPERATION_COPY:
                mMenuViewModel.copySticker();
                break;

            case EDIT_STICKER_OPERATION_SPLIT:
                mMenuViewModel.splitSticker();
                break;

            case EDIT_STICKER_OPERATION_REPLACE:
                showPanelViewPrepare(id, StickerPanelFragment.newInstance(true));
                break;

            case EDIT_STICKER_OPERATION_ANIMATION:
                showPanelViewPrepare(id, new StickerAnimationPanelFragment());
                break;

            case EDIT_STICKER_OPERATION_DELETE:
            case EDIT_GRAFFIT_OPERATION_DELETE:
                mMaterialEditViewModel.clearMaterialEditData();
                mMenuViewModel.deleteAsset();
                popView();
                break;

            case EDIT_VIDEO_STATE_TRANSPARENCY:
            case EDIT_VIDEO_OPERATION_TRANSPARENCY:
            case EDIT_PIP_OPERATION_TRANSPARENCY:
                showPanelViewPrepare(id, new TransparencyPanelFragment());
                break;

            case EDIT_AI_OPERATION_DELETE:
                mMenuViewModel.deleteAI();
                popView();
                break;

            case EDIT_TEXT_OPERATION_ADD:
                showPanelViewPrepare(id, EditPanelFragment.newInstance(false, false, true));
                break;

            case EDIT_TEXT_OPERATION_SPLIT:
            case EDIT_TEXT_MODULE_OPERATION_SPLIT:
                mMenuViewModel.splitText();
                break;

            case EDIT_TEXT_STATE_ADD:
                showPanelViewPrepare(id, EditPanelFragment.newInstance(false, false, false));
                break;

            case EDIT_TEXT_OPERATION_EDIT:
                mMaterialEditViewModel.setEditModel(true);
                showPanelViewPrepare(EDIT_TEXT_STATE_ADD, EditPanelFragment.newInstance(false, false, false));
                break;

            case EDIT_TEXT_OPERATION_COPY:
            case EDIT_TEXT_MODULE_OPERATION_COPY:
                mMenuViewModel.copyText();
                break;

            case EDIT_TEXT_OPERATION_ANIMATION:
                showPanelViewPrepare(id, EditPanelFragment.newInstance(false, true, false));
                break;

            case EDIT_TEXT_OPERATION_DELETE:
            case EDIT_TEXT_MODULE_OPERATION_DELETE:
                mMaterialEditViewModel.clearMaterialEditData();
                mMaterialEditViewModel.setEditModel(false);
                mMenuViewModel.deleteText();
                popView();
                break;

            case EDIT_SPECIAL_STATE_ADD:
            case EDIT_SPECIAL_OPERATION_ADD:
                if (mActivity != null && !mMenuViewModel.isCanAddEffect(false)) {
                    ToastWrapper
                        .makeText(mActivity, mActivity.getApplication().getResources().getString(R.string.noeffect),
                            Toast.LENGTH_SHORT)
                        .show();
                    return;
                }
                showPanelViewPrepare(id, EffectPanelFragment.newInstance(false));
                break;

            case EDIT_SPECIAL_OPERATION_REPLACE:
                showPanelViewPrepare(id, EffectPanelFragment.newInstance(true));
                break;

            case EDIT_SPECIAL_OPERATION_OBJECT:
            case EDIT_FILTER_OPERATION_OBJECT:
            case EDIT_ADJUST_OPERATION_OBJECT:
                showPanelViewPrepare(id, new ObjectFragment());
                break;

            case EDIT_SPECIAL_OPERATION_DELETE:
                mMenuViewModel.deleteSpecial();
                popView();
                break;

            case EDIT_FILTER_OPERATION_REPLACE:
                HVEEffect effect = editPreviewViewModel.getSelectedEffect();
                if (effect == null) {
                    return;
                }
                if (effect instanceof ColorFilterEffect) {
                    showPanelViewPrepare(id, ExclusiveFilterPanelFragment.newInstance());
                } else {
                    showPanelViewPrepare(id, FilterPanelFragment.newInstance(false));
                }

                break;

            case EDIT_ADJUST_OPERATION_REPLACE:
                showPanelViewPrepare(id, FilterAdjustPanelView.newInstance(false));
                break;

            case EDIT_FILTER_OPERATION_DELETE:
            case EDIT_ADJUST_OPERATION_DELETE:
                mMenuViewModel.deleteFilter(id);
                popView();
                break;

            case EDIT_PIP_OPERATION_DELETE:
                mMenuViewModel.deletePip();
                mMaterialEditViewModel.clearMaterialEditData();
                popView();
                break;

            case EDIT_PIP_OPERATION_MIX:
                showPanelViewPrepare(id, new PicInPicMixFragment());
                break;

            case EDIT_PIP_OPERATION_MASK:
                showPanelViewPrepare(id, MaskEffectFragment.newInstance(false));
                break;

            case EDIT_PIP_OPERATION_REPLACE:
                Intent intent2 = new Intent(mActivity, MediaPickActivity.class);
                HVEAsset asset2 = editPreviewViewModel.getSelectedAsset();
                if (asset2 == null) {
                    return;
                }
                intent2.putExtra("isReplaceAsset", true);
                intent2.putExtra(MediaPickActivity.ACTION_TYPE, MediaPickActivity.ACTION_ADD_PIP_MEDIA_TYPE);
                intent2.putExtra(MediaPickActivity.DURATION, asset2.getDuration());
                mActivity.startActivityForResult(intent2, ACTION_PIP_VIDEO_ASSET);
                break;

            case EDIT_PIP_OPERATION_COPY:
                mMenuViewModel.copyOtherLaneAsset();
                break;

            case EDIT_PIP_OPERATION_INVERTED:
                editPreviewViewModel.videoRevert();
                break;
            case EDIT_AUDIO_STATE_HWMUSIC:
            case EDIT_AUDIO_STATE_ADD:
                if (id == EDIT_AUDIO_STATE_ADD) {
                    HVEAsset audioAssets = editPreviewViewModel.getSelectedAsset();
                    if (!(audioAssets instanceof HVEAudioAsset)) {
                        return;
                    }
                }
                if (!LaneSizeCheckUtils.isCanAddAudio(editPreviewViewModel.getEditor(), mActivity)) {
                    ToastUtils.getInstance()
                        .showToast(mActivity, mActivity.getString(R.string.audio_lane_out_of_size), Toast.LENGTH_SHORT);
                    return;
                }
                if (mActivity != null && mMenuViewModel.isCanAddAudio()) {
                    gotoActivityFroResult(AudioPickActivity.class, VideoClipsActivity.ACTION_ADD_AUDIO_REQUEST_CODE);
                } else {
                    if (mActivity != null) {
                        ToastUtils.getInstance()
                            .showToast(mActivity,
                                mActivity.getApplication().getResources().getString(R.string.audio_music),
                                Toast.LENGTH_LONG);
                    }
                }
                break;

            case EDIT_AUDIO_STATE_SPLIT:
                HVEAsset cutAudioAsset = editPreviewViewModel.getSelectedAsset();
                long seekTime = editPreviewViewModel.getSeekTime();
                if (cutAudioAsset == null) {
                    break;
                }
                if ((seekTime - cutAudioAsset.getStartTime()) < 100 || (cutAudioAsset.getEndTime() - seekTime) < 100) {
                    ToastUtils.getInstance()
                        .showToast(mActivity, mActivity.getString(R.string.nodivision), Toast.LENGTH_SHORT);
                    break;
                }
                mMenuViewModel.splitAudio();
                break;
            case EDIT_AUDIO_STATE_DELETE:
                mMenuViewModel.deleteAudio();
                popView();
                break;

            case EDIT_AUDIO_STATE_COPY:
                mMenuViewModel.copyAudio();
                break;

            case EDIT_AUDIO_STATE_ACOUSTICS:
                if (!LaneSizeCheckUtils.isCanAddAudio(editPreviewViewModel.getEditor(), mActivity)) {
                    ToastUtils.getInstance()
                        .showToast(mActivity, mActivity.getString(R.string.audio_lane_out_of_size), Toast.LENGTH_SHORT);
                    return;
                }
                if (mActivity != null && mMenuViewModel.isCanAddAudio()) {
                    showPanelViewPrepare(id, SoundEffectFragment.newInstance());
                } else {
                    if (mActivity != null) {
                        ToastUtils.getInstance()
                            .showToast(mActivity,
                                mActivity.getApplication().getResources().getString(R.string.audio_effect),
                                Toast.LENGTH_SHORT);
                    }
                }
                break;

            case TRANSITION_PANEL:
                if (mMenuControlViewRouter != null) {
                    mMenuControlViewRouter.removeStackTopFragment();
                    showPanelViewPrepare(id, new TransitionPanelFragment());
                }
                break;

            case EDIT_RATIO_STATE:
            case EDIT_VIDEO_STATE_PROPORTION:
            case EDIT_VIDEO_OPERATION_PROPORTION:
                showPanelViewPrepare(id, new VideoProportionFragment());
                break;
            case EDIT_VIDEO_STATE_BLOCK_FACE:
                HVEAsset aiFaceAsset = editPreviewViewModel.getSelectedAsset();
                if (aiFaceAsset == null) {
                    aiFaceAsset = editPreviewViewModel.getMainLaneAsset();
                }

                if (!(aiFaceAsset instanceof HVEVisibleAsset)) {
                    return;
                }
                if (!isAiCanBeUsed((HVEVisibleAsset) aiFaceAsset, HVEEffect.HVEEffectType.FACEPRIVACY)) {
                    ToastWrapper.makeText(mActivity, mActivity.getResources().getString(R.string.ai_limit)).show();
                    return;
                }

                boolean isShowToast = SPManager.get(FACE_BLOCKING, mActivity).getBoolean(FACE_BLOCKING_KEY, false);
                if (!isShowToast) {
                    String maxFace =
                        mActivity.getResources().getQuantityString(R.plurals.face_blocking_max_face, 20, 20);
                    ToastWrapper.makeText(mActivity, maxFace, Toast.LENGTH_SHORT).show();
                    SPManager.get(FACE_BLOCKING, mActivity).put(FACE_BLOCKING_KEY, true);
                }
                editPreviewViewModel.setFaceBlockingEnter(id);
                break;
            case EDIT_VIDEO_STATE_HUMAN_TRACKING:
            case EDIT_VIDEO_OPERATION_HUMAN_TRACKING:
            case EDIT_PIP_OPERATION_HUMAN_TRACKING:
                if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                    return;
                }

                if (isCpu32()) {
                    ToastWrapper.makeText(mActivity, mActivity.getResources().getString(R.string.cpu_limit)).show();
                    return;
                }

                HVEAsset aiTrackAsset = editPreviewViewModel.getSelectedAsset();
                if (aiTrackAsset == null) {
                    aiTrackAsset = editPreviewViewModel.getMainLaneAsset();
                }

                if (!(aiTrackAsset instanceof HVEVisibleAsset)) {
                    return;
                }

                if (!isAiCanBeUsed((HVEVisibleAsset) aiTrackAsset, HVEEffect.HVEEffectType.HUMAN_TRACKING)) {
                    ToastWrapper.makeText(mActivity, mActivity.getResources().getString(R.string.ai_limit)).show();
                    return;
                }

                mPersonTrackingViewModel.setHumanTrackingEnter(id);
                mPersonTrackingViewModel.setHumanTrackingEntrance(id);
                break;
            case EDIT_VIDEO_STATE_AI_HAIR:
            case EDIT_VIDEO_OPERATION_AI_HAIR:
            case EDIT_PIP_OPERATION_AI_HAIR:
                if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                    return;
                }

                if (isCpu32()) {
                    ToastWrapper.makeText(mActivity, mActivity.getResources().getString(R.string.cpu_limit)).show();
                    return;
                }

                HVEAsset aiHairAsset = editPreviewViewModel.getSelectedAsset();
                if (aiHairAsset == null) {
                    aiHairAsset = editPreviewViewModel.getMainLaneAsset();
                }

                if (!(aiHairAsset instanceof HVEVisibleAsset)) {
                    return;
                }

                if (!isAiCanBeUsed((HVEVisibleAsset) aiHairAsset, HVEEffect.HVEEffectType.HAIR_DYEING)) {
                    ToastWrapper.makeText(mActivity, mActivity.getResources().getString(R.string.ai_limit)).show();
                    return;
                }

                mActivity.runOnUiThread(() -> {
                    LoadingDialogUtils.getInstance()
                        .builder(mActivity, mActivity.getLifecycle())
                        .show(mActivity.getString(R.string.intelligent_processing), true);
                });
                ((HVEVisibleAsset) aiHairAsset).initHairDyeingEngine(new HVEAIInitialCallback() {
                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onSuccess() {
                        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                            return;
                        }

                        mActivity.runOnUiThread(() -> {
                            LoadingDialogUtils.getInstance().dismiss();
                            AiHairFragment aiHairFragment = AiHairFragment.newInstance();
                            MenuClickManager.getInstance()
                                .showPanelViewPrepare(EDIT_VIDEO_STATE_AI_HAIR, aiHairFragment);
                        });
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                            return;
                        }

                        mActivity.runOnUiThread(() -> {
                            LoadingDialogUtils.getInstance().dismiss();
                            ToastWrapper
                                .makeText(mActivity, mActivity.getString(R.string.result_illegal), Toast.LENGTH_SHORT)
                                .show();
                        });
                    }
                });
                break;
            case EDIT_VIDEO_STATE_AI_FUN:
            case EDIT_VIDEO_OPERATION_AI_FUN:
            case EDIT_PIP_OPERATION_AI_FUN:
                if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
                    break;
                }

                if (isCpu32()) {
                    ToastWrapper.makeText(mActivity, mActivity.getResources().getString(R.string.cpu_limit)).show();
                    return;
                }

                HVEAsset aiFunAsset = editPreviewViewModel.getSelectedAsset();
                if (aiFunAsset == null) {
                    aiFunAsset = editPreviewViewModel.getMainLaneAsset();
                }

                if (!(aiFunAsset instanceof HVEVisibleAsset)) {
                    return;
                }

                if (!isAiCanBeUsed((HVEVisibleAsset) aiFunAsset, HVEEffect.HVEEffectType.AI_COLOR)
                    && !isAiCanBeUsed((HVEVisibleAsset) aiFunAsset, HVEEffect.HVEEffectType.FACE_REENACT)) {
                    ToastWrapper.makeText(mActivity, mActivity.getResources().getString(R.string.ai_limit)).show();
                    return;
                }

                boolean isFirstAiFun = SPManager.get(AI_FUN, mActivity).getBoolean(AI_FUN_KEY, true);
                if (isFirstAiFun) {
                    AIBlockingHintDialog aiFunDialog =
                        new AIBlockingHintDialog(mActivity, mActivity.getString(R.string.cut_second_menu_fun),
                            mActivity.getString(R.string.fun_description));
                    aiFunDialog.setOnPositiveClickListener(() -> {
                        SPManager.get(AI_FUN, mActivity).put(AI_FUN_KEY, false);
                        MenuClickManager.getInstance().showPanelViewPrepare(id, AiFunFragment.newInstance(id));
                    });
                    aiFunDialog.show();
                } else {
                    MenuClickManager.getInstance().showPanelViewPrepare(id, AiFunFragment.newInstance(id));
                }
                break;
            default:
                break;
        }
    }

    public void showPanelViewPrepare(int id, Fragment fragment) {
        mMenuControlViewRouter.showFragment(id, fragment);
    }

    private boolean isAiCanBeUsed(HVEVisibleAsset asset, HVEEffect.HVEEffectType type) {
        HVEEffect.HVEEffectType assetType = asset.getAIEffectType();
        return assetType == null || assetType.equals(type);
    }

    private boolean isCpu32() {
        String deviceType;
        try {
            deviceType = CpuUtils.getArchType();
        } catch (IOException e) {
            return false;
        }
        if (CpuUtils.CPU_ARCHITECTURE_TYPE_32.equals(deviceType)) {
            return true;
        }
        return false;
    }

    private List<Integer> getCloudMaterialsIdList() {
        cloudMaterialsIdList = new ArrayList<>();
        cloudMaterialsIdList.add(EDIT_FILTER_STATE_ADD);
        cloudMaterialsIdList.add(EDIT_FILTER_STATE_EXCLUSIVE);
        cloudMaterialsIdList.add(EDIT_VIDEO_STATE_FILTER);
        cloudMaterialsIdList.add(EDIT_PIP_OPERATION_FILTER);
        cloudMaterialsIdList.add(EDIT_STICKER_STATE_ADD_STICKER);
        cloudMaterialsIdList.add(EDIT_TEXT_STATE_STICKER);
        cloudMaterialsIdList.add(EDIT_STICKER_OPERATION_REPLACE);
        cloudMaterialsIdList.add(EDIT_SPECIAL_STATE_ADD);
        cloudMaterialsIdList.add(EDIT_AUDIO_STATE_ACOUSTICS);
        cloudMaterialsIdList.add(EDIT_AUDIO_STATE_HWMUSIC);
        cloudMaterialsIdList.add(EDIT_TEXT_MODULE_OPERATION_REPLACE);
        cloudMaterialsIdList.add(TRANSITION_PANEL);
        cloudMaterialsIdList.add(EDIT_RATIO_STATE);
        cloudMaterialsIdList.add(EDIT_BACKGROUND_STATE);
        cloudMaterialsIdList.add(EDIT_PIP_OPERATION_ANIMATION);
        cloudMaterialsIdList.add(EDIT_VIDEO_STATE_ANIMATION);
        cloudMaterialsIdList.add(EDIT_VIDEO_OPERATION_ANIMATION);
        cloudMaterialsIdList.add(EDIT_TEXT_OPERATION_ANIMATION);
        cloudMaterialsIdList.add(EDIT_STICKER_OPERATION_ANIMATION);
        cloudMaterialsIdList.add(EDIT_SPECIAL_OPERATION_REPLACE);
        cloudMaterialsIdList.add(EDIT_FILTER_OPERATION_REPLACE);
        cloudMaterialsIdList.add(EDIT_TEXT_STATE_ADD_WATER_MARK);
        cloudMaterialsIdList.add(EDIT_VIDEO_STATE_SPEED);
        cloudMaterialsIdList.add(EDIT_PIP_OPERATION_SPEED);
        cloudMaterialsIdList.add(EDIT_VIDEO_OPERATION_SPEED);
        cloudMaterialsIdList.add(EDIT_VIDEO_OPERATION_MASK);
        cloudMaterialsIdList.add(EDIT_VIDEO_STATE_MASK);
        cloudMaterialsIdList.add(EDIT_PIP_OPERATION_MASK);
        cloudMaterialsIdList.add(EDIT_VIDEO_STATE_AI_HAIR);
        cloudMaterialsIdList.add(EDIT_VIDEO_OPERATION_AI_HAIR);
        cloudMaterialsIdList.add(EDIT_PIP_OPERATION_AI_HAIR);
        return cloudMaterialsIdList;
    }

    public List<Integer> getUnableOperateIds(int type) {
        List<Integer> unableMenuId = new ArrayList<>();
        switch (type) {
            case 2:
                unableMenuId.add(EDIT_VIDEO_OPERATION_SPEED);
                unableMenuId.add(EDIT_VIDEO_OPERATION_VOLUME);
                unableMenuId.add(EDIT_VIDEO_OPERATION_INVERTED);
                unableMenuId.add(EDIT_VIDEO_OPERATION_HUMAN_TRACKING);
                break;
            case 3:
                unableMenuId.add(EDIT_VIDEO_OPERATION_SPLIT);
                unableMenuId.add(EDIT_VIDEO_OPERATION_SPEED);
                unableMenuId.add(EDIT_VIDEO_OPERATION_VOLUME);
                unableMenuId.add(EDIT_VIDEO_OPERATION_ANIMATION);
                unableMenuId.add(EDIT_VIDEO_OPERATION_FILTER_ADD);
                unableMenuId.add(EDIT_VIDEO_OPERATION_TAILORING);
                unableMenuId.add(EDIT_VIDEO_OPERATION_ADJUST);
                unableMenuId.add(EDIT_VIDEO_OPERATION_COPY);
                unableMenuId.add(EDIT_VIDEO_OPERATION_REPLACE);
                unableMenuId.add(EDIT_VIDEO_OPERATION_MASK);
                unableMenuId.add(EDIT_VIDEO_OPERATION_MIRROR);
                unableMenuId.add(EDIT_VIDEO_OPERATION_TRANSPARENCY);
                unableMenuId.add(EDIT_VIDEO_OPERATION_INVERTED);
                unableMenuId.add(EDIT_VIDEO_OPERATION_AI_HAIR);
                break;
            case 4:
                unableMenuId.add(EDIT_PIP_OPERATION_SPLIT);
                unableMenuId.add(EDIT_PIP_OPERATION_SPEED);
                unableMenuId.add(EDIT_PIP_OPERATION_VOLUME);
                unableMenuId.add(EDIT_PIP_OPERATION_INVERTED);
                unableMenuId.add(EDIT_PIP_OPERATION_HUMAN_TRACKING);
                break;
            case 5:
                unableMenuId.add(EDIT_PIP_OPERATION_SPLIT);
                break;
            case 6:
                unableMenuId.add(EDIT_TEXT_OPERATION_EDIT);
                break;
            case 7:
                unableMenuId.add(EDIT_VIDEO_OPERATION_AI_HAIR);
                break;
            case 8:
                unableMenuId.add(EDIT_VIDEO_STATE_HUMAN_TRACKING);
                unableMenuId.add(EDIT_VIDEO_OPERATION_HUMAN_TRACKING);
                unableMenuId.add(EDIT_PIP_OPERATION_HUMAN_TRACKING);
                break;
            case 9:
                unableMenuId.add(EDIT_VIDEO_STATE_INVERTED);
                unableMenuId.add(EDIT_VIDEO_OPERATION_INVERTED);
                unableMenuId.add(EDIT_PIP_OPERATION_INVERTED);
                break;
            default:
                break;
        }
        return unableMenuId;
    }

    public void translation(int index) {
        int position = 0;
        switch (index) {
            case EDIT_VIDEO_STATE:
                position = 0;
                break;
            case EDIT_AUDIO_STATE:
                position = 1;
                break;
            case EDIT_STICKER_STATE:
                position = 2;
                break;
            case EDIT_TEXT_STATE:
                position = 3;
                break;
            case EDIT_PIP_STATE:
                position = 4;
                break;
            case EDIT_SPECIAL_STATE:
                position = 5;
                break;
            case EDIT_FILTER_STATE:
                position = 6;
                break;
            default:
                break;
        }
        menuContentLayout.setCurrentFirstMenu(position);
    }

    public HVELane.HVELaneType getLaneType(int id) {
        HVELane.HVELaneType laneType;
        if (id == EDIT_PIP_STATE) {
            laneType = HVELane.HVELaneType.VIDEO;
        } else {
            laneType = HVELane.HVELaneType.STICKER;
        }
        return laneType;
    }

    private void gotoActivityFroResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(mActivity, cls);
        mActivity.startActivityForResult(intent, requestCode);
    }

    public interface OnAssetDeleteListener {
        void onDelete(HVEAsset asset);
    }

}
