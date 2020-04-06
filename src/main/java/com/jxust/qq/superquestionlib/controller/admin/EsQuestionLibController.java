package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsLibTagService;
import com.jxust.qq.superquestionlib.service.admin.EsQuestionLibService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsQuestionLibController
{
   private final EsQuestionLibService libService;
   private final EsLibTagService tagService;
    public EsQuestionLibController(EsQuestionLibService libService, EsLibTagService tagService) {
        this.libService = libService;
        this.tagService = tagService;
    }
    @AdminLoginToken
    @PostMapping("admin/questionLib/fuzzyQuery")
    public Result fuzzyQuery(@RequestParam("queryString") String queryString, @RequestParam("pagenum") int pagenum, @RequestParam("pagesize") int pagesize)
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
    @AdminLoginToken
    @GetMapping("admin/questionLib/findById/{id}")
    public Result findById(@PathVariable("id")int id)
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

    @AdminLoginToken
    @PostMapping("admin/questionLib/conditionQuery")
    public Result conditionQuery(@RequestParam("queryString") String queryString,
                                 @RequestParam("pagenum") int pagenum,@RequestParam("pagesize") int pagesize
                                 ,@RequestParam("hasPrivate")int hasPrivate,@RequestParam("questionLibLevelBegin") int questionLibLevelBegin
            ,@RequestParam("questionLibLevelEnd") int questionLibLevelEnd,@RequestParam("ligTagId") int libTagId)
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
    //删除
    @AdminLoginToken
    @GetMapping("admin/questionLib/deleteById/{id}")
    public Result deleteById(@PathVariable("id")long id)
    {
        int status=libService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @PostMapping("admin/questionLib/create")
    public Result create(@RequestBody EsQuestionLib questionLib)
    {
        if(questionLib.getQuestionLibName()==null) questionLib.setQuestionLibName("");
        if(questionLib.getQuestionLibCreateTime()==null) questionLib.setQuestionLibCreateTime(new Date());
        if(questionLib.getQuestionLibMark()==null)
            questionLib.setQuestionLibMark("");
        if(questionLib.getQuestionLibUrl()==null) questionLib.setQuestionLibUrl("");
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
    @AdminLoginToken
    @PostMapping("admin/questionLib/updateById")
    public Result updateById(@RequestBody EsQuestionLib questionLib)
    {

        if(questionLib.getQuestionLibName()==null) questionLib.setQuestionLibName("");
        if(questionLib.getQuestionLibCreateTime()==null) questionLib.setQuestionLibCreateTime(new Date());
        if(questionLib.getQuestionLibMark()==null)
        questionLib.setQuestionLibMark("");
        if(questionLib.getQuestionLibUrl()==null) questionLib.setQuestionLibUrl("");
        int status=libService.updateById(questionLib);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    @AdminLoginToken
    @GetMapping("/admin/questionLib/tags")
    public Result getTags() {
        JSONObject data = tagService.getAllTagsWithChildrenTag();
        return Result.SUCCESS(data);
    }
}
