package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.po.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserMapper {

    List<User> findAllUsers();

    int insertUser(@Param("user") User user);

    User selectUserByUserName(String username);

    User selectUserByNameAndPassword(String username, String password);

    String selectPasswordByUsername(String username);

    int updatePassword(String username, String password);

    void updateUser(User user);

    void updateAvatarAndMajor(String avatar, int majorId, String username);

    void updateLoginTime(String username, LocalDateTime time);
}
