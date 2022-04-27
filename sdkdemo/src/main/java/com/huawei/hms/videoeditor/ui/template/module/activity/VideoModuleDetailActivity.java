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

package com.huawei.hms.videoeditor.ui.template.module.activity;

import java.util.List;

import android.os.Bundle;

import com.huawei.hms.videoeditor.ui.common.utils.GsonUtils;
import com.huawei.hms.videoeditor.ui.common.view.navigator.FixFragmentNavigator;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.bean.MaterialData;
import com.huawei.hms.videoeditor.ui.template.bean.TemplateProjectBean;
import com.huawei.hms.videoeditor.ui.template.viewmodel.ModuleEditViewModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.NavHostFragment;

public class VideoModuleDetailActivity extends BaseActivity {
    private String mName;

    private String mDescription;

    private ModuleEditViewModel mModuleEditViewModel;

    private TemplateProjectBean templateProjectBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_detail);
        SafeIntent intent = new SafeIntent(getIntent());
        String templatedetail = intent.getStringExtra(Constant.TEMPLATE_KEY_DETAIL);
        templateProjectBean = GsonUtils.fromJson(templatedetail, TemplateProjectBean.class);
        initView(intent, templatedetail);
        initObject(intent);
    }

    private void initView(SafeIntent safeIntent, String templatedetail) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_module_detail);
        if (fragment != null) {
            NavController navController = NavHostFragment.findNavController(fragment);
            NavigatorProvider provider = navController.getNavigatorProvider();
            FixFragmentNavigator fragmentNavigator =
                new FixFragmentNavigator(this, fragment.getChildFragmentManager(), fragment.getId());
            provider.addNavigator(fragmentNavigator);

            Bundle bundle = safeIntent.getExtras();
            bundle.putString(Constant.TEMPLATE_KEY_DETAIL, templatedetail);
            navController.setGraph(R.navigation.nav_graph_video_module_detail_t, bundle);
        }
    }

    private void initObject(SafeIntent intent) {
        mModuleEditViewModel = new ViewModelProvider(this, mFactory).get(ModuleEditViewModel.class);
        mName = intent.getStringExtra(TemplateDetailActivity.NAME);
        mDescription = intent.getStringExtra(TemplateDetailActivity.DESCRIPTION);

        List<MaterialData> mMaterialDataList = intent.getParcelableArrayListExtra(Constant.EXTRA_MODULE_SELECT_RESULT);
        if (mMaterialDataList != null) {
            mModuleEditViewModel.initData(mMaterialDataList);

            if (templateProjectBean != null) {
                mModuleEditViewModel.updateDataProject(templateProjectBean);
            }
        }
    }

    public String getName() {
        return mName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mModuleEditViewModel.getEditor() != null) {
            mModuleEditViewModel.getEditor().stopRenderer();
            mModuleEditViewModel.getEditor().stopEditor();
            mModuleEditViewModel.setEditor(null);
        }
    }
}