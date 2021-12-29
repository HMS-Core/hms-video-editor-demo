# 华为视频剪辑服务示例代码

中文 | [English](README.md)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [运行步骤](#运行步骤)
 * [环境要求](#环境要求)
 * [许可证](#许可证)


## 介绍
视频编辑服务（Video Editor Kit）是华为快速构建视频编辑能力的服务，提供两种集成方式：

- 视频编辑UI SDK，提供产品级UI界面，集成简单。
- 视频编辑原子能力SDK，提供数百个底层能力接口，包含多个AI算法能力接口，可根据业务场景灵活选择。

这两种方式均提供导入、编辑、渲染、导出、媒体资源管理等一站式视频编辑能力，为您提供性能优异、简单易用、高兼容性的接口，帮助您轻松地构建应用。

您可根据使用场景选择不同的集成方式获取视频编辑能力。但您只能选择其中一种方式集成，不可同时使用。

本示例代码目的是为了介绍Video Editor Kit SDK的使用，提供了两种集成方式示例代码。

## 工程目录结构

```
|-- com.huawei.videoeditorkit.videoeditdemo
	|--sdkdemo 原子能力SDK集成demo
	|--uidemo UI SDk集成demo
```

## 运行步骤
 - 将本代码库克隆到本地。

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html?ha_source=hms1)。
 - agconnect-services.json文件请从[华为开发者联盟](https://developer.huawei.com/consumer/cn/doc/development/Media-Guides/config-agc-0000001101108580?ha_source=hms1)网站申请获取。
 - 将agconnect-services.json文件拷贝到应用级根目录下。
 - 如果您需要使用云侧服务的能力，需要您使用agconnect-services.json里的api_key值，在应用初始化时调用MediaApplication.getInstance().setApiKey(String apiKey)。
 - 编译并且在安卓设备或模拟器上运行。

注意：

该项目中的package name不能用于申请agconnect-services.json，您可以使用自定义package name来申请agconnect-services.json。
您只需将应用级build.gradle中的applicationId修改为与所申请的agconnect-services.json相同的package name，即可体验Video Editor Kit提供的服务。

## 环境要求
推荐使用的AndroidSDK版本为21及以上，JDK版本为1.8及以上。

##  技术支持

如果您对HMS Core还处于评估阶段，可在[Reddit社区](https://www.reddit.com/r/HuaweiDevelopers/)获取关于HMS Core的最新讯息，并与其他开发者交流见解。

如果您对使用HMS示例代码有疑问，请尝试：

- 开发过程遇到问题上[Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services?tab=Votes)，在\[huawei-mobile-services]标签下提问，有华为研发专家在线一对一解决您的问题。
- 到[华为开发者论坛](https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core板块与其他开发者进行交流。

如果您在尝试示例代码中遇到问题，请向仓库提交[issue](https://github.com/HMS-Core/hms-video-editor-demo/issues)，也欢迎您提交[Pull Request](https://github.com/HMS-Core/hms-video-editor-demo/pulls)。

##  许可证

此示例代码已获得[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0)。
