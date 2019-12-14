package com.bsd.payment.server.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.regex.Pattern;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 金额工具类
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
public class AmountUtil {
    /**
     * 正则：金额（精确）
     * <p>不为零</p>
     * <p>不为负数</p>
     * <p>不为小数</p>
     */
    public static final String AMOUNT = "^[1-9]\\d{0,7}$";

    /**
     * 验证金额（精确）
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isAmount(CharSequence input) {
        return isPayAmount(AMOUNT, input);
    }

    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isPayAmount(String regex, CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    /**
     * 将字符串"元"转换成"分"
     *
     * @param str
     * @return
     */
    public static String convertDollar2Cent(String str) {
        DecimalFormat df = new DecimalFormat("0.00");
        StringBuffer sb = df.format(Double.parseDouble(str),
                new StringBuffer(), new FieldPosition(0));
        int idx = sb.toString().indexOf(".");
        sb.deleteCharAt(idx);
        for (; sb.length() != 1; ) {
            if (sb.charAt(0) == '0') {
                sb.deleteCharAt(0);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串"分"转换成"元"（长格式），如：100分被转换为1.00元。
     *
     * @param s
     * @return
     */
    public static String convertCent2Dollar(String s) {
        if ("".equals(s) || s == null) {
            return "";
        }
        long l;
        if (s.length() != 0) {
            if (s.charAt(0) == '+') {
                s = s.substring(1);
            }
            l = Long.parseLong(s);
        } else {
            return "";
        }
        boolean negative = false;
        if (l < 0) {
            negative = true;
            l = Math.abs(l);
        }
        s = Long.toString(l);
        if (s.length() == 1) {
            return (negative ? ("-0.0" + s) : ("0.0" + s));
        }
        if (s.length() == 2) {
            return (negative ? ("-0." + s) : ("0." + s));
        } else {
            return (negative ? ("-" + s.substring(0, s.length() - 2)
                    + "." + s.substring(s.length() - 2)) : (s.substring(0, s.length() - 2)
                    + "." + s.substring(s.length() - 2)));
        }
    }

    /**
     * 将字符串"分"转换成"元"（短格式），如：100分被转换为1元。
     *
     * @param s
     * @return
     */
    public static String convertCent2DollarShort(String s) {
        String ss = convertCent2Dollar(s);
        ss = "" + Double.parseDouble(ss);
        if (ss.endsWith(".0")) {
            return ss.substring(0, ss.length() - 2);
        }
        if (ss.endsWith(".00")) {
            return ss.substring(0, ss.length() - 3);
        } else {
            return ss;
        }
    }
}
