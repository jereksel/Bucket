#!/usr/bin/env bash

export BUILD_NUMBER=$(($TRAVIS_BUILD_NUMBER + 1125))
export BRANCH=$TRAVIS_BRANCH

./gradlew publishPlayRelease
