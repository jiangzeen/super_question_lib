package com.jxust.qq.superquestionlib.dao.mapper.admin;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionLib;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EsUserQuestionLibMapper
{
    int deleteById(@Param("id") long id);
    int updateUserQuestionLib(@Param("userQuestionLib") EsUserQuestionLib userQuestionLib);
}
