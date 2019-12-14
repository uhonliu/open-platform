package com.bsd.dingtalk.server.util;

import com.bsd.dingtalk.server.constants.Constant;
import com.bsd.dingtalk.server.constants.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiProcessinstanceGetRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class MessageHelper {
    //发送工作消息
    public static String sendWorkMessage(String accessToken, Long agentId, String type, String userIdList, Map<String, String> map) {
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_SEND_MESSAGE);
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(userIdList);
        request.setAgentId(agentId);
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();

        if (type.equals(Constant.TEXT)) {
            msg.setMsgtype(Constant.TEXT);
            msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
            msg.getText().setContent(map.get("content"));
            request.setMsg(msg);
        } else if (type.equals(Constant.IMAGE)) {
            msg.setMsgtype(Constant.IMAGE);
            msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
            msg.getImage().setMediaId(map.get("mediaId"));
            request.setMsg(msg);
        } else if (type.equals(Constant.FILE)) {
            msg.setMsgtype(Constant.FILE);
            msg.setFile(new OapiMessageCorpconversationAsyncsendV2Request.File());
            msg.getFile().setMediaId(map.get("mediaId"));
            request.setMsg(msg);
        } else if (type.equals(Constant.LINK)) {
            msg.setMsgtype(Constant.LINK);
            msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
            msg.getLink().setTitle(map.get("title"));
            msg.getLink().setText(map.get("text"));
            msg.getLink().setMessageUrl(map.get("messageUrl"));
            msg.getLink().setPicUrl(map.get("picUrl"));
            request.setMsg(msg);
        } else if (type.equals(Constant.MARKDOWN)) {
            msg.setMsgtype(Constant.MARKDOWN);
            msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
            msg.getMarkdown().setText(map.get("text"));
            msg.getMarkdown().setTitle(map.get("title"));
            request.setMsg(msg);
        } else if (type.equals(Constant.OA)) {
            msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
            msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
            msg.getOa().getHead().setText(map.get("text"));
            msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
            msg.getOa().getBody().setContent(map.get("content"));
            msg.setMsgtype(Constant.OA);
            request.setMsg(msg);
        } else if (type.equals(Constant.ACTION_CARD)) {
            msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
            msg.getActionCard().setTitle(map.get("title"));
            msg.getActionCard().setMarkdown(map.get("markdown"));
            msg.getActionCard().setSingleTitle(map.get("singleTitle"));
            msg.getActionCard().setSingleUrl(map.get("singleUrl"));
            msg.setMsgtype(Constant.ACTION_CARD);
            request.setMsg(msg);
        } else {
            log.info("无效的消息类型:" + type);
        }
        try {
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getErrmsg();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OapiMessageCorpconversationAsyncsendV2Response sendMessageToOriginator(String accessToken, Long agentId, String processInstanceId, Map<String, String> map) throws RuntimeException {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_PROCESSINSTANCE);
            OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
            request.setProcessInstanceId(processInstanceId);
            OapiProcessinstanceGetResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            String recieverUserId = response.getProcessInstance().getOriginatorUserid();

            client = new DefaultDingTalkClient(URLConstant.URL_SEND_MESSAGE);

            OapiMessageCorpconversationAsyncsendV2Request messageRequest = new OapiMessageCorpconversationAsyncsendV2Request();
            messageRequest.setUseridList(recieverUserId);
            messageRequest.setAgentId(agentId);
            messageRequest.setToAllUser(false);

            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            msg.setMsgtype("text");
            msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
            msg.getText().setContent(map.get("content"));
            messageRequest.setMsg(msg);

            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(messageRequest, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return rsp;
        } catch (ApiException e) {
            log.error("send message failed", e);
            throw new RuntimeException();
        }
    }
}
