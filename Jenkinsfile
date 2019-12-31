pipeline {
  agent any
  triggers {
    githubPush()
  }
  environment {
    ARTIFACTORY = credentials("artifactory")
    sh('printenv | sort')
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
        sh './gradlew artifactoryPublish'
      }
    }

    stage('Cleanup') {
      steps {
        cleanWs()
      }
    }
  }
}