package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsLibTagService;
import com.jxust.qq.superquestionlib.service.admin.EsQuestionLibService;
import com.jxust.qq.superquestionlib.service.admin.EsUserQuestionLibService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsQuestionLibController
{
   private final EsQuestionLibService libService;
   private final EsLibTagService tagService;
   private final EsUserQuestionLibService userQuestionLibService;
    public EsQuestionLibController(EsQuestionLibService libService, EsLibTagService tagService, EsUserQuestionLibService userQuestionLibService) {
        this.libService = libService;
        this.tagService = tagService;
        this.userQuestionLibService = userQuestionLibService;
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
    @GetMapping("admin/questionLib/count")
    public Result count()
    {
        JSONObject data=new JSONObject();
        data.put("count",libService.count());
        return Result.SUCCESS(data);
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
            ,@RequestParam("questionLibLevelEnd") int questionLibLevelEnd,@RequestParam("questionLibTagId") int libTagId)
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
    @PostMapping("/admin/questionLib/uploadLib")
    public Result createLib(@RequestParam("username") String username, @RequestParam("question_file") MultipartFile file,
                            @RequestParam("tag_id") String tagId, @RequestParam("mark") String mark,
                            @RequestParam("hasPrivate") String hasPrivate, @RequestParam("privateName") String privateName) {
        String originName = file.getOriginalFilename();
        try {
            String saveUrl = libService.saveOriginLibFile(file, username);
            // TODO 开启事务处理,防止如果解析失败等情况出现
            int libId = libService.createQuestionLibByUser(username, originName, Integer.parseInt(tagId), saveUrl, mark, Integer.parseInt(hasPrivate));
            userQuestionLibService.saveUserLibRecord(username, libId, privateName);
            // 获取到原始上传文件在服务器中的位置 -- example: english.docx
            String fileUrl = libService.getFileUrl(saveUrl);
            try {
                libService.createQuestionByLibFile(fileUrl, libId);
                JSONObject data=new JSONObject(); data.put("questionLibId",libId);
                return Result.SUCCESS(data);
            } catch (IOException e) {
                Result res = Result.FAILD("");
                res.setMessage("解析文件失败");
                return res;
            }
        } catch (NullPointerException e) {
            Result res = Result.FAILD(null);
            res.setMessage("操作失败,上传文件不能为null");
            return res;
        } catch (IllegalArgumentException e) {
            Result res = Result.FAILD(null);
            res.setMessage("操作失败,上传文件必须为doc,docx或pdf文件");
            return res;
        }
    }
    @AdminLoginToken
    @GetMapping("/admin/questionLib/tags")
    public Result getTags() {
        JSONObject data = tagService.getAllTagsWithChildrenTag();
        return Result.SUCCESS(data);
    }
}
