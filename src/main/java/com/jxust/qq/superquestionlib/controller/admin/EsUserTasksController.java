package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsUserTasksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    @AdminLoginToken
    @PostMapping("/admin/userTasks/fuzzyQuery")
    public Result fuzzyQuery(@RequestParam("queryString") String queryString, @RequestParam("pagenum") int pagenum, @RequestParam("pagesize") int pagesize)
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
    @AdminLoginToken
    @GetMapping("/admin/userTasks/findById/{id}")
    public Result findById(@PathVariable("id")int id)
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
    @AdminLoginToken
    @PostMapping("admin/userTasks/conditionQuery")
    public Result conditionQuery(@RequestParam("queryString") String queryString, @RequestParam("dir") boolean dir,
                                 @RequestParam("pagenum") int pagenum, @RequestParam("pagesize") int pagesize,
                                 @RequestParam("questionNumBegin") int questionNumBegin
            ,@RequestParam("questionNumEnd") int questionNumEnd,@RequestParam("expired")int expired
            ,@RequestParam("hasCompleteNumbers") int hasCompleteNumbers)
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
    @AdminLoginToken
    @GetMapping("admin/userTasks/deleteById")
    public Result deleteById(@PathVariable("id")int id)
    {
        int status=userTasksService.deleteById(id);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @GetMapping("admin/userTasks/deleteByUserName/{userName}")
    public Result deleteByUserName(@PathVariable("userName")String userName)
    {
        int status=userTasksService.deleteByUserName(userName);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @PostMapping("admin/userTasks/create")
    public Result create(@RequestBody EsUserTasks userTasks)
        {
            if(userTasks.getUserName()==null)
                userTasks.setUserName("");
            if(userTasks.getMark()==null)
                userTasks.setMark("");
            if(userTasks.getCreatTime()==null)
                userTasks.setCreatTime(new Date());
            if(userTasks.getEndTime()==null)
                userTasks.setEndTime(new Date());
            userTasks.setScheduleTime(new Date());
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
    @AdminLoginToken
    @PostMapping("admin/userTasks/updateById")
    public Result updateById(@RequestBody EsUserTasks userTasks)
    {

        if(userTasks.getUserName()==null)
        userTasks.setUserName("");
        if(userTasks.getMark()==null)
        userTasks.setMark("");
        if(userTasks.getCreatTime()==null)
        userTasks.setCreatTime(new Date());
        if(userTasks.getEndTime()==null)
        userTasks.setEndTime(new Date());
        userTasks.setScheduleTime(new Date());
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
