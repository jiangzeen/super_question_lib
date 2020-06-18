package com.jxust.qq.superquestionlib.vo;

import lombok.Data;

import java.util.List;

@Data
public class QuestionLibVO {
    private int libId;
    private String username;
    private String libName;
    private int importance;
    private String privateName;
    private String mark;
    private String typeName;
    private String tagImgUrl;
    private List<QuestionVO> questions;
}
