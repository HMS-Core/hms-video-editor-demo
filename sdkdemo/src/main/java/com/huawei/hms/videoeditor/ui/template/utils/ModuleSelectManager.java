/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hms.videoeditor.ui.template.utils;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.bean.MediaData;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;

public class ModuleSelectManager {
    private static List<MediaData> sSelectItemList;

    private static List<MaterialData> sInitItemList;

    private int mCurrentIndex = 0;

    private static List<OnSelectItemChangeListener> sOnSelectItemChangeListenerList = new ArrayList<>();

    private OnSelectItemDeleteListener itemVideoDeleteListener;

    private OnSelectItemDeleteListener itemPictureDeleteListener;

    public static ModuleSelectManager getInstance() {
        if (sSelectItemList == null) {
            sSelectItemList = new ArrayList<>();
        }
        if (sOnSelectItemChangeListenerList == null) {
            sOnSelectItemChangeListenerList = new ArrayList<>();
        }
        if (sInitItemList == null) {
            sInitItemList = new ArrayList<>();
        }
        return SingleTonHolder.INSTANCE;
    }

    public void initMaterials(List<MaterialData> list) {
        sInitItemList.clear();
        sInitItemList.addAll(list);
    }

    public List<MaterialData> getInitItemList() {
        return sInitItemList;
    }

    public List<MediaData> getSelectItemList() {
        return sSelectItemList;
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public long getCurrentDuration() {
        if (canNextStep()) {
            return 0;
        }
        if (mCurrentIndex >= sInitItemList.size()) {
            return 0;
        }
        return sInitItemList.get(mCurrentIndex).getValidDuration();
    }

    public void addSelectItemAndSetIndex(MediaData item) {
        if (mCurrentIndex >= sInitItemList.size()) {
            return;
        }
        int index = item.getIndex();
        sSelectItemList.add(item);
        MaterialData materialData = sInitItemList.get(mCurrentIndex);
        materialData.setMimeType(item.getMimeType());
        materialData.setDuration(item.getDuration());
        materialData.setPath(item.getPath());
        materialData.setSize(item.getSize());
        materialData.setUri(item.getUri());
        materialData.setWidth(item.getWidth());
        materialData.setHeight(item.getHeight());
        materialData.setIndex(index);
        sInitItemList.set(mCurrentIndex, materialData);
        resetCurrentIndex();
        notifySelectItemChange(materialData);
    }

    public void resetCurrentIndex() {
        int index = -1;
        for (int i = 0; i < sInitItemList.size(); i++) {
            if (index == -1 && TextUtils.isEmpty(sInitItemList.get(i).getPath())) {
                mCurrentIndex = i;
                index = i;
            }
        }
    }

    public void removeSelectItemAndSetIndex(int position) {
        if (position >= sInitItemList.size()) {
            SmartLog.w(ModuleSelectManager.class.getSimpleName(), "IndexOutOfBoundsException");
            return;
        }

        MaterialData item = sInitItemList.get(position);
        item.setIndex(0);
        int index = getSelectListIndex(item.getPath());
        if (index >= 0 && index < sSelectItemList.size()) {
            removeMediaList(index);
        }
        item.setPath("");
        sInitItemList.set(position, item);
        resetCurrentIndex();
        notifySelectItemChange(item);
    }

    private int getSelectListIndex(String path) {
        int position = -1;
        for (int i = 0; i < sSelectItemList.size(); i++) {
            if (path.equals(sSelectItemList.get(i).getPath())) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void removeMediaList(int selectIndex) {
        MediaData item = sSelectItemList.get(selectIndex);
        int index = item.getIndex();
        item.setIndex(Math.max(index - 1, 0));
        if (item.getIndex() == 0) {
            if (item.isVideo() && itemVideoDeleteListener != null) {
                itemVideoDeleteListener.onSelectItemDelete(sSelectItemList.get(selectIndex));
            } else {
                if (itemPictureDeleteListener != null) {
                    itemPictureDeleteListener.onSelectItemDelete(sSelectItemList.get(selectIndex));
                }
            }
            sSelectItemList.remove(selectIndex);
        }
    }

    public boolean canNextStep() {
        boolean flag = true;
        for (int i = 0; i < sInitItemList.size(); i++) {
            if (TextUtils.isEmpty(sInitItemList.get(i).getPath())) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public void addOnSelectItemChangeListener(OnSelectItemChangeListener onSelectItemChangeListener) {
        if (!sOnSelectItemChangeListenerList.contains(onSelectItemChangeListener)) {
            sOnSelectItemChangeListenerList.add(onSelectItemChangeListener);
        }
    }

    public void destroy() {
        mCurrentIndex = 0;

        sOnSelectItemChangeListenerList.clear();
        for (MediaData item : sSelectItemList) {
            item.setIndex(0);
            item.setPosition(0);
        }
        sSelectItemList.clear();
        for (MaterialData item : sInitItemList) {
            item.setIndex(0);
            item.setPath("");
            item.setMimeType("");
            item.setDuration(0);
            item.setSize(0);
            item.setUri(null);
            item.setWidth(0);
            item.setHeight(0);
        }
        sInitItemList.clear();
    }

    private void notifySelectItemChange(MaterialData item) {
        for (OnSelectItemChangeListener listener : sOnSelectItemChangeListenerList) {
            listener.onSelectItemChange(item);
        }
    }

    public interface OnSelectItemChangeListener {
        void onSelectItemChange(MaterialData item);
    }

    public void setVideoItemDeleteListener(OnSelectItemDeleteListener itemDeleteListener) {
        this.itemVideoDeleteListener = itemDeleteListener;
    }

    public void setPictureItemDeleteListener(OnSelectItemDeleteListener itemDeleteListener) {
        this.itemPictureDeleteListener = itemDeleteListener;
    }

    public interface OnSelectItemDeleteListener {
        void onSelectItemDelete(MediaData item);
    }

    private static class SingleTonHolder {
        private static final ModuleSelectManager INSTANCE = new ModuleSelectManager();
    }
}
