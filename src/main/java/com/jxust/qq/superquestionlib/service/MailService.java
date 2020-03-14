package com.jxust.qq.superquestionlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@ConfigurationProperties(prefix = "spring.mail")
public class MailService {

    private String username = "1363646276@qq.com";
    private JavaMailSender sender;
    @Autowired
    private StringRedisTemplate redisTemplate;
    public MailService(JavaMailSender sender) {
        this.sender = sender;
    }

    public int senderSimpleMail (String to) {
        int code = new Random().nextInt(1000000);
        String msg = "您正在注册super-question-lib,您的验证码是" + code + ",5分钟内有效,如非本人操作请忽视";
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("super-question-lib注册码");
        mailMessage.setFrom(username);
        mailMessage.setTo(to);
        mailMessage.setText(msg);
        sender.send(mailMessage);
        return code;
    }

    public void senderSimpleMail(String to, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("super-question-lib注册码");
        mailMessage.setFrom(username);
        mailMessage.setTo(to);
        mailMessage.setText(text);
        sender.send(mailMessage);
    }

    public void sendTaskMessage(String to, String message) throws MessagingException {
        String subject = (String) redisTemplate.opsForHash().get("mailSender:task:model","subject");
        if (subject == null) {
            subject = "super-question-lib";
        }
        // 替换模板中的内容
        String model = (String) redisTemplate.opsForHash().get("mailSender:task:model","content");
        if (model != null) {
            model = model.replace("message", message);
        }else {
            model = "详情请登录查看";
        }
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper sendHelper = new MimeMessageHelper(mimeMessage);
        sendHelper.setFrom(username);
        sendHelper.setSubject(subject);
        sendHelper.setTo(to);
        sendHelper.setText(model, true);
        sender.send(mimeMessage);
    }

}
