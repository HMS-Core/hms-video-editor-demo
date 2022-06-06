
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

package com.huawei.hms.videoeditor.ui.mediaeditor.menu;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.utils.LocalResourceUtil;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.StringUtil;
import com.huawei.hms.videoeditor.ui.common.view.decoration.HorizontalDividerDecoration;
import com.huawei.hms.videoeditor.ui.common.view.tab.bottom.TabBottomInfo;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditMenuContentLayout extends LinearLayout {

    private static final String TAG = "EditMenuContentLayout";

    private EditMenuFirstLayout mMenuFirstLayout;

    private RecyclerView mMenuSecondRecyclerView;

    private LinearLayout mMenuOperateLayout;

    private LinearLayout mMenuOperateBack;

    private RecyclerView mMenuOperateRecyclerView;

    private List<EditMenuBean> mFirstMenus;

    private List<EditMenuBean> mSecondMenus;

    private List<EditMenuBean> mOperateMenus;

    private List<Integer> mUnVisibleIds;

    private MenuAdapter mMenuSecondAdapter;

    private MenuAdapter mMenuOperateAdapter;

    private boolean isOperateShow = false;

    private int mCurrentFirstIndex = -1;

    private OnMenuClickListener onMenuClickListener;

    private List<TabBottomInfo<?>> mInfoList;

    private long mSecondClickTime = 0;

    private long mOperateClickTime = 0;

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    public EditMenuContentLayout(Context context) {
        this(context, null);
    }

    public EditMenuContentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditMenuContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMenuLayout();
        initMenuObject();
        initMenuEvent();
    }

    private void initMenuLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.edit_menu_content_layout, this);
        mMenuFirstLayout = findViewById(R.id.first_menu_view);
        mMenuSecondRecyclerView = findViewById(R.id.second_menu_recyclerview);
        mMenuOperateLayout = findViewById(R.id.operate_layout);
        mMenuOperateBack = findViewById(R.id.layout_back_operate);
        mMenuOperateRecyclerView = findViewById(R.id.operate_menu_recyclerview);
    }

    private void initMenuObject() {
        mInfoList = new ArrayList<>();
        mFirstMenus = new ArrayList<>();
        mSecondMenus = new ArrayList<>();
        mOperateMenus = new ArrayList<>();
        mUnVisibleIds = new ArrayList<>();
        mMenuSecondAdapter = new MenuAdapter(getContext(), mSecondMenus, R.layout.adapter_menu_second_item);
        mMenuSecondRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        mMenuSecondRecyclerView.addItemDecoration(
            new HorizontalDividerDecoration(ContextCompat.getColor(getContext(), R.color.transparent),
                SizeUtils.dp2Px(getContext(), 56), SizeUtils.dp2Px(getContext(), 4)));
        mMenuSecondRecyclerView.setAdapter(mMenuSecondAdapter);
        ViewGroup.LayoutParams layoutParams =
            new ViewGroup.LayoutParams(SizeUtils.dp2Px(getContext(), 16), SizeUtils.dp2Px(getContext(), 56));
        View header = new View(getContext());
        header.setLayoutParams(layoutParams);
        View foot = new View(getContext());
        foot.setLayoutParams(layoutParams);
        mMenuSecondAdapter.addHeaderView(header);
        mMenuSecondAdapter.addFooterView(foot);

        mMenuOperateAdapter = new MenuAdapter(getContext(), mOperateMenus, R.layout.adapter_menu_operate_item);
        mMenuOperateRecyclerView
            .setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        mMenuOperateRecyclerView.addItemDecoration(
            new HorizontalDividerDecoration(ContextCompat.getColor(getContext(), R.color.transparent),
                SizeUtils.dp2Px(getContext(), 56), SizeUtils.dp2Px(getContext(), 4)));
        mMenuOperateRecyclerView.setAdapter(mMenuOperateAdapter);

    }

    private void initMenuEvent() {
        mMenuFirstLayout.addTabSelectedChangeListener((index, prevInfo, nextInfo) -> {
            if ((mCurrentFirstIndex == index && nextInfo.responseEnable) || mFirstMenus == null
                || index >= mFirstMenus.size()) {
                return;
            }
            mCurrentFirstIndex = index;
            EditMenuBean editMenuBean = mFirstMenus.get(index);
            mSecondMenus.clear();

            if (editMenuBean.getChildren() != null && editMenuBean.getChildren().size() > 0) {
                List<EditMenuBean> children = editMenuBean.getChildren();
                for (EditMenuBean item : children) {
                    if (mUnVisibleIds.contains(item.getId())) {
                        continue;
                    }
                    mSecondMenus.add(item);
                }
                mMenuSecondRecyclerView.setVisibility(VISIBLE);
            } else {
                mMenuSecondRecyclerView.setVisibility(GONE);
            }

            mMenuSecondAdapter.notifyDataSetChanged();
            if (onMenuClickListener != null) {
                onMenuClickListener.onFirstMenuClick(editMenuBean);
            }
        });

        mMenuSecondAdapter.setOnItemClickListener((position, dataPosition) -> {
            if (mSecondMenus == null || dataPosition >= mSecondMenus.size()) {
                return;
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - mSecondClickTime < 500) {
                return;
            }
            EditMenuBean secondMenu = mSecondMenus.get(dataPosition);
            if (onMenuClickListener != null) {
                mSecondClickTime = currentTime;
                onMenuClickListener.onSecondMenuClick(secondMenu);
            }
        });

        mMenuOperateBack.setOnClickListener(v -> {
            mMenuFirstLayout.setVisibility(VISIBLE);
            mMenuSecondRecyclerView.setVisibility(VISIBLE);
            mMenuOperateLayout.setVisibility(GONE);
            mOperateMenus.clear();
            mMenuOperateAdapter.notifyDataSetChanged();
            isOperateShow = false;
            if (onMenuClickListener != null) {
                onMenuClickListener.onOperateBackClick();
            }
        });

        mMenuOperateAdapter.setOnItemClickListener((position, dataPosition) -> {
            if (mOperateMenus == null || dataPosition >= mOperateMenus.size()) {
                return;
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - mOperateClickTime < 500) {
                return;
            }
            EditMenuBean operateMenu = mOperateMenus.get(dataPosition);
            if (onMenuClickListener != null) {
                mOperateClickTime = currentTime;
                onMenuClickListener.onOperateMenuClick(operateMenu);
            }
        });

    }


    public void initFirstMenuData(List<EditMenuBean> menuBeans, List<Integer> unVisibleIds) {
        mMenuFirstLayout.setVisibility(VISIBLE);
        mMenuSecondRecyclerView.setVisibility(GONE);
        mMenuOperateLayout.setVisibility(GONE);

        mSecondMenus.clear();
        mMenuSecondAdapter.notifyDataSetChanged();

        mOperateMenus.clear();
        mMenuOperateAdapter.notifyDataSetChanged();
        isOperateShow = false;

        mCurrentFirstIndex = -1;
        mFirstMenus.clear();
        mFirstMenus.addAll(menuBeans);

        mUnVisibleIds.clear();
        mUnVisibleIds.addAll(unVisibleIds);

        int textDefaultColor = ContextCompat.getColor(getContext(), R.color.tab_text_default_color);
        int textSelectColor = ContextCompat.getColor(getContext(), R.color.tab_text_tint_color);
        int defaultNameId = R.string.app_name;
        int defaultImageId = R.drawable.logo;
        mInfoList.clear();
        for (int i = 0; i < mFirstMenus.size(); i++) {
            EditMenuBean item = mFirstMenus.get(i);
            boolean hasChildMenu = item.getChildren() != null && !item.getChildren().isEmpty();

            boolean stringValid = LocalResourceUtil.getStringId(getContext(), item.getName()) != 0;

            boolean imageValid = LocalResourceUtil.getDrawableId(getContext(), item.getDrawableName()) != 0;
            TabBottomInfo<?> info = new TabBottomInfo<>(
                stringValid ? LocalResourceUtil.getStringId(getContext(), item.getName()) : defaultNameId, item.getId(),
                imageValid ? LocalResourceUtil.getDrawableId(getContext(), item.getDrawableName()) : defaultImageId,
                textDefaultColor, textSelectColor, item.isEnable());
            info.setResponseEnable(hasChildMenu);
            mInfoList.add(info);
        }
        mMenuFirstLayout.inflateInfo(mInfoList);
        if (mInfoList.size() > 0) {
            setCurrentFirstMenu(0);
        }
    }

    public void setCurrentFirstMenu(int position) {
        if (position >= 0 && position < mInfoList.size()) {
            mMenuFirstLayout.defaultSelected(mInfoList.get(position));
        }
    }

    public void hideOperateMenu() {
        mMenuFirstLayout.setVisibility(VISIBLE);
        mMenuSecondRecyclerView.setVisibility(VISIBLE);
        mMenuOperateLayout.setVisibility(GONE);
        mOperateMenus.clear();
        mMenuOperateAdapter.notifyDataSetChanged();
        isOperateShow = false;
    }

    public void showOperateMenu(int operateTypeId, List<Integer> unVisibleIds, List<Integer> unableIds) {
        setOperateMenuData(operateTypeId, unVisibleIds, unableIds);
        if (mOperateMenus.size() == 0) {
            return;
        }
        mMenuFirstLayout.setVisibility(GONE);
        mMenuSecondRecyclerView.setVisibility(GONE);
        mMenuOperateLayout.setVisibility(VISIBLE);
        isOperateShow = true;
    }

    private void setOperateMenuData(int operateTypeId, List<Integer> unVisibleIds, List<Integer> unableIds) {
        List<EditMenuBean> menuOperates = MenuConfig.getInstance().getMenuOperates();
        mOperateMenus.clear();
        for (EditMenuBean menuBean : menuOperates) {
            if (menuBean.getId() == operateTypeId) {
                mOperateMenus.addAll(filterUnableMenu(menuBean.getOperates(), unVisibleIds, unableIds));
                break;
            }
        }
        mMenuOperateAdapter.notifyDataSetChanged();
    }

    public void updateUnAbleMenus(boolean isEnable, List<Integer> isEnableIds) {
        if (mSecondMenus != null) {
            if (isEnableIds != null) {
                for (int i = 0, mSecondMenusSize = mSecondMenus.size(); i < mSecondMenusSize; i++) {
                    EditMenuBean menuBean = mSecondMenus.get(i);
                    if (isEnable) {
                        menuBean.setEnable(isEnableIds.contains(menuBean.getId()) ? 1 : 0);
                    } else {
                        menuBean.setEnable(isEnableIds.contains(menuBean.getId()) ? 0 : 1);
                    }
                }

            } else {
                for (EditMenuBean mSecondMenu : mSecondMenus) {
                    mSecondMenu.setEnable(1);
                }
            }
            mMenuSecondAdapter.notifyDataSetChanged();
        }
    }

    private List<EditMenuBean> filterUnableMenu(List<EditMenuBean> operates, List<Integer> unVisibleIds,
        List<Integer> unableIds) {
        List<EditMenuBean> menuBeans = new ArrayList<>();
        for (EditMenuBean bean : operates) {
            try {
                if (!unVisibleIds.contains(bean.getId())) {
                    EditMenuBean newBean = (EditMenuBean) bean.clone();
                    newBean.setEnable(unableIds.contains(bean.getId()) ? 0 : 1);
                    menuBeans.add(newBean);
                }
            } catch (CloneNotSupportedException e) {
                SmartLog.e(TAG, e.getMessage());
            }

        }
        return menuBeans;
    }


    public boolean isOperateShow() {
        return isOperateShow;
    }

    public interface OnMenuClickListener {
        void onFirstMenuClick(EditMenuBean secondMenu);

        void onSecondMenuClick(EditMenuBean secondMenu);

        void onOperateMenuClick(EditMenuBean operateMenu);

        void onOperateBackClick();
    }
}
