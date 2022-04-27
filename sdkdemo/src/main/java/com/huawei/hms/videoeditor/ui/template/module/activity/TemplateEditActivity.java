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

import android.content.Intent;
import android.os.Bundle;

import com.huawei.hms.videoeditor.ui.common.view.navigator.FixFragmentNavigator;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.utils.ModuleSelectManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.NavHostFragment;

public class TemplateEditActivity extends BaseActivity {
    public static final int REQUEST_EDIT_CODE = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        navigationBarColor = R.color.black;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_edit_t);
        initObject();
    }

    private void initObject() {
        SafeIntent intent = new SafeIntent(getIntent());
        float mTextureViewWidth = intent.getFloatExtra(TemplateDetailActivity.TEXTURE_VIEW_WIDTH, 0);
        float mTextureViewHeight = intent.getFloatExtra(TemplateDetailActivity.TEXTURE_VIEW_HEIGHT, 0);
        String templateinfo = intent.getStringExtra(Constant.TEMPLATE_KEY_INFO);
        String templatedetail = intent.getStringExtra(Constant.TEMPLATE_KEY_DETAIL);
        String source = intent.getStringExtra("source");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_home);

        if (fragment != null) {
            NavController navController = NavHostFragment.findNavController(fragment);
            NavigatorProvider provider = navController.getNavigatorProvider();
            FixFragmentNavigator fragmentNavigator =
                    new FixFragmentNavigator(this, fragment.getChildFragmentManager(), fragment.getId());
            provider.addNavigator(fragmentNavigator);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.TEMPLATE_KEY_INFO, templateinfo);
            bundle.putString(Constant.TEMPLATE_KEY_DETAIL, templatedetail);
            bundle.putString("source", source);
            bundle.putFloat(TemplateDetailActivity.TEXTURE_VIEW_WIDTH, mTextureViewWidth);
            bundle.putFloat(TemplateDetailActivity.TEXTURE_VIEW_HEIGHT, mTextureViewHeight);
            navController.setGraph(R.navigation.nav_graph_template_edit, bundle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_CODE && resultCode == Constant.RESULT_CODE) {
            ModuleSelectManager.getInstance().destroy();
            finish();
        } else if (requestCode == REQUEST_EDIT_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}