package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.po.ChoiceQuestion;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceQuestionMapper {
    void insertCQuestion(ChoiceQuestion cq);
}
