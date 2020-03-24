package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.EsQuestionLibMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.UtilMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsLibTagRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionLibRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsLibTag;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Service
@Slf4j
public class EsQuestionLibService
{
    @Autowired
    EsQuestionLibRepository questionLibRepository;
    @Autowired
    EsLibTagRepository libTagRepository;
    private final EsQuestionLibMapper questionLibMapper;
    private final UtilMapper utilMapper;
    public static int pagesum;

    public EsQuestionLibService(EsQuestionLibMapper questionLibMapper, UtilMapper utilMapper) {
        this.questionLibMapper = questionLibMapper;
        this.utilMapper = utilMapper;
    }

    //进行模糊匹配查找
    public List<EsQuestionLib> matchQuestionLib(String queryString, int pagenum, int pagesize)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        if(!queryString.isEmpty())
        {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,
                "questionLibName","questionLibMark");
        boolQueryBuilder.filter(matchQuery);
        }
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsQuestionLib> questionLibs=questionLibRepository.search(boolQueryBuilder,pageRequest);
        pagesum= (int) questionLibRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        List<EsQuestionLib> questionLibList=new ArrayList<>();
        for (EsQuestionLib questionLib : questionLibs) {
            questionLibList.add(questionLib);
        }
        return questionLibList;
    }
    //根据id完全匹配查找
    public EsQuestionLib termQuestionLib(int id)
    {
        EsQuestionLib questionLib=null;
        if(questionLibRepository.findById((long)id).isPresent())
        {
            questionLib=questionLibRepository.findById((long)id).get();
            pagesum=1;
        }
        else pagesum=0;
        return questionLib;
    }
    //hasPrivate为0私有,为1公有,-1不筛选  LibTagId为0不筛选
    public List<EsQuestionLib> boolQuestionLib(String queryString,int pagenum, int pagesize,int hasPrivate,int questionLibLevelBegin,
                                            int questionLibLevelEnd,int LibTagId)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        if(!queryString.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(queryString, "questionLibName", "questionLibMark"));
        }
        if(hasPrivate>=0)
        {
            TermQueryBuilder termQuery=QueryBuilders.termQuery("hasPrivate",hasPrivate);
            boolQueryBuilder.filter(termQuery);
        }
        if(questionLibLevelEnd!=0)
        {
            RangeQueryBuilder rangeQuery=QueryBuilders.rangeQuery("questionLibLevel").gte(questionLibLevelBegin)
                    .lte(questionLibLevelEnd);
            boolQueryBuilder.filter(rangeQuery);
        }
        if(LibTagId!=0) {
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("parentIds", LibTagId);
            boolQueryBuilder.filter(matchQuery);
        }
        //分页
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        pagesum= (int) questionLibRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        Iterable<EsQuestionLib> iterables=questionLibRepository.search(boolQueryBuilder,pageRequest);
        Iterator<EsQuestionLib> iterator=iterables.iterator();
        ArrayList<EsQuestionLib> questionLibs=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsQuestionLib questionLib=iterator.next();
            questionLibs.add(questionLib);
        }
        return questionLibs;
    }
    //删
    public int deleteById(long id)
    {
        if(questionLibRepository.existsById(id))
            questionLibRepository.deleteById(id);
        return questionLibMapper.deleteById(id);
    }
    //修改
    public int updateById(EsQuestionLib questionLib)
    {
        int status=questionLibMapper.updateQuestionLib(questionLib);
        if(status>0)
        {
            if(libTagRepository.findById((long)questionLib.getQuestionLibTagId()).isPresent()) {
                EsLibTag libTag=libTagRepository.findById((long)questionLib.getQuestionLibTagId()).get();
                questionLib.setLibTagInfo(libTag.getTagName());
                questionLib.setParentIds(utilMapper.getTagParent(questionLib.getQuestionLibTagId()));
                questionLibRepository.save(questionLib);
            }
        }
        return status;
    }
    //增加
    public int creatQuestionLib(EsQuestionLib questionLib)
    {
        int status;
        status=questionLibMapper.createQuestionLib(questionLib);
        if(status>0)
        {
            if(libTagRepository.findById((long)questionLib.getQuestionLibTagId()).isPresent()) {
                EsLibTag libTag=libTagRepository.findById((long)questionLib.getQuestionLibTagId()).get();
                questionLib.setLibTagInfo(libTag.getTagName());
                questionLib.setParentIds(utilMapper.getTagParent(questionLib.getQuestionLibTagId()));
                questionLibRepository.save(questionLib);
            }
        }
        return status;
    }
}
