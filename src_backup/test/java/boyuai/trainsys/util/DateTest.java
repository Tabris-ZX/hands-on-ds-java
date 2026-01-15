package boyuai.trainsys.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Date 类的单元测试
 * 测试日期解析、验证和各种边界情况
 */
public class DateTest {

    @Test
    void validDate_shouldParseCorrectly() {
        // 测试有效日期
        Date date1 = new Date("06-15");
        assertEquals(6, date1.getMon());
        assertEquals(15, date1.getMday());
        assertEquals("06-15", date1.toString());

        Date date2 = new Date("01-01");
        assertEquals(1, date2.getMon());
        assertEquals(1, date2.getMday());

        Date date3 = new Date("12-31");
        assertEquals(12, date3.getMon());
        assertEquals(31, date3.getMday());
    }

    @Test
    void invalidFormat_nullString_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Date((String) null);
        });
        assertTrue(exception.getMessage().contains("日期格式必须是 MM-DD"));
    }

    @Test
    void invalidFormat_wrongLength_shouldThrowException() {
        // 太短
        assertThrows(IllegalArgumentException.class, () -> new Date("6-15"));
        assertThrows(IllegalArgumentException.class, () -> new Date("06-5"));
        
        // 太长
        assertThrows(IllegalArgumentException.class, () -> new Date("2024-06-15"));
        assertThrows(IllegalArgumentException.class, () -> new Date("06-15-"));
    }

    @Test
    void invalidFormat_wrongSeparator_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Date("06/15");
        });
        assertTrue(exception.getMessage().contains("第3位必须是 '-'"));
        
        assertThrows(IllegalArgumentException.class, () -> new Date("06.15"));
        assertThrows(IllegalArgumentException.class, () -> new Date("06:15"));
    }

    @Test
    void invalidFormat_nonNumericCharacters_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Date("ab-cd"));
        assertThrows(IllegalArgumentException.class, () -> new Date("0a-15"));
        assertThrows(IllegalArgumentException.class, () -> new Date("06-1b"));
    }

    @Test
    void invalidMonth_shouldThrowException() {
        // 月份为0
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new Date("00-15");
        });
        assertTrue(exception1.getMessage().contains("月份必须在 1-12 之间"));
        
        // 月份超过12
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            new Date("13-15");
        });
        assertTrue(exception2.getMessage().contains("月份必须在 1-12 之间"));
        
        // 你之前的错误数据
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            new Date("20-47");
        });
        assertTrue(exception3.getMessage().contains("月份必须在 1-12 之间"));
    }

    @Test
    void invalidDay_shouldThrowException() {
        // 日期为0
        assertThrows(IllegalArgumentException.class, () -> new Date("06-00"));
        
        // 1月有31天，32天无效
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new Date("01-32");
        });
        assertTrue(exception1.getMessage().contains("1月的日期必须在 1-31 之间"));
        
        // 2月只有28天（平年）
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            new Date("02-29");
        });
        assertTrue(exception2.getMessage().contains("2月的日期必须在 1-28 之间"));
        
        // 4月只有30天
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            new Date("04-31");
        });
        assertTrue(exception3.getMessage().contains("4月的日期必须在 1-30 之间"));
    }

    @Test
    void dateComparison_shouldWorkCorrectly() {
        Date early = new Date("03-15");
        Date late = new Date("08-20");
        Date same1 = new Date("06-10");
        Date same2 = new Date("06-10");

        assertTrue(early.isBefore(late));
        assertFalse(late.isBefore(early));
        assertTrue(late.isAfter(early));
        assertFalse(early.isAfter(late));
        
        assertTrue(same1.equals(same2));
        assertFalse(same1.isBefore(same2));
        assertFalse(same1.isAfter(same2));
    }

    @Test
    void dateArithmetic_shouldWorkCorrectly() {
        Date date = new Date("01-30");
        
        // 加2天应该是2月1日
        Date result1 = date.addDays(2);
        assertEquals(2, result1.getMon());
        assertEquals(1, result1.getMday());
        
        // 减30天应该是12月31日（上一年，但月份会循环）
        Date result2 = date.subtractDays(30);
        assertEquals(12, result2.getMon());
        assertEquals(31, result2.getMday());
    }

    @Test
    void dateDifference_shouldCalculateCorrectly() {
        Date date1 = new Date("06-01");
        Date date2 = new Date("06-15");
        
        assertEquals(14, date2.difference(date1));
        assertEquals(-14, date1.difference(date2));
    }

    @Test
    void intConstructor_shouldWorkWithoutValidation() {
        // 注意：int构造函数不做验证，这是设计决定
        // 如果需要，可以考虑也添加验证
        Date date = new Date(6, 15);
        assertEquals(6, date.getMon());
        assertEquals(15, date.getMday());
    }
}

