package com.jxust.qq.superquestionlib.po;


import lombok.Data;


import java.time.LocalDateTime;

@Data
public class User {

    private long userId;
    private String userName;
    private String userPassword;
    private String userAvatar;
    private String userNick;
    private LocalDateTime userLastLoginTime;
    private LocalDateTime userCreateTime;
    private int userSex;
    private int userSchoolId;
    private int userMajorId;

}
