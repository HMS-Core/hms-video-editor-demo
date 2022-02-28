
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.huawei.hms.videoeditor.sdk.asset.HVEAsset.HVEAssetType.AUDIO;
import static com.huawei.hms.videoeditor.sdk.asset.HVEAsset.HVEAssetType.STICKER;
import static com.huawei.hms.videoeditor.sdk.asset.HVEAsset.HVEAssetType.WORD;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_ADJUST_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_AI_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_AUDIO_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_BACKGROUND_STATE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_FILTER_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_PIP_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_RATIO_STATE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_SPECIAL_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_STICKER_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_STICKER_OPERATION_ANIMATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_TEXT_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_TEXT_OPERATION_EDIT;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_TEXT_STATE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_OPERATION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_HUMAN_TRACKING;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_INVERTED;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_MASK;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_MOVE;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_PROPORTION;
import static com.huawei.hms.videoeditor.ui.mediaeditor.trackview.bean.MainViewState.EDIT_VIDEO_STATE_SPEED;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEStickerAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEWordAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.GraffitiManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.graffiti.view.GraffitiView;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditData;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.persontrack.PersonTrackingViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.MaskEffectViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.MaskEffectContainerView;
import com.huawei.hms.videoeditor.ui.mediaeditor.sticker.fragment.StickerPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.fragment.EditPanelFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";

    private static final String EDIT_PANEL_FRAGMENT_TAG = "addTextSticker";

    protected ViewModelProvider.AndroidViewModelFactory mFactory;

    private VideoClipsActivity mActivity;

    private int mCurrentViewId = EDIT_VIDEO_STATE;

    private EditMenuContentLayout menuContentLayout;

    private ViewGroup baseViewGroup;

    private FrameLayout panelContainer;

    private FrameLayout fragmentContainer;

    private MaskEffectContainerView mask_container_view;

    private GraffitiView mGraffitiView;

    private TextEditViewModel mTextEditViewModel;

    private MaskEffectViewModel mMaskEffectViewModel;

    private EditItemViewModel mEditViewModel;

    private EditPreviewViewModel mEditPreviewViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private PersonTrackingViewModel mPersonTrackingViewModel;

    private MenuViewModel mMenuViewModel;

    private long mLastClickTime = 0;

    private static final long SPACE_TIMES = 150;

    private List<Integer> mUnVisibleIds = new ArrayList<>();

    public MenuFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if ((activity instanceof VideoClipsActivity)) {
            mActivity = (VideoClipsActivity) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity != null) {
            mFactory = new ViewModelProvider.AndroidViewModelFactory(mActivity.getApplication());
            initViewModel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_control_view_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mActivity == null) {
            return;
        }
        initView(view);
        initData();
        initEvent(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mEditPreviewViewModel.isRecordAudio()) {
            mEditPreviewViewModel.setIsBack(true);
        }
        MenuConfig.getInstance().initMenuConfig(mActivity);
        List<EditMenuBean> menuBeans = MenuConfig.getInstance().getEditMenus();
        menuContentLayout.initFirstMenuData(menuBeans, mUnVisibleIds);
    }

    private void initViewModel() {
        mTextEditViewModel = new ViewModelProvider(mActivity, mFactory).get(TextEditViewModel.class);
        mMaskEffectViewModel = new ViewModelProvider(mActivity, mFactory).get(MaskEffectViewModel.class);
        mEditViewModel = new ViewModelProvider(mActivity, mFactory).get(EditItemViewModel.class);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mMenuViewModel = new ViewModelProvider(mActivity, mFactory).get(MenuViewModel.class);
        mPersonTrackingViewModel = new ViewModelProvider(mActivity, mFactory).get(PersonTrackingViewModel.class);
    }

    private void initView(View view) {
        baseViewGroup = view.findViewById(R.id.base_layout);
        menuContentLayout = view.findViewById(R.id.edit_menu_content_layout);
        panelContainer = view.findViewById(R.id.panelContainer);
        fragmentContainer = view.findViewById(R.id.fragment_container);
        mask_container_view = view.findViewById(R.id.mask_container_view);
        mGraffitiView = view.findViewById(R.id.graffiti_view);
        MenuClickManager.getInstance()
            .init(mActivity, menuContentLayout, mMenuViewModel, mEditPreviewViewModel, mMaterialEditViewModel,
                mPersonTrackingViewModel);
    }

    private void initData() {
        initMenu();
    }

    private void initMenu() {
        mUnVisibleIds.clear();
        MenuConfig.getInstance().initMenuConfig(mActivity);
        List<EditMenuBean> menuBeans = MenuConfig.getInstance().getEditMenus();
        menuContentLayout.initFirstMenuData(menuBeans, mUnVisibleIds);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MenuConfig.getInstance().getEditMenus().isEmpty()) {
            initMenu();
        }
        MenuClickManager.getInstance()
            .update(mActivity, menuContentLayout, mMenuViewModel, mEditPreviewViewModel, mMaterialEditViewModel,
                mPersonTrackingViewModel);
    }

    private void initEvent(View view) {
        view.findViewById(R.id.graffiti_viewparent).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        GraffitiManager manager = new GraffitiManager(mGraffitiView, view.findViewById(R.id.graffiti_layout), mActivity,
            mEditPreviewViewModel.getEditor());
        manager.start();

        menuContentLayout.setOnMenuClickListener(new EditMenuContentLayout.OnMenuClickListener() {
            @Override
            public void onFirstMenuClick(EditMenuBean firstMenu) {
                if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                    return;
                }
                mActivity.pauseTimeLine();
                popView();
                mCurrentViewId = firstMenu.getId();
                if (mCurrentViewId == -1 || mMaterialEditViewModel == null || mEditPreviewViewModel == null) {
                    return;
                }
                mMaterialEditViewModel.setCurrentFirstMenuId(firstMenu.getId());
                mMaterialEditViewModel.setEditModel(firstMenu.getId() != EDIT_TEXT_STATE);
                if (mCurrentViewId == EDIT_VIDEO_STATE) {
                    mEditPreviewViewModel.setIsDrawWave();
                }

                mEditViewModel.getItemsFirstSelected().postValue(firstMenu);

                if (mCurrentViewId == EDIT_RATIO_STATE || mCurrentViewId == EDIT_BACKGROUND_STATE) {
                    MenuClickManager.getInstance().handlerClickEvent(mCurrentViewId);
                }
            }

            @Override
            public void onSecondMenuClick(EditMenuBean secondMenu) {
                if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                    return;
                }
                mActivity.pauseTimeLine();
                mEditViewModel.getItemsSecondSelected().postValue(secondMenu);

                MenuClickManager.getInstance().handlerClickEvent(secondMenu.getId());
                if (mask_container_view != null) {
                    mask_container_view.setVisibility(secondMenu.getId() == EDIT_VIDEO_STATE_MASK ? VISIBLE : GONE);
                }
            }

            @Override
            public void onOperateMenuClick(EditMenuBean operateMenu) {
                if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                    return;
                }
                mActivity.pauseTimeLine();
                if (System.currentTimeMillis() - mLastClickTime < SPACE_TIMES) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                if (mEditPreviewViewModel == null) {
                    return;
                }
                MenuClickManager.getInstance().handlerClickEvent(operateMenu.getId());
            }

            @Override
            public void onOperateBackClick() {
                mEditPreviewViewModel.setSelectedUUID("");
            }
        });

        panelContainer.setOnTouchListener((v, event) -> true);

        fragmentContainer.setOnTouchListener((v, event) -> true);

        mMaterialEditViewModel.getMaterialDelete().observe(getViewLifecycleOwner(), new Observer<MaterialEditData>() {
            @Override
            public void onChanged(MaterialEditData data) {
                if (data.getAsset() != null && data.getMaterialType() == MaterialEditData.MaterialType.STICKER
                    || data.getMaterialType() == MaterialEditData.MaterialType.WORD) {
                    mMaterialEditViewModel.clearMaterialEditData();
                    mEditPreviewViewModel.setNeedAddTextOrSticker(false);
                    mMenuViewModel.deleteAsset(data.getAsset());
                    mEditPreviewViewModel.updateTimeLine();
                    if (getViewStack() != null && !getViewStack().isEmpty()) {
                        MenuControlViewRouter.Panel panel = getViewStack().lastElement();
                        if (panel.object instanceof BaseFragment) {
                            mActivity.onBackPressed();
                        }
                    } else {
                        popView();
                    }

                }
            }
        });

        mMaterialEditViewModel.getStickerEdit().observe(getViewLifecycleOwner(), new Observer<MaterialEditData>() {
            @Override
            public void onChanged(MaterialEditData data) {
                if (getViewStack() != null && !getViewStack().isEmpty()) {
                    MenuControlViewRouter.Panel panel = getViewStack().lastElement();
                    if (panel.object instanceof BaseFragment) {
                        popView();
                    }
                }
                MenuClickManager.getInstance().handlerClickEvent(EDIT_STICKER_OPERATION_ANIMATION);
            }
        });

        mMaterialEditViewModel.getTextDefaultEdit().observe(getViewLifecycleOwner(), new Observer<MaterialEditData>() {
            @Override
            public void onChanged(MaterialEditData data) {
                if (mEditPreviewViewModel.getTimeLine() == null) {
                    return;
                }
                if (!(data.getAsset() instanceof HVEWordAsset)) {
                    return;
                }
                mMaterialEditViewModel.setEditModel(true);

                if (getViewStack() != null && !getViewStack().isEmpty()) {
                    MenuControlViewRouter.Panel panel = getViewStack().lastElement();
                    if (panel.object instanceof EditPanelFragment) {
                        if (data.getAsset() != null) {
                            mEditPreviewViewModel.setTableIndex(0);
                            mEditPreviewViewModel.setEditPanelInputValue(((HVEWordAsset) data.getAsset()).getText());
                        }
                    } else {
                        mActivity.onBackPressed();
                    }
                } else {
                    mEditPreviewViewModel.setEditPanelInputValue(getResources().getString(R.string.inputtext));
                    MenuClickManager.getInstance().handlerClickEvent(EDIT_TEXT_OPERATION_EDIT);
                }

            }
        });

        mMaterialEditViewModel.getMaterialCopy().observe(getViewLifecycleOwner(), new Observer<MaterialEditData>() {
            @Override
            public void onChanged(MaterialEditData data) {
                switch (data.getMaterialType()) {
                    case WORD:
                        mMenuViewModel.copyText();
                        break;
                    case STICKER:
                        mMenuViewModel.copySticker();
                        break;
                    default:
                        break;
                }
            }
        });

        mEditPreviewViewModel.getClearGraffitView().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean && mGraffitiView != null) {
                    mGraffitiView.clear();
                }
            }
        });

        mEditPreviewViewModel.isMoveAsset().observe(getViewLifecycleOwner(), move -> {
            if (move) {
                MenuClickManager.getInstance().handlerClickEvent(EDIT_VIDEO_STATE_MOVE);
                mEditPreviewViewModel.setMoveAsset(false);
            }
        });

        mEditPreviewViewModel.getTransition().observe(getViewLifecycleOwner(), id -> {
            MenuClickManager.getInstance().handlerClickEvent(id);
        });

        mEditPreviewViewModel.getMainLaneAssetChanged().observe(getViewLifecycleOwner(), hveAsset -> {
            List<Integer> ableIds = new ArrayList<>();
            if (mCurrentViewId != EDIT_VIDEO_STATE) {
                if (menuContentLayout != null) {
                    menuContentLayout.updateUnAbleMenus(false, ableIds);
                }
                return;
            }
            if (hveAsset != null) {
                if (hveAsset instanceof HVEImageAsset) {
                    ableIds.add(EDIT_VIDEO_STATE_SPEED);
                    ableIds.add(EDIT_VIDEO_STATE_HUMAN_TRACKING);
                    if (menuContentLayout != null) {
                        menuContentLayout.updateUnAbleMenus(false, ableIds);
                    }
                } else if (hveAsset instanceof HVEVideoAsset) {
                    if (menuContentLayout != null) {
                        boolean isVideoReverse = ((HVEVideoAsset) hveAsset).isVideoReverse();
                        if (isVideoReverse) {
                            ableIds.add(EDIT_VIDEO_STATE_HUMAN_TRACKING);
                        }
                        boolean isHumanTracking = isContainHumanTrackingEffect(hveAsset);
                        if (isHumanTracking) {
                            ableIds.add(EDIT_VIDEO_STATE_INVERTED);
                        }
                        menuContentLayout.updateUnAbleMenus(false, ableIds);
                    }
                }

            } else {
                ableIds.add(EDIT_VIDEO_STATE_PROPORTION);
                if (menuContentLayout != null) {
                    menuContentLayout.updateUnAbleMenus(true, ableIds);
                }
            }
        });

        mEditPreviewViewModel.getSelectedUUID().observe(getViewLifecycleOwner(), uuid -> {
            if (mEditPreviewViewModel.getSelectedAsset() == null && mEditPreviewViewModel.getSelectedEffect() == null) {
                if (mCurrentViewId != -1) {
                    if (mEditPreviewViewModel.isEditTextStatus() || mEditPreviewViewModel.isEditTextTemplateStatus()
                        || mEditPreviewViewModel.isEditStickerStatus() || mEditPreviewViewModel.isTrailerStatus()
                        || mEditPreviewViewModel.isFaceBlockingStatus()
                        || mEditPreviewViewModel.isPersonTrackingStatus()) {
                        menuContentLayout.hideOperateMenu();
                        return;
                    }
                    popView();
                    menuContentLayout.hideOperateMenu();
                    mEditViewModel.setIsShowSecondItem(true);
                }
                mMaterialEditViewModel.clearMaterialEditData();
                if (mCurrentViewId == EDIT_TEXT_STATE) {
                    if (EditorManager.getInstance().getTimeLine() == null) {
                        return;
                    }
                    mEditPreviewViewModel.getEditor()
                        .refresh(EditorManager.getInstance().getTimeLine().getCurrentTime());
                }
                return;
            }
            mEditViewModel.setIsShowSecondItem(false);

            int state = 0;
            HVEAsset selectedHveAsset;
            HVEEffect effect;
            selectedHveAsset = mEditPreviewViewModel.getSelectedAsset();

            if (EditorManager.getInstance().getTimeLine() == null) {
                return;
            }
            List<Integer> unVisibleIds = new ArrayList<>();
            List<Integer> unableIds = new ArrayList<>();
            if (selectedHveAsset != null) {
                HVEAsset.HVEAssetType type = selectedHveAsset.getType();
                if (type == WORD) {
                    state = EDIT_TEXT_OPERATION;
                    if (selectedHveAsset instanceof HVEWordAsset) {
                        if (getViewStack() != null && !getViewStack().isEmpty()) {
                            MenuControlViewRouter.Panel panel = getViewStack().lastElement();
                            if (panel.object instanceof EditPanelFragment) {
                                mEditPreviewViewModel
                                    .setEditPanelInputValue(((HVEWordAsset) selectedHveAsset).getText());
                            } else {
                                if (mEditPreviewViewModel.isEditTextTemplateStatus()) {
                                    mActivity.showInputLayout(false);
                                } else {
                                    mActivity.onBackPressed();
                                }
                            }
                        } else {
                            if (mEditPreviewViewModel.isEditTextTemplateStatus()) {
                                mActivity.showInputLayout(false);
                            }
                        }
                        mMaterialEditViewModel.addMaterialEditData(new MaterialEditData(
                            (HVEVisibleAsset) selectedHveAsset, MaterialEditData.MaterialType.WORD));
                    }
                } else if (type == STICKER) {
                    state = EDIT_STICKER_OPERATION;
                    if (selectedHveAsset instanceof HVEStickerAsset) {
                        HVEStickerAsset stickerAsset = (HVEStickerAsset) selectedHveAsset;

                        if (getViewStack() != null && !getViewStack().isEmpty()) {
                            MenuControlViewRouter.Panel panel = getViewStack().lastElement();
                            if (!(panel.object instanceof StickerPanelFragment)) {
                                mActivity.onBackPressed();
                            }
                        } else {
                            if (mEditPreviewViewModel.isEditTextTemplateStatus()) {
                                mActivity.showInputLayout(false);
                            }
                        }
                        mMaterialEditViewModel.addMaterialEditData(
                            new MaterialEditData(stickerAsset, MaterialEditData.MaterialType.STICKER));
                    }

                } else if (type == AUDIO) {
                    state = EDIT_AUDIO_OPERATION;
                } else {
                    if (selectedHveAsset.getLaneIndex() == 0) {
                        state = EDIT_VIDEO_OPERATION;
                        if (selectedHveAsset instanceof HVEImageAsset) {
                            unableIds.addAll(MenuClickManager.getInstance().getUnableOperateIds(2));
                        }
                        if (selectedHveAsset instanceof HVEVideoAsset) {
                            boolean isVideoReverse = ((HVEVideoAsset) selectedHveAsset).isVideoReverse();
                            if (isVideoReverse) {
                                unableIds.addAll(MenuClickManager.getInstance().getUnableOperateIds(8));
                            }
                            boolean isHumanTracking =isContainHumanTrackingEffect(selectedHveAsset);
                            if (isHumanTracking) {
                                unableIds.addAll(MenuClickManager.getInstance().getUnableOperateIds(9));
                            }
                        }
                        mMaterialEditViewModel.addMaterialEditData(new MaterialEditData(
                            (HVEVisibleAsset) selectedHveAsset, MaterialEditData.MaterialType.MAIN_LANE));
                    } else {
                        state = EDIT_PIP_OPERATION;

                        if (selectedHveAsset instanceof HVEImageAsset) {
                            unableIds.addAll(MenuClickManager.getInstance().getUnableOperateIds(4));
                        }
                        if (selectedHveAsset instanceof HVEVideoAsset) {
                            unableIds.addAll(MenuClickManager.getInstance().getUnableOperateIds(5));
                            boolean isVideoReverse = ((HVEVideoAsset) selectedHveAsset).isVideoReverse();
                            if (isVideoReverse) {
                                unableIds.addAll(MenuClickManager.getInstance().getUnableOperateIds(8));
                            }
                            boolean isHumanTracking = isContainHumanTrackingEffect(selectedHveAsset);
                            if (isHumanTracking) {
                                unableIds.addAll(MenuClickManager.getInstance().getUnableOperateIds(9));
                            }
                        }
                        mMaterialEditViewModel.addMaterialEditData(new MaterialEditData(
                            (HVEVisibleAsset) selectedHveAsset, MaterialEditData.MaterialType.PIP_LANE));
                    }

                    if (EditorManager.getInstance().getTimeLine() == null) {
                        return;
                    }
                    if (selectedHveAsset.isVisible(EditorManager.getInstance().getTimeLine().getCurrentTime())) {
                        mMaskEffectViewModel.setHveVideoAsset(selectedHveAsset);
                    }
                }
            } else {
                effect = mEditPreviewViewModel.getSelectedEffect();
                if (effect != null) {
                    if (effect.getEffectType() == HVEEffect.HVEEffectType.NORMAL) {
                        state = EDIT_SPECIAL_OPERATION;
                    } else if (effect.getEffectType() == HVEEffect.HVEEffectType.FILTER) {
                        state = EDIT_FILTER_OPERATION;
                    } else if (effect.getEffectType() == HVEEffect.HVEEffectType.ADJUST) {
                        state = EDIT_ADJUST_OPERATION;
                    } else if (effect.getEffectType() == HVEEffect.HVEEffectType.MASK) {
                        state = EDIT_AI_OPERATION;
                    }
                }
            }

            if (state == 0) {
                return;
            }

            if (menuContentLayout != null) {
                menuContentLayout.showOperateMenu(state, unVisibleIds, unableIds);
            }
        });

        mTextEditViewModel.getWordDecorLiveData().observe(getViewLifecycleOwner(), wordStyle -> {
            if (mEditPreviewViewModel.getTimeLine() == null) {
                return;
            }

            HVEAsset asset = mEditPreviewViewModel.getSelectedAsset();
            if (asset == null && mEditPreviewViewModel.isAddCoverTextStatus()) {
                asset = mMaterialEditViewModel.getSelectAsset();
            }
            if (asset == null) {
                return;
            }
            if (asset instanceof HVEWordAsset) {
                HVEWordAsset wordAsset = (HVEWordAsset) asset;
                wordAsset.setWordStyle(wordStyle);
            }
            if (EditorManager.getInstance().getTimeLine() == null) {
                return;
            }
            HVEAsset finalAsset = asset;
            mEditPreviewViewModel.getEditor()
                .seekTimeLine(EditorManager.getInstance().getTimeLine().getCurrentTime(), () -> {
                    if (mActivity == null) {
                        return;
                    }

                    mActivity.runOnUiThread(() -> {
                        if (finalAsset instanceof HVEWordAsset) {
                            HVEWordAsset wordAsset = (HVEWordAsset) finalAsset;
                            mMaterialEditViewModel.addMaterialEditData(
                                new MaterialEditData((HVEVisibleAsset) finalAsset, MaterialEditData.MaterialType.WORD));
                        }
                    });
                });
        });

        mMaskEffectViewModel.getIsShow().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            boolean isShow = false;

            @Override
            public void onChanged(Boolean aBoolean) {
                if (mask_container_view == null) {
                    return;
                }
                if (aBoolean) {
                    if (mMaterialEditViewModel.getMaterialList() != null
                        && !mMaterialEditViewModel.getMaterialList().isEmpty()) {
                        isShow = true;
                        mMaterialEditViewModel.setMaterialEditShow(false);
                    } else {
                        isShow = false;
                    }
                    mask_container_view.setVisibility(View.VISIBLE);
                } else {
                    mask_container_view.setVisibility(View.GONE);
                    if (isShow) {
                        mMaterialEditViewModel.setMaterialEditShow(true);
                    }
                }
            }
        });

        mEditPreviewViewModel.getRefreshCurrentMenuControl().observe(getViewLifecycleOwner(), level -> {
            MenuClickManager.getInstance().translation(level);
        });

        initAnimation();
    }

    private boolean isContainHumanTrackingEffect(HVEAsset asset) {
        for (HVEEffect effect : asset.getEffects()) {
            if (effect.getEffectType() == HVEEffect.HVEEffectType.HUMAN_TRACKING) {
                return true;
            }
        }
        return false;
    }

    private void initAnimation() {
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view,
                int transitionType) {

            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if (transitionType != LayoutTransition.APPEARING) {
                    return;
                }
                if (container != fragmentContainer) {
                    return;
                }
                if (mEditPreviewViewModel == null) {
                    return;
                }
                if (mEditPreviewViewModel.isTrailerStatus()) {
                    return;
                }
                if (mEditViewModel.getItemsSecondSelected().getValue() != null) {
                    if (EDIT_PANEL_FRAGMENT_TAG.equals(view.getTag())) {
                        if (mMaterialEditViewModel.isEditModel()) {
                            SmartLog.e(TAG, "isEditModel is true");
                            return;
                        }
                        HVEWordAsset wordAsset = null;
                        if (mEditViewModel.getItemsSecondSelected().getValue() != null) {
                            wordAsset = mMenuViewModel.addText(getResources().getString(R.string.inputtext));
                        }
                        if (wordAsset == null) {
                            SmartLog.e(TAG, "wordAsset is null");
                            return;
                        }
                        HVEWordAsset finalWordAsset = wordAsset;
                        HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
                        HVETimeLine timeLine = mEditPreviewViewModel.getTimeLine();
                        if (editor == null || timeLine == null) {
                            return;
                        }
                        editor.seekTimeLine(timeLine.getCurrentTime(), () -> {
                            if (mActivity == null) {
                                return;
                            }
                            mActivity.runOnUiThread(() -> {
                                mMaterialEditViewModel.addMaterialEditData(
                                    new MaterialEditData(finalWordAsset, MaterialEditData.MaterialType.WORD));
                                if (mTextEditViewModel.getLastWordStyle() != null) {
                                    mTextEditViewModel.setWordStyle(mTextEditViewModel.getLastWordStyle());
                                }
                            });
                        });
                    }
                }

            }
        });
        fragmentContainer.setLayoutTransition(layoutTransition);
        panelContainer.setLayoutTransition(layoutTransition);
    }

    public void setOnAssetDeleteListener(MenuClickManager.OnAssetDeleteListener listener) {
        MenuClickManager.getInstance().setOnAssetDeleteListener(listener);
    }

    public void showMenu(boolean isShow) {
        baseViewGroup.setVisibility(isShow ? VISIBLE : GONE);
    }

    public boolean popView() {
        return MenuClickManager.getInstance().popView();
    }

    public Stack<MenuControlViewRouter.Panel> getViewStack() {
        return MenuClickManager.getInstance().getViewStack();
    }

    public void showFragment(int addCoverImageCode, Fragment fragment) {
        MenuClickManager.getInstance().showPanelViewPrepare(addCoverImageCode, fragment);
    }

    public void setCurrentFirstMenu(int position) {
        if (menuContentLayout != null) {
            menuContentLayout.setCurrentFirstMenu(position);
        }
    }
}