package com.jxust.qq.superquestionlib;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ParameterTest {
    private int a ;
    private int b ;
    private int except;

    public ParameterTest(int a, int b, int except) {
        this.a = a;
        this.b = b;
        this.except = except;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        String s = "";
        s.split("(?<=\\$)[0-9\\.]*");
        return Arrays.asList(new Object[][]{
                {1, 2, 3}
        });
    }

    @Test
    public void test() {
        Assert.assertEquals(except, a + b);
    }

    public int minCostClimbingStairs(int[] cost) {
        int pre = 0, cur = 0;
        int n = cost.length;
        for (int i = 0; i <= n; i++) {
            if (i == n) {
                cur = Math.min(cur, pre);
                break;
            }
            int temp = cur;
            cur = Math.min(pre, cur) + cost[i];
            pre = temp;
        }
        return cur;
    }
}
