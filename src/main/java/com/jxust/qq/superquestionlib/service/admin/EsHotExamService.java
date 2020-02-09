package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsHotExamRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsHotExam;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EsHotExamService {
    @Autowired
    EsHotExamRepository hotExam;
    //进行模糊匹配查找,正在进行的考试名
    public List<EsHotExam> matchHotExam(String queryString, int pagenum, int pagesize)
    {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,"examName","examTimeLevel");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsHotExam> hotExams=hotExam.search(matchQuery,pageRequest);
        List<EsHotExam> hotExamList=new ArrayList<>();
        for (EsHotExam exam : hotExams) {
            hotExamList.add(exam);
        }
        return hotExamList;
    }
    //根据id完全匹配查找
    public EsHotExam termHotExam(int id)
    {
        EsHotExam hotExam2=new EsHotExam();
        hotExam2.setExamName("英国人");
        hotExam2.setExamTimeLevel(5);
        hotExam2.setId(1);
        hotExam2.setTagIds("YAEH");
        hotExam.save(hotExam2);
        EsHotExam Exam=hotExam.findById(Integer.valueOf(id)).get();
        return Exam;
    }
    //true 降序 false 升序
    public List<EsHotExam> boolHotExam(String queryStrings, boolean dir, int pagenum, int pagesize, LocalTime toStartBegin
            , LocalTime toStartEnd, LocalTime startTimeBegin, LocalTime startTimeEnd)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        //按字符查询
        MultiMatchQueryBuilder matchQuery=QueryBuilders.multiMatchQuery(queryStrings,"examName","tagsId");
        boolQuery.must(matchQuery);
        //过滤条件
        RangeQueryBuilder toStartQuery=QueryBuilders.rangeQuery("toStartTime").gt(toStartBegin).gte(toStartEnd);
        RangeQueryBuilder startTimeQuery=QueryBuilders.rangeQuery("examStartTime").gt(startTimeBegin).gte(startTimeEnd);
        boolQuery.filter(toStartQuery);
        boolQuery.filter(startTimeQuery);
        //分页
        Sort sort;
        if(dir==true)
            sort=Sort.by(Sort.Direction.DESC,"examTimeLevel");
        else
            sort=Sort.by(Sort.Direction.ASC,"examTimeLevel");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        Iterable<EsHotExam> iterables=hotExam.search(boolQuery,pageRequest);
        Iterator<EsHotExam> iterator=iterables.iterator();
        ArrayList<EsHotExam> hotExams=new ArrayList<>();
        while(iterator.hasNext())
        {
            hotExams.add(iterator.next());
        }
        return hotExams;
    }
}
