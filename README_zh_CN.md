[English](https://github.com/zeutd/Disintegration/blob/master/README.md) | 中文

## 用Github Action构建

这个仓库已开启Github Action CI的自动构建
1. 点击Actions，点击最近的成功构建（显示绿色勾号）。
2. 翻到最后，点击Disintegration来下载**压缩的jar文件**（无法直接放入游戏）解压文件后才可以放入游戏mod文件夹。
* （此方法可以构建安卓和电脑都能用的文件）

## 本地构建

* ### 仅电脑版方法：
1. 安装 JDK **17**。
2. 运行 `gradlew jar`。
3. mod文件将会在`build/libs` 路径。

* ### 安卓和电脑版方法：
  （不推荐，只有action builds不能用的时候再用）
1. 下载Android SDK, 解压并将 `ANDROID_HOME` 环境变量设置为它的安装路径。*官方discord服务器中`modding-general`频道中有自动设置的批处理文件。*
2. 确定你安装了API level 30， 最好是最近的版本。 (比如 30.0.1)
3. 将build-tools放入路径。 比如, 如果安装的版本是 `30.0.1`， 那就是 `$ANDROID_HOME/build-tools/30.0.1`。
4. 运行 `gradlew deploy`。 如果你做对了, mod文件将会在 `build/libs` 路径。