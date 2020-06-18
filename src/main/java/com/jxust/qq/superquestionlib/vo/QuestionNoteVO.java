package com.jxust.qq.superquestionlib.vo;

import lombok.Data;

@Data
public class QuestionNoteVO {

    private int noteId;
    private String username;
    private String nickname;
    private String content;
    private String createTime;
    private int questionId;
}
