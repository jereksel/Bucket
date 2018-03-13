#!/usr/bin/env bash

export BUILD_NUMBER=$(($TRAVIS_BUILD_NUMBER + 1125))
export BRANCH=$TRAVIS_BRANCH

cd app
openssl aes-256-cbc -K $KEY -iv $IV -in secrets.tar.enc -out secrets.tar -d
tar xvf secrets.tar
cd ..
./gradlew publishPlayRelease --no-daemon

