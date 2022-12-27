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

package com.huawei.hms.videoeditor.ui.mediaeditor.aihair.fragment;

import static com.huawei.hms.videoeditor.ai.HVEAIError.AI_ERROR_NETWORK;
import static com.huawei.hms.videoeditor.ai.HVEAIError.AI_ERROR_NO_NETWORK;
import static com.huawei.hms.videoeditor.ai.HVEAIError.AI_ERROR_TIMEOUT;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.ai.HVEAICloudListener;
import com.huawei.hms.videoeditor.ai.HVEAIColorBean;
import com.huawei.hms.videoeditor.ai.HVEAIHairDyeing;
import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.BitmapDecodeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonProgressDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.ViewFileActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.aihair.FilterLinearLayoutManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.aihair.adapter.HairDyeingAdapter;
import com.huawei.hms.videoeditor.utils.SmartLog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HairDyeingFragment extends BaseFragment {
    private static final String TAG = "HairDyeingFragment";

    private static final String FILE_PATH = "file_path";

    protected static final int HAIR_DYEING_DOWN_SAMPLE_PARAM = 1920;

    private String mFilePath = "";

    private ImageView mIvCertain;

    private RecyclerView mRecyclerView;

    private View mAiHairNone;

    private HairDyeingAdapter mAiHairAdapter;

    private List<HVEAIColorBean> mHairInfoBeanList = new ArrayList<>();

    private CommonProgressDialog mHairDyeingDialog;

    private HVEAIHairDyeing mHairDyeing;

    private boolean isRunning;

    public static HairDyeingFragment newInstance(String filePath) {
        Bundle args = new Bundle();
        args.putString(FILE_PATH, filePath);
        HairDyeingFragment hairDyeingFragment = new HairDyeingFragment();
        hairDyeingFragment.setArguments(args);
        return hairDyeingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.color_20;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_hair_dyeing;
    }

    @Override
    protected void initView(View view) {
        TextView mTvTitle = view.findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.cut_second_menu_ai_hair);
        mIvCertain = view.findViewById(R.id.iv_certain);
        mRecyclerView = view.findViewById(R.id.recycler_view_ai_hair);
    }

    @Override
    protected void initObject() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        mFilePath = safeBundle.getString(FILE_PATH);

        View mNoneHeaderView =
                LayoutInflater.from(requireContext()).inflate(R.layout.adapter_add_mask_header, null, false);
        mNoneHeaderView.setLayoutParams(
                new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(context, 58F), SizeUtils.dp2Px(context, 75F)));
        mAiHairNone = mNoneHeaderView.findViewById(R.id.rl_cancel_mask_header);
        mAiHairNone.setSelected(true);
        mAiHairAdapter = new HairDyeingAdapter(mActivity, mHairInfoBeanList, R.layout.adapter_add_hair_dyeing_item);
        mAiHairAdapter.addHeaderView(mNoneHeaderView);
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                    .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                            SizeUtils.dp2Px(mActivity, 75F), SizeUtils.dp2Px(mActivity, 8F)));
        }
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new FilterLinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAiHairAdapter);
    }

    @Override
    protected void initData() {
        getAIHairColorList();
    }

    /**
     * 请求色卡列表
     */
    public void getAIHairColorList() {
        if (mHairDyeing == null) {
            mHairDyeing = new HVEAIHairDyeing();
        }
        mHairDyeing.getAIHairColorList(new HVEAICloudListener<List<HVEAIColorBean>>() {
            @Override
            public void onAICloudSuccess(List<HVEAIColorBean> result) {
                mHairInfoBeanList.clear();
                mHairInfoBeanList.addAll(result);
                if (isValidActivity()) {
                    mActivity.runOnUiThread(() -> {
                        if (mAiHairAdapter != null) {
                            mAiHairAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onAICloudError(int errorCode, String errorMessage) {
                SmartLog.e(TAG, errorMessage);
                showToast(errorCode, null);
            }
        });
    }

    @Override
    protected void initEvent() {
        mAiHairAdapter.setOnItemClickListener((colorBean, position) -> {
            if (isRunning) {
                return;
            }
            if (mHairDyeing == null) {
                mHairDyeing = new HVEAIHairDyeing();
            }
            isRunning = true;
            mHairDyeing.downloadAIHairColor(colorBean, new HVEAIProcessCallback<Bitmap>() {
                @Override
                public void onProgress(int progress) {
                }

                @Override
                public void onSuccess(Bitmap result) {
                    if (isValidActivity()) {
                        mActivity.runOnUiThread(() -> {
                            mAiHairNone.setSelected(false);
                            int selectPosition = mAiHairAdapter.getSelectPosition();
                            if (selectPosition != position) {
                                mAiHairAdapter.setSelectPosition(position);
                                if (selectPosition != -1) {
                                    mAiHairAdapter.notifyItemChanged(selectPosition);
                                }
                                mAiHairAdapter.notifyItemChanged(position);
                            }
                        });
                    }
                    if (result != null) {
                        hairDyeing(result);
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    isRunning = false;
                    SmartLog.e(TAG, "get ai hair color error, error code is: " + errorCode + ", error message: " + errorMessage);
                    showToast(errorCode, getString(R.string.ai_hair_get_color_card_fail));
                }
            });
        });

        mIvCertain.setOnClickListener(new OnClickRepeatedListener(v -> {
            if (!isValidActivity()) {
                return;
            }
            mActivity.onBackPressed();
        }));

        mAiHairNone.setOnClickListener(v -> {
            mAiHairNone.setContentDescription(getString(R.string.no_filter));
            mAiHairNone.setSelected(true);
            if (mAiHairNone.isSelected()) {
                int mSelectPosition = mAiHairAdapter.getSelectPosition();
                mAiHairAdapter.setSelectPosition(-1);
                if (mSelectPosition != -1) {
                    mAiHairAdapter.notifyItemChanged(mSelectPosition);
                }
            }
        });

        if (isValidActivity()) {
            mActivity.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_210;
    }

    /**
     * 一键染发detect
     *
     * @param colormapBitmap 色卡图bitmap
     */
    private void hairDyeing(Bitmap colormapBitmap) {
        if (!isValidActivity()) {
            return;
        }
        mActivity.runOnUiThread(() -> initHairDyeingProgressDialog());
        mHairDyeing = new HVEAIHairDyeing();
        mHairDyeing.initEngine(new HVEAIInitialCallback() {
            @Override
            public void onProgress(int progress) {
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mHairDyeingDialog != null) {
                        if (!mHairDyeingDialog.isShowing()) {
                            mHairDyeingDialog.show();
                        }
                        mHairDyeingDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mHairDyeingDialog != null) {
                        mHairDyeingDialog.updateProgress(0);
                        mHairDyeingDialog.dismiss();
                    }
                    Bitmap originBitmap = BitmapDecodeUtils.getFitSampleBitmap(mFilePath, HAIR_DYEING_DOWN_SAMPLE_PARAM,
                            HAIR_DYEING_DOWN_SAMPLE_PARAM);
                    mHairDyeing.process(originBitmap, colormapBitmap, new HVEAIProcessCallback<Bitmap>() {
                        @Override
                        public void onProgress(int progress) {

                        }

                        @Override
                        public void onSuccess(Bitmap result) {
                            if (result == null) {
                                isRunning = false;
                                return;
                            }
                            try {
                                String resultFilePath =
                                        FileUtil.saveBitmap(mActivity, result, System.currentTimeMillis() + "");
                                if (!TextUtils.isEmpty(resultFilePath)) {
                                    ViewFileActivity.startActivity(mActivity, resultFilePath, false);
                                }
                            } catch (IOException e) {
                                SmartLog.e(TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            showToast(errorCode, null);
                            SmartLog.e(TAG, "hair dyeing fail, error code is: " + errorCode + ", error message: " + errorMessage);
                        }
                    });
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.e(TAG, errorMessage);
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mHairDyeingDialog != null) {
                        mHairDyeingDialog.updateProgress(0);
                        mHairDyeingDialog.dismiss();
                    }
                });
                showToast(errorCode, null);
            }
        });
    }

    private void initHairDyeingProgressDialog() {
        mHairDyeingDialog = new CommonProgressDialog(mActivity, () -> {
            isRunning = false;
            mHairDyeingDialog.dismiss();
            mHairDyeing.releaseEngine();
            if (isValidActivity()) {
                mActivity.runOnUiThread(() -> {
                    if (mAiHairAdapter != null) {
                        mAiHairNone.setSelected(true);
                        mAiHairAdapter.setSelectPosition(-1);
                        mAiHairAdapter.notifyDataSetChanged();
                    }
                });
            }
            mHairDyeing = null;
            mHairDyeingDialog = null;
        });
        mHairDyeingDialog.setTitleValue(getString(R.string.intelligent_processing));
        mHairDyeingDialog.setCanceledOnTouchOutside(false);
        mHairDyeingDialog.setCancelable(false);
        mHairDyeingDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        isRunning = false;
    }

    private void showToast(int errorCode, String defaultString) {
        String errorMessage;
        if (TextUtils.isEmpty(defaultString)) {
            errorMessage = getString(R.string.ai_hair_fail);
        } else {
            errorMessage = defaultString;
        }
        isRunning = false;
        switch (errorCode) {
            case AI_ERROR_NETWORK:
            case AI_ERROR_NO_NETWORK:
                errorMessage = getString(R.string.result_illegal);
                break;
            case AI_ERROR_TIMEOUT:
                errorMessage = getString(R.string.ai_network_timeout);
                break;
            default:
                break;
        }
        String finalErrorMessage = errorMessage;
        mActivity.runOnUiThread(
                () -> ToastWrapper.makeText(mActivity, finalErrorMessage, Toast.LENGTH_SHORT)
                        .show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mHairDyeing != null) {
            mHairDyeing.releaseEngine();
            mHairDyeing = null;
        }
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }
}
