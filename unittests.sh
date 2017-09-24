#!/bin/bash

set -e

./gradlew testDebugUnitTest --no-daemon
./gradlew sublib:reader:test
./gradlew sublib:compiler:test

./gradlew jacocoTestDebugUnitTestReport
./gradlew sublib:reader:jacocoTestReport
./gradlew sublib:compiler:jacocoTestReport
