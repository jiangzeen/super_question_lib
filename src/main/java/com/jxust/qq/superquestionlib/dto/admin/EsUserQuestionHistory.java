package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class EsUserQuestionHistory {
    private int id;
    private String username;
    private int questionId;
    private int questionLibId;
    private String questionLibName;
    private String result;
    private Date submitTime;
}
