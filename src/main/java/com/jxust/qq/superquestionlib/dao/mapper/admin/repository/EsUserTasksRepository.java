package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;


import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsUserTasksRepository extends ElasticsearchRepository<EsUserTasks,Integer>
{
}
