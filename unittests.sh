#!/bin/bash

export GRADLE_OPTS="$GRADLE_OPTS -Dorg.gradle.daemon=false"

# To download Robolectric libs
./gradlew app:testFdroidDebugUnitTest --tests "**.BaseRobolectricTest"

set -e

./gradlew app:testFdroidDebugUnitTest

./gradlew sublib:reader:test
./gradlew sublib:compiler:test
./gradlew sublib:themereaderassetmanager:test
./gradlew sublib:compilerassetmanager:test

./gradlew app:jacocoTestReport
./gradlew sublib:reader:jacocoTestReport
./gradlew sublib:compiler:jacocoTestReport
./gradlew sublib:themereaderassetmanager:jacocoTestReportDebug
./gradlew sublib:compilerassetmanager:jacocoTestReportDebug
