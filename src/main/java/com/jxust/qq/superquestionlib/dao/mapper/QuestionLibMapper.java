package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.QuestionLib;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionLibMapper {

    int insertQuestionLib(@Param("lib") QuestionLib lib);

    QuestionLib selectQuestionLibById(@Param("id") int libId);

    int selectPublicQuestionLib();

    List<QuestionLibVO> selectPublicLibByPage(@Param("total") int total, @Param("limit") int limit, @Param("typeId") Integer typeId);

    List<Integer> selectLibTypes(@Param("id")int libId);

    String selectUserName(int libId);
}
