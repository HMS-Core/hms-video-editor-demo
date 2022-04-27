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

import static com.huawei.hms.videoeditor.ui.template.module.TemplateHomeFragment.SOURCE_KEY;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.huawei.hms.videoeditor.fragment.ClipFragment;
import com.huawei.hms.videoeditor.sdk.MediaApplication;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.mediaeditor.menu.MenuConfig;
import com.huawei.hms.videoeditor.ui.template.module.TemplateHomeFragment;
import com.huawei.hms.videoeditor.utils.SmartLog;
import com.huawei.hms.videoeditor.view.NoScrollViewPager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainActivity extends BaseActivity {
    private final String[] mPermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET};

    private NoScrollViewPager viewPager;

    public static final String SOURCE = "source";

    private List<Fragment> mFragments = new ArrayList<>();

    private TabLayout tabLayout;

    private static int initIndex = 0;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        statusBarColor = R.color.home_color_FF181818;
        navigationBarColor = R.color.home_color_FF181818;
        super.onCreate(savedInstanceState);
        VideoEditorApplication.getInstance().setContext(this);
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
        tabLayout = findViewById(R.id.home_tabLayout);
    }

    private void initData() {
        MediaApplication.getInstance().setApiKey("please set your apikey");

        UUID uuid = UUID.randomUUID();
        MediaApplication.getInstance().setLicenseId(uuid.toString());

        int[] tabItemImage = new int[] {R.drawable.home_tab_clip_selector, R.drawable.home_tab_draft_selector};
        String[] tabItemText = new String[] {getString(R.string.first_menu_cut), getString(R.string.home_tab1)};

        TemplateHomeFragment templateFragment = new TemplateHomeFragment();
        Bundle bundle = new Bundle();
        SafeIntent safeIntent = new SafeIntent(getIntent());
        String source = safeIntent.getStringExtra(SOURCE);
        bundle.putString(SOURCE_KEY, source);
        templateFragment.setArguments(bundle);
        mFragments.add(new ClipFragment());
        mFragments.add(templateFragment);
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
                        mFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();
                    }
                    return fragment;
                }

                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                    Fragment fragment = mFragments.get(position);
                    if (!mFragmentManager.isDestroyed()) {
                        mFragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss();
                    }
                }
            };

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(initIndex);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < mFragments.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.activity_main_tab_item, null, false);
            ImageView imageMainTabItem = view.findViewById(R.id.image_main_tab_item);
            TextView textMainTabItem = view.findViewById(R.id.text_main_tab_item);

            imageMainTabItem.setImageResource(tabItemImage[i]);
            textMainTabItem.setText(tabItemText[i]);

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(view);
            }
        }

        TabLayout.Tab tab = tabLayout.getTabAt(initIndex);
        if (tab != null) {
            tab.select();
        }
        tabLayout.setVisibility(View.VISIBLE);
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