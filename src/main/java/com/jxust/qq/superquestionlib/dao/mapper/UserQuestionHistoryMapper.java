package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.UserQuestionHistory;
import com.jxust.qq.superquestionlib.vo.UserQuestionHistoryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserQuestionHistoryMapper {

    int insertQHistory(@Param("history")UserQuestionHistory history);

    List<UserQuestionHistory> selectHistoriesByUsernameAndLibId(@Param("username") String username,
                                                                @Param("libId") int libId);

    List<Map<String, String>> selectHistoryErrorQuestion(String username, int libId, int number);

    List<Map<String, String>> selectRecentQuestion(String username, int libId, int limit);
}
