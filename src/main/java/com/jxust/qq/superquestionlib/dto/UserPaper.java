package com.jxust.qq.superquestionlib.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPaper {
    private int id;
    private String username;
    private int questionLibId;
    private LocalDateTime startTime;
    private int limitTime;
    private LocalDateTime endTime;
    private String result;
}
