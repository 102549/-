@echo off
REM 设置 Maven 和 Java 17 模块访问环境变量
REM 运行此脚本后，启动的 Spring Boot 应用将自动获得模块访问权限

set MAVEN_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/sun.security.ssl=ALL-UNNAMED --add-opens java.base/sun.security.util=ALL-UNNAMED --add-opens java.base/java.security=ALL-UNNAMED --add-opens java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED

echo Maven options set:
echo %MAVEN_OPTS%
echo.
echo Now you can run: mvn spring-boot:run
echo Or start your application from IDEA
pause
