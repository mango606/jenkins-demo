pipeline {
    agent any

    tools {
        jdk 'JDK 17'
    }

    stages {
        stage('소스 코드 가져오기') {
            steps {
                checkout scm
            }
        }

        stage('Gradle 권한 설정') {
            steps {
                sh 'chmod +x ./gradlew'
            }
        }

        stage('빌드') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('테스트') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('코드 품질 검사') {
            steps {
                echo '소나큐브 스캔 실행 (데모에서는 생략 가능)'
                // sh './gradlew sonarqube'
            }
        }

        stage('도커 이미지 빌드') {
            steps {
                sh 'docker build -t jenkins-demo:${BUILD_NUMBER} .'
                sh 'docker tag jenkins-demo:${BUILD_NUMBER} jenkins-demo:latest'
            }
        }

        stage('도커 이미지 실행') {
            steps {
                sh 'docker stop jenkins-demo || true'
                sh 'docker rm jenkins-demo || true'
                sh 'docker run -d -p 8081:8080 --name jenkins-demo jenkins-demo:latest'
            }
        }

        stage('애플리케이션 상태 확인') {
            steps {
                // 5초 대기 후 상태 확인
                sh 'sleep 5'
                sh 'curl -s http://localhost:8081/actuator/health || echo "애플리케이션이 정상적으로 시작되지 않았습니다"'
            }
        }
    }

    post {
        success {
            echo '파이프라인이 성공적으로 완료되었습니다!'
            echo '애플리케이션이 http://localhost:8081에서 실행 중입니다'
        }
        failure {
            echo '파이프라인 실행 중 오류가 발생했습니다.'
        }
    }
}