package com.jxust.qq.superquestionlib.dao.mapper.admin;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionHistory;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionLib;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface EsUserQuestionHistoryMapper
{
    int deleteByHistoryId(@Param("id") long id);
    int counts(@Param("username") String username);
    ArrayList<EsUserQuestionHistory> selectHistoriesByUsername(@Param("username")String username,
                                                               @Param("pagenum") int pagenum,
                                                               @Param("pagesize")int pagesize);
}
