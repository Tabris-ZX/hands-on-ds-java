package boyuai.textcode.chapter1;

// 代码清单1-5 最大连续子序列和的线性算法及其测试程序
// 输入序列长度n，以及序列中的数字a[i]，输出最大连续子序列的和以及子序列的终点和起点
// 样例输入：
// 5
// 2 3 -1 2 -3
// 样例输出：
// 最大连续子序列和为：6
// 子序列的起点：0，子序列的终点：3

import java.util.Scanner;

public class MaxSubsequenceSum4 {
    // 最大连续子序列和问题的线性算法（Kadane算法）O(O(n))
    public static int maxSum(int[] a, int n, int[] result) {
        int thisSum = 0, maxSum = 0, startTmp = 0;
        int start = 0, end = 0;
        for (int i = 0; i < n; i++) {
            thisSum += a[i];
            if (thisSum <= 0) {
                thisSum = 0;
                startTmp = i + 1;
            } else if (thisSum > maxSum) {
                maxSum = thisSum;
                start = startTmp;
                end = i;
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
        int[] result = new int[2];
        int maxSum = maxSum(a, n, result);
        System.out.println("最大连续子序列和为：" + maxSum);
        System.out.println("子序列的起点：" + result[0] + "，子序列的终点：" + result[1]);
    }
}
