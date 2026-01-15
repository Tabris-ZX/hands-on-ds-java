package boyuai.trainsys.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.Objects;

/**
 * 时间管理类
 * 管理某一天的具体时间（小时、分钟），支持跨日计算
 * 时间格式为 HH:MM
 */
@Setter
@Getter
public class Time implements Comparable<Time> {
    private final Date date; // 所属日期
    private int hour;  // 小时（24小时制）
    private int min;   // 分钟

    /**
     * 等同于"default"
     */
    public Time() {
        this.date = new Date();
        this.hour = 0;
        this.min = 0;
    }

    public Time(Time time) {
        this.date = new Date(time.date);
        this.hour = time.hour;
        this.min = time.min;
    }

    public Time(int hour, int min, int mon, int mday) {
        this.date = new Date(mon, mday);
        this.hour = hour;
        this.min = min;
    }

    /**
     * 从字符串构造时间，支持两种格式：
     * 1. "HH:MM_MM-DD"（下划线分隔，推荐）
     * 2. "HH:MM MM-DD"（空格分隔，兼容）
     *
     * @param str 时间字符串
     */
    public Time(String str) {
        // 1. 检查空值和长度（"HH:MM_MM-DD" 或 "HH:MM MM-DD" 长度都为11）
        if (str == null || str.length() != 11) {
            throw new IllegalArgumentException("时间格式必须是 HH:MM_MM-DD 或 HH:MM MM-DD（长度为11），当前输入: " + str);
        }
        
        // 2. 检查分隔符位置
        if (str.charAt(2) != ':') {
            throw new IllegalArgumentException("时间格式错误，第3位必须是 ':'，当前输入: " + str);
        }
        
        // 兼容空格和下划线两种分隔符
        char separator = str.charAt(5);
        if (separator != ' ' && separator != '_') {
            throw new IllegalArgumentException("时间格式错误，第6位必须是空格或下划线，当前输入: " + str);
        }
        
        // 3. 检查时分部分是否为数字
        for (int i = 0; i < 5; i++) {
            if (i == 2) continue;  // 跳过冒号
            if (!Character.isDigit(str.charAt(i))) {
                throw new IllegalArgumentException("时间部分包含非数字字符，当前输入: " + str);
            }
        }
        
        // 4. 解析小时和分钟
        this.hour = (str.charAt(0) - '0') * 10 + (str.charAt(1) - '0');
        this.min = (str.charAt(3) - '0') * 10 + (str.charAt(4) - '0');
        
        // 5. 验证小时范围（0-23）
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException(
                String.format("小时必须在 0-23 之间，当前: %d（输入: %s）", hour, str)
            );
        }
        
        // 6. 验证分钟范围（0-59）
        if (min < 0 || min > 59) {
            throw new IllegalArgumentException(
                String.format("分钟必须在 0-59 之间，当前: %d（输入: %s）", min, str)
            );
        }
        
        // 7. 解析日期部分（Date构造函数会进行验证）
        this.date = new Date(str.substring(6)); // 解析 MM-DD
    }

    /**
     * 自增分钟，支持跨日
     * 等同于 "+="
     *
     * @param minutes 要增加的分钟数
     */
    public void addMinutesInPlace(int minutes) {
        min += minutes;
        while (min >= 60) {
            min -= 60;
            hour++;
            if (hour >= 24) {
                hour -= 24;
                date.addDaysInPlace(1); // 跨日处理
            }
        }
    }

    /**
     * 自减分钟，支持跨日
     * 等同于 "-="
     *
     * @param minutes 要减去的分钟数
     */
    public void subtractMinutesInPlace(int minutes) {
        min -= minutes;
        while (min < 0) {
            min += 60;
            hour--;
            if (hour < 0) {
                hour += 24;
                date.subtractDaysInPlace(1); // 跨日处理
            }
        }
    }

    /**
     * 增加分钟，支持跨日
     * 等同于 "+"
     *
     * @return 新的时间对象
     */
    public Time addMinutes(int minutes) {
        Time ret = new Time(this);
        ret.addMinutesInPlace(minutes);
        return ret;
    }

    /**
     * 减少分钟，支持跨日
     * 等同于 "-"
     *
     * @return 新的时间对象
     */
    public Time subtractMinutes(int minutes) {
        Time ret = new Time(this);
        ret.subtractMinutesInPlace(minutes);
        return ret;
    }


    /**
     * 比较时间是否相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //模式变量 类型检查和转换
        if (!(o instanceof Time time)) return false;
        return hour == time.hour && min == time.min && Objects.equals(date, time.date);
    }


    /**
     * 等同于 "<"
     */
    public boolean isBefore(Time other) {
        if (!date.equals(other.date)) return date.isBefore(other.date);
        if (hour != other.hour) return hour < other.hour;
        return min < other.min;
    }

    /**
     * 等同于 ">"
     */
    public boolean isAfter(Time other) {
        return !this.equals(other) && !isBefore(other);
    }

    /**
     * 等同于 "<="
     */
    public boolean isBeforeOrEqual(Time other) {
        return this.equals(other) || isBefore(other);
    }

    /**
     * 等同于 ">="
     */
    public boolean isAfterOrEqual(Time other) {
        return this.equals(other) || isAfter(other);
    }

    /**
     * 计算与另一个时间的分钟差
     *
     * @param other 另一个时间
     * @return 分钟差
     */
    public int differenceInMinutes(Time other) {
        int mins = (date.difference(other.date)) * 1440;
        mins += (hour - other.hour) * 60;
        mins += (min - other.min);
        return mins;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, hour, min);
    }

    /**
     * @return 格式化时间字符串，格式为 "HH:MM_MM-DD"（使用下划线分隔，避免命令行解析问题）
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d_%s", hour, min, date.toString());
    }

    @Override
    public int compareTo(Time other) {
        int dateCompare = this.date.compareTo(other.date);
        if (dateCompare != 0) return dateCompare;

        if (this.hour != other.hour) {
            return Integer.compare(this.hour, other.hour);
        }

        return Integer.compare(this.min, other.min);
    }
}
