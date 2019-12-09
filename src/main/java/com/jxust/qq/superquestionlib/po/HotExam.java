package com.jxust.qq.superquestionlib.po;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HotExam {
    private int id;
    private String examName;
    private int examTimeLevel;
    private LocalTime examStartTime;
    private LocalTime examToStartTime;
    // 根据分隔符,分割出多个id
    private String tagIds;
}
