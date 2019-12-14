package com.bsd.payment.server.util;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description:
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
public interface MyLogInf {
    void debug(String paramString, Object[] paramArrayOfObject);

    void info(String paramString, Object[] paramArrayOfObject);

    void warn(String paramString, Object[] paramArrayOfObject);

    void error(Throwable paramThrowable, String paramString, Object[] paramArrayOfObject);
}
