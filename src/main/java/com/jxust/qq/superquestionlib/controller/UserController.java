package com.jxust.qq.superquestionlib.controller;

import com.jxust.qq.superquestionlib.po.User;
import com.jxust.qq.superquestionlib.service.UserMapperImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UserController {

    private final UserMapperImpl userMapper;

    public UserController(UserMapperImpl userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/")
    public void test_mybatis() {
        List<User> users = userMapper.findUsers();
        System.out.println(users);
    }

    @PostMapping("/re")
    public void test_insert() {
        User user = new User();
        user.setUserNick("xiaoming");
        user.setUserName("1363646276@qq.com");
        user.setUserPassword("123465");
        user.setUserAvatar("http://www.sdasda.com");
        user.setUserLastLoginTime(LocalDateTime.of(2019, 9, 27, 10, 30, 50));
        user.setUserCreateTime(LocalDateTime.of(2019, 9, 25, 10, 30, 50));
        user.setUserSchoolId(10);
        user.setUserMajorId(150);
        user.setUserSex(0);
        userMapper.createUser(user);
    }
}
