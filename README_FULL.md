# 火车票务管理系统 - 完整版

## 项目概述

这是一个完整的火车票务管理系统，包含：
- **后端**: SpringBoot + SQLite
- **前端**: Vue 3 + Element Plus + Vite

## 快速开始

### 1. 后端启动

```bash
# 编译项目
mvn clean compile

# 运行 SpringBoot 应用
mvn spring-boot:run

# 或者运行主类
java -cp target/classes boyuai.trainsys.TrainSystemApplication
```

后端将在 http://localhost:8080 启动

### 2. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:3000 启动

## 默认管理员账号

- **用户ID**: 0
- **用户名**: admin
- **密码**: admin

## 功能列表

### 普通用户功能
- ✅ 用户注册和登录
- ✅ 查询余票
- ✅ 购买车票
- ✅ 查看订单
- ✅ 退票
- ✅ 路线查询（连通性、所有路线、最优路线）

### 管理员功能（权限 >= 10）
- ✅ 添加车次
- ✅ 查询车次信息
- ✅ 发售车票
- ✅ 停售车票

## API 接口

### 用户管理
- `POST /api/user/login` - 登录
- `POST /api/user/register` - 注册
- `POST /api/user/logout` - 登出

### 车次管理（管理员）
- `POST /api/train/add` - 添加车次
- `GET /api/train/query/{trainId}` - 查询车次

### 票务管理
- `POST /api/ticket/release` - 发售车票（管理员）
- `POST /api/ticket/expire` - 停售车票（管理员）
- `POST /api/ticket/remaining` - 查询余票
- `POST /api/ticket/buy` - 购票
- `POST /api/ticket/refund` - 退票
- `GET /api/ticket/orders` - 查询我的订单

### 路线查询
- `POST /api/route/findAll` - 查询所有路线
- `POST /api/route/best` - 查询最优路线
- `POST /api/route/accessibility` - 查询连通性
- `GET /api/route/stations` - 获取所有站点

## 数据格式说明

### 时间格式
- **完整时间**: `HH:MM MM-DD`，例如 `08:00 06-15` (6月15日早上8点)
- **首发时间**: `HH:MM`，例如 `08:00`

### 添加车次格式
- **站点**: 用 `/` 分隔，例如 `北京/天津/济南/青岛`
- **时长**: 用 `/` 分隔的数字（分钟），例如 `35/95/160`
- **票价**: 用 `/` 分隔的数字（元），例如 `29/97/118`

注意：n个站点对应n-1个区段，所以时长和票价的数量应该是站点数-1

## 数据库

系统使用 SQLite 数据库，数据文件位于 `data/hands-on-ds.db`

站点数据从 `data/station.txt` 文件加载（如果数据库为空）

## 技术栈

### 后端
- Java 21
- Spring Boot 3.2.0
- SQLite
- Maven

### 前端
- Vue 3
- Element Plus
- Vue Router
- Axios
- Vite

## 目录结构

```
.
├── src/main/java/boyuai/trainsys/
│   ├── controller/      # REST API 控制器
│   ├── service/        # 业务逻辑服务
│   ├── dto/           # 数据传输对象
│   ├── config/        # 配置类
│   ├── core/          # 核心业务逻辑
│   ├── manager/       # 数据管理器
│   └── util/          # 工具类
├── src/main/resources/
│   └── application.properties  # SpringBoot 配置
├── frontend/          # Vue 前端项目
│   ├── src/
│   │   ├── views/     # 页面组件
│   │   ├── router/    # 路由
│   │   └── store/     # 状态管理
│   └── package.json
└── pom.xml            # Maven 配置

```

## 开发说明

### 后端开发
1. 修改 Java 代码后，Maven 会自动编译
2. SpringBoot 支持热重载（需要配置 IDE）

### 前端开发
1. Vite 支持热模块替换（HMR）
2. 修改代码后浏览器自动刷新

## 常见问题

### 1. 后端启动失败
- 检查 Java 版本是否为 21
- 检查端口 8080 是否被占用
- 检查数据库文件路径是否正确

### 2. 前端启动失败
- 确保已安装 Node.js (建议 16+)
- 删除 `node_modules` 后重新 `npm install`

### 3. API 请求失败
- 确保后端已启动
- 检查浏览器控制台的错误信息
- 检查网络请求的响应状态码

## 许可证

本项目仅供学习和参考使用。

