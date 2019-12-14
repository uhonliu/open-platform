package com.opencloud.msg.server.exchanger;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.opencloud.msg.client.model.BaseMessage;
import com.opencloud.msg.client.model.EmailMessage;
import com.opencloud.msg.client.model.EmailTplMessage;
import com.opencloud.msg.client.model.entity.EmailConfig;
import com.opencloud.msg.client.model.entity.EmailLogs;
import com.opencloud.msg.client.model.entity.EmailTemplate;
import com.opencloud.msg.server.service.EmailConfigService;
import com.opencloud.msg.server.service.EmailLogsService;
import com.opencloud.msg.server.service.EmailSender;
import com.opencloud.msg.server.service.EmailTemplateService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author woodev
 */
@Slf4j
public class EmailExchanger implements MessageExchanger {
    private EmailSender mailSender;
    private EmailConfigService emailConfigService;
    private EmailTemplateService emailTemplateService;
    private EmailLogsService emailLogsService;


    public EmailExchanger(EmailSender mailSender, EmailConfigService emailConfigService, EmailTemplateService emailTemplateService, EmailLogsService emailLogsService) {
        this.mailSender = mailSender;
        this.emailConfigService = emailConfigService;
        this.emailTemplateService = emailTemplateService;
        this.emailLogsService = emailLogsService;
    }

    @Override
    public boolean support(Object message) {
        return message instanceof EmailMessage;
    }

    @Override
    public boolean exchange(BaseMessage message) {
        Assert.notNull(mailSender, "邮件接口没有初始化");
        EmailMessage emailMessage = (EmailMessage) message;
        String error = null;
        Integer result = 0;
        EmailTemplate emailTemplate = null;
        EmailConfig emailConfig = null;
        Map<String, Object> configMap = Maps.newHashMap();
        Map<String, EmailConfig> senderMap = Maps.newHashMap();
        try {
            List<EmailConfig> configList = emailConfigService.getCacheConfig();
            for (EmailConfig config : configList) {
                if (config.getIsDefault().intValue() == 1) {
                    // 增加一条默认配置
                    senderMap.put("default", config);
                }
                senderMap.put(config.getConfigId().toString(), config);
            }
            // 模板消息
            if (emailMessage instanceof EmailTplMessage) {
                EmailTplMessage tplMessage = (EmailTplMessage) emailMessage;
                emailTemplate = emailTemplateService.getByCode(tplMessage.getTplCode());
                emailConfig = senderMap.get(emailTemplate.getConfigId());
                String content = freemarkerProcess(tplMessage.getTplParams(), emailTemplate.getTemplate());
                emailMessage.setContent(content);
            }
            if (emailConfig == null) {
                emailConfig = senderMap.get("default");
            }
            // 构建发送者
            JavaMailSenderImpl javaMailSender = buildMailSender(emailConfig);
            mailSender.sendSimpleMail(javaMailSender, emailMessage);
            result = 1;
        } catch (Exception e) {
            error = e.getMessage();
            log.error("发送错误:{}", error);
        } finally {
            // 保存发送日志
            try {
                configMap.put("template", emailTemplate);
                configMap.put("config", emailConfig);
                EmailLogs logs = new EmailLogs();
                logs.setSendTo(StringUtils.arrayToDelimitedString(emailMessage.getTo(), ";"));
                logs.setSendCc(StringUtils.arrayToDelimitedString(emailMessage.getCc(), ";"));
                logs.setSubject(emailMessage.getSubject());
                logs.setContent(emailMessage.getContent());
                logs.setError(error);
                logs.setResult(result);
                logs.setSendNums(1);
                logs.setTplCode(emailTemplate != null ? emailTemplate.getCode() : null);
                logs.setConfig(JSONObject.toJSONString(configMap));
                logs.setAttachments(JSONObject.toJSONString(emailMessage.getAttachments()));
                logs.setCreateTime(new Date());
                logs.setUpdateTime(logs.getCreateTime());
                emailLogsService.save(logs);
            } catch (Exception e) {
                log.error("邮箱日志错误:{}", e.getMessage());
            }
        }
        return result == 1;
    }


    private JavaMailSenderImpl buildMailSender(EmailConfig config) {
        if (config == null) {
            throw new RuntimeException("缺少默认邮件服务器配置");
        }
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getSmtpHost());
        sender.setUsername(config.getSmtpUsername());
        sender.setPassword(config.getSmtpPassword());
        sender.setDefaultEncoding("UTF-8");
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.starttls.required", "true");
        sender.setJavaMailProperties(props);
        return sender;
    }

    /**
     * freemark处理文字
     *
     * @param input
     * @param templateStr
     * @return
     */
    public static String freemarkerProcess(Map input, String templateStr) {
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        String template = "content";
        stringLoader.putTemplate(template, templateStr);
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        cfg.setTemplateLoader(stringLoader);
        try {
            Template templateCon = cfg.getTemplate(template, "UTF-8");
            StringWriter writer = new StringWriter();
            templateCon.process(input, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
