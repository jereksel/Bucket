#!/bin/bash

emulator -avd circleci-android22 -no-window &
circle-android wait-for-boot
./preparefortests.sh
adb shell input keyevent 82
./gradlew createDebugCoverageReport
