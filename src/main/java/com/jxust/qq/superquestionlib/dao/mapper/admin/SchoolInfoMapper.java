package com.jxust.qq.superquestionlib.dao.mapper.admin;

import com.jxust.qq.superquestionlib.dto.SchoolInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolInfoMapper
{
   SchoolInfo selectSchoolInfoById(@Param("id")int id);
}
