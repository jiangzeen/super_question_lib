package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsUserQuestionLib;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsUserQuestionLibService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsUserQuestionLibController
{
    private final EsUserQuestionLibService userQuestionLibService;

    public EsUserQuestionLibController(EsUserQuestionLibService userQuestionLibService) {
        this.userQuestionLibService = userQuestionLibService;
    }

    @AdminLoginToken
    @PostMapping("admin/userQuestionLib/findByUserName")
    public Result fuzzyQuery(@RequestParam("queryString")String queryString
            ,@RequestParam("userName") String userName, @RequestParam("pagenum") int pagenum,
                             @RequestParam("pagesize") int pagesize,@RequestParam("dir")boolean dir)
    {
        List<EsUserQuestionLib> esUserQuestionList=userQuestionLibService.boolQuestion(queryString,pagenum,pagesize,userName,dir);
        JSONObject data=new JSONObject();
        data.put("userQuestionList",esUserQuestionList);
        data.put("pagesum",userQuestionLibService.pagesum);
        if(!esUserQuestionList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //删除
    @AdminLoginToken
    @GetMapping("admin/userQuestionLib/deleteById/{id}")
    public Result deleteById(@PathVariable("id")long id)
    {
        int status=userQuestionLibService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    //修改
    @AdminLoginToken
    @PostMapping("admin/userQuestionLib/updateById")
    public Result updateById(@RequestBody EsUserQuestionLib userQuestionLib)
    {

        int status=userQuestionLibService.updateById(userQuestionLib);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
}
