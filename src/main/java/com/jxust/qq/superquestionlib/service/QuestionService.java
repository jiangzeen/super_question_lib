package com.jxust.qq.superquestionlib.service;



import com.jxust.qq.superquestionlib.dao.mapper.QuestionMapper;
import com.jxust.qq.superquestionlib.po.Question;
import org.springframework.stereotype.Service;


@Service
public class QuestionService {
    private final QuestionMapper questionMapper;


    public QuestionService(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    public void insert(Question question) {
        assert question != null;
        questionMapper.insert(question);
    }
}
