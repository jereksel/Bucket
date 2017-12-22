#!/bin/bash

export GRADLE_OPTS="$GRADLE_OPTS -Dorg.gradle.daemon=false"

# To download Robolectric libs
./gradlew app:testFdroidDebugUnitTest --tests "**.BaseRobolectricTest"

set -e

# Robolectric

./gradlew app:testFdroidDebugUnitTest --tests "**.MainViewTest"
./gradlew app:testFdroidDebugUnitTest --tests "**.MainViewMVVMTest"
./gradlew app:testFdroidDebugUnitTest --tests "**.MainViewTest2"
./gradlew app:testFdroidDebugUnitTest --tests "**.DetailedViewTest"
./gradlew app:testFdroidDebugUnitTest --tests "**.InstalledViewTest"
./gradlew app:testFdroidDebugUnitTest --tests "**.PrioritiesViewTest"
./gradlew app:testFdroidDebugUnitTest --tests "**.PrioritiesDetailViewTest"

./gradlew app:testFdroidDebugUnitTest --tests "com.jereksel.libresubstratum.adapters.*"

# Non-Robolectric

./gradlew app:testFdroidDebugUnitTest --tests "**.MainViewViewModelTest"
./gradlew app:testFdroidDebugUnitTest --tests "com.jereksel.libresubstratum.presenters.*"
./gradlew app:testFdroidDebugUnitTest --tests "com.jereksel.libresubstratum.data.*"
./gradlew app:testFdroidDebugUnitTest --tests "com.jereksel.libresubstratum.utils.*"
./gradlew app:testFdroidDebugUnitTest --tests "com.jereksel.libresubstratum.domain.*"

./gradlew sublib:reader:test
./gradlew sublib:compiler:test
./gradlew sublib:themereaderassetmanager:test
./gradlew sublib:compilerassetmanager:test

./gradlew app:jacocoTestReport
./gradlew sublib:reader:jacocoTestReport
./gradlew sublib:compiler:jacocoTestReport
./gradlew sublib:themereaderassetmanager:jacocoTestReportDebug
./gradlew sublib:compilerassetmanager:jacocoTestReportDebug
