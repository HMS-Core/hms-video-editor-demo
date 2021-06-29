# 华为视频剪辑服务示例代码
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

中文 | [English](README.md)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
华为视频剪辑服务（Video Editor Kit） 提供视频剪辑全量功能，为开发者应用视频剪辑能力开发各类应用提供优质体验。本示例代码目的是为了介绍Video Editor Kit SDK的使用。

- 支持多视频、多图片快速删除、混合导入，可自由调整片段排放顺序和时长，快速进入编辑界面。为了更好的体验，建议导入1080p及以下的视频。 
- 支持剪辑基本操作，包括分割、删除、音量、比例、变速、画布、旋转、裁剪、动画、蒙版、镜像、复制、替换。 支持添加滤镜，可自由调整亮度、对比度、饱和度、色调、色温、锐化等参数。 
- 支持画中画功能，在视频中插入另一视频，使原视频全屏播出的同时，在画面的小面积区域上同时播出插入的另一视频。 
- 支持添加背景音乐、音效；支持录音功能。 
- 支持导出视频，可抽取视频中任意一帧画面或导入相册中的图片作为封面，设置导出视频的分辨率（最高支持1080p）后，最终打包生成为MP4格式的视频。
- 提供丰富的素材库供开发者使用，后续会不断丰富更新，并且会有一定数量的免费内容给开发者使用。 

## 工程目录结构

```
|-- com.huawei.videoeditorkit.videoeditdemo
	|-- Activity
		|-- MainActivity // Video Editor UI SDK 入口
		|-- SettingActivity  // Demo基本信息
```

## 运行步骤
 - 将本代码库克隆到本地。

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)。
 - agconnect-services.json文件请从[华为开发者社区](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050990353)网站申请获取。
 - 替换工程中的sample-agconnect-services.json文件。
 - 编译并且在安卓设备或模拟器上运行。

注意：

该项目中的package name不能用于申请agconnect-services.json，您可以使用自定义package name来申请agconnect-services.json。
您只需将应用级build.gradle中的applicationId修改为与所申请的agconnect-services.json相同的package name，即可体验Video Editor Kit提供的服务。

## 要求环境
推荐使用的AndroidSDK版本为21及以上，JDK版本为1.8及以上。

##  技术支持

如果您对HMS Core还处于评估阶段，可在[Reddit社区](https://www.reddit.com/r/HuaweiDevelopers/)获取关于HMS Core的最新讯息，并与其他开发者交流见解。

如果您对使用HMS示例代码有疑问，请尝试：

- 开发过程遇到问题上[Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services)，在\[huawei-mobile-services]标签下提问，有华为研发专家在线一对一解决您的问题。
- 到[华为开发者论坛](https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core板块与其他开发者进行交流。

如果您在尝试示例代码中遇到问题，请向仓库提交[issue](https://github.com/HMS-Core/hms-video-editor-demo/issues)，也欢迎您提交[Pull Request](https://github.com/HMS-Core/hms-video-editor-demo/pulls)。

##  许可证

此示例代码已获得[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0)。


