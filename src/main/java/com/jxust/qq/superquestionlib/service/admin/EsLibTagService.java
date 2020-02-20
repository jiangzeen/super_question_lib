package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsLibTagRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsLibTag;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EsLibTagService
{
    @Autowired
    EsLibTagRepository eslibTagRepository;
    //进行模糊匹配
    public Iterable<EsLibTag> boolQuery(String queryString,int parentTagId) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //级别
        TermQueryBuilder parentQuery = QueryBuilders.termQuery("parentTagId", String.valueOf(parentTagId));
        boolQuery.filter(parentQuery);
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("libTagName", queryString);
        boolQuery.must(fuzzyQueryBuilder);
        PageRequest pageRequest = PageRequest.of(0, 20);
        Iterable<EsLibTag> libTags =eslibTagRepository.search(boolQuery, pageRequest);
        return libTags;
    }

}
