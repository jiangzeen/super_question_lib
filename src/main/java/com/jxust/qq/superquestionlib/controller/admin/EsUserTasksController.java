package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsUserTasksService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsUserTasksController
{

    private final EsUserTasksService userTasksService;

    public EsUserTasksController(EsUserTasksService userTasksService) {
        this.userTasksService = userTasksService;
    }
    @PostMapping("/admin/userTasks/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString, @Param("pagenum") int pagenum, @Param("pagesize") int pagesize)
    {
        List<EsUserTasks> esUserTasksList=userTasksService.matchTasks(queryString,pagenum,pagesize);
        JSONObject data=new JSONObject();
        data.put("esUserTasks",esUserTasksList);
        data.put("pagesum",userTasksService.pagesum);
        if(!esUserTasksList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @PostMapping("/admin/userTasks/findById")
    public Result findById(@Param("id")int id)
    {
        EsUserTasks esUserTasks=null;
        esUserTasks=userTasksService.termTask(id);
        JSONObject data=new JSONObject();
        data.put("pagesum",userTasksService.pagesum);
        if(esUserTasks!=null)
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //条件查找
    @PostMapping("admin/userTasks/conditionQuery")
    public Result conditionQuery(@Param("queryString") String queryString, @Param("dir") boolean dir,
                                 @Param("pagenum") int pagenum, @Param("pagesize") int pagesize,
                                 @Param("questionNumBegin") int questionNumBegin
            ,@Param("questionNumEnd") int questionNumEnd,@Param("expired")int expired
            ,@Param("hasCompleteNumbers") int hasCompleteNumbers)
    {
        List<EsUserTasks> esUserTasksList=userTasksService.boolTasks(queryString,pagenum,
                pagesize,
                questionNumBegin,questionNumEnd,expired,hasCompleteNumbers);
        JSONObject data=new JSONObject();
        data.put("esUserTasks",esUserTasksList);
        data.put("pagesum", EsHotExamService.pagesum);
        if(!esUserTasksList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @PostMapping("admin/userTasks/deleteById")
    public Result deleteById(@Param("id")int id)
    {
        int status=userTasksService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @PostMapping("admin/userTasks/deleteByUserName")
    public Result deleteByUserName(@Param("userName")String userName)
    {
        int status=userTasksService.deleteByUserName(userName);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @PostMapping("admin/userTasks/create")
    public Result create(@Param("userName")String userName,
                         @Param("mark")String mark,
                         @Param("questionLibId")int questionLibId,
                         @Param("questionNumbers")int questionNumbers,
                         @Param("createTime")String createTime,
            @Param("endTime") String endTime,
            @Param("scheduleTime")String scheduleTime,@Param("expired")int expired,
        @Param("hasCompleteNumbers")int hasCompleteNumbers)
        {
            EsUserTasks userTasks=new EsUserTasks();
            if(userName==null) userName="";
            userTasks.setUserName(userName);
        if(mark==null) mark="null";
        userTasks.setMark(mark);
        userTasks.setQuestionLibId(questionLibId);
        userTasks.setQuestionNumbers(questionNumbers);
        if(createTime==null)
                userTasks.setCreatTime(new Date());
            else
                userTasks.setCreatTime(DateFormat.DateFormatParse(createTime));
        userTasks.setEndTime(DateFormat.DateFormatParse(endTime));
        userTasks.setScheduleTime(DateFormat.DateFormatParse(scheduleTime));
        userTasks.setExpired(expired);
        userTasks.setHasCompleteNumbers(hasCompleteNumbers);
        int status=userTasksService.creatUserTasks(userTasks);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    //修改
    @PostMapping("admin/userTasks/updateById")
    public Result updateById(@Param("id")int id,@Param("userName")String userName,
                             @Param("mark")String mark,
                             @Param("questionLibId")int questionLibId,
                             @Param("questionNumbers")int questionNumbers,
                             @Param("creatTime")String creatTime,
                             @Param("endTime") String endTime,
                             @Param("scheduleTime")String scheduleTime,@Param("expired")int expired,
                             @Param("hasCompleteNumbers")int hasCompleteNumbers)
    {
        EsUserTasks userTasks=new EsUserTasks();
        userTasks.setId(id);
        if(userName==null) userName="";
        userTasks.setUserName(userName);
        if(mark==null) mark="";
        userTasks.setMark(mark);
        userTasks.setQuestionLibId(questionLibId);
        userTasks.setQuestionNumbers(questionNumbers);
        if(creatTime==null) userTasks.setCreatTime(new Date());
        else userTasks.setCreatTime(DateFormat.DateFormatParse(creatTime));
        userTasks.setEndTime(DateFormat.DateFormatParse(endTime));
        userTasks.setScheduleTime(DateFormat.DateFormatParse(scheduleTime));
        userTasks.setExpired(expired);
        userTasks.setHasCompleteNumbers(hasCompleteNumbers);
        int status=userTasksService.updateUserTasks(userTasks);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }

}
