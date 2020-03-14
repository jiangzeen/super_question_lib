package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;


import com.jxust.qq.superquestionlib.dto.admin.EsHotExam;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotExamRepository extends ElasticsearchRepository<EsHotExam,Integer>
{
}
