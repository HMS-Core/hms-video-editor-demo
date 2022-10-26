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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RenderHandler extends Handler {

    static final int MSG_INIT = 0x01;

    static final int MSG_DISPLAY_CHANGE = 0x02;

    static final int MSG_DESTROY = 0x03;

    static final int MSG_RENDER = 0x04;

    static final int MSG_PREPARE = 0x05;

    private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();

    private final WeakReference<Renderer> mWeakRender;

    public RenderHandler(Looper looper, Renderer renderer) {
        super(looper);
        mWeakRender = new WeakReference<>(renderer);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mWeakRender.get() == null) {
            return;
        }

        handleQueueEvent();

        Renderer renderer = mWeakRender.get();
        switch (msg.what) {
            case MSG_INIT:
                if (msg.obj instanceof SurfaceHolder) {
                    renderer.initRender(((SurfaceHolder) msg.obj).getSurface());
                } else if (msg.obj instanceof Surface) {
                    renderer.initRender((Surface) msg.obj);
                } else if (msg.obj instanceof SurfaceTexture) {
                    renderer.initRender((SurfaceTexture) msg.obj);
                }
                break;

            case MSG_DISPLAY_CHANGE:
                renderer.setDisplaySize(msg.arg1, msg.arg2);
                break;

            case MSG_DESTROY:
                renderer.release();
                break;

            case MSG_RENDER:
                renderer.onDrawFrame();
                break;

            case MSG_PREPARE:
                renderer.prepareBeauty();
                break;
        }
    }

    void handleQueueEvent() {
        synchronized (this) {
            Runnable runnable;
            while (!mEventQueue.isEmpty()) {
                runnable = mEventQueue.remove(0);
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }

    public void queueEvent(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable must not be null");
        }
        synchronized (this) {
            mEventQueue.add(runnable);
            notifyAll();
        }
    }
}
