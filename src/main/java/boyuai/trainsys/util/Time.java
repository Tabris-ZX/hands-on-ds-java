package boyuai.trainsys.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 时间管理类
 * 管理某一天的具体时间（小时、分钟），支持跨日计算
 * 时间格式为 HH:MM
 */
@Setter
@Getter
public class Time {
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
     * 从字符串构造时间，假定格式为 "HH:MM MM-DD"
     *
     * @param str 时间字符串
     */
    public Time(String str) {
        this.hour = (str.charAt(0) - '0') * 10 + (str.charAt(1) - '0');
        this.min = (str.charAt(3) - '0') * 10 + (str.charAt(4) - '0');
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
                date.addDays(1); // 跨日处理
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
                date.subtractDays(1); // 跨日处理
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
     * @return 格式化时间字符串，格式为 "HH:MM MM-DD"
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d %s", hour, min, date.toString());
    }
}
