package com.jxust.qq.superquestionlib.controller.schedule;

import com.jxust.qq.superquestionlib.service.MailService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class ScheduleController {

    private final MailService mailService;

    public ScheduleController(MailService mailService) {
        this.mailService = mailService;
    }

    @RequestMapping("/test/schedule/{to_email}")
    public String execute(@PathVariable String to_email) {
        ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(5);
        ScheduledFuture<String> future = scheduled.schedule(()->{
            String s = "今日事,今日毕,莫负韶华";
            mailService.senderSimpleMail(to_email, s);
            return "执行完毕";
        }, 60, TimeUnit.SECONDS);
        try {
            log.info(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "执行定时任务成功";
    }

}
