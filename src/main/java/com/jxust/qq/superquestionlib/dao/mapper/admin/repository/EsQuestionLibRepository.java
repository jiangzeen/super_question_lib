package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;

import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsQuestionLibRepository extends ElasticsearchRepository<EsQuestionLib,Long>
{
}
