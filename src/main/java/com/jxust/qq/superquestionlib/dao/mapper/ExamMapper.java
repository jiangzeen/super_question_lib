package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.UserPaper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamMapper {

    void insertExam(@Param("userPaper") UserPaper userPaper);

    void updateExamPaper(@Param("id") int id, @Param("endTime")LocalDateTime endTime,
                         @Param("result") String result);

    List<UserPaper> selectPapersByIdAndName(@Param("username") String username, @Param("id") int libId);

    int findUserPaperById(int id);

    UserPaper selectPaperById(@Param("id") int examId);
}
