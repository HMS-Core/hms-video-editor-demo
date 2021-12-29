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

package com.huawei.hms.videoeditor.ui.common.utils;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.RequiresApi;

public class ToastWrapper {
    private static final String TAG = "ToastWrapper";

    private static Field sFieldTN = null;

    private static Field sFieldTNHandler = null;

    private static CharSequence message;

    private final Handler mHandler = new Handler();

    private static boolean canceled = true;

    private static Toast toast;

    private static Toast mToast = null;

    @SuppressLint("StaticFieldLeak")
    private static TextView tvMsg;

    public ToastWrapper() {
    }

    public ToastWrapper(Toast toast) {
        this.toast = toast;
    }

    static {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public Object run() {
                try {
                    sFieldTN = Toast.class.getDeclaredField("mTN");
                    sFieldTN.setAccessible(true);
                    sFieldTNHandler = sFieldTN.getType().getDeclaredField("mHandler");
                    sFieldTNHandler.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    SmartLog.e(TAG, e.getMessage());
                }
                return Optional.empty();
            }
        });
    }

    private static void hook(Toast toast) {
        try {
            if (sFieldTN != null) {
                Object tn = sFieldTN.get(toast);
                Handler preHandler = new Handler();
                if (sFieldTNHandler.get(tn) instanceof Handler) {
                    preHandler = (Handler) sFieldTNHandler.get(tn);
                }
                sFieldTNHandler.set(tn, new SafelyHandlerWrapper(preHandler));
            }
        } catch (IllegalAccessException e) {
            SmartLog.e(TAG, e.getMessage());
        }
    }

    private static class SafelyHandlerWrapper extends Handler {
        private Handler impl;

        SafelyHandlerWrapper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);
        }
    }

    /**
     * 编辑界面菜单使用Toast
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public static ToastWrapper makeText(Context context, CharSequence text, int duration) {
        message = text;
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }

        int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
        mToast.getView().setBackgroundColor(Color.TRANSPARENT);
        TextView textView = mToast.getView().findViewById(tvToastId);
        textView.setBackground(context.getDrawable(R.drawable.bg_toast_show));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.clip_color_E6FFFFFF));
        textView.setPadding(SizeUtils.dp2Px(context, 16), SizeUtils.dp2Px(context, 8), SizeUtils.dp2Px(context, 16),
            SizeUtils.dp2Px(context, 8));
        mToast.setGravity(Gravity.CENTER, 0, -SizeUtils.dp2Px(context, 30));
        return new ToastWrapper(mToast);
    }

    public static ToastWrapper makeText(Context context, CharSequence text) {
        message = text;
        Toast t = new Toast(context.getApplicationContext());
        View v = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.toast_custom, null);
        tvMsg = v.findViewById(R.id.content);
        tvMsg.setText(text);
        t.setGravity(Gravity.CENTER, 0, -SizeUtils.dp2Px(context, 30));
        t.setView(v);
        ToastWrapper wrapper = new ToastWrapper(t);
        return wrapper;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void makeTextWithShow(Context context, String text, int duration) {
        TextView textView;
        int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), text, duration);
            toast.getView().setBackgroundColor(Color.TRANSPARENT);
            textView = toast.getView().findViewById(tvToastId);
            if (textView != null) {
                textView.setBackground(context.getResources().getDrawable(R.drawable.bg_toast_show));
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(context.getResources().getColor(R.color.clip_color_E6FFFFFF));
            }
        } else {
            toast.getView().setBackgroundColor(Color.TRANSPARENT);
            textView = toast.getView().findViewById(tvToastId);
            if (textView != null) {
                textView.setBackground(context.getResources().getDrawable(R.drawable.bg_toast_show));
                textView.setGravity(Gravity.CENTER);
                textView.setText(text);
                textView.setTextColor(context.getResources().getColor(R.color.clip_color_E6FFFFFF));
            }
        }

        if (textView == null) {
            SmartLog.e(TAG, "textView is null");
            return;
        }
        textView.setPadding(SizeUtils.dp2Px(context, 16), SizeUtils.dp2Px(context, 8), SizeUtils.dp2Px(context, 16),
            SizeUtils.dp2Px(context, 8));
        toast.setGravity(Gravity.CENTER, 0, -SizeUtils.dp2Px(context, 30));
        // hook(toast);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.show();
                }
            }
        }, 0, 1000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                }
                timer.cancel();
            }
        }, duration);
    }

    public static ToastWrapper makeText(Context context, int res, int duration) {
        return makeText(context.getApplicationContext(), context.getResources().getString(res), duration);
    }

    private static class TimeCount extends CountDownTimer {
        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvMsg.setText(message);
        }

        @Override
        public void onFinish() {
            hide();
        }
    }

    private void showUntilCancel() {
        if (canceled) {
            return;
        }
        toast.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showUntilCancel();
            }
        }, Toast.LENGTH_LONG);
    }

    public void show() {
        if (toast != null) {
            toast.show();
        }
    }

    public void show(int duration) {
        TimeCount timeCount = new TimeCount(duration, 1000);
        if (canceled) {
            timeCount.start();
            canceled = false;
            showUntilCancel();
        }
    }

    public static void hide() {
        if (toast != null) {
            toast.cancel();
        }
        canceled = true;
    }

    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
