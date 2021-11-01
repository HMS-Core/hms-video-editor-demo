
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

package com.huawei.hms.videoeditor.ui.mediaeditor.sticker.viewmodel;

import static com.huawei.hms.videoeditor.ui.mediaeditor.sticker.repository.StickerRepository.addStickerAsset;

import java.util.List;

import android.content.Intent;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEImageAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEStickerAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVideoAsset;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.cover.CoverImageActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsRespository;
import com.huawei.hms.videoeditor.ui.mediaeditor.sticker.repository.StickerRepository;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StickerPanelViewModel extends ViewModel {

    private ColumnsRespository columnsRespository;

    private MutableLiveData<List<HVEColumnInfo>> columns = new MutableLiveData<>();

    private MutableLiveData<CloudMaterialBean> selectData = new MutableLiveData<>();

    private MutableLiveData<Boolean> removeData = new MutableLiveData<>();

    private MutableLiveData<Integer> errorType = new MutableLiveData<>();

    public StickerPanelViewModel() {
        columnsRespository = new ColumnsRespository();
        columnsRespository.seatColumnsListener(columnsListener);
    }

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return columns;
    }

    public MutableLiveData<CloudMaterialBean> getSelectData() {
        return selectData;
    }

    public MutableLiveData<Boolean> getRemoveData() {
        return removeData;
    }

    public MutableLiveData<Integer> getErrorType() {
        return errorType;
    }

    public void setSelectCutContent(CloudMaterialBean mCutContent) {
        selectData.postValue(mCutContent);
    }

    public void pickLocalMaterial(FragmentActivity activity) {
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        HVETimeLine timeLine = EditorManager.getInstance().getTimeLine();
        if (activity == null || videoLane == null || timeLine == null) {
            return;
        }

        Intent intent = new Intent(activity, CoverImageActivity.class);
        intent.putExtra("sticker", 1009);

        if (videoLane.getAssets().isEmpty()) {
            return;
        }

        HVEAsset asset = videoLane.getAssetByIndex(0);
        int width = 0;
        int height = 0;

        if (asset instanceof HVEVideoAsset) {
            width = ((HVEVideoAsset) asset).getWidth();
            height = ((HVEVideoAsset) asset).getHeight();
        } else if (asset instanceof HVEImageAsset) {
            width = ((HVEImageAsset) asset).getWidth();
            height = ((HVEImageAsset) asset).getHeight();
        } else {
            return;
        }
        intent.putExtra("width", width);
        intent.putExtra("height", height);
        activity.startActivityForResult(intent, VideoClipsActivity.ACTION_ADD_STICKER_REQUEST_CODE);
    }

    public boolean deleteStickerAsset(HVEAsset asset) {
        if (asset == null) {
            return false;
        }
        return StickerRepository.deleteAsset(asset);
    }

    public HVEStickerAsset replaceStickerAsset(HVEAsset asset, CloudMaterialBean content, long startTime) {
        if (content == null) {
            return null;
        }
        if (asset == null) {
            return addStickerAsset(content, startTime);
        }
        return StickerRepository.replaceStickerAsset(asset, content);
    }

    public void initColumns(String type) {
        columnsRespository.initColumns(type);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        columnsRespository = null;
        columnsListener = null;
    }

    private ColumnsListener columnsListener = new ColumnsListener() {
        @Override
        public void columsData(List<HVEColumnInfo> materialsCutContentList) {
            columns.postValue(materialsCutContentList);
        }

        @Override
        public void errorType(int type) {
            errorType.postValue(type);
        }
    };
}
