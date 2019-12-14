package com.opencloud.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * 货币转换工具类
 *
 * @author: admin
 * @date: 2018/7/31 14:50
 * @description:
 */
public class CurrencyUtils extends org.apache.commons.lang3.math.NumberUtils {
    private static final Double MILLION = 10000.0;
    private static final Double MILLIONS = 1000000.0;
    private static final Double BILLION = 100000000.0;
    private static final String MILLION_UNIT = "万";
    private static final String BILLION_UNIT = "亿";
    private static final int DEFAULT_DECIMAL = 2;

    /**
     * 元转换为分
     *
     * @param fen
     * @return
     */
    public static long yuan2fen(double fen) {
        return yuan2fen(new BigDecimal(fen));
    }

    /**
     * 元转换为分
     *
     * @param fen
     * @return
     */
    public static long yuan2fen(BigDecimal fen) {
        return fen.multiply(new BigDecimal(100)).longValue();
    }

    /**
     * 分转换为元
     *
     * @param fen
     * @return
     */
    public static double fen2Yuan(long fen) {
        return fen2Yuan(new BigDecimal(fen));
    }

    /**
     * 分转换为元
     *
     * @param fen
     * @return
     */
    public static double fen2Yuan(BigDecimal fen) {
        return fen.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).doubleValue();
    }


    /**
     * 对数字进行四舍五入，保留2位小数
     *
     * @param number   要四舍五入的数字
     * @param decimal  保留的小数点数
     * @param rounding 是否四舍五入
     * @return
     * @author
     * @version 1.00.00
     * @date 2018年1月18日
     */
    public static double formatNumber(double number, int decimal, boolean rounding) {
        BigDecimal bigDecimal = new BigDecimal(number);
        if (rounding) {
            return bigDecimal.setScale(decimal, RoundingMode.HALF_UP).doubleValue();
        } else {
            return bigDecimal.setScale(decimal, RoundingMode.DOWN).doubleValue();
        }
    }

    /**
     * 对四舍五入的数据进行补0显示，即显示.00
     *
     * @return
     * @author
     * @version 1.00.00
     * @date 2018年1月23日
     */
    public static String zeroFill(double number) {
        String value = String.valueOf(number);
        String str = ".";
        if (value.indexOf(str) < 0) {
            value = value + ".00";
        } else {
            String decimalValue = value.substring(value.indexOf(str) + 1);

            if (decimalValue.length() < DEFAULT_DECIMAL) {
                value = value + "0";
            }
        }
        return value;
    }

    /**
     * 将数字转换成以万为单位或者以亿为单位，因为在前端数字太大显示有问题
     *
     * @param amount 报销金额
     * @return 120.00
     * 1816.00亿
     * 122.21万
     * 1.29亿
     */
    public static String formatAmountCny(double amount) {
        //最终返回的结果值
        String result = String.valueOf(amount);
        //四舍五入后的值
        double value = 0;
        //转换后的值
        double tempValue = 0;
        //余数
        double remainder = 0;

        //金额大于1百万小于1亿
        if (amount > MILLIONS && amount < BILLION) {
            tempValue = amount / MILLION;
            remainder = amount % MILLION;

            //余数小于5000则不进行四舍五入
            if (remainder < (MILLION / DEFAULT_DECIMAL)) {
                value = formatNumber(tempValue, DEFAULT_DECIMAL, false);
            } else {
                value = formatNumber(tempValue, DEFAULT_DECIMAL, true);
            }
            //如果值刚好是10000万，则要变成1亿
            if (value == MILLION) {
                result = zeroFill(value / MILLION) + BILLION_UNIT;
            } else {
                result = zeroFill(value) + MILLION_UNIT;
            }
        }
        //金额大于1亿
        else if (amount > BILLION) {
            tempValue = amount / BILLION;
            remainder = amount % BILLION;

            //余数小于50000000则不进行四舍五入
            if (remainder < (BILLION / DEFAULT_DECIMAL)) {
                value = formatNumber(tempValue, DEFAULT_DECIMAL, false);
            } else {
                value = formatNumber(tempValue, DEFAULT_DECIMAL, true);
            }
            result = zeroFill(value) + BILLION_UNIT;
        } else {
            result = zeroFill(amount);
        }
        return result;
    }

    /**
     * 测试方法入口
     *
     * @param args
     * @author
     * @version 1.00.00
     * @date 2018年1月18日
     */
    public static void main(String[] args) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        System.out.println(currencyFormat.format(222222222222L));
        //554,545.4544;
        System.out.println(yuan2fen(120.445));
        System.out.println(formatAmountCny(1200.35));
        System.out.println(formatAmountCny(12000.35));
        System.out.println(formatAmountCny(120000.35));
        System.out.println(formatAmountCny(1200000.35));
        System.out.println(formatAmountCny(12000000.35));
        System.out.println(formatAmountCny(120000000.35));
        System.out.println(formatAmountCny(1200000000.35));
    }
}
