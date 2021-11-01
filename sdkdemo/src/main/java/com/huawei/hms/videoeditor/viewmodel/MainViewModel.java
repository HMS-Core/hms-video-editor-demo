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

package com.huawei.hms.videoeditor.viewmodel;

import java.util.List;

import android.app.Application;

import com.huawei.hms.videoeditor.sdk.HVEProject;
import com.huawei.hms.videoeditor.sdk.HVEProjectManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<HVEProject>> mDraftProjects = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void initDraftProjects() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<HVEProject> draftProjects = HVEProjectManager.getDraftProjects();
                mDraftProjects.postValue(draftProjects);
            }
        }.start();
    }

    public MutableLiveData<List<HVEProject>> getDraftProjects() {
        return mDraftProjects;
    }
}
