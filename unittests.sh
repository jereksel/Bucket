#!/bin/bash

set -e

./gradlew testDebugUnitTest --stacktrace --no-daemon
./gradlew sublib:reader:test
./gradlew sublib:compiler:test

./gradlew jacocoTestDebugUnitTestReport
./gradlew sublib:reader:jacocoTestReport
./gradlew sublib:compiler:jacocoTestReport
