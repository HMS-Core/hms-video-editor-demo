# Video Editor Sample
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](README_ZH.md)

## Table of Contents

* [Introduction](#introduction)
* [Project directory structure](#project-directory-structure)
* [Running Procedure](#running-procedure)
* [Supported environment](#supported-environment)
* [License](#license)


## Introduction
Huawei Video Editor Kit (Video Editor Kit) provides full video editing functions to provide high-quality experience for developers to use video editing capabilities to develop various applications.This sample code is used to describe how to use the Video Editor Kit SDK.

- Allows your users to delete videos and images in batches, import both videos and images at a time, adjust the sequence and duration of video clips, and easily access the editing screen. Videos with a resolution of 1080p or lower are recommended for better experience. 
- Supports basic editing operations, including video splitting/deletion, volume/aspect ratio/playback speed adjustment, adding canvases/animations/masks, rotation, cropping, mirroring, copying, and replacement. 
- Allows your users to customize filters by modifying parameters like brightness, contrast, saturation, hue, color temperature, and sharpening. 
- Supports picture-in-picture. Your users can overlay a video into another video so that the video added will appear in a small window floating on the original video in full-screen mode. 
- Supports background music, sound effects, and recording. 
- Allows your users to export videos in MP4 format, extract any frame from a video or import a image from the photo albums as its cover, and set the resolution of the exported video (1080p at most). 
- Offers diverse material libraries which are constantly enriched and updated and some of which will be free to you. 

## Project directory structure

```
|-- com.huawei.videoeditorkit.videoeditdemo
	|-- Activity
		|-- MainActivity // Video Editor UI SDK entry
		|-- SettingActivity // Basic Demo information
```



## Running Procedure
- Clone the code base to the local host.

- If you haven't already registered as a developer, register and create an app on [AppGalleryConnect](https://developer.huawei.com/consumer/en/service/josp/agc/index.html).
- Obtain the agconnect-services.json file from [HUAWEI Developers](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050990353).
- Replace the sample-agconnect-services.json file in the project.
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