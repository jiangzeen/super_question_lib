package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsQuestionService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("admin/question/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString, @Param("pagenum") int pagenum, @Param("pagesize") int pagesize)
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
    public Result conditionQuery(@Param("queryString")String queryString,
                                 @Param("pagenum") int pagenum,@Param("pagesize") int pagesize
            ,@Param("questionTypeId") int questionTypeId
    ,@Param("questionLevelBegin")int questionLevelBegin,@Param("questionLevelEnd")int questionLevelEnd)
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
    @PostMapping("admin/question/findByQuestionLibId")
    public Result conditionQuery(@Param("queryString")String queryString
            ,@Param("pagenum") int pagenum,@Param("pagesize") int pagesize,@Param("questionLibId") int questionLibId)
    {
        JSONObject data=questionService.findByQuestionLibId(queryString,pagenum,pagesize,questionLibId);
        return Result.SUCCESS(data);
    }
    @PostMapping("admin/question/count")
    public Result count()
    {
        JSONObject data=questionService.staticQuestionLevel();
        return Result.SUCCESS(data);
    }




    @PostMapping("admin/question/deleteById")
    public Result deleteById(@Param("questionId")long id)
    {
        int status=questionService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @PostMapping("admin/question/create")
    public Result create(@Param("questionContent")String  questionContent,
                         @Param("questionLevel")String questionLevel,
                         @Param("questionLibId")String questionLibId,
                         @Param("questionTypeId")String  questionTypeId,
                         @Param("rightTime")String rightTime,
                         @Param("wrongTime")String wrongTime,
                         @Param("createTime")String createTime,
                         @Param("keyword")String keyword)
    {
        EsQuestion question=new EsQuestion();
        if(questionContent==null) questionContent="";
        question.setQuestionContent(questionContent);
        if(questionLevel==null) questionLevel="0";
        question.setQuestionLevel(Integer.parseInt(questionLevel));
        if(questionLibId==null) questionLibId="0";
        question.setQuestionLibId(Integer.parseInt(questionLibId));
        if(questionTypeId==null) questionTypeId="0";
        question.setQuestionTypeId(Integer.parseInt(questionTypeId));
        if(rightTime==null) rightTime="0";
        question.setRightTime(Integer.parseInt(rightTime));
        if(wrongTime==null) wrongTime="0";
        question.setWrongTime(Integer.parseInt(wrongTime));
        if(createTime==null) question.setCreateTime(new Date());
        else question.setCreateTime(DateFormat.DateFormatParse(createTime));
        question.setLastModify(new Date());
        if(keyword==null) keyword="";
        question.setKeyword(keyword);
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
    @PostMapping("admin/question/updateById")
    public Result updateById(@Param("questionId")long questionId,@Param("questionContent")String  questionContent,
                             @Param("questionLevel")String questionLevel,
                             @Param("questionLibId")String questionLibId,
                             @Param("questionTypeId")String  questionTypeId,
                             @Param("rightTime")String rightTime,
                             @Param("wrongTime")String wrongTime,
                             @Param("createTime")String createTime,
                             @Param("keyword")String keyword)
    {
        EsQuestion question=new EsQuestion();
        question.setQuestionId(questionId);
        if(questionContent==null) questionContent="";
        question.setQuestionContent(questionContent);
        if(questionLevel==null) questionLevel="0";
        question.setQuestionLevel(Integer.parseInt(questionLevel));
        if(questionLibId==null) questionLibId="0";
        question.setQuestionLibId(Integer.parseInt(questionLibId));
        if(questionTypeId==null) questionTypeId="0";
        question.setQuestionTypeId(Integer.parseInt(questionTypeId));
        if(rightTime==null) rightTime="0";
        question.setRightTime(Integer.parseInt(rightTime));
        if(wrongTime==null) wrongTime="0";
        question.setWrongTime(Integer.parseInt(wrongTime));
        if(createTime==null) question.setCreateTime(new Date());
        else question.setCreateTime(DateFormat.DateFormatParse(createTime));
        question.setLastModify(new Date());
        if(keyword==null) keyword="";
        question.setKeyword(keyword);
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
