package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionLibRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Service
@Slf4j
public class EsQuestionLibService
{
    @Autowired
    EsQuestionLibRepository questionLibRepository;
    public static int pagesum;
    //进行模糊匹配查找
    public List<EsQuestionLib> matchQuestionLib(String queryString, int pagenum, int pagesize)
    {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,
                "questionLibName","questionLibMark");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsQuestionLib> questionLibs=questionLibRepository.search(matchQuery,pageRequest);
        pagesum= (int) questionLibRepository.search(matchQuery,pageRequest).getTotalElements();
        List<EsQuestionLib> questionLibList=new ArrayList<>();
        for (EsQuestionLib questionLib : questionLibList) {
            questionLibList.add(questionLib);
        }
        return questionLibList;
    }
    //根据id完全匹配查找
    public EsQuestionLib termQuestionLib(int id)
    {
        EsQuestionLib questionLib=null;
        if(questionLibRepository.findById((long)id).isPresent())
        {
            questionLib=questionLibRepository.findById((long)id).get();
            pagesum=1;
        }
        else pagesum=0;
        return questionLib;
    }
    //hasPrivate为0私有,为1公有,-1不筛选  LibTagId为0不筛选
    public List<EsQuestionLib> boolQuestionLib(String queryString,int pagenum, int pagesize,int hasPrivate,int questionLibLevelBegin,
                                            int questionLibLevelEnd,int LibTagId)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(queryString,"questionLibName","questionLibMark"));
        if(hasPrivate>=0)
        {
            TermQueryBuilder termQuery=QueryBuilders.termQuery("hasPrivate",hasPrivate);
            boolQueryBuilder.filter(termQuery);
        }
        if(questionLibLevelEnd!=0)
        {
            RangeQueryBuilder rangeQuery=QueryBuilders.rangeQuery("questionLibLevel").gte(questionLibLevelBegin)
                    .lte(questionLibLevelEnd);
        }
           MatchQueryBuilder matchQuery=QueryBuilders.matchQuery("parentIds",LibTagId);
           boolQueryBuilder.filter(matchQuery);
        //分页
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        pagesum= (int) questionLibRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        Iterable<EsQuestionLib> iterables=questionLibRepository.search(boolQueryBuilder,pageRequest);
        Iterator<EsQuestionLib> iterator=iterables.iterator();
        ArrayList<EsQuestionLib> questionLibs=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsQuestionLib questionLib=iterator.next();
            questionLibs.add(questionLib);
        }
        return questionLibs;
    }
}
