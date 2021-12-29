
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

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class TrailerFragment extends BaseFragment {
    private static final String TAG = "TrailerFragment";

    private View mView;

    private EditText mEditText;

    private LinearLayout mCertainLayout;

    private EditPreviewViewModel mEditPreviewViewModel;

    private MaterialEditViewModel mMaterialEditViewModel;

    private boolean isLoad = false;

    public static TrailerFragment newInstance() {
        return new TrailerFragment();
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
        return R.layout.panel_add_trailer;
    }

    @Override
    protected void initView(View view) {
        this.mView = view;
        mCertainLayout = view.findViewById(R.id.layout_certain);
        mEditText = view.findViewById(R.id.edit);
    }

    @Override
    protected void initObject() {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mEditPreviewViewModel.setTrailerStatus(true);
        mMaterialEditViewModel.setIsTextTrailerEditState(true);

        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEditText.requestFocus();

                try {
                    mEditText.setSelection(mEditText.getText().length());
                } catch (RuntimeException e) {
                    SmartLog.w(TAG, "initObject setSelection " + e.getMessage());
                }
                InputMethodManager manager =
                    ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null) {
                    manager.showSoftInput(mEditText, 0);
                }
            }
        }, 400);
    }

    @Override
    protected void initData() {
        HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
    }

    @Override
    public void onResume() {
        super.onResume();
        isLoad = true;
    }

    @Override
    protected void initEvent() {
        mCertainLayout.setOnClickListener(new OnClickRepeatedListener(v -> {
            hideKeyboard();
        }));

        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                return true;
            }
            return false;
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
                if (s.toString().length() == 0) {
                    mEditPreviewViewModel.setTextTrailer(getString(R.string.edit_tail));
                } else {
                    mEditPreviewViewModel.setTextTrailer(s.toString());
                }
            }
        });

        mEditPreviewViewModel.getKeyBordShow().observe(getViewLifecycleOwner(), aBoolean -> {
            if (!isLoad) {
                return;
            }
            if (aBoolean) {
                int height = (int) (SizeUtils.screenHeight(mActivity) * 0.425f + SizeUtils.dp2Px(mActivity, 42));
                if (mEditPreviewViewModel.getKeyBordShowHeight() > 0) {
                    height = mEditPreviewViewModel.getKeyBordShowHeight() + SizeUtils.dp2Px(mActivity, 56);
                }
                mView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
            } else {
                if (mActivity != null) {
                    mActivity.onBackPressed();
                }
            }
        });
    }

    public void hideKeyboard() {
        if (mActivity == null) {
            return;
        }
        InputMethodManager inputMethodManager =
            (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected int setViewLayoutEvent() {
        return USUALLY_HEIGHT;
    }

    @Override
    public void onBackPressed() {
        if (mEditPreviewViewModel != null && mActivity != null) {
            mEditText.clearFocus();
            mEditText.setFocusable(false);
            mEditText.setFocusableInTouchMode(false);
            mEditPreviewViewModel.setSelectedUUID("");
        }

        if (mEditPreviewViewModel != null) {
            mEditPreviewViewModel.setTrailerStatus(false);
        }

        if (mMaterialEditViewModel != null) {
            mMaterialEditViewModel.clearMaterialEditData();
            mMaterialEditViewModel.setIsTextTrailerEditState(false);
        }

        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUsuallyViewLayoutChange();
    }
}
