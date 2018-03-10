start javaw -jar backend-eskimo.jar --config %cd%\eskimo.properties
echo "SERVER STARTED ---------------------------------------------------------"
start javaw -jar invoker-eskimo.jar
echo "INVOKER STARTED ---------------------------------------------------------"
