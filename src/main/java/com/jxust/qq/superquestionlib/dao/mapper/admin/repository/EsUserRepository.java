package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;

import com.jxust.qq.superquestionlib.dto.admin.EsUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsUserRepository extends ElasticsearchRepository<EsUser,Long>
{

}
