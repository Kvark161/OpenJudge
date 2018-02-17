BACKEND_PID=`jps -l | grep backend-0.0.1-SNAPSHOT.jar | awk '{print $1;}'`
echo $BACKEND_PID
kill -9 $BACKEND_PID
echo "SERVER STOPPED ---------------------------------------------------------"
INVOKER_PID=`jps -l | grep invoker-0.0.1-SNAPSHOT.jar | awk '{print $1;}'`
echo $INVOKER_PID
kill -9 $INVOKER_PID
echo "INVOKER STOPPED ---------------------------------------------------------"

