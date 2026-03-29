#!/usr/bin/env bash
set -euo pipefail

WEBDAV_URL="${WEBDAV_URL:-https://dav.jianguoyun.com/dav}"
WEBDAV_USER="${WEBDAV_USER:-}"
WEBDAV_PASS="${WEBDAV_PASS:-}"

if [[ -z "$WEBDAV_USER" || -z "$WEBDAV_PASS" ]]; then
  echo "请通过环境变量提供 WEBDAV_USER / WEBDAV_PASS" >&2
  exit 1
fi

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
DIST_DIR="$ROOT_DIR/dist"
TMP_DIR="$ROOT_DIR/.package_tmp"
mkdir -p "$DIST_DIR"
rm -rf "$TMP_DIR"
mkdir -p "$TMP_DIR"

variants=(
  "v1_full:完整多模块版本"
  "v2_hydrogen:氢动态壁纸主打版本"
  "v3_text:文字生成壁纸主打版本"
  "v4_frog_name:旅行青蛙+姓氏壁纸主打版本"
)

base_url="${WEBDAV_URL%/}"
for item in "${variants[@]}"; do
  key="${item%%:*}"
  desc="${item#*:}"
  work="$TMP_DIR/$key"
  rsync -a --exclude '.git' --exclude '.package_tmp' --exclude 'dist' --exclude '*.zip' "$ROOT_DIR/" "$work/"
  cat > "$work/BUILD_VARIANT.md" <<TXT
# $key
$desc
TXT
  zip_path="$DIST_DIR/bizhi_${key}.zip"
  (cd "$work" && zip -qr "$zip_path" .)
  curl --fail-with-body -u "$WEBDAV_USER:$WEBDAV_PASS" -T "$zip_path" "$base_url/bizhi_${key}.zip"
  echo "uploaded: bizhi_${key}.zip"
done

rm -rf "$TMP_DIR"
