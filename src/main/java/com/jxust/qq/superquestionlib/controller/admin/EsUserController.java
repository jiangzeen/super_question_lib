package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsUser;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsUserService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Slf4j
public class EsUserController
{
     private final EsUserService userService;

    public EsUserController(EsUserService userService) {
        this.userService = userService;
    }
    @PostMapping("/admin/user/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString, @Param("pagenum") int pagenum, @Param("pagesize") int pagesize)
    {
        List<EsUser> esUserList=userService.matchUser(queryString,pagenum,pagesize);
        JSONObject data=new JSONObject();
        data.put("esUsers",esUserList);
        data.put("pagesum",userService.pagesum);
        if(!esUserList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @PostMapping("/admin/user/findById")
    public Result findById(@Param("id")long id)
    {
        EsUser esUser=null;
        esUser=userService.termUser(id);
        JSONObject data=new JSONObject();
        data.put("esUser",esUser);
        data.put("pagesum",userService.pagesum);
        if(esUser!=null)
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //条件查找
    @PostMapping("admin/user/conditionQuery")
    public Result conditionQuery(@Param("queryString") String queryString, @Param("dir") boolean dir,
                                 @Param("pagenum") int pagenum, @Param("pagesize") int pagesize,
                                 @Param("schoolInfoId") int schoolInfoId, @Param("sex") int sex,
                                 @Param("creatTimeBegin") String createTimeBegin,@Param("creatTimeEnd") String createTimeEnd)
    {
        List<EsUser> esUserList=userService.boolHotUser(queryString,dir,pagenum,
                pagesize,
                pagenum,schoolInfoId,
                DateFormat.DateFormatParse(createTimeBegin),DateFormat.DateFormatParse(createTimeEnd));
        JSONObject data=new JSONObject();
        data.put("esUsers",esUserList);
        data.put("pagesum",EsHotExamService.pagesum);
        if(!esUserList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //用户登陆统计图
    @PostMapping("admin/user/chart")
    public Result chart()
    {
        JSONObject data=userService.aggregationBuilder();
        return Result.SUCCESS(data);
    }
}
