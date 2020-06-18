package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.dto.UserQuestionLib;
import com.jxust.qq.superquestionlib.vo.LibSimpleInfoVO;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuestionLibMapper {

    UserQuestionLib selectByLibIdAndUsername(@Param("username") String username, @Param("libId") int libId);

    void insertUserLib(@Param("userLib")UserQuestionLib userQuestionLib);

    List<QuestionLibVO> selectLibByUsername(@Param("username") String username, @Param("typeId") Integer typeId,
                                            @Param("limit") int limit, @Param("total") int total);

    QuestionLibVO selectQuestionsByIdAndUsername(@Param("username") String username, @Param("libId") int libId,
                                                 @Param("typeId") Integer typeId);

    void updateQuestionLib(@Param("username") String username, @Param("info")LibSimpleInfoVO infoVO);

    void deleteById(int userLibId);

    int selectUserLibsNumbers(String username);
}
