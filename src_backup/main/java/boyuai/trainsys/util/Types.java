package boyuai.trainsys.util;

import lombok.Getter;
import lombok.Setter;

/**
 * 类型别名定义
 */
@Getter
@Setter
public class Types {
    // 用户ID类型
    public record UserID(long value) {}
        // 站点ID类型

    public record StationID(int value) {}

    // 火车ID类型
    public static class TrainID extends FixedString {
        public TrainID() {
            super();
        }

        public TrainID(String str) {
            super(str);
        }

        public TrainID(TrainID other) {
            super(other);
        }
    }

    // 站点名称类型
    public static class StationName extends FixedString {
        public StationName() {
            super();
        }

        public StationName(String str) {
            super(str);
        }

        public StationName(StationName other) {
            super(other);
        }
    }
}
