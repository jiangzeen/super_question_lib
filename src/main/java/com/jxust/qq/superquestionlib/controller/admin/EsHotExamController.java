package com.jxust.qq.superquestionlib.controller.admin;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsHotExam;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsHotExamController {

    private final EsHotExamService esHotExamService;
    public EsHotExamController(EsHotExamService esHotExamService) {
        this.esHotExamService = esHotExamService;
    }

    @AdminLoginToken
    @PostMapping("admin/hotExam/fuzzyQuery")
    public Result fuzzyQuery(@RequestParam("queryString") String queryString,@RequestParam("pagenum") int pagenum,
                             @RequestParam("pagesize") int pagesize)
    {
        List<EsHotExam> esHotExamList=esHotExamService.matchHotExam(queryString,pagenum,pagesize);
        JSONObject data=new JSONObject();
        data.put("esHotExams",esHotExamList);
        data.put("pagesum",EsHotExamService.pagesum);
        if(!esHotExamList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @AdminLoginToken
    @PostMapping("admin/hotExam/findById")
    public Result findById(@RequestParam("id")int id)
    {
        EsHotExam esHotExam=null;
        esHotExam=esHotExamService.termHotExam(id);
        JSONObject data=new JSONObject();
        data.put("esHotExam",esHotExam);
        data.put("pagesum",EsHotExamService.pagesum);
        if(esHotExam!=null)
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @AdminLoginToken
    @PostMapping("admin/hotExam/conditionQuery")
    public Result conditionQuery(@RequestParam("queryString") String queryString, @RequestParam("dir") boolean dir,
                                 @RequestParam("pagenum") int pagenum,@RequestParam("pagesize") int pagesize
            ,@RequestParam("startTimeBegin") String startTimeBegin
            ,@RequestParam("startTimeEnd") String startTimeEnd)
    {
            List<EsHotExam> esHotExamList=esHotExamService.boolHotExam(queryString,dir,pagenum,
                    pagesize,DateFormat.DateFormatParse(startTimeBegin),DateFormat.DateFormatParse(startTimeEnd));
            JSONObject data=new JSONObject();
            data.put("esHotExams",esHotExamList);
            data.put("pagesum",EsHotExamService.pagesum);
        if(!esHotExamList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
   //查询


    //修改
    @AdminLoginToken
    @PostMapping("admin/hotExam/updateById")
    public Result updateById(@RequestBody EsHotExam esHotExam)
    {
        if(esHotExam.getExamName()==null)
            esHotExam.setExamName("");
        if(esHotExam.getExamStartTime()==null)
            esHotExam.setExamStartTime(new Date());
        if(esHotExam.getTagIds()==null)
            esHotExam.setTagIds("");
        int status=esHotExamService.updateExamById(esHotExam);
        JSONObject data=new JSONObject();
        if (status < 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    @AdminLoginToken
    @PostMapping("admin/hotExam/updateByName")
    public Result updateByName(@RequestBody EsHotExam esHotExam)
    {
        if(esHotExam.getExamName()==null)
            esHotExam.setExamName("");
        if(esHotExam.getExamStartTime()==null)
            esHotExam.setExamStartTime(new Date());
        if(esHotExam.getTagIds()==null)
            esHotExam.setTagIds("");
        int status=esHotExamService.updateExamByName(esHotExam);
        JSONObject data=new JSONObject();
        if (status < 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    //增加
    @AdminLoginToken
    @PostMapping("admin/hotExam/create")
    public Result create(@RequestBody EsHotExam esHotExam)
    {
        System.out.println(esHotExam.getExamStartTime());
        if(esHotExam.getExamName()==null)
           esHotExam.setExamName("");
        if(esHotExam.getExamStartTime()==null)
           esHotExam.setExamStartTime(new Date());
           if(esHotExam.getTagIds()==null)
               esHotExam.setTagIds("");
           int status=esHotExamService.creatHotExam(esHotExam);
           JSONObject data=new JSONObject();
        if (status < 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    @AdminLoginToken
    @PostMapping("admin/hotExam/findAll")
    public Result findAll(@RequestParam("pagenum")int pagenum,@RequestParam("pagesize") int pagesize)
    {
        List<EsHotExam> esHotExamList=esHotExamService.findAll(pagenum,pagesize);
        JSONObject data = new JSONObject();
        data.put("esHotExams",esHotExamList);
        data.put("pagesum",EsHotExamService.pagesum);
        if(!esHotExamList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @AdminLoginToken
    @GetMapping("admin/hotExam/deleteById/{id}")
    public Result deleteById(@PathVariable("id")int id)
    {
        int status=esHotExamService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @GetMapping("admin/hotExam/deleteByName/{examName}")
    public Result deleteByName(@PathVariable("examName")String examName)
    {
        int status=esHotExamService.deleteByExamName(examName);
        JSONObject data = new JSONObject();
        if(status<0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
}
