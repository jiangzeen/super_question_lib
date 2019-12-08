package com.jxust.qq.superquestionlib.po;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class QuestionLib {

    private long questionLibId;
    private int questionLibLevel;
    private String questionLibName;
    private long questionLibAuthorId;
    private String questionLibUrl;
    private LocalDateTime questionLibCreateTime;
    private int questionLibTagId;
    private String questionLibMark;
}
