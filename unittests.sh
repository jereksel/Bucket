#!/bin/bash

set -e

# Robolectric

./gradlew app:testFdroidDebugUnitTest --no-daemon --tests "**.MainViewTest"
./gradlew app:testFdroidDebugUnitTest --no-daemon --tests "**.DetailedViewTest"
./gradlew app:testFdroidDebugUnitTest --no-daemon --tests "**.InstalledViewTest"

./gradlew app:testFdroidDebugUnitTest --no-daemon --tests "com.jereksel.libresubstratum.adapters.*"

# Non-Robolectric

./gradlew app:testFdroidDebugUnitTest --no-daemon --tests "com.jereksel.libresubstratum.presenters.*"
./gradlew app:testFdroidDebugUnitTest --no-daemon --tests "com.jereksel.libresubstratum.data.*"
./gradlew app:testFdroidDebugUnitTest --no-daemon --tests "com.jereksel.libresubstratum.utils.*"
./gradlew app:testFdroidDebugUnitTest --no-daemon --tests "com.jereksel.libresubstratum.domain.*"

./gradlew sublib:reader:test --no-daemon
./gradlew sublib:compiler:test --no-daemon
./gradlew sublib:themereaderassetmanager:test --no-daemon
./gradlew sublib:compilerassetmanager:test --no-daemon

./gradlew app:jacocoTestReport --no-daemon
./gradlew sublib:reader:jacocoTestReport --no-daemon
./gradlew sublib:compiler:jacocoTestReport --no-daemon
./gradlew sublib:themereaderassetmanager:jacocoTestReportDebug --no-daemon
./gradlew sublib:compilerassetmanager:jacocoTestReportDebug --no-daemon
