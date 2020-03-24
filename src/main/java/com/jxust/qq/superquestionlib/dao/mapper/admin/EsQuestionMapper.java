package com.jxust.qq.superquestionlib.dao.mapper.admin;

import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EsQuestionMapper
{
    int deleteById(@Param("id") long id);
    int updateQuestion(@Param("question") EsQuestion question);
    int createQuestion(@Param("question") EsQuestion question);
}
