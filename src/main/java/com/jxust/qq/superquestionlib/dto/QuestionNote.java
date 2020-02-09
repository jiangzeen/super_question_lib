package com.jxust.qq.superquestionlib.dto;

import lombok.Data;


@Data
public class QuestionNote {

    private int noteId;
    private String username;
    private int questionId;
    private String content;
    private String createTime;

}
