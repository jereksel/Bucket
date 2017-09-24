#!/bin/bash

set -e

./gradlew testDebugUnitTest --no-daemon --tests \*DetailedPresenterTest
./gradlew testDebugUnitTest --no-daemon --tests \*InstalledPresenterTest
./gradlew testDebugUnitTest --no-daemon --tests \*MainPresenterTest

./gradlew testDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.presenters.*

./gradlew testDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.adapters.*
./gradlew testDebugUnitTest --no-daemon --tests com.jereksel.libresubstratum.utils.*


./gradlew sublib:reader:test --no-daemon
./gradlew sublib:compiler:test --no-daemon

./gradlew jacocoTestDebugUnitTestReport --no-daemon
./gradlew sublib:reader:jacocoTestReport --no-daemon
./gradlew sublib:compiler:jacocoTestReport --no-daemon
