package com.jxust.qq.superquestionlib.vo;

import lombok.Data;

import java.util.List;

@Data
public class TaskSimpleInfo {

    private int libId;
    private int questionNumber;
    private String mark;
    private String createTime;
    private String scheduleTime;
    private String endTime;
    private boolean customize;
    List<Integer> questionList;
}
