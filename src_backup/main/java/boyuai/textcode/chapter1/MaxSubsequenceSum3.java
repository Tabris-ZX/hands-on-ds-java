package boyuai.textcode.chapter1;

// 代码清单1-4 最大连续子序列和的O(nlogn)递归算法及其测试程序
// 输入序列长度n，以及序列中的数字a[i]，输出最大连续子序列的和以及子序列的终点和起点
// 样例输入：
// 5
// 2 3 -1 2 -3
// 样例输出：
// 最大连续子序列和为：6
// 子序列的起点：0，子序列的终点：3

import java.util.Scanner;

public class MaxSubsequenceSum3 {
    // 分治，递归O(nlogn)
    public static int maxSum(int[] a, int left, int right, int[] result) {
        int maxLeft, maxRight, center;
        int leftSum = 0, rightSum = 0;
        int maxLeftTmp = 0, maxRightTmp = 0;
        int[] resultL =  new int[2], resultR = new int[2];

        if (left == right) {
            result[0] = result[1] = left;
            return a[left] > 0 ? a[left] : 0;
        }
        center = (left + right) / 2;
        maxLeft = maxSum(a, left, center, resultL);
        maxRight = maxSum(a, center + 1, right, resultR);

        result[0] = center;
        for (int i = center; i >= left; i--) {
            leftSum += a[i];
            if (leftSum > maxLeftTmp) {
                maxLeftTmp = leftSum;
                result[0] = i;
            }
        }
        result[1] = center + 1;
        for (int i = center + 1; i <= right; i++) {
            rightSum += a[i];
            if (rightSum > maxRightTmp) {
                maxRightTmp = rightSum;
                result[1] = i;
            }
        }

        // 求3种情况的最大连续子序列和
        if (maxLeft > maxRight) {
            if (maxLeft > maxLeftTmp + maxRightTmp) {
                result[0] = resultL[0];
                result[1] = resultL[1];
                return maxLeft;
            } else {
                return maxLeftTmp + maxRightTmp;
            }
        } else if (maxRight > maxLeftTmp + maxRightTmp) {
            result[0] = resultR[0];
            result[1] = resultR[1];
            return maxRight;
        } else {
            return maxLeftTmp + maxRightTmp;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = sc.nextInt();
        }
        int[] result = new int[2];
        System.out.println("最大连续子序列和为：" + maxSum(a, 0, n - 1, result));
        System.out.println("子序列的起点：" + result[0] + "，子序列的终点：" + result[1]);
    }
}
