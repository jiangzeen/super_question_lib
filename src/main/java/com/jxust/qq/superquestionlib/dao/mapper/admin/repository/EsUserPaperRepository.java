package com.jxust.qq.superquestionlib.dao.mapper.admin.repository;

import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EsUserPaperRepository extends ElasticsearchRepository<EsUserPaper,Integer>
{
    int deleteByUserName(String userName);
    List<EsUserPaper> findByUserName(String userName);
}
