package com.itheima;


import boyuai.textcode.chapter1.MaxSubsequenceSum1;
import org.junit.jupiter.api.Test;

/**
 *  测试类
 */
public class PairTest {
    @Test
    public void testMaxSubsequenceSum1() {
        int[] a = {2, 3, -1, 2, -3};
        int start = 0, end = 0;
        int maxSum = MaxSubsequenceSum1.maxSubsequenceSum1(a, a.length, start, end);
        System.out.println(maxSum);
    }
}
