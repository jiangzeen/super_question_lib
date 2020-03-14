package com.jxust.qq.superquestionlib.service.admin;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.Terms;
import org.apache.poi.util.Internal;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
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
public class EsQuestionService
{
    @Autowired
   EsQuestionRepository questionRepository;
    public static int pagesum;
    //进行模糊匹配查找
    public List<EsQuestion> matchQuestion(String queryString, int pagenum, int pagesize)
    {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,
                "questionLibName","questionContent","keyword");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsQuestion> questions=questionRepository.search(matchQuery,pageRequest);
        pagesum= (int) questionRepository.search(matchQuery,pageRequest).getTotalElements();
        List<EsQuestion> questionList=new ArrayList<>();
        for (EsQuestion question : questionList) {
            questionList.add(question);
        }
        return questionList;
    }
    //根据id完全匹配查找
    public EsQuestion termQuestion(int id)
    {
        EsQuestion question=null;
        if(questionRepository.findById(id).isPresent())
        {
            question=questionRepository.findById(id).get();
            pagesum=1;
        }
        else pagesum=0;
        return question;
    }
    //true 降序 false升序
    // 1	单选题
    //2	多选题
    //3	填空题
    //4	判断题
    //5	解答题
    public List<EsQuestion> boolQuestion(int pagenum, int pagesize,int questionTypeId)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        if(questionTypeId!=0) {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("questionTypeId", questionTypeId);
            boolQueryBuilder.filter(termQuery);
        }
        //分页
        Sort sort;
        sort=Sort.by(Sort.Direction.ASC,"QuestionLibId");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum= (int) questionRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        Iterable<EsQuestion> iterables=questionRepository.search(boolQueryBuilder,pageRequest);
        Iterator<EsQuestion> iterator=iterables.iterator();
        ArrayList<EsQuestion> questions=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsQuestion question=iterator.next();
            questions.add(question);
        }
        return questions;
    }
    public JSONObject findByQuestionLibId(String queryString, int pagenum, int pagesize, int QuestionLibId)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        TermQueryBuilder term=QueryBuilders.termQuery("QuestionLibId",QuestionLibId);
        boolQueryBuilder.filter(term);
        MultiMatchQueryBuilder matchQuery;
        if(queryString.isEmpty())
        {
            matchQuery= QueryBuilders.multiMatchQuery(queryString,"questionContent","keyword");
            boolQueryBuilder.filter(matchQuery);
        }
        StatsAggregationBuilder statsRight=AggregationBuilders.stats("avgRightTime").field("rightTime");
        StatsAggregationBuilder statsWrong=AggregationBuilders.stats("avgWrongTime").field("wrongTime");
        Sort sort;
        sort=Sort.by(Sort.Direction.ASC,"QuestionLevel");
        TermsAggregationBuilder terms=AggregationBuilders.terms("aggName").field("questionLibId")
                .subAggregation(statsRight).subAggregation(statsWrong);
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        SearchQuery searchQuery=new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).addAggregation(terms)
                .build();
        AggregatedPage<EsUser> search=(AggregatedPage)questionRepository.search(searchQuery);
        StringTerms agg= (StringTerms) search.getAggregation(String.valueOf(QuestionLibId));
        double avg1,avg2;
        String s1 = null;
        List<StringTerms.Bucket> buckets=agg.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            InternalAvg avgRightTime=(InternalAvg) bucket.getAggregations().asMap().get("avgRightTime");
            InternalAvg avgWrongTime=(InternalAvg) bucket.getAggregations().asMap().get("avgWrongTime");
            avg1=avgRightTime.getValue();
            avg2=avgWrongTime.getValue();
            DecimalFormat df = new DecimalFormat("#.00");
            s1=df.format(avg1/(avg1+avg2));
        }
        pagesum= (int) questionRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        Iterable<EsQuestion>questions=questionRepository.search(boolQueryBuilder,pageRequest);
        ArrayList<EsQuestion> questionList=new ArrayList<>();
        for (EsQuestion question : questions) {
            questionList.add(question);
        }
        JSONObject data=new JSONObject();
        data.put("esQuestions",questionList);
        data.put("pagesum",pagesum);
        data.put("avgRightTime",s1);
        return data;
    }
}
