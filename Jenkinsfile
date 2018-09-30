pipeline {
    agent any
    tools {
        maven 'maven'
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean test install -DskipTests'
            }
        }
        stage('Docker') {
            steps {
              configFileProvider([configFile(fileId: 'global.maven.settings.xml', variable: 'MAVEN_SETTINGS')]) {
                  sh "mvn -s $MAVEN_SETTINGS --projects podleo-api docker:build"
              }
            }
        }
    }
}
