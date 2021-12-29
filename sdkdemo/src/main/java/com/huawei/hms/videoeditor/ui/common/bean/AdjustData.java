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

package com.huawei.hms.videoeditor.ui.common.bean;

import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;

public class AdjustData {
    private String mEffectName;

    private String effectPath;

    private String effectId;

    private HVEEffect effect;

    private long startTime;

    private long mEndTime;

    private float brightness;

    private float contrast;

    private float saturation;

    private float hueAdjust;

    private float temperature;

    private float sharpness;

    private float lightsense;

    private float highlights;

    private float shadow;

    private float fade;

    private float darkangle;

    private float grain;

    public AdjustData(String effectName, String effectPath, String effectId, HVEEffect effect, long startTime,
        long endTime, float brightness, float contrast, float saturation, float hueAdjust, float temperature,
        float sharpness, float lightsense, float highlights, float shadow, float fade, float darkangle, float grain) {
        this.mEffectName = effectName;
        this.effectPath = effectPath;
        this.effectId = effectId;
        this.effect = effect;
        this.startTime = startTime;
        this.mEndTime = endTime;
        this.brightness = brightness;
        this.contrast = contrast;
        this.saturation = saturation;
        this.hueAdjust = hueAdjust;
        this.temperature = temperature;
        this.sharpness = sharpness;
        this.lightsense = lightsense;
        this.highlights = highlights;
        this.shadow = shadow;
        this.fade = fade;
        this.darkangle = darkangle;
        this.grain = grain;
    }

    public String getEffectName() {
        return mEffectName;
    }

    public void setEffectName(String effectName) {
        this.mEffectName = effectName;
    }

    public String getEffectPath() {
        return effectPath;
    }

    public void setEffectPath(String effectPath) {
        this.effectPath = effectPath;
    }

    public String getEffectId() {
        return effectId;
    }

    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    public HVEEffect getEffect() {
        return effect;
    }

    public void setEffect(HVEEffect effect) {
        this.effect = effect;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getHueAdjust() {
        return hueAdjust;
    }

    public void setHueAdjust(float hueAdjust) {
        this.hueAdjust = hueAdjust;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getSharpness() {
        return sharpness;
    }

    public void setSharpness(float sharpness) {
        this.sharpness = sharpness;
    }

    public float getLightsense() {
        return lightsense;
    }

    public void setLightsense(float lightsense) {
        this.lightsense = lightsense;
    }

    public float getHighlights() {
        return highlights;
    }

    public void setHighlights(float highlights) {
        this.highlights = highlights;
    }

    public float getShadow() {
        return shadow;
    }

    public void setShadow(float shadow) {
        this.shadow = shadow;
    }

    public float getFade() {
        return fade;
    }

    public void setFade(float fade) {
        this.fade = fade;
    }

    public float getDarkangle() {
        return darkangle;
    }

    public void setDarkangle(float darkangle) {
        this.darkangle = darkangle;
    }

    public float getGrain() {
        return grain;
    }

    public void setGrain(float grain) {
        this.grain = grain;
    }

    @Override
    public String toString() {
        return "AdjustData{" + "effectName='" + mEffectName + '\'' + ", effectPath='" + effectPath + '\''
            + ", effectId='" + effectId + '\'' + ", effect=" + effect + ", startTime=" + startTime + ", endTime="
            + mEndTime + ", brightness=" + brightness + ", contrast=" + contrast + ", saturation=" + saturation
            + ", hueAdjust=" + hueAdjust + ", temperature=" + temperature + ", sharpness=" + sharpness + ", lightsense="
            + lightsense + ", highlights=" + highlights + ", shadow=" + shadow + ", fade=" + fade + ", darkangle="
            + darkangle + ", grain=" + grain + '}';
    }
}
