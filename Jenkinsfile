// Jenkinsfile — declarative pipeline
// Runs on the EC2 Jenkins agent with Chrome + Maven installed
// Stages: Checkout → Build → Smoke → Regression → Upload to S3 → Notify

pipeline {
    agent any  // Runs on the EC2 Jenkins agent

    environment {
        // These are set as Jenkins Credentials / Env Vars — never hardcoded
        S3_BUCKET     = credentials('s3-test-reports-bucket')
        AWS_REGION    = 'ap-south-1'
        BROWSER       = 'chrome-headless'  // Headless on EC2 (no display)
    }

    triggers {
        // Run full regression every night at 11 PM
        cron('0 23 * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out code from GitHub...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling project...'
                sh 'mvn clean compile -q'
            }
        }

        stage('Smoke Tests') {
            steps {
                echo 'Running smoke tests...'
                sh """
                    mvn test -P smoke \
                        -Dbrowser=${BROWSER} \
                        -Ds3.bucket.name=${S3_BUCKET}
                """
            }
            post {
                always {
                    // Publish TestNG results in Jenkins UI
                    testNG 'reports/testng-output/testng-results.xml'
                }
            }
        }

        stage('Regression Tests') {
            // Only run regression on nightly trigger, not on every commit
            when {
                triggeredBy 'TimerTrigger'
            }
            steps {
                echo 'Running full regression suite...'
                sh """
                    mvn test -P regression \
                        -Dbrowser=${BROWSER} \
                        -Ds3.bucket.name=${S3_BUCKET}
                """
            }
        }

        stage('Upload Reports to S3') {
            steps {
                echo 'Uploading HTML reports and screenshots to S3...'
                sh """
                    aws s3 cp reports/extent-reports/ \
                        s3://${S3_BUCKET}/reports/${BUILD_NUMBER}/ \
                        --recursive --region ${AWS_REGION}
                    aws s3 cp reports/screenshots/ \
                        s3://${S3_BUCKET}/screenshots/${BUILD_NUMBER}/ \
                        --recursive --region ${AWS_REGION}
                """
                echo "Reports available at: s3://${S3_BUCKET}/reports/${BUILD_NUMBER}/"
            }
        }
    }

    post {
        failure {
            echo 'Pipeline FAILED — check CloudWatch logs for details'
            // In real setup: send email/Slack notification here
        }
        always {
            echo 'Cleaning workspace...'
            cleanWs()
        }
    }
}
