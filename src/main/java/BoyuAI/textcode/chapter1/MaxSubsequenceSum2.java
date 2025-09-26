package BoyuAI.textcode.chapter1;
// 代码清单1-3 最大连续子序列和的O(n^2)两重循环算法及其测试程序
// 输入序列长度n，以及序列中的数字a[i]，输出最大连续子序列的和以及子序列的终点和起点
// 样例输入：
// 5
// 2 3 -1 2 -3
// 样例输出：
// 最大连续子序列和为：6
// 子序列的起点：0，子序列的终点：3
import java.util.Scanner;

public class MaxSubsequenceSum2 {
    // 解法二：二重循环(O(n^2))
    public static int maxSubsequenceSum(int[] a, int size, int[] result) {
        int maxSum = 0;  // 已知的最大连续子序列之和
        int start = 0, end = 0;

        for (int i = 0; i < size; i++) {    // 连续子序列的起始位置
            int thisSum = 0;                // 从i开始的连续子序列之和
            for (int j = i; j < size; j++) {  // 连续子序列的终止位置
                thisSum += a[j];  // 计算从第i到第j个元素的连续子序列之和
                if (thisSum > maxSum) {
                    maxSum = thisSum;
                    start = i;
                    end = j;
                }
            }
        }

        result[0] = start;
        result[1] = end;
        return maxSum;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int[] a = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }

        int[] result = new int[2];  // result[0]存储start，result[1]存储end
        int maxSum = maxSubsequenceSum(a, n, result);

        System.out.println("最大连续子序列和为：" + maxSum);
        System.out.println("子序列的起点：" + result[0] + "，子序列的终点：" + result[1]);

        scanner.close();
    }
}

