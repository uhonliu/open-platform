package com.bsd.dingtalk.server.util;

import com.bsd.dingtalk.server.constants.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiWorkrecordAddRequest;
import com.dingtalk.api.response.OapiWorkrecordAddResponse;
import com.taobao.api.ApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkRecordHelper {
    //发起待办
    //企业可以调用该接口发起一个待办事项，该待办事项会出现在钉钉客户端“待办事项”页面，与钉钉审批待办事项并列
    public static Long addWorkRecord(String access_token, String userid, Map<String, String> map) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_ADD_WORK_RECORD);
            OapiWorkrecordAddRequest req = new OapiWorkrecordAddRequest();
            req.setUserid(userid);
            req.setCreateTime(System.currentTimeMillis());
            req.setTitle(map.get("title"));
            req.setUrl(map.get("url"));
            List<OapiWorkrecordAddRequest.FormItemVo> list2 = new ArrayList<>();
            OapiWorkrecordAddRequest.FormItemVo obj3 = new OapiWorkrecordAddRequest.FormItemVo();
            list2.add(obj3);
            obj3.setTitle(map.get("title"));
            obj3.setContent(map.get("content"));
            req.setFormItemList(list2);
            OapiWorkrecordAddResponse rsp = client.execute(req, access_token);
            if (rsp.getErrcode() != 0) {
                return null;
            }
            return Long.valueOf(rsp.getRecordId());
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
