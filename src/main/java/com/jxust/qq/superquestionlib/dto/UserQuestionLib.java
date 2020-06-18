package com.jxust.qq.superquestionlib.dto;

import lombok.Data;

@Data
public class UserQuestionLib {
    private int id;
    private String username;
    private int questionLibId;
    private String privateName;
    private String privateMark;
    private int questionLibImportance;
}
