#!/bin/bash

set -e

./gradlew testDebugUnitTest --no-daemon
./gradlew sublib:reader:test --no-daemon
./gradlew sublib:compiler:test --no-daemon

./gradlew jacocoTestDebugUnitTestReport --no-daemon
./gradlew sublib:reader:jacocoTestReport --no-daemon
./gradlew sublib:compiler:jacocoTestReport --no-daemon
