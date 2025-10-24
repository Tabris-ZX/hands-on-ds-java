# 火车票务管理系统 - 前端页面

## 项目概述

这是一个完整的火车票务管理系统，包含前端Web界面和后端Java API。

## 功能模块

### 1. 用户管理
- 用户注册
- 用户登录/登出
- 个人资料查看
- 权限管理（管理员功能）

### 2. 列车管理（管理员功能）
- 添加列车
- 查询列车信息

### 3. 票务管理（管理员功能）
- 发布车票
- 使车票过期

### 4. 车票交易
- 查询余票
- 购买车票
- 查询我的订单
- 退票

### 5. 路线查询
- 显示所有路线
- 查询最佳路径（时间优先/价格优先）
- 可达性查询

## 技术栈

### 前端
- HTML5
- CSS3 (现代化响应式设计)
- JavaScript (ES6+)
- Font Awesome 图标

### 后端
- Java 21
- Spring Boot 3.2.0
- Maven
- SQLite 数据库

## 启动方式

### 1. 编译项目
```bash
mvn clean compile
```

### 2. 启动后端服务
```bash
mvn spring-boot:run
```

或者
```bash
java -jar target/hands-on-ds-1.0-SNAPSHOT.jar
```

### 3. 访问前端页面
打开浏览器访问：http://localhost:8080

## API接口

所有API接口都在 `/api` 路径下：

- `POST /api/login` - 用户登录
- `POST /api/register` - 用户注册
- `POST /api/logout` - 用户登出
- `GET /api/user/{userId}` - 查询用户信息
- `PUT /api/user/{userId}/privilege` - 修改用户权限
- `PUT /api/user/{userId}/password` - 修改用户密码
- `POST /api/train` - 添加列车
- `GET /api/train/{trainId}` - 查询列车信息
- `POST /api/ticket/release` - 发布车票
- `POST /api/ticket/expire` - 过期车票
- `GET /api/ticket/remaining` - 查询余票
- `POST /api/ticket/buy` - 购票
- `GET /api/ticket/orders` - 查询订单
- `POST /api/ticket/refund` - 退票
- `GET /api/route/display` - 显示路线
- `GET /api/route/best` - 查询最佳路径
- `GET /api/route/accessibility` - 可达性查询

## 默认管理员账号

- 用户ID: 0
- 用户名: admin
- 密码: admin
- 权限: 管理员

## 界面特性

- 响应式设计，支持移动端
- 现代化UI设计
- 实时消息提示
- 权限控制（管理员功能需要登录管理员账号）
- 直观的导航界面

## 注意事项

1. 首次启动会自动创建管理员账号
2. 数据文件存储在 `data/` 目录下
3. 前端页面支持所有现代浏览器
4. API支持CORS跨域请求
