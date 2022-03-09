#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh # 자바 import와 동일

function switch_proxy() {
  IDLE_PORT=$(find_idle_port)

  echo "> 전환할 Port: $IDLE_PORT"
  echo "> Port 전환"
  echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc #tee: 파일에도 쓰고 표준출력도 한다.

  echo "> 엔진엑스 Reload"
  sudo service nginx reload # restart는 끊김이 있지만 reload는 끊김이 없다.
}