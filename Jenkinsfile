pipeline {
  agent any

  tools {
    maven 'Maven3'      // Configure dans Jenkins > Global Tools
    jdk 'JDK17'         // Configure dans Jenkins > Global Tools
  }

  environment {
    SONAR_TOKEN = credentials('SONAR_TOKEN')       // Secret Text
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -B clean package -DskipTests'
      }
      post {
        success {
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }

    stage('Unit Tests') {
      steps {
        sh 'mvn -B test'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
        }
      }
    }

    stage('Code Coverage') {
      steps {
        sh 'mvn -B jacoco:report'
      }
      post {
        success {
          publishHTML(target: [
            reportName: 'JaCoCo Coverage',
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            keepAll: true
          ])
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('SonarQube') {
          sh """
            mvn -B sonar:sonar \
              -Dsonar.projectKey=voting-ci-demo \
              -Dsonar.token=$SONAR_TOKEN
          """
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 3, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Deliver') {
      steps {
        echo "Delivery step - optional"
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
      cleanWs()
    }
  }
}

