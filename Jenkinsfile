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
        sh './gradlew clean shadowJar publish'
      }
    }

    stage('Artifacts') {
      steps {
        archiveArtifacts(artifacts: '**/build/libs/*.jar', onlyIfSuccessful: true)
      }
    }

    stage('Cleanup') {
      steps {
        cleanWs()
      }
    }
  }
}