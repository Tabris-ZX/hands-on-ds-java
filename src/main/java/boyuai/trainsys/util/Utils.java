package boyuai.trainsys.util;

import java.util.List;

/**
 * 通用工具方法
 */
public class Utils {

    // 工具类禁止实例化
    private Utils() {}

    /**
     * 二分查找
     * @param data 已排序的列表
     * @param x    待查找元素
     * @param <T>  元素类型（必须可比较）
     * @return 元素索引，若不存在返回 -1
     */
    public static <T extends Comparable<T>> int binarySearch(List<T> data, T x) {
        int low = 0, high = data.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = x.compareTo(data.get(mid));
            if (cmp == 0) return mid;
            else if (cmp < 0) high = mid - 1;
            else low = mid + 1;
        }
        return -1;
    }
}
