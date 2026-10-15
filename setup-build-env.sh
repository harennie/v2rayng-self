#!/usr/bin/env bash
# Download JDK 21 and the Android SDK packages this project builds with.
# Files land in .build-env/ next to this script. local.properties points Gradle at that SDK.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_DIR="$ROOT/.build-env"
SDK_DIR="$ENV_DIR/android-sdk"
JDK_DIR="$ENV_DIR/jdk"
CMDLINE_REV="14742923"
PACKAGES=(
  "platforms;android-37.0"
  "build-tools;37.0.0"
  "platform-tools"
)

java_major() {
  if ! command -v java >/dev/null 2>&1; then
    echo 0
    return
  fi
  java -version 2>&1 | awk -F'"' '/version/ {print $2; exit}' | cut -d. -f1
}

host_os() {
  case "$(uname -s)" in
    Linux) echo linux ;;
    Darwin) echo mac ;;
    *)
      echo "这个脚本只覆盖 macOS 和 Linux。Windows 请运行 setup-build-env.ps1" >&2
      exit 1
      ;;
  esac
}

host_arch() {
  case "$(uname -m)" in
    x86_64|amd64) echo x64 ;;
    aarch64|arm64) echo aarch64 ;;
    *)
      echo "不支持的 CPU 架构: $(uname -m)" >&2
      exit 1
      ;;
  esac
}

mkdir -p "$ENV_DIR"

if [ "$(java_major)" -ge 21 ]; then
  echo "已有 JDK $(java_major)，跳过 JDK 下载"
else
  os="$(host_os)"
  arch="$(host_arch)"
  echo "下载 Temurin JDK 21 ($os/$arch)"
  tmp="$(mktemp)"
  curl -fL --retry 3 -o "$tmp" \
    "https://api.adoptium.net/v3/binary/latest/21/ga/${os}/${arch}/jdk/hotspot/normal/eclipse?project=jdk"
  rm -rf "$JDK_DIR"
  mkdir -p "$JDK_DIR"
  tar -xzf "$tmp" -C "$JDK_DIR" --strip-components=1
  rm -f "$tmp"
  export JAVA_HOME="$JDK_DIR"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

os="$(host_os)"
if [ ! -x "$SDK_DIR/cmdline-tools/latest/bin/sdkmanager" ]; then
  echo "下载 Android 命令行工具"
  tmp="$(mktemp)"
  curl -fL --retry 3 -o "$tmp" \
    "https://dl.google.com/android/repository/commandlinetools-${os}-${CMDLINE_REV}_latest.zip"
  rm -rf "$SDK_DIR/cmdline-tools"
  mkdir -p "$SDK_DIR/cmdline-tools"
  unzip -q "$tmp" -d "$SDK_DIR/cmdline-tools-tmp"
  rm -f "$tmp"
  if [ -d "$SDK_DIR/cmdline-tools-tmp/cmdline-tools" ]; then
    mv "$SDK_DIR/cmdline-tools-tmp/cmdline-tools" "$SDK_DIR/cmdline-tools/latest"
    rm -rf "$SDK_DIR/cmdline-tools-tmp"
  else
    mv "$SDK_DIR/cmdline-tools-tmp" "$SDK_DIR/cmdline-tools/latest"
  fi
fi

SDKMANAGER="$SDK_DIR/cmdline-tools/latest/bin/sdkmanager"
echo "安装 Android SDK 组件: ${PACKAGES[*]}"
set +o pipefail
yes | "$SDKMANAGER" --sdk_root="$SDK_DIR" --licenses >/dev/null || true
set -o pipefail
"$SDKMANAGER" --sdk_root="$SDK_DIR" "${PACKAGES[@]}"

printf 'sdk.dir=%s\n' "$SDK_DIR" > "$ROOT/V2rayNG/local.properties"
echo "构建环境已放在 $ENV_DIR"
echo "下一步: cd V2rayNG && ./gradlew :app:assemblePlaystoreDebug -PABI_FILTERS=arm64-v8a"
