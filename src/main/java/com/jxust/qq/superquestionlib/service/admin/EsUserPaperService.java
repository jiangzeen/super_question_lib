package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.EsUserPaperMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionLibRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsUserPaperRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
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
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class EsUserPaperService
{
    @Autowired
    EsUserPaperRepository userPaperRepository;
    @Autowired
    EsQuestionLibRepository questionLibRepository;
    public  static int pagesum;
    private final EsUserPaperMapper userPaperMapper;

    public EsUserPaperService(EsUserPaperMapper userPaperMapper) {
        this.userPaperMapper = userPaperMapper;
    }

    //进行模糊匹配查找
    public List<EsUserPaper> matchUserPaper(String queryString, int pagenum, int pagesize)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        if(!queryString.isEmpty())
        {
            MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,"userName"
                    ,"result","questionLibName");
            boolQuery.filter(matchQuery);
        }
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsUserPaper> users=userPaperRepository.search(boolQuery,pageRequest);
        pagesum= (int) userPaperRepository.search(boolQuery,pageRequest).getTotalElements();
        List<EsUserPaper> paperList=new ArrayList<>();
        for (EsUserPaper userPaper : users)
        {
            paperList.add(userPaper);
        }
        return paperList;
    }
    //根据id完全匹配查找
    public EsUserPaper termUser(int id)
    {
        EsUserPaper userPaper=null;
        if(userPaperRepository.findById(id).isPresent())
        {
            userPaper=userPaperRepository.findById(id).get();
            pagesum=1;
        }
        else pagesum=0;
        return userPaper;
    }
    public ArrayList<EsUserPaper> boolUserPaper(String queryString,int pagenum, int pagesize, int limitTimeBegin,int limitTimeEnd)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        //按字符查询
        if(!queryString.isEmpty()) {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryString, "userName"
                    , "paperResult", "questionLibName");
            boolQuery.must(matchQuery);
        }
        //过滤条件
        if(limitTimeEnd!=0) {
            RangeQueryBuilder limitTimeQuery = QueryBuilders.rangeQuery("limitTime").gte(limitTimeBegin).lte(limitTimeEnd);
            boolQuery.filter(limitTimeQuery);
        }
        //分页
        Sort sort;
        sort=Sort.by(Sort.Direction.DESC,"endTime");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum= (int) userPaperRepository.search(boolQuery,pageRequest).getTotalElements();
        Iterable<EsUserPaper> iterables=userPaperRepository.search(boolQuery,pageRequest);
        Iterator<EsUserPaper> iterator=iterables.iterator();
        ArrayList<EsUserPaper> userPaperList=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsUserPaper userPaper=iterator.next();
            userPaperList.add(userPaper);
        }
        return userPaperList;
    }
    //删
    public int deleteById(int id)
    {
        if(userPaperRepository.existsById(id))
            userPaperRepository.deleteById(id);
        return userPaperMapper.deleteById(id);
    }
    //删
    public int deleteByUserName(String userName)
    {
        if(!userPaperRepository.findByUserName(userName).isEmpty()) {
            userPaperRepository.deleteByUserName(userName);
        }
        return userPaperMapper.deleteByUserName(userName);
    }
    //修改
    public int updateById(EsUserPaper userPaper)
    {
        int status;
        status=userPaperMapper.updateUserPaper(userPaper);
        if(status>0)
        {
            EsQuestionLib questionLib=null;
            if(questionLibRepository.findById((long)userPaper.getQuestionLibId()).isPresent()) {
                questionLib=questionLibRepository.findById((long) userPaper.getQuestionLibId()).get();
                userPaper.setQuestionLibName(questionLib.getQuestionLibName());
                userPaperRepository.save(userPaper);
            }
        }
        return status;
    }
    //增加
    public int creatUserPaper(EsUserPaper userPaper)
    {
        int status;
        status=userPaperMapper.insertUserPaper(userPaper);
        if(status>0)
        {
            EsQuestionLib questionLib=null;
            if(questionLibRepository.findById((long)userPaper.getQuestionLibId()).isPresent()) {
                questionLib=questionLibRepository.findById((long) userPaper.getQuestionLibId()).get();
                userPaper.setQuestionLibName(questionLib.getQuestionLibName());
                userPaperRepository.save(userPaper);
            }
        }
        return status;
    }
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }
}
