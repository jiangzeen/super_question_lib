package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsUserTasks;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsUserTasksService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
        data.put("esUsers",esUserTasksList);
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
        data.put("esUser",esUserTasks);
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
            ,@Param("questionNumEnd") int questionNumEnd)
    {
        List<EsUserTasks> esUserTasksList=userTasksService.boolTasks(queryString,dir,pagenum,
                pagesize,
                questionNumBegin,questionNumEnd);
        JSONObject data=new JSONObject();
        data.put("esUsers",esUserTasksList);
        data.put("pagesum", EsHotExamService.pagesum);
        if(!esUserTasksList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
}
