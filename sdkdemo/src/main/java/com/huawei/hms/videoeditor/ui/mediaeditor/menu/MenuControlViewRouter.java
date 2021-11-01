
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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import java.util.Stack;

import com.huawei.hms.videoeditor.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.VideoClipsActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MenuControlViewRouter {
    private final FragmentManager mFragmentManager;

    private final int mFragmentContainerId;

    private EditMenuContentLayout mEditMenuContainer = null;

    private final Stack<Panel> mViewStack = new Stack<>();

    private long firstTime = 0;

    private static final long INTERVAL = 1000;

    private int lastId = -1;

    private MenuViewModel menuViewModel;

    public MenuControlViewRouter(VideoClipsActivity activity, int id, EditMenuContentLayout view2,
        MenuViewModel menuViewModel) {
        this.mFragmentContainerId = id;
        this.mEditMenuContainer = view2;
        this.mFragmentManager = activity.getSupportFragmentManager();
        this.menuViewModel = menuViewModel;
    }

    public void updateMenuViewModel(MenuViewModel menuViewModel) {
        this.menuViewModel = menuViewModel;
    }

    public void showFragment(int id, Fragment fragment) {
        menuViewModel.isShowMenuPanel.postValue(true);
        long lastTime = System.currentTimeMillis();
        if (lastId == id && lastTime - firstTime <= INTERVAL) {
            return;
        }
        mViewStack.push(new Panel(id, fragment));
        if (mFragmentManager != null && !mFragmentManager.isDestroyed()) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(mFragmentContainerId, fragment);
            transaction.commitAllowingStateLoss();
        }
        firstTime = lastTime;
        lastId = id;
    }

    private void removeFragment(Fragment fragment) {
        if (mFragmentManager == null || mFragmentManager.isDestroyed()) {
            return;
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }

    private void removeOperateItem() {
        mEditMenuContainer.hideOperateMenu();
    }

    public boolean popView() {
        if (mViewStack.isEmpty()) {
            if (mEditMenuContainer.isOperateShow()) {
                removeOperateItem();
                return true;
            }
            return false;
        }
        Panel panel = mViewStack.pop();
        if (panel.object instanceof BaseFragment) {
            ((BaseFragment) panel.object).onBackPressed();
            removeFragment((Fragment) panel.object);
            if (mViewStack.empty()) {
                menuViewModel.isShowMenuPanel.postValue(false);
            }
        }

        return true;
    }

    public boolean removeStackTopFragment() {
        if (mViewStack.isEmpty()) {
            return false;
        }
        Panel panel = mViewStack.pop();
        if (panel.object instanceof BaseFragment) {
            ((BaseFragment) panel.object).onBackPressed();
            removeFragment((Fragment) panel.object);
        }

        return true;
    }

    public Stack<Panel> getViewStack() {
        return mViewStack;
    }

    public static final class Panel {

        public Panel(int id, Object object) {
            this.object = object;
        }

        public int id;

        public Object object;

    }
}
