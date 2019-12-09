package com.jxust.qq.superquestionlib.po;

import lombok.Data;

import java.time.LocalTime;

@Data
public class UserPaper {
    private int id;
    private String username;
    private int questionLibId;
    private LocalTime startTime;
    private LocalTime limitTime;
    private LocalTime endTime;
    private String result;
}
