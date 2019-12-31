pipeline {
  agent any
  triggers {
    githubPush()
  }
  environment {
    ARTIFACTORY = credentials("artifactory")
  }
  stages {
    stage('Build') {
      steps {
        sh './gradlew clean build shadowJar'
      }
    }

    stage('Artifacts') {
      steps {
        archiveArtifacts(artifacts: '**/build/libs/*.jar', onlyIfSuccessful: true)
      }
    }

    stage('Deploy') {
      steps {
        sh './gradlew '
    }

    stage('Cleanup') {
      steps {
        cleanWs()
      }
    }

  }
}