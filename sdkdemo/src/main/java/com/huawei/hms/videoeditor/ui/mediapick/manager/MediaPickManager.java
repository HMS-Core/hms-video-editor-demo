
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

package com.huawei.hms.videoeditor.ui.mediapick.manager;

import java.util.ArrayList;
import java.util.List;

import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;

public class MediaPickManager {
    private List<MediaData> sSelectItemList = new ArrayList<>();

    private final List<OnSelectItemChangeListener> sOnSelectItemChangeListenerList = new ArrayList<>();

    private int maxSelectCount = Constant.MAX_PICK_NUM;

    private MediaData mediaData;

    public static MediaPickManager getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    public List<MediaData> getSelectItemList() {
        return sSelectItemList;
    }

    public void setsSelectItemList(List<MediaData> selectItemList) {
        this.sSelectItemList = selectItemList;
    }

    public MediaData getMediaData(String filePath) {
        MediaData data = null;
        if (!StringUtil.isEmpty(filePath)) {
            for (int i = 0; i < sSelectItemList.size(); i++) {
                if (!StringUtil.isEmpty(sSelectItemList.get(i).getPath())
                    && sSelectItemList.get(i).getPath().equals(filePath)) {
                    data = sSelectItemList.get(i);
                    break;
                }
            }
        }
        if (data == null) {
            data = mediaData;
        }
        if (data == null) {
            data = new MediaData();
        }
        data.setFromCloud(true);
        return data;
    }

    public boolean addSelectItemAndSetIndex(MediaData item) {
        if (sSelectItemList.size() >= maxSelectCount) {
            return false;
        }
        item.setIndex(sSelectItemList.size() + 1);
        item.setSetSelected(true);
        sSelectItemList.add(item);
        notifySelectItemChange(item);
        return true;
    }

    public boolean addSelectItemAndSetIndexNum(MediaData item) {
        if (sSelectItemList.size() >= maxSelectCount) {
            return false;
        }
        if (!sSelectItemList.isEmpty() && sSelectItemList.get(0).getIndex() == 1) {
            item.setIndex(item.getIndex() + 2);
        } else {
            item.setIndex(item.getIndex() + 1);
        }
        item.setSetSelected(true);
        sSelectItemList.add(item);
        notifySelectItemChange(item);
        return true;
    }

    public void removeSelectItemAndSetIndex(MediaData item) {
        item.setIndex(0);
        item.setSetSelected(false);

        if ("material".equals(item.getName())) {
            for (int i = 0; i < sSelectItemList.size(); i++) {
                if (!StringUtil.isEmpty(sSelectItemList.get(i).getPath())
                    && sSelectItemList.get(i).getPath().equals(item.getPath())) {
                    sSelectItemList.remove(i);
                    break;
                }
            }
        } else {
            sSelectItemList.remove(item);
        }
        clearMediaData(item);
        notifySelectItemChange(item);
    }

    public MediaData updateSelectMediaData(String path) {
        MediaData resultMediaData = null;
        for (MediaData selectItem : sSelectItemList) {
            if (selectItem.getPath().equals(path)) {
                resultMediaData = selectItem;
                break;
            }
        }
        return resultMediaData;
    }

    private void clearMediaData(MediaData mediaData) {
        mediaData.setGlLeftBottomX(0);
        mediaData.setGlLeftBottomY(0);
        mediaData.setGlRightTopX(0);
        mediaData.setGlRightTopY(0);
        mediaData.setScaleX(0);
        mediaData.setScaleY(0);
        mediaData.setMirrorStatus(false);
        mediaData.setHVEWidth(0);
        mediaData.setHVEHeight(0);
        mediaData.setCutTrimIn(0);
        mediaData.setCutTrimOut(0);
        mediaData.setRotation(0);
    }

    public void updateSortedIndex() {
        for (int i = 0; i < sSelectItemList.size(); i++) {
            MediaData mediaData = sSelectItemList.get(i);
            if (mediaData != null && (mediaData.getIndex() - 1) != i) {
                setNewIndexForSelectItem(mediaData, i + 1);
            }
        }
    }

    public void setNewIndexForSelectItem(MediaData item, int newIndex) {
        item.setIndex(newIndex);
        notifySelectItemChange(item);
    }

    public void setMaxSelectCount(int count) {
        if (count > 0) {
            maxSelectCount = count;
        }
    }

    public int getMaxSelectCount() {
        return maxSelectCount;
    }

    public void addOnSelectItemChangeListener(OnSelectItemChangeListener onSelectItemChangeListener) {
        if (!sOnSelectItemChangeListenerList.contains(onSelectItemChangeListener)) {
            sOnSelectItemChangeListenerList.add(onSelectItemChangeListener);
        }
    }

    public void clear() {
        maxSelectCount = Constant.MAX_PICK_NUM;
        sOnSelectItemChangeListenerList.clear();
        sSelectItemList.clear();
    }

    public void notifySelectItemChange(MediaData item) {
        for (OnSelectItemChangeListener listener : sOnSelectItemChangeListenerList) {
            listener.onSelectItemChange(item);
        }
    }

    public void setPreviewMediaData(MediaData item) {
        mediaData = item;
    }

    public void updatePreviewMediaData(MediaData item) {
        if (mediaData.isSetSelected()) {
            removeSelectItemAndSetIndex(mediaData);
            addSelectItemAndSetIndex(item);
        }
        mediaData = item;
    }

    public MediaData getMediaData() {
        return mediaData;
    }

    public interface OnSelectItemChangeListener {
        void onSelectItemChange(MediaData item);
    }

    private static class SingleTonHolder {
        private static final MediaPickManager INSTANCE = new MediaPickManager();
    }
}
