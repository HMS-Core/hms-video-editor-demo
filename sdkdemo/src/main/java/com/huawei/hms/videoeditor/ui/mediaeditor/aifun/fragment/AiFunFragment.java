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

package com.huawei.hms.videoeditor.ui.mediaeditor.aifun.fragment;

import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.HVEEffectType.AI_COLOR;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.dialog.CommonProgressDialog;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.aifun.adapter.AiFunAdapter;
import com.huawei.hms.videoeditor.ui.mediaeditor.filter.FilterLinearLayoutManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AiFunFragment extends BaseFragment {
    private static final String TAG = "AiFunFragment";

    private static final String OPERATE_ID = "operate_id";

    private ImageView mIvCertain;

    private RecyclerView mRecyclerView;

    private LoadingIndicatorView mIndicatorView;

    private View mAiFunNone;

    private List<CloudMaterialBean> currentAiFunContentList;

    private AiFunAdapter mAiFunAdapter;

    HuaweiVideoEditor mEditor;

    private EditPreviewViewModel mEditPreviewViewModel;

    private VideoClipsActivity.TimeOutOnTouchListener mOnAiFunTouchListener;

    private HVEAsset currentSelectedAsset;

    private int currentOperationId;

    private CommonProgressDialog mAIFunProgressDialog;

    private int detailAIType;

    public static AiFunFragment newInstance(int operationId) {
        Bundle args = new Bundle();
        args.putInt(OPERATE_ID, operationId);
        AiFunFragment aiFunFragment = new AiFunFragment();
        aiFunFragment.setArguments(args);
        return aiFunFragment;
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
        return R.layout.fragment_ai_fun;
    }

    @Override
    protected void initView(View view) {
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mEditor = mEditPreviewViewModel.getEditor();

        TextView mTvTitle = view.findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.cut_second_menu_fun);
        mIvCertain = view.findViewById(R.id.iv_certain);
        mRecyclerView = view.findViewById(R.id.recycler_view_ai_fun);
        mIndicatorView = view.findViewById(R.id.indicator_ai_fun);
        mIndicatorView.show();

        mOnAiFunTouchListener = ev -> {
            initTimeoutState();
            return false;
        };

        currentSelectedAsset = mEditPreviewViewModel.getSelectedAsset();
        if (currentSelectedAsset == null) {
            currentSelectedAsset = mEditPreviewViewModel.getMainLaneAsset();
        }
        if (!(currentSelectedAsset instanceof HVEImageAsset)) {
            return;
        }

        ((VideoClipsActivity) mActivity).registerMyOnTouchListener(mOnAiFunTouchListener);
    }

    @Override
    protected void initObject() {
        SafeBundle safeBundle = new SafeBundle(getArguments());
        currentOperationId = safeBundle.getInt(OPERATE_ID, 0);
        SmartLog.i(TAG, "currentOperationId==" + currentOperationId);

        View mNoneHeaderView =
            LayoutInflater.from(requireContext()).inflate(R.layout.adapter_add_mask_header, null, false);
        mNoneHeaderView.setLayoutParams(
            new ConstraintLayout.LayoutParams(SizeUtils.dp2Px(context, 58F), SizeUtils.dp2Px(context, 75F)));
        mAiFunNone = mNoneHeaderView.findViewById(R.id.rl_cancel_mask_header);
        mAiFunNone.setSelected(true);
        currentAiFunContentList = new ArrayList<>();
        mAiFunAdapter = new AiFunAdapter(mActivity, currentAiFunContentList, R.layout.adapter_add_ai_fun_item);
        mAiFunAdapter.addHeaderView(mNoneHeaderView);
        if (mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView
                .addItemDecoration(new HorizontalDividerDecoration(ContextCompat.getColor(mActivity, R.color.color_20),
                    SizeUtils.dp2Px(mActivity, 75F), SizeUtils.dp2Px(mActivity, 8F)));
        }
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new FilterLinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAiFunAdapter);
    }

    @Override
    protected void initData() {
        CloudMaterialBean materialsCutContent = new CloudMaterialBean();
        materialsCutContent.setName(context.getString(R.string.motion_photo));
        materialsCutContent.setLocalDrawableId(R.mipmap.icon_dynamic_pic_ai);

        CloudMaterialBean materialsCutContent2 = new CloudMaterialBean();
        materialsCutContent2.setName(context.getString(R.string.ai_color));
        materialsCutContent2.setLocalDrawableId(R.mipmap.icon_dynamic_pic_ai);

        currentAiFunContentList.add(materialsCutContent);
        currentAiFunContentList.add(materialsCutContent2);

        mAiFunAdapter.notifyDataSetChanged();
        mIndicatorView.hide();
    }

    @Override
    protected void initEvent() {
        mAiFunAdapter.setOnItemClickListener((position, dataPosition) -> {
            mAiFunNone.setSelected(false);
            int mSelectPosition = mAiFunAdapter.getSelectPosition();
            if (mSelectPosition != position) {
                mAiFunAdapter.setSelectPosition(position);
                if (mSelectPosition != -1) {
                    mAiFunAdapter.notifyItemChanged(mSelectPosition);
                }
                mAiFunAdapter.notifyItemChanged(position);
                SmartLog.d(TAG, "setOnItemClickListener position: " + position);
                if (position == 1) {
                    detailAIType = 1;

                    if (HVEAsset.HVEAssetType.VIDEO.equals(currentSelectedAsset.getType())) {
                        ToastWrapper.makeText(context, context.getResources().getString(R.string.reenact_limit)).show();
                        return;
                    }

                    addFaceReenactEffect();
                }

                if (position == 2) {
                    detailAIType = 2;

                    long size = FileUtil.getFileSize(currentSelectedAsset.getPath(), FileUtil.SIZETYPE_MB);

                    if (size >= 100) {
                        ToastWrapper.makeText(context, context.getResources().getString(R.string.ai_color_limit))
                            .show();
                        return;
                    }

                    addAIColorEffect();
                }
            }
        });

        mIvCertain.setOnClickListener(new OnClickRepeatedListener(v -> mActivity.onBackPressed()));

        mAiFunNone.setOnClickListener(v -> {
            mAiFunNone.setContentDescription(getString(R.string.no_filter));
            if (mAiFunNone.isSelected()) {
                mAiFunNone.setSelected(false);
            } else {
                mAiFunNone.setSelected(true);
                int mSelectPosition = mAiFunAdapter.getSelectPosition();
                mAiFunAdapter.setSelectPosition(-1);
                if (mSelectPosition != -1) {
                    mAiFunAdapter.notifyItemChanged(mSelectPosition);
                }

                if (currentSelectedAsset.getType() == HVEAsset.HVEAssetType.IMAGE) {
                    if (((HVEImageAsset) currentSelectedAsset).isFaceReenactEnabled()) {
                        boolean isCanceled = ((HVEImageAsset) currentSelectedAsset).removeFaceReenactAIEffect();
                        if (isCanceled) {
                            showToast(getString(R.string.motion_photo_cancel));
                            refreshMainLaneAndUIData();
                        } else {
                            showToast(getString(R.string.motion_photo_fail));
                        }
                    }
                }

                if (currentSelectedAsset.getType() == HVEAsset.HVEAssetType.IMAGE
                    || currentSelectedAsset.getType() == HVEAsset.HVEAssetType.VIDEO) {
                    if (isAIColorEnabled(currentSelectedAsset)) {
                        boolean isCanceled = ((HVEVisibleAsset) currentSelectedAsset).removeAIColorEffect();
                        if (isCanceled) {
                            showToast(getString(R.string.ai_color_cancel));
                            refreshMainLaneAndUIData();
                        } else {
                            showToast(getString(R.string.ai_color_fail));
                        }
                    }
                }
            }
        });

    }

    private boolean isAIColorEnabled(HVEAsset asset) {
        List<HVEEffect> effects = asset.getEffectsWithType(AI_COLOR);
        return !effects.isEmpty();
    }

    private void addFaceReenactEffect() {
        if (currentSelectedAsset.getType() != HVEAsset.HVEAssetType.IMAGE) {
            SmartLog.e(TAG, "addFaceReenactEffect: " + "only support Image Asset");
            return;
        }

        initAIFunProgressDialog();
        ((HVEImageAsset) currentSelectedAsset).addFaceReenactAIEffect(new HVEAIProcessCallback() {
            @Override
            public void onProgress(int progress) {
                SmartLog.i(TAG, "onAICloudProgress==" + progress);
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mAIFunProgressDialog != null) {
                        mAIFunProgressDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mAIFunProgressDialog != null) {
                        mAIFunProgressDialog.updateProgress(0);
                        mAIFunProgressDialog.dismiss();
                    }
                    refreshMainLaneAndUIData();
                    showToast(getString(R.string.motion_photo_success));
                });

            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.i(TAG, "onAICloudError==" + errorMessage);
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mAIFunProgressDialog != null) {
                        mAIFunProgressDialog.updateProgress(0);
                        mAIFunProgressDialog.dismiss();
                    }

                    if (detailAIType == 1) {
                        showToast(getString(R.string.motion_photo_fail));
                    }
                    if (detailAIType == 2) {
                        showToast(getString(R.string.ai_color_fail));
                    }
                });
            }
        });
    }

    private void addAIColorEffect() {
        initAIFunProgressDialog();
        if (!(currentSelectedAsset instanceof HVEVisibleAsset)) {
            return;
        }

        ((HVEVisibleAsset) currentSelectedAsset).addColorAIEffect(new HVEAIProcessCallback() {
            @Override
            public void onProgress(int progress) {
                SmartLog.i(TAG, "onAICloudProgress==" + progress);
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mAIFunProgressDialog != null) {
                        mAIFunProgressDialog.updateProgress(progress);
                    }
                });
            }

            @Override
            public void onSuccess() {
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mAIFunProgressDialog != null) {
                        mAIFunProgressDialog.updateProgress(0);
                        mAIFunProgressDialog.dismiss();
                    }
                    refreshMainLaneAndUIData();
                    showToast(getString(R.string.ai_color_success));
                });

            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                SmartLog.i(TAG, "onAICloudError==" + errorMessage);
                if (!isValidActivity()) {
                    return;
                }
                mActivity.runOnUiThread(() -> {
                    if (mAIFunProgressDialog != null) {
                        mAIFunProgressDialog.updateProgress(0);
                        mAIFunProgressDialog.dismiss();
                    }
                    showToast(getString(R.string.ai_color_fail));
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentSelectedAsset != null && currentSelectedAsset.getType() == HVEAsset.HVEAssetType.IMAGE) {
            if (((HVEImageAsset) currentSelectedAsset).isFaceReenactEnabled() && mAiFunAdapter != null) {
                mAiFunNone.setSelected(false);
                mAiFunAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_266;
    }

    @Override
    public void onBackPressed() {
        if (!(mActivity instanceof VideoClipsActivity)) {
            return;
        }
        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(mOnAiFunTouchListener);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (!(mActivity instanceof VideoClipsActivity)) {
            return;
        }
        ((VideoClipsActivity) mActivity).unregisterMyOnTouchListener(mOnAiFunTouchListener);
        mEditPreviewViewModel.destroyTimeoutManager();
        super.onDestroy();
    }

    private void refreshMainLaneAndUIData() {
        if (!isValidActivity()) {
            return;
        }
        if (mEditPreviewViewModel == null || mEditor == null) {
            return;
        }
        mActivity.runOnUiThread(() -> {
            mEditor.playTimeLine(currentSelectedAsset.getStartTime(), currentSelectedAsset.getEndTime());
        });
    }

    private void showToast(String toastText) {
        if (!isValidActivity()) {
            return;
        }

        if (TextUtils.isEmpty(toastText)) {
            return;
        }
        mActivity.runOnUiThread(() -> ToastWrapper.makeText(mActivity, toastText, Toast.LENGTH_SHORT).show());
    }

    private void initAIFunProgressDialog() {
        if (!isValidActivity()) {
            return;
        }
        mAIFunProgressDialog = new CommonProgressDialog(mActivity, () -> {
            if ((currentSelectedAsset instanceof HVEImageAsset)) {
                if (detailAIType == 1) {
                    ((HVEImageAsset) currentSelectedAsset).interruptFaceReenact();
                }
                if (detailAIType == 2) {
                    ((HVEImageAsset) currentSelectedAsset).interruptAIColor();
                }
            }
            if ((currentSelectedAsset instanceof HVEVideoAsset)) {
                if (detailAIType == 2) {
                    ((HVEVideoAsset) currentSelectedAsset).interruptAIColor();
                }
            }
            mAIFunProgressDialog = null;
        });

        if (detailAIType == 1) {
            mAIFunProgressDialog.setTitleValue(getString(R.string.motion_photo_generated));
        }
        if (detailAIType == 2) {
            mAIFunProgressDialog.setTitleValue(getString(R.string.ai_color_generated));
        }

        mAIFunProgressDialog.setCanceledOnTouchOutside(false);
        mAIFunProgressDialog.setCancelable(false);
        mAIFunProgressDialog.show();
    }
}