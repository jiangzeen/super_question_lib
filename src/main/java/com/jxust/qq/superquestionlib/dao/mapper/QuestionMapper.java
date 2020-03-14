package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface QuestionMapper {

    int insert(@Param("question") Question question);

    int updateContentById(@Param("id") int qId, @Param("content") String content);

    QuestionLibVO selectUserInfoByQuestionId(@Param("question_id") int questionId);

    QuestionVO selectQuestionById(@Param("questionId") int questionId);

    List<Integer> selectQuestionByLibId(Map<String, Object> params);

    List<QuestionVO> selectQuestionList(List<Integer> ids);
}
