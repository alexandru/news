#!/bin/sh

set -e

BUILD_DIR="/tmp/news"
rm -rf "$BUILD_DIR"

echo "Detecting JBANG..."
if ! command -v jbang >/dev/null; then
    JBANG_PATH="$HOME/.jbang/bin/jbang"
    if [ ! -x "$JBANG_PATH" ]; then
        echo "ERROR: JBang executable not found!" >&2
        exit 2
    fi
else
    JBANG_PATH="$(which jbang)"
fi

echo "Cloning repository..."
git clone "https://github.com/alexandru/news.git" "$BUILD_DIR" -b gh-pages

echo "Generating releases.xml..."
"$JBANG_PATH" --quiet ./generate.kt >"$BUILD_DIR/releases.xml"

echo "Pushing changes..."
cd "$BUILD_DIR"
git add .
git config user.name "Alexandru Nedelcu"
git config user.email "noreply@alexn.org"
git commit -m "Update releases.xml"
git push --quiet "https://alexandru:$GH_TOKEN@github.com/alexandru/news.git" gh-pages:gh-pages

echo "Cleaning up..."
cd ~
rm -rf "$BUILD_DIR"
