#!/bin/bash

emulator -avd circleci-android24 -no-window &
circle-android wait-for-boot
./preparefortests.sh
adb shell screencap -p | perl -pe 's/\x0D\x0A/\x0A/g' > $CIRCLE_ARTIFACTS/screen-$(date +"%T").png
adb shell input keyevent 82 &
adb shell screencap -p | perl -pe 's/\x0D\x0A/\x0A/g' > $CIRCLE_ARTIFACTS/screen-$(date +"%T").png
./gradlew connectedAndroidTest
