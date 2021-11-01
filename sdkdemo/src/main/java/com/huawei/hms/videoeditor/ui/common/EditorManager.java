
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

package com.huawei.hms.videoeditor.ui.common;

import java.lang.ref.WeakReference;

import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.HuaweiVideoEditor;
import com.huawei.hms.videoeditor.sdk.lane.HVEVideoLane;

public class EditorManager {
    private WeakReference<HuaweiVideoEditor> editor;

    private static class EditorManagerHolder {
        private static final EditorManager INSTANCE = new EditorManager();
    }

    private EditorManager() {

    }

    public static EditorManager getInstance() {
        return EditorManagerHolder.INSTANCE;
    }

    public void setEditor(HuaweiVideoEditor mEditor) {
        this.editor = new WeakReference<>(mEditor);
    }

    public HuaweiVideoEditor getEditor() {
        if (editor == null) {
            return null;
        }
        return editor.get();
    }

    public HVETimeLine getTimeLine() {
        if (editor == null) {
            return null;
        }
        HuaweiVideoEditor videoEditor = editor.get();
        if (videoEditor == null) {
            return null;
        }
        HVETimeLine timeline = videoEditor.getTimeLine();
        if (timeline == null) {
            return null;
        }
        return timeline;
    }

    public HVEVideoLane getMainLane() {
        if (editor == null) {
            return null;
        }
        HuaweiVideoEditor videoEditor = editor.get();
        if (videoEditor == null) {
            return null;
        }
        HVETimeLine timeline = videoEditor.getTimeLine();
        if (timeline == null) {
            return null;
        }
        if (timeline.getAllVideoLane().size() == 0) {
            return null;
        }
        return timeline.getVideoLane(0);
    }

    public synchronized void recyclerEditor() {
        if (editor == null) {
            return;
        }

        HuaweiVideoEditor huaweiVideoEditor = editor.get();
        if (huaweiVideoEditor != null) {
            huaweiVideoEditor.stopEditor();
            editor = null;
        }
    }
}
