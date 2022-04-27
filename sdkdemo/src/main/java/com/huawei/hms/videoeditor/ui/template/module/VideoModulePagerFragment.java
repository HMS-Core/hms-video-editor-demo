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

package com.huawei.hms.videoeditor.ui.template.module;

import static android.app.Activity.RESULT_OK;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.template.HVETemplateInfo;
import com.huawei.hms.videoeditor.ui.common.listener.OnClickRepeatedListener;
import com.huawei.hms.videoeditor.ui.common.utils.SizeUtils;
import com.huawei.hms.videoeditor.ui.common.utils.ToastWrapper;
import com.huawei.hms.videoeditor.ui.common.view.decoration.MStaggeredGridLayoutManager;
import com.huawei.hms.videoeditor.ui.common.view.loading.LoadingIndicatorView;
import com.huawei.hms.videoeditor.ui.template.adapter.TemplatePagerAdapter;
import com.huawei.hms.videoeditor.ui.template.bean.Constant;
import com.huawei.hms.videoeditor.ui.template.bean.TemplateCacheData;
import com.huawei.hms.videoeditor.ui.template.delegate.TemplateItemView;
import com.huawei.hms.videoeditor.ui.template.module.activity.TemplateDetailActivity;
import com.huawei.hms.videoeditor.ui.template.view.RecyclerViewAtViewPager2;
import com.huawei.hms.videoeditor.ui.template.view.decoration.StaggeredDividerDecoration;
import com.huawei.hms.videoeditor.ui.template.viewmodel.HVETemplateCategoryModel;
import com.huawei.hms.videoeditorkit.sdkdemo.R;
import com.huawei.secure.android.common.intent.SafeBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoModulePagerFragment extends LazyFragment {

    private static final String TAG = "VideoModulePagerFg";

    public static final int GOTO_TEMPLATE_DETAILREQUEST_CODE = 1001;

    public static final String TEMPLATES_PAGE_TAB_POSITION = "templates_page_tab_position";

    public static final String TEMPLATES_LIST = "HVETemplateList";

    public static final String TEMPLATES_POSITION = "HVETemplatePosition";

    public static final String TEMPLATES_PAGING = "HVETemplatePaging";

    public static final String TEMPLATES_CATEGORY_ID = "HVETemplateCategoryId";

    public static final String TEMPLATES_COLOUNM = "TEMPLATES_COLOUNM";

    public static final String SOURCE_KEY = "source";

    private TemplatePagerAdapter mouldPagerAdapter;

    private List<HVETemplateInfo> mModuleHVETemplateList;

    private RecyclerViewAtViewPager2 mModuleRecyclerView;

    private HeaderViewRecyclerAdapter mFooterAdapter;

    private RelativeLayout mModuleErrorLayout;

    private RelativeLayout mModuleEmptyLayout;

    private TextView mErrorTv;

    private FrameLayout mModuleLoadingLayout;

    private LoadingIndicatorView mModuleIndicatorView;

    private HVETemplateCategoryModel mHVETemplateModel;

    private String categoryId;

    private int mCurrentPage = 0;

    private boolean mHasNextPage = false;

    private boolean mIsRefresh = false;

    private boolean isFirst;

    private View mNoMoreDataHintView;

    private String mSource;

    public static VideoModulePagerFragment newInstance(int tabPosition, HVEColumnInfo content, String source) {
        SafeBundle args = new SafeBundle();
        args.putInt(TEMPLATES_PAGE_TAB_POSITION, tabPosition);
        args.putString(Constant.EXTRA_SELECT_RESULT, content.getColumnId());
        args.putString("source", source);
        VideoModulePagerFragment fragment = new VideoModulePagerFragment();
        fragment.setArguments(args.getBundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        navigationBarColor = R.color.home_color_FF181818;
        mNoMoreDataHintView = LayoutInflater.from(mContext).inflate(R.layout.all_topics_no_more_data_layout, null);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_video_module_view_page_t;
    }

    @Override
    protected void initView(View view) {
        mModuleRecyclerView = view.findViewById(R.id.pager_recycler_view);
        mModuleEmptyLayout = view.findViewById(R.id.empty_layout);
        mModuleErrorLayout = view.findViewById(R.id.error_layout);
        mErrorTv = view.findViewById(R.id.error_text);
        mModuleLoadingLayout = view.findViewById(R.id.loading_layout);
        mModuleIndicatorView = view.findViewById(R.id.indicator);
    }

    @Override
    protected void initObject() {
        SafeBundle bundle = new SafeBundle(getArguments());
        categoryId = bundle.getString(Constant.EXTRA_SELECT_RESULT);
        mSource = bundle.getString(SOURCE_KEY, "");

        mHVETemplateModel = new ViewModelProvider(this, mFactory).get(HVETemplateCategoryModel.class);
        mModuleHVETemplateList = new ArrayList<>();
        initAdapter();
    }

    private void initAdapter() {
        mouldPagerAdapter = new TemplatePagerAdapter(mContext, mModuleHVETemplateList, new TemplateItemClickListener());
        MStaggeredGridLayoutManager manager = new MStaggeredGridLayoutManager(2, MStaggeredGridLayoutManager.VERTICAL);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setGapStrategy(MStaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mModuleRecyclerView.setLayoutManager(manager);
        DefaultItemAnimator defaultItemAnimator = (DefaultItemAnimator) mModuleRecyclerView.getItemAnimator();
        if (defaultItemAnimator != null) {
            defaultItemAnimator.setSupportsChangeAnimations(false);
        }
        mModuleRecyclerView.setHasFixedSize(true);
        mModuleRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        for (int i = 0; i < mModuleRecyclerView.getItemDecorationCount(); i++) {
            mModuleRecyclerView.removeItemDecorationAt(i);
        }

        mModuleRecyclerView.addItemDecoration(
            new StaggeredDividerDecoration(SizeUtils.dp2Px(mContext, 8f), SizeUtils.dp2Px(mContext, 16f)));
        mModuleRecyclerView.setAdapter(mouldPagerAdapter);
        mModuleRecyclerView.setItemViewCacheSize(0);
        mFooterAdapter = new HeaderViewRecyclerAdapter(mouldPagerAdapter);
        mModuleRecyclerView.setAdapter(mFooterAdapter);
    }

    private void refreshFooterView() {
        SmartLog.i(TAG, "refreshFooterView");
        if (mFooterAdapter == null) {
            return;
        }

        if (!mHasNextPage) {
            mFooterAdapter.setFooterVisibility(true);
            mFooterAdapter.addFooterView(mNoMoreDataHintView);
        } else {
            mFooterAdapter.setFooterVisibility(false);
        }
    }

    @Override
    protected void initData() {
        mModuleLoadingLayout.setVisibility(View.VISIBLE);
        mModuleIndicatorView.show();
        mIsRefresh = true;
        mHVETemplateModel.initTemplateListLiveData(mCurrentPage, categoryId, false);
    }

    @Override
    protected void initEvent() {
        mHVETemplateModel.getHVECloudTemplateList().observe(this, hVECloudTemplateList -> {
            if (mCurrentPage == 0) {
                mModuleLoadingLayout.setVisibility(View.GONE);
                mModuleIndicatorView.hide();
            }
            if (mIsRefresh) {
                mModuleHVETemplateList.clear();
                mModuleHVETemplateList.addAll(hVECloudTemplateList);
                mouldPagerAdapter.notifyDataSetChanged();
            } else {
                int start = mModuleHVETemplateList.size();
                mModuleHVETemplateList.addAll(hVECloudTemplateList);
                mouldPagerAdapter.notifyItemRangeInserted(start, hVECloudTemplateList.size());
            }
            new Handler().postDelayed(() -> {
                refreshFooterView();
                if (mIsRefresh) {
                    mIsRefresh = false;
                }
            }, 200);
        });

        mHVETemplateModel.getBoundaryPageData()
            .observe(getViewLifecycleOwner(), hasNestPage -> this.mHasNextPage = hasNestPage);
        mHVETemplateModel.getErrorString().observe(getViewLifecycleOwner(), errorString -> {
            mModuleLoadingLayout.setVisibility(View.GONE);
            mModuleIndicatorView.hide();
            mErrorTv.setText(errorString);
            mModuleErrorLayout.setVisibility(View.VISIBLE);
            if (mIsRefresh || mModuleHVETemplateList.size() > 0) {
                mIsRefresh = false;
                mModuleErrorLayout.setVisibility(View.GONE);
                ToastWrapper.makeText(mContext, getString(R.string.result_illegal), Toast.LENGTH_SHORT).show();
            }
        });

        mHVETemplateModel.getEmptyString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (mCurrentPage == 0) {
                    mModuleEmptyLayout.setVisibility(View.VISIBLE);
                    mModuleLoadingLayout.setVisibility(View.GONE);
                    mModuleIndicatorView.hide();
                }
                if (mIsRefresh) {
                    mModuleEmptyLayout.setVisibility(View.VISIBLE);
                    mIsRefresh = false;
                }
            }
        });

        mModuleErrorLayout.setOnClickListener(v -> {
            mModuleErrorLayout.setVisibility(View.GONE);
            mHVETemplateModel.initTemplateListLiveData(mCurrentPage, categoryId, true);
        });

        mModuleEmptyLayout.setOnClickListener(new OnClickRepeatedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleEmptyLayout.setVisibility(View.GONE);
                mHVETemplateModel.initTemplateListLiveData(mCurrentPage, categoryId, true);
            }
        }));

        mModuleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                MStaggeredGridLayoutManager manager = (MStaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (manager == null) {
                    return;
                }
                int[] first = new int[2];
                manager.findFirstCompletelyVisibleItemPositions(first);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                    manager.invalidateSpanAssignments();
                }

                int totalItemCount = mouldPagerAdapter.getItemCount();
                if (mIsRefresh || !mHasNextPage || totalItemCount <= 0) {
                    return;
                }
                int[] lastVisibleItemPositions = manager.findLastVisibleItemPositions(null);
                int lastVisibleItem = Math.max(lastVisibleItemPositions[0], lastVisibleItemPositions[1]);
                int firstVisibleItem = manager.findFirstVisibleItemPositions(null)[0];
                if (lastVisibleItem <= 0) {
                    return;
                }
                boolean arriveBottom = (lastVisibleItem >= (totalItemCount - 1)) && (firstVisibleItem > 0);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && (mCurrentPage == 0 || arriveBottom)) {
                    SmartLog.d(TAG, "loadMore");
                    mCurrentPage++;
                    mHVETemplateModel.initTemplateListLiveData(mCurrentPage, categoryId, true);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIsRefresh) {
            mIsRefresh = false;
        }
    }

    private class TemplateItemClickListener implements TemplateItemView.OnItemClickListener {

        @Override
        public void onItemClick(int position) {
            int currentPosition = position;
            String columnName = TemplateCacheData.getColumnName();
            Intent intent = new Intent(mActivity, TemplateDetailActivity.class);

            ArrayList<String> templateJsons = new ArrayList<>();
            for (int i = 0; i < mModuleHVETemplateList.size(); i++) {
                templateJsons.add(new Gson().toJson(mModuleHVETemplateList.get(i)));
            }

            intent.putStringArrayListExtra(TEMPLATES_LIST, templateJsons);
            intent.putExtra(TEMPLATES_POSITION, currentPosition);
            intent.putExtra(TEMPLATES_PAGING, mCurrentPage);
            intent.putExtra(TEMPLATES_CATEGORY_ID, categoryId);
            intent.putExtra(TEMPLATES_COLOUNM, columnName);
            intent.putExtra(SOURCE_KEY, mSource);
            startActivityForResult(intent, GOTO_TEMPLATE_DETAILREQUEST_CODE);
        }

        @Override
        public void onLoadFailed(HVETemplateInfo template) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            int intPosition = data.getIntExtra(Constant.CropConst.INTENT_EXTRAS_POSITION, -1);
            if (mModuleHVETemplateList != null && mModuleHVETemplateList.size() > 0
                && mModuleHVETemplateList.size() > intPosition) {
                if (intPosition >= 0) {
                    mModuleHVETemplateList.remove(intPosition);
                    mouldPagerAdapter.notifyItemRemoved(intPosition);
                    if (intPosition != (mModuleHVETemplateList.size())) {
                        mouldPagerAdapter.notifyItemRangeChanged(intPosition,
                            mModuleHVETemplateList.size() - intPosition);
                    }
                }
            }
        }
        if (GOTO_TEMPLATE_DETAILREQUEST_CODE == requestCode && resultCode == RESULT_OK) {
            if (mActivity != null) {
                mActivity.setResult(RESULT_OK);
                mActivity.finish();
            }
        }
    }
}
