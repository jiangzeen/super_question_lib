package com.jxust.qq.superquestionlib.vo;

import com.jxust.qq.superquestionlib.dto.UserTasks;
import lombok.Data;

import java.util.List;

@Data
public class UserTaskVO {

    private String username;
    private List<UserTasks> tasksList;
    private int count;
}
