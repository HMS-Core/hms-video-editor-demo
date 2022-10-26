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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.fragment.CameraPreviewFragment;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.render.Renderer;

public class CameraPreviewPresenter extends PreviewPresenter<CameraPreviewFragment>
    implements PreviewCallback, OnSurfaceTextureListener, OnFrameAvailableListener {

    private static final String TAG = "CameraPreviewPresenter";

    private CameraParam mCameraParam;

    private FragmentActivity mActivity;

    private ICameraManager mCameraManager;

    private final Renderer mRenderer;

    public CameraPreviewPresenter(CameraPreviewFragment target) {
        super(target);
        mCameraParam = CameraParam.getInstance();
        mRenderer = new Renderer(this);
    }

    public void onAttach(FragmentActivity activity) {
        mActivity = activity;
        mRenderer.initRenderer();
        mCameraManager = new CameraManager(mActivity);
        mCameraManager.setPreviewCallback(this);
        mCameraManager.setOnFrameAvailableListener(this);
        mCameraManager.setOnSurfaceTextureListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRenderer.onPause();
        closeCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        mActivity = null;
        mRenderer.destroyRenderer();
    }

    public void initBeauty(HVEAIInitialCallback callback) {
        mRenderer.initBeauty(callback);
    }

    public void prepareBeauty() {
        mRenderer.onPrepareBeauty();
    }

    @NonNull
    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
        mRenderer.onSurfaceCreated(surfaceTexture);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        mRenderer.onSurfaceChanged(width, height);
    }

    @Override
    public void onSurfaceDestroyed() {
        mRenderer.onSurfaceDestroyed();
    }

    @Override
    public void showCompare(boolean enable) {
        mCameraParam.showCompare = enable;
    }

    private void openCamera() {
        mCameraManager.openCamera();
        calculateImageSize();
    }

    private void closeCamera() {
        mCameraManager.closeCamera();
    }

    @Override
    public void switchCamera() {
        mCameraManager.switchCamera();
    }

    @Override
    public void onSurfaceTexturePrepared(@NonNull SurfaceTexture surfaceTexture) {
        mRenderer.bindInputSurfaceTexture(surfaceTexture);
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        Log.d(TAG, "onPreviewFrame: width - " + mCameraManager.getPreviewWidth() + ", height - "
            + mCameraManager.getPreviewHeight());

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mRenderer.requestRender();
    }

    private void calculateImageSize() {
        int width;
        int height;
        if (mCameraManager.getOrientation() == 90 || mCameraManager.getOrientation() == 270) {
            width = mCameraManager.getPreviewHeight();
            height = mCameraManager.getPreviewWidth();
        } else {
            width = mCameraManager.getPreviewWidth();
            height = mCameraManager.getPreviewHeight();
        }
        mRenderer.setTextureSize(width, height);
    }
}
