#!/usr/bin/env bash
BACKEND_PID=`jps -l | grep backend-eskimo.jar | awk '{print $1;}'`
kill -9 $BACKEND_PID
echo "SERVER STOPPED ---------------------------------------------------------"
INVOKER_PID=`jps -l | grep invoker-eskimo.jar | awk '{print $1;}'`
kill -9 $INVOKER_PID
echo "INVOKER STOPPED ---------------------------------------------------------"

