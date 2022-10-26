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

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraManager implements ICameraManager, Camera.PreviewCallback {

    private static final String TAG = "CameraManager";

    private static final int DEFAULT_WIDTH = 1280;

    private static final int DEFAULT_HEIGHT = 720;

    private int mExpectFps = CameraParam.PREVIEW_FPS;

    private int mPreviewWidth = DEFAULT_WIDTH;

    private int mPreviewHeight = DEFAULT_HEIGHT;

    private int mOrientation;

    private Camera mCamera;

    private int mCameraId;

    private OnSurfaceTextureListener mSurfaceTextureListener;

    private PreviewCallback mPreviewCallback;

    private OnFrameAvailableListener mFrameAvailableListener;

    private SurfaceTexture mOutputTexture;

    private HandlerThread mOutputThread;

    private final Activity mActivity;

    public CameraManager(@NonNull Activity activity) {
        Log.d(TAG, "CameraController: created！");
        mActivity = activity;
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    @Override
    public void openCamera() {
        closeCamera();
        if (mCamera != null) {
            throw new RuntimeException("camera already initialized!");
        }
        mCamera = Camera.open(mCameraId);
        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }
        CameraParam cameraParam = CameraParam.getInstance();
        cameraParam.cameraId = mCameraId;
        Camera.Parameters parameters = mCamera.getParameters();
        cameraParam.previewFps = chooseFixedPreviewFps(parameters, mExpectFps * 1000);
        parameters.setRecordingHint(true);
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK && supportAutoFocusFeature(parameters)) {
            mCamera.cancelAutoFocus();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(parameters);
        setPreviewSize(mCamera, mPreviewWidth, mPreviewHeight);
        mOrientation = calculateCameraPreviewOrientation(mActivity);
        mCamera.setDisplayOrientation(mOrientation);
        releaseSurfaceTexture();
        mOutputTexture = createDetachedSurfaceTexture();
        try {
            mCamera.setPreviewTexture(mOutputTexture);
            mCamera.setPreviewCallback(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        if (mSurfaceTextureListener != null) {
            mSurfaceTextureListener.onSurfaceTexturePrepared(mOutputTexture);
        }
    }

    @Override
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.addCallbackBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        releaseSurfaceTexture();
    }

    private boolean supportAutoFocusFeature(@NonNull Camera.Parameters parameters) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            return true;
        }
        return false;
    }

    private SurfaceTexture createDetachedSurfaceTexture() {
        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        surfaceTexture.detachFromGLContext();
        if (Build.VERSION.SDK_INT >= 21) {
            if (mOutputThread != null) {
                mOutputThread.quit();
                mOutputThread = null;
            }
            mOutputThread = new HandlerThread("FrameAvailableThread");
            mOutputThread.start();
            surfaceTexture.setOnFrameAvailableListener(texture -> {
                if (mFrameAvailableListener != null) {
                    mFrameAvailableListener.onFrameAvailable(texture);
                }
            }, new Handler(mOutputThread.getLooper()));
        } else {
            surfaceTexture.setOnFrameAvailableListener(texture -> {
                if (mFrameAvailableListener != null) {
                    mFrameAvailableListener.onFrameAvailable(texture);
                }
            });
        }
        return surfaceTexture;
    }

    private void releaseSurfaceTexture() {
        if (mOutputTexture != null) {
            mOutputTexture.release();
            mOutputTexture = null;
        }
        if (mOutputThread != null) {
            mOutputThread.quitSafely();
            mOutputThread = null;
        }
    }

    @Override
    public void setOnSurfaceTextureListener(OnSurfaceTextureListener listener) {
        mSurfaceTextureListener = listener;
    }

    @Override
    public void setPreviewCallback(PreviewCallback callback) {
        mPreviewCallback = callback;
    }

    @Override
    public void setOnFrameAvailableListener(OnFrameAvailableListener listener) {
        mFrameAvailableListener = listener;
    }

    @Override
    public void switchCamera() {
        boolean front = !isFront();
        if (front != isFront()) {
            setFront(front);
            openCamera();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mPreviewCallback != null) {
            mPreviewCallback.onPreviewFrame(data);
        }
    }

    @Override
    public void setFront(boolean front) {
        if (front) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    @Override
    public boolean isFront() {
        return (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    public int getOrientation() {
        return mOrientation;
    }

    @Override
    public int getPreviewWidth() {
        return mPreviewWidth;
    }

    @Override
    public int getPreviewHeight() {
        return mPreviewHeight;
    }

    private int calculateCameraPreviewOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CameraParam.getInstance().cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private void setPreviewSize(Camera camera, int expectWidth, int expectHeight) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = calculatePerfectSize(parameters.getSupportedPreviewSizes(), expectWidth, expectHeight);
        parameters.setPreviewSize(size.width, size.height);
        mPreviewWidth = size.width;
        mPreviewHeight = size.height;
        camera.setParameters(parameters);
    }

    private static Camera.Size calculatePerfectSize(List<Camera.Size> sizes, int expectWidth, int expectHeight) {
        sortList(sizes);
        List<Camera.Size> bigEnough = new ArrayList<>();
        List<Camera.Size> noBigEnough = new ArrayList<>();
        for (Camera.Size size : sizes) {
            if (size.height * expectWidth / expectHeight == size.width) {
                if (size.width > expectWidth && size.height > expectHeight) {
                    bigEnough.add(size);
                } else {
                    noBigEnough.add(size);
                }
            }
        }
        Camera.Size perfectSize = null;
        if (noBigEnough.size() > 0) {
            Camera.Size size = Collections.max(noBigEnough, new CompareAreaSize());
            if (((float) size.width / expectWidth) >= 0.8 && ((float) size.height / expectHeight) > 0.8) {
                perfectSize = size;
            }
        } else if (bigEnough.size() > 0) {
            Camera.Size size = Collections.min(bigEnough, new CompareAreaSize());
            if (((float) expectWidth / size.width) >= 0.8 && ((float) (expectHeight / size.height)) >= 0.8) {
                perfectSize = size;
            }
        }
        if (perfectSize == null) {
            Camera.Size result = sizes.get(0);
            boolean widthOrHeight = false;
            for (Camera.Size size : sizes) {
                if (size.width == expectWidth && size.height == expectHeight
                    && ((float) size.height / (float) size.width) == CameraParam.getInstance().currentRatio) {
                    result = size;
                    break;
                }
                if (size.width == expectWidth) {
                    widthOrHeight = true;
                    if (Math.abs(result.height - expectHeight) > Math.abs(size.height - expectHeight)
                        && ((float) size.height / (float) size.width) == CameraParam.getInstance().currentRatio) {
                        result = size;
                        break;
                    }
                } else if (size.height == expectHeight) {
                    widthOrHeight = true;
                    if (Math.abs(result.width - expectWidth) > Math.abs(size.width - expectWidth)
                        && ((float) size.height / (float) size.width) == CameraParam.getInstance().currentRatio) {
                        result = size;
                        break;
                    }
                } else if (!widthOrHeight) {
                    if (Math.abs(result.width - expectWidth) > Math.abs(size.width - expectWidth)
                        && Math.abs(result.height - expectHeight) > Math.abs(size.height - expectHeight)
                        && ((float) size.height / (float) size.width) == CameraParam.getInstance().currentRatio) {
                        result = size;
                    }
                }
            }
            perfectSize = result;
        }
        return perfectSize;
    }

    private static int chooseFixedPreviewFps(Camera.Parameters parameters, int expectedThoudandFps) {
        List<int[]> supportedFps = parameters.getSupportedPreviewFpsRange();
        for (int[] entry : supportedFps) {
            if (entry[0] == entry[1] && entry[0] == expectedThoudandFps) {
                parameters.setPreviewFpsRange(entry[0], entry[1]);
                return entry[0];
            }
        }
        int[] temp = new int[2];
        int guess;
        parameters.getPreviewFpsRange(temp);
        if (temp[0] == temp[1]) {
            guess = temp[0];
        } else {
            guess = temp[1] / 2;
        }
        return guess;
    }

    private static void sortList(List<Camera.Size> list) {
        Collections.sort(list, new CompareAreaSize());
    }

    private static class CompareAreaSize implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size pre, Camera.Size after) {
            return Long.signum((long) pre.width * pre.height - (long) after.width * after.height);
        }
    }
}
