pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    environment {
        SONAR_TOKEN = credentials('SONAR_TOKEN')
    }

    stages {

        /* ------------------------------------------------------
           BUILD MAVEN
        ------------------------------------------------------- */
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

        /* ------------------------------------------------------
           UNIT TESTS
        ------------------------------------------------------- */
        stage('Unit Tests') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        /* ------------------------------------------------------
           JACOCO COVERAGE
        ------------------------------------------------------- */
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

        /* ------------------------------------------------------
           SONARQUBE ANALYSIS
        ------------------------------------------------------- */
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=voting-ci-demo \
                          -Dsonar.token=$SONAR_TOKEN
                    """
                }
            }
        }

        /* ------------------------------------------------------
           QUALITY GATE BLOCKER
        ------------------------------------------------------- */
        stage('Quality Gate') {
            steps {
                timeout(time: 3, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deliver') {
            steps {
                echo "Delivery step (optional)"
            }
        }
    }

    post {
        always {
            echo "Cleaning workspace..."
            cleanWs()
        }
    }
}

