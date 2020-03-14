package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;

import com.jxust.qq.superquestionlib.dto.admin.EsSchoolInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsSchoolInfoRepository extends ElasticsearchRepository<EsSchoolInfo,Integer>
{
}

