package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.dao.mapper.ChoiceQuestionMapper;

import com.jxust.qq.superquestionlib.po.ChoiceQuestion;
import org.springframework.stereotype.Service;


@Service
public class QuestionService {

    private final ChoiceQuestionMapper cqMapper;
    public QuestionService(ChoiceQuestionMapper cqMapper) {
        this.cqMapper = cqMapper;
    }

    public void createCQuestion(ChoiceQuestion question) {
        cqMapper.insertCQuestion(question);
    }
}
