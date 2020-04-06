package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.EsUserTasksMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionLibRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsUserTasksRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
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
    @Autowired
    EsQuestionLibRepository questionLibRepository;
    private final EsUserTasksMapper userTasksMapper;
    static  public int pagesum;

    public EsUserTasksService(EsUserTasksMapper userTasksMapper) {
        this.userTasksMapper = userTasksMapper;
    }

    //进行模糊匹配查找
    public List<EsUserTasks> matchTasks(String queryString, int pagenum, int pagesize)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        if(!queryString.isEmpty()) {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryString, "mark", "questionLibName"
                    , "userName");
            boolQuery.filter(matchQuery);
        }
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsUserTasks> users=userTasksRepository.search(boolQuery,pageRequest);
        pagesum= (int) userTasksRepository.search(boolQuery,pageRequest).getTotalElements();
        List<EsUserTasks> userTasksList=new ArrayList<>();
        for (EsUserTasks tasks: users)
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
    public ArrayList<EsUserTasks> boolTasks(String queryStrings, int pagenum, int pagesize, int questionNumBegin
            ,int questionNumEnd
    ,int expired,int hasCompleteNumbers,boolean dir)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        //按字符查询
        if(!queryStrings.isEmpty()) {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryStrings, "userName", "mark","questionLibName");
            boolQuery.must(matchQuery);
        }
        if(hasCompleteNumbers>=0) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("hasCompleteNumbers");
            boolQuery.must(rangeQueryBuilder);
        }
        if(expired!=0&&expired!=1)
        {
            TermQueryBuilder termQueryBuilder=QueryBuilders.termQuery("expired",expired);
            boolQuery.filter(termQueryBuilder);
        }
        //过滤条件
        RangeQueryBuilder numQuery=QueryBuilders.rangeQuery("questionNumbers").gte(questionNumBegin).lte(questionNumEnd);

        boolQuery.filter(numQuery);
        Sort sort;
        if(dir==true)
        sort=Sort.by(Sort.Direction.DESC,"endTime");
        else
            sort=Sort.by(Sort.Direction.ASC,"endTime");
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
    //删
    public int deleteById(int id)
    {
        if(userTasksRepository.existsById(id))
            userTasksRepository.deleteById(id);
        return userTasksMapper.deleteById(id);
    }
    //删
    public int deleteByUserName(String userName)
    {
        if(!userTasksRepository.findByUserName(userName).isEmpty()) {
            userTasksRepository.deleteByUserName(userName);
        }
        return userTasksMapper.deleteByUserName(userName);
    }
    //修改
    public int updateUserTasks(EsUserTasks userTasks)
    {
        int status= userTasksMapper.updateUserTasks(userTasks);
        if(status>0) {
            EsQuestionLib questionLib=null;
            if(questionLibRepository.findById((long)userTasks.getQuestionLibId()).isPresent()) {
                questionLib=questionLibRepository.findById((long)userTasks.getQuestionLibId()).get();
                userTasks.setQuestionLibName(questionLib.getQuestionLibName());
            }
            userTasksRepository.save(userTasks);
        }
        return status;
    }
    //增加
    public int creatUserTasks(EsUserTasks userTasks)
    {
        int status;
        status=userTasksMapper.createUserTasks(userTasks);
        if(status>0)
        {
            EsQuestionLib questionLib=null;
            if(questionLibRepository.findById((long)userTasks.getQuestionLibId()).isPresent()) {
                questionLib=questionLibRepository.findById((long)userTasks.getQuestionLibId()).get();
                userTasks.setQuestionLibName(questionLib.getQuestionLibName());
            }
            userTasksRepository.save(userTasks);
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
