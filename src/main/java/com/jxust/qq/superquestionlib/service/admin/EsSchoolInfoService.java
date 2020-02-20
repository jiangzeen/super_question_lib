package com.jxust.qq.superquestionlib.service.admin;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsSchoolInfoRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsSchoolInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class EsSchoolInfoService {
    @Autowired
    EsSchoolInfoRepository schoolInfoRepository;

    //进行模糊匹配查找,正在进行的考试名
    public Iterable<EsSchoolInfo> boolQuery(String queryString,int schoolParentId) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //级别
        TermQueryBuilder schoolLevelQuery = QueryBuilders.termQuery("schoolParentId",schoolParentId);
        boolQuery.filter(schoolLevelQuery);
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("schoolName",queryString);
        boolQuery.filter(fuzzyQueryBuilder);
        PageRequest pageRequest = PageRequest.of(0, 20);
        Iterable<EsSchoolInfo> schoolInfos = schoolInfoRepository.search(boolQuery);
        return schoolInfos;
    }
}