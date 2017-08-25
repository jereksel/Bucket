#!/bin/bash

git submodule update --init --recursive
cd testthemes/OmniTheme
chmod +x gradlew
./gradlew assembleDebug
cd app/build/outputs/apk
adb install -r app-debug.apk
