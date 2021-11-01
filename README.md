# HUAWEI Video Editor Kit Sample

English | [中文](README_ZH.md)

## Table of Contents

* [Introduction](#introduction)
* [Project directory structure](#project-directory-structure)
* [Running Procedure](#running-procedure)
* [Supported environment](#supported-environment)
* [License](#license)


## Introduction
Huawei Video Editor Kit (Video Editor Kit) provided by Huawei to quickly build video editing capabilities. It provides two integration modes.

- Video editor UI SDK, which provides product-level UIs and easy integration.

- Video editor atomic capability SDK, which provides hundreds of underlying capability interfaces, including multiple AI algorithm capability interfaces, which can be flexibly selected based on service scenarios.

Both modes provide one-stop video editing capabilities, such as import, editing, rendering, export, and media resource management. These two modes provide high-performance, easy-to-use, and highly compatible interfaces, helping you easily build applications.

## Project directory structure

```
|-- com.huawei.videoeditorkit.videoeditdemo
	|--sdkdemo  atomic capability SDK demo
	|--uidemo   UI SDk demo
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
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services?tab=Votes) is the best place for any programming questions. Be sure to tag your question with 
  `huawei-mobile-services`.
- [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001) HMS Core Module is great for general questions, or seeking recommendations and opinions.

If you run into a bug in our samples, please submit an [issue](https://github.com/HMS-Core/hms-video-editor-demo/issues) to the Repository. Even better you can submit a [Pull Request](https://github.com/HMS-Core/hms-video-editor-demo/pulls) with a fix.

## License

This sample code has obtained [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
