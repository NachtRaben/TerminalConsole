pipeline {
  agent any
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

    stage ('Artifactory configuration') {
                steps {
                    rtServer (
                        id: "nachtraben.com",
                        url: SERVER_URL,
                        credentialsId: CREDENTIALS
                    )

                    rtGradleDeployer (
                        id: "GRADLE_DEPLOYER",
                        serverId: "ARTIFACTORY_SERVER",
                        repo: "libs-release-local",
                    )

                    rtGradleResolver (
                        id: "GRADLE_RESOLVER",
                        serverId: "ARTIFACTORY_SERVER",
                        repo: "jcenter"
                    )
                }
            }

    stage('Deploy') {
      steps {
        rtGradleRun (
          tool: GRADLE_TOOL, // Tool name from Jenkins configuration
          rootDir: "gradle-examples/gradle-example-ci-server/",
          buildFile: 'build.gradle',
          tasks: 'clean artifactoryPublish',
          deployerId: "GRADLE_DEPLOYER",
          resolverId: "GRADLE_RESOLVER"
        )
      }
    }

    stage('Cleanup') {
      steps {
        cleanWs()
      }
    }

  }
}