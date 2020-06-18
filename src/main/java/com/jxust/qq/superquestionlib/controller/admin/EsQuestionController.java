package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class EsQuestionController
{
    private final EsQuestionService questionService;

    public EsQuestionController(EsQuestionService questionService) {
        this.questionService = questionService;
    }
    @PostMapping("/admin/question/fuzzyQuery")
    public Result fuzzyQuery(@RequestParam("queryString") String queryString, @RequestParam("pagenum") int pagenum, @RequestParam("pagesize") int pagesize)
    {
        List<EsQuestion> esQuestionList=questionService.matchQuestion(queryString,pagenum,pagesize);
        JSONObject data=new JSONObject();
        data.put("esQuestions",esQuestionList);
        data.put("pagesum",questionService.pagesum);
        if(!esQuestionList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //条件查找
    @PostMapping("admin/question/conditionQuery")
    public Result conditionQuery(@Param("pagenum") int pagenum,@Param("pagesize") int pagesize,@Param("questionTypeId") int questionTypeId)
    {
        List<EsQuestion> esQuestionList=questionService.boolQuestion(pagenum,pagesize,questionTypeId);
        JSONObject data=new JSONObject();
        data.put("esQuestions",esQuestionList);
        data.put("pagesum", EsHotExamService.pagesum);
        if(!esQuestionList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //题库检索
    @PostMapping("admin/question/findByQuestionLibId")
    public Result conditionQuery(@Param("queryString")String queryString
            ,@Param("pagenum") int pagenum,@Param("pagesize") int pagesize,@Param("questionTypeId") int questionLibId)
    {
        JSONObject data=questionService.findByQuestionLibId(queryString,pagenum,pagesize,questionLibId);
        return Result.SUCCESS(data);
    }
}
