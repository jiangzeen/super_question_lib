package com.jxust.qq.superquestionlib.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.UserQuestionHistoryMapper;
import com.jxust.qq.superquestionlib.dto.UserQuestionHistory;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import com.jxust.qq.superquestionlib.vo.UserQuestionHistoryVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserQuestionHistoryService {

    private final UserQuestionHistoryMapper historyMapper;
    private final QuestionService questionService;

    public UserQuestionHistoryService(UserQuestionHistoryMapper historyMapper, QuestionService questionService) {
        this.historyMapper = historyMapper;
        this.questionService = questionService;
    }

    private final int DEFAULT_LIMIT = 10;
    public void addQuestionHistory(String username, int id, String result) {
        UserQuestionHistory questionHistory = new UserQuestionHistory();
        questionHistory.setQuestionId(id);
        questionHistory.setUsername(username);
        questionHistory.setUserResult(result);
        String answer = questionService.findAnswerByQuestionId(id);
        QuestionVO qVO = questionService.findQuestionById(id);
        questionHistory.setResult(answer);
        questionHistory.setQuestionLibId(qVO.getLibId());
        questionHistory.setSubmitTime(LocalDateTime.now());
        historyMapper.insertQHistory(questionHistory);
    }

    public List<UserQuestionHistoryVO> findHistoriesByLibIdAndUsername(String username, int libId) {
        // 对时间进行格式化
        List<UserQuestionHistory> historyList = historyMapper.selectHistoriesByUsernameAndLibId(username, libId);
        List<UserQuestionHistoryVO> historyVOList = new ArrayList<>();
        historyList.forEach(it -> {
            UserQuestionHistoryVO historyVO = new UserQuestionHistoryVO();
            historyVO.setQuestionId(it.getQuestionId());
            historyVO.setQuestionLibId(it.getQuestionLibId());
            historyVO.setResult(it.getResult());
            historyVO.setUserResult(it.getUserResult());
            historyVO.setSubmitTime(
                    it.getSubmitTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            historyVOList.add(historyVO);
        });
        return historyVOList;
    }

    /**
     * 获取历史做题记录,并且判别出是否做正确了
     * @param libId
     * @param username
     * @param limit
     * @return
     */
    public String findErrorQuestionFromHistory(int libId, String username, int limit) {
        StringBuilder builder = new StringBuilder();
        List<Map<String, String>> questionList =
            historyMapper.selectHistoryErrorQuestion(username, libId, limit);
        for (Map<String, String> stringMap : questionList) {
            String result = stringMap.get("result");
            JSONObject content = JSON.parseObject(stringMap.get("content"));
            String answer = content.getString("answer");
            if (!result.equals(answer.trim())) {
                builder.append(stringMap.get("questionId")).append(",");
            }
        }
        return builder.toString();
    }

    /**
     * 返回用户对一个题库最近做过的题目(包含正确和错误)
     * 其中JSONObject如下所示:
     * {
     *     "questionId": "",
     *     "result": "1"    // 1代表正确, 0代表错误, -1代表此题目无答案
     *     "content": ""    // 题目题干
     *     "submitTime": "" // 做题时间
     * }
     * @param libId 题库Id
     * @param username 用户名
     * @param limit 返回的数目
     * @return JSONArray
     */
    public JSONArray findRecentQuestionWithLimit(int libId, String username, int limit) {
        JSONArray array = new JSONArray();
        List<Map<String, String>> recentList = historyMapper.selectRecentQuestion(username, libId, limit);
        recentList.forEach(question -> {
            String content = question.get("content");
            content = JSONObject.parseObject(content).getString("title");
            question.put("content", content);
            String result = question.get("result");
            if (result == null) {
                question.put("result", "-1");
            } else if (!result.equals(question.get("userResult"))) {
                question.put("result", "1");
            } else {
                question.put("result","0");
            }
            question.remove("userResult");
            JSONObject data = new JSONObject();
            data.putAll(question);
            array.add(data);
        });
        return array;
    }

    public JSONArray findRecentQuestionByDefault(int libId, String username) {
        return
                findRecentQuestionWithLimit(libId, username, DEFAULT_LIMIT);
    }

}
