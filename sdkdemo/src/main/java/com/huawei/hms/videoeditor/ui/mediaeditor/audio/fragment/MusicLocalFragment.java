
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

package com.huawei.hms.videoeditor.ui.mediaeditor.audio.fragment;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.videoeditor.ui.common.LazyFragment;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MusicLocalFragment extends LazyFragment {

    private LinearLayout mLocalLayout;

    private LinearLayout mFetchLayout;

    private LinearLayout mLinkLayout;

    private ImageView mLocalIv;

    private ImageView mFetchIv;

    private ImageView mLinkIv;

    private TextView mLocalTv;

    private TextView mFetchTv;

    private TextView mLinkTv;

    private int contentId;

    private int currIndex = 0;

    private List<Fragment> fragments;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_audio_music_local;
    }

    @Override
    protected void initView(View view) {
        mLocalLayout = view.findViewById(R.id.local_layout);
        mFetchLayout = view.findViewById(R.id.fetch_layout);
        mLinkLayout = view.findViewById(R.id.link_layout);
        mLocalIv = view.findViewById(R.id.local_icon);
        mFetchIv = view.findViewById(R.id.fetch_icon);
        mLinkIv = view.findViewById(R.id.link_icon);
        mLocalTv = view.findViewById(R.id.local_tv);
        mFetchTv = view.findViewById(R.id.fetch_tv);
        mLinkTv = view.findViewById(R.id.link_tv);
    }

    @Override
    protected void initObject() {
        contentId = R.id.fragment_content;

        LocalLocalMusicFragment mLocalLocalMusicFragment = new LocalLocalMusicFragment();
        LocalLinkMusicFragment mLocalLinkMusicFragment = new LocalLinkMusicFragment();
        fragments = new ArrayList<>();
        fragments.add(mLocalLocalMusicFragment);
        fragments.add(mLocalLinkMusicFragment);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(contentId, fragments.get(0));
        transaction.commit();
        currIndex = 0;
    }

    @Override
    protected void initEvent() {
        mLocalLayout.setOnClickListener(v -> {
            if (currIndex != 0) {
                selectChanged(0);
                resetView();
            }
        });
        mFetchLayout.setOnClickListener(v -> {
            if (currIndex != 1) {
                selectChanged(1);
                resetView();
            }
        });
        mLinkLayout.setOnClickListener(v -> {
            if (currIndex != 2) {
                selectChanged(2);
                resetView();
            }
        });
    }

    private void resetView() {
        switch (currIndex) {
            case 0:
                mLocalIv.setImageResource(R.drawable.music_local_select);
                mFetchIv.setImageResource(R.drawable.music_fetch_normal);
                mLinkIv.setImageResource(R.drawable.music_link_normal);
                mLocalTv.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                mFetchTv.setTextColor(ContextCompat.getColor(mContext, R.color.local_music_normal_color));
                mLinkTv.setTextColor(ContextCompat.getColor(mContext, R.color.local_music_normal_color));
                break;
            case 1:
                mLocalIv.setImageResource(R.drawable.music_local_normal);
                mFetchIv.setImageResource(R.drawable.music_fetch_select);
                mLinkIv.setImageResource(R.drawable.music_link_normal);
                mLocalTv.setTextColor(ContextCompat.getColor(mContext, R.color.local_music_normal_color));
                mFetchTv.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                mLinkTv.setTextColor(ContextCompat.getColor(mContext, R.color.local_music_normal_color));
                break;
            case 2:
                mLocalIv.setImageResource(R.drawable.music_local_normal);
                mFetchIv.setImageResource(R.drawable.music_fetch_normal);
                mLinkIv.setImageResource(R.drawable.music_link_select);
                mLocalTv.setTextColor(ContextCompat.getColor(mContext, R.color.local_music_normal_color));
                mFetchTv.setTextColor(ContextCompat.getColor(mContext, R.color.local_music_normal_color));
                mLinkTv.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                break;
            default:
                break;
        }
    }

    public void selectChanged(int index) {
        Fragment fragment = fragments.get(index);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        getCurrentFragment().onPause();
        if (fragment.isAdded()) {
            fragment.onResume();
        } else {
            ft.add(contentId, fragment);
        }
        showTab(index);
        ft.commit();
    }

    protected void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        currIndex = idx;
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currIndex);
    }
}
