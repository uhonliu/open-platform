package com.bsd.dingtalk.server.util;

import com.bsd.dingtalk.server.constants.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiReportGetunreadcountRequest;
import com.dingtalk.api.request.OapiReportListRequest;
import com.dingtalk.api.response.OapiReportGetunreadcountResponse;
import com.dingtalk.api.response.OapiReportListResponse;
import com.taobao.api.ApiException;

import java.util.concurrent.TimeUnit;

public class LogHelper {
    //获取用户日志数据
    public static OapiReportListResponse.PageVo getReportList(String accessToken, String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_REPORT_LIST);
            OapiReportListRequest request = new OapiReportListRequest();
            request.setUserid(userId);
            //获取日志的天数限制，默认为10
            request.setStartTime(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10));
            request.setEndTime(System.currentTimeMillis());
            request.setCursor(0L);
            request.setSize(100L);
            OapiReportListResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResult();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取用户日志未读数
    public static Long getReportUnreadCount(String accessToken, String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_REPORT_UNREAD_COUNT);
            OapiReportGetunreadcountRequest request = new OapiReportGetunreadcountRequest();
            request.setUserid(userId);
            OapiReportGetunreadcountResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getCount();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
