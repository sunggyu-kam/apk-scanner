#!/bin/bash

CONTENTS_PATH=../images/`ls ../images/`/${APP_DIR_NAME}/Contents
echo CONTENTS_PATH : $CONTENTS_PATH

echo "Copy an APK file icon"
cp ${RELEASE_DIR}/ApkIcon.icns "${CONTENTS_PATH}/Resources"

if [ -x "${CONTENTS_PATH}/app" ]; then
APP_PATH="${CONTENTS_PATH}/app"
elif [ -x "${CONTENTS_PATH}/Java" ]; then
APP_PATH="${CONTENTS_PATH}/Java"
fi
echo APP_PATH : $APP_PATH

echo "Remove other platform tools"
rm -rf "${APP_PATH}/tool/linux"
rm -rf "${APP_PATH}/tool/windows"

if [ ! -e "${APP_PATH}/plugin" ]; then
echo "Make a plugin folder"
mkdir -p "${APP_PATH}/plugin"
fi
rm -f "${APP_PATH}/plugin/plugins.conf"
