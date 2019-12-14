package com.bsd.dingtalk.server.util;

import com.bsd.dingtalk.server.constants.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiProcessGettodonumRequest;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.request.OapiProcessinstanceGetRequest;
import com.dingtalk.api.request.OapiProcessinstanceListidsRequest;
import com.dingtalk.api.response.OapiProcessGettodonumResponse;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiProcessinstanceListidsResponse;
import com.taobao.api.ApiException;

import java.util.ArrayList;
import java.util.List;

public class ApprovalHelper {
    //发起审批实例
    public static String ProcessinstanceCreate(String accessToken, Long agentId, OapiProcessinstanceCreateRequest entity) {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_PROCESSINSTANCE_CREATE);
            OapiProcessinstanceCreateRequest request = new OapiProcessinstanceCreateRequest();
            request.setAgentId(agentId);
            request.setProcessCode(entity.getProcessCode());
            List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValues = new ArrayList<OapiProcessinstanceCreateRequest.FormComponentValueVo>();
            OapiProcessinstanceCreateRequest.FormComponentValueVo vo = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            vo.setName("企业名称");
            vo.setValue("test");
            formComponentValues.add(vo);
            request.setFormComponentValues(formComponentValues);
            request.setApprovers(entity.getApprovers());
            request.setOriginatorUserId(entity.getOriginatorUserId());
            request.setDeptId(entity.getDeptId());
            request.setCcList(entity.getCcList());
            request.setCcPosition(entity.getCcPosition());
            OapiProcessinstanceCreateResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getProcessInstanceId();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //批量获取审批实例id
    public static List<String> getProcessinstanceListIds(String accessToken, String processCode, String useridList, Long startTime, Long endTime, Long size) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_PROCESSINSTANCE_LIST_IDS);
            OapiProcessinstanceListidsRequest req = new OapiProcessinstanceListidsRequest();
            req.setProcessCode(processCode);
            req.setStartTime(startTime);
            req.setEndTime(endTime);
            req.setSize(size);
            req.setCursor(0L);
            req.setUseridList(useridList);
            OapiProcessinstanceListidsResponse response = client.execute(req, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getResult().getList();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取单个审批实例
    public static OapiProcessinstanceGetResponse.ProcessInstanceTopVo getProcessinstance(String accessToken, String processInstanceId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_PROCESSINSTANCE);
            OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
            request.setProcessInstanceId(processInstanceId);
            OapiProcessinstanceGetResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getProcessInstance();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取用户待审批数量
    public static Long getProcessInstanceToDoNum(String accessToken, String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_PROCESSINSTANCE_TO_DO_NUM);
            OapiProcessGettodonumRequest req = new OapiProcessGettodonumRequest();
            req.setUserid(userId);
            OapiProcessGettodonumResponse response = client.execute(req, accessToken);
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
