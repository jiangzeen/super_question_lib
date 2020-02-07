package com.jxust.qq.superquestionlib.vo;

import lombok.Data;


@Data
public class UserQuestionHistoryVO {

    private int questionId;
    private int questionLibId;
    private String result;
    private String userResult;
    private String submitTime;

}
