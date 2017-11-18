#!/bin/bash

sed -i "s|maven { url 'https://maven.fabric.io/public' }||g" build.gradle

sed -i "s|classpath 'io.fabric.tools:gradle:1.24.1'||g" build.gradle

sed -i "s|apply plugin: 'io.fabric'||g" build.gradle
