package com.opencloud.msg.client.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author woodev
 */
@ApiModel("邮件消息")
public class EmailMessage extends BaseMessage {
    /**
     * 收件人
     */
    @ApiModelProperty(value = "收件人")
    private String[] to;

    /**
     * 抄送人
     */
    @ApiModelProperty(value = "抄送人")
    private String[] cc;

    /**
     * 邮件标题
     */
    @ApiModelProperty(value = "邮件标题")
    private String subject;

    /**
     * 邮件内容
     */
    @ApiModelProperty(value = "邮件内容")
    private String content;

    /**
     * 附件
     */
    @ApiModelProperty(value = "附件路径")
    private List<Map<String, String>> attachments = new ArrayList<>();

    public EmailMessage() {

    }

    public EmailMessage(String to, String subject, String content) {
        this.to = new String[]{to};
        this.subject = subject;
        this.content = content;
    }


    public EmailMessage(String[] to, String[] cc, String subject, String content) {
        this.to = to;
        this.cc = cc;
        this.subject = subject;
        this.content = content;
    }


    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public List<Map<String, String>> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Map<String, String>> attachments) {
        this.attachments = attachments;
    }


    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }
}
