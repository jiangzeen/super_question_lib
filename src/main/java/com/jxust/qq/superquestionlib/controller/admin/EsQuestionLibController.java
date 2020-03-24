package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.QuestionLib;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import com.jxust.qq.superquestionlib.dto.admin.EsUserPaper;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsQuestionLibService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsQuestionLibController
{
   private final EsQuestionLibService libService;
    public EsQuestionLibController(EsQuestionLibService libService) {
        this.libService = libService;
    }
    @PostMapping("admin/questionLib/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString, @Param("pagenum") int pagenum, @Param("pagesize") int pagesize)
    {
        List<EsQuestionLib> esQuestionLibList=libService.matchQuestionLib(queryString,pagenum,pagesize);
        JSONObject data=new JSONObject();
        data.put("esQuestionLibs",esQuestionLibList);
        data.put("pagesum",libService.pagesum);
        if(!esQuestionLibList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @PostMapping("admin/questionLib/findById")
    public Result findById(@Param("id")int id)
    {
        EsQuestionLib questionLib=null;
        questionLib=libService.termQuestionLib(id);
        JSONObject data=new JSONObject();
        data.put("esQuestionLib",questionLib);
        data.put("pagesum", EsHotExamService.pagesum);
        if(questionLib!=null)
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @PostMapping("admin/questionLib/conditionQuery")
    public Result conditionQuery(@Param("queryString") String queryString,
                                 @Param("pagenum") int pagenum,@Param("pagesize") int pagesize
                                 ,@Param("hasPrivate")int hasPrivate,@Param("questionLibLevelBegin") int questionLibLevelBegin
            ,@Param("questionLibLevelEnd") int questionLibLevelEnd,@Param("ligTagId") int libTagId)
    {
        List<EsQuestionLib> esQuestionLibList=libService.boolQuestionLib(queryString,pagenum,
                pagesize,hasPrivate,questionLibLevelBegin,questionLibLevelEnd,libTagId);
        JSONObject data=new JSONObject();
        data.put("esQuestionLibs",esQuestionLibList);
        data.put("pagesum",EsHotExamService.pagesum);
        if(!esQuestionLibList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //查询


    @PostMapping("admin/questionLib/deleteById")
    public Result deleteById(@Param("id")long id)
    {
        int status=libService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @PostMapping("admin/questionLib/create")
    public Result create(@Param("questionLibLevel")int questionLibLevel,
                         @Param("questionLibName")String questionLibName,
                         @Param("questionLibPrivate")int questionLibPrivate,
                         @Param("questionLibCreate") String questionLibCreate,
                         @Param("questionLibTagId")int questionLibTagId,
                         @Param("questionLibMark")String questionLibMark,
                         @Param("questionLibUrl")String questionLibUrl)
    {
        EsQuestionLib questionLib=new EsQuestionLib();
        questionLib.setQuestionLibLevel(questionLibLevel);
        if(questionLibName==null) questionLibName="";
        questionLib.setQuestionLibName(questionLibName);
        questionLib.setHasPrivate(questionLibPrivate);
        if(questionLibCreate==null) questionLib.setQuestionLibCreateTime(new Date());
        else questionLib.setQuestionLibCreateTime(DateFormat.DateFormatParse(questionLibCreate));
        questionLib.setQuestionLibTagId(questionLibTagId);
        if(questionLibMark==null) questionLibMark="";
        questionLib.setQuestionLibMark(questionLibMark);
        if(questionLibUrl==null) questionLibUrl="";
        questionLib.setQuestionLibUrl(questionLibUrl);
        int status=libService.creatQuestionLib(questionLib);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    //修改
    @PostMapping("admin/questionLib/updateById")
    public Result updateById(@Param("id")long id,@Param("questionLibLevel")int questionLibLevel,
                             @Param("questionLibName")String questionLibName,
                             @Param("questionLibPrivate")int questionLibPrivate,
                             @Param("questionLibCreate") String questionLibCreate,
                             @Param("questionLibTagId")int questionLibTagId,
                             @Param("questionLibMark")String questionLibMark,
                             @Param("questionLibUrl")String questionLibUrl)
    {
        EsQuestionLib questionLib=new EsQuestionLib();
        questionLib.setQuestionLibId(id);
        questionLib.setQuestionLibLevel(questionLibLevel);
        if(questionLibName==null) questionLibName="";
        questionLib.setQuestionLibName(questionLibName);
        questionLib.setHasPrivate(questionLibPrivate);
        if(questionLibCreate==null) questionLib.setQuestionLibCreateTime(new Date());
        else questionLib.setQuestionLibCreateTime(DateFormat.DateFormatParse(questionLibCreate));
        questionLib.setQuestionLibTagId(questionLibTagId);
        if(questionLibMark==null) questionLibMark="";
        questionLib.setQuestionLibMark(questionLibMark);
        if(questionLibUrl==null) questionLibUrl="";
        questionLib.setQuestionLibUrl(questionLibUrl);
        int status=libService.updateById(questionLib);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }

}
