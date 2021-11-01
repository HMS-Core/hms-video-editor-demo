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

package com.huawei.hms.videoeditor.ui.common.bean;

import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;

public class FilterData {
    private String effectName;

    private String effectPath;

    private String mEffectId;

    private HVEEffect effect;

    private long startTime;

    private long endTime;

    private float mStrength;

    public FilterData(String effectName, String effectPath, String effectId, HVEEffect effect, long startTime,
        long endTime, float strength) {
        this.effectName = effectName;
        this.effectPath = effectPath;
        this.mEffectId = effectId;
        this.effect = effect;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mStrength = strength;
    }

    public String getEffectName() {
        return effectName;
    }

    public void setEffectName(String effectName) {
        this.effectName = effectName;
    }

    public String getEffectPath() {
        return effectPath;
    }

    public void setEffectPath(String effectPath) {
        this.effectPath = effectPath;
    }

    public String getEffectId() {
        return mEffectId;
    }

    public void setEffectId(String effectId) {
        this.mEffectId = effectId;
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
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public float getStrength() {
        return mStrength;
    }

    public void setStrength(float strength) {
        this.mStrength = strength;
    }

    @Override
    public String toString() {
        return "FilterData{" + "effectName='" + effectName + '\'' + ", effectPath='" + effectPath + '\''
            + ", effectId='" + mEffectId + '\'' + ", effect=" + effect + ", startTime=" + startTime + ", endTime="
            + endTime + ", strength=" + mStrength + '}';
    }
}
