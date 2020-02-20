package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsUserPaperService;
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

    public EsUserPaperController(EsUserPaperService userPaperService) {
        this.userPaperService = userPaperService;
    }


    @PostMapping("/admin/userPaper/fuzzyQuery")
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
    @PostMapping("/admin/userPaper/findById")
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
}
