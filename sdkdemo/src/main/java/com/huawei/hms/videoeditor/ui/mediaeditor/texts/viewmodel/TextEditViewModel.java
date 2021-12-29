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

package com.huawei.hms.videoeditor.ui.mediaeditor.texts.viewmodel;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.graphics.Color;

import com.huawei.hms.videoeditor.common.network.http.ability.util.array.ArrayUtils;
import com.huawei.hms.videoeditor.sdk.HVETimeLine;
import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.asset.HVEWordAsset;
import com.huawei.hms.videoeditor.sdk.bean.HVEWordStyle;
import com.huawei.hms.videoeditor.sdk.engine.word.FontFileManager;
import com.huawei.hms.videoeditor.sdk.lane.HVEStickerLane;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.huawei.hms.videoeditor.ui.common.EditorManager;
import com.huawei.hms.videoeditorkit.sdkdemo.R;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class TextEditViewModel extends AndroidViewModel {
    private final List<Integer> normalImageList = new ArrayList<>(30);

    private final List<HVEWordStyle> wordStyleList = new ArrayList<>(30);

    private final List<Integer> colorList = new ArrayList<>(30);

    private final MutableLiveData<List<HVEWordAsset>> choseWordList = new MutableLiveData<>();

    private final MutableLiveData<List<HVEWordAsset>> wordList = new MutableLiveData<>();

    private final MutableLiveData<HVEWordStyle> wordStyle = new MutableLiveData<>(new HVEWordStyle());

    private HVEWordStyle mLastWordStyle;

    private int fontDefaultAlpha = 0xFF;

    private int strokeDefaultAlpha = 0xFF;

    private int shadowDefaultAlpha = 0xFF;

    private int bgDefaultAlpha = 0xFF;

    private int mColorTransValue = 100;

    private int mColorPositionValue = 0;

    private int mStrokeTransValue = 100;

    private int mStrokeThicknessValue = 40;

    private int mStrokeColorPositionValue = -1;

    private int mShadowColorPositionValue = -1;

    private int mShadowTransValue = 80;

    private int mShadowBlurValue = 40;

    private int mShadowDisValue = 0;

    private int mShadowAngleValue = 0;

    private int mBackColorPositionValue = -1;

    private int mBackTransValue = 100;

    private int mStylePosition = 0;

    private int mTextFontPosition = -1;

    private int mTextStyleTransVale = 100;

    public int getTextStyleTransVale() {
        return mTextStyleTransVale;
    }

    public void setTextStyleTransVale(int mTextStyleTransVale) {
        this.mTextStyleTransVale = mTextStyleTransVale;
    }

    public int getStylePosition() {
        return mStylePosition;
    }

    public void setStylePosition(int mStylePosition) {
        this.mStylePosition = mStylePosition;
    }

    public int getTextFontPosition() {
        return mTextFontPosition;
    }

    public void setTextFontPosition(int mTextFontPosition) {
        this.mTextFontPosition = mTextFontPosition;
    }

    public int getColorTransValue() {
        return mColorTransValue;
    }

    public void setColorTransValue(int mColorTrans) {
        this.mColorTransValue = mColorTrans;
    }

    public int getColorPositionValue() {
        return mColorPositionValue;
    }

    public void setColorPositionValue(int mColorPosition) {
        this.mColorPositionValue = mColorPosition;
    }

    public int getStrokeTransValue() {
        return mStrokeTransValue;
    }

    public void setStrokeTransValue(int mStrokeTrans) {
        this.mStrokeTransValue = mStrokeTrans;
    }

    public int getStrokeThicknessValue() {
        return mStrokeThicknessValue;
    }

    public void setStrokeThicknessValue(int mStrokeThickness) {
        this.mStrokeThicknessValue = mStrokeThickness;
    }

    public int getStrokeColorPositionValue() {
        return mStrokeColorPositionValue;
    }

    public void setStrokeColorPositionValue(int mStrokeColorPosition) {
        this.mStrokeColorPositionValue = mStrokeColorPosition;
    }

    public int getShadowColorPositionValue() {
        return mShadowColorPositionValue;
    }

    public void setShadowColorPositionValue(int mShowColorPosition) {
        this.mShadowColorPositionValue = mShowColorPosition;
    }

    public int getShadowTransValue() {
        return mShadowTransValue;
    }

    public void setShadowTransValue(int mShadowTrans) {
        this.mShadowTransValue = mShadowTrans;
    }

    public int getShadowBlurValue() {
        return mShadowBlurValue;
    }

    public void setShadowBlurValue(int mShadowBlur) {
        this.mShadowBlurValue = mShadowBlur;
    }

    public int getShadowDisValue() {
        return mShadowDisValue;
    }

    public void setShadowDisValue(int mShadowDis) {
        this.mShadowDisValue = mShadowDis;
    }

    public int getShadowAngleValue() {
        return mShadowAngleValue;
    }

    public void setShadowAngleValue(int mShadowAngle) {
        this.mShadowAngleValue = mShadowAngle;
    }

    public int getBackColorPositionValue() {
        return mBackColorPositionValue;
    }

    public void setBackColorPositionValue(int mBackColorPosition) {
        this.mBackColorPositionValue = mBackColorPosition;
    }

    public int getBackTransValue() {
        return mBackTransValue;
    }

    public void setBackTransValue(int mBackTrans) {
        this.mBackTransValue = mBackTrans;
    }

    public TextEditViewModel(@NonNull Application application) {
        super(application);
        initNormalImages();
        initColorList();
        initWordStyle();
    }

    public int getTextColor() {
        HVEWordStyle aWordStyle = getWordStyle();
        int color = aWordStyle.getFontColor();
        return (color & 0x00ffffff);
    }

    public int getStrokeColor() {
        HVEWordStyle cWordStyle = getWordStyle();
        int color = cWordStyle.getStrokeColor();
        return (color & 0x00ffffff);
    }

    public void setTextColor(int color) {
        HVEWordStyle gWordStyle = getWordStyle();
        int fontColor = gWordStyle.getFontColor();
        int newColor = color | (fontColor & 0xff000000);
        gWordStyle.setFontColor(newColor);
        setWordStyle(gWordStyle);
    }

    public void setTextColor(int alpha, int color) {
        HVEWordStyle hWordStyle = getWordStyle();
        hWordStyle.setFontColor(ColorUtils.setAlphaComponent(color, alpha));
        setWordStyle(hWordStyle);
    }

    public void setStrokeColor(int color) {
        HVEWordStyle iWordStyle = getWordStyle();
        int fontColor = iWordStyle.getFontColor();
        int newColor = color | (fontColor & 0xff000000);
        iWordStyle.setStrokeColor(newColor);
        setWordStyle(iWordStyle);

    }

    public void setStrokeColor(int alpha, int color) {
        HVEWordStyle jWordStyle = getWordStyle();
        jWordStyle.setStrokeColor(ColorUtils.setAlphaComponent(color, alpha));
        setWordStyle(jWordStyle);

    }

    public void setShadowColor(int alpha, int color) {
        HVEWordStyle kWordStyle = getWordStyle();
        kWordStyle.setShadowColor(ColorUtils.setAlphaComponent(color, alpha));
        setWordStyle(kWordStyle);
    }

    public void setWordLabel(int color) {
        HVEWordStyle lWordStyle = getWordStyle();
        lWordStyle.setBackgroundColor(color);
        setWordStyle(lWordStyle);
    }

    public void setLabelColor(int alpha, int color) {
        HVEWordStyle mWordStyle = getWordStyle();
        mWordStyle.setBackgroundColor(ColorUtils.setAlphaComponent(color, alpha));
        setWordStyle(mWordStyle);
    }

    public void setTextTrans(int alpha) {
        setFontAlpha(alpha);
    }

    public void setColorTrans(int alpha) {
        setFontAlpha(alpha);
    }

    public void setStrokeTrans(int alpha) {
        setStrokeAlpha(alpha);
    }

    public void setLabelTrans(int i) {
        setLabelAlpha(i);
    }

    public void setStrokeSize(int strokeSize) {
        HVEWordStyle nWordStyle = getWordStyle();
        nWordStyle.setStrokeFineness(strokeSize);
        setWordStyle(nWordStyle);
    }

    public void setShadowTrans(int alpha) {
        setShadowAlpha(alpha);
    }

    public HVEWordStyle getWordStyle() {
        return wordStyle.getValue();
    }

    public MutableLiveData<HVEWordStyle> getWordDecorLiveData() {
        return wordStyle;
    }

    public void setWordStyle(HVEWordStyle wordStyle) {
        setLastWordStyle(wordStyle);
        this.wordStyle.postValue(wordStyle);
    }

    private void setFontAlpha(int alpha) {
        HVEWordStyle pWordStyle = getWordStyle();
        pWordStyle.setFontColor(ColorUtils.setAlphaComponent(pWordStyle.getFontColor(), alpha));
        setWordStyle(pWordStyle);
    }

    private void setStrokeAlpha(int alpha) {
        HVEWordStyle qWordStyle = getWordStyle();
        qWordStyle.setStrokeColor(ColorUtils.setAlphaComponent(qWordStyle.getStrokeColor(), alpha));
        setWordStyle(qWordStyle);
    }

    private void setShadowAlpha(int alpha) {
        HVEWordStyle rWordStyle = getWordStyle();
        rWordStyle.setShadowColor(ColorUtils.setAlphaComponent(rWordStyle.getShadowColor(), alpha));
        setWordStyle(rWordStyle);
    }

    public void setLabelAlpha(int alpha) {
        HVEWordStyle sWordStyle = getWordStyle();
        sWordStyle.setBackgroundColor(ColorUtils.setAlphaComponent(sWordStyle.getBackgroundColor(), alpha));
        setWordStyle(sWordStyle);
    }

    public void setWordAlignment(int alignment) {
        HVEWordStyle tWordStyle = getWordStyle();
        tWordStyle.setAlignment(alignment);
        setWordStyle(tWordStyle);
    }

    public void setWordSpace(int rowSpace) {
        HVEWordStyle uWordStyle = getWordStyle();
        uWordStyle.setWordSpace(rowSpace);
        setWordStyle(uWordStyle);
    }

    public void setRowSpace(int rowSpace) {
        HVEWordStyle vWordStyle = getWordStyle();
        vWordStyle.setRowSpace(rowSpace);
        setWordStyle(vWordStyle);
    }

    public void setBold(boolean isBold) {
        HVEWordStyle wWordStyle = getWordStyle();
        wWordStyle.setBold(isBold);
        setWordStyle(wWordStyle);
    }

    public void setItalics(boolean isItalics) {
        HVEWordStyle xWordStyle = getWordStyle();
        xWordStyle.setItalics(isItalics);
        setWordStyle(xWordStyle);
    }

    public void setUnderline(boolean isUnderline) {
        HVEWordStyle yWordStyle = getWordStyle();
        yWordStyle.setUnderline(isUnderline);
        setWordStyle(yWordStyle);
    }

    public void setFontPath(String path, String id) {
        SmartLog.i("path", path);
        HVEWordStyle zWordStyle = getWordStyle();
        zWordStyle.setFontPath(FontFileManager.locateFontFile(path));
        zWordStyle.setCloudId(id);
        setWordStyle(zWordStyle);
    }

    public List<Integer> getNormalImageList() {
        return normalImageList;
    }

    public List<Integer> getColorList() {
        return colorList;
    }

    private void initColorList() {
        colorList.add(0xFFFFFFFF);
        colorList.add(0xFFB8B8B8);
        colorList.add(0xFF808080);
        colorList.add(0xFF333333);
        colorList.add(0xFF000000);
        colorList.add(0xFFFBACAC);
        colorList.add(0xFFE26464);
        colorList.add(0xFFD14040);
        colorList.add(0xFFC41B1B);
        colorList.add(0xFF950202);
        colorList.add(0xFFFBF1CA);
        colorList.add(0xFFE8D471);
        colorList.add(0xFFD2B72D);
        colorList.add(0xFFBC9A12);
        colorList.add(0xFFDFFBBF);
        colorList.add(0xFFA9E36E);
        colorList.add(0xFF5FC235);
        colorList.add(0xFF3EA214);
        colorList.add(0xFF056A01);
        colorList.add(0xFFCCF6D1);
        colorList.add(0xFF8CEC97);
        colorList.add(0xFF48DB59);
        colorList.add(0xFF169B25);
        colorList.add(0xFF03680E);
        colorList.add(0xFFC3F5F6);
        colorList.add(0xFF71E7E9);
        colorList.add(0xFF32C7C9);
        colorList.add(0xFF10A5A7);
        colorList.add(0xFF005E60);
        colorList.add(0xFFDEE8FE);
        colorList.add(0xFF8FAEF2);
        colorList.add(0xFF3163D1);
        colorList.add(0xFF1243AE);
        colorList.add(0xFF022268);
        colorList.add(0xFFC1BFFB);
        colorList.add(0xFF8683F2);
        colorList.add(0xFF4642E4);
        colorList.add(0xFF2521CE);
        colorList.add(0xFF0A0798);
        colorList.add(0xFFF3CCF6);
        colorList.add(0xFFE48DEB);
        colorList.add(0xFFD354DE);
        colorList.add(0xFFA612B4);
        colorList.add(0xFF770881);
        colorList.add(0xFFF6C3D9);
        colorList.add(0xFFEC89B4);
        colorList.add(0xFFD9548E);
        colorList.add(0xFFB71E61);
        colorList.add(0xFF750A39);
        colorList.add(0xFF85CEFF);
        colorList.add(0xFF5F98BE);
        colorList.add(0xFF46789A);
        colorList.add(0xFF2A4557);
        colorList.add(0xFF12202A);
        colorList.add(0xFFD6CC96);
        colorList.add(0xFFA49C70);
        colorList.add(0xFF6B6647);
        colorList.add(0xFF27251B);
        colorList.add(0xFFF1C7BD);
        colorList.add(0xFFC8836F);
        colorList.add(0xFFAB604D);
        colorList.add(0xFF823E2C);
        colorList.add(0xFF3E180E);
    }

    private void initNormalImages() {
        normalImageList.add(R.drawable.icon_cancel_wu);
        normalImageList.add(R.drawable.ico_t1);
        normalImageList.add(R.drawable.ico_t2);
        normalImageList.add(R.drawable.ico_t3);
        normalImageList.add(R.drawable.ico_t4);
        normalImageList.add(R.drawable.ico_t5);
        normalImageList.add(R.drawable.ico_t6);
        normalImageList.add(R.drawable.ico_t7);
        normalImageList.add(R.drawable.ico_t8);
        normalImageList.add(R.drawable.ico_t9);
        normalImageList.add(R.drawable.ico_t10);
        normalImageList.add(R.drawable.ico_t11);
        normalImageList.add(R.drawable.ico_t12);
        normalImageList.add(R.drawable.ico_t13);
        normalImageList.add(R.drawable.ico_t14);
        normalImageList.add(R.drawable.ico_t15);
        normalImageList.add(R.drawable.ico_t16);
        normalImageList.add(R.drawable.ico_t17);
        normalImageList.add(R.drawable.ico_t18);
    }

    private void initWordStyle() {
        HVEWordStyle wordStyle0 = new HVEWordStyle();
        wordStyle0.setFontColor(0xFFFFFFFF);
        wordStyle0.setStrokeColor(0x00000000);
        wordStyle0.setFontSize(72);
        wordStyle0.setStrokeFineness(0);
        wordStyle0.setShadowColor(0x00000000);
        wordStyle0.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle0);

        HVEWordStyle wordStyle1 = new HVEWordStyle();
        wordStyle1.setFontColor(0xFFFFFFFF);
        wordStyle1.setStrokeColor(0xFF000000);
        wordStyle1.setFontSize(72);
        wordStyle1.setStrokeFineness(50);
        wordStyle1.setShadowColor(0x00000000);
        wordStyle1.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle1);

        HVEWordStyle wordStyle2 = new HVEWordStyle();
        wordStyle2.setFontColor(0xFF000000);
        wordStyle2.setStrokeColor(0xFFFFFFFF);
        wordStyle2.setFontSize(72);
        wordStyle2.setStrokeFineness(50);
        wordStyle2.setShadowColor(0x00000000);
        wordStyle2.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle2);

        HVEWordStyle wordStyle3 = new HVEWordStyle();
        wordStyle3.setFontColor(0xFFD2B72D);
        wordStyle3.setStrokeColor(0xFF000000);
        wordStyle3.setFontSize(72);
        wordStyle3.setStrokeFineness(50);
        wordStyle3.setShadowColor(0x00000000);
        wordStyle3.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle3);

        HVEWordStyle wordStyle4 = new HVEWordStyle();
        wordStyle4.setFontColor(0xFFE26464);
        wordStyle4.setStrokeColor(0xFFFFFFFF);
        wordStyle4.setFontSize(72);
        wordStyle4.setStrokeFineness(50);
        wordStyle4.setShadowColor(0x00000000);
        wordStyle4.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle4);

        HVEWordStyle wordStyle5 = new HVEWordStyle();
        wordStyle5.setFontColor(0xFF8FAEF2);
        wordStyle5.setStrokeColor(0xFF000000);
        wordStyle5.setFontSize(72);
        wordStyle5.setStrokeFineness(50);
        wordStyle5.setShadowColor(0x00000000);
        wordStyle5.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle5);

        HVEWordStyle wordStyle6 = new HVEWordStyle();
        wordStyle6.setFontColor(0xFFF6C3D9);
        wordStyle6.setStrokeColor(0xFF750A39);
        wordStyle6.setFontSize(72);
        wordStyle6.setStrokeFineness(50);
        wordStyle6.setShadowColor(0x00000000);
        wordStyle6.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle6);

        HVEWordStyle wordStyle7 = new HVEWordStyle();
        wordStyle7.setFontColor(0xFFDEE8FE);
        wordStyle7.setStrokeColor(0xFF022268);
        wordStyle7.setFontSize(72);
        wordStyle7.setStrokeFineness(50);
        wordStyle7.setShadowColor(0x00000000);
        wordStyle7.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle7);

        HVEWordStyle wordStyle8 = new HVEWordStyle();
        wordStyle8.setFontColor(0xFFA9E36E);
        wordStyle8.setStrokeColor(0xFF056A01);
        wordStyle8.setFontSize(72);
        wordStyle8.setStrokeFineness(50);
        wordStyle8.setShadowColor(0x00000000);
        wordStyle8.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle8);

        HVEWordStyle wordStyle9 = new HVEWordStyle();
        wordStyle9.setFontColor(0xFF5F98BE);
        wordStyle9.setStrokeColor(0xFF12202A);
        wordStyle9.setFontSize(72);
        wordStyle9.setStrokeFineness(50);
        wordStyle9.setShadowColor(0x00000000);
        wordStyle9.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle9);

        HVEWordStyle wordStyle10 = new HVEWordStyle();
        wordStyle10.setFontColor(0xFFFBACAC);
        wordStyle10.setStrokeColor(0xFFD10808);
        wordStyle10.setFontSize(72);
        wordStyle10.setStrokeFineness(50);
        wordStyle10.setShadowColor(0x00000000);
        wordStyle10.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle10);

        HVEWordStyle wordStyle11 = new HVEWordStyle();
        wordStyle11.setFontColor(0xFF915E07);
        wordStyle11.setStrokeColor(0xFFFFFFFF);
        wordStyle11.setFontSize(72);
        wordStyle11.setStrokeFineness(50);
        wordStyle11.setShadowColor(0x00000000);
        wordStyle11.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle11);

        HVEWordStyle wordStyle12 = new HVEWordStyle();
        wordStyle12.setFontColor(0xFFD6CC96);
        wordStyle12.setStrokeColor(0xFF474330);
        wordStyle12.setFontSize(72);
        wordStyle12.setStrokeFineness(50);
        wordStyle12.setShadowColor(0x00000000);
        wordStyle12.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle12);

        HVEWordStyle wordStyle13 = new HVEWordStyle();
        wordStyle13.setFontColor(0xFFF1C7BD);
        wordStyle13.setStrokeColor(0xFF823E2C);
        wordStyle13.setFontSize(72);
        wordStyle13.setStrokeFineness(50);
        wordStyle13.setShadowColor(0x00000000);
        wordStyle13.setBackgroundColor(0x00000000);
        wordStyleList.add(wordStyle13);

        HVEWordStyle wordStyle14 = new HVEWordStyle();
        wordStyle14.setFontColor(0xFFFFFFFF);
        wordStyle14.setStrokeColor(0x00000000);
        wordStyle14.setFontSize(72);
        wordStyle14.setStrokeFineness(0);
        wordStyle14.setShadowColor(0x00000000);
        wordStyle14.setBackgroundColor(0xFF000000);
        wordStyleList.add(wordStyle14);

        HVEWordStyle wordStyle15 = new HVEWordStyle();
        wordStyle15.setFontColor(0xFF000000);
        wordStyle15.setStrokeColor(0x00000000);
        wordStyle15.setFontSize(72);
        wordStyle15.setStrokeFineness(0);
        wordStyle15.setShadowColor(0x00000000);
        wordStyle15.setBackgroundColor(0xFFFFFFFF);
        wordStyleList.add(wordStyle15);

        HVEWordStyle wordStyle16 = new HVEWordStyle();
        wordStyle16.setFontColor(0xFF000000);
        wordStyle16.setStrokeColor(0x00000000);
        wordStyle16.setFontSize(72);
        wordStyle16.setStrokeFineness(0);
        wordStyle16.setShadowColor(0x00000000);
        wordStyle16.setBackgroundColor(0xFFECD71B);
        wordStyleList.add(wordStyle16);

        HVEWordStyle wordStyle17 = new HVEWordStyle();
        wordStyle17.setFontColor(0xFFFFFFFF);
        wordStyle17.setStrokeColor(0x00000000);
        wordStyle17.setFontSize(72);
        wordStyle17.setStrokeFineness(0);
        wordStyle17.setShadowColor(0x00000000);
        wordStyle17.setBackgroundColor(0xFFB71E61);
        wordStyleList.add(wordStyle17);

        HVEWordStyle wordStyle18 = new HVEWordStyle();
        wordStyle18.setFontColor(0xFF8CEC97);
        wordStyle18.setStrokeColor(0x00000000);
        wordStyle18.setFontSize(72);
        wordStyle18.setStrokeFineness(0);
        wordStyle18.setShadowColor(0x00000000);
        wordStyle18.setBackgroundColor(0xFF000000);
        wordStyleList.add(wordStyle18);
    }

    public void setDefWordStyle(int position) {
        HVEWordStyle wordStyle19 = this.wordStyle.getValue();
        HVEWordStyle defStyle = wordStyleList.get(position);
        if (wordStyle19 == null || defStyle == null) {
            return;
        }
        wordStyle19.setStrokeColor(defStyle.getStrokeColor());
        wordStyle19.setShadowColor(defStyle.getShadowColor());
        wordStyle19.setBackgroundColor(defStyle.getBackgroundColor());
        wordStyle19.setStrokeFineness(defStyle.getStrokeFineness());
        wordStyle19.setFontColor(ColorUtils.setAlphaComponent(defStyle.getFontColor(), fontDefaultAlpha));
        setWordStyle(wordStyle19);
    }

    public int getFontDefaultAlpha() {
        return fontDefaultAlpha;
    }

    public void setFontDefaultAlpha(int fontDefaultAlpha) {
        this.fontDefaultAlpha = fontDefaultAlpha;
    }

    public int getStrokeDefaultAlpha() {
        return strokeDefaultAlpha;
    }

    public void setStrokeDefaultAlpha(int strokeDefaultAlpha) {
        this.strokeDefaultAlpha = strokeDefaultAlpha;
    }

    public int getShadowDefaultAlpha() {
        return shadowDefaultAlpha;
    }

    public void setShadowDefaultAlpha(int shadowDefaultAlpha) {
        this.shadowDefaultAlpha = shadowDefaultAlpha;
    }

    public int getLabelDefaultAlpha() {
        return bgDefaultAlpha;
    }

    public void setLabelDefaultAlpha(int bgDefaultAlpha) {
        this.bgDefaultAlpha = bgDefaultAlpha;
    }

    public HVEWordStyle getLastWordStyle() {
        return mLastWordStyle;
    }

    public void setLastWordStyle(HVEWordStyle wordStyle) {
        mLastWordStyle = new HVEWordStyle();
        mLastWordStyle.setFontColor(wordStyle.getFontColor());
        mLastWordStyle.setBackgroundColor(wordStyle.getBackgroundColor());
        mLastWordStyle.setFontSize(wordStyle.getFontSize());
        mLastWordStyle.setFontPath(wordStyle.getFontPath());
        mLastWordStyle.setAlignment(wordStyle.getAlignment());
        mLastWordStyle.setShadowColor(wordStyle.getShadowColor());
        mLastWordStyle.setStrokeColor(wordStyle.getStrokeColor());
        mLastWordStyle.setShadowAngle(wordStyle.getShadowAngle());
        mLastWordStyle.setShadowBlur(wordStyle.getShadowBlur());
        mLastWordStyle.setShadowDistance(wordStyle.getShadowDistance());
        mLastWordStyle.setStrokeFineness(wordStyle.getStrokeFineness());
        mLastWordStyle.setBold(wordStyle.isBold());
        mLastWordStyle.setItalics(wordStyle.isItalics());
        mLastWordStyle.setUnderline(wordStyle.isUnderline());
    }


    public MutableLiveData<List<HVEWordAsset>> getWordList() {
        return wordList;
    }

    public void setWordList(List<HVEWordAsset> wordList) {
        this.wordList.postValue(wordList);
    }

    public MutableLiveData<List<HVEWordAsset>> getChoseWordList() {
        return choseWordList;
    }

    public void setChoseWordList(List<HVEWordAsset> choseWordList) {
        this.choseWordList.postValue(new ArrayList<>(choseWordList));
    }

    public void refreshAsset() {
        setDefWordStyle(0);
        setFontDefaultAlpha(255);
        setTextTrans(255);
        setBold(false);
        setItalics(false);
        setUnderline(false);
        setWordLabel(Color.TRANSPARENT);
        setStrokeColor(0, Color.WHITE);
        setShadowColor(0, Color.WHITE);
        setWordAlignment(HVEWordStyle.ALIGNMENT_HORIZONTAL_LEFT);
        setStrokeColor(0, Color.WHITE);
        setShadowTrans(0);
        getLastWordStyle().setShadowAngle(0);
        getLastWordStyle().setShadowBlur(0);
        getLastWordStyle().setShadowDistance(0);
        setWordSpace(0);
        setRowSpace(0);
        setTextColor(255, Color.WHITE);
        setBackColorPositionValue(-1);
        setShadowColorPositionValue(-1);
        setColorTransValue(100);
        setColorPositionValue(0);
    }
}
