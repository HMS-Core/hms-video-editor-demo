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

package com.huawei.hms.videoeditor.ui.mediaeditor.audio.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.huawei.hms.videoeditor.ui.common.BaseActivity;
import com.huawei.hms.videoeditor.ui.common.bean.AudioData;
import com.huawei.hms.videoeditor.ui.common.bean.Constant;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.mediaeditor.audio.fragment.MusicLocalFragment;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class AudioPickActivity extends BaseActivity {

    public static final int MUSIC_LIBRARY_REQUEST_CODE = 1101;

    public static final String SELECT_NAME = "select_name";

    public static final String SELECT_PATH = "select_path";

    private ImageView mCloseIcon;

    private EditText mSearchEdit;

    private ImageView mDeleteIcon;

    private TextView mCancelTv;

    private TabLayout mTabLayout;

    private RecyclerView mSearchRecyclerview;

    private int contentId;

    private int currIndex = 0;

    private List<Fragment> fragments;

    private int[] mTabs = new int[] {/* R.string.music_library, */R.string.music_local/* , R.string.music_favorites */};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_pick);
        initView();
        initObject();
        initEvent();
    }

    private void initView() {
        contentId = R.id.fragment_content;
        mCloseIcon = findViewById(R.id.iv_back);
        mSearchEdit = findViewById(R.id.tv_search);
        mDeleteIcon = findViewById(R.id.delete_iv);
        mCancelTv = findViewById(R.id.cancel_tv);
        mTabLayout = findViewById(R.id.tab_layout);
        mSearchRecyclerview = findViewById(R.id.recycler_view);
    }

    private void initObject() {
        for (int mTab : mTabs) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTab));
        }
        MusicLocalFragment mLocalFragment = new MusicLocalFragment();
        fragments = new ArrayList<>();
        fragments.add(mLocalFragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(contentId, fragments.get(0));
        transaction.commit();
        currIndex = 0;
    }

    private void initEvent() {
        mCloseIcon.setOnClickListener(new OnClickRepeatedListener(v -> finish()));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
        mSearchEdit.setEnabled(false);
        mSearchEdit.setOnFocusChangeListener((v, hasFocus) -> {
            mCancelTv.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            mSearchRecyclerview.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
        });

        mCancelTv.setOnClickListener(new OnClickRepeatedListener(v -> mSearchEdit.clearFocus()));

        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDeleteIcon.setVisibility(StringUtil.isEmpty(s.toString()) ? View.GONE : View.VISIBLE);
            }
        });

        mDeleteIcon.setOnClickListener(new OnClickRepeatedListener(v -> mSearchEdit.setText("")));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectChanged(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == MUSIC_LIBRARY_REQUEST_CODE && resultCode == Constant.RESULT_CODE) {
                String name = data.getStringExtra(SELECT_NAME);
                String path = data.getStringExtra(SELECT_PATH);
                setChoiceResult(name, path);
            }
        }
    }

    public void setChoiceResult(String itemName, String itemPath) {
        if (TextUtils.isEmpty(itemName)) {
            return;
        }
        if (TextUtils.isEmpty(itemPath)) {
            return;
        }
        Intent intent = new Intent();
        AudioData audioData = new AudioData();
        audioData.setName(itemName);
        audioData.setPath(itemPath);
        intent.putExtra(Constant.EXTRA_AUDIO_SELECT_RESULT, audioData);
        setResult(Constant.RESULT_CODE, intent);
        finish();
    }

    public void selectChanged(int index) {
        Fragment fragment = fragments.get(index);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getCurrentFragment().onPause();
        if (fragment.isAdded()) {
            fragment.onResume();
        } else {
            ft.add(contentId, fragment);
        }
        showTab(index);
        ft.commit();
    }

    protected void showTab(int index) {
        for (int k = 0; k < fragments.size(); k++) {
            Fragment fragmentValue = fragments.get(k);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (index == k) {
                fragmentTransaction.show(fragmentValue);
            } else {
                fragmentTransaction.hide(fragmentValue);
            }
            fragmentTransaction.commit();
        }
        currIndex = index;
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currIndex);
    }
}
