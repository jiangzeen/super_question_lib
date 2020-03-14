package com.jxust.qq.superquestionlib.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class UserTasks {
    private int id;
    private String username;
    private String mark;
    private int questionLibId;
    private int questionNumbers;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
    private LocalDateTime scheduleTime;
    private String customizeList;
    private int completeNumber;
    private int expired;
}
