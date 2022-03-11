#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ubuntu/app/step3
IDLE_PORT=$(find_idle_port)
IDLE_PROFILE=$(find_idle_profile)
IMAGEPATH=/home/ubuntu/app


echo "> IDLE_PORT : $IDLE_PORT"
echo "> IDLE_PROFILE : $IDLE_PROFILE"

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/zip/*.jar $REPOSITORY/"

cp $REPOSITORY/zip/*.jar $REPOSITORY

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -S $REPOSITORY/*.jar | head -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

sudo chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."

cd $REPOSITORY

nohup java -jar -Dspring.profiles.active=$IDLE_PROFILE one-page-resume-BE-0.0.1-SNAPSHOT.jar &