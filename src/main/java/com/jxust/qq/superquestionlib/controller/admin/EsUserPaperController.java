package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import com.jxust.qq.superquestionlib.service.admin.EsQuestionLibService;
import com.jxust.qq.superquestionlib.service.admin.EsUserPaperService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @AdminLoginToken
    @PostMapping("admin/userPaper/fuzzyQuery")
    public Result fuzzyQuery(@RequestParam("queryString") String queryString, @RequestParam("pagenum") int pagenum, @RequestParam("pagesize") int pagesize)
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
    @AdminLoginToken
    @PostMapping("admin/userPaper/findById")
    public Result findById(@RequestParam("id")int id)
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
    @AdminLoginToken
    @PostMapping("admin/userPaper/conditionQuery")
    public Result conditionQuery(@RequestParam("queryString") String queryString,@RequestParam("pagenum") int pagenum,
                                 @RequestParam("pagesize") int pagesize, @RequestParam("limitTimeBegin") int limitTimeBegin
            ,@RequestParam("limitTimeEnd") int limitTimeEnd)
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
    @AdminLoginToken
    @GetMapping("admin/userPaper/deleteById/{id}")
    public Result deleteById(@PathVariable("id")int id)
    {
        int status=userPaperService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @GetMapping("admin/userPaper/deleteByUserName/{userName}")
    public Result deleteByUserName(@PathVariable("userName")String userName)
    {
        int status=userPaperService.deleteByUserName(userName);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @PostMapping("admin/userPaper/create")
    public Result create(@RequestBody EsUserPaper userPaper)
    {
        if(userPaper.getUserName()==null) userPaper.setUserName("");
        if(userPaper.getStartTime()==null)
            userPaper.setStartTime(new Date());
        if(userPaper.getEndTime()==null)
            userPaper.setEndTime(new Date());
        if(userPaper.getPaperResult()==null)
            userPaper.setPaperResult("");
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
    @AdminLoginToken
    @PostMapping("admin/userPaper/updateById")
    public Result updateById(@RequestBody EsUserPaper userPaper)
    {
        if(userPaper.getUserName()==null) userPaper.setUserName("");
        if(userPaper.getStartTime()==null)
        userPaper.setStartTime(new Date());
        if(userPaper.getEndTime()==null)
        userPaper.setEndTime(new Date());
        if(userPaper.getPaperResult()==null)
        userPaper.setPaperResult("");
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
