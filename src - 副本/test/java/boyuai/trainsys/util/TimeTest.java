package boyuai.trainsys.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Time 类的单元测试
 * 测试时间解析、验证和各种边界情况
 */
public class TimeTest {

    @Test
    void validTime_shouldParseCorrectly() {
        // 测试有效时间
        Time time1 = new Time("14:30 06-15");
        assertEquals(14, time1.getHour());
        assertEquals(30, time1.getMin());
        assertEquals(6, time1.getDate().getMon());
        assertEquals(15, time1.getDate().getMday());
        assertEquals("14:30 06-15", time1.toString());

        Time time2 = new Time("00:00 01-01");
        assertEquals(0, time2.getHour());
        assertEquals(0, time2.getMin());

        Time time3 = new Time("23:59 12-31");
        assertEquals(23, time3.getHour());
        assertEquals(59, time3.getMin());
    }

    @Test
    void invalidFormat_nullString_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Time((String) null);
        });
        assertTrue(exception.getMessage().contains("时间格式必须是 HH:MM MM-DD"));
    }

    @Test
    void invalidFormat_wrongLength_shouldThrowException() {
        // 太短
        assertThrows(IllegalArgumentException.class, () -> new Time("14:30 6-15"));
        assertThrows(IllegalArgumentException.class, () -> new Time("4:30 06-15"));
        
        // 太长
        assertThrows(IllegalArgumentException.class, () -> new Time("14:30:00 06-15"));
        assertThrows(IllegalArgumentException.class, () -> new Time("14:30 2024-06-15"));
    }

    @Test
    void invalidFormat_wrongSeparators_shouldThrowException() {
        // 错误的时间分隔符
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new Time("14-30 06-15");
        });
        assertTrue(exception1.getMessage().contains("第3位必须是 ':'"));
        
        // 缺少空格 (这个会先触发Date异常，因为"14:3006-15"长度是11，"06-15"部分实际会导致Date解析异常)
        assertThrows(IllegalArgumentException.class, () -> {
            new Time("14:30:06-15");  // 改用明显的错误分隔符
        });
    }

    @Test
    void invalidFormat_nonNumericCharacters_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Time("ab:cd 06-15"));
        assertThrows(IllegalArgumentException.class, () -> new Time("1a:30 06-15"));
        assertThrows(IllegalArgumentException.class, () -> new Time("14:3b 06-15"));
    }

    @Test
    void invalidHour_shouldThrowException() {
        // 小时超过23
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new Time("24:00 06-15");
        });
        assertTrue(exception1.getMessage().contains("小时必须在 0-23 之间"));
        
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            new Time("25:30 06-15");
        });
        assertTrue(exception2.getMessage().contains("小时必须在 0-23 之间"));
    }

    @Test
    void invalidMinute_shouldThrowException() {
        // 分钟超过59
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new Time("14:60 06-15");
        });
        assertTrue(exception1.getMessage().contains("分钟必须在 0-59 之间"));
        
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            new Time("14:99 06-15");
        });
        assertTrue(exception2.getMessage().contains("分钟必须在 0-59 之间"));
    }

    @Test
    void invalidDate_shouldThrowException() {
        // 日期部分无效会通过 Date 构造函数抛出异常
        assertThrows(IllegalArgumentException.class, () -> new Time("14:30 13-15"));
        assertThrows(IllegalArgumentException.class, () -> new Time("14:30 06-32"));
        assertThrows(IllegalArgumentException.class, () -> new Time("14:30 02-29"));
    }

    @Test
    void timeComparison_shouldWorkCorrectly() {
        Time early = new Time("10:30 06-15");
        Time late = new Time("14:45 06-15");
        Time sameDay = new Time("10:30 06-15");
        Time nextDay = new Time("09:00 06-16");

        assertTrue(early.isBefore(late));
        assertFalse(late.isBefore(early));
        assertTrue(late.isAfter(early));
        assertFalse(early.isAfter(late));
        
        assertTrue(early.equals(sameDay));
        assertTrue(early.isBefore(nextDay));
    }

    @Test
    void timeArithmetic_shouldWorkCorrectly() {
        Time time = new Time("23:50 06-15");
        
        // 加20分钟应该跨到第二天
        Time result1 = time.addMinutes(20);
        assertEquals(0, result1.getHour());
        assertEquals(10, result1.getMin());
        assertEquals(16, result1.getDate().getMday());
        
        // 减100分钟
        Time time2 = new Time("01:30 06-15");
        Time result2 = time2.subtractMinutes(100);
        assertEquals(23, result2.getHour());
        assertEquals(50, result2.getMin());
        assertEquals(14, result2.getDate().getMday());
    }

    @Test
    void timeDifference_shouldCalculateCorrectly() {
        Time time1 = new Time("10:00 06-15");
        Time time2 = new Time("12:30 06-15");
        
        assertEquals(150, time2.differenceInMinutes(time1));
        assertEquals(-150, time1.differenceInMinutes(time2));
        
        // 跨天计算
        Time time3 = new Time("23:00 06-15");
        Time time4 = new Time("01:00 06-16");
        assertEquals(120, time4.differenceInMinutes(time3));
    }

    @Test
    void intConstructor_shouldWorkCorrectly() {
        Time time = new Time(14, 30, 6, 15);
        assertEquals(14, time.getHour());
        assertEquals(30, time.getMin());
        assertEquals(6, time.getDate().getMon());
        assertEquals(15, time.getDate().getMday());
    }
}

