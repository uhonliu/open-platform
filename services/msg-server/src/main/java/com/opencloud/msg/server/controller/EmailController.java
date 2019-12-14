package com.opencloud.msg.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.opencloud.common.model.ResultBody;
import com.opencloud.msg.client.model.EmailMessage;
import com.opencloud.msg.client.model.EmailTplMessage;
import com.opencloud.msg.client.service.IEmailClient;
import com.opencloud.msg.server.dispatcher.MessageDispatcher;
import com.opencloud.msg.server.utils.MultipartUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author woodev
 */
@RestController
@Api(value = "邮件", tags = "邮件")
public class EmailController implements IEmailClient {
    @Autowired
    private MessageDispatcher dispatcher;

    /**
     * 发送邮件
     *
     * @param to          接收人 多个用;号隔开
     * @param cc          抄送人 多个用;号隔开
     * @param subject     主题
     * @param content     内容
     * @param attachments 附件
     * @return
     */
    @ApiOperation(value = "发送邮件", notes = "发送邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "to", required = true, value = "接收人 多个用;号隔开", paramType = "form"),
            @ApiImplicitParam(name = "cc", required = false, value = "抄送人 多个用;号隔开", paramType = "form"),
            @ApiImplicitParam(name = "subject", required = true, value = "主题", paramType = "form"),
            @ApiImplicitParam(name = "content", required = true, value = "内容", paramType = "form"),
            @ApiImplicitParam(name = "attachments", required = false, value = "附件:最大不超过10M", dataType = "file", paramType = "form", allowMultiple = true),
    })
    @PostMapping(value = "/email/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResultBody send(@RequestParam(value = "to") String to,
                           @RequestParam(value = "cc", required = false) String cc,
                           @RequestParam(value = "subject") String subject,
                           @RequestParam(value = "content") String content,
                           @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {
        EmailMessage message = new EmailMessage();
        message.setTo(StringUtils.delimitedListToStringArray(to, ";"));
        message.setCc(StringUtils.delimitedListToStringArray(cc, ";"));
        message.setSubject(subject);
        message.setAttachments(MultipartUtil.getMultipartFilePaths(attachments));
        message.setContent(content);
        this.dispatcher.dispatch(message);
        return ResultBody.ok();
    }

    /**
     * 发送模板邮件
     *
     * @param to          接收人 多个用;号隔开
     * @param cc          抄送人 多个用;号隔开
     * @param subject     主题
     * @param tplCode     内容
     * @param tplCode     模板编号
     * @param tplParams   模板参数 json字符串
     * @param attachments 附件
     * @return
     */
    @Override
    @ApiOperation(value = "发送模板邮件", notes = "发送模板邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "to", required = true, value = "接收人 多个用;号隔开", paramType = "form"),
            @ApiImplicitParam(name = "cc", required = false, value = "抄送人 多个用;号隔开", paramType = "form"),
            @ApiImplicitParam(name = "subject", required = true, value = "主题", paramType = "form"),
            @ApiImplicitParam(name = "tplCode", required = true, value = "模板编号", paramType = "form"),
            @ApiImplicitParam(name = "tplParams", required = true, value = "模板参数 json字符串", paramType = "form"),
            @ApiImplicitParam(name = "attachments", required = false, value = "附件:最大不超过10M", dataType = "file", paramType = "form", allowMultiple = true),
    })
    @PostMapping(value = "/email/send/tpl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultBody sendByTpl(
            @RequestParam(value = "to") String to,
            @RequestParam(value = "cc", required = false) String cc,
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "tplCode") String tplCode,
            @RequestParam(value = "tplParams") String tplParams,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        EmailTplMessage message = new EmailTplMessage();
        message.setTo(StringUtils.delimitedListToStringArray(to, ";"));
        message.setCc(StringUtils.delimitedListToStringArray(cc, ";"));
        message.setSubject(subject);
        message.setAttachments(MultipartUtil.getMultipartFilePaths(attachments));
        message.setTplCode(tplCode);
        message.setTplParams(JSONObject.parseObject(tplParams));
        this.dispatcher.dispatch(message);
        return ResultBody.ok();
    }
}
