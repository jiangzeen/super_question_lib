package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.dao.mapper.UserMapper;
import com.jxust.qq.superquestionlib.po.User;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMapperImpl{

    private final UserMapper mapper;

    public UserMapperImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    public List<User> findUsers() {
        return mapper.findAllUsers();
    }

    public void createUser(User user) {
        assert user != null;
        mapper.insertUser(user);
    }
}
