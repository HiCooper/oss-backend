#!/usr/bin/env bash

functionName=$1

APP_NAME=$2


usage() {
    echo "Usage: run.sh [start|stop|halt|restart|status]"
    exit 1
}

function checkParams(){
    if [[ ! -n ${functionName} ]] || [[ ! -n ${APP_NAME}  ]]; then
        echo "ERROR!!! Please check the command, need like ./run.sh start app.jar"
        exit 1
    fi
}

function start(){
    checkParams
    count=`ps -ef |grep java|grep ${APP_NAME}|grep -v grep|wc -l`
    if [[ ${count} != 0 ]];then
        echo "progress is running... try restart?"
    else
        nohup java -jar ${APP_NAME} > /dev/null 2>&1 &
        echo ">>> start successed PID=$! <<<"
    fi
}

function stop(){
    checkParams
    tpid=`ps -ef|grep java |grep ${APP_NAME}|grep -v grep|grep -v kill|awk '{print $2}'`
    if [[ -n ${tpid} ]]; then
      echo 'Kill Process!'
      kill -9 ${tpid}
      echo 'Stop Success!'
    else
        echo "Not Running."
    fi
}

function halt(){
    checkParams
    tpid=`ps -ef|grep java |grep ${APP_NAME}|grep -v grep|grep -v kill|awk '{print $2}'`
    if [[ -n ${tpid} ]]; then
        echo 'Kill Process!'
        kill -9 ${tpid}
    fi
}

function status(){
    checkParams
    count=`ps -ef |grep java  |grep ${APP_NAME}|grep -v grep|wc -l`
    if [[ ${count} != 0 ]];then
        echo "Running..."
    else
        echo "NOT running."
    fi
}

function restart(){
    checkParams
    stop
    sleep 2
    start
}

case ${functionName} in
    "start" )
        start
        ;;
    "stop" )
        stop
        ;;
    "halt" )
        halt
        ;;
    "status" )
        status
        ;;
    "restart" )
        restart
        ;;
    * )
        usage
        ;;
esac
exit 0