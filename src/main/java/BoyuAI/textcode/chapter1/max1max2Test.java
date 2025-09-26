package BoyuAI.textcode.chapter1;

// 代码清单1-1 函数max1与max2的实现及其测试程序
// 输入序列长度n，乘数d，以及序列中的数字a[i]，输出序列中的最大值和d的乘积
// 样例输入：
// 5 2
// 2 3 -1 2 -3
// 样例输出：
// max1: 6
// max2: 6

import java.util.Scanner;

public class max1max2Test {
    public static int max1(int[] array, int size, int d) {
        int max = 0, i;
        for (i = 0; i < size; ++i) array[i] *= d;
        for (i = 0; i < size; ++i)
            if (array[i] > max) max = array[i];
        return max;
    }

    public static int max2(int[] array, int size, int d) {
        int max = 0, i;
        for (i = 0; i < size; ++i) {
            if (array[i] > max) max = array[i];
        }
        return max * d;
    }

    public static void main(String[] args) {
        int n, d;
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        d = in.nextInt();
        int[] a = new int[n];
        int[] b = new int[n];
        for (int i = 0; i < n; ++i) {
            a[i] = in.nextInt();
            b[i] = a[i];
        }
        int start, end;
        System.out.println(max1(a, n, d));
        System.out.println(max2(b, n, d));
    }
}
