package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.UserQuestionLibMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.EsUserQuestionLibMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionLibRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsUserQuestionLibRepository;
import com.jxust.qq.superquestionlib.dto.UserQuestionLib;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionLib;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class EsUserQuestionLibService
{
    @Autowired
    EsUserQuestionLibRepository userQuestionLibRepository;
    @Autowired
    EsQuestionLibRepository questionLibRepository;
    private final UserQuestionLibMapper userQuestionLibMapper;
    private final EsUserQuestionLibMapper esUserQuestionLibMapper;
    public static int pagesum;

    public EsUserQuestionLibService(UserQuestionLibMapper userQuestionLibMapper, EsUserQuestionLibMapper esUserQuestionLibMapper) {
        this.userQuestionLibMapper = userQuestionLibMapper;
        this.esUserQuestionLibMapper = esUserQuestionLibMapper;
    }

    public List<EsUserQuestionLib> boolQuestion(String queryString,int pagenum, int pagesize,String userName,boolean dir)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();

        if(!queryString.isEmpty())
        {
            boolQueryBuilder.filter(QueryBuilders.fuzzyQuery("questionLibName","privateName"));
        }

        TermQueryBuilder termQueryBuilder=QueryBuilders.termQuery("userName",userName);
        boolQueryBuilder.filter(termQueryBuilder);
        //分页
        Sort sort;
        if(dir==true)
        sort=Sort.by(Sort.Direction.ASC,"questionLibImportance");
        else
            sort=Sort.by(Sort.Direction.DESC,"questionLibImportance");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum= (int) userQuestionLibRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        Iterable<EsUserQuestionLib> iterables=userQuestionLibRepository.search(boolQueryBuilder,pageRequest);
        Iterator<EsUserQuestionLib> iterator=iterables.iterator();
        ArrayList<EsUserQuestionLib> questions=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsUserQuestionLib question=iterator.next();
            questions.add(question);
        }
        return questions;
    }

    /**
     * 移植方法
     * @param username
     * @param libId
     * @param privateName
     */
    public void saveUserLibRecord(String username, int libId, String privateName) {
        UserQuestionLib userLib = new UserQuestionLib();
        userLib.setUsername(username);
        userLib.setPrivateName(privateName);
        userLib.setQuestionLibId(libId);
        userLib.setQuestionLibImportance(0);
        userQuestionLibMapper.insertUserLib(userLib);
        String questionLibName=questionLibRepository.findById((long) libId).get().getQuestionLibName();
        UserQuestionLib userLib2=userQuestionLibMapper.selectByLibIdAndUsername(username,libId);
        EsUserQuestionLib userQuestionLib=new EsUserQuestionLib(userLib2.getId(),userLib2.getUsername(),
                userLib2.getQuestionLibId(),questionLibName,userLib2.getPrivateName(),userLib2.getQuestionLibImportance());
        userQuestionLibRepository.save(userQuestionLib);
    }
    //删
    public int deleteById(long id)
    {
        if(userQuestionLibRepository.existsById((int) id))
            userQuestionLibRepository.deleteById((int) id);
        return esUserQuestionLibMapper.deleteById(id);
    }
    //修改
    public int updateById(EsUserQuestionLib esUserQuestionLib)
    {
        int status=esUserQuestionLibMapper.updateUserQuestionLib(esUserQuestionLib);
        if(status>0)
        {
                if(questionLibRepository.findById((long)esUserQuestionLib.getQuestionLibId()).isPresent())
                {
                    esUserQuestionLib.setQuestionLibName(questionLibRepository.findById((long)esUserQuestionLib.getQuestionLibId()).get().getQuestionLibName());
                }
                userQuestionLibRepository.save(esUserQuestionLib);
        }
        return status;
    }
}
