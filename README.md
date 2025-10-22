# 动手学数据结构与算法(Java 实现版)

## 每章节代码清单(textcode目录下)
目前在做...

## 火车票务管理系统(trainsys目录下)
本项目为 C++ 版迁移，支持命令行操作火车票务相关功能。数据自动存储于 data/ 目录。

### 构建与运行
- 构建：`mvn compile`
- 运行：`java -cp target/classes boyuai.trainsys.Main`
- 输入 `help` 查看指令，`exit` 退出

### 数据文件
| 类型    | 路径                 |
|-------|--------------------|
| 站点库   | data/station.txt   |
| 用户库   | data/users_*       |
| 调度库   | data/schedulers_*  |
| 车票库   | data/tickets_*     |
| 行程库   | data/trips_*       |

### 指令总览

| 模块    | 指令名                   | 参数示例/说明                                                                                    |
|-------|-----------------------|--------------------------------------------------------------------------------------------|
| 用户管理  | `register`            | `-i <用户ID>` `-u <用户名>` `-p <密码>`                                                           |
|       | `login`               | `-i <用户ID>` `-p <密码>`                                                                      |
|       | `logout`              | 无参数                                                                                        |
|       | `modify_password`     | `-i <用户ID>` `-p <新密码>`                                                                     |
|       | `modify_privilege`    | `-i <用户ID>` `-g <权限值>`                                                                     |
|       | `query_profile`       | `-i <用户ID>`                                                                                |
| 运行计划  | `add_train`           | `-i <车次ID>` `-m <席位数>` `-n <站点数>` `-s <站名1/站名2/...>` `-t <时长1/时长2/...>` `-p <票价1/票价2/...>` |
|       | `query_train`         | `-i <车次ID>`                                                                                |
| 票务    | `release_ticket`      | `-i <车次ID>` `-d <日期>`                                                                      |
|       | `expire_ticket`       | `-i <车次ID>` `-d <日期>`                                                                      |
|       | `query_remaining`     | `-i <车次ID>` `-d <日期>` `-f <出发站名>`                                                          |
|       | `buy_ticket`          | `-i <车次ID>` `-d <日期>` `-f <出发站名>`                                                          |
|       | `query_order`         | 无参数                                                                                        |
|       | `refund_ticket`       | `-i <车次ID>` `-d <日期>` `-f <出发站名>`                                                          |
| 路线    | `display_route`       | `-s <起点站名>` `-t <终点站名>`                                                                    |
|       | `query_best_path`     | `-s <起点站名>` `-t <终点站名>` `-p <time or price>`                                               |
|       | `query_accessibility` | `-s <起点站名>` `-t <终点站名>`                                                                    |
| 系统    | `help`                | 无参数                                                                                        |
|       | `exit`                | 无参数                                                                                        |

### 示例

```
login -i 0 -p admin
add_train -i D2282 -m 1000 -n 4 -s 北京/天津/济南/青岛 -t 35/95/160 -p 29/97/118
release_ticket -i D2282 -d 08-01
query_remaining -i D2282 -d 08-01 -f 北京
buy_ticket -i D2282 -d 08-01 -f 北京
query_order
query_best_path -s 北京 -t 青岛 -p time
```

### 差异文件(doc/difference.md)
**C++ 与 Java 实现差异说明**

### Plus版本(trainsys_plus)
**优化后的火车票务系统,项目操作更加人性化,更贴合现实项目**<br>
目前在做...