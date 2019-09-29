package com.jxust.qq.superquestionlib.po;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class ChoiceQuestion {

    private long questionId;
    private long questionTypeId;
    private long questionLibId;
    private int questionLevel;
    private String questionBody;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private long rightTime;
    private long wrongTime;
    private String keyWord;
    private LocalDateTime createTime;
    private LocalDateTime lastModify;
}
