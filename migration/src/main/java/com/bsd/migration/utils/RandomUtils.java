package com.bsd.migration.utils;

import java.util.Random;

/**
 * @author liujianhong
 */
public class RandomUtils {
    /**
     * 随机生成位数，并且每位数都不重复
     */
    public static int getNum(int length) {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = result * 10 + array[i];
        }
        if (String.valueOf(result).length() == length) {
            return result;
        } else {
            return getNum(length);
        }
    }

    public static void main(String[] args) {
        //生成一千个随机六位数
        int length = 100;
        for (int i = 0; i < length; i++) {
            // int num = (int) ((Math.random() * 9 + 1) * 10000);
            int num = getNum(5);
            System.out.println(num);
        }
    }
}
