# 数据库重复数据问题修复说明

## 问题原因

数据库出现重复数据的主要原因：

1. **ticket_info 表缺少唯一约束**
   - 原表只有自增主键，没有唯一约束
   - 多次发售同一车票会插入重复记录
   - 修复：添加了 `UNIQUE(train_id, departure_time, departure_station)` 约束

2. **使用普通 INSERT 而非 INSERT OR REPLACE**
   - `train_scheduler`、`user_info` 等表虽然有主键，但使用普通 INSERT
   - 如果业务逻辑检查失败，仍可能插入重复数据
   - 修复：改为使用 `INSERT OR REPLACE`

## 已修复的问题

### 1. TicketManager
- ✅ 添加了唯一约束：`UNIQUE(train_id, departure_time, departure_station)`
- ✅ `releaseTicket()` 方法改为使用 `INSERT OR REPLACE`

### 2. SchedulerManager
- ✅ `addScheduler()` 方法改为使用 `INSERT OR REPLACE`

### 3. UserManager
- ✅ `insertUser()` 方法改为使用 `INSERT OR REPLACE`

## 清理现有重复数据

如果数据库中已有重复数据，请按以下步骤清理：

### 方法 1：使用 SQLite 命令行工具

```bash
# 进入项目目录
cd /path/to/hands-on-ds

# 执行清理脚本
sqlite3 data/hands-on-ds.db < data/cleanup_duplicates.sql
```

### 方法 2：使用 SQLite 图形工具

1. 打开 `data/hands-on-ds.db` 文件
2. 执行 `data/cleanup_duplicates.sql` 中的 SQL 语句

### 方法 3：手动执行 SQL

```sql
-- 删除重复的车票记录
DELETE FROM ticket_info
WHERE id NOT IN (
    SELECT MIN(id)
    FROM ticket_info
    GROUP BY train_id, departure_time, departure_station
);
```

## 注意事项

1. **备份数据库**：执行清理脚本前，请先备份数据库文件
   ```bash
   cp data/hands-on-ds.db data/hands-on-ds.db.backup
   ```

2. **表结构更新**：如果表已存在，唯一约束可能不会自动添加。需要：
   - 先删除重复数据
   - 然后重新创建表（删除表后重启应用会自动创建）

3. **数据验证**：清理后建议验证数据完整性

## 预防措施

修复后的代码已经包含以下预防措施：

1. ✅ 唯一约束防止重复插入
2. ✅ `INSERT OR REPLACE` 自动处理重复情况
3. ✅ 业务逻辑检查（如 `existScheduler`）

## 验证修复

重启应用后，重复插入操作应该：
- 自动更新现有记录（而不是创建新记录）
- 或者抛出明确的错误信息（如果违反唯一约束）


