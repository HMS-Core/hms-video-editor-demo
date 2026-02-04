# 华为视频剪辑服务示例代码

中文 | [English](README.md)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [运行步骤](#运行步骤)
 * [环境要求](#环境要求)
 * [许可证](#许可证)
 * [关键功能代码索引](#关键功能代码索引)


## 介绍
视频编辑服务（Video Editor Kit）是华为快速构建视频编辑能力的服务，提供两种集成方式：

- 视频编辑UI SDK，提供产品级UI界面，集成简单。
- 视频编辑原子能力SDK，提供数百个底层能力接口，包含多个AI算法能力接口，可根据业务场景灵活选择。
- 屏幕录制SDK，提供了基本屏幕录制方法，包括：开始/停止功能的录制方法和相关的配置方法。

这两种方式均提供导入、编辑、渲染、导出、媒体资源管理等一站式视频编辑能力，为您提供性能优异、简单易用、高兼容性的接口，帮助您轻松地构建应用。

您可根据使用场景选择不同的集成方式获取视频编辑能力。但您只能选择其中一种方式集成，不可同时使用。

本示例代码目的是为了介绍Video Editor Kit SDK的使用，提供了两种集成方式示例代码。

## 工程目录结构

```
|-- com.huawei.videoeditorkit.videoeditdemo
	|--sdkdemo 原子能力SDK集成demo
	|--uidemo UI SDk集成demo
	|--screenrecorddemo  屏幕录制SDK集成demo 
```

## 运行步骤
 - 将本代码库克隆到本地。

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)。
 - agconnect-services.json文件请从[华为开发者联盟](https://developer.huawei.com/consumer/cn/doc/development/Media-Guides/config-agc-0000001101108580)网站申请获取。
 - 将agconnect-services.json文件拷贝到应用级根目录下。
 - 如果您需要使用云侧服务的能力，需要您使用agconnect-services.json里的api_key值，在应用初始化时调用MediaApplication.getInstance().setApiKey(String apiKey)。
 - 编译并且在安卓设备或模拟器上运行。

注意：

该项目中的package name不能用于申请agconnect-services.json，您可以使用自定义package name来申请agconnect-services.json。
您只需将应用级build.gradle中的applicationId修改为与所申请的agconnect-services.json相同的package name，即可体验Video Editor Kit提供的服务。

## 环境要求
推荐使用的AndroidSDK版本为24及以上，JDK版本为1.8及以上。

##  技术支持

如果您对HMS Core还处于评估阶段，可在[Reddit社区](https://www.reddit.com/r/HuaweiDevelopers/)获取关于HMS Core的最新讯息，并与其他开发者交流见解。

如果您对使用HMS示例代码有疑问，请尝试：

- 开发过程遇到问题上[Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services)，在\[huawei-mobile-services]标签下提问，有华为研发专家在线一对一解决您的问题。
- 到[华为开发者论坛](https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core板块与其他开发者进行交流。

如果您在尝试示例代码中遇到问题，请向仓库提交[issue](https://github.com/HMS-Core/hms-video-editor-demo/issues)，也欢迎您提交[Pull Request](https://github.com/HMS-Core/hms-video-editor-demo/pulls)。

##  许可证

此示例代码已获得[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0)。

##  关键功能代码索引

```
|-- HomeActivity：应用首页，可跳转至创作主页(MainActivity)和使用各个独立开放的AI能力
	|--首页各个AI能力对应的处理入口：
    	|--动态照片：faceReenact(String imagePath)
    	|--一键微笑：faceSmile(String imagePath)
    	|--AI着色：aiColor(String filePath)
    	|--一键动效：timeLapse(String imagePath)
    	|--精彩片段：videoSelection(String videoPath)
    	|--目标分割：objectSeg(String photoPath)
    	|--头部分割：headSeg(String photoPath)
    	|--一键染发：拉起HairDyeingFragment，选中某个发色下载成功后，调用hairDyeing(Bitmap colormapBitmap)处理
    	|--美颜：拉起CameraActivity和CameraPreviewFragment

|-- MainActivity：创作主页，可切换剪辑页(ClipFragment)和模板首页(TemplateHomeFragment)
|-- MediaPickActivity：素材选择页，当需要从相册选取素材时会拉起该页面
|-- VideoClipsActivity：视频剪辑页面，通过开始创作导入素材或者历史草稿进入；页面上半部分是预览显示区域，中间是播放和时间线，下方是两级菜单区域
|-- MaterialEditFragment：预览区素材编辑，在预览区选中素材进行双指缩放、拖动、旋转等操作的处理入口
|-- MenuClickManager：菜单点击管理，在handlerClickEvent方法中处理用户在剪辑页面的点击菜单事件，每个点击事件会拉起一个对应的Fragment，这些Fragment继承BaseFragment抽象类，实现了initView、initObject、initData和initEvent方法，其中initView用于布局和控件的初始化，在initEvent方法中创建对应控件的监听器，响应用户在各个Fragment内的点击事件，进行对应的业务逻辑处理

|-- 剪辑功能相关：
	|--修剪时长：AssetCropFragment
	|--分割素材：AssetSplitFragment
	|--视频变速：GeneralSpeedFragment
	|--动画：AnimationPanelFragment
	|--贴纸：StickerPanelFragment
	|--文字：EditPanelFragment
	|--文字样式：EditTextStyleFragment
	|--文字动画：EditTextAnimateFragment
	|--文字气泡：EditTextBubblesFragment
	|--花字：EditTextFlowerFragment
	|--滤镜：FilterPanelFragment
	|--特效：EffectPanelFragment
	|--调节：FilterAdjustPanelView
	|--蒙版：MaskEffectFragment
	|--人脸遮挡：FaceBlockingFragment
	|--人物追踪：PersonTrackingFragment
	|--不透明度：TransparencyPanelFragment
	|--画布比例：VideoProportionFragment
	|--画布背景：CanvasBackgroundFragment
	|--音频-添加音乐：AudioPickActivity、MusicLocalFragment
	|--音频-添加音效：SoundEffectFragment
	|--音频变速：AudioSpeedFragment
	|--音量：VolumePanelFragment
	|--关键帧：KeyFrameFragment
	|--裁剪：CropNewActivity
|-- 模板功能相关：
	|--模板首页：TemplateHomeFragment，展示所有模板列表
	|--模板详情页：TemplateDetailActivity，选择某个模板后，展示具体的模板效果
	|--模板素材选择页：VideoModulePickFragment，选择某个模板，点击开始使用后拉起
	|--模板编辑页：VideoModuleEditFragment
	|--模板素材替换页面：VideoModuleReplaceFragment
|-- 导出相关：
	|--VideoExportActivity：导出主页面
	|--ExportFragment：导出前参数设置、导出过程中进度展示、导出失败处理
	|--ExportSuccessFragment：导出成功页面展示
```
