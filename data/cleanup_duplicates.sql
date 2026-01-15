-- 清理重复数据的 SQL 脚本
-- 注意：执行前请备份数据库！

-- 1. 为 ticket_info 表添加唯一约束（如果表已存在，需要先删除重复数据）
-- 删除重复的车票记录，只保留 id 最小的那条
DELETE FROM ticket_info
WHERE id NOT IN (
    SELECT MIN(id)
    FROM ticket_info
    GROUP BY train_id, departure_time, departure_station
);

-- 添加唯一约束（如果表结构已更新，这个约束会自动添加）
-- 如果执行失败，说明约束已存在，可以忽略

-- 2. 清理 train_scheduler 表的重复数据（理论上不应该有，因为 train_id 是主键）
-- 但如果有，保留最新的记录
DELETE FROM train_scheduler
WHERE rowid NOT IN (
    SELECT MAX(rowid)
    FROM train_scheduler
    GROUP BY train_id
);

-- 3. 清理 user_info 表的重复数据（理论上不应该有，因为 user_id 是主键）
-- 但如果有，保留最新的记录
DELETE FROM user_info
WHERE rowid NOT IN (
    SELECT MAX(rowid)
    FROM user_info
    GROUP BY user_id
);

-- 4. 清理 trip_info 表的重复数据（可选，根据业务需求决定）
-- 如果业务允许重复订单，可以跳过这一步
-- DELETE FROM trip_info
-- WHERE id NOT IN (
--     SELECT MIN(id)
--     FROM trip_info
--     GROUP BY user_id, train_id, departure_time, departure_station, arrival_station
-- );

-- 查看清理后的数据统计
SELECT 'ticket_info' as table_name, COUNT(*) as count FROM ticket_info
UNION ALL
SELECT 'train_scheduler', COUNT(*) FROM train_scheduler
UNION ALL
SELECT 'user_info', COUNT(*) FROM user_info
UNION ALL
SELECT 'trip_info', COUNT(*) FROM trip_info;


