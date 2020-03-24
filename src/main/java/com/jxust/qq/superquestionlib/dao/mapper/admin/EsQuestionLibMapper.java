package com.jxust.qq.superquestionlib.dao.mapper.admin;

import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EsQuestionLibMapper
{
    int deleteById(@Param("id") long id);
    int updateQuestionLib(@Param("questionLib") EsQuestionLib questionLib);
    int createQuestionLib(@Param("questionLib") EsQuestionLib questionLib);
}
