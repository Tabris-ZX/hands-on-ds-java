@echo off
echo 启动火车票务管理系统...
echo.

echo 1. 编译项目...
call mvn clean compile
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo.
echo 2. 启动Spring Boot应用...
echo 前端页面将在 http://localhost:8080 启动
echo 按 Ctrl+C 停止服务
echo.

call mvn spring-boot:run

pause
