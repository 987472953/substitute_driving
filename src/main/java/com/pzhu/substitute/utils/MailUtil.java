package com.pzhu.substitute.utils;

import com.pzhu.substitute.common.CommonConstants;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;
import java.util.List;

/**
 * @author dengyiqing
 * @description 发送邮件工具
 * @date 2022/3/27
 */
public class MailUtil {

    private JavaMailSender javaMailSender;

    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    //=============================common============================

    public boolean sendMessage(String subject, String infoText, String recipient) {
        return sendMessageToOne(subject, infoText, recipient, null, null);
    }

    public boolean sendMessageToOne(String subject, String infoText, String recipient, List<String> cc, List<String> bcc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        return baseSendMessage(message, subject, infoText, cc, bcc);
    }

    public boolean sendMessageToMultiple(String subject, String infoText, List<String> recipient, List<String> cc, List<String> bcc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient.toArray(new String[0]));
        return baseSendMessage(message, subject, infoText, cc, bcc);
    }

    public boolean baseSendMessage(SimpleMailMessage message, String subject, String infoText, List<String> cc, List<String> bcc) {
        message.setSubject(subject);
        message.setFrom(CommonConstants.MAIL_SENDER);
        if (cc != null) {
            message.setCc(cc.toArray(new String[0]));
        }
        if (bcc != null) {
            message.setBcc(bcc.toArray(new String[0]));
        }
        message.setSentDate(new Date());
        message.setText(infoText);
        javaMailSender.send(message);
        return true;
    }
}
