package com.jxust.qq.superquestionlib.controller.admin;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsHotExam;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsHotExamController {

    private final EsHotExamService esHotExamService;
    public EsHotExamController(EsHotExamService esHotExamService) {
        this.esHotExamService = esHotExamService;
    }
    @PostMapping("admin/hotExam/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString,@Param("pagenum") int pagenum,@Param("pagesize") int pagesize)
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
    @PostMapping("admin/hotExam/findById")
    public Result findById(@Param("id")int id)
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
    @PostMapping("admin/hotExam/conditionQuery")
    public Result conditionQuery(@Param("queryString") String queryString, @Param("dir") boolean dir,
                                 @Param("pagenum") int pagenum,@Param("pagesize") int pagesize,@Param("startTimeBegin") String startTimeBegin
            ,@Param("startTimeEnd") String startTimeEnd)
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
    @PostMapping("admin/hotExam/updateById")
    public Result updateById(@Param("id")int id,@Param("examName")String examName,
             @Param("examStartTime")String examStartTime,
                             @Param("examToStartTime")int examToStartTime, @Param("tagIds")String tagIds)
    {
        EsHotExam esHotExam=new EsHotExam();
        esHotExam.setId(id);
        esHotExam.setExamName(examName);
        esHotExam.setExamStartTime(DateFormat.DateFormatParse(examStartTime));
        esHotExam.setExamToStartTime(examToStartTime);
        esHotExam.setTagIds(tagIds);
        int status=esHotExamService.updateExamById(esHotExam);
        JSONObject data=new JSONObject();
        if (status < 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    @PostMapping("admin/hotExam/updateByName")
    public Result updateByName(@Param("examName")String examName, @Param("examStartTime")String examStartTime,
                             @Param("examToStartTime")int examToStartTime, @Param("tagIds")String tagIds)
    {
        EsHotExam esHotExam=new EsHotExam();
        esHotExam.setExamName(examName);
        esHotExam.setExamStartTime(DateFormat.DateFormatParse(examStartTime));
        esHotExam.setExamToStartTime(examToStartTime);
        esHotExam.setTagIds(tagIds);
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
    @PostMapping("admin/hotExam/create")
    public Result findAll(@Param("examName")String examName, @Param("examStartTime")Date examStartTime,
                          @Param("examToStartTime")int examToStartTime, @Param("tagIds")String tagIds)
    {
           EsHotExam esHotExam=new EsHotExam();
           esHotExam.setExamName(examName);
           esHotExam.setExamStartTime(examStartTime);
           esHotExam.setExamToStartTime(examToStartTime);
           esHotExam.setTagIds(tagIds);
           int status=esHotExamService.creatHotExam(esHotExam);
           JSONObject data=new JSONObject();
        if (status < 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    @PostMapping("admin/hotExam/findAll")
    public Result findAll(@Param("pagenum")int pagenum,@Param("pagesize") int pagesize)
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

    @PostMapping("admin/hotExam/deleteById")
    public Result deleteById(@Param("id")int id)
    {
        int status=esHotExamService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }

    @PostMapping("admin/hotExam/deleteByName")
    public Result deleteByName(@Param("examName")String examName)
    {
        int status=esHotExamService.deleteByExamName(examName);
        JSONObject data = new JSONObject();
        if(status<0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }
}
