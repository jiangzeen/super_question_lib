package com.jxust.qq.superquestionlib.vo;

import lombok.Data;

import java.util.Map;

@Data
public class PaperVO {

    private int id;
    private String username;
    private String startTime;
    private String endTime;
    private int limitTime;
    private Map<String, Object> userResult;
}
