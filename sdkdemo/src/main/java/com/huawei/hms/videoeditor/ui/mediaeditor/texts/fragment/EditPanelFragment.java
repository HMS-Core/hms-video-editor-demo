
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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.fragment;

import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEWordAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEStickerLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.view.navigator.FixFragmentNavigator;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopInfo;
import com.huawei.hms.videoeditor.ui.common.view.tab.TabTopLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.animation.AnimationPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextPanelViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import java.util.ArrayList;
import java.util.List;

public class EditPanelFragment extends BaseFragment {

    private static final String TAG = "EditPanelFragment";

    private EditText mEditText;

    private LinearLayout certainLayout;

    private TabTopLayout mTabTopLayout;

    private MaterialEditViewModel mMaterialEditViewModel;

    private NavController mNavController;

    private EditPreviewViewModel mEditPreviewViewModel;

    private AnimationPanelViewModel aAnimationPanelViewModel;

    private TextPanelViewModel textPanelViewModel;

    private View mView;

    private boolean isForAddCover = false;

    private boolean isTextAnimOperate = false;

    private boolean isAddOperate;

    private List<TabTopInfo<?>> mInfoList;

    private final int[] aTabs = {R.string.keybaord, R.string.edit_item2_1_2, R.string.cut_second_menu_animation,
                R.string.edit_item2_1_12, R.string.edit_item2_1_13};

    private final int[] aTabsForCover =
        {R.string.keybaord, R.string.edit_item2_1_2, R.string.edit_item2_1_12, R.string.edit_item2_1_13};

    public static EditPanelFragment newInstance(boolean isForAddCover, boolean isAnimOperate, boolean isAdd) {
        Bundle args = new Bundle();
        args.putBoolean(Constant.EXTRA_SELECT_RESULT, isForAddCover);
        args.putBoolean(Constant.TEXT_ANIM_OPERATE, isAnimOperate);
        args.putBoolean(Constant.TEXT_ADD_OPERATE, isAdd);
        EditPanelFragment fragment = new EditPanelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViewModelObserve() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.panel_add_edit;
    }

