package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsQuestionService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsQuestionController
{
    private final EsQuestionService questionService;

    public EsQuestionController(EsQuestionService questionService) {
        this.questionService = questionService;
    }
    @AdminLoginToken
    @PostMapping("admin/question/fuzzyQuery")
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
    @AdminLoginToken
    @PostMapping("admin/question/conditionQuery")
    public Result conditionQuery(@RequestParam("queryString")String queryString,
                                 @RequestParam("pagenum") int pagenum,@RequestParam("pagesize") int pagesize
            ,@RequestParam("questionTypeId") int questionTypeId
    ,@RequestParam("questionLevelBegin")int questionLevelBegin,@RequestParam("questionLevelEnd")int questionLevelEnd)
    {
        List<EsQuestion> esQuestionList=questionService.boolQuestion(queryString,
                pagenum,pagesize,questionTypeId,questionLevelBegin,questionLevelEnd);
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
    @AdminLoginToken
    @PostMapping("admin/question/findByQuestionLibId")
    public Result conditionQuery(@RequestParam("queryString")String queryString
            ,@RequestParam("pagenum") int pagenum,@RequestParam("pagesize") int pagesize,@RequestParam("questionLibId") int questionLibId)
    {
        JSONObject data=questionService.findByQuestionLibId(queryString,pagenum,pagesize,questionLibId);
        return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @GetMapping("admin/question/count")
    public Result count()
    {
        JSONObject data=questionService.staticQuestionLevel();
        return Result.SUCCESS(data);
    }



    @AdminLoginToken
    @GetMapping("admin/question/deleteById/{questionId}")
    public Result deleteById(@PathVariable("questionId")long id)
    {
        int status=questionService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @PostMapping("admin/question/create")
    public Result create(@RequestBody EsQuestion question)
    {
        if(question.getQuestionContent()==null)
            question.setQuestionContent("");
        if(question.getCreateTime()==null) question.setCreateTime(new Date());
        question.setLastModify(new Date());
        if(question.getKeyword()==null) question.setKeyword("");
        int status=questionService.creatQuestion(question);
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
    @PostMapping("admin/question/updateById")
    public Result updateById(@RequestBody EsQuestion question)
    {
        if(question.getQuestionContent()==null)
        question.setQuestionContent("");
        if(question.getCreateTime()==null) question.setCreateTime(new Date());
        question.setLastModify(new Date());
        if(question.getKeyword()==null) question.setKeyword("");
        int status=questionService.updateById(question);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
}
