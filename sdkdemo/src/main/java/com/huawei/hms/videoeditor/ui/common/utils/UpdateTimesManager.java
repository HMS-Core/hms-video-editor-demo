
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

package com.huawei.hms.videoeditor.ui.common.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

import androidx.annotation.NonNull;

public class UpdateTimesManager {
    private static final String TAG = "UpdateTimesManager";

    public static final long MIN_UPDATE_TIME = 1000 * 60 * 10;

    private static final int INIT = 0x00;

    private static final int UPDATE = 0x01;

    private HandlerThread handlerThread;

    private final Handler mHandler;

    private long mTimeSpan;

    private long mCurrentTime;

    private UpdateListener mListener;

    private State state = State.INIT;

    private static class Holder {
        public static final UpdateTimesManager INSTANCE = new UpdateTimesManager();
    }

    public UpdateTimesManager() {
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (null == mListener) {
                    SmartLog.e(TAG, "no listener.");
                    return;
                }
                switch (msg.what) {
                    case INIT:
                        mTimeSpan = 0;
                        mCurrentTime = System.currentTimeMillis();
                        mListener.onStart();
                        state = State.INIT;
                        mHandler.sendEmptyMessageDelayed(UPDATE, 1000);
                        SmartLog.d(TAG, "on listener start.");
                        break;
                    case UPDATE:
                        mTimeSpan = System.currentTimeMillis() - mCurrentTime;
                        if (mTimeSpan > MIN_UPDATE_TIME) {
                            mListener.onTimeout(mTimeSpan);
                            state = State.TIMEOUT;
                            this.removeCallbacksAndMessages(null);
                            SmartLog.d(TAG, "on listener timeout.");
                        } else {
                            mListener.onUpdate(mTimeSpan);
                            state = State.UPDATE;
                            mHandler.sendEmptyMessageDelayed(UPDATE, 1000);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public static UpdateTimesManager getInstance() {
        return Holder.INSTANCE;
    }

    public enum State {
        INIT,
        TIMEOUT,
        UPDATE
    }

    public synchronized void initTimesManager(UpdateListener listener) {
        SmartLog.d(TAG, "init timemanager");
        mListener = listener;
        mHandler.sendEmptyMessageDelayed(INIT, 1000);
    }

    public synchronized void destroy() {
        SmartLog.d(TAG, "destroy timemanager");
        mHandler.removeCallbacksAndMessages(null);
        state = State.INIT;
        mTimeSpan = 0;
        mCurrentTime = System.currentTimeMillis();
    }

    public interface UpdateListener {
        void onTimeout(long currentTimeSpan);

        void onStart();

        void onUpdate(long currentTimeSpan);
    }
}
