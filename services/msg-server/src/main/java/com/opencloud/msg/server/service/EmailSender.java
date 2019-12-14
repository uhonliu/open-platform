package com.opencloud.msg.server.service;

import com.opencloud.msg.client.model.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * @author woodev
 */
@Component
@Slf4j
public class EmailSender {
    /**
     * 发送邮件
     */
    public void sendSimpleMail(JavaMailSenderImpl javaMailSender, EmailMessage emailMessage) throws Exception {
        MimeMessage message = null;
        message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(emailMessage.getTo());
        if (emailMessage.getCc() != null && emailMessage.getCc().length > 0) {
            helper.setCc(emailMessage.getCc());
        }
        helper.setFrom(javaMailSender.getUsername());
        helper.setSubject(emailMessage.getSubject());
        helper.setText(emailMessage.getContent(), true);
        this.addAttachment(helper, emailMessage);
        javaMailSender.send(message);
    }

    private void addAttachment(MimeMessageHelper helper, EmailMessage emailMessage) throws MessagingException {
        if (emailMessage.getAttachments() != null && !emailMessage.getAttachments().isEmpty()) {
            for (Map<String, String> fileMap : emailMessage.getAttachments()) {
                String filePath = fileMap.get("filePath");
                String originalFilename = fileMap.get("originalFilename");
                File file = new File(filePath);
                helper.addAttachment(originalFilename, file);
            }
        }
    }
}
