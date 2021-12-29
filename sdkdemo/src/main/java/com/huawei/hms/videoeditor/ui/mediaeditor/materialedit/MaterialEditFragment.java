
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

package com.huawei.hms.videoeditor.ui.mediaeditor.materialedit;

import static com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.TransformView.DOUBLE_TAP_TIMEOUT;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEWordAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.bean.HVESize;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuClickManager;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.VideoClipsPlayViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.persontrack.PersonTrackingViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel.TextEditViewModel;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MaterialEditFragment extends BaseFragment {
    private static final String TAG = "MaterialEditFragment";

    private FrameLayout mContentLayout;

    private ReferenceLineView mReferenceLineView;

    private MaterialEditViewModel mMaterialEditViewModel;

    private EditPreviewViewModel mEditPreviewViewModel;

    private PersonTrackingViewModel mPersonTrackingViewModel;

    private TextEditViewModel mTextEditViewModel;

    private VideoClipsPlayViewModel mSdkPlayViewModel;

    private boolean isFullScreenState = false;

    private String mLastSelectWordUUID = "";

    private long mLastSelectWordTime = 0;

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_material_edit_layout;
    }

    @Override
    protected void initView(View view) {
        mContentLayout = view.findViewById(R.id.content_layout);
        mReferenceLineView = view.findViewById(R.id.reference_line_view);
    }

    @Override
    protected void initObject() {
        mMaterialEditViewModel = new ViewModelProvider(mActivity, mFactory).get(MaterialEditViewModel.class);
        mEditPreviewViewModel = new ViewModelProvider(mActivity, mFactory).get(EditPreviewViewModel.class);
        mPersonTrackingViewModel = new ViewModelProvider(mActivity, mFactory).get(PersonTrackingViewModel.class);
        mTextEditViewModel = new ViewModelProvider(mActivity, mFactory).get(TextEditViewModel.class);
        mSdkPlayViewModel = new ViewModelProvider(mActivity, mFactory).get(VideoClipsPlayViewModel.class);
    }

    @Override
    protected void initData() {
        mMaterialEditViewModel.getSelectMaterials().observe(this, materialEditDataList -> {
            mContentLayout.removeAllViews();
            for (MaterialEditData materialEditData : materialEditDataList) {
                addMaterialRectView(materialEditData);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initEvent() {
        mContentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isFullScreenState && event.getAction() == MotionEvent.ACTION_DOWN) {
                    mLastSelectWordUUID = "";
                    mLastSelectWordTime = 0;
                    HVEPosition2D position2D = new HVEPosition2D(event.getX(), event.getY());
                    HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                    if (editor == null) {
                        return false;
                    }
                    boolean isPersonTrackingEdit = mEditPreviewViewModel.isPersonTrackingStatus();
                    if (isPersonTrackingEdit) {
                        getPoint(event.getX(), event.getY());
                        return false;
                    }
                    HVEAsset asset = getIEditable(position2D, mMaterialEditViewModel.getCurrentFirstMenuId());

                    if (asset == null) {
                        mEditPreviewViewModel.setChoiceAsset(null);
                        return false;
                    }

                    mEditPreviewViewModel.setChoiceAsset((HVEAsset) asset);
                    if (asset instanceof HVEWordAsset) {
                        HVEWordAsset wordAsset = (HVEWordAsset) asset;
                        if (wordAsset.getWordAssetType() != HVEWordAsset.HVEWordAssetType.NORMAL_TEMPLATE) {
                            mLastSelectWordUUID = wordAsset.getUuid();
                            mLastSelectWordTime = System.currentTimeMillis();
                        }
                    }
                }
                return false;
            }
        });

        mEditPreviewViewModel.getCurrentTime().observe(this, time -> {
            HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
            if (!(selectedAsset instanceof HVEVisibleAsset)) {
                SmartLog.e(TAG, "selectedAsset is unValid");
                return;
            }
            HVEVisibleAsset hveVisibleAsset = (HVEVisibleAsset) selectedAsset;
            String uuid = selectedAsset.getUuid();
            for (int i = 0; i < mContentLayout.getChildCount(); i++) {
                View view = mContentLayout.getChildAt(i);
                if (!(view instanceof TransformView)) {
                    SmartLog.e(TAG, "view is unValid");
                    continue;
                }
                TransformView transformView = (TransformView) view;
                if (!StringUtil.isEmpty((String) transformView.getTag()) && uuid.equals(transformView.getTag())) {
                    boolean isVisible = selectedAsset.isVisible(time);
                    transformView.setVisibility(isVisible ? View.VISIBLE : View.GONE);

                    boolean hasDiff = checkRectDiff(transformView, hveVisibleAsset);
                    if (isVisible && hasDiff) {
                        transformView.setRectangularPoints(hveVisibleAsset.getRect(), hveVisibleAsset.getSize(),
                            hveVisibleAsset.getRotation());
                    }
                    break;
                }
            }
        });

        mSdkPlayViewModel.getFullScreenState().observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(Boolean isFullScreen) {
                isFullScreenState = isFullScreen;
                mContentLayout.setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
            }
        });

        mMaterialEditViewModel.getIsMaterialEditShow().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mContentLayout.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }
        });

        mMaterialEditViewModel.getRefreshState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshMaterialList();
            }
        });

        mMaterialEditViewModel.getIsStickerEditState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
                if (!(selectedAsset instanceof HVEVisibleAsset)) {
                    SmartLog.e(TAG, "selectedAsset is unValid");
                    return;
                }
                HVEVisibleAsset hveVisibleAsset = (HVEVisibleAsset) selectedAsset;
                String uuid = selectedAsset.getUuid();
                for (int i = 0; i < mContentLayout.getChildCount(); i++) {
                    View view = mContentLayout.getChildAt(i);
                    if (!(view instanceof StickerView)) {
                        continue;
                    }
                    StickerView transformView = (StickerView) view;
                    if (!StringUtil.isEmpty((String) transformView.getTag()) && uuid.equals(transformView.getTag())) {
                        transformView.setDrawIconStatus(true, !aBoolean, !aBoolean, true);
                        transformView.setTag(uuid);
                        transformView.setRectangularPoints(hveVisibleAsset.getRect(), hveVisibleAsset.getSize(),
                            hveVisibleAsset.getRotation());
                        break;
                    }
                }
            }
        });

        mMaterialEditViewModel.getIsTextEditState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
                if (selectedAsset == null && mEditPreviewViewModel.isAddCoverTextStatus()) {
                    selectedAsset = mMaterialEditViewModel.getSelectAsset();
                }
                if (!aBoolean && selectedAsset == null) {
                    if (mMaterialEditViewModel.getText().getValue() == null
                        || StringUtil.isEmpty(mMaterialEditViewModel.getText().getValue())) {
                        mMaterialEditViewModel.clearMaterialEditData();
                        return;
                    }
                }
                if (!(selectedAsset instanceof HVEVisibleAsset)) {
                    SmartLog.e(TAG, "selectedAsset is unValid");
                    return;
                }
                HVEVisibleAsset hveVisibleAsset = (HVEVisibleAsset) selectedAsset;
                String uuid = hveVisibleAsset.getUuid();
                for (int i = 0; i < mContentLayout.getChildCount(); i++) {
                    View view = mContentLayout.getChildAt(i);
                    if (!(view instanceof TextDefaultView)) {
                        continue;
                    }
                    TextDefaultView transformView = (TextDefaultView) view;
                    if (!StringUtil.isEmpty((String) transformView.getTag()) && uuid.equals(transformView.getTag())) {
                        transformView.setDrawIconStatus(true, !aBoolean, !aBoolean, true);
                        transformView.setTag(hveVisibleAsset.getUuid());
                        transformView.setRectangularPoints(hveVisibleAsset.getRect(), hveVisibleAsset.getSize(),
                            hveVisibleAsset.getRotation());
                        break;
                    }
                }
            }
        });

        mMaterialEditViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String string) {
                HVEAsset selectedAsset = mEditPreviewViewModel.getSelectedAsset();
                if (selectedAsset == null && mEditPreviewViewModel.isAddCoverTextStatus()) {
                    selectedAsset = mMaterialEditViewModel.getSelectAsset();
                }
                if (selectedAsset instanceof HVEVisibleAsset) {
                    HVEVisibleAsset hveVisibleAsset = (HVEVisibleAsset) selectedAsset;
                    String uuid = hveVisibleAsset.getUuid();
                    ((HVEWordAsset) hveVisibleAsset).setText(string);
                    if (EditorManager.getInstance().getEditor() == null
                        || EditorManager.getInstance().getTimeLine() == null) {
                        return;
                    }
                    EditorManager.getInstance()
                        .getEditor()
                        .seekTimeLine(EditorManager.getInstance().getTimeLine().getCurrentTime(),
                            () -> mActivity.runOnUiThread(() -> {

                                for (int i = 0; i < mContentLayout.getChildCount(); i++) {
                                    View view = mContentLayout.getChildAt(i);
                                    if (view instanceof TextDefaultView) {
                                        TextDefaultView transformView = (TextDefaultView) view;
                                        if (!StringUtil.isEmpty((String) transformView.getTag())
                                            && uuid.equals(transformView.getTag())) {
                                            transformView.setDrawIconStatus(true, false, false, true);
                                            transformView.setTag(hveVisibleAsset.getUuid());
                                            transformView.setRectangularPoints(hveVisibleAsset.getRect(),
                                                hveVisibleAsset.getSize(), hveVisibleAsset.getRotation());
                                            break;
                                        }
                                    }
                                }
                            }));
                }
            }
        });
    }

    private void getPoint(float posX, float posY) {
        float x = posX;
        float y = posY;
        HVEAsset selectedAsset = mPersonTrackingViewModel.getSelectedTracking();
        HVEVisibleAsset hveVisibleAsset = (HVEVisibleAsset) selectedAsset;
        int w = hveVisibleAsset.getWidth();
        int h = hveVisibleAsset.getHeight();
        float vw = hveVisibleAsset.getSize().width;
        float vh = hveVisibleAsset.getSize().height;
        boolean isMirror = hveVisibleAsset.getHorizontalMirrorState();
        boolean isVerticalMirror = hveVisibleAsset.getVerticalMirrorState();
        if (hveVisibleAsset.getRect().size() > 1) {
            HVEPosition2D leftTop = hveVisibleAsset.getRect().get(1);
            HVEPosition2D bottomRight = hveVisibleAsset.getRect().get(3);
            float centerX = (leftTop.xPos + bottomRight.xPos) / 2;
            float centerY = (leftTop.yPos + bottomRight.yPos) / 2;

            x = x - centerX;
            y = y - centerY;
            float ro = hveVisibleAsset.getRotation();
            Matrix matrix = new Matrix();
            matrix.postRotate(ro);
            float[] f = new float[] {x, y};
            float[] dst = new float[2];
            matrix.mapPoints(dst, f);
            f[0] = ((dst[0] + vw / 2)) / vw;
            f[1] = (vh / 2 - dst[1]) / vh;
            if (isMirror) {
                f[0] = 1 - f[0];
            }
            if (isVerticalMirror) {
                f[1] = 1 - f[1];
            }

            Point position2D = new Point((int) (f[0] * w), (int) ((1 - f[1]) * h));
            mPersonTrackingViewModel.setTrackingPoint(position2D);
        }
    }

    @Override
    protected int setViewLayoutEvent() {
        return 0;
    }

    private boolean checkRectDiff(TransformView transformView, HVEVisibleAsset hveVisibleAsset) {
        if (transformView.getSrcRotation() != hveVisibleAsset.getRotation()) {
            return true;
        }

        if (transformView.getSize() == null || hveVisibleAsset.getSize() == null
            || transformView.getSize().width != hveVisibleAsset.getSize().width
            || transformView.getSize().height != hveVisibleAsset.getSize().height) {
            return true;
        }

        if (transformView.getHVEPosition2DList() == null || transformView.getHVEPosition2DList().size() != 4
            || hveVisibleAsset.getRect() == null || hveVisibleAsset.getRect().size() != 4) {
            return true;
        }

        if (transformView.getHVEPosition2DList().get(0) == null || transformView.getHVEPosition2DList().get(1) == null
            || transformView.getHVEPosition2DList().get(2) == null
            || transformView.getHVEPosition2DList().get(3) == null) {
            return true;
        }

        if (hveVisibleAsset.getRect().get(0) == null || hveVisibleAsset.getRect().get(1) == null
            || hveVisibleAsset.getRect().get(2) == null || hveVisibleAsset.getRect().get(3) == null) {
            return true;
        }

        if (transformView.getHVEPosition2DList().get(0).xPos != hveVisibleAsset.getRect().get(0).xPos
            || transformView.getHVEPosition2DList().get(0).yPos != hveVisibleAsset.getRect().get(0).yPos) {
            return true;
        }

        return false;
    }

    private void refreshMaterialList() {
        mContentLayout.removeAllViews();
        if (mMaterialEditViewModel.getMaterialList() != null) {
            for (MaterialEditData materialEditData : mMaterialEditViewModel.getMaterialList()) {
                addMaterialRectView(materialEditData);
            }
        }
    }

    private void addMaterialRectView(MaterialEditData data) {
        if (data == null || data.getAsset() == null || isFullScreenState) {
            SmartLog.e(TAG, "data is unValid");
            return;
        }
        TransformView transformView;
        HuaweiVideoEditor editor = mEditPreviewViewModel.getEditor();
        switch (data.getMaterialType()) {
            case STICKER:
                transformView = new StickerView(mActivity);
                transformView.setEnableOutAreaLimit(true);
                if (editor != null) {
                    transformView.setAssetWidth(editor.getCanvasWidth());
                    transformView.setAssetHeight(editor.getCanvasHeight());
                }
                transformView.setDrawIconStatus(true, true, true, true);
                break;
            case WORD:
                transformView = new TextDefaultView(mActivity);
                transformView.setEnableOutAreaLimit(true);
                if (editor != null) {
                    transformView.setAssetWidth(editor.getCanvasWidth());
                    transformView.setAssetHeight(editor.getCanvasHeight());
                }
                transformView.setDrawIconStatus(true, !mEditPreviewViewModel.isEditTextStatus(),
                    !mEditPreviewViewModel.isEditTextStatus(), true);
                break;
            case PERSON:
                transformView = new TransformView(mActivity);
                transformView.setTransForm(false);
                transformView.setTouchAble(true);
                transformView.setDrawIconStatus(false, false, false, false);
                break;
            case FACE:
            case WORD_TAIL:
                transformView = new TransformView(mActivity);
                transformView.setTransForm(false);
                transformView.setTouchAble(false);
                transformView.setDrawIconStatus(false, false, false, false);
                break;
            case MAIN_LANE:
            case PIP_LANE:
            default:
                if ((data.getMaterialType() == MaterialEditData.MaterialType.MAIN_LANE
                    || data.getMaterialType() == MaterialEditData.MaterialType.PIP_LANE)) {
                    mContentLayout.setVisibility(View.VISIBLE);
                }
                transformView = new TransformView(mActivity);
                break;
        }

        transformView.setTag(data.getAsset().getUuid());

        if (data.getMaterialType() == MaterialEditData.MaterialType.FACE
            || data.getMaterialType() == MaterialEditData.MaterialType.PERSON) {
            transformView.setRectangularPoints(data.getFaceBoxList());
        } else {
            transformView.setRectangularPoints(data.getAsset().getRect(), data.getAsset().getSize(),
                data.getAsset().getRotation());
        }

        transformView.setOnEditListener(new OnMaterialEditListener() {

            HVESize oldSize;

            boolean isGenerateAction;

            @Override
            public void onTap(HVEPosition2D position2D) {
                SmartLog.i(TAG, "[onTap] start");
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null) {
                    return;
                }
                boolean isPersonTrackingEdit = mEditPreviewViewModel.isPersonTrackingStatus();
                if (isPersonTrackingEdit) {
                    if (position2D.xPos == 0 && position2D.yPos == 0) {
                        return;
                    }
                    mMaterialEditViewModel.clearMaterialEditData();
                    getPoint(position2D.xPos, position2D.yPos);
                    return;
                }
                HVEAsset asset = getIEditable(position2D, mMaterialEditViewModel.getCurrentFirstMenuId());

                long currentTime = System.currentTimeMillis();
                if (mLastSelectWordTime != 0 && currentTime - mLastSelectWordTime < DOUBLE_TAP_TIMEOUT) {
                    if (asset instanceof HVEWordAsset
                        && ((HVEWordAsset) asset).getWordAssetType() != HVEWordAsset.HVEWordAssetType.NORMAL_TEMPLATE) {
                        if (asset.getUuid().equals(mLastSelectWordUUID)) {
                            mMaterialEditViewModel.setTextDefaultEdit(data);
                            return;
                        }
                    }
                }
                mLastSelectWordTime = 0;
                mLastSelectWordUUID = "";

                if (asset == null) {
                    if (mEditPreviewViewModel.isEditTextStatus() || mEditPreviewViewModel.isEditTextTemplateStatus()
                        || mEditPreviewViewModel.isEditStickerStatus()) {
                        return;
                    }
                    mMaterialEditViewModel.clearMaterialEditData();
                    mEditPreviewViewModel.setChoiceAsset(null);
                    return;
                }

                if (asset.getUuid().equals(data.getAsset().getUuid())) {
                    onFingerDown();
                } else {
                    mEditPreviewViewModel.setChoiceAsset(asset);
                }
            }

            @Override
            public void onFingerDown() {
                SmartLog.i(TAG, "[onFingerDown] start");
                if (data.getAsset() == null || data.getAsset().getPosition() == null
                    || data.getAsset().getSize() == null) {
                    return;
                }
                oldSize = new HVESize(data.getAsset().getSize().width, data.getAsset().getSize().height);
            }

            @Override
            public void onFling(float dx, float dy) {
                SmartLog.i(TAG, "[onFling] start");
                if (data.getAsset() == null) {
                    return;
                }
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null || editor.getTimeLine() == null) {
                    return;
                }
                isGenerateAction = true;
                HVEPosition2D hvePosition2D = data.getAsset().getPosition();
                if (hvePosition2D == null) {
                    return;
                }
                data.getAsset().setPosition(hvePosition2D.xPos + dx, hvePosition2D.yPos + dy);
                mEditPreviewViewModel.getEditor().refresh(mEditPreviewViewModel.getTimeLine().getCurrentTime());

                transformView.setRectangularPoints(data.getAsset().getRect(), data.getAsset().getSize(),
                    data.getAsset().getRotation());
            }

            @Override
            public void onVibrate() {
                SmartLog.i(TAG, "[onVibrate] start");
                vibrate();
            }

            @Override
            public void onShowReferenceLine(boolean isShow, boolean isHorizontal) {
                mReferenceLineView.setShowReferenceLine(isShow, isHorizontal);
            }

            @Override
            public void onFingerUp() {
                SmartLog.i(TAG, "[onFingerUp] start");
                mReferenceLineView.setShowReferenceLine(false, true);
                mReferenceLineView.setShowReferenceLine(false, false);
                if (data.getAsset() == null || data.getAsset().getPosition() == null) {
                    return;
                }
                if (!isGenerateAction) {
                    return;
                }
                isGenerateAction = false;
            }

            @Override
            public void onScaleRotate(float scale, float angle) {
                SmartLog.i(TAG, "[onFingerUp] start");
                if (data.getAsset() == null) {
                    return;
                }
                HuaweiVideoEditor editor = EditorManager.getInstance().getEditor();
                if (editor == null || editor.getTimeLine() == null) {
                    return;
                }
                if (scale < 0.2f) {
                    scale = 0.2f;
                }
                isGenerateAction = true;
                HVESize size = data.getAsset().getSize();
                if (oldSize == null) {
                    oldSize = new HVESize(size.width, size.height);
                }
                float rate = oldSize.height * 1.0f / oldSize.width;
                data.getAsset().setSize((int) (size.width * scale), (int) (size.width * scale * rate));
                data.getAsset().setRotation(angle);
                mEditPreviewViewModel.getEditor()
                    .seekTimeLine(editor.getTimeLine().getCurrentTime(), new HuaweiVideoEditor.SeekCallback() {
                        @Override
                        public void onSeekFinished() {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    transformView.setRectangularPoints(data.getAsset().getRect(),
                                        data.getAsset().getSize(), data.getAsset().getRotation());
                                }
                            });
                        }
                    });
            }

            @Override
            public void onDelete() {
                SmartLog.i(TAG, "[onDelete] start");
                mMaterialEditViewModel.setMaterialDelete(data);
                refreshMaterialList();
            }

            @Override
            public void onEdit() {
                SmartLog.i(TAG, "[onEdit] start");
                switch (data.getMaterialType()) {
                    case STICKER:
                        mMaterialEditViewModel.setStickerEdit(data);
                        break;
                    case WORD:
                        mMaterialEditViewModel.setTextDefaultEdit(data);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onDoubleFingerTap() {
                SmartLog.i(TAG, "[onDoubleFingerTap] start");
                if (data.getMaterialType() == MaterialEditData.MaterialType.WORD) {
                    mMaterialEditViewModel.setTextDefaultEdit(data);
                }
            }

            @Override
            public void onCopy() {
                SmartLog.i(TAG, "[onCopy] start");
                mMaterialEditViewModel.setMaterialCopy(data);
            }

        });
        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mContentLayout.addView(transformView, params);
    }

    private void vibrate() {
    }

    private HVEAsset getIEditable(HVEPosition2D position, int id) {
        if (EditorManager.getInstance().getTimeLine() == null || position == null) {
            return null;
        }
        return LaneSizeCheckUtils.getHVEAsset(EditorManager.getInstance().getEditor(), position,
            EditorManager.getInstance().getTimeLine().getCurrentTime(), MenuClickManager.getInstance().getLaneType(id));
    }
}
