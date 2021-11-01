
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

package com.huawei.hms.videoeditor.ui.mediaeditor.preview.view;

import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.CENTER_RADIUS_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.CENTER_X_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.CENTER_Y_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.DIFF_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.INVERT_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.MASK_HEIGHT_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.MASK_SIZE_RATE_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.MASK_WIDTH_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.ROTATION_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.SCALE_X_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.impl.ScriptableMaskEffect.SCALE_Y_KEY;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.bean.HVESize;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.MaskEffectViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class MaskEffectContainerView extends View implements Observer<Class<? extends MaskShape>> {

    private static final String TAG = "MaskEffectContainerView";

    private Paint linePaint;

    private Paint bitmapPaint;

    private Context mCtx;

    private MaskShape maskShape;

    private MaskEffectViewModel viewModel;

    private int mWidth;

    private int mHeight;

    public MaskEffectContainerView(Context context) {
        this(context, null);
    }

    public MaskEffectContainerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskEffectContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaskEffectContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    private void init(Context context) {
        this.mCtx = context;

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.color_text_focus));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);

        bitmapPaint = new Paint();

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        initViewModel();
    }

    private void initViewModel() {
        ViewModelProvider.AndroidViewModelFactory mFactory =
            new ViewModelProvider.AndroidViewModelFactory(((Activity) mCtx).getApplication());
        viewModel = new ViewModelProvider((ViewModelStoreOwner) mCtx, mFactory).get(MaskEffectViewModel.class);
        viewModel.getShapeClass().observe((LifecycleOwner) mCtx, this);
        viewModel.getHveVideoAsset().observe((LifecycleOwner) mCtx, new HveVideoLaneObserver());
        viewModel.getIsInverse().observe((LifecycleOwner) mCtx, new InverseObserver());
    }

    public void setMaskShape(MaskShape maskShape) {
        this.maskShape = maskShape;
        if (this.maskShape == null) {
            invalidate();
            viewModel.refresh();
            return;
        }

        maskShape.setOnInvalidate(new MaskShape.OnInvalidate() {
            @Override
            public void invalidateShape() {
                MaskEffectContainerView.this.invalidate();
            }

            @Override
            public void invalidateDrawable(Drawable drawable) {
                MaskEffectContainerView.this.invalidateDrawable(drawable);
            }
        });
        invalidate();
        viewModel.refresh();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maskShape != null) {
            maskShape.drawShape(canvas, linePaint, bitmapPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (maskShape != null) {
            return maskShape.onTouch(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onChanged(Class<? extends MaskShape> aClass) {

        if (getVisibility() != VISIBLE) {
            return;
        }
        // 构造ui图形
        MaskShape curShape = null;
        if (aClass == null) {
            viewModel.removeCurrentEffect();
            setMaskShape(null);
            return;
        }
        try {
            Constructor constructor = aClass.getConstructor(Context.class);
            curShape = (MaskShape) constructor.newInstance(mCtx);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
            | InvocationTargetException e) {
            SmartLog.e(TAG, e.getMessage());
        }

        initShape(curShape, viewModel.getHveVideoAsset().getValue());
        setMaskShape(curShape);
        setShapeMaskChange(curShape, viewModel.getHveVideoAsset().getValue());
    }

    public void initShape(MaskShape maskShape, HVEAsset hveVideoAsset) {
        if (maskShape == null || hveVideoAsset == null) {
            return;
        }

        if (!(hveVideoAsset instanceof HVEVisibleAsset)) {
            return;
        }

        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) hveVideoAsset;
        List<HVEPosition2D> list = visibleAsset.getRect();
        if (list == null || list.isEmpty() || list.size() < 2) {
            return;
        }
        for (HVEPosition2D h : list) {
            if (h == null) {
                return;
            }
        }

        float maskCenterx = 0.5f;
        float maskCentery = 0.5f;
        float pipRotate = visibleAsset.getRotation();

        int pipWidth = (int) spacing(list.get(1).xPos, list.get(1).yPos, list.get(2).xPos, list.get(2).yPos);
        int pipHeight = (int) spacing(list.get(1).xPos, list.get(1).yPos, list.get(0).xPos, list.get(0).yPos);
        float blur = 0;

        float maskRotate = 0;

        List<HVEEffect> hveEffects = hveVideoAsset.getEffectsWithType(HVEEffect.HVEEffectType.MASK);
        HVEEffect hveEffect = null;
        if (hveEffects.isEmpty()) {
            hveEffect =
                viewModel.appendHVEEffect(hveVideoAsset, viewModel.getMaterialsCutContentMutableLiveData().getValue());
        } else {
            hveEffect = hveEffects.get(0);
        }
        if (hveEffect == null) {
            return;
        }

        String effectName = hveEffect.getEffectName();
        if (!TextUtils.isEmpty(effectName)) {
            switch (effectName) {
                case HVEEffect.MASK_SEMIPLANEMASK:
                    maskCenterx = hveEffect.getFloatVal(CENTER_X_KEY);
                    maskCentery = hveEffect.getFloatVal(CENTER_Y_KEY);
                    maskRotate = hveEffect.getFloatVal(ROTATION_KEY);
                    blur = hveEffect.getFloatVal(DIFF_KEY);
                    Boolean isInverseSemip = viewModel.getIsInverse().getValue();
                    if (isInverseSemip != null && isInverseSemip) {
                        hveEffect.setFloatVal(INVERT_KEY, 1.0f);
                    } else {
                        hveEffect.setFloatVal(INVERT_KEY, 0.0f);
                    }
                    viewModel.refresh();
                    break;
                case HVEEffect.MASK_CYCLE:
                    maskCenterx = hveEffect.getFloatVal(CENTER_X_KEY);
                    maskCentery = hveEffect.getFloatVal(CENTER_Y_KEY);
                    maskRotate = hveEffect.getFloatVal(ROTATION_KEY);
                    blur = hveEffect.getFloatVal(DIFF_KEY);
                    if (maskShape instanceof CycleMask) {
                        CycleMask cycleMask = (CycleMask) maskShape;
                        if (cycleMask.aParameterSet instanceof CycleMask.CycleParameterSet) {
                            CycleMask.CycleParameterSet parameterSet =
                                (CycleMask.CycleParameterSet) cycleMask.aParameterSet;
                            float ew = visibleAsset.getSize().width;
                            float eh = visibleAsset.getSize().height;
                            float rate = ((CycleMask) maskShape).defSize;
                            hveEffect.setFloatVal(MASK_SIZE_RATE_KEY, rate);
                            if (Float.compare(ew, eh) > 0) {
                                float baseX = eh * rate;
                                float baseY = eh * rate;
                                parameterSet.setxLen((int) (baseX * hveEffect.getFloatVal(SCALE_X_KEY)));
                                parameterSet.setyLen((int) (baseY * hveEffect.getFloatVal(SCALE_Y_KEY)));
                            } else {
                                float baseX = ew * rate;
                                float baseY = ew * rate;
                                parameterSet.setxLen((int) (baseX * hveEffect.getFloatVal(SCALE_X_KEY)));
                                parameterSet.setyLen((int) (baseY * hveEffect.getFloatVal(SCALE_Y_KEY)));
                            }
                            Boolean isInverseCycle = viewModel.getIsInverse().getValue();
                            if (isInverseCycle != null && isInverseCycle) {
                                hveEffect.setFloatVal(INVERT_KEY, 1.0f);
                            } else {
                                hveEffect.setFloatVal(INVERT_KEY, 0.0f);
                            }
                        }
                    }
                    viewModel.refresh();
                    break;
                case HVEEffect.MASK_FLOWER:
                case HVEEffect.MASK_HEART:
                case HVEEffect.MASK_STAR:
                    maskCenterx = hveEffect.getFloatVal(CENTER_X_KEY);
                    maskCentery = hveEffect.getFloatVal(CENTER_Y_KEY);
                    maskRotate = hveEffect.getFloatVal(ROTATION_KEY);
                    Boolean isInverseStar = viewModel.getIsInverse().getValue();
                    if (isInverseStar != null && isInverseStar) {
                        hveEffect.setFloatVal(INVERT_KEY, 1.0f);
                    } else {
                        hveEffect.setFloatVal(INVERT_KEY, 0.0f);
                    }

                    if (maskShape instanceof StickerMask) {
                        StickerMask.StickerSet set = null;
                        Object objSet = maskShape.getParameterSet();
                        if (objSet instanceof StickerMask.StickerSet) {
                            set = (StickerMask.StickerSet) objSet;
                        }

                        float ews = visibleAsset.getSize().width;
                        float ehs = visibleAsset.getSize().height;
                        float rates = ((StickerMask) maskShape).lineWidthRate;

                        hveEffect.setFloatVal(MASK_SIZE_RATE_KEY, rates);
                        if (Float.compare(ews, ehs) > 0) {
                            hveEffect.setFloatVal(MASK_WIDTH_KEY, ehs * rates / ehs);
                            hveEffect.setFloatVal(MASK_HEIGHT_KEY, ehs * rates / ehs);
                            if (set != null) {
                                set.setDefVectorWidth((int) (ehs * rates));
                                set.setDefVectorHeight((int) (ehs * rates));
                            }
                        } else {
                            hveEffect.setFloatVal(MASK_WIDTH_KEY, ews * rates / ehs);
                            hveEffect.setFloatVal(MASK_HEIGHT_KEY, ews * rates / ehs);
                            if (set != null) {
                                set.setDefVectorWidth((int) (ews * rates));
                                set.setDefVectorHeight((int) (ews * rates));
                            }
                        }
                    }
                    viewModel.refresh();
                    break;
                case HVEEffect.MASK_RECTANGLE:
                    maskCenterx = hveEffect.getFloatVal(CENTER_X_KEY);
                    maskCentery = hveEffect.getFloatVal(CENTER_Y_KEY);
                    maskRotate = -hveEffect.getFloatVal(ROTATION_KEY);
                    blur = hveEffect.getFloatVal(DIFF_KEY);
                    Boolean isInverseRect = viewModel.getIsInverse().getValue();
                    if (isInverseRect != null && isInverseRect) {
                        hveEffect.setFloatVal(INVERT_KEY, 1.0f);
                    } else {
                        hveEffect.setFloatVal(INVERT_KEY, 0.0f);
                    }
                    RoundRectMask.RoundRectParameterSet roundRectParameterSet = null;
                    Object objSet = maskShape.getParameterSet();
                    if (objSet instanceof RoundRectMask.RoundRectParameterSet) {
                        roundRectParameterSet = (RoundRectMask.RoundRectParameterSet) objSet;
                    }

                    if (maskShape instanceof RoundRectMask) {
                        float rate = (float) ((RoundRectMask) maskShape).lineWidthRate;
                        int defSize = (int) getDefSize(rate, hveVideoAsset);
                        float w = getRate(hveVideoAsset);
                        if (Float.compare(hveEffect.getFloatVal(MASK_WIDTH_KEY), -1.0f) == 0
                            || Float.compare(hveEffect.getFloatVal(MASK_HEIGHT_KEY), -1.0f) == 0) {
                            hveEffect.setFloatVal(MASK_WIDTH_KEY, defSize);
                            hveEffect.setFloatVal(MASK_HEIGHT_KEY, defSize);
                            if (roundRectParameterSet != null) {
                                roundRectParameterSet.setMaskRectX((int) (defSize * w));
                                roundRectParameterSet.setMaskRectY((int) (defSize * w));
                            }
                        } else {
                            if (roundRectParameterSet != null) {
                                roundRectParameterSet.setMaskRectX((int) (hveEffect.getFloatVal(MASK_WIDTH_KEY) * w));
                                roundRectParameterSet.setMaskRectY((int) (hveEffect.getFloatVal(MASK_HEIGHT_KEY) * w));
                            }
                        }

                        if (roundRectParameterSet != null) {
                            roundRectParameterSet.setDefRoundAgle(hveEffect.getFloatVal(CENTER_RADIUS_KEY));
                        }
                    }
                    viewModel.refresh();
                    break;
                case HVEEffect.MASK_STIPEFFECT:
                    maskCenterx = hveEffect.getFloatVal(CENTER_X_KEY);
                    maskCentery = hveEffect.getFloatVal(CENTER_Y_KEY);
                    maskRotate = -hveEffect.getFloatVal(ROTATION_KEY);
                    blur = hveEffect.getFloatVal(DIFF_KEY);
                    Boolean isInverseStrip = viewModel.getIsInverse().getValue();
                    if (isInverseStrip != null && isInverseStrip) {
                        hveEffect.setFloatVal(INVERT_KEY, 1.0f);
                    } else {
                        hveEffect.setFloatVal(INVERT_KEY, 0.0f);
                    }
                    ParallelMask.ParallelParameterSet parameterSet = null;
                    Object objParaSet = maskShape.getParameterSet();
                    if (objParaSet instanceof ParallelMask.ParallelParameterSet) {
                        parameterSet = (ParallelMask.ParallelParameterSet) objParaSet;
                    }
                    if (maskShape instanceof ParallelMask) {
                        float rate = ((ParallelMask) maskShape).lineWidthRate;
                        int size = (int) getDefSize(rate, hveVideoAsset);
                        float rate1 = getRate(hveVideoAsset);
                        if (Float.compare(hveEffect.getFloatVal(MASK_HEIGHT_KEY), -1.0f) == 0) {
                            hveEffect.setFloatVal(MASK_HEIGHT_KEY, size);
                            if (parameterSet != null) {
                                parameterSet.setHeightDisatance((int) (size * rate1));
                            }
                        } else {
                            if (parameterSet != null) {
                                parameterSet.setHeightDisatance((int) (hveEffect.getFloatVal(MASK_HEIGHT_KEY) * rate1));
                            }
                        }
                    }
                    viewModel.refresh();
                    break;
                default:
                    break;
            }
        }
        maskShape.init(mWidth, mHeight, list, pipWidth, pipHeight, maskCenterx, maskCentery, pipRotate, maskRotate,
            blur);
    }

    private float getDefSize(float rate, HVEAsset asset) {
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        if (visibleAsset.getWidth() > visibleAsset.getHeight()) {
            return visibleAsset.getHeight() * rate;
        } else {
            return visibleAsset.getWidth() * rate;
        }
    }

    private float getRate(HVEAsset asset) {
        int videoSize = 0;
        float viewSize = 0;
        HVEVisibleAsset visibleAsset = (HVEVisibleAsset) asset;
        if (visibleAsset.getWidth() > visibleAsset.getHeight()) {
            videoSize = visibleAsset.getHeight();
            viewSize = visibleAsset.getSize().height;
        } else {
            videoSize = visibleAsset.getWidth();
            viewSize = visibleAsset.getSize().width;
        }
        return viewSize * 1.0f / videoSize;
    }

    private class HveVideoLaneObserver implements Observer<HVEAsset> {
        @Override
        public void onChanged(HVEAsset hve) {
            List<HVEEffect> hveEffects = hve.getEffectsWithType(HVEEffect.HVEEffectType.MASK);
            if (hveEffects.isEmpty()) {
                setMaskShape(null);
                return;
            }

            HVEEffect hveEffect = hveEffects.get(0);
            MaskShape curShape = null;
            String effectName = hveEffect.getEffectName();
            if (!TextUtils.isEmpty(effectName)) {
                switch (effectName) {
                    case HVEEffect.MASK_SEMIPLANEMASK:
                        curShape = new LineMask(mCtx);
                        break;
                    case HVEEffect.MASK_RECTANGLE:
                        curShape = new RoundRectMask(mCtx);
                        break;
                    case HVEEffect.MASK_CYCLE:
                        curShape = new CycleMask(mCtx);
                        break;
                    case HVEEffect.MASK_FLOWER:
                        curShape = new FlowerMask(mCtx);
                        break;
                    case HVEEffect.MASK_HEART:
                        curShape = new HeartMask(mCtx);
                        break;
                    case HVEEffect.MASK_STAR:
                        curShape = new StarMask(mCtx);
                        break;
                    case HVEEffect.MASK_STIPEFFECT:
                        curShape = new ParallelMask(mCtx);
                        break;
                    default:
                        SmartLog.i(TAG, "onChanged run in default case");
                }
            }
            if (curShape != null) {
                initShape(curShape, hve);
                setMaskShape(curShape);
                setShapeMaskChange(curShape, hve);
            }
        }
    }

    protected float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    private class InverseObserver implements Observer<Boolean> {
        @Override
        public void onChanged(Boolean aBoolean) {
            HVEAsset hveAsset = viewModel.getHveVideoAsset().getValue();
            if (hveAsset == null) {
                return;
            }
            List<HVEEffect> hveEffectList = hveAsset.getEffectsWithType(HVEEffect.HVEEffectType.MASK);
            if (hveEffectList.isEmpty()) {
                return;
            }
            HVEEffect hveEffect = hveEffectList.get(0);
            if (aBoolean) {
                hveEffect.setFloatVal(INVERT_KEY, 1.0f);
            } else {
                hveEffect.setFloatVal(INVERT_KEY, 0.0f);
            }
            viewModel.refresh();
        }
    }

    private void setShapeMaskChange(MaskShape ms, HVEAsset asset) {
        if (ms == null) {
            return;
        }
        ms.setOnParameterSetChange((MaskShape.ParameterSetChange<MaskShape.ParameterSet>) parameterSet -> {
            if (viewModel == null) {
                return;
            }
            MutableLiveData<HVEAsset> mutableLiveData = viewModel.getHveVideoAsset();
            if (mutableLiveData == null) {
                return;
            }
            HVEAsset hveAssetValue = mutableLiveData.getValue();
            if (hveAssetValue == null) {
                return;
            }
            List<HVEEffect> list = hveAssetValue.getEffectsWithType(HVEEffect.HVEEffectType.MASK);
            if (list.isEmpty()) {
                return;
            }
            HVEEffect hveEffect = list.get(0);
            String effectName = hveEffect.getEffectName();
            if (!TextUtils.isEmpty(effectName)) {
                switch (effectName) {
                    case HVEEffect.MASK_SEMIPLANEMASK:
                        hveEffect.setFloatVal(ROTATION_KEY, parameterSet.getMaskRotation());
                        hveEffect.setFloatVal(CENTER_X_KEY, parameterSet.getMaskPosition().x);
                        hveEffect.setFloatVal(CENTER_Y_KEY, parameterSet.getMaskPosition().y);
                        hveEffect.setFloatVal(DIFF_KEY, parameterSet.getMaskBlur());
                        viewModel.refresh();
                        break;

                    case HVEEffect.MASK_RECTANGLE:
                        RoundRectMask.RoundRectParameterSet roundRectParameterSet =
                            (RoundRectMask.RoundRectParameterSet) parameterSet;
                        hveEffect.setFloatVal(ROTATION_KEY, -roundRectParameterSet.getMaskRotation());

                        MutableLiveData<HVEAsset> liveData = viewModel.getHveVideoAsset();
                        HVEAsset hveAsset = null;
                        if (liveData != null) {
                            hveAsset = liveData.getValue();
                        }
                        if (hveAsset != null) {
                            float w = getRate(hveAsset);
                            hveEffect.setFloatVal(MASK_WIDTH_KEY, roundRectParameterSet.getMaskRectX() / w);
                            hveEffect.setFloatVal(MASK_HEIGHT_KEY, roundRectParameterSet.getMaskRectY() / w);
                            hveEffect.setFloatVal(CENTER_RADIUS_KEY, roundRectParameterSet.getDefRoundAgle() / w);
                        }
                        hveEffect.setFloatVal(CENTER_X_KEY, roundRectParameterSet.getMaskPosition().x);
                        hveEffect.setFloatVal(CENTER_Y_KEY, roundRectParameterSet.getMaskPosition().y);
                        hveEffect.setFloatVal(DIFF_KEY, roundRectParameterSet.getMaskBlur());
                        viewModel.refresh();
                        break;

                    case HVEEffect.MASK_CYCLE:
                        CycleMask.CycleParameterSet cycleParameterSet = (CycleMask.CycleParameterSet) parameterSet;
                        hveEffect.setFloatVal(ROTATION_KEY, cycleParameterSet.getMaskRotation());
                        float dx = cycleParameterSet.xLen;
                        float dy = cycleParameterSet.yLen;
                        if (asset instanceof HVEVisibleAsset) {
                            HVEVisibleAsset editAble = (HVEVisibleAsset) asset;
                            float ew = editAble.getSize().width;
                            float eh = editAble.getSize().height;
                            float rate = ((CycleMask) maskShape).defSize;
                            if (Float.compare(ew, eh) > 0) {
                                float baseX = eh * rate;
                                float baseY = eh * rate;
                                hveEffect.setFloatVal(SCALE_X_KEY, dx / baseX);
                                hveEffect.setFloatVal(SCALE_Y_KEY, dy / baseY);
                            } else {
                                float baseX = ew * rate;
                                float baseY = ew * rate;
                                hveEffect.setFloatVal(SCALE_X_KEY, dx / baseX);
                                hveEffect.setFloatVal(SCALE_Y_KEY, dy / baseY);
                            }
                        }
                        hveEffect.setFloatVal(CENTER_X_KEY, cycleParameterSet.getMaskPosition().x);
                        hveEffect.setFloatVal(CENTER_Y_KEY, cycleParameterSet.getMaskPosition().y);
                        hveEffect.setFloatVal(DIFF_KEY, cycleParameterSet.getMaskBlur());
                        viewModel.refresh();
                        break;

                    case HVEEffect.MASK_FLOWER:
                    case HVEEffect.MASK_HEART:
                    case HVEEffect.MASK_STAR:
                        StickerMask.StickerSet stickerSet = (StickerMask.StickerSet) parameterSet;
                        hveEffect.setFloatVal(ROTATION_KEY, stickerSet.getMaskRotation());
                        hveEffect.setFloatVal(CENTER_X_KEY, stickerSet.getMaskPosition().x);
                        hveEffect.setFloatVal(CENTER_Y_KEY, stickerSet.getMaskPosition().y);
                        hveEffect.setFloatVal(DIFF_KEY, stickerSet.getMaskBlur());
                        HVEVisibleAsset editAble = (HVEVisibleAsset) viewModel.getHveVideoAsset().getValue();
                        if (editAble == null) {
                            return;
                        }
                        HVESize size = editAble.getSize();
                        if (size == null) {
                            return;
                        }
                        float eh = size.height;
                        int wh = stickerSet.getDefVectorWidthHalf() * 2;
                        hveEffect.setFloatVal(MASK_WIDTH_KEY, wh / eh);
                        hveEffect.setFloatVal(MASK_HEIGHT_KEY, wh / eh);
                        viewModel.refresh();
                        break;

                    case HVEEffect.MASK_STIPEFFECT:
                        ParallelMask.ParallelParameterSet parallelParameterSet =
                            (ParallelMask.ParallelParameterSet) parameterSet;
                        hveEffect.setFloatVal(ROTATION_KEY, -parallelParameterSet.getMaskRotation());
                        MutableLiveData<HVEAsset> hveVideoAsset = viewModel.getHveVideoAsset();
                        if (hveVideoAsset == null) {
                            return;
                        }
                        HVEAsset value = hveVideoAsset.getValue();
                        if (value == null) {
                            return;
                        }
                        float ws = getRate(value);
                        hveEffect.setFloatVal(MASK_HEIGHT_KEY, parallelParameterSet.getHeightDisatance() / ws);
                        hveEffect.setFloatVal(CENTER_X_KEY, parallelParameterSet.getMaskPosition().x);
                        hveEffect.setFloatVal(CENTER_Y_KEY, parallelParameterSet.getMaskPosition().y);
                        hveEffect.setFloatVal(DIFF_KEY, parallelParameterSet.getMaskBlur());
                        viewModel.refresh();
                        break;
                    default:
                        SmartLog.i(TAG, "setShapeMaskChange run in default case");
                }
            }
        });

    }
}
