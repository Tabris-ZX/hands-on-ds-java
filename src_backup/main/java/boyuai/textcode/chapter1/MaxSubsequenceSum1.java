package boyuai.textcode.chapter1;

// 代码清单1-2 最大连续子序列和的枚举算法及其测试程序
// 输入序列长度n，以及序列中的数字a[i]，输出最大连续子序列的和以及子序列的终点和起点
// 样例输入：
// 5
// 2 3 -1 2 -3
// 样例输出：
// 最大连续子序列和为：6
// 子序列的起点：0，子序列的终点：3

import java.util.Scanner;

public class MaxSubsequenceSum1 {

    // 解法一：枚举法(O(n^3))
    public static int maxSubsequenceSum1(int[] array, int size, int s, int e) {
        int maxSum = 0;
        for (int i = 0; i < size; i++) {
            int sum = 0;
            for (int j = i; j < size; j++) {
                sum += array[j];
                if (sum > maxSum) maxSum = sum;
            }
        }
        s = array[0];
        e = array[size - 1];
        System.out.println("s = " + s + ", e = " + e);
        return maxSum;
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; ++i) {
            a[i] = sc.nextInt();
        }
        int start = 0, end = 0;
        System.out.println(maxSubsequenceSum1(a, n, start, end));
        sc.close();
    }
}
