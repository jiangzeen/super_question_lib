package com.jxust.qq.superquestionlib.dao.mapper.admin;

import com.jxust.qq.superquestionlib.dto.admin.EsHotExam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsHotExamMapper
{
     List<EsHotExam> findExams();
     EsHotExam selectExamByExamName(@Param("examName") String examName);
     EsHotExam selectExamById(@Param("id") int id);
     int insertExam(@Param("hotExam") EsHotExam esHotExam);
     int updateExam(@Param("hotExam") EsHotExam esHotExam);
     int deleteByExamName(@Param("examName") String examName);
     int deleteById(@Param("id") int id);
     int updateExamByName(@Param("hotExam") EsHotExam esHotExam);
     int updateExamById(@Param("hotExam") EsHotExam esHotExam);
}
