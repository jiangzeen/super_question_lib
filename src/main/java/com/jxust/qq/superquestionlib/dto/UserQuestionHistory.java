package com.jxust.qq.superquestionlib.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class UserQuestionHistory {
    private int id;
    private String username;
    private int questionId;
    private int questionLibId;
    private String result;
    private String userResult;
    private LocalDateTime submitTime;
}
