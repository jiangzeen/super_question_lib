package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;

import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsQuestionLibRepository extends ElasticsearchRepository<EsQuestionLib,Long> {
}
