pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    stages {

        stage('Build') {
            steps {
                bat 'mvn -version'
                bat 'mvn clean compile'
            }
        }

        stage('Smoke Tests') {
            steps {
                bat 'mvn test -P smoke'
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed'
        }
    }
}
