
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

package com.huawei.hms.videoeditor.ui.mediaeditor.persontrack;

import java.util.ArrayList;
import java.util.List;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVECut;
import com.huawei.hms.videoeditor.sdk.bean.HVEPosition2D;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.mediaeditor.materialedit.MaterialEditData;

public class DrawBoxUtil {
    public static synchronized MaterialEditData drawBox(HVEAsset hveAsset, HVETimeLine timeLine,
        MaterialEditData.MaterialType materialType, float minX, float minY, float maxX, float maxY) {
        float bottomLeftX = 0;
        float bottomLeftY = 0;

        float topLeftX = 0;
        float topLeftY = 0;

        float topRightX = 0;
        float topRightY = 0;

        float bottomRightX = 0;
        float bottomRightY = 0;

        float cutLeftBottomX = 0;
        float cutLeftBottomY = 0;
        float cutRightTopX = 0;
        float cutRightTopY = 0;
        boolean isMirror = false;
        boolean isVerticalMirror = false;
        HVECut hveCut = null;
        if (timeLine != null) {
            HVEVideoLane videoLane = timeLine.getVideoLane(0);
            if (videoLane != null) {
                if (hveAsset != null) {
                    List<HVEPosition2D> position2DList = new ArrayList<>();
                    if (hveAsset instanceof HVEVideoAsset) {
                        position2DList = ((HVEVideoAsset) hveAsset).getRect();
                        isMirror = ((HVEVideoAsset) hveAsset).getHorizontalMirrorState();
                        isVerticalMirror = ((HVEVideoAsset) hveAsset).getVerticalMirrorState();
                    } else if (hveAsset instanceof HVEImageAsset) {
                        position2DList = ((HVEImageAsset) hveAsset).getRect();
                        isMirror = ((HVEImageAsset) hveAsset).getHorizontalMirrorState();
                        isVerticalMirror = ((HVEImageAsset) hveAsset).getVerticalMirrorState();
                    }
                    bottomLeftX = position2DList.get(0).xPos;
                    bottomLeftY = position2DList.get(0).yPos;
                    topLeftX = position2DList.get(1).xPos;
                    topLeftY = position2DList.get(1).yPos;
                    topRightX = position2DList.get(2).xPos;
                    topRightY = position2DList.get(2).yPos;
                    bottomRightX = position2DList.get(3).xPos;
                    bottomRightY = position2DList.get(3).yPos;
                    if (hveCut == null) {
                        cutLeftBottomX = 0;
                        cutLeftBottomY = 0;
                        cutRightTopX = 1;
                        cutRightTopY = 1;
                    } else {
                        cutLeftBottomX = hveCut.getGlLeftBottomX();
                        cutLeftBottomY = hveCut.getGlLeftBottomY();
                        cutRightTopX = hveCut.getGlRightTopX();
                        cutRightTopY = hveCut.getGlRightTopY();
                    }
                }
            }
        }

        List<HVEPosition2D> faceBoxList = new ArrayList<>();
        float minX1;
        float maxY1;
        if (!isMirror) {
            minX1 = (minX - cutLeftBottomX) / (cutRightTopX - cutLeftBottomX);
        } else {
            minX1 = 1 - ((minX - cutLeftBottomX) / (cutRightTopX - cutLeftBottomX));
        }
        if (!isVerticalMirror) {
            maxY1 = (maxY - (1 - cutRightTopY)) / (cutRightTopY - cutLeftBottomY);
        } else {
            maxY1 = 1 - ((maxY - (1 - cutRightTopY)) / (cutRightTopY - cutLeftBottomY));
        }
        minX1 = setPointRatio(minX1);
        maxY1 = setPointRatio(maxY1);
        float x1 = (topLeftX * (1 - minX1) + topRightX * minX1) * (1 - maxY1)
            + (bottomLeftX * (1 - minX1) + bottomRightX * minX1) * maxY1;
        float y1 = (topLeftY * (1 - minX1) + topRightY * minX1) * (1 - maxY1)
            + (bottomLeftY * (1 - minX1) + bottomRightY * minX1) * maxY1;
        faceBoxList.add(new HVEPosition2D(x1, y1));
        float minX2;
        float minY2;
        if (!isMirror) {
            minX2 = (minX - cutLeftBottomX) / (cutRightTopX - cutLeftBottomX);
        } else {
            minX2 = 1 - ((minX - cutLeftBottomX) / (cutRightTopX - cutLeftBottomX));
        }
        if (!isVerticalMirror) {
            minY2 = (minY - (1 - cutRightTopY)) / (cutRightTopY - cutLeftBottomY);
        } else {
            minY2 = 1 - ((minY - (1 - cutRightTopY)) / (cutRightTopY - cutLeftBottomY));
        }
        minX2 = setPointRatio(minX2);
        minY2 = setPointRatio(minY2);
        float x2 = (topLeftX * (1 - minX2) + topRightX * minX2) * (1 - minY2)
            + (bottomLeftX * (1 - minX2) + bottomRightX * minX2) * minY2;
        float y2 = (topLeftY * (1 - minX2) + topRightY * minX2) * (1 - minY2)
            + (bottomLeftY * (1 - minX2) + bottomRightY * minX2) * minY2;
        faceBoxList.add(new HVEPosition2D(x2, y2));
        float maxX3;
        float minY3;
        if (!isMirror) {
            maxX3 = (maxX - cutLeftBottomX) / (cutRightTopX - cutLeftBottomX);
        } else {
            maxX3 = 1 - ((maxX - cutLeftBottomX) / (cutRightTopX - cutLeftBottomX));
        }
        if (!isVerticalMirror) {
            minY3 = (minY - (1 - cutRightTopY)) / (cutRightTopY - cutLeftBottomY);
        } else {
            minY3 = 1 - ((minY - (1 - cutRightTopY)) / (cutRightTopY - cutLeftBottomY));
        }
        maxX3 = setPointRatio(maxX3);
        minY3 = setPointRatio(minY3);
        float x3 = (topLeftX * (1 - maxX3) + topRightX * maxX3) * (1 - minY3)
            + (bottomLeftX * (1 - maxX3) + bottomRightX * maxX3) * minY3;
        float y3 = (topLeftY * (1 - maxX3) + topRightY * maxX3) * (1 - minY3)
            + (bottomLeftY * (1 - maxX3) + bottomRightY * maxX3) * minY3;
        faceBoxList.add(new HVEPosition2D(x3, y3));
        float maxX4;
        float maxY4;
        if (!isMirror) {
            maxX4 = (maxX - cutLeftBottomX) / (cutRightTopX - cutLeftBottomX);
        } else {
            maxX4 = 1 - ((maxX - cutLeftBottomX) / (cutRightTopX - cutLeftBottomX));
        }
        if (!isVerticalMirror) {
            maxY4 = (maxY - (1 - cutRightTopY)) / (cutRightTopY - cutLeftBottomY);
        } else {
            maxY4 = 1 - ((maxY - (1 - cutRightTopY)) / (cutRightTopY - cutLeftBottomY));
        }
        maxX4 = setPointRatio(maxX4);
        maxY4 = setPointRatio(maxY4);
        float x4 = (topLeftX * (1 - maxX4) + topRightX * maxX4) * (1 - maxY4)
            + (bottomLeftX * (1 - maxX4) + bottomRightX * maxX4) * maxY4;
        float y4 = (topLeftY * (1 - maxX4) + topRightY * maxX4) * (1 - maxY4)
            + (bottomLeftY * (1 - maxX4) + bottomRightY * maxX4) * maxY4;
        faceBoxList.add(new HVEPosition2D(x4, y4));
        if (hveAsset instanceof HVEVisibleAsset) {
            return new MaterialEditData((HVEVisibleAsset) hveAsset, materialType, faceBoxList);
        } else {
            return new MaterialEditData(null, materialType, faceBoxList);
        }
    }


    private static float setPointRatio(float pointRatio) {
        float result = 0;
        if (pointRatio <= 0) {
            result = 0;
        } else if (pointRatio >= 1) {
            result = 1;
        } else {
            result = pointRatio;
        }
        return result;
    }
}
