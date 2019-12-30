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
                        url: "https://nachtraben.com/artifactory",
                        credentialsId: "nachtraben.com"
                    )

                    rtGradleDeployer (
                        id: "GRADLE_DEPLOYER",
                        serverId: "nachtraben.com",
                        repo: "snapshots",
                    )

                    rtGradleResolver (
                        id: "GRADLE_RESOLVER",
                        serverId: "nachtraben.com",
                        repo: "snapshots"
                    )
                }
            }

    stage('Deploy') {
      steps {
        rtGradleRun (
          tool: "gradle", // Tool name from Jenkins configuration
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