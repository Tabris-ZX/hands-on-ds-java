# npm 镜像加速配置

## 临时使用淘宝镜像
```bash
npm install --registry=https://registry.npmmirror.com
```

## 永久设置淘宝镜像
```bash
npm config set registry https://registry.npmmirror.com
```

## 恢复官方镜像
```bash
npm config set registry https://registry.npmjs.org
```

## 查看当前镜像
```bash
npm config get registry
```

## 使用 yarn（推荐，更快）
```bash
# 安装 yarn
npm install -g yarn

# 设置 yarn 镜像
yarn config set registry https://registry.npmmirror.com

# 使用 yarn 安装依赖
yarn install
```

## 使用 pnpm（最快）
```bash
# 安装 pnpm
npm install -g pnpm

# 设置 pnpm 镜像
pnpm config set registry https://registry.npmmirror.com

# 使用 pnpm 安装依赖
pnpm install
```

