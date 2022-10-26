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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.ui.common.view.dialog.LoadingDialog;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.camera.CameraParam;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.camera.CameraPreviewPresenter;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.widget.CameraTextureView;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.widget.PreviewFrameLayout;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.widget.PreviewTopbar;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

public class CameraPreviewFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CameraPreviewFragment";

    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

    private CameraParam mCameraParam;

    private View mContentView;

    private PreviewFrameLayout mPreviewLayout;

    private CameraTextureView mCameraTextureView;

    private PreviewTopbar mPreviewTopbar;

    private PreviewBeautyFragment mEffectFragment;

    private final Handler mMainHandler;

    private FragmentActivity mActivity;

    private CameraPreviewPresenter mPreviewPresenter;

    private LoadingDialog mProgressDialog;

    public CameraPreviewFragment() {
        mCameraParam = CameraParam.getInstance();
        mMainHandler = new Handler(Looper.getMainLooper());
        mPreviewPresenter = new CameraPreviewPresenter(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        } else {
            mActivity = getActivity();
        }
        mPreviewPresenter.onAttach(mActivity);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreviewPresenter.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_camera_preview, container, false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isCameraEnable()) {
            initView(mContentView);
        }
    }

    private void initView(View view) {
        initPreviewSurface();
        initPreviewTopbar();
    }

    private static Point getScreenSize(Context mClientContext) {
        WindowManager windowManager = (WindowManager) mClientContext.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        defaultDisplay.getRealSize(outPoint);

        return outPoint;
    }

    void initSurfaceViewSize(Point outPoint, TextureView surfaceView) {
        float sceenWidth = outPoint.x;
        float sceenHeight = outPoint.y;
        int width = 720;
        int height = 1280;
        float rate;
        android.widget.FrameLayout.LayoutParams paramSurface =
            (android.widget.FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        if (sceenWidth / (float) width > sceenHeight / (float) height) {
            rate = sceenWidth / (float) width;
            int targetHeight = (int) (height * rate);
            paramSurface.width = android.widget.FrameLayout.LayoutParams.MATCH_PARENT;
            paramSurface.height = targetHeight;
            paramSurface.gravity = Gravity.CENTER;
        } else {
            rate = sceenHeight / (float) height;
            int targetWidth = (int) (width * rate);
            paramSurface.height = android.widget.FrameLayout.LayoutParams.MATCH_PARENT;
            paramSurface.width = targetWidth;
            paramSurface.gravity = Gravity.CENTER;
        }
        surfaceView.setLayoutParams(paramSurface);
    }

    private void initPreviewSurface() {
        mPreviewLayout = mContentView.findViewById(R.id.layout_camera_preview);
        mCameraTextureView = new CameraTextureView(mActivity);
        mCameraTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        mPreviewLayout.addView(mCameraTextureView);

        Point outPoint = getScreenSize(mActivity);
        initSurfaceViewSize(outPoint, mCameraTextureView);
    }

    private void initProgressDialog() {
        mProgressDialog = new LoadingDialog(mActivity, R.style.CustomDialog);
        mProgressDialog.show();
    }

    private void initPreviewTopbar() {
        mPreviewTopbar = mContentView.findViewById(R.id.camera_preview_topbar);
        mPreviewTopbar.addOnCameraCloseListener(this::closeCamera)
            .addOnCameraSwitchListener(this::switchCamera)
            .addOnShowListener(type -> {
                switch (type) {
                    case PreviewTopbar.PanelFilter: {
                        initProgressDialog();
                        mPreviewPresenter.initBeauty(new HVEAIInitialCallback() {
                            @Override
                            public void onProgress(int progress) {
                            }

                            @Override
                            public void onSuccess() {
                                Log.i(TAG, "onSuccess");
                                if (mProgressDialog != null) {
                                    mProgressDialog.dismiss();
                                }
                                showEffectFragment();
                                mPreviewPresenter.prepareBeauty();
                            }

                            @Override
                            public void onError(int errorCode, String errorMsg) {
                            }
                        });
                        break;
                    }
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
        mPreviewPresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPreviewPresenter.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreviewPresenter.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        mPreviewPresenter.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        mContentView = null;
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        mPreviewPresenter.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        mPreviewPresenter.onDetach();
        mPreviewPresenter = null;
        mActivity = null;
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    public boolean onBackPressed() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            removeFragment();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {

    }

    private void closeCamera() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    private void switchCamera() {
        if (!isCameraEnable()) {
            return;
        }
        mPreviewPresenter.switchCamera();
    }

    private void showEffectFragment() {
        if (mEffectFragment == null) {
            mEffectFragment = new PreviewBeautyFragment();
        }
        mEffectFragment.addOnCompareEffectListener(compare -> {
            mPreviewPresenter.showCompare(compare);
        });

        if (!mEffectFragment.isAdded()) {
            getChildFragmentManager().beginTransaction()
                .add(R.id.fragment_bottom_container, mEffectFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();
        } else {
            getChildFragmentManager().beginTransaction().show(mEffectFragment).commitAllowingStateLoss();
        }
    }

    private void removeFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            getChildFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mPreviewPresenter.onSurfaceCreated(surface);
            mPreviewPresenter.onSurfaceChanged(width, height);
            Log.d(TAG, "onSurfaceTextureAvailable: ");
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mPreviewPresenter.onSurfaceChanged(width, height);
            Log.d(TAG, "onSurfaceTextureSizeChanged: ");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mPreviewPresenter.onSurfaceDestroyed();
            Log.d(TAG, "onSurfaceTextureDestroyed: ");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private boolean isCameraEnable() {
        return permissionChecking(mActivity, Manifest.permission.CAMERA);
    }

    private boolean permissionChecking(@NonNull Context context, @NonNull String permission) {
        int targetVersion = 1;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            targetVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {

        }
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && targetVersion >= Build.VERSION_CODES.M) {
            result = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        } else {
            result =
                (PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED);
        }
        return result;
    }
}
