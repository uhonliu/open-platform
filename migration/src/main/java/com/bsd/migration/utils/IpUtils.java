package com.bsd.migration.utils;

import java.util.regex.Pattern;

public class IpUtils {
    /**
     * 检测IP
     *
     * @param ip
     * @return
     */
    public static boolean matchIp(String ip) {
        if (ip == null) {
            return false;
        }
        String regex = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
        return Pattern.matches(regex, ip);
    }
}
