package com.jxust.qq.superquestionlib.dao.mapper.admin;
import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EsUserPaperMapper
{
    int deleteById(@Param("id") int id);
    int deleteByUserName(@Param("userName") String userName);
    int updateUserPaper(@Param("userPaper") EsUserPaper userPaper);
    int insertUserPaper(@Param("userPaper") EsUserPaper userPaper);
}
