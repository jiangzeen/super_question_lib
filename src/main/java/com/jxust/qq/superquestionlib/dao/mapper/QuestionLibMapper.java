package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.po.QuestionLib;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionLibMapper {
    int insertQuestionLib(@Param("lib") QuestionLib lib);
}
