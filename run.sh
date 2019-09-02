#!/usr/bin/env bash
export LANG="en_US.UTF-8"

functionName=$1

APP_NAME=$2

usage() {
  echo "Usage: run.sh [start|stop|halt|restart|status]"
  exit 1
}

function checkParams() {
  if [[ ! -n ${functionName} ]] || [[ ! -n ${APP_NAME} ]]; then
    echo "ERROR!!! Please check the command, need like ./run.sh start app.jar"
    exit 1
  fi
}

function getPid() {
  task_pid=$(ps -ef | grep java | grep "${APP_NAME}" | grep -v grep | grep -v kill | awk '{print $2}')
  echo "${task_pid}"
}

function start() {
  checkParams
  task_pid=$(getPid)
  if [[ -n ${task_pid} ]]; then
    echo "progress is running... try restart?"
  else
    nohup java -jar "${APP_NAME}" --spring.profiles.active=cc > /dev/null 2>&1 &
    echo ">>> start successed PID=$! <<<"
  fi
}

function stop() {
  checkParams
  task_pid=$(getPid)
  if [[ -n ${task_pid} ]]; then
    echo "Kill Process! PID:${task_pid}"
    kill -9 "${task_pid}"
    echo 'Stop Success!'
  else
    echo "Not Running!"
  fi
}

function halt() {
  checkParams
  task_pid=$(getPid)
  if [[ -n ${task_pid} ]]; then
    echo "Stop Process! PID:${task_pid} Gracefully"
    kill -15 "${task_pid}"
    echo 'Stop Success!'
  else
    echo "Not Running!"
  fi
}

function status() {
  checkParams
  task_pid=$(getPid)
  if [[ -n ${task_pid} ]]; then
    echo "Running..."
  else
    echo "NOT running."
  fi
}

function restart() {
  stop
  sleep 2
  start
}

case ${functionName} in
"start")
  start
  ;;
"stop")
  stop
  ;;
"halt")
  halt
  ;;
"status")
  status
  ;;
"restart")
  restart
  ;;
*)
  usage
  ;;
esac
exit 0