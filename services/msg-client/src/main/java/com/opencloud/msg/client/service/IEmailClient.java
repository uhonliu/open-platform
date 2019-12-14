package com.opencloud.msg.client.service;

import com.opencloud.common.model.ResultBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 发送邮件接口
 *
 * @author woodev
 */
public interface IEmailClient {
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
    @PostMapping(value = "/email/send",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResultBody<String> send(
            @RequestParam(value = "to") String to,
            @RequestParam(value = "cc", required = false) String cc,
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "content") String content,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    );

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
    @PostMapping(value = "/email/send/tpl",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResultBody<String> sendByTpl(
            @RequestParam(value = "to") String to,
            @RequestParam(value = "cc", required = false) String cc,
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "tplCode") String tplCode,
            @RequestParam(value = "tplParams") String tplParams,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    );
}
