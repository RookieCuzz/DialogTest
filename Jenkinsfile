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
            withCredentials([usernamePassword(
                credentialsId: 'aliyun-docker-login', // 就是你上面填写的 ID
                usernameVariable: 'DOCKER_USERNAME',
                passwordVariable: 'DOCKER_PASSWORD'
            )]) {
                sh """
                    echo "\$DOCKER_PASSWORD" | docker login --username \$DOCKER_USERNAME --password-stdin crpi-vqe38j3xeblrq0n4.cn-hangzhou.personal.cr.aliyuncs.com
                """
                }    
                script {
                    // 定义镜像名字和 Tag
                    def imageTag = "${env.REGISTRY ?: 'crpi-vqe38j3xeblrq0n4.cn-hangzhou.personal.cr.aliyuncs.com/go-mctown'}/${env.JOB_NAME.toLowerCase()}:${env.BUILD_NUMBER}"
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
        
                    // 更新 yml 中 image tag 为 :latest，或者构建时替换 env
                    sh """
                        echo "🔄 替换 image 为 latest..."
                        docker tag ${imageTag} ${latestImage}
                    """
        
                    // 停止旧容器（docker compose down）
                    sh 'docker-compose down || true'
        
                    // 启动新容器（docker compose up）
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
