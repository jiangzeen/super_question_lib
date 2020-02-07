package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.UserQuestionHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuestionHistoryMapper {

    int insertQHistory(@Param("history")UserQuestionHistory history);

    List<UserQuestionHistory> selectHistoriesByUsernameAndLibId(@Param("username") String username,
                                                                @Param("libId") int libId);
}
