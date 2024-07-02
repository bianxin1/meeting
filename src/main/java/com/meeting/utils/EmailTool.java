package com.meeting.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Component
@Slf4j
public class EmailTool {
    @Value("${email.protocol}")
    private String emailProtocol;

    @Value("${email.smtpHost}")
    private String emailSMTPHost;

    @Value("${email.port}")
    private String emailPort;

    @Value("${email.account}")
    private String emailAccount;

    @Value("${email.password}")
    private String emailPassword;
    public boolean sendEmail(String email, String content,String title) {
        if (email == null || content == null) {
            return false;
        }
        try {
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", emailProtocol);
            props.setProperty("mail.smtp.host", emailSMTPHost);
            props.setProperty("mail.smtp.port", emailPort);
            props.setProperty("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // 指定SSL版本
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.connectiontimeout", "10000"); // 与邮件服务器建立连接的时间限制
            props.setProperty("mail.smtp.timeout", "10000"); // 邮件smtp读取的时间限制
            props.setProperty("mail.smtp.writetimeout", "10000"); // 邮件内容上传的时间限制

            Session session = Session.getDefaultInstance(props);
            session.setDebug(false); // 设置为debug模式, 可以查看详细的发送log

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount, "会议管理员", "UTF-8"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email, email, "UTF-8"));
            message.setSubject(title, "UTF-8");
            message.setContent(content, "text/html;charset=UTF-8");
            message.setSentDate(new Date());
            message.saveChanges();

            Transport transport = session.getTransport();
            transport.connect(emailAccount, emailPassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败, 系统错误！", e);
            return false;
        }
    }
}
