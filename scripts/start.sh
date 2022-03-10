#!/usr/bin/env bash
# start.sh
# 서버 구동을 위한 스크립트

# 절대경로를 이용하여 profile.sh 경로 찾은 후 import
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh


# 현재 프로젝트 경로 지정
REPOSITORY=/home/ec2-user/app/step3
IDLE_PORT=$(find_idle_port)
IDLE_PROFILE=$(find_idle_profile)

echo "> JASYPT_ENCRYPTOR_PASSWORD: ${JASYPT_ENCRYPTOR_PASSWORD}"
echo "> IDLE_PORT : $IDLE_PORT"
echo "> IDLE_PROFILE : $IDLE_PROFILE"

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/zip/*.jar $REPOSITORY/"

# 새로운 jar file 덮어쓰기
cp $REPOSITORY/zip/*.jar $REPOSITORY

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -S $REPOSITORY/*.jar | head -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

sudo chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."

cd $REPOSITORY

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."
nohup java -jar -Dspring.config.location=classpath:/application.properties,classpath:/application-$IDLE_PROFILE.properties,/home/ec2-user/app/application-oauth.properties,/home/ec2-user/app/application-real-db.properties -Dspring.profiles.active=$IDLE_PROFILE $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &