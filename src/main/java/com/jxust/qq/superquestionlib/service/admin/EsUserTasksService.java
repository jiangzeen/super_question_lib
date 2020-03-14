package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsUserTasksRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
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
public class EsUserTasksService
{
    @Autowired
    EsUserTasksRepository userTasksRepository;
    static  public int pagesum;
    //进行模糊匹配查找
    public List<EsUserTasks> matchTasks(String queryString, int pagenum, int pagesize)
    {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,"mark","questionLibName"
        ,"userName");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsUserTasks> users=userTasksRepository.search(matchQuery,pageRequest);
        pagesum= (int) userTasksRepository.search(matchQuery,pageRequest).getTotalElements();
        List<EsUserTasks> userTasksList=new ArrayList<>();
        for (EsUserTasks tasks: userTasksList)
        {
            userTasksList.add(tasks);
        }
        return userTasksList;
    }
    //根据id完全匹配查找
    public EsUserTasks termTask(int id)
    {
        EsUserTasks userTasks=null;
        if(userTasksRepository.findById(id).isPresent())
        {
            userTasks=userTasksRepository.findById(id).get();
            pagesum=1;
        }
        else pagesum=0;
        return userTasks;
    }
    //true 降序 false 升序
    public ArrayList<EsUserTasks> boolTasks(String queryStrings, boolean dir, int pagenum, int pagesize, int questionNumBegin
            ,int questionNumEnd)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        //按字符查询

        MultiMatchQueryBuilder matchQuery=QueryBuilders.multiMatchQuery(queryStrings,"userName","mark");
        boolQuery.must(matchQuery);
        //过滤条件
        RangeQueryBuilder numQuery=QueryBuilders.rangeQuery("questionNumbers").gte(questionNumBegin).lte(questionNumEnd);

        boolQuery.filter(numQuery);
        Sort sort;
        if(dir)
            sort=Sort.by(Sort.Direction.DESC,"creatTime");
        else
            sort=Sort.by(Sort.Direction.ASC,"creatTime");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum= (int) userTasksRepository.search(boolQuery,pageRequest).getTotalElements();
        Iterable<EsUserTasks> iterables=userTasksRepository.search(boolQuery,pageRequest);
        Iterator<EsUserTasks> iterator=iterables.iterator();
        ArrayList<EsUserTasks> userstasks=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsUserTasks userTasks=iterator.next();
            userstasks.add(userTasks);
        }
        return userstasks;
    }
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }
}
