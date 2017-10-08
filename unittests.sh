#!/bin/bash

set -e

./gradlew testFdroidDebugUnitTest --no-daemon --tests \*MainViewTest
./gradlew testFdroidDebugUnitTest --no-daemon --tests \*DetailedViewTest
./gradlew testFdroidDebugUnitTest --no-daemon --tests \*InstalledViewTest

./gradlew testFdroidDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.presenters.*

./gradlew testFdroidDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.adapters.*
./gradlew testFdroidDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.utils.*

./gradlew sublib:reader:test --no-daemon
./gradlew sublib:compiler:test --no-daemon

./gradlew app:jacocoTestReport --no-daemon
./gradlew sublib:reader:jacocoTestReport --no-daemon
./gradlew sublib:compiler:jacocoTestReport --no-daemon
