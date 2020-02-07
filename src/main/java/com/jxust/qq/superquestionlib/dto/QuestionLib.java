package com.jxust.qq.superquestionlib.dto;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class QuestionLib {

    private long questionLibId;
    private int questionLibLevel;
    private String questionLibName;
    private String questionLibUrl;
    private int hasPrivate;
    private LocalDateTime questionLibCreateTime;
    private int questionLibTagId;
    private String questionLibMark;
}
