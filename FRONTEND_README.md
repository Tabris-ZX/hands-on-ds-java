# 火车票务管理系统 - 前端页面

## 项目概述

这是一个完整的火车票务管理系统，包含现代化的Web前端界面和Spring Boot后端API。

## 功能特性

### 🎯 核心功能
- **用户管理**: 注册、登录、权限管理
- **列车管理**: 添加列车、查询列车信息（管理员功能）
- **票务管理**: 发布车票、过期车票（管理员功能）
- **车票交易**: 查询余票、购票、退票、查询订单
- **路线查询**: 显示路线、最佳路径、可达性查询

### 🎨 界面特性
- 现代化响应式设计
- 直观的导航界面
- 实时消息提示
- 权限控制（管理员功能需要登录管理员账号）
- 支持移动端访问

## 技术栈

### 前端
- **HTML5** - 语义化标记
- **CSS3** - 现代化样式，渐变背景，动画效果
- **JavaScript ES6+** - 模块化编程，异步处理
- **Font Awesome** - 图标库

### 后端
- **Java 21** - 现代Java特性
- **Spring Boot 3.2.0** - Web框架
- **Maven** - 依赖管理
- **SQLite** - 轻量级数据库

## 快速开始

### 方法一：使用启动脚本（推荐）

**Windows用户：**
```bash
start.bat
```

**Linux/Mac用户：**
```bash
./start.sh
```

### 方法二：手动启动

1. **编译项目**
```bash
mvn clean compile
```

2. **启动后端服务**
```bash
mvn spring-boot:run
```

3. **访问前端页面**
打开浏览器访问：http://localhost:8080

## 默认账号

- **管理员账号**
  - 用户ID: 0
  - 用户名: admin
  - 密码: admin
  - 权限: 管理员

## 使用说明

### 1. 用户管理
- **登录**: 使用用户ID和密码登录
- **注册**: 新用户需要先注册账号
- **权限管理**: 管理员可以修改用户权限

### 2. 列车管理（管理员功能）
- **添加列车**: 设置车次ID、席位数、站点信息、时长、票价
- **查询列车**: 查看列车详细信息

### 3. 票务管理（管理员功能）
- **发布车票**: 为指定车次和日期发布车票
- **过期车票**: 使指定车次和日期的车票过期

### 4. 车票交易
- **查询余票**: 查看指定车次、日期、出发站的余票数量
- **购票**: 购买指定车次的车票
- **查询订单**: 查看当前用户的所有订单
- **退票**: 退掉已购买的车票

### 5. 路线查询
- **显示路线**: 显示从起点到终点的所有可能路线
- **最佳路径**: 根据时间或价格优先查找最优路径
- **可达性查询**: 检查两个站点是否连通

## API接口

所有API接口都在 `/api` 路径下：

### 用户管理
- `POST /api/login` - 用户登录
- `POST /api/register` - 用户注册
- `POST /api/logout` - 用户登出
- `GET /api/user/{userId}` - 查询用户信息
- `PUT /api/user/{userId}/privilege` - 修改用户权限
- `PUT /api/user/{userId}/password` - 修改用户密码

### 列车管理
- `POST /api/train` - 添加列车
- `GET /api/train/{trainId}` - 查询列车信息

### 票务管理
- `POST /api/ticket/release` - 发布车票
- `POST /api/ticket/expire` - 过期车票
- `GET /api/ticket/remaining` - 查询余票
- `POST /api/ticket/buy` - 购票
- `GET /api/ticket/orders` - 查询订单
- `POST /api/ticket/refund` - 退票

### 路线查询
- `GET /api/route/display` - 显示路线
- `GET /api/route/best` - 查询最佳路径
- `GET /api/route/accessibility` - 可达性查询

## 项目结构

```
src/main/resources/static/
├── index.html          # 主页面
├── styles.css          # 样式文件
├── script.js           # JavaScript逻辑
└── README.md           # 说明文档

src/main/java/boyuai/trainsys/
├── TrainSystemApplication.java    # Spring Boot主类
├── controller/
│   ├── TrainSystemController.java # API控制器
│   └── StaticController.java     # 静态资源控制器
└── config/
    └── TrainSystemConfig.java    # 系统配置
```

## 注意事项

1. **首次启动**: 系统会自动创建管理员账号
2. **数据存储**: 数据文件存储在 `data/` 目录下
3. **浏览器支持**: 支持所有现代浏览器
4. **跨域支持**: API支持CORS跨域请求
5. **权限控制**: 管理员功能需要登录管理员账号才能使用

## 故障排除

### 常见问题

1. **端口被占用**
   - 修改 `application.properties` 中的端口配置
   - 或使用 `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`

2. **编译失败**
   - 确保Java版本为21或更高
   - 检查Maven配置

3. **前端页面无法访问**
   - 确保后端服务已启动
   - 检查浏览器控制台是否有错误信息

### 开发调试

- 后端日志：查看控制台输出
- 前端调试：使用浏览器开发者工具
- API测试：可以使用Postman等工具测试API接口

## 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

## 许可证

本项目采用MIT许可证。
