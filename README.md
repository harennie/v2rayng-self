# 澄

基于 [v2rayNG](https://github.com/2dust/v2rayNG) 的 Android 个人版本。上游提交 `e1b73f2`（v2rayNG 2.3.11）。协议、路由和 Xray 内核沿用上游，界面改成纸色和松绿，底部操作写成「连接 / 断开」。

包名是 `com.v2ray.ang.cheng`。它可以和官方 v2rayNG 装在同一台手机上。

这是给你自己用的分支，不是官方客户端。许可证仍是 GPL-3.0，见 `LICENSE`。上游说明留在 `docs/v2rayng-upstream-readme.md`。

## 这版改了什么

- 纸色背景、松绿主色，协议名用赭色，延迟用 celadon。
- 标题是「澄」。字体是 Instrument Serif 和 Instrument Sans。
- 选中的配置是一张圆角卡片。
- 底部左侧点一下会测试当前配置，右侧是连接或断开。
- 第一次打开默认不用系统动态取色，配色会保持。设置里仍可以打开。
- 检查更新不会去下载官方 v2rayNG 安装包。

## 先看界面

`studio` 是同一套界面的浏览器预览。它可以切换纸色和墨色、选择配置、导入分享链接。导入只读取名称、协议和主机，不保存密钥，也不转发流量。

```bash
cd studio
npm install
npm run dev -- --hostname 127.0.0.1 --port 43127
```

打开 <http://127.0.0.1:43127>。

## 装到 Android 手机

需要 JDK 21、Android SDK 37、Build-Tools 37.0.0、NDK `30.0.16248370`。

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export NDK_HOME="$ANDROID_HOME/ndk/30.0.16248370"
./scripts/bootstrap-native.sh
cd V2rayNG
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew :app:assemblePlaystoreDebug -PABI_FILTERS=arm64-v8a
```

调试包在：

`V2rayNG/app/build/outputs/apk/playstore/debug/Cheng_1.0.0_arm64-v8a.apk`

也可以用 Android Studio 打开 `V2rayNG/` 目录。`bootstrap-native.sh` 会先准备这两个本地文件，它们不进 git：

- `V2rayNG/app/libs/libv2ray.aar`，来自 [AndroidLibXrayLite v26.10.10](https://github.com/2dust/AndroidLibXrayLite/releases/tag/v26.10.10)，对应上游子模块 `1f3e2c5`
- `hev-socks5-tunnel` 编出的 `libhev-socks5-tunnel.so` 和 `libhevsockstun.so`

手机是 arm64 时加上 `-PABI_FILTERS=arm64-v8a`。要打出全部架构，去掉这个参数。

## 许可

程序本身是 GPL-3.0。修改后再分发时需要附上源代码。

- v2rayNG 与 Xray 内核的版权归各自贡献者。
- `hev-socks5-tunnel` 的源码和许可证在 `hev-socks5-tunnel/`，固定在提交 `d9dca26`。
- Instrument Sans 与 Instrument Serif 使用 SIL Open Font License，全文在 `third_party/instrument/OFL.txt`。
