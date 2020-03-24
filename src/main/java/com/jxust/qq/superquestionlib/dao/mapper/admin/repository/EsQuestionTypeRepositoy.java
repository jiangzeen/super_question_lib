package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;

import com.jxust.qq.superquestionlib.dto.admin.EsQuestionType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsQuestionTypeRepositoy extends ElasticsearchRepository<EsQuestionType,Long>
{
}
