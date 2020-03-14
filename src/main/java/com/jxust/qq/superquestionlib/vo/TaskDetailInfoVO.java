package com.jxust.qq.superquestionlib.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDetailInfoVO {

    private String mark;
    private int taskId;
    private String libName;
    private int questionNumber;
    private LocalDateTime createTime;
    private LocalDateTime scheduleTime;
    private LocalDateTime endTime;
    private int completeNum;
    private int expiredDayNum;
}
