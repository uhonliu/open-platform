package com.bsd.dingtalk.server.util;

import com.bsd.dingtalk.server.constants.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import com.taobao.api.internal.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AttenceHelper {
    //获取企业考勤排班详情
    public static OapiAttendanceListscheduleResponse.AtScheduleListForTopVo getAttendanceList(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_ATTENDANCE_LIST);
            OapiAttendanceListscheduleRequest request = new OapiAttendanceListscheduleRequest();
            request.setWorkDate(new Date());
            request.setOffset(0L);
            request.setSize(100L);
            OapiAttendanceListscheduleResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResult();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取企业考勤组详情
    public static OapiAttendanceGetsimplegroupsResponse.AtGroupListForTopVo getAttendanceSimpleGruop(String accessToken) {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_ATTENDANCE_SIMPLE_GRUOP);
            OapiAttendanceGetsimplegroupsRequest request = new OapiAttendanceGetsimplegroupsRequest();
            OapiAttendanceGetsimplegroupsResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResult();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取打卡详情
    public static List<OapiAttendanceListRecordResponse.Recordresult> getAttendanceListRecord(String accessToken, String dateFrom, String dateTo, String userIdList) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_ATTENDANCE_LIST_RECORD);
            OapiAttendanceListRecordRequest request = new OapiAttendanceListRecordRequest();
            request.setCheckDateFrom(dateFrom);
            request.setCheckDateTo(dateTo);
            request.setUserIds(Arrays.asList(userIdList));
            OapiAttendanceListRecordResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getRecordresult();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取打卡结果
    public static List<OapiAttendanceListResponse.Recordresult> getAttendanceResult(String accessToken, String dateFrom, String dateTo, String userIdList) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_ATTENDANCE_RESULT);
            OapiAttendanceListRequest request = new OapiAttendanceListRequest();
            request.setWorkDateFrom(dateFrom);
            request.setWorkDateTo(dateTo);
            request.setUserIdList(Arrays.asList(userIdList));
            request.setOffset(0L);
            request.setLimit(10L);
            OapiAttendanceListResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getRecordresult();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取请假时长
    public static Long getLeaveTime(String accessToken, String userId, String dateFrom, String dateTo) {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_LEAVE_TIME);
            OapiAttendanceGetleaveapprovedurationRequest request = new OapiAttendanceGetleaveapprovedurationRequest();
            request.setFromDate(StringUtils.parseDateTime(dateFrom));
            request.setToDate(StringUtils.parseDateTime(dateTo));
            request.setUserid(userId);
            OapiAttendanceGetleaveapprovedurationResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResult().getDurationInMinutes();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取用户考勤组
    public static OapiAttendanceGetusergroupResponse.AtGroupFullForTopVo getUserAttendanceGruop(String accessToken, String userId) {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_ATTENDANCE_GRUOP);
            OapiAttendanceGetusergroupRequest request = new OapiAttendanceGetusergroupRequest();
            request.setUserid(userId);
            OapiAttendanceGetusergroupResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResult();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
