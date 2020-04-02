#!/usr/bin/env bash

INPUT=$2
SW_SERVER=$3
FILE_PATH=$(readlink -f ${INPUT})
SERVICE=${INPUT##*/}
SERVICE_NAME=${SERVICE%.*}
DEPLOY_DIR=$(pwd)
JVM_OPTS="-server -Xms128m -Xmx768m -XX:+UseG1GC -XX:SurvivorRatio=6 -XX:MaxGCPauseMillis=400 -XX:G1ReservePercent=15 -XX:ParallelGCThreads=4 -XX:ConcGCThreads=1 -XX:InitiatingHeapOccupancyPercent=40 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:../logs/gc.log -javaagent:skywalking-agent.jar -Dskywalking.agent.service_name=$SERVICE_NAME -Dskywalking.collector.backend_service=$SW_SERVER"

if [[ "$1" == "" ]]; then
  echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
  exit 1
fi

if [[ "$SERVICE" == "" ]]; then
  echo -e "\033[0;31m 未输入应用名 \033[0m"
  exit 1
fi

LOGS_DIR="$DEPLOY_DIR/logs/$SERVICE_NAME"
echo "$LOGS_DIR"
if [[ ! -d "$LOGS_DIR" ]]; then
  mkdir -p ${LOGS_DIR}
fi

LOG_PATH="$LOGS_DIR/stdout.out"
pid=0

start() {
  checkPid
  if [[ ! -n "$pid" ]]; then
    BUILD_ID=dontKillMe nohup java ${JVM_OPTS} -jar ${FILE_PATH} >>${LOG_PATH} 2>&1 &
    echo "$SERVICE_NAME is starting you can check the $LOG_PATH"
  else
    echo "$SERVICE_NAME is runing PID: $pid"
  fi
}

checkPid() {
  pid=$(ps -ef | grep ${FILE_PATH} | grep -v grep | awk '{print $2}')
}

stop() {
  checkPid
  if [[ ! -n "$pid" ]]; then
    echo "$SERVICE_NAME not runing"
  else
    echo "$SERVICE_NAME stop..."
    kill -9 ${pid}
  fi
}

restart() {
  stop
  sleep 2
  start
}

status() {
  checkPid
  if [[ ! -n "$pid" ]]; then
    echo "$SERVICE_NAME not runing"
  else
    echo "$SERVICE_NAME runing PID: $pid"
  fi
}

case $1 in
start) start ;;
stop) stop ;;
restart) restart ;;
status) status ;;
*) echo "require start|stop|restart|status" ;;
esac
