package boyuai.trainsys.util;

import boyuai.trainsys.config.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 定长字符串类
 * 用于作为索引，替代 C++ 中的定长字符数组
 */
@Getter
@Setter
public class FixedString implements Comparable<FixedString> {
    private final String value;

    public FixedString() {
        this.value = "";
    }

    public FixedString(String str) {
        if (str == null) {
            this.value = "";
        } else if (str.length() > Config.MAX_STRING_LENGTH) {
            this.value = str.substring(0, Config.MAX_STRING_LENGTH);
        } else {
            this.value = str;
        }
    }

    public FixedString(FixedString other) {
        this.value = other.value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FixedString that = (FixedString) obj;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(FixedString other) {
        return this.value.compareTo(other.value);
    }
}
