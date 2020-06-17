package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionHistory;
import com.jxust.qq.superquestionlib.service.admin.EsUserQuestionHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsUserQuestionHistoryController
{
    private final EsUserQuestionHistoryService esUserQuestionHistoryService;

    public EsUserQuestionHistoryController(EsUserQuestionHistoryService esUserQuestionHistoryService) {
        this.esUserQuestionHistoryService = esUserQuestionHistoryService;
    }

    @AdminLoginToken
    @GetMapping("admin/history/deleteById/{id}")
    public Result deleteById(@PathVariable("id")long id)
    {
        int status=esUserQuestionHistoryService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    //修改
    @AdminLoginToken
    @PostMapping("admin/question/findAll")
    public Result selectAll(@RequestParam("userName")String userName,
                            @RequestParam("pagenum")int pagenum,
                            @RequestParam("pagesize")int pagesize)
    {
        ArrayList<EsUserQuestionHistory> histories=esUserQuestionHistoryService.findAll
                (userName,pagenum,pagesize);
        System.out.println(userName);
        JSONObject data=new JSONObject();
        data.put("histories",histories);
        if (histories.isEmpty()) {
            data.put("pagesum",0);
            return Result.FAILD(data);
        }
        else{
            data.put("pagesum",EsUserQuestionHistoryService.pagesum);
            return Result.SUCCESS(data);
        }
    }
}
