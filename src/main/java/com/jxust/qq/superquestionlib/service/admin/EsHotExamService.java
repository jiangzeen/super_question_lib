package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.LibTagMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.EsHotExamMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.EsLibTagMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsHotExamRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsHotExam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EsHotExamService {
    @Autowired
    EsHotExamRepository hotExam;
    private final EsHotExamMapper examMapper;
    private final EsLibTagMapper libTagMapper;
    public static long pagesum;

    public EsHotExamService(EsHotExamMapper examMapper, EsLibTagMapper libTagMapper) {
        this.examMapper = examMapper;
        this.libTagMapper = libTagMapper;
    }
    public String transferIdsToString(String ids)
    {
        String s1="";
        if(StringUtils.isBlank(ids))
        {
            System.out.println("Blank"+ids);
            return s1;
        }
            String regex="[0-9]+";
            Pattern pattern=Pattern.compile(regex);
            Matcher matcher=pattern.matcher(ids);
            while(matcher.find())
            {
                String id=matcher.group(0);
                String temp=libTagMapper.selectTagName(Integer.parseInt(id));
                if(!s1.equals(""))
                s1=s1+","+temp;
                else
                s1=s1+temp;
            }
        return s1;
    }
    //进行模糊匹配查找,正在进行的考试名
    public List<EsHotExam> matchHotExam(String queryString, int pagenum, int pagesize)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        if(!queryString.isEmpty()) {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryString, "examName", "tagIds");
            boolQuery.filter(matchQuery);
        }
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsHotExam> hotExams=hotExam.search(boolQuery,pageRequest);
        pagesum=hotExam.search(boolQuery,pageRequest).getTotalElements();
        List<EsHotExam> hotExamList=new ArrayList<>();
        for (EsHotExam exam : hotExams) {
            exam.setExamTagInfo(transferIdsToString(exam.getTagIds()));
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
            Exam.setExamTagInfo(transferIdsToString(Exam.getTagIds()));
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
        if(!queryStrings.isEmpty()) {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryStrings, "examName", "tagIds");
            boolQuery.must(matchQuery);
        }
        //过滤条件
        if(startTimeBegin!=null&&startTimeEnd!=null) {
            RangeQueryBuilder startTimeQuery = QueryBuilders.rangeQuery("examStartTime").timeZone("UTC").gte(startTimeBegin.getTime()).lte(startTimeEnd.getTime());
            boolQuery.filter(startTimeQuery);
        }
        //分页
        Sort sort;

        if(dir==true)
            sort=Sort.by(Sort.Direction.DESC,"examTimeLevel");
        else
            sort=Sort.by(Sort.Direction.ASC,"examTimeLevel");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum=hotExam.search(boolQuery,pageRequest).getTotalElements();
        Iterable<EsHotExam> iterables=hotExam.search(boolQuery,pageRequest);
        ArrayList<EsHotExam> hotExams=new ArrayList<>();
        for (EsHotExam esHotExam : iterables) {
            esHotExam.setExamTagInfo(transferIdsToString(esHotExam.getTagIds()));
            hotExams.add(esHotExam);
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
            hotExam.setExamTagInfo(transferIdsToString(hotExam.getTagIds()));
            examList.add(hotExam);
        }
        return examList;
    }
    //增加
    public int creatHotExam(EsHotExam esHotExam)
    {
       int status=examMapper.insertExam(esHotExam);
       if(status<=0) {return status;}
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
    public int deleteById(int id)
    {
        if(hotExam.existsById(id))
        hotExam.deleteById(id);
        return examMapper.deleteById(id);
    }
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }
}