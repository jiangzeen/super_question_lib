package com.jxust.qq.superquestionlib.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@ConfigurationProperties(prefix = "spring.mail")
public class MailService {

    private String username = "1363646276@qq.com";
    private JavaMailSender sender;

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
}
