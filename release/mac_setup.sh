#!/bin/bash

# purpose: this script creates a signed ".app" application directory for the
#          ACME application
#
# known assumptions for this script:
#   - the application jar files are in the 'lib' directory
#   - the icon file is in the current directory
#   - the necessary resource files are in the 'resources' directory (.ini, etc.)
#   - the necessary apple certificates are installed on the Mac this script is run on
#
# see this URL for details about the `javapackager` command:
# https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javapackager.html

if [[ $OSTYPE != 'darwin'* ]]; then
echo "This script support only MacOS"
exit 1;
fi

if [ -n "${FILE_VERSION}" ]; then
  FILE_VERSION="${FILE_VERSION}_"
fi

# necessary variables

export RELEASE_DIR=`dirname "$0"`
if [[ $RELEASE_DIR != '/'* ]]; then
export RELEASE_DIR=`pwd`/${RELEASE_DIR}
fi

if [ ! -x "${RELEASE_DIR}/ApkScanner.jar" ]; then
cp ${RELEASE_DIR}/ApkScanner-*.jar ${RELEASE_DIR}/ApkScanner.jar
fi

export APP_DIR_NAME="APK Scanner.app"

mkdir -p "${RELEASE_DIR}/plugin"

echo JAVA_HOME : $JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
JAVA_HOME=`/usr/libexec/java_home -v 1.8`
echo JAVA_HOME_1.8 : $JAVA_HOME
fi

if [ -x "$JAVA_HOME/bin/javapackager" ]; then
# Lecacy approach using javapackager (deprecated in Java 11 and removed in Java 14)
# javapackager command notes:
#   - `-native image` creates a ".app" file (as opposed to DMG or other)
#   - `-name` is used as the app name in the menubar if you don't specify "-Bmac.CFBundleName"
#   - oracle notes says "use cms for desktop apps"
#   - `v` is for verbose mode. remove it if you don't want/need to see all of the output

# (1) create and sign the ".app" directory structure. this command creates the
#     "./release/bundles/ACME.app" directory.
${JAVA_HOME}/bin/javapackager \
  -deploy -Bruntime=${JAVA_HOME} \
  -native dmg \
  -outdir "${RELEASE_DIR}" \
  -outfile "${APP_DIR_NAME}" \
  -srcdir "${RELEASE_DIR}" \
  -srcfiles ApkScanner.jar:data:lib:security:tool:plugin \
  -appclass com.apkscanner.Main \
  -name "APK Scanner" \
  -title "APK Scanner" \
  -vendor "APK Spectrum" \
  -Bicon="${RELEASE_DIR}/AppIcon.icns" \
  -BdropinResourcesRoot="${RELEASE_DIR}" \
  -v


## if native is image, use below commands.
# (2b) copy *all* resource files into the ".app" directory
# cp ApkIcon.icns "${RELEASE_DIR}/bundles/${APP_DIR_NAME}/Contents/Resources"

# rm -rf "${RELEASE_DIR}/bundles/${APP_DIR_NAME}/Contents/Java/tool/linux"
# rm -rf "${RELEASE_DIR}/bundles/${APP_DIR_NAME}/Contents/Java/tool/windows"
# if [ -e plugin/plugins.conf ]; then
# rm -f "${RELEASE_DIR}/bundles/${APP_DIR_NAME}/Contents/Java/plugin/plugins.conf"
# fi

else
# jpackage approach (recommended for Java 14+)
JAVA_HOME=`/usr/libexec/java_home -v 17`
echo JAVA_HOME after : $JAVA_HOME

mkdir -p "${RELEASE_DIR}/bundles/"
cp "${RELEASE_DIR}/ApkScanner.jar" "${RELEASE_DIR}/bundles/"
cp -r "${RELEASE_DIR}/data" "${RELEASE_DIR}/bundles/"
cp -r "${RELEASE_DIR}/lib" "${RELEASE_DIR}/bundles/"
cp -r "${RELEASE_DIR}/security" "${RELEASE_DIR}/bundles/"
cp -r "${RELEASE_DIR}/tool" "${RELEASE_DIR}/bundles/"
cp -r "${RELEASE_DIR}/plugin" "${RELEASE_DIR}/bundles/"

## Build a custom runtime image with jlink and package with jpackage
## (1) detect required modules (best-effort) using jdeps
echo "Running jpackage to create DMG without bundled runtime (requires system JRE)..."
JPACKAGE_BIN="${JAVA_HOME}/bin/jpackage"
if [ ! -x "${JPACKAGE_BIN}" ]; then
  # try to find jpackage on PATH
  JPACKAGE_BIN=`which jpackage 2>/dev/null || true`
fi
if [ -z "${JPACKAGE_BIN}" ] || [ ! -x "${JPACKAGE_BIN}" ]; then
  echo "jpackage not found; cannot create DMG on this runner. Please use a JDK that provides jpackage (e.g., Temurin 16+)."
  exit 1
fi

${JPACKAGE_BIN} \
  --type dmg \
  --input "${RELEASE_DIR}/bundles/" \
  --dest "${RELEASE_DIR}/bundles/" \
  --name "APK Scanner" \
  --main-jar ApkScanner.jar \
  --main-class com.apkscanner.Main \
  --icon "${RELEASE_DIR}/AppIcon.icns" \
  --resource-dir "${RELEASE_DIR}/package/macosx/" \
  --app-version "1.0" \
  --vendor "APK Spectrum" \
  --verbose

fi # if [ -x "$JAVA_HOME/bin/javapackager" ]

# Move produced DMG to the original naming scheme (best-effort)
for f in "${RELEASE_DIR}/bundles"/*.dmg; do
  echo "Checking for DMG file: $f"
  if [ -f "$f" ]; then
    mv "$f" "${RELEASE_DIR}/APKScanner_${FILE_VERSION}mac.dmg"
    echo output : "${RELEASE_DIR}/APKScanner_${FILE_VERSION}mac.dmg"
    break
  fi
done

# cleanup
rm -rf "${RELEASE_DIR}/bundles"
