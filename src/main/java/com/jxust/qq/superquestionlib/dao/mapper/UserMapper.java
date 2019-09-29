package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.po.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    List<User> findAllUsers();

    void insertUser(@Param("user") User user);

    User selectUserByUserName(String username);

    User selectUserByNameAndPassword(String username, String password);


}
