package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;

import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsUserPaperRepository extends ElasticsearchRepository<EsUserPaper,Integer>
{
}
