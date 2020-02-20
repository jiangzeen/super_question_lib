package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;

import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsQuestionRepository extends ElasticsearchRepository<EsQuestion,Integer>
{
}
