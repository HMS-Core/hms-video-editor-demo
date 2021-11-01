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

package com.huawei.hms.videoeditor.ui.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class TextEditView extends EditText {
    public TextEditView(Context context) {
        super(context);
    }

    public TextEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1) {
            if (mShowKeyBord != null) {
                mShowKeyBord.showKeyBord(true);
            }
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public ShowKeyBord mShowKeyBord;

    public void setShowKeyBord(ShowKeyBord mShowKeyBord) {
        this.mShowKeyBord = mShowKeyBord;
    }

    public interface ShowKeyBord {
        void showKeyBord(boolean isShow);
    }
}
