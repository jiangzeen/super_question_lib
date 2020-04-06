package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsUser;
import com.jxust.qq.superquestionlib.service.UserService;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsUserService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsUserController
{
     private final EsUserService userService;
     private final UserService userService2;

    public EsUserController(EsUserService userService, UserService userService2) {
        this.userService = userService;
        this.userService2 = userService2;
    }
    @AdminLoginToken
    @PostMapping("admin/user/fuzzyQuery")
    public Result fuzzyQuery(@RequestParam("queryString") String queryString, @RequestParam("pagenum") int pagenum, @RequestParam("pagesize") int pagesize)
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
    @AdminLoginToken
    @PostMapping("admin/user/findById")
    public Result findById(@RequestParam("id")long id)
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
    @AdminLoginToken
    @PostMapping("admin/user/conditionQuery")
    public Result conditionQuery(@RequestParam("queryString") String queryString, @RequestParam("dir") boolean dir,
                                 @RequestParam("pagenum") int pagenum, @RequestParam("pagesize") int pagesize,
                                 @RequestParam("schoolInfoId") int schoolInfoId, @RequestParam("sex") int sex,
                                 @RequestParam("creatTimeBegin") String createTimeBegin,@RequestParam("creatTimeEnd") String createTimeEnd)
    {
        List<EsUser> esUserList=userService.boolHotUser(queryString,dir,pagenum,
                pagesize,
                schoolInfoId,sex,
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
    @AdminLoginToken
    @GetMapping("admin/user/chart/{period}")
    public Result chart(@PathVariable("period")int period)
    {
        JSONObject data=userService.aggregationBuilder(period);
        return Result.SUCCESS(data);
    }
    //用户增长曲线
    @AdminLoginToken
    @GetMapping("admin/user/increaseChart/{period}")
    public Result increaseChart(@PathVariable("period")int period)
    {
        JSONObject data=userService.increaseAggregationBuilder(period);
        return Result.SUCCESS(data);
    }

    @AdminLoginToken
    @GetMapping("admin/user/deleteById/{userId}")
    public Result deleteById(@PathVariable("userId") long userId)
    {
        int status=userService.deleteById(userId);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @PostMapping("admin/user/create")
    public Result create(@RequestBody EsUser user)
    {
        if(user.getUserName()==null)
            user.setUserName("");
        if(user.getUserNick()==null) user.setUserNick("");
        if(user.getUserPassword()==null)
            user.setUserPassword("");
        user.setUserPassword(userService2.encrypt(user.getUserName(),user.getUserPassword()));
        if(user.getUserAvatar()==null)
            user.setUserAvatar("");
        if(user.getUserLastLoginTime()==null) user.setUserLastLoginTime(new Date());
        if(user.getUserCreateTime()==null) user.setUserCreateTime(new Date());
        int status=userService.createUser(user);
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
    @PostMapping("admin/user/updateById")
    public Result updateById(@RequestBody EsUser user)
    {
        if(user.getUserName()==null)
        user.setUserName("");
        if(user.getUserNick()==null) user.setUserNick("");
        if(user.getUserPassword()==null)
        user.setUserPassword("");
        user.setUserPassword(userService2.encrypt(user.getUserName(),user.getUserPassword()));
        if(user.getUserAvatar()==null)
        user.setUserAvatar("");
        if(user.getUserLastLoginTime()==null) user.setUserLastLoginTime(new Date());
        if(user.getUserCreateTime()==null) user.setUserCreateTime(new Date());
        int status=userService.updateById(user);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
}
