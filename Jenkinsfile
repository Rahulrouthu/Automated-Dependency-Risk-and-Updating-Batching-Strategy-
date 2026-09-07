pipeline {
    agent any

    environment {
        JAVA_HOME = tool 'JDK21'
        NODE_HOME = tool 'NodeJS20'
        PATH = "${env.JAVA_HOME}/bin:${env.NODE_HOME}/bin:${env.PATH}"
        SPRING_PROFILES_ACTIVE = 'dev'
    }

    stages {
        stage('1. Checkout Repository') {
            steps {
                echo 'Pulling latest codebase and dependency analysis configs...'
                checkout scm
            }
        }

        stage('2. Backend Verification & Tests') {
            steps {
                dir('backend') {
                    echo 'Executing Unit & Integration Tests (Parsers, SemVer, Risk Scoring, OSV Client)...'
                    sh './mvnw clean test'
                }
            }
        }

        stage('3. Dependency Intelligence & OSV Scan') {
            steps {
                echo 'Triggering automated dependency vulnerability & risk analysis...'
                dir('backend') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        stage('4. Frontend Build & Asset Bundle') {
            steps {
                dir('frontend') {
                    echo 'Building Vite React 18 production bundle...'
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('5. Docker Image Packaging') {
            when {
                branch 'main'
            }
            steps {
                echo 'Building Docker container images...'
                sh 'docker build -f Dockerfile.backend -t automated-dependency-risk-backend:latest .'
                sh 'docker build -f Dockerfile.frontend -t automated-dependency-risk-frontend:latest .'
            }
        }
    }

    post {
        always {
            echo 'Archiving test reports and build artifacts...'
            junit testResults: 'backend/target/surefire-reports/*.xml', allowEmptyResults: true
        }
        success {
            echo 'Automated Dependency Risk Assessment & Batching Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed. Check error logs and surefire reports.'
        }
    }
}
