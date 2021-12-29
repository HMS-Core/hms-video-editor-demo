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

import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;

public interface OnMaterialEditListener {

    void onTap(HVEPosition2D position2D);

    void onFingerDown();

    void onFling(float dx, float dy);

    void onVibrate();

    void onShowReferenceLine(boolean isShow, boolean isHorizontal);

    void onFingerUp();

    void onScaleRotate(float scale, float angle);

    void onDelete();

    void onEdit();

    void onDoubleFingerTap();

    void onCopy();
}