    @Override
    protected void initView(View view) {
        this.mView = view;
        mEditText = view.findViewById(R.id.edit);
        certainLayout = view.findViewById(R.id.layout_certain);
        mTabTopLayout = view.findViewById(R.id.tab_top_layout);
        if (ScreenUtil.isRTL()) {
            mTabTopLayout.setScaleX(RTL_UI);
        } else {
            mTabTopLayout.setScaleX(LTR_UI);
        }
        mEditText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        SafeBundle safeBundle = new SafeBundle(getArguments());
        isForAddCover = safeBundle.getBoolean(Constant.EXTRA_SELECT_RESULT, false);
        isTextAnimOperate = safeBundle.getBoolean(Constant.TEXT_ANIM_OPERATE, false);
        isAddOperate = safeBundle.getBoolean(Constant.TEXT_ADD_OPERATE, false);

        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        aAnimationPanelViewModel = new ViewModelProvider(mActivity, mFactory).get(AnimationPanelViewModel.class);
        textPanelViewModel = new ViewModelProvider(mActivity, mFactory).get(TextPanelViewModel.class);
        textPanelViewModel.setFontContent(null);
        textPanelViewModel.setBubblesContent(null);
        textPanelViewModel.setFlowerContent(null);
        textPanelViewModel.setAnimaText(null);

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.style_fragment_container);
        if (fragment != null && mActivity != null) {
            mNavController = NavHostFragment.findNavController(fragment);
            NavigatorProvider provider = mNavController.getNavigatorProvider();
            FixFragmentNavigator fragmentNavigator =
                new FixFragmentNavigator(mActivity, fragment.getChildFragmentManager(), fragment.getId());
            provider.addNavigator(fragmentNavigator);
            if (!isTextAnimOperate) {
                view.setTag(Constant.ADDTEXTSTICK);
                mEditText.setFocusable(true);
                mEditText.setFocusableInTouchMode(true);
                mNavController.setGraph(R.navigation.nav_graph_edit_text);
            } else {
                mNavController.setGraph(R.navigation.nav_graph_edit_text);
                view.setTag(Constant.ADDANIMATION);
            }
        }
    }

    @Override
    protected void initObject() {
        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(onTouchListener);
        mEditPreviewViewModel.setEditTextStatus(true);
        mMaterialEditViewModel.setIsTextEditState(true);
        mEditPreviewViewModel.setIndexTitle(0);
        mEditPreviewViewModel.setNeedAddTextOrSticker(true);
        mInfoList = new ArrayList<>();
        int defaultColor = ContextCompat.getColor(mActivity, R.color.white);
        int tintColor = ContextCompat.getColor(mActivity, R.color.tab_text_tint_color);
        int rightPadding = SizeUtils.dp2Px(mActivity, 12);
        for (int item : isForAddCover ? aTabsForCover : aTabs) {
            TabTopInfo<?> info =
                new TabTopInfo<>(getString(item), true, defaultColor, tintColor, 14, 14, rightPadding, rightPadding);
            mInfoList.add(info);
        }
        mTabTopLayout.inflateInfo(mInfoList);

        if (!isTextAnimOperate) {
            mTabTopLayout.defaultSelected(mInfoList.get(0));
        } else {
            mCurrentIndex = 2;
            clearFocus();
            hideKeyboard(mActivity);
            mTabTopLayout.defaultSelected(mInfoList.get(2));
            mNavController.navigate(R.id.action_to_animate_fragment);
        }

        if (isForAddCover) {
            mEditPreviewViewModel.setAddCoverTextStatus(true);
        }

        new Handler().postDelayed(() -> {
            if (mActivity != null) {
                if (!isTextAnimOperate) {
                    mEditText.setFocusable(true);
                    mEditText.setFocusableInTouchMode(true);

                    mEditText.requestFocus();

                    try {
                        mEditText.setSelection(mEditText.getText().length());
                    } catch (RuntimeException e) {
                        SmartLog.w(TAG, "initObject setSelection " + e.getMessage());
                    }

                    Boolean keyBordShow = mEditPreviewViewModel.getKeyBordShow().getValue();
                    if (keyBordShow != null) {
                        isShowKeyBord = keyBordShow;
                    }
                    if (!isShowKeyBord) {
                        showKeyboard(mActivity);
                    }
                }
            }
        }, 400);
    }

    @Override
    protected void initData() {
        if (mMaterialEditViewModel.isEditModel()) {
            HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
            if (isForAddCover) {
                selectedAsset = mMaterialEditViewModel.getSelectAsset();
            }
            if (selectedAsset instanceof HVEWordAsset) {
                HVEWordAsset hveWordAsset = (HVEWordAsset) selectedAsset;
                String text = hveWordAsset.getText();
                if (!TextUtils.isEmpty(text) && !text.equals(getResources().getString(R.string.inputtext))) {
                    mEditText.setText(text);
                }
            }
        }
        if (!isAddOperate) {
            HVEAsset asset = mEditPreviewViewModel.getSelectedAsset();
            if (asset == null) {
                return;
            }
            if (asset instanceof HVEWordAsset) {
                String text = ((HVEWordAsset) asset).getText();
                if (!TextUtils.isEmpty(text)) {
                    mEditText.setText(text);
                }
            }
        }
    }

    boolean isShowKeyBord = true;

    private boolean isShowKeyBoard = true;

    private void tabToOtherFragment(int index) {
        NavDestination navDestination = mNavController.getCurrentDestination();
        switch (index) {
            case 0:
                Boolean ketBordShow = mEditPreviewViewModel.getKeyBordShow().getValue();
                if (ketBordShow != null) {
                    isShowKeyBoard = ketBordShow;
                }
                if (!isShowKeyBoard) {
                    mEditText.requestFocus();
                    isShowKeyBord = true;
                    showKeyboard(mActivity);
                }

                if (navDestination != null && navDestination.getId() != R.id.keyboard_fragment) {
                    mNavController.navigate(R.id.action_to_keyboard_fragment);
                }
                break;
            case 1:
                mCurrentIndex = 1;
                isShowKeyBord = false;
                clearFocus();
                hideKeyboard(mActivity);
                if (navDestination != null && navDestination.getId() != R.id.style_fragment) {
                    mNavController.navigate(R.id.action_to_style_fragment);
                }
                break;
            case 2:
                mCurrentIndex = 2;
                isShowKeyBord = false;
                clearFocus();
                hideKeyboard(mActivity);
                if (isForAddCover) {
                    if (navDestination != null && navDestination.getId() != R.id.bubbles_fragment) {
                        mNavController.navigate(R.id.action_to_bubbles_fragment);
                    }
                } else {
                    if (navDestination != null && navDestination.getId() != R.id.animate_fragment) {
                        mNavController.navigate(R.id.action_to_animate_fragment);
                    }
                }
                break;
            case 3:
                mCurrentIndex = 3;
                isShowKeyBord = false;
                clearFocus();
                hideKeyboard(mActivity);
                if (isForAddCover) {
                    if (navDestination != null && navDestination.getId() != R.id.flower_fragment) {
                        mNavController.navigate(R.id.action_to_flower_fragment);
                    }
                } else {
                    if (navDestination != null && navDestination.getId() != R.id.bubbles_fragment) {
                        mNavController.navigate(R.id.action_to_bubbles_fragment);
                    }
                }
                break;
            case 4:
                mCurrentIndex = 4;
                isShowKeyBord = false;
                clearFocus();
                hideKeyboard(mActivity);
                if (navDestination != null && navDestination.getId() != R.id.flower_fragment) {
                    mNavController.navigate(R.id.action_to_flower_fragment);
                }
                break;
            default:
                SmartLog.d(TAG, "tabToOtherFragment run in default case");
        }
    }

    private void clearFocus() {
        if (mEditText.requestFocus()) {
            mEditText.clearFocus();
        }
    }

    private int mCurrentIndex;

    @Override
    protected void initEvent() {

        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!mEditText.requestFocus()) {
                        mEditText.requestFocus();
                    }
                    Boolean keyBordShow = mEditPreviewViewModel.getKeyBordShow().getValue();
                    if (keyBordShow != null) {
                        isShowKeyBoard = keyBordShow;
                    }
                    if (!isShowKeyBoard) {
                        if (mInfoList != null && mInfoList.size() > 0) {
                            mTabTopLayout.defaultSelected(mInfoList.get(0));
                        }
                    }
                }
                return false;
            }
        });

        mEditPreviewViewModel.getKeyBordShow().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                int height = (int) (SizeUtils.screenHeight(mActivity) * 0.425f + SizeUtils.dp2Px(mActivity, 70));
                if (mEditPreviewViewModel.getKeyBordShowHeight() > 0) {
                    height = mEditPreviewViewModel.getKeyBordShowHeight() + SizeUtils.dp2Px(mActivity, 96);
                }
                mView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
                if (!isTextAnimOperate) {
                    if (mTabTopLayout != null && mInfoList != null && mInfoList.size() > 0) {
                        mTabTopLayout.defaultSelected(mInfoList.get(0));
                    }
                }
            } else {
                clearFocus();
                mView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    (int) (SizeUtils.screenHeight(mActivity) * 0.425f + SizeUtils.dp2Px(mActivity, 30))));
                if (mTabTopLayout != null && mInfoList != null && mInfoList.size() > 0) {
                    if (mCurrentIndex == 0) {
                        mCurrentIndex = 1;
                    }
                    mTabTopLayout.defaultSelected(mInfoList.get(mCurrentIndex));
                }
            }
        });

        mEditPreviewViewModel.getEditPanelInputValue().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!StringUtil.isEmpty(s) && !s.equals(getResources().getString(R.string.inputtext))
                    && !StringUtil.isEmpty(mEditText.getText().toString())) {
                    mEditText.setText(s);

                    try {
                        mEditText.setSelection(s.length());
                    } catch (RuntimeException e) {
                        SmartLog.w(TAG, "initEvent onChanged setSelection " + e.getMessage());
                    }
                }
            }
        });

        mEditPreviewViewModel.getTableIndex().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer index) {
                mTabTopLayout.defaultSelected(mInfoList.get(index));
            }
        });
        certainLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
            if (selectedAsset == null && mEditPreviewViewModel.isAddCoverTextStatus()) {
                selectedAsset = mMaterialEditViewModel.getSelectAsset();
            }
            if (TextUtils.isEmpty(mEditText.getText()) || mEditText.getText().toString().length() == 0) {
                if (selectedAsset != null) {
                    int index = selectedAsset.getIndex();
                    int lineIndex = selectedAsset.getLaneIndex();
                    deleteTextNew(index, lineIndex);
                    mMaterialEditViewModel.clearMaterialEditData();
                }
                if (selectedAsset == null && isForAddCover) {
                    selectedAsset = mMaterialEditViewModel.getSelectAsset();
                    if (selectedAsset != null) {
                        mEditPreviewViewModel.updateTimeLine();
                    }
                }
            }
            if (mActivity != null) {
                mActivity.onBackPressed();
            }
        }));

        mTabTopLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            tabToOtherFragment(index);
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
                if (isForAddCover) {
                    selectedAsset = mMaterialEditViewModel.getSelectAsset();
                }
                int curCount = mMaterialEditViewModel.setTextNotPost(s.toString(), selectedAsset);
                if (curCount != 0) {
                    if (curCount != -1) {
                        s.delete(curCount, s.length());
                    } else {
                        removeAsset();
                    }
                } else {
                    mMaterialEditViewModel.setTextNotPost(getString(R.string.inputtext), selectedAsset);
                }
            }
        });

        viewModel.getTimeout().observe(this, isTimeout -> {
            if (isTimeout && !isBackground) {
                mActivity.onBackPressed();
            }
        });
    }

    @Override
    protected int setViewLayoutEvent() {
        return SHOW_KEYBORD;
    }

    private void removeAsset() {
        HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
        if (editor == null || editor.getTimeLine() == null) {
            SmartLog.e(TAG, "remove asset editor or timeline null return! editor:" + editor);
            return;
        }
        HVEAsset hveAsset = mEditPreviewViewModel.getSelectedAsset();
        if (hveAsset == null) {
            return;
        }
        if (hveAsset.getType() != HVEAsset.HVEAssetType.WORD) {
            return;
        }
        HVEStickerLane lane;
        lane = editor.getTimeLine().getStickerLane(hveAsset.getLaneIndex());
        if (lane == null) {
            return;
        }
        lane.removeAsset(hveAsset.getIndex());
        mEditPreviewViewModel.updateTimeLine();
        mMaterialEditViewModel.setIsTextEditState(false);
    }

    @Override
    public void onBackPressed() {
        if (mMaterialEditViewModel == null) {
            return;
        }
        mMaterialEditViewModel.setIsTextEditState(false);
        if (mEditPreviewViewModel == null) {
            return;
        }
        mEditPreviewViewModel.setEditTextStatus(false);
        mEditPreviewViewModel.setAddCoverTextStatus(false);
        if (StringUtil.isEmpty(mEditText.getText().toString())) {
            HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
            if (selectedAsset instanceof HVEWordAsset) {
                int index = selectedAsset.getIndex();
                int lineIndex = selectedAsset.getLaneIndex();
                deleteTextNew(index, lineIndex);
                mMaterialEditViewModel.clearMaterialEditData();
            }
        }

        mMaterialEditViewModel.setEditModel(false);

        HVEAsset asset = mEditPreviewViewModel.getSelectedAsset();
        if (asset != null) {
            mEditPreviewViewModel.setSelectedUUID(asset.getUuid());
        }
        super.onBackPressed();
    }

    private void deleteTextNew(int index, int lineIndex) {
        HVETimeLine mTimeLine = mEditPreviewViewModel.getTimeLine();
        if (mTimeLine == null) {
            return;
        }
        HVEStickerLane lane = mTimeLine.getStickerLane(lineIndex);
        if (lane == null) {
            return;
        }
        lane.removeAsset(index);
        mEditPreviewViewModel.setSelectedUUID("");
    }

    private void hideKeyboard(Activity context) {
        InputMethodManager inputMethodManager =
            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
    }

    private void showKeyboard(Activity context) {
        if (context instanceof VideoClipsActivity) {
            VideoClipsActivity activity = (VideoClipsActivity) context;
            activity.setSoftKeyboardShow(true);
        }
        InputMethodManager inputMethodManager =
            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUsuallyViewLayoutChange();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActivity instanceof VideoClipsActivity) {
            ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(onTouchListener);
        }
        viewModel.destroyTimeoutManager();
    }

    VideoClipsActivity.TimeOutOnTouchListener onTouchListener = ev -> {
        initTimeoutState();
        return false;
    };
}
