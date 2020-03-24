package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;


import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsUserTasksRepository extends ElasticsearchRepository<EsUserTasks,Integer>
{
    int deleteByUserName(String userName);
    List<EsUserTasks> findByUserName(String userName);
}
