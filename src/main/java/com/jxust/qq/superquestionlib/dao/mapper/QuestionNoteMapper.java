package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.QuestionNote;
import com.jxust.qq.superquestionlib.vo.QuestionNoteVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface QuestionNoteMapper {

    int insertNote(@Param("note")QuestionNote note);

    List<QuestionNoteVO> selectNoteByQuestionId(@Param("id") int questionId);

    List<QuestionNoteVO> selectNoteByOtherUsernameAndId(@Param("username") String username,
                                            @Param("id") int questionId);

    List<HashMap<String, Object>> selectNotesByType(@Param("username") String username);

    void updateById(@Param("time")String time, @Param("id") int noteId,
                    @Param("content") String content);

    QuestionNote selectNoteById(@Param("id") int id);
}
