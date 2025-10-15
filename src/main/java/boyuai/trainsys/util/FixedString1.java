package boyuai.trainsys.util;

import boyuai.trainsys.config.Config;
import java.util.Objects;


/**
 * 定长字符串类，用于代替动态 String，方便索引/比较
 * 从源代码的 Utils.h 拆分下来
 *
 */
public class FixedString1 implements Comparable<FixedString1> {
    private final char[] index = new char[Config.MAX_STRING_LENGTH];

    public FixedString1() {}

    public FixedString1(String str) {
        set(str);
    }

    public FixedString1(FixedString1 other) {
        System.arraycopy(other.index, 0, this.index, 0, Config.MAX_STRING_LENGTH);
    }

    public void set(String str) {
        char[] chars = str.toCharArray();
        int len = Math.min(chars.length, Config.MAX_STRING_LENGTH);
        System.arraycopy(chars, 0, index, 0, len);
        if (len < Config.MAX_STRING_LENGTH) {
            for (int i = len; i < Config.MAX_STRING_LENGTH; i++) {
                index[i] = '\0';
            }
        }
    }

    @Override
    public String toString() {
        return new String(index).trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //模式变量 检查和转换
        if (!(o instanceof FixedString that)) return false;
        return this.toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.toString());
    }

    /**
     * 等同于 ">"
     */
    public boolean greaterThan(FixedString other) {
        return this.toString().compareTo(other.toString()) > 0;
    }
    /**
     * 等同于 ">="
     */
    public boolean greaterOrEqual(FixedString other) {
        return this.toString().compareTo(other.toString()) >= 0;
    }
    /**
     * 等同于 "<"
     */
    public boolean lessThan(FixedString other) {
        return this.toString().compareTo(other.toString()) < 0;
    }
    /**
     * 等同于 "<="
     */
    public boolean lessOrEqual(FixedString other) {
        return this.toString().compareTo(other.toString()) <= 0;
    }

    @Override
    public int compareTo(FixedString1 o) {
        return 0;
    }
}
