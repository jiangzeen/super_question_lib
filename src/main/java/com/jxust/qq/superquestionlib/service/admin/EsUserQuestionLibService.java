package com.jxust.qq.superquestionlib.service.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.EsQuestionMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionLibRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionTypeRepositoy;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsUserQuestionLibRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionLib;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.stats.InternalStats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class EsUserQuestionLibService
{
    @Autowired
    EsUserQuestionLibRepository userQuestionLibRepository;
    @Autowired
    EsQuestionLibRepository questionLibRepository;
    public static int pagesum;
    public List<EsUserQuestionLib> boolQuestion(String queryString,int pagenum, int pagesize,String userName,boolean dir)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        if(!queryString.isEmpty())
        {
            boolQueryBuilder.filter(QueryBuilders.fuzzyQuery("questionLibName","privateName"));
        }
        TermQueryBuilder termQueryBuilder=QueryBuilders.termQuery("userName",userName);
        boolQueryBuilder.filter(termQueryBuilder);
        //分页
        Sort sort;
        if(dir==true)
        sort=Sort.by(Sort.Direction.ASC,"questionLibIdImportance");
        else
            sort=Sort.by(Sort.Direction.DESC,"questionLibIdImportance");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum= (int) userQuestionLibRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        Iterable<EsUserQuestionLib> iterables=userQuestionLibRepository.search(boolQueryBuilder,pageRequest);
        Iterator<EsUserQuestionLib> iterator=iterables.iterator();
        ArrayList<EsUserQuestionLib> questions=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsUserQuestionLib question=iterator.next();
            questions.add(question);
        }
        return questions;
    }
    /** //删
    public int deleteById(long id)
    {
        if(questionRepository.existsById(id))
            questionRepository.deleteById(id);
        return questionMapper.deleteById(id);
    }
    //修改
    public int updateById(EsQuestion question)
    {
        int status=questionMapper.updateQuestion(question);
        if(status>0)
        {
                if(questionLibRepository.findById((long)question.getQuestionLibId()).isPresent())
                {
                    question.setQuestionLibName(questionLibRepository.findById((long)question.getQuestionLibId()).get().getQuestionLibName());
                }
                /**  if(questionTypeRepositoy.findById((long)question.getQuestionTypeId()).isPresent())
                 {
                 question.setQ
                 }
                 **/
           /**     questionRepository.save(question);
        }
        return status;
    }
    **
    //增加
    public int creatQuestion(EsQuestion question)
    {
        int status;
        status=questionMapper.createQuestion(question);
        if(status>0)
        {
            if(questionLibRepository.findById((long)question.getQuestionLibId()).isPresent())
            {
                question.setQuestionLibName(questionLibRepository.findById((long)question.getQuestionLibId()).get().getQuestionLibName());
            }
          /**  if(questionTypeRepositoy.findById((long)question.getQuestionTypeId()).isPresent())
            {
                question.setQ
            }

            questionRepository.save(question);
        }
        return status;
    }
    **/
}
