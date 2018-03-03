#!/usr/bin/env bash
nohup java -jar backend-eskimo.jar &
echo "SERVER STARTED ---------------------------------------------------------"
nohup java -jar invoker-eskimo.jar &
echo "INVOKER STARTED ---------------------------------------------------------"