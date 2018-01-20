pipeline {
  agent any
  stages {
    stage('Build') {
      parallel {
        stage('Build') {
          steps {
            sh './gradlew assembleDebug'
          }
        }
        stage('Test') {
          steps {
            sh './gradlew testAll'
          }
        }
      }
    }
  }
  environment {
    ANDROID_HOME = '/opt/android-sdk'
    ANDROID_NDK_HOME = '/opt/android-ndk-15c/android-ndk-r15c'
  }
}