package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.dao.mapper.UserQuestionMapper;
import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.dto.UserQuestion;
import com.jxust.qq.superquestionlib.util.DateFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.jxust.qq.superquestionlib.controller.QuestionLibController.userLocal;

@Service
public class UserQuestionService {

    private final UserQuestionMapper questionMapper;

    public UserQuestionService(UserQuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    public void addUserQuestion(List<Question> questionList) {
        // 从静态域获取到threadLocal对象
        String username = userLocal.get();
        List<UserQuestion> userQuestionList =
                questionList.stream().map(question -> {
            UserQuestion userQuestion = new UserQuestion();
            userQuestion.setUsername(username);
            userQuestion.setQuestionId((int)question.getQuestionId());
            userQuestion.setContent(question.getQuestionContent());
            userQuestion.setCreateTime(question.getCreateTime());
            userQuestion.setLibId((int) question.getQuestionLibId());
            userQuestion.setEasiness(2.5f);
            userQuestion.setInterval(0);
            userQuestion.setRepetitions(0);
            return userQuestion;
        }).collect(Collectors.toList());
        questionMapper.insertQuestion(userQuestionList);
    }

    public List<UserQuestion> findQuestionByRecent(String username, int libId) {
        LocalDateTime time = LocalDateTime.now();
        // 转换为yyyy-mm-dd的形式
        String nowString = time.format(DateFormat.DAY_FORMATTER.getFormatter());
        List<UserQuestion> recentQuestion = questionMapper.selectRecentQuestion(nowString, username, libId);
        List<UserQuestion> recentQuestionWithNullPracticeTime
                           = questionMapper.selectQuestionWithNullPracticeTime(username, libId);
        recentQuestion.addAll(recentQuestionWithNullPracticeTime);
        return recentQuestion;
    }

    public UserQuestion findQuestion(String username, int questionId) {
        return
            questionMapper.selectQuestionByUsernameAndId(username, questionId);
    }

    public void updateQuestionAfterProcess(UserQuestion updatedQuestion) {
        float easiness = updatedQuestion.getEasiness();
        int repetition = updatedQuestion.getRepetitions();
        int interval = updatedQuestion.getInterval();
        String username = updatedQuestion.getUsername();
        int id = updatedQuestion.getQuestionId();
        LocalDateTime practiceTime = updatedQuestion.getLastPracticeTime();
        questionMapper.updateQuestionEasinessEtc(easiness, repetition, interval, practiceTime, id, username);
    }
}
