package com.jxust.qq.superquestionlib.po;

import lombok.Data;

import java.time.LocalTime;

@Data
public class UserTasks {
    private int id;
    private String username;
    private String mark;
    private int questionLibId;
    private int questionNumbers;
    private LocalTime createTime;
}
