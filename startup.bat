@echo off
REM Java 17 启动脚本
REM 解决 Seata/Sentinel 等框架在 Java 17 下的模块访问限制问题

set JAVA_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.lang.reflect=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.util=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.io=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.net=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.nio=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/sun.nio.ch=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/sun.security.ssl=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/sun.security.util=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.security=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.util.concurrent=ALL-UNNAMED

echo Starting %1 with Java 17 compatibility settings...
echo JVM Options: %JAVA_OPTS%
echo.

java %JAVA_OPTS% -jar %*

pause
