#!/bin/bash

cd testthemes/OmniTheme
chmod +x gradlew
./gradlew assembleDebug
cd app/build/outputs/apk
adb install -r app-debug.apk