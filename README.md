# HarenRay

HarenRay 是 [MikuRay](https://github.com/HatsuneMikuUwU/MikuRay) 的独立修改版。MikuRay 基于 [v2rayNG](https://github.com/2dust/v2rayNG)，代理核心由 [Xray](https://github.com/XTLS/Xray-core) 提供。感谢上游项目和贡献者，项目源码按 [GPL-3.0](LICENSE) 发布。

HarenRay 自 2026-10-19 起调整了应用名称、包名、主题和界面。2026-11-21 发布的 1.0.4 更新了 Xray 核心，并修复 Hysteria2 导入和 DNS 路由。

包名是 `com.v2ray.haren`，可以和官方 v2rayNG 同时安装。启动器名称是 HarenRay。

---

## 在自己电脑上编译

`V2rayNG/app/libs/` 里已经带好 `libv2ray.aar` 和 HEV 的原生库，不用再编 NDK 或 Go。

克隆后在仓库根目录执行一次。脚本会把 JDK 21 和 Android SDK 下载到本机的 `.build-env/`，并写好 `V2rayNG/local.properties`。

Windows PowerShell：

```powershell
powershell -ExecutionPolicy Bypass -File .\setup-build-env.ps1
cd V2rayNG
.\gradlew.bat :app:assembleRelease "-PABI_FILTERS=arm64-v8a"
```

macOS 或 Linux：

```bash
bash setup-build-env.sh
cd V2rayNG
./gradlew :app:assembleRelease -PABI_FILTERS=arm64-v8a
```

安装包在 `V2rayNG/app/build/outputs/apk/release/`。配置 Gradle 属性 `KS_PATH`、`KS_ALIAS`、`KS_PASS` 和 `KEY_PASS` 来生成签名的发布包。可以用同名的 `ORG_GRADLE_PROJECT_` 前缀环境变量提供这些值。未配置发布密钥时，发布包不签名。

---

## Download / 下载

在 [HarenRay Releases](https://github.com/harennie/v2rayng-self/releases) 下载 APK。每个版本的发布说明都附有对应的源码标签。源码的 Git 子模块记录了 AndroidLibXrayLite 和 HEV 的版本，克隆时可加上 `--recurse-submodules`。

> [!TIP]
> v2rayNG is the mobile version. For the desktop version, please visit the v2rayN \
> v2rayNG 是手机版，电脑版请访问 v2rayN
>
> https://github.com/2dust/v2rayN

---

### Geoip and Geosite

- geoip.dat and geosite.dat files are in `Android/data/com.v2ray.haren/files/assets` (path may differ on some Android device)
- download feature will get enhanced version in this [repo](https://github.com/Loyalsoldier/v2ray-rules-dat) (note: it needs a working proxy)
- latest official [domain list](https://github.com/Loyalsoldier/v2ray-rules-dat) and [ip list](https://github.com/Loyalsoldier/geoip) can be imported manually
- possible to use a third-party dat file in the same folder, like [h2y](https://guide.v2fly.org/routing/sitedata.html#%E5%A4%96%E7%BD%AE%E7%9A%84%E5%9F%9F%E5%90%8D%E6%96%87%E4%BB%B6)

More in our [wiki](https://github.com/2dust/v2rayNG/wiki)

### Geoip 与 Geosite

- geoip.dat 和 geosite.dat 文件位于 `Android/data/com.v2ray.haren/files/assets`（部分设备路径可能不同）
- 下载功能将获取该 [仓库](https://github.com/Loyalsoldier/v2ray-rules-dat) 中的增强版本（注意：此功能需要一个可用的代理）
- 最新官方 [域名列表](https://github.com/Loyalsoldier/v2ray-rules-dat) 和 [IP 列表](https://github.com/Loyalsoldier/geoip) 可手动导入
- 也可在同一文件夹中使用第三方 dat 文件，例如 [h2y](https://guide.v2fly.org/routing/sitedata.html#%E5%A4%96%E7%BD%AE%E7%9A%84%E5%9F%9F%E5%90%8D%E6%96%87%E4%BB%B6)

更多内容请见我们的 [wiki](https://github.com/2dust/v2rayNG/wiki)

---

## Development guide / 开发指南

### Note

- Android project under the V2rayNG folder can be compiled directly in Android Studio, or using the Gradle wrapper. But the v2ray core inside the aar is (probably) outdated.
- The aar can be compiled from the Golang project [AndroidLibV2rayLite](https://github.com/2dust/AndroidLibV2rayLite) or [AndroidLibXrayLite](https://github.com/2dust/AndroidLibXrayLite). For a quick start, read the guides for [Go Mobile](https://github.com/golang/go/wiki/Mobile) and [Makefiles for Go Developers](https://tutorialedge.net/golang/makefiles-for-go-developers/).
- v2rayNG can run on Android Emulators. For WSA, VPN permission needs to be granted via `appops set [package name] ACTIVATE_VPN allow`.

### 提示

- V2rayNG 文件夹下的 Android 项目可直接在 Android Studio 中编译，或使用 Gradle wrapper 编译。但 aar 内置的 v2ray core（可能）已过时。
- aar 可由 Golang 项目 [AndroidLibV2rayLite](https://github.com/2dust/AndroidLibV2rayLite) 或 [AndroidLibXrayLite](https://github.com/2dust/AndroidLibXrayLite) 编译而成。快速入门可参考 [Go Mobile](https://github.com/golang/go/wiki/Mobile) 指南和 [Makefiles for Go Developers](https://tutorialedge.net/golang/makefiles-for-go-developers/)。
- v2rayNG 可在 Android 模拟器上运行。对于 WSA，需要通过 `appops set [package name] ACTIVATE_VPN allow` 授予 VPN 权限。

---


## APK 签名校验

从 1.0.4 起，HarenRay 使用专用发布证书。用 Android SDK 的 `apksigner verify --print-certs 文件.apk` 核对签名证书的 SHA-256 指纹。

```text
5c4862cb236ff7c8a7999dc41c824d4598461e388600861d1226b933c1e82835
```

较早的版本使用了不同的证书。迁移到 1.0.4 时，请先导出 `.mikubackup`，再卸载旧版、安装新版并还原。

---

## Community / 社区

Telegram Group / Telegram 群组：

[https://t.me/v2rayN](https://t.me/v2rayN)

Telegram Channel / Telegram 频道：

[https://t.me/github_2dust](https://t.me/github_2dust)
