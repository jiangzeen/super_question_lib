package com.jxust.qq.superquestionlib.service.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.EsQuestionMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsLibTagRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionLibRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionTypeRepositoy;
import com.jxust.qq.superquestionlib.dto.QuestionLib;
import com.jxust.qq.superquestionlib.dto.admin.EsLibTag;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
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
public class EsQuestionService
{
    @Autowired
    EsQuestionRepository questionRepository;
    @Autowired
    EsQuestionTypeRepositoy questionTypeRepositoy;
    @Autowired
    EsQuestionLibRepository questionLibRepository;
    private final EsQuestionMapper questionMapper;
    public static int pagesum;

    public EsQuestionService(EsQuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    //进行模糊匹配查找
    public List<EsQuestion> matchQuestion(String queryString, int pagenum, int pagesize)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        if(!queryString.isEmpty()) {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryString,
                    "questionLibName", "questionContent", "keyword");
           boolQuery.filter(matchQuery);
        }
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsQuestion> questions=questionRepository.search(boolQuery,pageRequest);
        pagesum= (int) questionRepository.search(boolQuery,pageRequest).getTotalElements();
        List<EsQuestion> questionList=new ArrayList<>();
        for (EsQuestion question : questions) {
            questionList.add(question);
        }
        return questionList;
    }
    //根据id完全匹配查找
    public EsQuestion termQuestion(long id)
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
    public List<EsQuestion> boolQuestion(String queryString,int pagenum, int pagesize,int questionTypeId
    ,int questionLevelBegin,int questionLevelEnd)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        if(!queryString.isEmpty())
        {
            boolQueryBuilder.filter(QueryBuilders.fuzzyQuery("questionLibName","questionContent"));
        }
        if(questionLevelEnd!=0)
        {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("questionLevel").gte(questionLevelBegin).lte(questionLevelEnd));
        }
        if(questionTypeId!=0) {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("questionTypeId", questionTypeId);
            boolQueryBuilder.filter(termQuery);
        }
        //分页
        Sort sort;
        sort=Sort.by(Sort.Direction.ASC,"questionLibId");
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
    public JSONObject staticQuestionLevel()
    {
        JSONObject data=new JSONObject();
        StatsAggregationBuilder stats=AggregationBuilders.stats("staticLevel").field("questionLevel");
        SearchQuery searchQuery=new NativeSearchQueryBuilder().addAggregation(stats)
                .build();
        AggregatedPage<EsQuestion> search=(AggregatedPage)questionRepository.search(searchQuery);
        InternalStats statsTerms= (InternalStats) search.getAggregation("staticLevel");
        double max = 0;
        double min = 0;
        long count = 0;
        double avg=0;
        max=statsTerms.getMax();
        min=statsTerms.getMin();
        avg=statsTerms.getAvg();
        count=statsTerms.getCount();
        DecimalFormat df = new DecimalFormat("#.00");
        data.put("max",max);
        data.put("min",min);
        data.put("avg",Double.parseDouble(df.format(avg)));
        data.put("count",count);
        return data;
    }
    public JSONObject findByQuestionLibId(String queryString, int pagenum, int pagesize, int QuestionLibId)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        TermQueryBuilder term=QueryBuilders.termQuery(
                "questionLibId",QuestionLibId);
        boolQueryBuilder.filter(term);
        MultiMatchQueryBuilder matchQuery;
        if(!queryString.isEmpty())
        {
            matchQuery= QueryBuilders.multiMatchQuery(queryString,"questionContent","keyword");
            boolQueryBuilder.filter(matchQuery);
        }
        AvgAggregationBuilder statsRight=AggregationBuilders.avg("avgRightTime").field("rightTime");
        AvgAggregationBuilder statsWrong=AggregationBuilders.avg("avgWrongTime").field("wrongTime");
        Sort sort;
        sort=Sort.by(Sort.Direction.ASC,"questionLevel");
        TermsAggregationBuilder terms=AggregationBuilders.terms("aggName").field("questionLibId")
               .subAggregation(statsRight).subAggregation(statsWrong);
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
       SearchQuery searchQuery=new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).addAggregation(terms)
                .build();
        AggregatedPage<EsQuestion> search=(AggregatedPage)questionRepository.search(searchQuery);
        LongTerms agg= (LongTerms) search.getAggregation("aggName");
        double avg1,avg2;
        String s1 = null;
        List<LongTerms.Bucket> buckets=agg.getBuckets();
        if(!buckets.isEmpty()) {
            for (LongTerms.Bucket bucket : buckets) {
                InternalAvg avgRightTime = (InternalAvg) bucket.getAggregations().asMap().get("avgRightTime");
                InternalAvg avgWrongTime = (InternalAvg) bucket.getAggregations().asMap().get("avgWrongTime");
                avg1 = avgRightTime.getValue();
                avg2 = avgWrongTime.getValue();
                DecimalFormat df = new DecimalFormat("0.00");
                s1 = df.format(avg1 / (avg1 + avg2));
            }
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



    //删
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
                questionRepository.save(question);
        }
        return status;
    }
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
           **/
            questionRepository.save(question);
        }
        return status;
    }
}
