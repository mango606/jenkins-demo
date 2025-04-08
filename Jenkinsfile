pipeline {
    agent any

    stages {
        stage('CHECKOUT') {
            steps {
                checkout scm
            }
        }

        stage('BUILD') {
            steps {
                sh '''
                # Gradle 래퍼에 실행 권한 부여
                chmod +x ./gradlew

                # 빌드 실행
                ./gradlew clean build -x test
                '''
            }
        }

        stage('TEST') {
            parallel {
                stage('FIREFOX') {
                    steps {
                        sh '''
                        echo "Firefox 브라우저에서 테스트 실행 중..."
                        ./gradlew test --tests "com.example.jenkins_demo.controller.DemoControllerTest"
                        '''
                    }
                }

                stage('EDGE') {
                    steps {
                        sh '''
                        echo "Edge 브라우저에서 테스트 실행 중..."
                        ./gradlew test --tests "com.example.jenkins_demo.JenkinsDemoApplicationTests"
                        '''
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('STAGING') {
            steps {
                sh '''
                echo "스테이징 환경에 배포 중..."
                # 기존 프로세스 종료
                STAGING_PID=$(lsof -t -i:8082) || true
                if [ ! -z "$STAGING_PID" ]; then
                  echo "포트 8082를 사용 중인 프로세스 종료: $STAGING_PID"
                  kill -9 $STAGING_PID || true
                fi

                # 스테이징용 JAR 파일 실행
                nohup java -jar build/libs/jenkins-demo-0.0.1-SNAPSHOT.jar --server.port=8082 > staging.log 2>&1 &
                echo "스테이징 애플리케이션 시작됨 (PID: $!)"

                # 배포 확인
                sleep 10
                curl -s http://localhost:8082/actuator/health || echo "스테이징 배포 실패"
                '''
            }
        }

        stage('PRODUCTION') {
            steps {
                sh '''
                echo "프로덕션 환경에 배포 중..."
                # 기존 프로세스 종료
                PROD_PID=$(lsof -t -i:8081) || true
                if [ ! -z "$PROD_PID" ]; then
                  echo "포트 8081를 사용 중인 프로세스 종료: $PROD_PID"
                  kill -9 $PROD_PID || true
                fi

                # 프로덕션용 JAR 파일 실행
                nohup java -jar build/libs/jenkins-demo-0.0.1-SNAPSHOT.jar --server.port=8081 > production.log 2>&1 &
                echo "프로덕션 애플리케이션 시작됨 (PID: $!)"

                # 배포 확인
                sleep 10
                curl -s http://localhost:8081/actuator/health || echo "프로덕션 배포 실패"
                '''
            }
        }
    }

    post {
        success {
            echo '파이프라인이 성공적으로 완료되었습니다!'
            echo '애플리케이션이 http://localhost:8081(프로덕션) 및 http://localhost:8082(스테이징)에서 실행 중입니다'
        }
        failure {
            echo '파이프라인 실행 중 오류가 발생했습니다.'
        }
    }
}