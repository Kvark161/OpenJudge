for /f "tokens=1" %i in ('jps -l ^| find "backend-0.0.1-SNAPSHOT.jar"') do ( taskkill /F /PID %i )
for /f "tokens=1" %i in ('jps -l ^| find "invoker-0.0.1-SNAPSHOT.jar"') do ( taskkill /F /PID %i )