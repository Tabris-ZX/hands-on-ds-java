# 火车票务管理系统 - 前端使用说明

## 项目结构

```
frontend/
├── src/
│   ├── views/          # 页面组件
│   ├── router/         # 路由配置
│   ├── store/          # 状态管理
│   ├── App.vue         # 主组件
│   └── main.js         # 入口文件
├── index.html          # HTML模板
├── package.json        # 依赖配置
└── vite.config.js      # Vite配置
```

## 安装依赖

### 方式一：使用配置好的镜像（推荐）
项目已配置淘宝镜像，直接安装即可：
```bash
cd frontend
npm install
```

### 方式二：手动指定镜像
如果仍然很慢，可以使用：
```bash
npm install --registry=https://registry.npmmirror.com
```

### 方式三：使用 yarn（更快）
```bash
# 安装 yarn（如果还没有）
npm install -g yarn

# 使用 yarn 安装依赖
cd frontend
yarn install
```

### 方式四：使用 pnpm（最快）
```bash
# 安装 pnpm（如果还没有）
npm install -g pnpm

# 使用 pnpm 安装依赖
cd frontend
pnpm install
```

## 运行前端

```bash
npm run dev
# 或
yarn dev
# 或
pnpm dev
```

前端将在 http://localhost:3000 启动

## 运行后端

确保后端 SpringBoot 应用运行在 http://localhost:8080

## 功能说明

### 普通用户功能
- **登录/注册**: 用户登录和注册
- **票务查询**: 查询指定车次的余票
- **购票**: 购买车票
- **我的订单**: 查看个人订单，支持退票
- **路线查询**: 查询站点间的路线和连通性

### 管理员功能（权限 >= 10）
- **车次管理**: 添加和查询车次信息
- **票务管理**: 发售和停售车票

## API 说明

前端通过代理访问后端 API，所有请求以 `/api` 开头，会被代理到 `http://localhost:8080`

## 注意事项

1. 时间格式：`HH:MM MM-DD`，例如 `08:00 06-15` (6月15日早上8点)
2. 首发时间格式：`HH:MM`，例如 `08:00`
3. 站点输入：用 `/` 分隔，例如 `北京/天津/济南`
4. 时长和票价：用 `/` 分隔，例如 `35/95/160`

## 加速安装提示

如果 npm install 很慢，可以：
1. 使用项目已配置的 `.npmrc` 文件（自动使用淘宝镜像）
2. 使用 `yarn` 或 `pnpm` 替代 npm（通常更快）
3. 检查网络连接，确保可以访问镜像源

