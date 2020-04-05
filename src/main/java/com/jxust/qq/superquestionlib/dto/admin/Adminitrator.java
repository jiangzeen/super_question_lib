package com.jxust.qq.superquestionlib.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Adminitrator
{
    @JsonProperty("id")
    private int adminId;
    @JsonProperty("adminName")
    private String adminName;
    @JsonProperty("password")
    private String adminPassword;
    private Date lastLoginTime;
}
