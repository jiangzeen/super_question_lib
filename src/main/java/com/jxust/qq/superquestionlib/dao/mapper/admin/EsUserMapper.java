package com.jxust.qq.superquestionlib.dao.mapper.admin;
import com.jxust.qq.superquestionlib.dto.admin.EsUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EsUserMapper
{
    int deleteById(@Param("id") long id);
    int updateUser(@Param("user") EsUser user);
    int createUser(@Param("user") EsUser user);
}
