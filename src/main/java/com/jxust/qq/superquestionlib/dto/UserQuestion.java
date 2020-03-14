package com.jxust.qq.superquestionlib.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQuestion {

    private int questionId;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime lastPracticeTime;
    private String username;
    private int libId;
    private int repetitions;
    private float easiness;
    private int interval;
}
