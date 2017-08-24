#!/bin/bash

emulator -avd circleci-android24 -no-window &
circle-android wait-for-boot
adb shell input keyevent 82
./gradlew connectedAndroidTest
