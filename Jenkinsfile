pipeline {
    agent { label 'dockeragent' }

    tools {
        maven 'Maven 3.9.11'
        jdk 'JDK 21'
    }

    environment {
        REGISTRY = 'crpi-vqe38j3xeblrq0n4.cn-hangzhou.personal.cr.aliyuncs.com/go-mctown'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
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
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aliyun-docker-login',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    sh """
                        echo "\$DOCKER_PASSWORD" | docker login --username \$DOCKER_USERNAME --password-stdin ${env.REGISTRY.split('/')[0]}
                    """
                }

                script {
                    def imageTag = "${env.REGISTRY}/${env.JOB_NAME.toLowerCase()}:${env.BUILD_NUMBER}"
                    echo "🏗️ Building Docker image: ${imageTag}"
                    sh """
                        docker build -t ${imageTag} .
                        docker push ${imageTag}
                    """
                }
            }
        }

        stage('Deploy with Compose') {
            steps {
                script {
                    def imageTag = "${env.REGISTRY}/${env.JOB_NAME.toLowerCase()}:${env.BUILD_NUMBER}"
                    def latestImage = "${env.REGISTRY}/${env.JOB_NAME.toLowerCase()}:latest"

                    // 标记 latest
                    sh "docker tag ${imageTag} ${latestImage}"
                    sh "docker push ${latestImage}"

                    // 替换 docker-compose.yml 中镜像（可选，如果用了变量可以跳过）
                    // sh "sed -i 's|image: .*|image: ${latestImage}|' docker-compose.yml"

                    // 重新部署
                    sh 'docker-compose down || true'
                    sh 'docker-compose pull'
                    sh 'docker-compose up -d --remove-orphans'
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
