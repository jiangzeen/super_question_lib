package com.jxust.qq.superquestionlib.service;

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

@Service
public class UserQuestionHistoryService {

    private final UserQuestionHistoryMapper historyMapper;
    private final QuestionService questionService;

    public UserQuestionHistoryService(UserQuestionHistoryMapper historyMapper, QuestionService questionService) {
        this.historyMapper = historyMapper;
        this.questionService = questionService;
    }

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
}
