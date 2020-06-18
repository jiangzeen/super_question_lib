package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.SchoolInfo;
import com.jxust.qq.superquestionlib.dto.User;
import com.jxust.qq.superquestionlib.vo.UserVO;
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

    void updateUserInfo(String username, String nickname, int sex, int schoolInfoId);

    void updateLoginTime(String username, LocalDateTime time);

    void updateUserAvatar(String avatarUrl, String username);

    SchoolInfo selectSchoolById(int schoolId);

    List<SchoolInfo> selectSchoolInfos();

    SchoolInfo selectSchoolByName(String name);
}
