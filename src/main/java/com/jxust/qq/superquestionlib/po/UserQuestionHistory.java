package com.jxust.qq.superquestionlib.po;

import lombok.Data;

import java.time.LocalTime;

@Data
public class UserQuestionHistory {
    private int id;
    private String username;
    private int questionId;
    private int questionLibId;
    private int result;
    private LocalTime submitTime;
}
