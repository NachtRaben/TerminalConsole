pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh './gradlew clean build shadowJar'
      }
    }

    stage('Deploy') {
      steps {
        sh './gradlew artifactoryPublish'
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