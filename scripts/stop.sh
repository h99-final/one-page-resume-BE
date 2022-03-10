#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh # 자바 import와 동일

#IDLE_PORT에서 쉬고 구동 중인 애플리케이션이 있다면 끈다.
IDLE_PORT=$(find_idle_port)

echo "> $IDLE_PORT 에서 구동 중이 애플리케이션 pid 확인"
IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})

