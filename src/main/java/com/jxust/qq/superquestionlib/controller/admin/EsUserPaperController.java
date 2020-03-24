package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import com.jxust.qq.superquestionlib.service.admin.EsQuestionLibService;
import com.jxust.qq.superquestionlib.service.admin.EsUserPaperService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
public class EsUserPaperController
{
    private final EsUserPaperService userPaperService;
    private final EsQuestionLibService questionLibService;
    public EsUserPaperController(EsUserPaperService userPaperService, EsQuestionLibService questionLibService) {
        this.userPaperService = userPaperService;
        this.questionLibService = questionLibService;
    }


    @PostMapping("admin/userPaper/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString, @Param("pagenum") int pagenum, @Param("pagesize") int pagesize)
    {
        List<EsUserPaper> esUserPaperList=userPaperService.matchUserPaper(queryString,pagenum,pagesize);
        JSONObject data=new JSONObject();
        data.put("esUserPaperList",esUserPaperList);
        data.put("pagesum",userPaperService.pagesum);
        if(!esUserPaperList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @PostMapping("admin/userPaper/findById")
    public Result findById(@Param("id")int id)
    {
        EsUserPaper esUserPaper=null;
        esUserPaper=userPaperService.termUser(id);
        JSONObject data=new JSONObject();
        data.put("esUserPaper",esUserPaper);
        data.put("pagesum",userPaperService.pagesum);
        if(esUserPaper!=null)
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //条件查找
    @PostMapping("admin/userPaper/conditionQuery")
    public Result conditionQuery(@Param("queryString") String queryString,@Param("pagenum") int pagenum,
                                 @Param("pagesize") int pagesize, @Param("limitTimeBegin") int limitTimeBegin
            ,@Param("limitTimeEnd") int limitTimeEnd)
    {
        List<EsUserPaper> esUserPaperList=userPaperService.boolUserPaper(queryString,pagenum,
                pagesize,
                limitTimeBegin,limitTimeEnd);
        JSONObject data=new JSONObject();
        data.put("esUserPaperList",esUserPaperList);
        data.put("pagesum", EsUserPaperService.pagesum);
        if(!esUserPaperList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @PostMapping("admin/userPaper/deleteById")
    public Result deleteById(@Param("id")int id)
    {
        int status=userPaperService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @PostMapping("admin/userPaper/deleteByUserName")
    public Result deleteByUserName(@Param("userName")String userName)
    {
        int status=userPaperService.deleteByUserName(userName);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @PostMapping("admin/userPaper/create")
    public Result create(@Param("userName")String userName,
                             @Param("questionLibId")int questionLibId,
                             @Param("limitTime")int limitTime,
                             @Param("startTime") String startTime,
                             @Param("endTime")String endTime,@Param("paperResult")String paperResult)
    {
        EsUserPaper userPaper=new EsUserPaper();
        if(userName==null) userName="";
        userPaper.setUserName(userName);
        userPaper.setQuestionLibId(questionLibId);
        userPaper.setLimitTime(limitTime);
        userPaper.setStartTime(DateFormat.DateFormatParse(startTime));
        userPaper.setEndTime(DateFormat.DateFormatParse(endTime));
        userPaper.setPaperResult(paperResult);
        int status=userPaperService.creatUserPaper(userPaper);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    //修改
    @PostMapping("admin/userPaper/updateById")
    public Result updateById(@Param("id")int id,@Param("userName")String userName,
                             @Param("questionLibId")int questionLibId,
                             @Param("limitTime")int limitTime,
                             @Param("startTime") String startTime,
                             @Param("endTime")String endTime,@Param("paperResult")String paperResult)
    {
        EsUserPaper userPaper=new EsUserPaper();
        userPaper.setId(id);
        if(userName==null) userName="";
        userPaper.setUserName(userName);
        userPaper.setQuestionLibId(questionLibId);
        userPaper.setLimitTime(limitTime);
        userPaper.setStartTime(DateFormat.DateFormatParse(startTime));
        userPaper.setEndTime(DateFormat.DateFormatParse(endTime));
        userPaper.setPaperResult(paperResult);
        int status=userPaperService.updateById(userPaper);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }

}
