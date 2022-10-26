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

package com.huawei.hms.videoeditor.ui.mediaeditor.ai.render;

import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.camera.PreviewPresenter;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.gles.EglCore;
import com.huawei.hms.videoeditor.ui.mediaeditor.ai.gles.WindowSurface;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.opengles.GL10;

public class Renderer extends Thread {

    private static final String TAG = "Renderer";

    private int mPriority;

    private Looper mLooper;

    private @Nullable RenderHandler mHandler;

    private EglCore mEglCore;

    private WindowSurface mDisplaySurface;

    private volatile boolean mNeedToAttach;

    private WeakReference<SurfaceTexture> mWeakSurfaceTexture;

    private final float[] mMatrix = new float[16];

    private int mInputTexture = -1;

    private int mCurrentTexture;

    private final RenderManager mRenderManager;

    // Presenter
    private final WeakReference<PreviewPresenter> mWeakPresenter;

    private volatile boolean mThreadStarted;

    public Renderer(@NonNull PreviewPresenter presenter) {
        super(TAG);
        mPriority = Process.THREAD_PRIORITY_DISPLAY;
        mWeakPresenter = new WeakReference<>(presenter);
        mRenderManager = new RenderManager();
        mThreadStarted = false;
    }

    public void initRenderer() {
        synchronized (this) {
            if (!mThreadStarted) {
                start();
                mThreadStarted = true;
            }
        }
    }

    public void destroyRenderer() {
        synchronized (this) {
            quit();
        }
    }

    public void onPause() {
        if (mWeakSurfaceTexture != null) {
            mWeakSurfaceTexture.clear();
        }
    }

    public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
        Handler handler = getHandler();
        handler.sendMessage(handler.obtainMessage(RenderHandler.MSG_INIT, surfaceTexture));
    }

    public void initBeauty(HVEAIInitialCallback callback) {
        Log.i(TAG, "initBeauty");
        mRenderManager.initBeauty(callback);
    }

    public void onPrepareBeauty() {
        Handler handler = getHandler();
        handler.sendMessage(handler.obtainMessage(RenderHandler.MSG_PREPARE));
    }

    public void onSurfaceChanged(int width, int height) {
        Handler handler = getHandler();
        handler.sendMessage(handler.obtainMessage(RenderHandler.MSG_DISPLAY_CHANGE, width, height));
    }

    public void onSurfaceDestroyed() {
        Handler handler = getHandler();
        handler.sendMessage(handler.obtainMessage(RenderHandler.MSG_DESTROY));
    }

    public void setTextureSize(int width, int height) {
        mRenderManager.setTextureSize(width, height);
    }

    public void bindInputSurfaceTexture(@NonNull SurfaceTexture surfaceTexture) {
        queueEvent(() -> onBindInputSurfaceTexture(surfaceTexture));
    }

    void release() {
        Log.d(TAG, "release: ");

        if (mDisplaySurface != null) {
            mDisplaySurface.makeCurrent();
        }
        if (mInputTexture != OpenGLUtils.GL_NOT_TEXTURE) {
            OpenGLUtils.deleteTexture(mInputTexture);
            mInputTexture = OpenGLUtils.GL_NOT_TEXTURE;
        }
        mRenderManager.release();
        if (mWeakSurfaceTexture != null) {
            mWeakSurfaceTexture.clear();
        }
        if (mDisplaySurface != null) {
            mDisplaySurface.release();
            mDisplaySurface = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
    }

    public void queueEvent(@NonNull Runnable runnable) {
        getHandler().queueEvent(runnable);
    }

    public void requestRender() {
        getHandler().sendEmptyMessage(RenderHandler.MSG_RENDER);
    }

    void initRender(Surface surface) {
        if (mWeakPresenter == null || mWeakPresenter.get() == null) {
            return;
        }
        Log.d(TAG, "initRender: ");
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mDisplaySurface = new WindowSurface(mEglCore, surface, false);
        mDisplaySurface.makeCurrent();

        GLES30.glDisable(GL10.GL_DITHER);
        GLES30.glClearColor(0, 0, 0, 0);
        GLES30.glEnable(GL10.GL_CULL_FACE);
        GLES30.glEnable(GL10.GL_DEPTH_TEST);

        mRenderManager.init(mWeakPresenter.get().getContext());
    }

    void initRender(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "initRender: ");
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mDisplaySurface = new WindowSurface(mEglCore, surfaceTexture);
        mDisplaySurface.makeCurrent();

        GLES30.glDisable(GL10.GL_DITHER);
        GLES30.glClearColor(0, 0, 0, 0);
        GLES30.glEnable(GL10.GL_CULL_FACE);
        GLES30.glEnable(GL10.GL_DEPTH_TEST);

        mRenderManager.init(mWeakPresenter.get().getContext());
    }

    void setDisplaySize(int width, int height) {
        mRenderManager.setDisplaySize(width, height);
    }

    void onDrawFrame() {
        if (mDisplaySurface == null || mWeakSurfaceTexture == null || mWeakSurfaceTexture.get() == null) {
            return;
        }
        mDisplaySurface.makeCurrent();

        long timeStamp = 0;
        synchronized (this) {
            final SurfaceTexture surfaceTexture = mWeakSurfaceTexture.get();
            updateSurfaceTexture(surfaceTexture);
            timeStamp = surfaceTexture.getTimestamp();
        }

        if (mInputTexture == OpenGLUtils.GL_NOT_TEXTURE) {
            return;
        }
        mCurrentTexture = mRenderManager.drawFrame(mInputTexture, mMatrix);

        mDisplaySurface.swapBuffers();
    }

    private void updateSurfaceTexture(@NonNull SurfaceTexture surfaceTexture) {
        synchronized (this) {
            if (mNeedToAttach) {
                if (mInputTexture != OpenGLUtils.GL_NOT_TEXTURE) {
                    OpenGLUtils.deleteTexture(mInputTexture);
                }
                mInputTexture = OpenGLUtils.createOESTexture();
                try {
                    surfaceTexture.attachToGLContext(mInputTexture);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mNeedToAttach = false;
            }
        }
        try {
            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(mMatrix);
        } catch (Exception e) {
            Log.e(TAG, "updateSurfaceTexture  exception");
        }
    }

    private void onBindInputSurfaceTexture(SurfaceTexture surfaceTexture) {
        synchronized (this) {
            if (mWeakSurfaceTexture == null || mWeakSurfaceTexture.get() != surfaceTexture) {
                mWeakSurfaceTexture = new WeakReference<>(surfaceTexture);
                mNeedToAttach = true;
            }
        }
    }

    void prepareBeauty() {
        mRenderManager.prepareBeauty();
    }

    @Override
    public void run() {
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        Looper.loop();
        getHandler().handleQueueEvent();
        getHandler().removeCallbacksAndMessages(null);
        release();
        mThreadStarted = false;
        Log.d(TAG, "Thread has delete!");
    }

    private Looper getLooper() {
        if (!isAlive()) {
            return null;
        }
        synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {

                }
            }
        }
        return mLooper;
    }

    @NonNull
    public RenderHandler getHandler() {
        if (mHandler == null) {
            mHandler = new RenderHandler(getLooper(), this);
        }
        return mHandler;
    }

    private boolean quit() {
        Looper looper = getLooper();
        if (looper != null) {
            looper.quitSafely();
            return true;
        }
        return false;
    }
}
