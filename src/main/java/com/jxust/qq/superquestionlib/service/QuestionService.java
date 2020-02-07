package com.jxust.qq.superquestionlib.service;



import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.QuestionMapper;
import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import org.springframework.stereotype.Service;


@Service
public class QuestionService {
    private final QuestionMapper questionMapper;


    public QuestionService(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    public int insert(Question question) {
        assert question != null;
        return questionMapper.insert(question);
    }

    public void updateContent(int id, String content) {
        assert content != null;
        questionMapper.updateContentById(id, content);
    }

    public QuestionVO findQuestionById(int qId) {
          return questionMapper.selectQuestionById(qId);
    }

    public boolean findUsernameByQId(String username, int question_id) {
        QuestionLibVO libVO = questionMapper.selectUserInfoByQuestionId(question_id);
        return libVO.getUsername().equals(username);
    }

    public String findAnswerByQuestionId(int questionId) {
        QuestionVO vo = questionMapper.selectQuestionById(questionId);
        String answer;
        JSONObject JSONQuestion = JSONObject.parseObject(vo.getContent());
        answer = JSONQuestion.getString("answer");
        if (answer == null || answer.trim().equals("")) {
            return "暂无答案";
        }else {
            return answer;
        }
    }
}
