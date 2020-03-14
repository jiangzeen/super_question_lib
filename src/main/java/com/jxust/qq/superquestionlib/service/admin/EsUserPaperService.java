package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsUserPaperRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsUser;
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
    public  static int pagesum;
    //进行模糊匹配查找
    public List<EsUserPaper> matchUserPaper(String queryString, int pagenum, int pagesize)
    {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,"userName"
                ,"result","questionLibName");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsUserPaper> users=userPaperRepository.search(matchQuery,pageRequest);
        pagesum= (int) userPaperRepository.search(matchQuery,pageRequest).getTotalElements();
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
    //true 降序 false 升序
    public ArrayList<EsUserPaper> boolUserPaper(String queryString,int pagenum, int pagesize, int limitTimeBegin,int limitTimeEnd)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        //按字符查询
        if(queryString!="") {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryString, "userName"
                    , "result", "questionLibName");
            boolQuery.must(matchQuery);
        }
        //过滤条件
        if(limitTimeEnd!=0) {
            RangeQueryBuilder limitTimeQuery = QueryBuilders.rangeQuery("limitTime").from(limitTimeBegin).to(limitTimeEnd);
            boolQuery.filter(limitTimeQuery);
        }
        //分页
        Sort sort;
        sort=Sort.by(Sort.Direction.DESC,"limitTime");
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
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }
}
