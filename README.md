# HUAWEI Video Editor Kit Sample

English | [中文](README_ZH.md)

## Table of Contents

* [Introduction](#introduction)
* [Project directory structure](#project-directory-structure)
* [Running Procedure](#running-procedure)
* [Supported environment](#supported-environment)
* [License](#license)
* [Code indexes for key functions](#code-indexes-for-key-functions)


## Introduction
Huawei Video Editor Kit (Video Editor Kit) provided by Huawei to quickly build video editing capabilities. It provides two integration modes.

- Video editor UI SDK, which provides product-level UIs and easy integration.

- Video editor atomic capability SDK, which provides hundreds of underlying capability interfaces, including multiple AI algorithm capability interfaces, which can be flexibly selected based on service scenarios.

- Screen recorder demo provide basic usage of screen recording. There are functions demonstrating usage of start/stop recording and setting configuration.

- Template creation SDK, which provides the capability of creating and uploading templates.

Both modes provide one-stop video editing capabilities, such as import, editing, rendering, export, and media resource management. These two modes provide high-performance, easy-to-use, and highly compatible interfaces, helping you easily build applications.

## Project directory structure

```
|-- com.huawei.videoeditorkit.videoeditdemo
	|--sdkdemo  atomic capability SDK demo
	|--uidemo   UI SDk demo
	|--screenrecorddemo  Screen Recorder SDK Demo
	|--templatetooldemo  Template Creation SDK Demo
```

## Running Procedure
- Clone the code base to the local host.

- If you haven't already registered as a developer, register and create an app on [AppGalleryConnect](https://developer.huawei.com/consumer/en/service/josp/agc/index.html).
- Obtain the agconnect-services.json file from [HUAWEI Developers]([https://developer.huawei.com/consumer/en/doc/development/Media-Guides/config-agc-0000001101108580](javascript:;)).
- Place the agconnect-services.json file in the root directory of the demo.
- If you need to use cloud-side service capabilities, you need to use the api_key value in agconnect-services.json to call MediaApplication.getInstance().setApiKey(String apiKey) during application initialization.
- Compile and run on an Android device or emulator.

Note:

The package name in this project cannot be used to apply for agconnect-services.json. You can use a customized package name to apply for agconnect-services.json.
You only need to change applicationId in application-level build.gradle to the same package name as the applied agconnect-services.json to experience Video Editor Kit services.

## Supported Environment
Android SDK 21 or later and JDK 1.8 or later are recommended.

## Question or issues
If you want to evaluate more about HMS Core,
[r/HMSCore on Reddit](https://www.reddit.com/r/HuaweiDevelopers/) is for you to keep up with latest news about HMS Core, and to exchange insights with other developers.

If you have questions about how to use HMS samples, try the following options:
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) is the best place for any programming questions. Be sure to tag your question with 
  `huawei-mobile-services`.
- [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001) HMS Core Module is great for general questions, or seeking recommendations and opinions.

If you run into a bug in our samples, please submit an [issue](https://github.com/HMS-Core/hms-video-editor-demo/issues) to the Repository. Even better you can submit a [Pull Request](https://github.com/HMS-Core/hms-video-editor-demo/pulls) with a fix.

## License

This sample code has obtained [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).

## Code indexes for key functions

```
|-- HomeActivity: App home page, which can be redirected to the video creation page (MainActivity) and the open, independent AI capabilities.
	|-- Entries to AI capabilities on the home screen:
		|-- faceReenact(String imagePath): moving picture
		|-- faceSmile(String imagePath): auto-smile
		|-- aiColor(String filePath): AI color
		|-- timeLapse(String imagePath): auto-timelapse
		|-- videoSelection(String videoPath): highlight
		|-- objectSeg(String photoPath): object segmentation
		|-- headSeg(String photoPath): head segmentation
		|-- Color hair. **HairDyeingFragment** is launched. After a hair color is selected and successfully downloaded, **hairDyeing(Bitmap colormapBitmap)** is called for processing.
		|-- Beauty. **CameraActivity** and **CameraPreviewFragment** are launched.

|-- MainActivity: video creation page, which can be switched to the editing screen (**ClipFragment**) and template home screen (**TemplateHomeFragment**).
|-- MediaPickActivity: material selection screen. When there is a need to select materials from the album, this screen will be launched.
|-- VideoClipsActivity: video editing screen, which can be entered via material import for creation or a draft. The upper part of the screen is the preview area, the middle part contains playback operations and the timeline, and the lower part is the two-level menu area.
|-- MaterialEditFragment: preview area, where a material can be edited. It is the entry to the zooming in or out using two fingers, dragging, rotating, and other operations on the material selected on the preview area.
|-- MenuClickManager: manager for menu clicks. The menu click event on the video editing screen is processed in the **handlerClickEvent** method. Each click event launches a corresponding fragment. All fragments inherit the **BaseFragment** abstract class, implementing the following methods: **initView**, **initObject**, **initData**, and **initEvent**. **initView** initializes the layout and component. The listener for the component is created in the **initEvent** method, to respond to the click event in each fragment and conduct relevant service logic processing.

|-- Editing-related functions:
	|-- AssetCropFragment: cropping
	|-- AssetSplitFragment: splitting
	|-- GeneralSpeedFragment: video playback speed adjustment
	|-- AnimationPanelFragment: animation
	|-- StickerPanelFragment: sticker
	|-- EditPanelFragment: text
	|-- EditTextStyleFragment: text style
	|-- EditTextAnimateFragment: text animation
	|-- EditTextBubblesFragment: text bubble
	|-- EditTextFlowerFragment: artistic font
	|-- FilterPanelFragment: filter
	|-- EffectPanelFragment: special effect
	|-- FilterAdjustPanelView: adjustment
	|-- MaskEffectFragment: mask
	|-- FaceBlockingFragment: face mask
	|-- PersonTrackingFragment: track person
	|-- TransparencyPanelFragment: transparency
	|-- VideoProportionFragment: canvas ratio
	|-- CanvasBackgroundFragment: canvas background
	|-- AudioPickActivity and MusicLocalFragment: adding music
	|-- SoundEffectFragment: adding a sound effect
	|-- AudioSpeedFragment: audio playback speed adjustment
	|-- VolumePanelFragment: volume
	|-- KeyFrameFragment: key frame
	|-- CropNewActivity: cropping
|-- Template-related functions:
	|-- TemplateHomeFragment: template home screen, which shows all template lists
	|-- TemplateDetailActivity: template details screen, which shows the effect of a template after it is selected
	|-- VideoModulePickFragment: template selection screen, which is launched after a template is selected and the button for using it is tapped
	|-- VideoModuleEditFragment: template editing screen
	|-- VideoModuleReplaceFragment: template replacement screen
|-- Export-related functions:
	|--VideoExportActivity: main screen for export
	|--ExportFragment: parameter configuration before export, export progress, and export failure
	|--ExportSuccessFragment: export success
```
