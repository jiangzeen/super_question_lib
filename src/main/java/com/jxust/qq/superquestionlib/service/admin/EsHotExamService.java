package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.HotExamMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsHotExamRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsHotExam;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class EsHotExamService {
    @Autowired
    EsHotExamRepository hotExam;
    private final HotExamMapper examMapper;
    public static long pagesum;

    public EsHotExamService(HotExamMapper examMapper) {
        this.examMapper = examMapper;
    }

    //进行模糊匹配查找,正在进行的考试名
    public List<EsHotExam> matchHotExam(String queryString, int pagenum, int pagesize)
    {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,"examName","examTagIds");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsHotExam> hotExams=hotExam.search(matchQuery,pageRequest);
        pagesum=hotExam.search(matchQuery,pageRequest).getTotalElements();
        List<EsHotExam> hotExamList=new ArrayList<>();
        for (EsHotExam exam : hotExams) {
            hotExamList.add(exam);
        }
        return hotExamList;
    }
    //根据id完全匹配查找
    public EsHotExam termHotExam(int id)
    {
        EsHotExam Exam=null;
        if(hotExam.findById(id).isPresent())
        {
            Exam=hotExam.findById(id).get();
            pagesum=1;
        }
        else pagesum=0;
        return Exam;
    }
    //true 降序 false 升序
    public List<EsHotExam> boolHotExam(String queryStrings, boolean dir, int pagenum, int pagesize,Date startTimeBegin,Date startTimeEnd)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        //按字符查询

        MultiMatchQueryBuilder matchQuery=QueryBuilders.multiMatchQuery(queryStrings,"examName","tagsId");
        boolQuery.must(matchQuery);
        //过滤条件
        RangeQueryBuilder startTimeQuery=QueryBuilders.rangeQuery("examStartTime").from(startTimeBegin.getTime()-3600*8*1000).to(startTimeEnd.getTime()-3600*8*1000);
        boolQuery.filter(startTimeQuery);
        //分页
        Sort sort;

        if(dir==true)
            sort=Sort.by(Sort.Direction.DESC,"examTimeLevel");
        else
            sort=Sort.by(Sort.Direction.ASC,"examTimeLevel");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum=hotExam.search(boolQuery,pageRequest).getTotalElements();
        Iterable<EsHotExam> iterables=hotExam.search(boolQuery,pageRequest);
        Iterator<EsHotExam> iterator=iterables.iterator();
        ArrayList<EsHotExam> hotExams=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsHotExam exam=iterator.next();
            try {
                exam.setExamStartTime(DateFormat.parseUTCText(exam.getExamStartTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            hotExams.add(exam);
        }
        return hotExams;
    }




    //改
   public int updateExamByName(EsHotExam esHotExam)
   {
       esHotExam.setId(examMapper.selectExamByExamName(esHotExam.getExamName()).getId());
       if(hotExam.existsById(esHotExam.getId()))
           hotExam.save(esHotExam);
       return examMapper.updateExamByName(esHotExam);
   }


    public int updateExamById(EsHotExam esHotExam)
    {
        if(hotExam.existsById(esHotExam.getId()))
            hotExam.save(esHotExam);
        return examMapper.updateExamById(esHotExam);
    }
    //查
    public List<EsHotExam> findAll(int pagenum,int pagesize)
    {
        Sort sort=Sort.by(Sort.Direction.ASC,"id");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum= hotExam.findAll(pageRequest).getTotalElements();
        List<EsHotExam> examList=new ArrayList<>();
        for (EsHotExam hotExam:hotExam.findAll(pageRequest)) {
            examList.add(hotExam);
        }
        return examList;
    }
    //增加
    public int creatHotExam(EsHotExam esHotExam)
    {
       int status=examMapper.insertExam(esHotExam);
       if(status<0) {return status;}
       else {
           hotExam.save(examMapper.selectExamByExamName(esHotExam.getExamName()));
           return status;
       }
    }
    //删除
    public int deleteByExamName(String examName)
    {
        EsHotExam esHotExam=examMapper.selectExamByExamName(examName);
        if(hotExam.existsById(esHotExam.getId()))
            hotExam.delete(esHotExam);
        return examMapper.deleteByExamName(examName);
    }
    //删
    public int deleteByExamId(int id)
    {
        if(hotExam.existsById(id))
        hotExam.deleteById(id);
        return examMapper.deleteById(id);
    }
}