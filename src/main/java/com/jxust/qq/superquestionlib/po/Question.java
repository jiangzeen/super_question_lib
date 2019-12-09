package com.jxust.qq.superquestionlib.po;

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
    private String keyWord;
    private LocalDateTime createTime;
    private LocalDateTime lastModify;
}
