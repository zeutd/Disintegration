English | [ÖÐÎÄ](README_zh_CN.md)

+ **Discord Server**: https://discord.gg/XGq7NBz8RZ
+ **QQ**: 676206144

![](images/qq-QRcode.png)

## Action Builds

This repository is set up with Github Actions CI to automatically build the mod for every commit.
1. Check the "Actions" tab on your repository page. Select the most recent commit in the list. If it completed successfully, there should be a download link under the "Artifacts" section.
2. Click the download link. This will download a **zipped jar** - **not** the jar file itself [2]! Unzip this file and import the jar contained within in Mindustry. This version should work both on Android and Desktop.

## Locally Builds

* ### Build for only desktop:
1. Install JDK **17**.
2. Run `gradlew jar` [1].
3. Your mod jar will be in the `build/libs` directory. **Only use this version for testing on desktop. It will not work with Android.**
   To build an Android-compatible version, you need the Android SDK. You can either let Github Actions handle this, or set it up yourself. See steps below.


* ### Build for both desktop and android:
  (not recommend, only use if action builds not working)

1. Download the Android SDK, unzip it and set the `ANDROID_HOME` environment variable to its location. *in official discord server, `modding-general` has the batch file to set the environment.*
2. Make sure you have API level 30 installed, as well as any recent version of build tools (e.g. 30.0.1)
3. Add a build-tools folder to your PATH. For example, if you have `30.0.1` installed, that would be `$ANDROID_HOME/build-tools/30.0.1`.
4. Run `gradlew deploy`. If you did everything correctlly, this will create a jar file in the `build/libs` directory that can be run on both Android and desktop.
* Building locally takes more time to set up, but shouldn't be a problem if you've done Android development before.
