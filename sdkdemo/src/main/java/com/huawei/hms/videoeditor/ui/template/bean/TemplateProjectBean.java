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

package com.huawei.hms.videoeditor.ui.template.bean;

import java.util.ArrayList;
import java.util.List;

import com.huawei.hms.videoeditor.template.HVETemplateElement;

public class TemplateProjectBean {
    private String templateId;

    private List<HVETemplateElement> editableElements = new ArrayList<>();

    public List<HVETemplateElement> getEditableElements() {
        return editableElements;
    }

    public void setEditableElements(List<HVETemplateElement> editableElements) {
        this.editableElements = editableElements;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}