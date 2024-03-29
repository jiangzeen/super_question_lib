package com.jxust.qq.superquestionlib.service;



import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.QuestionMapper;
import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.util.QuestionTypeEnum;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


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
        System.out.println("vo:" + vo);
        String answer;
        JSONObject JSONQuestion = JSONObject.parseObject(vo.getContent());
        answer = JSONQuestion.getString("answer");
        if (answer == null || answer.trim().equals("")) {
            return null;
        }else {
            return answer;
        }
    }

    public List<QuestionVO> findQuestionListByIds(List<Integer> ids) {
        return questionMapper.selectQuestionList(ids);
    }

    public List<Integer> findOtherQuestionId(int libId, List<Integer> srcId, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("ids", srcId);
        params.put("libId", libId);
        params.put("limit", limit);
        return questionMapper.selectQuestionByLibId(params);
    }

    public List<QuestionVO> practiceQuestions(List<Integer> types, int nums) {
        // 难点:时间间隔算法
        int sum = types.stream().reduce(Integer::sum).orElse(0);
        return null;
    }


    public List<JSONObject> findQuestionByLibId(int libId, String username){
        List<QuestionVO> vos =
                questionMapper.selectQuestionsByIdAndName(libId, username);
        return vos.stream().map(vo -> {
            JSONObject data = new JSONObject();
            JSONObject question = JSONObject.parseObject(vo.getContent());
            question.remove("answer");
            data.put("content", question);
            data.put("questionId", vo.getId());
            return data;
        }).collect(Collectors.toList());
    }

    public QuestionTypeEnum findQuestionType(QuestionVO questionVO) {
        JSONObject qContent = JSONObject.parseObject(questionVO.getContent());
        return QuestionTypeEnum.findTypeById(qContent.getInteger("type"));
    }
}
