#!/bin/bash

set -e

./gradlew testDebugUnitTest --no-daemon --tests \*MainViewTest
./gradlew testDebugUnitTest --no-daemon --tests \*DetailedViewTest
./gradlew testDebugUnitTest --no-daemon --tests \*InstalledViewTest

./gradlew testDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.presenters.*

./gradlew testDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.adapters.*
./gradlew testDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.utils.*

./gradlew sublib:reader:test --no-daemon
./gradlew sublib:compiler:test --no-daemon

./gradlew app:jacocoTestReport --no-daemon
./gradlew sublib:reader:jacocoTestReport --no-daemon
./gradlew sublib:compiler:jacocoTestReport --no-daemon
