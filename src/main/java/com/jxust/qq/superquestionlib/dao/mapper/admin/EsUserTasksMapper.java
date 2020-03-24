package com.jxust.qq.superquestionlib.dao.mapper.admin;

import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EsUserTasksMapper
{
    int deleteById(@Param("id") int id);
    int deleteByUserName(@Param("userName") String userName);
    int updateUserTasks(@Param("userTasks") EsUserTasks userTasks);
    int createUserTasks(@Param("userTasks") EsUserTasks userTasks);
}
