package com.jxust.qq.superquestionlib.dao.mapper.admin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilMapper
{
    String  getSchoolParent(@Param("id") int id);
    String  getTagParent(@Param("id") int id);
}
