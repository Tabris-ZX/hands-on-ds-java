package boyuai.trainsys.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 日期管理类
 * 管理平年中的日期（2月28天，总共365天）
 * 日期格式为 MM-DD
 */
@Getter
@Setter
public class Date implements Comparable<Date> {
    // 每个月的天数
    private static final int[] MDAY_NUMBER = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    // 每个月前缀天数总和
    private static final int[] PREFIX_TOTAL = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
    private int mon;   // 月份
    private int mday;  // 日

    // 构造函数
    public Date() {
    }

    public Date(int mon, int mday) {
        this.mon = mon;
        this.mday = mday;
    }

    // 从字符串构造日期，格式为 "MM-DD"
    public Date(String str) {
        // 1. 检查空值和长度
        if (str == null || str.length() != 5) {
            throw new IllegalArgumentException("日期格式必须是 MM-DD（长度为5），当前输入: " + str);
        }
        
        // 2. 检查分隔符
        if (str.charAt(2) != '-') {
            throw new IllegalArgumentException("日期格式必须是 MM-DD，第3位必须是 '-'，当前输入: " + str);
        }
        
        // 3. 检查是否为数字
        for (int i = 0; i < 5; i++) {
            if (i == 2) continue;  // 跳过分隔符
            if (!Character.isDigit(str.charAt(i))) {
                throw new IllegalArgumentException("日期中包含非数字字符，当前输入: " + str);
            }
        }
        
        // 4. 解析月份和日期
        this.mon = (str.charAt(0) - '0') * 10 + (str.charAt(1) - '0');
        this.mday = (str.charAt(3) - '0') * 10 + (str.charAt(4) - '0');
        
        // 5. 验证月份范围（1-12）
        if (mon < 1 || mon > 12) {
            throw new IllegalArgumentException(
                String.format("月份必须在 1-12 之间，当前: %d（输入: %s）", mon, str)
            );
        }
        
        // 6. 验证日期范围（1-当月最大天数）
        if (mday < 1 || mday > MDAY_NUMBER[mon]) {
            throw new IllegalArgumentException(
                String.format("%d月的日期必须在 1-%d 之间，当前: %d（输入: %s）", 
                    mon, MDAY_NUMBER[mon], mday, str)
            );
        }
    }

    // 复制构造函数
    public Date(Date other) {
        this.mon = other.mon;
        this.mday = other.mday;
    }

    /**
     * 增加天数
     * 等同于 "+"
     *
     * @param days 增加的天数
     * @return 新的日期对象
     */
    public Date addDays(int days) {
        Date ret = new Date(this);
        ret.mday += days;
        while (ret.mday > MDAY_NUMBER[ret.mon]) {
            ret.mday -= MDAY_NUMBER[ret.mon];
            ret.mon++;
            if (ret.mon > 12) ret.mon = 1;
        }
        return ret;
    }

    /**
     * 减少天数
     * 等同于 "-"
     *
     * @param days 减少的天数
     * @return 新的日期对象
     */
    public Date subtractDays(int days) {
        Date ret = new Date(this);
        ret.mday -= days;
        while (ret.mday <= 0) {
            ret.mon--;
            if (ret.mon <= 0) ret.mon = 12;
            ret.mday += MDAY_NUMBER[ret.mon];
        }
        return ret;
    }

    /**
     * 自增天数
     * 等同于 "+="
     *
     * @param days 增加的天数
     */
    public void addDaysInPlace(int days) {
        this.mday += days;
        while (mday > MDAY_NUMBER[mon]) {
            mday -= MDAY_NUMBER[mon];
            mon++;
            if (mon > 12) mon = 1;
        }
    }

    /**
     * 自减天数
     * 等同于 "-="
     *
     * @param days 减少的天数
     */
    public void subtractDaysInPlace(int days) {
        this.mday -= days;
        while (mday <= 0) {
            mon--;
            if (mon <= 0) mon = 12;
            mday += MDAY_NUMBER[mon];
        }
    }

    /**
     * 计算与另一个日期的天数差
     *
     * @param other 另一个日期
     * @return 天数差（正数表示当前日期在后，负数表示当前日期在前）
     */
    public int difference(Date other) {
        int total1 = PREFIX_TOTAL[this.mon - 1] + this.mday;
        int total2 = PREFIX_TOTAL[other.mon - 1] + other.mday;
        return total1 - total2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // 类型检查和转换
        if (!(o instanceof Date date)) return false;
        return mon == date.mon && mday == date.mday;
    }

    @Override
    public int compareTo(Date other) {
        if (this.mon != other.mon) {
            return Integer.compare(this.mon, other.mon);
        }
        return Integer.compare(this.mday, other.mday);
    }

    /**
     * 判断是否早于另一个日期
     * 等同于 "<"
     *
     * @param other 另一个日期
     */
    public boolean isBefore(Date other) {
        return this.difference(other) < 0;
    }

    /**
     * 判断是否晚于另一个日期
     * 等同于 ">"
     *
     * @param other 另一个日期
     */
    public boolean isAfter(Date other) {
        return this.difference(other) > 0;
    }

    /**
     * 判断是否早于或等于另一个日期
     * 等同于 "<="
     *
     * @param other 另一个日期
     */
    public boolean isBeforeOrEqual(Date other) {
        return this.difference(other) <= 0;
    }

    /**
     * 判断是否晚于或等于另一个日期
     * 等同于 ">="
     *
     * @param other 另一个日期
     */
    public boolean isAfterOrEqual(Date other) {
        return this.difference(other) >= 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mon, mday);
    }

    /**
     * @return 统一的返回日期字符串，格式为 "MM-DD"
     */
    @Override
    public String toString() {
        return String.format("%02d-%02d", mon, mday);
    }
}
