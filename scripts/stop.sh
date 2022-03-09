#!/usr/bin/env bash




# readlink $0 첫번째 파라메터로 stop.sh 쉘파일이 있는곳의 f 옵션으로 절대경로 출력
ABSPATH=$(readlink -f $0)
# ABSDIR : 현재 stop.sh 파일 위치의 경로
ABSDIR=$(dirname $ABSPATH)
# import profile.sh
source ${ABSDIR}/profile.sh


IDLE_PORT=$(find_idle_port)
IDLE_PROFILE=$(find_idle_profile)


echo "> IDLE_PORT : ${IDLE_PORT}"

echo "> $IDLE_PORT 에서 구동중인 애플리케이션 pid 확인"
IDLE_PID=$(sudo lsof -ti tcp:${IDLE_PORT})

if [ -z ${IDLE_PID} ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $IDLE_PID"   # Nginx에 연결되어 있지는 않지만 현재 실행 중인 jar 를 Kill 합니다.
  kill -15 ${IDLE_PID}
  sleep 5
fi