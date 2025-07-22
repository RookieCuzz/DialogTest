pipeline {
    agent { label 'dockeragent'}

    tools {
        // 这些工具需在 Jenkins 管理页面配置 Global Tool Configuration
        maven 'Maven 3.9.11'
        jdk 'JDK 21'
    }

    options {
        // 保留最近 5 次构建
        buildDiscarder(logRotator(numToKeepStr: '5'))
        // 禁止同一 Job 并发构建
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '💡 Building with Maven...'
                sh 'mvn clean install -DskipTests'
            }
        }


        stage('Package') {
            steps {
                echo '📦 Packaging application...'
                sh 'mvn package'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

         stage('Docker Build & Push') {
            steps {
                script {
                    // 定义镜像名字和 Tag
                    def imageTag = "${env.REGISTRY ?: 'myregistry.example.com'}/${env.JOB_NAME}:${env.BUILD_NUMBER}"
                    echo "🏗️ Building Docker image: ${imageTag}"
                    sh """
                        docker build -t ${imageTag} .
                        docker push ${imageTag}
                    """
                }
            }
        }



        
    }

    post {
        success {
            echo '🎉 Build succeeded!'
        }
        failure {
            echo '❌ Build failed!'
        }
        always {
            echo '🧹 Cleaning workspace...'
            cleanWs()
        }
    }
}
