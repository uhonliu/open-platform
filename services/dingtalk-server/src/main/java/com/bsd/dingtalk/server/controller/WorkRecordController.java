package com.bsd.dingtalk.server.controller;

import com.alibaba.fastjson.JSON;
import com.bsd.dingtalk.server.configuration.DingtalkProperties;
import com.bsd.dingtalk.server.constants.URLConstant;
import com.bsd.dingtalk.server.util.AccessTokenUtil;
import com.bsd.dingtalk.server.util.LogFormatter;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiWorkrecordAddRequest;
import com.dingtalk.api.request.OapiWorkrecordGetbyuseridRequest;
import com.dingtalk.api.request.OapiWorkrecordUpdateRequest;
import com.dingtalk.api.response.OapiWorkrecordAddResponse;
import com.dingtalk.api.response.OapiWorkrecordGetbyuseridResponse;
import com.dingtalk.api.response.OapiWorkrecordUpdateResponse;
import com.opencloud.common.model.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 待办基础功能
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Slf4j
@Api(tags = "待办基础功能")
@EnableConfigurationProperties({DingtalkProperties.class})
@RestController
public class WorkRecordController {
    @Autowired
    private DingtalkProperties dingtalkProperties;

    private volatile Map<String, String> localCache = new HashMap<String, String>();

    /**
     * 发起待办事项
     */
    @ApiOperation(value = "发起待办事项", notes = "发起待办事项")
    @RequestMapping(value = "/workrecord/start", method = RequestMethod.POST)
    @ResponseBody
    public ResultBody startWorkRecord() {
        try {
            String userId = "manager7078";
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_ADD_WORK_RECORD);
            OapiWorkrecordAddRequest req = new OapiWorkrecordAddRequest();
            req.setUserid(userId);
            req.setCreateTime(System.currentTimeMillis());
            req.setTitle("title");

            String id = userId + System.currentTimeMillis();
            req.setUrl(dingtalkProperties.getCallbackUrlHost() + "/workrecord/" + id + "/update");
            List<OapiWorkrecordAddRequest.FormItemVo> list2 = new ArrayList<>();
            OapiWorkrecordAddRequest.FormItemVo obj3 = new OapiWorkrecordAddRequest.FormItemVo();
            list2.add(obj3);
            obj3.setTitle("标题");
            obj3.setContent("内容");

            OapiWorkrecordAddRequest.FormItemVo obj4 = new OapiWorkrecordAddRequest.FormItemVo();
            list2.add(obj4);
            obj4.setTitle("发起时间");
            obj4.setContent(String.valueOf(System.currentTimeMillis()));
            req.setFormItemList(list2);
            OapiWorkrecordAddResponse rsp = client.execute(req, AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret()));
            System.out.println(JSON.toJSONString(rsp));

            int errorCode = Integer.valueOf(rsp.getErrorCode());
            if (errorCode != 0) {
                return ResultBody.failed().code(errorCode).msg(rsp.getErrmsg());
            }
            localCache.put(id, rsp.getRecordId());
            return ResultBody.ok().data(rsp.getRecordId());
        } catch (Exception e) {
            String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.END, "startWorkRecord fail");
            log.info(errLog, e);
            return ResultBody.failed().code(-1).msg("系统繁忙");
        }
    }

    /**
     * 修改待办事项
     */
    @ApiOperation(value = "修改待办事项", notes = "修改待办事项")
    @RequestMapping(value = "/workrecord/{id}/update", method = RequestMethod.GET)
    @ResponseBody
    public String updateWorkRecord(@PathVariable String id) {
        try {
            if (!localCache.containsKey(id)) {
                return "can't find the workrecord";
            }
            String recordId = localCache.get(id);

            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_UPDATE_WORK_RECORD);
            OapiWorkrecordUpdateRequest req = new OapiWorkrecordUpdateRequest();
            req.setUserid("manager7078");
            req.setRecordId(recordId);
            OapiWorkrecordUpdateResponse response = client.execute(req, AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret()));
            System.out.println(JSON.toJSONString(response));

            if (Integer.valueOf(response.getErrorCode()) != 0) {
                return "update workrecord fail";
            }
            return "待办事项更新完成";
        } catch (Exception e) {
            String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.END, "updateWorkRecord fail");
            log.info(errLog, e);
            return "update workrecord fail";
        }
    }

    /**
     * 获取待办事项
     *
     * @return
     */
    @ApiOperation(value = "获取待办事项", notes = "获取待办事项")
    @RequestMapping(value = "/workrecord/get/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBody getWorkRecordByUserId(@PathVariable String userid) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_WORK_RECORD_BY_USER_ID);
            OapiWorkrecordGetbyuseridRequest req = new OapiWorkrecordGetbyuseridRequest();
            req.setUserid(userid);
            req.setOffset(0L);
            req.setLimit(50L);
            req.setStatus(0L);
            OapiWorkrecordGetbyuseridResponse rsp = client.execute(req, AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret()));
            System.out.println(rsp.getBody());

            return ResultBody.ok().data(rsp.getRecords());
        } catch (Exception e) {
            return ResultBody.failed().code(-1).msg("系统繁忙");
        }
    }
}


