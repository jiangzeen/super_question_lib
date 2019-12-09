package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.po.Question;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionMapper {
    void insert(Question question);
}
