package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Adminitrator
{
    private int adminId;
    private String adminName;
    private String adminPassword;
    private LocalDateTime lastLoginTime;
}
