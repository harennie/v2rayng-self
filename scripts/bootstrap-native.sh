#!/usr/bin/env bash
# Download the Xray AAR and build the hev tun library used by the Android app.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
AAR_URL="https://github.com/2dust/AndroidLibXrayLite/releases/download/v26.10.10/libv2ray.aar"
AAR_PATH="$ROOT/V2rayNG/app/libs/libv2ray.aar"

if [[ -z "${NDK_HOME:-}" || ! -d "$NDK_HOME" ]]; then
  echo "Set NDK_HOME to Android NDK 30.0.16248370" >&2
  exit 1
fi

if [[ ! -f "$ROOT/hev-socks5-tunnel/third-part/yaml/Android.mk" ]]; then
  echo "hev-socks5-tunnel sources are incomplete." >&2
  exit 1
fi

mkdir -p "$ROOT/V2rayNG/app/libs"
if [[ ! -s "$AAR_PATH" ]]; then
  curl -fL --retry 3 -o "$AAR_PATH" "$AAR_URL"
fi

bash "$ROOT/compile-hevtun.sh"
mkdir -p "$ROOT/V2rayNG/app/libs"
cp -a "$ROOT/libs/." "$ROOT/V2rayNG/app/libs/"
find "$ROOT/V2rayNG/app/libs" -type f ! -name '*.so' ! -name '*.aar' -delete

echo "Ready: $AAR_PATH"
