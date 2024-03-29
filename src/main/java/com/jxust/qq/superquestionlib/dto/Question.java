package com.jxust.qq.superquestionlib.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Question {

    private long questionId;
    private int  questionLevel;
    private long questionLibId;
    private long questionTypeId;
    private String questionContent;
    private long rightTime;
    private long wrongTime;
    private String keyword;
    private LocalDateTime createTime;
    private LocalDateTime lastModify;
}
