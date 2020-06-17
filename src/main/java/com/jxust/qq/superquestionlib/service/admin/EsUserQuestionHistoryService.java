package com.jxust.qq.superquestionlib.service.admin;

import com.jxust.qq.superquestionlib.dao.mapper.admin.EsUserQuestionHistoryMapper;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class EsUserQuestionHistoryService
{

    private final EsUserQuestionHistoryMapper userQuestionHistoryMapper;
    public static int pagesum;

    public EsUserQuestionHistoryService(EsUserQuestionHistoryMapper userQuestionHistoryMapper) {
        this.userQuestionHistoryMapper = userQuestionHistoryMapper;
    }

    public int deleteById(long id)
    {
        return userQuestionHistoryMapper.deleteByHistoryId(id);
    }
    public ArrayList<EsUserQuestionHistory> findAll(String userName,int pagenum,int pagesize)
    {
        pagesum=userQuestionHistoryMapper.counts(userName);
        ArrayList<EsUserQuestionHistory> userQuestionHistories=userQuestionHistoryMapper.selectHistoriesByUsername
                (userName,pagenum-1,pagesize);
        return userQuestionHistories;
    }
}
