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

package com.huawei.hms.videoeditor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.huawei.hms.videoeditor.fragment.ClipFragment;
import com.huawei.hms.videoeditor.sdk.MediaApplication;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuConfig;
import com.huawei.hms.videoeditor.utils.SmartLog;
import com.huawei.hms.videoeditor.view.NoScrollViewPager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends BaseActivity {
    private final String[] mPermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET};

    private NoScrollViewPager viewPager;

    private List<Fragment> mFragments = new ArrayList<>();

    private static int initIndex = 0;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        statusBarColor = R.color.home_color_FF181818;
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        MenuConfig.getInstance().initMenuConfig(this);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        viewPager = (NoScrollViewPager) findViewById(R.id.home_viewPager);
    }

    private void initData() {
        MediaApplication.getInstance()
            .setApiKey("please set your apikey");

        UUID uuid = UUID.randomUUID();
        MediaApplication.getInstance().setLicenseId(uuid.toString());

        mFragments.add(new ClipFragment());

        FragmentPagerAdapter mAdapter =
            new FragmentPagerAdapter(mFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    return mFragments.get(position);
                }

                @Override
                public int getCount() {
                    return mFragments.size();
                }

                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    Fragment fragment = (Fragment) super.instantiateItem(container, position);
                    if (!mFragmentManager.isDestroyed()) {
                        mFragmentManager.beginTransaction().show(fragment).commit();
                    }
                    return fragment;
                }

                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                    Fragment fragment = mFragments.get(position);
                    if (!mFragmentManager.isDestroyed()) {
                        mFragmentManager.beginTransaction().hide(fragment).commit();
                    }
                }
            };

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(initIndex);
    }

    @Override
    protected void onPause() {
        super.onPause();
        initIndex = 0;
    }

    private void initEvent() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initIndex = 0;
    }

    private void verifyStoragePermissions(MainActivity activity) {
        final int REQUEST_EXTERNAL_STORAGE = 1;

        try {
            int permissionRead =
                ActivityCompat.checkSelfPermission(activity, "android.permission.READ_EXTERNAL_STORAGE");
            if (permissionRead != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, mPermissions, REQUEST_EXTERNAL_STORAGE);
            }

            int permissionWrite =
                ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, mPermissions, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            SmartLog.e("MainActivity", e.getMessage());
        }
    }
}