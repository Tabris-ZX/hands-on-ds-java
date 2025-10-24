# 火车票务管理系统 API 示例操作

本文档提供了使用火车票务管理系统API的示例操作，包括用户管理、列车管理、票务操作和路线查询等。

## 基础信息

- API基础URL: `http://localhost:8080/api`
- 所有请求和响应都是JSON格式

## 用户管理

### 1. 用户注册

```bash
curl -X POST http://localhost:8080/api/register   -H "Content-Type: application/json"   -d '{
    "userId": 1001,
    "username": "张三",
    "password": "123456"
  }'
```

### 2. 用户登录

```bash
curl -X POST http://localhost:8080/api/login   -H "Content-Type: application/json"   -d '{
    "userId": 1001,
    "password": "123456"
  }'
```

### 3. 查询用户信息

```bash
curl -X GET http://localhost:8080/api/user/1001
```

### 4. 修改用户权限

```bash
curl -X PUT http://localhost:8080/api/user/1001/privilege   -H "Content-Type: application/json"   -d '{
    "privilege": 2
  }'
```

### 5. 修改用户密码

```bash
curl -X PUT http://localhost:8080/api/user/1001/password   -H "Content-Type: application/json"   -d '{
    "password": "newpassword123"
  }'
```

### 6. 用户登出

```bash
curl -X POST http://localhost:8080/api/logout
```

## 列车管理

### 1. 添加列车

```bash
curl -X POST http://localhost:8080/api/train   -H "Content-Type: application/json"   -d '{
    "trainId": "G1234",
    "seatNum": 500,
    "stationCount": 5,
    "stations": ["北京", "天津", "济南", "南京", "上海"],
    "durations": [30, 120, 180, 90],
    "prices": [55, 120, 200, 100]
  }'
```

### 2. 查询列车信息

```bash
curl -X GET http://localhost:8080/api/train/G1234
```

## 票务操作

### 1. 发布车票

```bash
curl -X POST http://localhost:8080/api/ticket/release   -H "Content-Type: application/json"   -d '{
    "trainId": "G1234",
    "date": "2025-11-01"
  }'
```

### 2. 查询余票

```bash
curl -X GET "http://localhost:8080/api/ticket/remaining?trainId=G1234&date=2025-11-01&departureStation=北京"
```

### 3. 购买车票

```bash
curl -X POST http://localhost:8080/api/ticket/buy   -H "Content-Type: application/json"   -d '{
    "trainId": "G1234",
    "date": "2025-11-01",
    "departureStation": "北京"
  }'
```

### 4. 查询订单

```bash
curl -X GET http://localhost:8080/api/ticket/orders
```

### 5. 退票

```bash
curl -X POST http://localhost:8080/api/ticket/refund   -H "Content-Type: application/json"   -d '{
    "trainId": "G1234",
    "date": "2025-11-01",
    "departureStation": "北京"
  }'
```

### 6. 过期车票

```bash
curl -X POST http://localhost:8080/api/ticket/expire   -H "Content-Type: application/json"   -d '{
    "trainId": "G1234",
    "date": "2025-11-01"
  }'
```

## 路线查询

### 1. 显示所有路线

```bash
curl -X GET "http://localhost:8080/api/route/display?startStation=北京&endStation=上海"
```

### 2. 查询最佳路径（时间优先）

```bash
curl -X GET "http://localhost:8080/api/route/best?startStation=北京&endStation=上海&preference=time"
```

### 3. 查询最佳路径（价格优先）

```bash
curl -X GET "http://localhost:8080/api/route/best?startStation=北京&endStation=上海&preference=price"
```

### 4. 可达性查询

```bash
curl -X GET "http://localhost:8080/api/route/accessibility?startStation=北京&endStation=广州"
```

## 完整示例流程

以下是一个完整的示例流程，展示从用户注册到购买车票的整个过程：

```bash
# 1. 注册用户
curl -X POST http://localhost:8080/api/register   -H "Content-Type: application/json"   -d '{
    "userId": 2001,
    "username": "李四",
    "password": "123456"
  }'

# 2. 登录
curl -X POST http://localhost:8080/api/login   -H "Content-Type: application/json"   -d '{
    "userId": 2001,
    "password": "123456"
  }'

# 3. 添加列车（管理员操作）
curl -X POST http://localhost:8080/api/train   -H "Content-Type: application/json"   -d '{
    "trainId": "D5678",
    "seatNum": 300,
    "stationCount": 4,
    "stations": ["北京", "石家庄", "郑州", "武汉"],
    "durations": [60, 120, 150],
    "prices": [60, 120, 150]
  }'

# 4. 发布车票（管理员操作）
curl -X POST http://localhost:8080/api/ticket/release   -H "Content-Type: application/json"   -d '{
    "trainId": "D5678",
    "date": "2025-11-05"
  }'

# 5. 查询余票
curl -X GET "http://localhost:8080/api/ticket/remaining?trainId=D5678&date=2025-11-05&departureStation=北京"

# 6. 购买车票
curl -X POST http://localhost:8080/api/ticket/buy   -H "Content-Type: application/json"   -d '{
    "trainId": "D5678",
    "date": "2025-11-05",
    "departureStation": "北京"
  }'

# 7. 查询订单
curl -X GET http://localhost:8080/api/ticket/orders

# 8. 查询路线
curl -X GET "http://localhost:8080/api/route/display?startStation=北京&endStation=武汉"

# 9. 登出
curl -X POST http://localhost:8080/api/logout
```

## 注意事项

1. 某些操作（如添加列车、发布车票）可能需要管理员权限
2. 购票前需要先登录系统
3. 日期格式应为 "YYYY-MM-DD"
4. 站点名称必须与系统中已存在的站点匹配
5. 列车ID、站点ID等参数大小写敏感

## 错误处理

所有API调用都会返回一个包含"success"字段的JSON对象：
- 当success为true时，表示操作成功
- 当success为false时，表示操作失败，"message"字段会包含错误信息

示例错误响应：
```json
{
  "success": false,
  "message": "用户不存在"
}
```
