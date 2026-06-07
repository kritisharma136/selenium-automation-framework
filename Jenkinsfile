pipeline {
agent any

```
stages {

    stage('Build') {
        steps {
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
```

}
