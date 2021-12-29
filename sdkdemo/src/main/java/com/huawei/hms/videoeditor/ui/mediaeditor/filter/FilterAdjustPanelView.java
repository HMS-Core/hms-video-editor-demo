
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

package com.huawei.hms.videoeditor.ui.mediaeditor.filter;

import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_BRIGHTNESS_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_CONTRAST_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_EXPOSURE_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_FADE_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_GRAIN_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_HIGHLIGHT_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_HUEADJUST_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_SATURATION_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_SHADOWS_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_SHARPNESS_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_TEMPERATURE_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.ADJUST_VIGNETTE_KEY;
import static com.huawei.hms.videoeditor.sdk.effect.HVEEffect.HVEEffectType.ADJUST;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.LTR_UI;
import static com.huawei.hms.videoeditor.ui.common.bean.Constant.RTL_UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;
import com.huawei.hms.videoeditor.sdk.effect.impl.ColorAdjustEffect;
import com.huawei.hms.videoeditor.sdk.lane.HVEEffectLane;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.common.adapter.OnEditItemClickListener;
import com.huawei.hms.videoeditor.ui.common.adapter.ValueItemAdapter;
import com.huawei.hms.videoeditor.ui.common.bean.AdjustData;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.LaneSizeCheckUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ScreenUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.preview.view.FilterSeekBar;
import com.huawei.hms.videoeditor.ui.mediaeditor.trackview.viewmodel.EditPreviewViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

