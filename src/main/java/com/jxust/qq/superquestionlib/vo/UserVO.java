package com.jxust.qq.superquestionlib.vo;

import com.jxust.qq.superquestionlib.dto.SchoolInfo;
import lombok.Data;

@Data
public class UserVO {

    public UserVO(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }

    private String nickname;
    private String username;
    private String avatar;
    private int schoolId;
    private String schoolInfo;
    private int sex;
    private String lastLoginTime;
}
