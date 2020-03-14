package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.UserQuestion;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserQuestionMapper {

    void insertQuestion(@Param("questions") List<UserQuestion> questions);

    List<UserQuestion> selectRecentQuestion(String nowDay, String username, int libId);

    List<UserQuestion> selectQuestionWithNullPracticeTime(String username, int libId);

    UserQuestion selectQuestionByUsernameAndId(@Param("username") String username,
                                               @Param("questionId") int questionId);

    void updateQuestionEasinessEtc(@Param("easiness") float easiness, @Param("repetitions") int repetitions,
                                   @Param("interval") int interval, @Param("practiceTime")LocalDateTime time,
                                   @Param("questionId") int id, @Param("username") String username);
}
