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

package com.huawei.hms.videoeditor.ui.mediaeditor.blockface;

import static com.huawei.hms.videoeditor.sdk.materials.network.response.MaterialsCutContentType.CUSTOM_STICKERS;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEVisibleAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEAIFaceTemplate;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;
import com.huawei.hms.videoeditor.ui.common.bean.CloudMaterialBean;
import com.huawei.hms.videoeditor.ui.common.database.CloudMaterialsDataManager;
import com.huawei.hms.videoeditor.sdk.store.database.DBManager;
import com.huawei.hms.videoeditor.ui.common.database.bean.CloudMaterialDaoBean;
import com.huawei.hms.videoeditor.ui.common.database.bean.CloudMaterialsBeanDao;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditor.ui.common.utils.SPGuideUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsListener;
import com.huawei.hms.videoeditor.ui.mediaeditor.repository.ColumnsRespository;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FaceBlockingViewModel extends AndroidViewModel {
    private static final String TAG = FaceBlockingViewModel.class.getSimpleName();

    private ColumnsRespository columnsRespository;

    private MutableLiveData<List<HVEColumnInfo>> columns = new MutableLiveData<>();

    private MutableLiveData<List<HVEAIFaceTemplate>> faceBlockingList = new MutableLiveData<>();

    private MutableLiveData<Integer> detectedProgress = new MutableLiveData<>();

    private MutableLiveData<Boolean> isDetectedSuccess = new MutableLiveData<>();

    private MutableLiveData<Integer> errorType = new MutableLiveData<>();

    private final CloudMaterialsDataManager mLocalDataManager;

    private long mVideoStartTime;

    private long mVideoEndTime;

    private boolean isFaceVideoCutFragmentShow;

    public FaceBlockingViewModel(@NonNull Application application) {
        super(application);
        mLocalDataManager = new CloudMaterialsDataManager(application.getBaseContext());
        columnsRespository = new ColumnsRespository();
        columnsRespository.seatColumnsListener(columnsListener);
    }

    public MutableLiveData<List<HVEColumnInfo>> getColumns() {
        return columns;
    }

    public MutableLiveData<Integer> getErrorType() {
        return errorType;
    }

    public MutableLiveData<List<HVEAIFaceTemplate>> getFaceBlockingList() {
        return faceBlockingList;
    }

    public void setFaceBlockingList(List<HVEAIFaceTemplate> list) {
        this.faceBlockingList.postValue(list);
    }

    public long getVideoStartTime() {
        return mVideoStartTime;
    }

    public void setVideoStartTime(long mVideoStartTime) {
        this.mVideoStartTime = mVideoStartTime;
    }

    public long getVideoEndTime() {
        return mVideoEndTime;
    }

    public void setVideoEndTime(long mVideoEndTime) {
        this.mVideoEndTime = mVideoEndTime;
    }

    public boolean isFaceVideoCutFragmentShow() {
        return isFaceVideoCutFragmentShow;
    }

    public void setFaceVideoCutFragmentShow(boolean faceVideoCutFragmentShow) {
        isFaceVideoCutFragmentShow = faceVideoCutFragmentShow;
    }

    public MutableLiveData<Integer> getDetectedProgress() {
        return detectedProgress;
    }

    public void setDetectedProgress(int detectedProgress) {
        this.detectedProgress.postValue(detectedProgress);
    }

    public MutableLiveData<Boolean> getIsDetectedSuccess() {
        return isDetectedSuccess;
    }

    public void setIsDetectedSuccess(boolean isDetectedSuccess) {
        this.isDetectedSuccess.postValue(isDetectedSuccess);
    }

    public CloudMaterialBean addStickerCustomToLocal(String stickerId, String path) {
        if (TextUtils.isEmpty(stickerId)) {
            SmartLog.e(TAG, "stickerId is null!");
            return null;
        } else {
            CloudMaterialBean cloudMaterialBean = new CloudMaterialBean();
            cloudMaterialBean.setType(CUSTOM_STICKERS);
            cloudMaterialBean.setId(stickerId);
            cloudMaterialBean
                .setName(getApplication().getResources().getString(R.string.first_menu_sticker) + "_" + stickerId);
            cloudMaterialBean.setCategoryName(
                getApplication().getResources().getString(R.string.first_menu_sticker) + "_" + stickerId);
            cloudMaterialBean.setLocalPath(path);
            boolean result = mLocalDataManager.updateCloudMaterialsBean(cloudMaterialBean);
            if (result) {
                return cloudMaterialBean;
            } else {
                return null;
            }
        }
    }

    public List<CloudMaterialBean> getLocalMaterialsDataByType(int type) {
        if (type == CUSTOM_STICKERS) {
            List<CloudMaterialBean> cutContents = mLocalDataManager.queryCloudMaterialsBeanByType(type);

            Collections.reverse(cutContents);
            return cutContents;
        } else {
            return new ArrayList<>();
        }
    }

    public boolean deleteLocalCustomSticker(CloudMaterialBean item) {
        String contentId = item.getId();
        if (contentId != null) {
            if (TextUtils.isEmpty(contentId)) {
                SmartLog.e(TAG, "deleteMaterialsCutContent fail because contentId is empty");
                return false;
            }
            List<WhereCondition> whereConditionList = new ArrayList<>();
            whereConditionList.add(CloudMaterialsBeanDao.Properties.ID.eq(contentId));
            List<CloudMaterialDaoBean> beanListTemp =
                DBManager.getInstance().queryByCondition(CloudMaterialDaoBean.class, whereConditionList);
            if (beanListTemp != null && beanListTemp.size() > 0) {
                DBManager.getInstance().delete(beanListTemp.get(0));
                return true;
            }
        }
        return false;
    }

    public long[] getLastDetectedTime() {
        String nowLaneHashCode = getLaneHashCode();
        String projectId = EditorManager.getInstance().getEditor().getProjectId();
        String oldLaneHashCode = SPGuideUtils.getInstance().getFaceLaneHash(getApplication(), projectId);
        if (!oldLaneHashCode.equals(nowLaneHashCode)) {
            return new long[0];
        }

        return SPGuideUtils.getInstance().getFaceDetectedTimes(getApplication(), projectId);
    }

    public void saveDetectedTime(long startTime, long endTime) {
        String projectId = EditorManager.getInstance().getEditor().getProjectId();
        String laneHashCode = getLaneHashCode();
        if (StringUtil.isEmpty(laneHashCode)) {
            return;
        }
        SPGuideUtils.getInstance().saveFaceDetectedTimes(getApplication(), startTime, endTime, projectId, laneHashCode);
    }

    private String getLaneHashCode() {
        StringBuilder hashCode = new StringBuilder();
        HVEVideoLane videoLane = EditorManager.getInstance().getMainLane();
        if (videoLane == null) {
            return hashCode.toString();
        }
        for (HVEAsset asset : videoLane.getAssets()) {
            hashCode.append(asset.hashCode());
        }
        return hashCode.toString();
    }

    public void interruptFaceBlocking(HVEAsset hveAsset) {
        if (hveAsset instanceof HVEVisibleAsset) {
            ((HVEVisibleAsset) hveAsset).interruptFacePrivacyDetect();
        }
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
