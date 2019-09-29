package com.jxust.qq.superquestionlib.po;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class FillQuestion {

    private long fillQuestionId;
    private int  fillQuestionLevel;
    private String fillQuestionBody;
    private String answer;
    private long questionLibId;
    private long questionTypeId;
    private long rightTime;
    private long wrongTime;
    private String keyWord;
    private LocalDateTime createTime;
    private LocalDateTime lastModify;
}
