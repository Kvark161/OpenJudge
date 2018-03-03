for /f "tokens=1" %i in ('jps -l ^| find "backend-eskimo.jar"') do ( taskkill /F /PID %i )
for /f "tokens=1" %i in ('jps -l ^| find "invoker-eskimo.jar"') do ( taskkill /F /PID %i )