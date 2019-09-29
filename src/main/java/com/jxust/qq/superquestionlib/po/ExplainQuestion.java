package com.jxust.qq.superquestionlib.po;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class ExplainQuestion {

    private long explainQuestionId;
    private int explainQuestionLevel;
    private String answer;
    private long questionLibId;
    private long questionTypeId;
    private long rightTime;
    private long wrongTime;
    private String keyWord;
    private LocalDateTime createTime;
    private LocalDateTime lastModify;
}
