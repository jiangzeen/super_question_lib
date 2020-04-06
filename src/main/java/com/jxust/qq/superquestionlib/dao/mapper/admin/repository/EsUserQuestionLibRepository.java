package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;
import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionLib;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsUserQuestionLibRepository extends ElasticsearchRepository<EsUserQuestionLib,Integer>
{
    int deleteByUserName(String userName);
    List<EsUserQuestionLib> findByUserName(String userName);
}
