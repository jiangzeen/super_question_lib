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
    public static int pagesum;
    @Autowired
    EsLibTagRepository eslibTagRepository;
    //进行模糊匹配
    public Iterable<EsLibTag> boolQuery(String queryString,int parentTagId) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //级别
        System.out.println(parentTagId);
        System.out.println(parentTagId);
        System.out.println(parentTagId);
        TermQueryBuilder parentQuery = QueryBuilders.termQuery("parentTagId", parentTagId);
        boolQuery.filter(parentQuery);
        if(!queryString.isEmpty()) {
            FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("tagName", queryString);
            boolQuery.must(fuzzyQueryBuilder);
        }
        PageRequest pageRequest = PageRequest.of(0, 20);
        Iterable<EsLibTag> libTags =eslibTagRepository.search(boolQuery, pageRequest);
        pagesum= (int) eslibTagRepository.search(boolQuery, pageRequest).getTotalElements()+1;
        return libTags;
    }

}
