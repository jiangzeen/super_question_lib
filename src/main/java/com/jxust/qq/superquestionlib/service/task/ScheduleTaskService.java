package com.jxust.qq.superquestionlib.service.task;

import com.jxust.qq.superquestionlib.service.MailService;
import com.jxust.qq.superquestionlib.service.RedisService;
import com.jxust.qq.superquestionlib.service.UserTaskService;
import com.jxust.qq.superquestionlib.util.DateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class ScheduleTaskService {

    private ScheduledExecutorService scheduledExecutorService;

    private final MailService mailService;


    @Autowired
    private RedisService redisService;

    private final UserTaskService taskService;

    private ConcurrentHashMap<Integer, Future<Integer>> taskMap;

    public ScheduleTaskService(MailService mailService, UserTaskService taskService) {
        this.mailService = mailService;
        this.taskService = taskService;
        this.taskMap = new ConcurrentHashMap<>();
        this.scheduledExecutorService
                = new ScheduledThreadPoolExecutor(10);
    }


    public int executorTask(int taskId, String mark,
                            String username, long time, TimeUnit timeUnit) {
        if (taskMap.containsKey(taskId)) {
            return -1;
        }
        Future<Integer> future = scheduledExecutorService.
                schedule(() -> {
                    try {
                        mailService.sendTaskMessage(username, mark);
                        // 再启动一个定时任务,对任务expired字段进行修改
                        updateTaskExecutor(taskId);
                        taskMap.remove(taskId);
                        // 记录发送过的邮件
                        logTaskDetail(taskId, mark, username, LocalDateTime.now());
                        return taskId;
                    }catch (MessagingException e) {
                        logTaskDetail(taskId, mark, username, LocalDateTime.now(), false);
                        return -1;
                    }
                }, time, timeUnit);
        taskMap.putIfAbsent(taskId, future);
        return 1;
    }

    /**
     * 执行定时任务--任务开始后,在结束时间后更新任务状态
     * @param taskId
     */
    public void updateTaskExecutor(int taskId) {
        try {
            int minutes = taskService.findTaskLastingTimeById(taskId);
            scheduledExecutorService.schedule(() -> {
                taskService.modifyTaskState(taskId);
                return 1;
            }, minutes, TimeUnit.MINUTES);
        }catch (IllegalArgumentException ignored) {
        }
    }

    public void logTaskDetail(int taskId, String message, String username, LocalDateTime time, boolean success) {
        Map<String, String> taskMap = new HashMap<>();
        taskMap.put("taskId", String.valueOf(taskId));
        taskMap.put("message", message);
        taskMap.put("to", username);
        taskMap.put("time", time.format(DateFormat.SECOND_FORMATTER.getFormatter()));
        if (success) {
            redisService.hSet("mailSender:task:success:" + taskId, taskMap);
        }else {
            redisService.hSet("mailSender:task:fail:" + taskId, taskMap);
        }

    }

    public void logTaskDetail(int taskId, String message, String username, LocalDateTime time) {
        logTaskDetail(taskId, message, username, time, true);
    }

}
