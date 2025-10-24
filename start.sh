#!/bin/bash

echo "启动火车票务管理系统..."
echo

echo "1. 编译项目..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "编译失败！"
    exit 1
fi

echo
echo "2. 启动Spring Boot应用..."
echo "前端页面将在 http://localhost:8080 启动"
echo "按 Ctrl+C 停止服务"
echo

mvn spring-boot:run
