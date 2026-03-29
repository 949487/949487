#!/usr/bin/env bash
set -euo pipefail

TARGET_REPO="${TARGET_REPO:-https://github.com/949487/walls.git}"
TARGET_BRANCH="${TARGET_BRANCH:-main}"
GH_USER="${GH_USER:-}"
GH_TOKEN="${GH_TOKEN:-}"

if [[ -z "$GH_USER" || -z "$GH_TOKEN" ]]; then
  echo "请提供 GH_USER 和 GH_TOKEN 环境变量" >&2
  exit 1
fi

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
DIST_DIR="$ROOT_DIR/dist"
TMP_DIR="$ROOT_DIR/.github_upload_tmp"
PKG_TMP="$ROOT_DIR/.package_tmp"

mkdir -p "$DIST_DIR"
rm -rf "$PKG_TMP" "$TMP_DIR"
mkdir -p "$PKG_TMP"

variants=(
  "v1_full:完整多模块版本"
  "v2_hydrogen:氢动态壁纸主打版本"
  "v3_text:文字生成壁纸主打版本"
  "v4_frog_name:旅行青蛙+姓氏壁纸主打版本"
)

for item in "${variants[@]}"; do
  key="${item%%:*}"
  desc="${item#*:}"
  work="$PKG_TMP/$key"
  rsync -a --exclude '.git' --exclude '.package_tmp' --exclude '.github_upload_tmp' --exclude 'dist' --exclude '*.zip' "$ROOT_DIR/" "$work/"
  cat > "$work/BUILD_VARIANT.md" <<TXT
# $key
$desc
TXT
  (cd "$work" && zip -qr "$DIST_DIR/bizhi_${key}.zip" .)
done

repo_auth_url="https://${GH_USER}:${GH_TOKEN}@github.com/949487/walls.git"

git clone "$repo_auth_url" "$TMP_DIR"
cd "$TMP_DIR"
git checkout "$TARGET_BRANCH" || git checkout -b "$TARGET_BRANCH"
mkdir -p uploads
cp "$DIST_DIR"/*.zip uploads/

git add uploads/*.zip
if git diff --cached --quiet; then
  echo "没有新文件需要提交"
  exit 0
fi

git config user.name "${GH_USER}"
git config user.email "${GH_USER}@users.noreply.github.com"
git commit -m "chore: upload 4 Bizhi zip packages"
git push origin "$TARGET_BRANCH"

cd "$ROOT_DIR"
rm -rf "$PKG_TMP" "$TMP_DIR"
