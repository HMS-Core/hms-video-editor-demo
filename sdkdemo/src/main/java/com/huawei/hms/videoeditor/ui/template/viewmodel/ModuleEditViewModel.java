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

package com.huawei.hms.videoeditor.ui.template.viewmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;
import com.huawei.hms.videoeditor.ui.template.bean.TemplateProjectBean;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ModuleEditViewModel extends AndroidViewModel {
    private static final String TAG = "ModuleEditViewModel";

    private List<MaterialData> mMaterialDataList;

    private HuaweiVideoEditor mEditor;

    private MutableLiveData<List<MaterialData>> mMaterialDataLiveData = new MutableLiveData<>();

    private MutableLiveData<Integer> mReplaceLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mCoverPath = new MutableLiveData<>();

    private MutableLiveData<TemplateProjectBean> mTemplateResource = new MutableLiveData<>();

    public ModuleEditViewModel(@NonNull Application application) {
        super(application);
    }

    public void setEditor(HuaweiVideoEditor editor) {
        this.mEditor = editor;
    }

    public HuaweiVideoEditor getEditor() {
        return mEditor;
    }

    public void updateDataProject(TemplateProjectBean templateResource) {
        mTemplateResource.postValue(templateResource);
    }

    public void initData(List<MaterialData> list) {
        if (mMaterialDataList == null) {
            mMaterialDataList = new ArrayList<>();
        }
        mMaterialDataList.addAll(list);
        mMaterialDataLiveData.postValue(mMaterialDataList);
    }

    public void updateMaterialList(List<MaterialData> list) {
        mMaterialDataList = list;
    }

    public List<MaterialData> getInitData() {
        return mMaterialDataList;
    }

    public void setMaterialData(int position, MaterialData materialData) {
        this.mMaterialDataList.set(position, materialData);
        mReplaceLiveData.postValue(position);
    }

    public MutableLiveData<List<MaterialData>> getMaterialDataListLiveData() {
        return mMaterialDataLiveData;
    }

    public MutableLiveData<Integer> getReplacePositionLiveData() {
        return mReplaceLiveData;
    }

    public MutableLiveData<TemplateProjectBean> getTemplateResourceLiveData() {
        return mTemplateResource;
    }

    public MutableLiveData<String> getCoverPath() {
        return mCoverPath;
    }

    public void initCoverPath(long timeStamp, HuaweiVideoEditor mEditor, Context context, String bitmapName,
        OnCoverCallback callback) {
        if (mEditor == null || mEditor.getTimeLine() == null) {
            SmartLog.e(TAG, "input mEditor or timeline is null.");
            return;
        }
        if (context == null) {
            SmartLog.e(TAG, "input context is null.");
            return;
        }
        if (timeStamp > mEditor.getTimeLine().getEndTime() || timeStamp < 0) {
            SmartLog.w(TAG, "input time is small than timeline or is less than zero.");
            timeStamp = 0;
        }
        long finalTimeStamp = timeStamp;
        mEditor.seekTimeLine(0, new HuaweiVideoEditor.SeekCallback() {
            @Override
            public void onSeekFinished() {
                SmartLog.e("HuaweiVideoEditor", "seekSuccess" + bitmapName);
                mEditor.getBitmapAtSelectedTime(finalTimeStamp, new HuaweiVideoEditor.ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, long time) {
                        try {
                            String imagePath = FileUtil.saveBitmap(context, bitmap, bitmapName);
                            SmartLog.e(TAG, "save cover image success. " + bitmapName);
                            mCoverPath.postValue(imagePath);
                            if (mEditor != null && mEditor.getTimeLine() != null) {
                                mEditor.getTimeLine().addCoverImage(imagePath);
                            }
                        } catch (IOException e) {
                            SmartLog.e(TAG, "save cover image failed.");
                        }
                        if (callback != null) {
                            callback.onCoverSuccess();
                        }
                    }

                    @Override
                    public void onFail(int errorCode) {
                        SmartLog.e(TAG, "save cover image failed. errorCode is: " + errorCode);
                        if (callback != null) {
                            callback.onCoverFail();
                        }
                    }
                });
            }
        });
    }

    public interface OnCoverCallback {
        void onCoverSuccess();

        void onCoverFail();
    }
}