public class FilterAdjustPanelView extends BaseFragment
    implements FilterSeekBar.OnProgressChangedListener, OnEditItemClickListener {
    private static final String TAG = "FilterAdjustPanelView";

    private RecyclerView rl_type;

    private ImageView iv_certain;

    private int minProgress = 0;

    private FilterSeekBar sb_items;

    private TextView reset;

    private int maxProgress = 100;

    private int anchorProgress = 0;

    private int position = -1;

    private int brightnessProgress = 0;

    private int contrastProgress = 0;

    private int saturationProgress = 0;

    private int hueProgress = 0;

    private int temperatureProgress = 0;

    private int sharpeningProgress = 0;

    private int exposureProgress = 0;

    private int highlightsProgress = 0;

    private int shadowProgress = 0;

    private int fadeProgress = 0;

    private int vignetteProgress = 0;

    private int grainProgress = 0;

    private ValueItemAdapter valueItemApdater;

    private int[] items = {R.string.filter_brightness, R.string.filter_contrast, R.string.filter_saturation,
        R.string.filter_hue, R.string.filter_temperature, R.string.filter_sharpening, R.string.filter_exposure,
        R.string.filter_highlights, R.string.filter_shadow, R.string.filter_fade, R.string.filter_vignette,
        R.string.filter_grain};

    private EditPreviewViewModel mEditPreviewViewModel;

    private String contentName;

    private String contentPath;

    private String contentId;

    private long startTime = 0;

    private long endTime = 0;

    private HVEVisibleAsset visibleAsset;

    private static final int ADJUST_CHANGE = 0;

    private static final int ADJUST_LAST = 1;

    private Map<Integer, AdjustData> adjustDataMap = new HashMap();

    private static boolean isAsset;

    private float brightness = 0.0f;

    private float contrast = 0.0f;

    private float saturation = 0.0f;

    private float hueAdjust = 0.0f;

    private float temperature = 0.0f;

    private float sharpness = 0.0f;

    private float exposure = 0.0f;

    private float highlights = 0.0f;

    private float shadow = 0.0f;

    private float fade = 0.0f;

    private float vignette = 0.0f;

    private float grain = 0.0f;

    public static FilterAdjustPanelView newInstance(boolean isAssetStatus) {
        isAsset = isAssetStatus;
        FilterAdjustPanelView fragment = new FilterAdjustPanelView();
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.panel_filter_adjust;
    }

    @Override
    protected void initView(View view) {
        mEditPreviewViewModel = new ViewModelProvider((ViewModelStoreOwner) mActivity).get(EditPreviewViewModel.class);
        rl_type = view.findViewById(R.id.rl_type);
        ((TextView) view.findViewById(R.id.tv_title)).setText(mActivity.getString(R.string.filter_title));
        iv_certain = view.findViewById(R.id.iv_certain);
        reset = view.findViewById(R.id.reset_filter_adjust);
        sb_items = view.findViewById(R.id.sb_items);
        sb_items.setOnProgressChangedListener(this);
        sb_items.setbTouchListener(
            isTouch -> mEditPreviewViewModel.setToastTime(isTouch ? String.valueOf(sb_items.getProgress()) : ""));
        sb_items.setmMinProgress(minProgress);
        sb_items.setmMaxProgress(maxProgress);
        sb_items.setmAnchorProgress(anchorProgress);
        sb_items.setProgress(maxProgress / 2);
        if (ScreenUtil.isRTL()) {
            sb_items.setScaleX(RTL_UI);
        } else {
            sb_items.setScaleX(LTR_UI);
        }
        List<Integer> imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.ic_value_item_1);
        imageList.add(R.drawable.ic_value_item_2);
        imageList.add(R.drawable.ic_value_item_3);
        imageList.add(R.drawable.ic_value_item_4);
        imageList.add(R.drawable.ic_value_item_5);
        imageList.add(R.drawable.ic_value_item_6);
        imageList.add(R.drawable.ic_value_item_7);
        imageList.add(R.drawable.ic_value_item_8);
        imageList.add(R.drawable.ic_value_item_9);
        imageList.add(R.drawable.ic_value_item_10);
        imageList.add(R.drawable.ic_value_item_11);
        imageList.add(R.drawable.ic_value_item_12);
        valueItemApdater = new ValueItemAdapter(mActivity, items, imageList);
        valueItemApdater.setOnItemClickListener(this);
        rl_type.setAdapter(valueItemApdater);

        if (isAsset) {
            HVEAsset asset = null;
            asset = mEditPreviewViewModel.getSelectedAsset();
            if (asset == null) {
                asset = mEditPreviewViewModel.getMainLaneAsset();
            }
            if (asset instanceof HVEVisibleAsset) {
                visibleAsset = (HVEVisibleAsset) asset;
            }
        }
        adjustDataMap.put(ADJUST_CHANGE, null);
        adjustDataMap.put(ADJUST_LAST, null);
        initAdjustData(visibleAsset, isAsset);
    }

    @Override
    protected void initObject() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        iv_certain.setOnClickListener(new OnClickRepeatedListener(v -> {
            mActivity.onBackPressed();
        }));

        reset.setOnClickListener(new OnClickRepeatedListener(v -> {
            brightness = 0.0f;
            contrast = 0.0f;
            saturation = 0.0f;
            hueAdjust = 0.0f;
            temperature = 0.0f;
            sharpness = 0.0f;
            exposure = 0.0f;
            highlights = 0.0f;
            shadow = 0.0f;
            fade = 0.0f;
            vignette = 0.0f;
            grain = 0.0f;
            reset();

        }));
    }

    @Override
    protected int setViewLayoutEvent() {
        return FIXED_HEIGHT_210;
    }

    private void reset() {
        if (adjustDataMap != null) {
            AdjustData adjustData = adjustDataMap.get(ADJUST_CHANGE);
            if (adjustData != null) {
                HVEEffect effect = adjustData.getEffect();
                if (adjustData.getBrightness() != 0 || adjustData.getContrast() != 0 || adjustData.getSaturation() != 0
                    || adjustData.getHueAdjust() != 0 || adjustData.getTemperature() != 0
                    || adjustData.getLightsense() != 0 || adjustData.getHighlights() != 0) {
                    sb_items.setProgress(50);
                }
                if (adjustData.getSharpness() != 0 || adjustData.getShadow() != 0 || adjustData.getFade() != 0
                    || adjustData.getDarkangle() != 0 || adjustData.getGrain() != 0) {
                    sb_items.setProgress(0);
                }
                disPlayAdjustOnLan(effect, effect.getStartTime(), effect.getEndTime(), brightness, contrast, saturation,
                    hueAdjust, temperature, sharpness, exposure, highlights, shadow, fade, vignette, grain);
                AdjustData resetData = new AdjustData(contentName, contentPath, contentId, effect, startTime, endTime,
                    brightness, contrast, saturation, hueAdjust, temperature, sharpness, exposure, highlights, shadow,
                    fade, vignette, grain);
                adjustDataMap.put(ADJUST_CHANGE, resetData);
                adjustDataMap.put(ADJUST_LAST, resetData);
            }

        }
    }

    @Override
    public void onProgressChanged(int progress) {
        mEditPreviewViewModel.setToastTime(String.valueOf(progress));
        if (position == 0) {
            this.brightnessProgress = progress;
            brightness = (float) progress / 50 - 1f;
        } else if (position == 1) {
            this.contrastProgress = progress;
            contrast = (float) progress / 50 - 1f;
        } else if (position == 2) {
            this.saturationProgress = progress;
            saturation = (float) progress / 50 - 1f;
        } else if (position == 3) {
            this.hueProgress = progress;
            hueAdjust = ((float) progress / 50 - 1f) * 10;
        } else if (position == 4) {
            this.temperatureProgress = progress;
            temperature = (float) progress / 50 - 1f;
        } else if (position == 5) {
            this.sharpeningProgress = progress;
            sharpness = (float) progress / 100;
        } else if (position == 6) {
            this.exposureProgress = progress;
            exposure = (float) progress / 50 - 1f;
        } else if (position == 7) {
            this.highlightsProgress = progress;
            highlights = (float) progress / 50 - 1f;
        } else if (position == 8) {
            this.shadowProgress = progress;
            shadow = (float) progress / 100;
        } else if (position == 9) {
            this.fadeProgress = progress;
            fade = (float) progress / 100;
        } else if (position == 10) {
            this.vignetteProgress = progress;
            vignette = (float) progress / 100;
        } else if (position == 11) {
            this.grainProgress = progress;
            grain = (float) progress / 100;
        }

        HVEEffect hveEffect = null;
        if (adjustDataMap.get(ADJUST_CHANGE) != null) {
            hveEffect = adjustDataMap.get(ADJUST_CHANGE).getEffect();
        }
        if (!isAsset) {
            hveEffect = disPlayAdjustOnLan(hveEffect, 0, 0, brightness, contrast, saturation, hueAdjust, temperature,
                sharpness, exposure, highlights, shadow, fade, vignette, grain);
        } else {
            hveEffect = disPlayAdjustInLan(hveEffect, brightness, contrast, saturation, hueAdjust, temperature,
                sharpness, exposure, highlights, shadow, fade, vignette, grain);
        }
        if (hveEffect == null) {
            return;
        }

        contentName = hveEffect.getOptions().getEffectName();
        contentPath = hveEffect.getOptions().getEffectPath();
        contentId = hveEffect.getOptions().getEffectId();
        startTime = hveEffect.getStartTime();
        endTime = hveEffect.getEndTime();
        brightness = hveEffect.getFloatVal(ADJUST_BRIGHTNESS_KEY);
        contrast = hveEffect.getFloatVal(ADJUST_CONTRAST_KEY);
        saturation = hveEffect.getFloatVal(ADJUST_SATURATION_KEY);
        hueAdjust = hveEffect.getFloatVal(ADJUST_HUEADJUST_KEY);
        temperature = hveEffect.getFloatVal(ADJUST_TEMPERATURE_KEY);
        sharpness = hveEffect.getFloatVal(ADJUST_SHARPNESS_KEY);
        exposure = hveEffect.getFloatVal(ADJUST_EXPOSURE_KEY);
        highlights = hveEffect.getFloatVal(ADJUST_HIGHLIGHT_KEY);
        shadow = hveEffect.getFloatVal(ADJUST_SHADOWS_KEY);
        fade = hveEffect.getFloatVal(ADJUST_FADE_KEY);
        vignette = hveEffect.getFloatVal(ADJUST_VIGNETTE_KEY);
        grain = hveEffect.getFloatVal(ADJUST_GRAIN_KEY);

        AdjustData adjustData =
            new AdjustData(contentName, contentPath, contentId, hveEffect, startTime, endTime, brightness, contrast,
                saturation, hueAdjust, temperature, sharpness, exposure, highlights, shadow, fade, vignette, grain);
        adjustDataMap.put(ADJUST_CHANGE, adjustData);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, Object data, int position) {
        if (this.position == position) {
            return;
        } else {
            sb_items.setVisibility(View.VISIBLE);
            reset.setVisibility(View.VISIBLE);
        }
        this.position = position;
        if (position == 0) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            brightnessProgress = (int) ((brightness + 1) * 50);
            sb_items.setProgress(brightnessProgress);
        } else if (position == 1) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            contrastProgress = (int) ((contrast + 1) * 50);
            sb_items.setProgress(contrastProgress);
        } else if (position == 2) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            saturationProgress = (int) ((saturation + 1) * 50);
            sb_items.setProgress(saturationProgress);
        } else if (position == 3) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            hueProgress = (int) ((hueAdjust / 10 + 1) * 50);
            sb_items.setProgress(hueProgress);
        } else if (position == 4) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            temperatureProgress = (int) ((temperature + 1) * 50);
            sb_items.setProgress(temperatureProgress);
        } else if (position == 5) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            sharpeningProgress = (int) (sharpness * 100);
            sb_items.setProgress(sharpeningProgress);
        } else if (position == 6) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            exposureProgress = (int) ((exposure + 1) * 50);
            sb_items.setProgress(exposureProgress);
        } else if (position == 7) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            highlightsProgress = (int) (highlights * 50 + 50);
            sb_items.setProgress(highlightsProgress);
        } else if (position == 8) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            shadowProgress = (int) (shadow * 100);
            sb_items.setProgress(shadowProgress);
        } else if (position == 9) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            fadeProgress = (int) (fade * 100);
            sb_items.setProgress(fadeProgress);
        } else if (position == 10) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            vignetteProgress = (int) (vignette * 100);
            sb_items.setProgress(vignetteProgress);
        } else if (position == 11) {
            minProgress = 0;
            maxProgress = 100;
            anchorProgress = 0;
            sb_items.setmMinProgress(minProgress);
            sb_items.setmMaxProgress(maxProgress);
            sb_items.setmAnchorProgress(anchorProgress);
            grainProgress = (int) (grain * 100);
            sb_items.setProgress(grainProgress);
        }
        sb_items.invalidate();
    }

    @Override
    public boolean onItemLongClick(RecyclerView.ViewHolder holder, Object data, int position) {
        return false;
    }

    private HVEEffect disPlayAdjustOnLan(HVEEffect effect, long startTime, long endTime, float brightness,
        float contrast, float saturation, float hueAdjust, float temperature, float sharpness, float lightsense,
        float highlights, float shadow, float fade, float darkangle, float grain) {
        HuaweiVideoEditor mEditor = mEditPreviewViewModel.getEditor();
        if (mEditor == null) {
            return null;
        }
        HVEVideoLane videoLane = mEditPreviewViewModel.getVideoLane();
        if (videoLane == null) {
            return null;
        }
        if (effect == null) {
            if (startTime == 0 && endTime == 0) {
                startTime = mEditPreviewViewModel.getSeekTime();
                endTime = Math.min(mEditPreviewViewModel.getSeekTime() + 3000, videoLane.getEndTime());
            }
            HVEEffectLane hveEffectLane = LaneSizeCheckUtils.getFilterFreeLan(mEditor, startTime, endTime);
            if (hveEffectLane == null) {
                return null;
            }
            effect = hveEffectLane.appendEffect(new HVEEffect.Options(HVEEffect.EFFECT_COLORADJUST, "", ""), startTime,
                3000);
            if (effect == null) {
                return null;
            }
            effect.setEndTime(endTime);
            mEditor.seekTimeLine(startTime);
        }

        HashMap<String, Float> maps = new HashMap<>();
        maps.put(ADJUST_BRIGHTNESS_KEY, brightness);
        maps.put(ADJUST_CONTRAST_KEY, contrast);
        maps.put(ADJUST_SATURATION_KEY, saturation);
        maps.put(ADJUST_HUEADJUST_KEY, hueAdjust);
        maps.put(ADJUST_TEMPERATURE_KEY, temperature);
        maps.put(ADJUST_SHARPNESS_KEY, sharpness);
        maps.put(ADJUST_EXPOSURE_KEY, lightsense);
        maps.put(ADJUST_HIGHLIGHT_KEY, highlights);
        maps.put(ADJUST_SHADOWS_KEY, shadow);
        maps.put(ADJUST_FADE_KEY, fade);
        maps.put(ADJUST_VIGNETTE_KEY, darkangle);
        maps.put(ADJUST_GRAIN_KEY, grain);
        if (effect instanceof ColorAdjustEffect) {
            ((ColorAdjustEffect) effect).setFloatVal(maps);
        }

        mEditor.seekTimeLine(mEditPreviewViewModel.getSeekTime());
        if (!isAsset) {
            mEditPreviewViewModel.setSelectedUUID(effect.getUuid());
        }
        return effect;
    }

    private HVEEffect disPlayAdjustInLan(HVEEffect effect, float brightness, float contrast, float saturation,
        float hueAdjust, float temperature, float sharpness, float lightsense, float highlights, float shadow,
        float fade, float darkangle, float grain) {
        HuaweiVideoEditor mEditor = mEditPreviewViewModel.getEditor();
        if (mEditor == null) {
            return null;
        }
        HVEVideoLane videoLane = mEditPreviewViewModel.getVideoLane();
        if (videoLane == null) {
            return null;
        }
        if (effect == null) {
            effect = visibleAsset.appendEffectUniqueOfType(new HVEEffect.Options(HVEEffect.EFFECT_COLORADJUST, "", ""),
                ADJUST);
        }

        HashMap<String, Float> maps = new HashMap<>();
        maps.put(ADJUST_BRIGHTNESS_KEY, brightness);
        maps.put(ADJUST_CONTRAST_KEY, contrast);
        maps.put(ADJUST_SATURATION_KEY, saturation);
        maps.put(ADJUST_HUEADJUST_KEY, hueAdjust);
        maps.put(ADJUST_TEMPERATURE_KEY, temperature);

        maps.put(ADJUST_SHARPNESS_KEY, sharpness);
        maps.put(ADJUST_EXPOSURE_KEY, lightsense);
        maps.put(ADJUST_HIGHLIGHT_KEY, highlights);
        maps.put(ADJUST_SHADOWS_KEY, shadow);
        maps.put(ADJUST_FADE_KEY, fade);
        maps.put(ADJUST_VIGNETTE_KEY, darkangle);
        maps.put(ADJUST_GRAIN_KEY, grain);
        if (effect instanceof ColorAdjustEffect) {
            ((ColorAdjustEffect) effect).setFloatVal(maps);
        }

        mEditor.seekTimeLine(mEditPreviewViewModel.getSeekTime());
        if (!isAsset) {
            mEditPreviewViewModel.refreshTrackView(effect.getUuid());
        }
        return effect;
    }

    private void initAdjustData(HVEVisibleAsset visibleAsset, boolean isAsset) {
        AdjustData adjustData = null;
        if (!isAsset) {
            HVEEffect effect = mEditPreviewViewModel.getSelectedEffect();
            if (effect == null) {
                return;
            }
            vignette = effect.getFloatVal(ADJUST_VIGNETTE_KEY);
            grain = effect.getFloatVal(ADJUST_GRAIN_KEY);
            contentId = effect.getOptions().getEffectId();
            startTime = effect.getStartTime();
            contentName = effect.getOptions().getEffectName();
            contentPath = effect.getOptions().getEffectPath();
            contrast = effect.getFloatVal(ADJUST_CONTRAST_KEY);
            saturation = effect.getFloatVal(ADJUST_SATURATION_KEY);
            endTime = effect.getEndTime();
            brightness = effect.getFloatVal(ADJUST_BRIGHTNESS_KEY);
            hueAdjust = effect.getFloatVal(ADJUST_HUEADJUST_KEY);
            temperature = effect.getFloatVal(ADJUST_TEMPERATURE_KEY);
            fade = effect.getFloatVal(ADJUST_FADE_KEY);
            highlights = effect.getFloatVal(ADJUST_HIGHLIGHT_KEY);
            shadow = effect.getFloatVal(ADJUST_SHADOWS_KEY);
            sharpness = effect.getFloatVal(ADJUST_SHARPNESS_KEY);
            exposure = effect.getFloatVal(ADJUST_EXPOSURE_KEY);

            adjustData =
                new AdjustData(contentName, contentPath, contentId, effect, startTime, endTime, brightness, contrast,
                    saturation, hueAdjust, temperature, sharpness, exposure, highlights, shadow, fade, vignette, grain);
        } else {
            if (visibleAsset == null) {
                return;
            }
            List<HVEEffect> hveEffects = visibleAsset.getEffectsWithType(ADJUST);
            if (hveEffects.isEmpty()) {
                return;
            }
            for (HVEEffect effect : hveEffects) {
                contentName = effect.getOptions().getEffectName();
                contentPath = effect.getOptions().getEffectPath();
                contentId = effect.getOptions().getEffectId();
                brightness = effect.getFloatVal(ADJUST_BRIGHTNESS_KEY);
                contrast = effect.getFloatVal(ADJUST_CONTRAST_KEY);
                saturation = effect.getFloatVal(ADJUST_SATURATION_KEY);
                hueAdjust = effect.getFloatVal(ADJUST_HUEADJUST_KEY);
                temperature = effect.getFloatVal(ADJUST_TEMPERATURE_KEY);
                sharpness = effect.getFloatVal(ADJUST_SHARPNESS_KEY);
                exposure = effect.getFloatVal(ADJUST_EXPOSURE_KEY);
                highlights = effect.getFloatVal(ADJUST_HIGHLIGHT_KEY);
                shadow = effect.getFloatVal(ADJUST_SHADOWS_KEY);
                fade = effect.getFloatVal(ADJUST_FADE_KEY);
                vignette = effect.getFloatVal(ADJUST_VIGNETTE_KEY);
                grain = effect.getFloatVal(ADJUST_GRAIN_KEY);

                adjustData = new AdjustData(contentName, contentPath, contentId, effect, startTime, endTime, brightness,
                    contrast, saturation, hueAdjust, temperature, sharpness, exposure, highlights, shadow, fade,
                    vignette, grain);
            }
        }

        if (adjustData == null) {
            return;
        }
        adjustDataMap.put(ADJUST_LAST, adjustData);
        adjustDataMap.put(ADJUST_CHANGE, adjustData);

        brightnessProgress = (int) ((brightness + 1f) * 50);
        sb_items.setProgress(brightnessProgress);
    }

    @Override
    protected void initViewModelObserve() {

    }
}
